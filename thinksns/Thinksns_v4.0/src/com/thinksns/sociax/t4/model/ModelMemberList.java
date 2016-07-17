package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明：
 * 
 * @author Zoey
 * @date 2015-8-4
 * @version 1.0
 */
public class ModelMemberList extends SociaxItem {

	int uid;
	String uname;
	int ctime;
	int mtime;
	int message_new;
	int message_num;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public int getCtime() {
		return ctime;
	}

	public void setCtime(int ctime) {
		this.ctime = ctime;
	}

	public int getMtime() {
		return mtime;
	}

	public void setMtime(int mtime) {
		this.mtime = mtime;
	}

	public int getMessage_new() {
		return message_new;
	}

	public void setMessage_new(int message_new) {
		this.message_new = message_new;
	}

	public int getMessage_num() {
		return message_num;
	}

	public void setMessage_num(int message_num) {
		this.message_num = message_num;
	}

	public ModelMemberList(JSONObject object) {

		try {
			
			this.setUid(object.getInt("uid"));
			this.setUname(object.getString("uname"));
			this.setCtime(object.getInt("ctime"));
			this.setMtime(object.getInt("mtime"));
			this.setMessage_new(object.getInt("message_new"));
			this.setMessage_num(object.getInt("message_num"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return null;
	}
}
