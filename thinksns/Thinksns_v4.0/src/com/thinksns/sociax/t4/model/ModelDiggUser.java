package com.thinksns.sociax.t4.model;

import org.json.JSONObject;


/**
 * 类说明：赞我的用户列表
 *  用于微博详情
 * @author wz
 * @date 2014-9-28
 * @version 1.0
 */
public class ModelDiggUser extends ModelSearchUser {
	private String cTime, uname, intro, avatar;
	public ModelDiggUser() {

	}

	public ModelDiggUser(JSONObject data) {
		try {
			if (data.has("uname"))
				this.setUname(data.getString("uname"));
			if (data.has("id"))
				this.setId(data.getInt("id"));

			if (data.has("uid"))
				this.setUid(data.getInt("uid"));

			if (data.has("cTime"))
				this.setcTime(data.getString("cTime"));

			if (data.has("intro"))
				this.setIntro(data.getString("intro"));

			if (data.has("avatar"))
				this.setAvatar(data.getString("avatar"));

			if (data.has("follow_status")) {
				this.setFollowing(data.getJSONObject("follow_status").getString("following"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getcTime() {
		return cTime;
	}

	public void setcTime(String cTime) {
		this.cTime = cTime;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return avatar;
	}
}
