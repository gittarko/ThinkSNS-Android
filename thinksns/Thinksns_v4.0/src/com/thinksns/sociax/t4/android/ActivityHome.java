package com.thinksns.sociax.t4.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import com.thinksns.sociax.android.BuildConfig;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.android.Listener.UnreadMessageListener;
import com.thinksns.sociax.t4.android.chat.OnChatListener;
import com.thinksns.sociax.t4.android.checkin.ActivityCheckIn;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.*;
import com.thinksns.sociax.t4.android.img.Bimp;
import com.thinksns.sociax.t4.android.popupwindow.PopuWindowMainMenu;
import com.thinksns.sociax.t4.android.temp.SelectImageListener;
import com.thinksns.sociax.t4.android.video.MediaRecorderActivity;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateBase;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateWeibo;
import com.thinksns.sociax.t4.component.MoreWindow;
import com.thinksns.sociax.t4.component.MoreWindow.IMoreWindowListener;
import com.thinksns.sociax.t4.model.ModelNotification;
import com.thinksns.sociax.t4.sharesdk.ShareSDKManager;
import com.thinksns.sociax.thinksnsbase.activity.widget.BadgeView;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.sociax.unit.SociaxUIUtils;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.constant.TSChat;
import com.thinksns.tschat.listener.TSChatCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类说明：app主页
 * @author wz
 * @version 1.0
 * @date 2014-10-16
 */
