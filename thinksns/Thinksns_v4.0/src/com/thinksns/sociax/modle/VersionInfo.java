package com.thinksns.sociax.modle;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONException;
import org.json.JSONObject;

public class VersionInfo extends SociaxItem {

	private int versionCode;
	private Object upgradeTips;
	private String downUrl;
	private int mustUgrade;

	public VersionInfo() {
	}

	public VersionInfo(JSONObject data) throws JSONException {
		versionCode = data.getInt("version_code");
		upgradeTips = data.getString("upgrade_tips");
		downUrl = data.getString("download_url");
		mustUgrade = data.getInt("must_upgrade");
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public Object getUpgradeTips() {
		return upgradeTips;
	}

	public void setUpgradeTips(Object upgradeTips) {
		this.upgradeTips = upgradeTips;
	}

	public String getDownUrl() {
		return downUrl;
	}

	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}

	public int getMustUgrade() {
		return mustUgrade;
	}

	public void setMustUgrade(int mustUgrade) {
		this.mustUgrade = mustUgrade;
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
