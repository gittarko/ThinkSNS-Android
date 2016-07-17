package com.thinksns.sociax.modle;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.exception.SiteDataInvalidException;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

public class ApproveSite extends SociaxItem {
	private static final long serialVersionUID = 1853900287499877764L;
	private static final String TAG = "ApproveSite";
	private int site_id;
	private String name;
	private String url;
	private String logo;
	private String description;
	private int uid;
	private String email;
	private String phone;
	private String ctime;
	private boolean status;
	private String status_mtime;
	private String denied_reason;
	private int isInUsed;

	public ApproveSite(JSONObject siteData) throws SiteDataInvalidException {
		try {
			this.setSite_id(siteData.getInt("site_id"));
			this.setName(siteData.getString("name"));
			this.setUrl(siteData.getString("url"));
			this.setLogo(siteData.getString("logo"));
			this.setDescription(siteData.has("description") ? siteData
					.getString("description") : "");
			this.setIsInUsed(siteData.has("isused") ? siteData.getInt("isused")
					: 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			throw new SiteDataInvalidException(e.getMessage());
		}
	}

	/*
	 * public ApproveSite (JSONObject siteData) throws SiteDataInvalidException{
	 * try { this.setSite_id(siteData.getInt("site_id"));
	 * this.setName(siteData.getString("name"));
	 * this.setUrl(siteData.getString("url"));
	 * this.setLogo(siteData.getString("logo"));
	 * this.setDescription(siteData.has
	 * ("description")?siteData.getString("description"):"");
	 * this.setUid(siteData.has("uid")?siteData.getInt("uid"):0);
	 * this.setEmail(siteData.has("email")?siteData.getString("email"):"");
	 * this.setPhone(siteData.has("phone")?siteData.getString("phone"):"");
	 * this.setCtime(siteData.has("phone")?siteData.getString("ctime"):"");
	 * this.setStatus(siteData.getInt("status") == 1? true:false);
	 * this.setStatus_mtime
	 * (siteData.has("status_mtime")?siteData.getString("status_mtime"):"");
	 * this.setDenied_reason(siteData.has("denied_reason")?siteData.getString(
	 * "denied_reason"):"");
	 * this.setIsInUsed(siteData.has("isused")?siteData.getInt("isused"):0); }
	 * catch (JSONException e) { // TODO Auto-generated catch block throw new
	 * SiteDataInvalidException(e.getMessage()); } }
	 */

	public int getIsInUsed() {
		return isInUsed;
	}

	public void setIsInUsed(int isInUsed) {
		this.isInUsed = isInUsed;
	}

	public ApproveSite() {

	}

	public int getSite_id() {
		Log.d(TAG, "site_id=" + site_id);
		return site_id;
	}

	public void setSite_id(int site_id) {
		this.site_id = site_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getStatus_mtime() {
		return status_mtime;
	}

	public void setStatus_mtime(String status_mtime) {
		this.status_mtime = status_mtime;
	}

	public String getDenied_reason() {
		return denied_reason;
	}

	public void setDenied_reason(String denied_reason) {
		this.denied_reason = denied_reason;
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

	public Bitmap getLogoUrl() {
		return Thinksns.getImageCache().get(this.getLogo());
	}

	public void setLogoUrl(Bitmap thumbLarge) {
		Thinksns.getImageCache().put(this.getLogo(), thumbLarge);
	}

}