public class ActivityHome extends ThinksnsAbscractActivity implements OnChatListener,
        UnreadMessageListener{

    // 底部5个按钮
    private RadioButton rb_buttom_home, rb_buttom_find,
            rb_buttom_new, rb_buttom_message, rb_buttom_my;
    private RelativeLayout ll_message, ll_my;
    private FrameLayout fl_bottom_home, fl_bottom_find,
            fl_bottom_new,
            fl_bottom_message, fl_bottom_my;
    private MoreWindow mMoreWindow;

    // 声明被选择的值
    private final int SELECTED_HOME = 1;
    private final int SELECTED_FIND = 2;
    private final int SELECTED_NEW = 3;
    private final int SELECTED_MESSAGE = 4;
    private final int SELECTED_MY = 5;

    // 当前被选择的页面，默认是选中首页
    private int selected = SELECTED_HOME;
    // 新建用到的变量
    private boolean isNewOpen = false;  // 标记新建是否打开

    private PopuWindowMainMenu mPopu;// 点击新建弹出来的对话框
    // 监听当前fragment
    private FragmentSociax currentFragment;
    private FragmentHome fg_home;
    // 发现用到的变量
    private FragmentFind fg_find;
    private FragmentMessage fg_message;
    private FragmentMyFriends fg_myFriends;
    // my用到的变量
    private FragmentMy fg_my;
    private LinearLayout ll_content;

    boolean registerReceive = false;// 是否已经注册广播

    // handler ,规定msg.arg1标记载入类型，msg.arg2标记操作对象
    private ActivityHandler handler;
    private final int SELECT = 201;// 标记handler执行的是载入页面
    // 消息提醒用到的
    private TextView tv_remind_message;// 消息底部红点（不需要个数
    //消息未读数
    private BadgeView badgeMessage, badgeMy,badgeWeiba;
    //新消息实体类
    private ModelNotification mdNotification;

    private DoubleClickExitHelper mDoubleClickExit;

    private TextView tv_home, tv_find, tv_new,
            tv_message, tv_my;  // 底部导航文字
    private int unreadMsg = 0;

    // 广播接收
    private BroadcastReceiver createNewWeiBoBroadcastReceiver;
    private boolean hasNewChatMessage;
    public static boolean offline = false; // 是否收到离线消息
    private String skip_from;//从哪跳转来

    private Thinksns app;
    protected SelectImageListener listener_selectImage;   //拍照工具

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreateNoTitle(savedInstanceState);
        // 其他activity尽量以这种格式进入页面：1.初始化intent；2.初始化UI；3.初始化Listener；4.初始化数据，创建时可直接复制com.zhishisoft.v4.android.uint-->activity
        initIntentData();
        initView();
        initListener();
        app = (Thinksns) getApplication();
        initData();
        //启动极光推送
        ShareSDKManager.register();
        //启动socket连接
        TSChatManager.login(Thinksns.getMy());
    }

    /**
     * 初始化intent携带的信息
     */
    private void initIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            //进入主页的方式，比如来自通知栏
            skip_from = intent.getStringExtra("type");
        }
    }

    /**
     * 初始化页面
     */
    private void initView() {
        mDoubleClickExit = new DoubleClickExitHelper(this);
        ll_content = (LinearLayout) findViewById(R.id.ll_container);

        // 获取底部5个按钮
        rb_buttom_home = (RadioButton) findViewById(R.id.tv_bottom_home);
        rb_buttom_message = (RadioButton) findViewById(R.id.tv_bottom_message);
        rb_buttom_new = (RadioButton) findViewById(R.id.tv_bottom_new);
        rb_buttom_find = (RadioButton) findViewById(R.id.tv_bottom_find);
        rb_buttom_my = (RadioButton) findViewById(R.id.tv_bottom_my);
        fl_bottom_home = (FrameLayout) findViewById(R.id.fl_bottom_home);
        fl_bottom_find = (FrameLayout) findViewById(R.id.fl_bottom_find);
        fl_bottom_new = (FrameLayout) findViewById(R.id.fl_bottom_new);
        fl_bottom_message = (FrameLayout) findViewById(R.id.fl_bottom_message);
        fl_bottom_my = (FrameLayout) findViewById(R.id.fl_bottom_my);
        ll_message = (RelativeLayout) findViewById(R.id.ll_message);
        ll_my = (RelativeLayout) findViewById(R.id.ll_my);

        tv_home = (TextView) findViewById(R.id.txt_home);
        tv_find = (TextView) findViewById(R.id.txt_find);
        tv_message = (TextView) findViewById(R.id.txt_message);
        tv_my = (TextView) findViewById(R.id.txt_my);
        tv_new = (TextView)findViewById(R.id.txt_new);

        badgeWeiba = (BadgeView)findViewById(R.id.badgeWeiba);
        badgeMessage = (BadgeView)findViewById(R.id.badgeMessage);
        badgeMy = (BadgeView)findViewById(R.id.badgeMy);
        handler = new ActivityHandler();


        listener_selectImage = new SelectImageListener(this);

    }

    /**
     * 初始化监事件 暂时以new OnClickListener替代所有监听事件，后面考虑到代码优化只使用1个click即可
     */
    private void initListener() {
        // 底部5个按钮点击监听
        fl_bottom_home.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentFragment != fg_home)
                    setSelected(SELECTED_HOME);
            }
        });
        fl_bottom_find.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentFragment != fg_find)
                    setSelected(SELECTED_FIND);
            }
        });
        // 底部导航弹出菜单
        rb_buttom_new.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mMoreWindow == null) {
                    mMoreWindow = new MoreWindow(ActivityHome.this);
                }
                mMoreWindow.showMoreWindow(v);
                mMoreWindow.setOnItemClick(new IMoreWindowListener() {

                    @Override
                    public void OnItemClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_create_weibo_camera:
                                listener_selectImage.cameraImage();
                                break;
                            case R.id.tv_create_weibo_pic:
                                selectPhoto();
                                break;
                            case R.id.tv_create_weibo_video:
                                //拍摄视频
                                recordVideo();
                                break;
                            case R.id.tv_create_weibo_sign:
                                //签到
                                Intent intent = new Intent(ActivityHome.this, ActivityCheckIn.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        Anim.in(ActivityHome.this);
                    }
                });

                //长按事件
                rb_buttom_new.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                            Intent intent = new Intent(ActivityHome.this, ActivityCreateBase.class);
                            intent.putExtra("type", AppConstant.CREATE_TEXT_WEIBO);
                            startActivityForResult(intent, 100);
                            Anim.in(ActivityHome.this);
                        return false;
                    }
                });
            }
        });

        fl_bottom_message.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentFragment != fg_message) {
                    hasNewChatMessage = false;
                    setSelected(SELECTED_MESSAGE);
                }
            }
        });

        fl_bottom_my.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentFragment != fg_my)
                    setSelected(SELECTED_MY);
            }
        });

        /************ 广播注册 **********/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(StaticInApp.SERVICE_NEW_NOTIFICATION);
        intentFilter.addAction(StaticInApp.SERVICE_NEW_MESSAGE);
        intentFilter.addAction(TSChat.RECEIVE_NEW_MSG);
        intentFilter.addAction(TSChat.CLEAR_UNREADS);
        createNewWeiBoBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(StaticInApp.SERVICE_NEW_NOTIFICATION)) {
                    mdNotification = (ModelNotification) intent.getSerializableExtra("content");
                    setUnReadUi(mdNotification);
                } else if (action.equals(StaticInApp.SERVICE_NEW_MESSAGE)) {
                    hasNewChatMessage = true;
                } else if (action.equals(TSChat.RECEIVE_NEW_MSG)) {
                    unreadMsg = intent.getIntExtra(TSChat.NEW_MSG_COUNT, 0);
                    if (unreadMsg < 0) {
                        unreadMsg = 0;
                    }else if(unreadMsg > 99) {
                        unreadMsg = 99;
                    }

                    badgeMessage.setBadgeCount(unreadMsg);
                }else if(action.equals(TSChat.CLEAR_UNREADS)) {
                    int count = intent.getIntExtra(TSChat.CLEAR_UNREADS, 0);
                    int showCount = badgeMessage.getBadgeCount() - count;
                    if(showCount <0)
                        showCount = 0;
                    else if(showCount > 99)
                        showCount = 99;
                    badgeMessage.setBadgeCount(showCount);
                }

            }
        };
        if (!registerReceive) {
            try {
                registerReceiver(createNewWeiBoBroadcastReceiver, intentFilter);
                registerReceive = true;
            } catch (Exception e) {
                e.printStackTrace();
                unregisterReceiver(createNewWeiBoBroadcastReceiver);
                registerReceiver(createNewWeiBoBroadcastReceiver, intentFilter);
                registerReceive = true;
            }
        }
    }

    private void selectPhoto() {
        Intent getImage = new Intent(this, MultiImageSelectorActivity.class);
        getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        getImage.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        getImage.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        getImage.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST,
                new ArrayList<String>());
        startActivityForResult(getImage, StaticInApp.LOCAL_IMAGE);
    }

    //录制视频
    private void recordVideo() {
        //跳转视频录制
        Intent intentVideo = new Intent(this, MediaRecorderActivity.class);
        startActivityForResult(intentVideo,AppConstant.CREATE_VIDEO_WEIBO);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        setSelected(SELECTED_HOME);
    }

    /**
     * 对消息界面和我的界面进行设置消息提醒的红点
     *
     * @param mdNotification
     */
    private void setUnReadUi(ModelNotification mdNotification) {
        if (mdNotification.checkValid()) {
            //设置新增粉丝数
            int follower = mdNotification.getFollower() > 99 ? 99 : mdNotification.getFollower();
            badgeMy.setBadgeCount(follower);
            if (fg_my != null) {
                fg_my.setUnReadUi(mdNotification);
            }
            //设置点赞、评论新消息
            int newMsg = mdNotification.getComment() + mdNotification.getDigg();
            if(fg_message != null)
                fg_message.setUnreadNotice(mdNotification);
            int totalMsg = newMsg + unreadMsg;
            badgeMessage.setBadgeCount(totalMsg > 99 ? 99 : totalMsg);
            //设置微吧评论新消息
            int weibaNew = mdNotification.getWeibaComment() > 99 ? 99 : mdNotification.getWeibaComment();
            badgeWeiba.setBadgeCount(weibaNew);
            if(fg_find != null){
                fg_find.setWeibaUnreadCount(mdNotification);
            }
        }
    }

    public int getSelected() {
        return selected;
    }

    /**
     * 修改当前选择的页面 当页面修改时，首先修改头部view的显示，然后修改底部view的显示，最后修改显示数据（以后完善时在首尾添加加载中的提示）
     *
     * @param selected
     */
    public void setSelected(int selected) {
        this.selected = selected;
        switch (selected) {
            case SELECTED_HOME:
                setButtomUI(rb_buttom_home);
                break;
            case SELECTED_NEW:// 选择新建相关内容
                setButtomUI(rb_buttom_new);
                break;
            case SELECTED_FIND:// 选择发现相关内容
                setButtomUI(rb_buttom_find);
                break;
            case SELECTED_MESSAGE:// 选择消息相关内容
                setButtomUI(rb_buttom_message);
                break;
            case SELECTED_MY:// 选择个人中心相关内容
                setButtomUI(rb_buttom_my);
                break;

        }
        // 头部尾部ui都设置好之后，启用新线程载入数据以减少卡屏
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                msg.what = getSelected();
                msg.arg1 = SELECT;
                msg.sendToTarget();
            }
        }).start();
    }

    /**
     * 设置底部按钮的UI显示
     *
     * @param selected 被选中的按钮
     */
    private void setButtomUI(RadioButton selected) {

        RadioButton[] rg_bottom_bottoms = {rb_buttom_home, rb_buttom_find, rb_buttom_new, rb_buttom_message,
                rb_buttom_my};

        TextView[] txt_buttoms = {tv_home, tv_find, tv_new, tv_message, tv_my};

        if (selected.getId() == rb_buttom_new.getId()) {// 如果点击了新建按钮，则保持原来的其他按钮的显示以及当前显示数据状态不变，只修改新建按钮的状态
            if (isNewOpen) {// 如果当前已经打开了新建按钮以及新建的菜单
                isNewOpen = false;
                rg_bottom_bottoms[2].setChecked(false);
                // 后面需要加入关闭菜单动作
            } else {// 如果当前没有打开新建菜单，则打开菜单
                isNewOpen = true;
                rg_bottom_bottoms[2].setChecked(true);
                // 后面需要加入动画
            }
        } else {
            if (isNewOpen) {// 如果当前已经打开了新建按钮以及新建的菜单
                isNewOpen = false;
                rg_bottom_bottoms[2].setChecked(false);
                // 后面需要加入关闭菜单动作
            }
            for (int i = 0; i < 5; i++) {// 否则遍历底部按钮，把被选中的id对应的按钮修改掉，再把其他的修改成非选择状态
                if (rg_bottom_bottoms[i] != rb_buttom_home) {
//					pauseFragmentVideoBesideIndex(-1);// 不是选中home暂停home中视频
                }
                if (rg_bottom_bottoms[i].getId() != selected.getId()) {
                    rg_bottom_bottoms[i].setChecked(false);
                    txt_buttoms[i].setTextColor(this.getResources().getColor(R.color.actionbar_txtcolor_gray));
                } else {
                    rg_bottom_bottoms[i].setChecked(true);
                    txt_buttoms[i].setTextColor(this.getResources().getColor(R.color.actionbar_txtcolor_blue));
                }
                continue;
            }
        }
    }

    // 如果首页数据页面没有被初始化过，则先执行初始话
    public void initHome() {
        if (fg_home == null) {
            fg_home = new FragmentHome();
        }

        currentFragment = fg_home;
        fragmentManager.beginTransaction().replace(R.id.ll_container, fg_home).addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        if (intent.hasExtra("weiboId")) {
            int weiboId = intent.getIntExtra("weiboId", -1);
            // currentFragment.updataWeiboList(weiboId);
        }
        if (intent.hasExtra("type") && "createSuccess".equals(intent.getStringExtra("type"))) {
            int weiboId = intent.getIntExtra("weiboId", -1);
            // currentFragment.updataWeiboList(weiboId);
        }

        if(intent.hasExtra("type")) {
            skip_from = intent.getStringExtra("type");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!BuildConfig.DEBUG) {
            // 开启消息红点提醒，暂时关闭，因为开发影响log
            Thinksns app = (Thinksns) getApplication();
            app.startService();
        }

        if (currentFragment != null && currentFragment.getAdapter() != null)
            currentFragment.getAdapter().notifyDataSetChanged();

        if(!TextUtils.isEmpty(skip_from)) {
            //切换至消息页面
            if(skip_from.equals("message")) {
                setSelected(SELECTED_MESSAGE);
                skip_from = null;
            }
        }
    }

    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }


    @Override
    protected void onPause() {
        super.onPause();
        Thinksns app = (Thinksns) getApplication();
        app.stopService();
    }

    @Override
    public String getTitleCenter() {
        return null;
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    public void refreshHeader() {

        if (selected == SELECTED_MESSAGE) {
//			fg_chatList.getAdapter().doRefreshHeader();
            //注释
//            if (fg_message.currentFragment != null && fg_message.currentFragment.getAdapter() != null) {
//                fg_message.currentFragment.doRefreshHeader();
//            }
        } else {
            if (currentFragment != null && currentFragment.getAdapter() != null)
                currentFragment.getAdapter().doRefreshHeader();
        }
    }

    @Override
    public void refreshFooter() {
        if (selected == SELECTED_MESSAGE) {
//			fg_chatList.getAdapter().doRefreshFooter();
            //注释
//            if (fg_message.currentFragment != null && fg_message.currentFragment.getAdapter() != null) {
//                fg_message.currentFragment.getAdapter().doRefreshFooter();
//            }
        } else {
            currentFragment.getAdapter().doRefreshFooter();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return mDoubleClickExit.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 清除消息未读提醒
     * @param type  未读消息类型
     * @param unread 待清除的个数
     */
    @Override
    public void clearUnreadMessage(int type, int unread) {
        switch (type) {
            case StaticInApp.UNREAD_COMMENT:
            case StaticInApp.UNREAD_DIGG:
                int msg = badgeMessage.getBadgeCount();
                msg = msg - unread;
                if(msg <= 0)
                    msg = 0;
                badgeMessage.setBadgeCount(msg);
                break;
            case StaticInApp.UNREAD_WEIBA:
                badgeWeiba.setBadgeCount(0);
                break;
            case StaticInApp.UNREAD_FOLLOW:
                badgeMy.setBadgeCount(0);
                break;
        }
    }

    private class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.arg1 == SELECT) {
                // 选择不同页面时候先执行这里
                switch (msg.what) {
                    // 底部五个页卡
                    case SELECTED_HOME:// 选择home相关的内容
                        initHome();
                        break;
                    case SELECTED_NEW:// 选择新建相关内容,保持原来的不变则可
                        break;
                    case SELECTED_FIND:// 选择发现相关内容
                        initFind();
                        break;
                    case SELECTED_MESSAGE:// 选择消息相关内容
                        initChatList();
                        break;
                    case SELECTED_MY:// 选择我的相关内容
                        initMy();
                        break;
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (registerReceive) {// 如果注册了广播监听，则关闭广播
                unregisterReceiver(createNewWeiBoBroadcastReceiver);
                registerReceive = false;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
        this.finish();
    }

    /**
     * 初始化我家页面
     */
    public void initMy() {
        if (fg_my == null) {
            fg_my = new FragmentMy();
        }
        currentFragment = fg_my;
        fragmentManager.beginTransaction().replace(R.id.ll_container, fg_my).addToBackStack(null).commit();
    }

    /**
     * 初始化消息页面
     */
    public void initChatList() {
        if (fg_message == null) {
            fg_message = FragmentMessage.newInstance(mdNotification);
        }
        currentFragment = fg_message;
        fragmentManager.beginTransaction().replace(R.id.ll_container, fg_message).addToBackStack(null).commit();
    }


    /**
     * 初始化发现页面
     */
    private void initFind() {
        if (fg_find == null) {
            fg_find = FragmentFind.newInstance(badgeWeiba.getBadgeCount());
        }
        currentFragment = fg_find;
        fragmentManager.beginTransaction().replace(R.id.ll_container, fg_find).addToBackStack(null).commit();
    }

    @Override
    public void update(int count) {

        // 判断接收到的是否是离线消息
//		if (fg_chatList == null)
        if (fg_message == null)
            offline = true;
        else
            offline = false;
        unreadMsg += count;

        Log.e("AndroidHome", "unread msg count:" + unreadMsg);
        if (unreadMsg <= 0) {
            unreadMsg = 0;
            tv_remind_message.setVisibility(View.GONE);
        } else {
            tv_remind_message.setVisibility(View.VISIBLE);
            tv_remind_message.setText(unreadMsg + "");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 刷新首页数据
        if (resultCode == RESULT_OK) {
            Intent intent=null;
            switch (requestCode) {
                case StaticInApp.LOCAL_IMAGE:
                    List<String> photoList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    boolean original = data.getBooleanExtra(MultiImageSelectorActivity.EXTRA_SELECT_ORIGIANL, false);
                    if (Bimp.address.size() < 9) {
                        for (String addr : photoList) {
                            if (!Bimp.address.contains(addr)) {
                                Bimp.address.add(addr);
                            }
                        }
                    }
                    //跳转至发布微博页
                    intent = new Intent(ActivityHome.this, ActivityCreateBase.class);
                    intent.putExtra("type", AppConstant.CREATE_ALBUM_WEIBO);
                    intent.putExtra("is_original", original);
                    startActivity(intent);
                    Anim.in(ActivityHome.this);
                    break;
                case StaticInApp.CAMERA_IMAGE:
                    if (Bimp.address.size() < 9) {
                        Bimp.address.add(listener_selectImage.getImagePath());
                    }
                    //跳转至发布微博页
                    intent = new Intent(ActivityHome.this, ActivityCreateBase.class);
                    intent.putExtra("type", AppConstant.CREATE_ALBUM_WEIBO);
                    intent.putExtra("is_original", false);
                    startActivity(intent);
                    Anim.in(ActivityHome.this);
                    break;
                case AppConstant.CREATE_VIDEO_WEIBO:
                    intent = new Intent(ActivityHome.this,ActivityCreateBase.class);
                    intent.putExtra("type", AppConstant.CREATE_VIDEO_WEIBO);
                    startActivity(intent);
                    Anim.in(ActivityHome.this);
                    break;
                default:
                if (currentFragment instanceof FragmentHome) {
                    currentFragment.onActivityResult(requestCode, resultCode, data);
                }
                if (currentFragment instanceof FragmentMy) {
                    ((FragmentMy) currentFragment).showBasicInfo(Thinksns.getMy());
                }
            }
        }

    }
}
