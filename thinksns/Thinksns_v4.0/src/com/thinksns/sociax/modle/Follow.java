package com.thinksns.sociax.modle;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.UserDataInvalidException;

public class Follow extends ModelUser {

	protected int friendId;

	protected int followId;
	private int uid;
	private String uname;
	private String remark;
	private int following;
	private int follower;
	// private String department ;

	private String headUrl;

	public int getFriendId() {
		return friendId;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	public int getFollowId() {
		return followId;
	}

	public void setFollowId(int followId) {
		this.followId = followId;
	}

	public Follow(JSONObject data) throws DataInvalidException {
		if (data.equals("null"))
			throw new DataInvalidException();
		try {
			this.initUserInfo(data.getJSONObject("user"));
			if (data.has("id"))
				this.setFollowId(data.getInt("id"));
			if (data.has("weibo") && !data.getString("weibo").equals("false")) {
				this.setLastWeibo(new ModelWeibo(data.getJSONObject("weibo")));
			}
		} catch (JSONException e) {
			Log.d(AppConstant.APP_TAG, "json erroe wm " + e.toString());
			throw new UserDataInvalidException();
		}
	}

	public Follow(JSONObject data, String tag) throws DataInvalidException {
		if (data.equals("null"))
			throw new DataInvalidException();
		try {
			if (data.has("follow_id")) {
				setFollowId(data.getInt("follow_id"));
			}
			setUid(data.getInt("uid"));
			setUname(data.getString("uname"));
			// setDepartment(data.getJSONObject("department").getString("title"));
			/*
			 * if(!data.getString("department").equals("")){
			 * setDepartment(data.getJSONObject
			 * ("department").getString("title")); }
			 */

			if (data.has("sex"))
				this.setSex(data.getString("sex"));

			if (data.has("profile")
					&& (!data.getString("profile").equals("[]"))) {
				JSONObject departInfo = data.getJSONObject("profile");
				if (departInfo.has("department"))
					setDepartment(departInfo.getJSONObject("department")
							.getString("value"));
			}

			if (data.has("follow_state")) {
				JSONObject countInfo = data.getJSONObject("follow_state");
				this.setFollowed(countInfo.getString("following").equals("0") ? false
						: true);
			}

			JSONObject temp = data.getJSONObject("follow_state");
			setFollowing(temp.getInt("following"));
			setFollower(temp.getInt("follower"));
			setHeadUrl(data.getString("avatar_big"));

			if (data.has("count_info")) {
				JSONObject countInfo = data.getJSONObject("count_info");
				if (countInfo.has("weibo_count")) {
					this.setWeiboCount(countInfo.getInt("weibo_count"));
				}
				if (countInfo.has("follower_count")) {
					this.setFollowersCount(countInfo
							.getString("follower_count").equals("false") ? 0
							: countInfo.getInt("follower_count"));
				}

				if (countInfo.has("following_count")) {
					this.setFollowedCount(countInfo
							.getString("following_count").equals("false") ? 0
							: countInfo.getInt("following_count"));
				}
			}

		} catch (JSONException e) {
			Log.d(AppConstant.APP_TAG,
					"follow.java json erroe wm " + e.toString());
			throw new DataInvalidException();
		}
	}

	@Override
	public int getUid() {
		return uid;
	}

	@Override
	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		setUserName(uname);
		this.uname = uname;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getFollowing() {
		return following;
	}

	public void setFollowing(int following) {
		this.following = following;
	}

	public int getFollower() {
		return follower;
	}

	public void setFollower(int follower) {
		this.follower = follower;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	@Override
	public String getUserface() {
		return this.getHeadUrl();
	}

	/*
	 * public String getDepartment() { return department; }
	 * 
	 * public void setDepartment(String department) { this.department =
	 * department; }
	 */

}
