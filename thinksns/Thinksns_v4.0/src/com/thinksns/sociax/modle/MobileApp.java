package com.thinksns.sociax.modle;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

public class MobileApp extends SociaxItem {

	private int appId;
	private String appName;
	private int appIconId;
	private String appIconUrl;
	private String appType; // 是html5 还是 native
	private String appLink; //
	private int isInstall;

	private String tag;
	private Class<? extends Activity> appClazz;

	public MobileApp() {
	}

	// AppInfo(int id, String appName, int appImage, boolean isNotice)
	public MobileApp(int appId, String appName, int appIconId, String appType,
			String appLink) {
		this.setAppId(appId);
		this.setAppName(appName);
		this.setAppIconId(appIconId);
		this.setAppType(appType);
		this.setAppLink(appLink);
		setAppClazz();
	}

	public MobileApp(JSONObject data) throws DataInvalidException,
			JSONException {
		super(data);
		this.setAppId(data.getInt("app_id"));
		this.setAppName(data.getString("app_name"));
		this.setAppIconUrl(data.getString("android_icon"));
		this.setAppType(data.getString("host_type"));
		this.setAppLink(data.getString("type"));
		this.setInstall(data.getInt("is_used"));
		setAppClazz();
	}

	public Class<? extends Activity> getAppClazz() {
		return appClazz;
	}

	public void setAppClazz(Class<? extends Activity> appClazz) {
		this.appClazz = appClazz;
	}

	public void setAppClazz() {
//		this.appClazz = SociaxActivityClazz.getGoAppClazz(getAppType(),
//				getAppLink());
	}

	public void startApp(Activity activity) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, appClazz);
		intent.putExtra("type", tag);
		intent.putExtra("link", getAppLink());
		// intent.putExtra("type", getAppName());
		activity.startActivity(intent);
		Anim.in(activity);
	}

	public void startApp(Activity activity, String tag) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, appClazz);
		intent.putExtra("type", tag);
		intent.putExtra("link", getAppLink());
		activity.startActivity(intent);
	}

	public void startApp(Activity activity, Bundle bundle) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, appClazz);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}

	// ////////////geter and seter /////////////////////////////////////

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int getAppIconId() {
		return appIconId;
	}

	public void setAppIconId(int appIconId) {
		this.appIconId = appIconId;
	}

	public String getAppIconUrl() {
		return appIconUrl;
	}

	public void setAppIconUrl(String appIconUrl) {
		this.appIconUrl = appIconUrl;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getAppLink() {
		return appLink;
	}

	public void setAppLink(String appLink) {
		this.appLink = appLink;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	// ////////////////////////*****************//////////////////////

	public int isInstall() {
		return isInstall;
	}

	public void setInstall(int isInstall) {
		this.isInstall = isInstall;
	}

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserface() {
		// TODO Auto-generated method stub
		return null;
	}

}
