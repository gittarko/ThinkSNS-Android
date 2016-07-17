package com.thinksns.sociax.t4.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Stack;
import java.util.WeakHashMap;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import cn.jpush.android.api.JPushInterface;
import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.app.AppManager;
import com.thinksns.sociax.db.AtMeSqlHelper;
import com.thinksns.sociax.db.AttachSqlHelper;
import com.thinksns.sociax.db.ChannelSqlHelper;
import com.thinksns.sociax.db.ChatMsgSqlhelper;
import com.thinksns.sociax.db.FavoritWeiboSqlHelper;
import com.thinksns.sociax.db.MobileAppSqlHelper;
import com.thinksns.sociax.db.MyCommentSqlHelper;
import com.thinksns.sociax.db.MyMessageSqlhelper;
import com.thinksns.sociax.db.RemindSqlHelper;
import com.thinksns.sociax.db.SitesSqlHelper;
import com.thinksns.sociax.db.SqlHelper;
import com.thinksns.sociax.db.UserSqlHelper;
import com.thinksns.sociax.db.WeibaSqlHelper;
import com.thinksns.sociax.db.WeiboSqlHelper;
import com.thinksns.sociax.modle.ApproveSite;
import com.thinksns.sociax.net.Request;
import com.thinksns.sociax.thinksnsbase.base.BaseApplication;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.t4.android.db.SQLHelperChatMessage;
import com.thinksns.sociax.t4.android.db.SQLHelperWeiboDraft;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.service.ServiceUnReadMessage;
import com.thinksns.sociax.t4.service.UpdateLocationService;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.UserDataInvalidException;
import com.thinksns.tschat.chat.TSChatManager;
import com.yixia.camera.VCamera;
import com.thinksns.sociax.android.R;

public class Thinksns extends BaseApplication {
    /***********
     * t4
     ****************/
    private Api.WeiboApi weiboApi;// 微博api
    private Api.GiftApi giftApi;// 微博api
    private Api api;
    private Api.Friendships friendships;
    private Api.StatusesApi statuses;
    private Api.Oauth oauth;
    private Api.Favorites favorites;
    private Api.Message messages;
    private Api.Users users;
    private Api.NotifytionApi notifytionApi;
    private Api.Sites sites;
    private Api.STContacts contact;
    private Api.Tasks tasks;
    private Api.Documents documents;
    private Api.WeibaApi weibaApi;
    private Api.ChannelApi channelApi;
    private Api.GroupApi groupApi;
    private Api.MobileApps mobileApps;
    private Api.Public publicApi;
    private Api.Credit creditApi;
    private Api.Medal medalApi;
    private Api.FindPeople findPeopleApi;
    private Api.Tags tagsApi;
    private Api.Information informationApi;
    private static ApproveSite mySite;
    private static int delIndex;
    public static DisplayImageOptions options;
    public static BitmapFactory.Options opts;

    public static final String NULL = "";
    private static final String TAG = "Thinksns";
    private static ModelUser my;
    private static Stack<SqlHelper> sqlHelper;

    private static WeakHashMap<String, Bitmap> imageCache;
    private static ListData<SociaxItem> lastWeiboList;
    private static Thinksns application;

    private MediaPlayer mediaPlayer;                //媒体播放器
    public static boolean isFirstSignIn = true;
    public static boolean isFirstGetInChatRoom = true;
    private static Object checkInfo = null;        //签到信息
    private static Object rankInfo = null;        //签到排行榜

    //设置签到
    public static void setCheckIn(Object b) {
        checkInfo = b;
    }

    public static Object getCheckInfo() {
        return checkInfo;
    }

    public static void setRankInfo(Object info) {
        rankInfo = info;
    }

    public static Object getRankInfo() {
        return rankInfo;
    }

    //错误日志保存路径
    public static final String LOG_DIR = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/thinksns/log";

    public Thinksns() {
        super();
        application = this;
        sqlHelper = new Stack<SqlHelper>();
        imageCache = new WeakHashMap<String, Bitmap>();
    }

    public static Application getContext() {
        return application;
    }

    public static Thinksns getApplication() {
        return application;
    }

    public static DisplayImageOptions getOptions() {
        if (options == null) {
            return null;
        }
        return options;
    }

    public static boolean getIsFirstSignIn() {
        return isFirstSignIn;
    }

    public static boolean getIsFirstGetInChatRoom() {
        return isFirstGetInChatRoom;
    }

    public static BitmapFactory.Options getOpts() {
        if (opts == null) {
            return null;
        }
        return opts;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化聊天管理
        TSChatManager.initialize(this);
        initJpush();
        initMediaPlayer();
    }

