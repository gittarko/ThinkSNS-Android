package com.thinksns.sociax.thinksnsbase.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

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
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by hedong on 16/3/11.
 * 应用程序基类
 */
public class BaseApplication extends Application {
    private static final String cacheDirName = "ThinkSNS";  //缓存目录名
    protected static String cachePath;     //缓存路劲
    protected static ActivityStack activityStack;   //当前应用活动的activity栈
    private static Context mContext;

    protected ImageLoader mImageLoader = null;
    public static DisplayImageOptions options;
    public static BitmapFactory.Options opts;

    public BaseApplication() {
        activityStack = new ActivityStack();
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initCachePath();
        initImageLoader();
        //初始化网络请求
        ApiHttpClient.newHttpClient(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public String[] getHostInfo() {
        return ApiHttpClient.getHostInfo();
    }

    public URI getSocketURI() throws URISyntaxException {
        String socket_addr = ApiHttpClient.getSocketUrl();
        return new URI(socket_addr);
    }

    /**
     * 设置token信息
     * @param token
     * @param tokenSecret
     */
    public void setTokenInfo(String token, String tokenSecret) {
        ApiHttpClient.setTokenInfo(token, tokenSecret);
    }

    /**
     * 初始化缓存路径
     */
    private void initCachePath() {
        File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator +
                cacheDirName);
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }

        cachePath = sampleDir.getAbsolutePath();
    }

    /**
     * imageloader 参数
     * @author Zoey
     * @return
     */
    public void initImageLoader() {
        // 创建默认的ImageLoader配置参数
        if (mImageLoader == null) {
            File cacheDir = StorageUtils.getOwnCacheDirectory(
                    getApplicationContext(), cachePath +"/image");
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    this)
                    .memoryCacheExtraOptions(480, 800)
                    // default = device screen dimensions
                    .threadPoolSize(3)
                    // default
                    .threadPriority(Thread.NORM_PRIORITY - 1)
                    // default
                    .tasksProcessingOrder(QueueProcessingType.FIFO)
                    // default
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                    .memoryCacheSize(10 * 1024 * 1024)
                    .memoryCacheSizePercentage(13)
                    // default
                    .diskCache(new UnlimitedDiskCache(cacheDir))
                    // 自定义缓存路径
                    // default
                    .diskCacheSize(80 * 1024 * 1024)
                    .diskCacheFileCount(100)
                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                    .imageDownloader(new BaseImageDownloader(this)) // default
                    .defaultDisplayImageOptions(
                            DisplayImageOptions.createSimple()) // default
                    .writeDebugLogs().writeDebugLogs().build();
            ImageLoader.getInstance().init(config);// 全局初始化此配置
            mImageLoader = ImageLoader.getInstance();
        }

        //显示图片的配置
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)    //设置图片的缩放类型，该方法可以有效减少内存的占用
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    public static void addActivity(Activity activity) {
        activityStack.addCache(activity);
    }

    // 通过name获取Activity对象
    public static Activity getActivityByName(String name) {
        return activityStack.getActivityByName(name);
    }

    public static void finishActivity(Activity activity) {
        activityStack.finishActivity(activity);
    }

    /**
     * 退出程序
     */
    public static void exitApp() {
        activityStack.clear();
        System.exit(0);
    }

    /**
     * 清空所有activity
     */
    public static void clearAllActivity() {
       activityStack.clear();
    }
}
