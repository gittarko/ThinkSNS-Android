package com.thinksns.sociax.t4.android;

import com.thinksns.sociax.android.R;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;


/***
 * 双击退出
 * @author dong.he
 */
public class DoubleClickExitHelper {

	private final Activity mActivity;
	private boolean isOnKeyBacking;
	private Handler mHandler;
	private Toast mBackToast;
	private Runnable onBackTimeRunnable;

	public DoubleClickExitHelper(Activity activity) {
		mActivity = activity;
		mHandler = new Handler(Looper.getMainLooper());
		initRunnable();
	}
	
	/**
	 * Activity onKeyDown事件
	 * */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode != KeyEvent.KEYCODE_BACK) {
			return false;
		}
		if(isOnKeyBacking) {
			mHandler.removeCallbacks(onBackTimeRunnable);
			if(mBackToast != null){
				mBackToast.cancel();
			}
			// 退出
			mActivity.finish();
			Thinksns app = (Thinksns) mActivity.getApplicationContext();
			app.exitApp();
			return true;
		} else {
			isOnKeyBacking = true;
			if(mBackToast == null) {
				mBackToast = Toast.makeText(mActivity, R.string.tip_double_click_exit, 2000);
			}
			mBackToast.show();
			mHandler.postDelayed(onBackTimeRunnable, 2000);
			return true;
		}
	}

	private void initRunnable() {
		onBackTimeRunnable = new Runnable() {
			@Override
			public void run() {
				isOnKeyBacking = false;
				if(mBackToast != null)
					mBackToast.cancel();
			}
		};
	}
}
