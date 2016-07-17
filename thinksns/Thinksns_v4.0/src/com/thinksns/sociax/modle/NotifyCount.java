package com.thinksns.sociax.modle;

import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class NotifyCount implements Serializable {

	private static final long serialVersionUID = 1L;
	private int message;
	private int notify;
	private int weiboComment;
	private int atme;
	private int total;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public static enum Type {
		message, notify, weibo_count, atme, comment;
	}

	public NotifyCount() {
	}

	public NotifyCount(JSONObject data) throws DataInvalidException {
		try {
			this.setAtme(data.getInt("atme"));
			this.setNotify(data.getInt("notify"));
			this.setMessage(data.getInt("message"));
			this.setWeiboComment(data.getInt("weibo_comment"));
			this.setTotal(data.getInt("total"));
		} catch (JSONException e) {
			throw new DataInvalidException("数据格式错误");
		}
	}

	public void setCount1(int message, int notify, int weiboComment, int atme) {
		this.setAtme(atme);
		this.setNotify(notify);
		this.setMessage(message);
		this.setWeiboComment(weiboComment);
	}

	@Override
	public String toString() {
		return "NotifyCount [message=" + message + ", notify=" + notify
				+ ", weiboComment=" + weiboComment + ", atme=" + atme
				+ ", total=" + total + "]";
	}

	public int getMessage() {
		return message;
	}

	public void setMessage(int message) {
		this.message = message;
	}

	public int getNotify() {
		return notify;
	}

	public void setNotify(int notify) {
		this.notify = notify;
	}

	public int getWeiboComment() {
		return weiboComment;
	}

	public void setWeiboComment(int weiboComment) {
		this.weiboComment = weiboComment;
	}

	public int getAtme() {
		return atme;
	}

	public void setAtme(int atme) {
		this.atme = atme;
	}

}
