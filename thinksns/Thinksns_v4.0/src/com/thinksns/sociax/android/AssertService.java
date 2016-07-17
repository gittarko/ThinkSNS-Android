package com.thinksns.sociax.android;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import com.thinksns.sociax.t4.android.video.Logger;
//import com.thinksns.sociax.t4.android.video.ThemeHelper;
//import com.yixia.camera.util.DeviceUtils;

/**
 * 专门用来处理解压资源
 * 
 * @author tangjun
 * 
 */
public class AssertService extends Service implements Runnable {

	/** 是否正在运行 */
	private static boolean mIsRunning;

	@Override
	public void onCreate() {
		super.onCreate();
		mIsRunning = true;
		new Thread(this).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void run() {
		try {
			File mThemeCacheDir;
			// 获取传入参数
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())
					&& !isExternalStorageRemovable())
				mThemeCacheDir = new File(getExternalCacheDir(), "VideoTheme");
			else
				mThemeCacheDir = new File(getCacheDir(), "VideoTheme");
//			ThemeHelper.prepareTheme(getApplication(), mThemeCacheDir);
		} catch (OutOfMemoryError e) {
			Logger.e(e);
		} catch (Exception e) {
			System.out.println("======1>" + e.toString());
			Logger.e(e);
		}
		mIsRunning = false;
		stopSelf();
	}

	public static boolean isRunning() {
		return mIsRunning;
	}

	public static boolean isExternalStorageRemovable() {
//		if (DeviceUtils.hasGingerbread())
//			return Environment.isExternalStorageRemovable();
//		else
//			return Environment.MEDIA_REMOVED.equals(Environment
//					.getExternalStorageState());
		return false;
	}
}
