package com.thinksns.sociax.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AppInfo {

	private int id;
	private String appName;
	private int appImage;
	private boolean isNotice;
	private String tag;

	private Class<? extends Activity> appClazz;

	public AppInfo() {
	}

	public AppInfo(String appName) {
		this.appName = appName;
	}

	public AppInfo(int id, String appName, int appImage, boolean isNotice) {
		this.id = id;
		this.appName = appName;
		this.appImage = appImage;
		this.isNotice = isNotice;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int getAppImage() {
		return appImage;
	}

	public void setAppImage(int appImage) {
		this.appImage = appImage;
	}

	public boolean isNotice() {
		return isNotice;
	}

	public void setNotice(boolean isNotice) {
		this.isNotice = isNotice;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Class<? extends Activity> getAppClazz() {
		return appClazz;
	}

	public void setAppClazz(Class<? extends Activity> appClazz) {
		this.appClazz = appClazz;
	}

	public void setAppClazz(Class<? extends Activity> appClazz, String link) {
		this.appClazz = appClazz;
	}

	public void startApp(Activity activity) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, appClazz);
		intent.putExtra("type", tag);
		// intent.putExtra("type", getAppName());
		activity.startActivity(intent);
	}

	public void startApp(Activity activity, String tag) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, appClazz);
		intent.putExtra("type", tag);
		activity.startActivity(intent);
	}

	public void startApp(Activity activity, Bundle bundle) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, appClazz);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}

}