    /**
     * 显示图片
     *
     * @param path
     * @param imageView
     */
    public void displayImage(String path, ImageView imageView) {
        // 显示图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image_small)
                .showImageOnFail(R.drawable.default_image_small)
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        if (path.indexOf("storage") != -1 && path.indexOf("emulated") != -1) {
            mImageLoader.displayImage("file://" + path, imageView, options);
        } else {
            mImageLoader.displayImage(path, imageView, options);
        }
    }

    public void displayDrawable(int drawable, ImageView imageView) {
        // 显示图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image_small)
                .showImageOnFail(R.drawable.default_image_small)
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        mImageLoader.displayImage("drawable://" + drawable, imageView, options);
    }


    public Api.Status initOauth() throws ApiException {
        return this.getOauth().requestEncrypKey();
    }


    /**
     * 初始化当前默认的站点信息 Thinksns.setMySite(as);
     */
    public void initApi() {
        SitesSqlHelper db = this.getSiteSql();
        ApproveSite as = null;
        if (db.hasSites() == 0) {// 如果数据库没有任何站点信息
            // 则新建一个站点,根据value-->app_init_set里面的host和past生成
            as = new ApproveSite();
            as.setUrl("http://"
                    + getHostInfo()[0] + "/"
                    + getHostInfo()[1]);
            as.setName(getResources().getString(R.string.app_name));
            db.addSites(as);// 把站点信息添加到数据库
        }
        try {
            as = db.getInUsed();// 获取站点信息
        } catch (Exception e) {
            e.printStackTrace();
            this.api = Api.getInstance(getApplicationContext(), false, null);
            Thinksns.setMySite(as);
            return;
        }
        if (as == null) {
            this.api = Api.getInstance(getApplicationContext(), false, null);
        } else {
            Log.i(TAG, "app site info of db " + as.getUrl());
            this.api = Api.getInstance(getApplicationContext(), true,
                    dealUrl(as.getUrl()));
        }
        Thinksns.setMySite(as);
    }

    public Api getApi() {
        return this.api;
    }

    public static WeakHashMap<String, Bitmap> getImageCache() {
        return imageCache;
    }

