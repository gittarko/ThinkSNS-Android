package com.thinksns.sociax.modle;

import org.json.JSONException;
import org.json.JSONObject;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

public class Contact extends ModelUser {

	private int uid;
	private String uname;
	private String uHeadUrl;
	private String firstLetter; // 首字母
	private String department; // 部门
	private int followerState;
	private int isFavorite;

	private String type;

	public Contact() {
	}

	public Contact(JSONObject data) throws DataInvalidException, JSONException {
		// super(data);
		this.setUid(data.getInt("uid"));
		if (data.has("firstLetter"))
			this.setFirstLetter(data.getString("firstLetter"));
		this.setUname(data.getString("uname"));
		if (data.has("avatar_middle"))
			this.setuHeadUrl(data.getString("avatar_middle"));
		if (data.has("follow_state"))
			this.setFollowerState(data.getJSONObject("follow_state").getInt(
					"following"));
		if (data.has("isFavorites"))
			this.setIsFavorite(data.getInt("isFavorites"));
		this.setDepartment(data.getString("department"));
		if (data.has("type"))
			this.setType(data.getString("type"));
		if (data.has("email"))
			this.setUserEmail(data.getString("email"));
	}

	@Override
	public int getUid() {
		return uid;
	}

	@Override
	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getuHeadUrl() {
		return uHeadUrl;
	}

	public void setuHeadUrl(String uHeadUrl) {
		this.uHeadUrl = uHeadUrl;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getFirstLetter() {
		return firstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		this.firstLetter = firstLetter;
	}

	@Override
	public String getDepartment() {
		return department;
	}

	@Override
	public void setDepartment(String department) {
		this.department = department;
	}

	public int getFollowerState() {
		return followerState;
	}

	public void setFollowerState(int followerState) {
		this.followerState = followerState;
	}

	public int getIsFavorite() {
		return isFavorite;
	}

	public void setIsFavorite(int isFavorite) {
		this.isFavorite = isFavorite;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
