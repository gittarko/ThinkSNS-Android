package com.thinksns.sociax.t4.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明： 返回信息解析类
 * 
 * @author PC
 * @date 2014-9-4
 * @version 1.0
 */
public class ModelBackMessage {

	private int status;// 返回状态
	private String msg;// 返回信息
	private int weiboId;// 被操作的微博id

	public int getWeiboId() {
		return weiboId;
	}

	public void setWeiboId(int weiboId) {
		this.weiboId = weiboId;
	}

	public ModelBackMessage(String jsonString) throws JSONException {
		JSONObject jo = new JSONObject(jsonString);
		if(jo.has("status"))this.setStatus(jo.getInt("status"));
		if(jo.has("msg"))this.setMsg(jo.getString("msg"));
		if (jo.has("feed_id"))
			this.setWeiboId(jo.getInt("feed_id"));
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "ModelBackMessage [status=" + status + ", msg=" + msg
				+ ", weiboId=" + weiboId + "]";
	}

	
}