    public boolean HasLoginUser() {
        UserSqlHelper db = this.getUserSql();
        try {
            ModelUser user = db.getLoginedUser();
            if (user != null) {
                Request.setToken(user.getToken());
                Request.setSecretToken(user.getSecretToken());
                setTokenInfo(user.getToken(), user.getSecretToken());
                Thinksns.setMy(user);
                return true;
            }
        } catch (UserDataInvalidException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static UserSqlHelper getUserSql() {
        UserSqlHelper db = UserSqlHelper.getInstance(application);
        sqlHelper.add(db);
        return db;
    }

    public static WeiboSqlHelper getWeiboSql() {
        WeiboSqlHelper db = WeiboSqlHelper.getInstance(application);
        sqlHelper.add(db);
        return db;
    }

    public FavoritWeiboSqlHelper getFavoritWeiboSql() {
        FavoritWeiboSqlHelper db = FavoritWeiboSqlHelper
                .getInstance(getApplicationContext());
        sqlHelper.add(db);
        return db;
    }

    public AtMeSqlHelper getAtMeSql() {
        AtMeSqlHelper db = AtMeSqlHelper.getInstance(getApplicationContext());
        sqlHelper.add(db);
        return db;
    }

    public MyMessageSqlhelper getMyMessageSql() {
        MyMessageSqlhelper db = MyMessageSqlhelper
                .getInstance(getApplicationContext());
        sqlHelper.add(db);
        return db;
    }

    public ChatMsgSqlhelper getChatMsgSql() {
        ChatMsgSqlhelper db = ChatMsgSqlhelper
                .getInstance(getApplicationContext());
        sqlHelper.add(db);
        return db;
    }

    public MyCommentSqlHelper getMyCommentSql() {
        MyCommentSqlHelper db = MyCommentSqlHelper
                .getInstance(getApplicationContext());
        sqlHelper.add(db);
        return db;
    }

    public SitesSqlHelper getSiteSql() {
        SitesSqlHelper db = SitesSqlHelper.getInstance(getApplicationContext());
        sqlHelper.add(db);
        return db;
    }

    public RemindSqlHelper getRemindSql() {
        RemindSqlHelper db = RemindSqlHelper
                .getInstance(getApplicationContext());
        sqlHelper.add(db);
        return db;
    }

    public MobileAppSqlHelper getMobileAppSql() {
        MobileAppSqlHelper db = MobileAppSqlHelper
                .getInstance(getApplicationContext());
        sqlHelper.add(db);
        return db;
    }

    public ChannelSqlHelper getChannelSql() {
        ChannelSqlHelper db = ChannelSqlHelper
                .getInstance(getApplicationContext());
        sqlHelper.add(db);
        return db;
    }

    public AttachSqlHelper getAttachSqlHelper() {
        AttachSqlHelper db = AttachSqlHelper
                .getInstance(getApplicationContext());
        sqlHelper.add(db);
        return db;
    }

    /**
     * 草稿箱helper
     */
    public static SQLHelperWeiboDraft getWeiboDraftSQL() {
        SQLHelperWeiboDraft db = SQLHelperWeiboDraft.getInstance(getContext());
        sqlHelper.add(db);
        return db;
    }

    public Api.Friendships getFriendships() {
        if (friendships == null) {
            friendships = new Api.Friendships();
        }
        return friendships;
    }

    public Api.StatusesApi getStatuses() {
        if (statuses == null) {
            statuses = new Api.StatusesApi();
        }
        return statuses;
    }

    public Api.Oauth getOauth() {
        if (oauth == null) {
            oauth = new Api.Oauth();
        }
        return oauth;
    }

    public Api.Favorites getFavorites() {
        if (favorites == null) {
            favorites = new Api.Favorites();
        }
        return favorites;
    }

    public Api.Message getMessages() {
        if (messages == null) {
            messages = new Api.Message();
        }
        return messages;
    }

    public Api.Public getPublicApi() {
        if (publicApi == null) {
            publicApi = new Api.Public();
        }
        return publicApi;
    }

    public Api.Tags getTagsApi() {
        if (tagsApi == null) {
            tagsApi = new Api.Tags();
        }
        return tagsApi;
    }

    /**
     * 资讯
     * @return
     */
    public Api.Information getInformationApi() {
        if (informationApi == null) {
            informationApi = new Api.Information();
        }
        return informationApi;
    }

    /**
     * Zoey
     *
     * @return
     */
    public Api.Credit getApiCredit() {
        if (creditApi == null) {
            creditApi = new Api.Credit();
        }
        return creditApi;
    }

    public Api.Medal getMedalApi() {
        if (medalApi == null) {
            medalApi = new Api.Medal();
        }
        return medalApi;
    }

    public Api.FindPeople getFindPeopleApi() {
        if (findPeopleApi == null) {
            findPeopleApi = new Api.FindPeople();
        }
        return findPeopleApi;
    }

    public Api.STContacts getContact() {
        if (contact == null) {
            contact = new Api.STContacts();
        }
        return contact;
    }

    public Api.Tasks getTasksApi() {
        if (tasks == null) {
            tasks = new Api.Tasks();
        }
        return tasks;
    }

    public Api.Documents getDocument() {
        if (documents == null) {
            documents = new Api.Documents();
        }
        return documents;
    }

    public Api.MobileApps getMobileApps() {
        if (mobileApps == null) {
            mobileApps = new Api.MobileApps();
        }
        return mobileApps;
    }

    public Api.WeibaApi getWeibaApi() {
        if (weibaApi == null) {
            weibaApi = new Api.WeibaApi();
        }
        return weibaApi;
    }

    public Api.ChannelApi getChannelApi() {
        if (channelApi == null) {
            channelApi = new Api.ChannelApi();
        }
        return channelApi;
    }

    public Api.GroupApi getGroupApi() {
        if (groupApi == null) {
            groupApi = new Api.GroupApi();
        }
        return groupApi;
    }

    public Api.Users getUsers() {
        if (users == null) {
            users = new Api.Users();
        }
        return users;
    }

    public Api.Sites getSites() {
        if (sites == null) {
            sites = new Api.Sites();
        }
        return sites;
    }

    public Api.NotifytionApi getApiNotifytion() {
        if (notifytionApi == null) {
            notifytionApi = new Api.NotifytionApi();
        }
        return notifytionApi;
    }

    public static ModelUser getMy() {
        return my;
    }

    public static void setMy(ModelUser my) {
        Thinksns.my = null;
        Thinksns.my = my;
    }

    public static ListData<SociaxItem> getLastWeiboList() {
        return lastWeiboList;
    }

    public static void setLastWeiboList(ListData<SociaxItem> lastWeiboList) {
        Thinksns.lastWeiboList = lastWeiboList;
    }

    /**
     * 将站点信息分解
     *
     * @param url 完整的http：//+host+past
     * @return [host，past]
     */
    public static String[] dealUrl(String url) {
        String[] tempUrl = new String[2];
        String temp = "";
        String[] buttonText = url.substring(7).split("/");// 截取“http：//”之后的字符串，并且用/将url分成段
        if (buttonText.length == 1) {// 如果没有/号，则只有1个host
            tempUrl[0] = buttonText[0];
            tempUrl[1] = "";
        } else {// 否则有host和path
            tempUrl[0] = buttonText[0];// host部分

            // path部分
            for (int i = 1; i < buttonText.length; i++) {
                temp += buttonText[i] + "/";
            }
            tempUrl[1] = temp;
        }
        Log.d(TAG, "tempUrl" + tempUrl[0] + "----" + tempUrl[1]);
        return tempUrl;
    }

    public static Activity getLastActivity() {
        return activityStack.getLastActivity();
    }

    public static int getDelIndex() {
        return delIndex;
    }

    public static void setDelIndex(int delIndex) {
        Thinksns.delIndex = delIndex;
    }

    public static ApproveSite getMySite() {
        return mySite;
    }

    public static void setMySite(ApproveSite mySite) {
        Thinksns.mySite = mySite;
    }

    /**************************** t4 ***********************/
    /**
     * 获取微博api
     *
     * @return
     */
    public Api.WeiboApi getWeiboApi() {
        if (weiboApi == null)
            weiboApi = new Api.WeiboApi();
        return weiboApi;
    }

    /**
     * 获取礼物api
     *
     * @return
     */
    public Api.GiftApi getApiGift() {

        if (giftApi == null)
            giftApi = new Api.GiftApi();
        return giftApi;

    }

    /**
     * 设置微博api
     *
     * @param weiboApi
     */
    public void setWeiboApi(Api.WeiboApi weiboApi) {
        this.weiboApi = weiboApi;
    }

    /**
     * 获取聊天数据库helper
     *
     * @return
     */
    public SQLHelperChatMessage getSQLHelperChatMessage() {
        SQLHelperChatMessage db = SQLHelperChatMessage.getInstance(getApplicationContext());
        sqlHelper.add(db);
        return db;
    }


    /**
     * 清理数据库
     */
    public void clearDataBase() {
        getWeiboSql().clearCacheDB();
        getMyMessageSql().clearCacheDB();
        getMyCommentSql().clearCacheDB();
        getFavoritWeiboSql().clearCacheDB();
        getChatMsgSql().clearCacheDB();
        getChannelSql().clearCacheDB();
        getAtMeSql().clearCacheDB();
        getSQLHelperChatMessage().clearCacheDB();
        getWeiboDraftSQL().clearWeiboDraft();
    }

    /**
     * 关闭数据库
     */
    public static void closeDb() {
        if (!sqlHelper.empty()) {
            for (SqlHelper db : sqlHelper) {
                db.close();
            }
        }
    }

    /**
     * 获取mp3播放器
     *
     * @return
     */
    public MediaPlayer getMediaPlayer() {

        return mediaPlayer;
    }

    /**
     * 获取积分api
     *
     * @return
     */
    public Object getCreditApi() {
        if (weiboApi == null)
            weiboApi = new Api.WeiboApi();
        return weiboApi;
    }

    /**
     * 初始化获取未读消息的service
     */
    public void startService() {
        startService(new Intent(this, ServiceUnReadMessage.class));
        startService(new Intent(this, UpdateLocationService.class));
    }

    /**
     * 初始化获取未读消息的service
     */
    public void stopService() {
        stopService(new Intent(this, ServiceUnReadMessage.class));
        stopService(new Intent(this, UpdateLocationService.class));
    }


    /**
     * 清理缓存
     *
     * @return
     */
    public boolean clearCache() {
        boolean result = true;
        try {
            getImageCache().clear();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    boolean debug = true;

    /**
     * 初始化播放器，以及缓存路径
     */
    public void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        // 设置拍摄视频缓存路径
        VCamera.setVideoCachePath(cachePath + "/video/");
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(false);
        // 初始化拍摄SDK，必须
        VCamera.initialize(this);
    }

    /**
     * 获取缓存路径
     * 后面其他内容都放在本文件下
     *
     * @return
     */
    public static String getCache_path() {
        return cachePath;
    }

    /**
     * 设置缓存路径
     *
     * @param cache_path
     */
    public void setCache_path(String cache_path) {
        this.cachePath = cache_path;
    }

    /**
     * 初始化极光推送
     */
    private void initJpush() {
        //开启日志模式，正式发布可以取消
        JPushInterface.setDebugMode(true);
        try {
            //初始化激光推送
            JPushInterface.init(this);
        } catch (Exception e) {
            e.printStackTrace();
            //极光推送初始化失败
        }
    }


}
