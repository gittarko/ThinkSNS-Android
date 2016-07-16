package com.thinksns.sociax.thinksnsbase.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Stack;

public class ActivityStack extends Stack<Activity> {
	private static Stack<Activity> cacheExit;
	private static Intent intent;

	public ActivityStack() {
		super();
		intent = new Intent();
		cacheExit = new Stack<Activity>();
	}

	public static void addCache(Activity activity) {
		if (cacheExit == null) {
			cacheExit = new Stack<Activity>();
		}
		cacheExit.push(activity);
	}

	public static void startActivity(Activity now, Class<? extends Activity> target,
			Bundle data) {
		intent.setClass(now, target);
		if (data != null) {
			if (intent.getExtras() != null) {
				intent.replaceExtras(data);
			} else {
				intent.putExtras(data);
			}
		}
		now.startActivity(intent);
		Anim.in(now);
	}

	public static void startActivity(Activity now, Class<? extends Activity> target,
							  Bundle data, int flag) {
		intent.setClass(now, target);
		intent.setFlags(flag); // 注意本行的FLAG设置
		if (data != null) {
			if (intent.getExtras() != null) {
				intent.replaceExtras(data);
			} else {
				intent.putExtras(data);
			}
		}
		now.startActivity(intent);
		Anim.in(now);
	}

	public static void startActivity(Activity now, Class<? extends Activity> target) {
		startActivity(now, target, null);
	}

	public static void startActivityForResult(Activity now,
									   Class<? extends Activity> target, Bundle data) {
		intent.setClass(now, target);
		if (data != null) {
			if (intent.getExtras() != null) {
				intent.replaceExtras(data);
			} else {
				intent.putExtras(data);
			}
		}
		now.startActivityForResult(intent, 3456);
		Anim.in(now);
	}

	public void returnActivity(Activity now, Bundle data) {
		if (cacheExit.empty()) {
			return;
		}

		Activity temp = cacheExit.pop();
		intent.setClass(now, temp.getClass());
		if (data != null) {
			intent.putExtras(data);
		}
		now.startActivity(intent);
		Anim.in(now);
		now.finish();
	}

	public void returnActivity(Activity now) {
		this.returnActivity(now, null);
	}

	//返回上一个页面
	public void finishActivity(Activity now) {
		if (cacheExit.empty()) {
			return;
		}

		Activity temp = cacheExit.pop();
		temp.finish();
		Anim.exit(now);
	}

	// 通过name获取Activity对象
	public static Activity getActivityByName(String name) {
		Activity getac = null;
		for (Activity ac : cacheExit) {
			if (ac.getClass().getName().indexOf(name) >= 0) {
				getac = ac;
			}
		}
		return getac;
	}

	public static Activity getLastActivity() {
		int size = cacheExit.size();
		if(size > 0) {
			return cacheExit.get(size -1);
		}else {
			return null;
		}
	}

	@Override
	public void clear() {
		while (!cacheExit.empty()) {
			Activity activity = cacheExit.pop();
			if(!activity.isFinishing()) {
				activity.finish();
			}
		}

		super.clear();
	}

	@Override
	public synchronized Activity pop() {
		return super.pop();
	}
}
