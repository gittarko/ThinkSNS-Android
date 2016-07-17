package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.io.Serializable;

import org.json.JSONObject;


/**
 * 类说明： 未读消息抽象类
 * 
 * @author wz
 * @date 2014-11-26
 * @version 1.0
 */
public class ModelNotification extends SociaxItem implements Serializable {
	int comment,// 消息页面新的评论我的，-1表示错误数据
			atme,// 消息页面新的@我的，-1表示错误数据
			digg,// 消息页面新的赞我的，-1表示错误数据
			follower;// 个人主页我新粉丝，-1表示错误数据
	int weibaComment;	//微吧新增评论数
	private boolean isValid = false;

	public ModelNotification(JSONObject data) {
		try {
			if (data.has("comment")) {
				this.setComment(data.getInt("comment"));
			}
			if (data.has("atme")) {
				this.setAtme(data.getInt("atme"));
			}
			if (data.has("digg")) {
				this.setDigg(data.getInt("digg"));
			}
			if (data.has("follower")) {
				this.setFollower(data.getInt("follower"));
			}
			if(data.has("weiba")) {
				this.setWeibaComment(data.getInt("weiba_comment"));
			}
			isValid = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 没有网络数据的情况下生成的notificaition全部为-1
	 */
	public ModelNotification() {
		this.setAtme(-1);
		this.setComment(-1);
		this.setDigg(-1);
		this.setFollower(-1);
		this.setWeibaComment(-1);

	}

	public int getComment() {
		return comment;
	}

	public void setComment(int comment) {
		this.comment = comment;
	}

	public int getAtme() {
		return atme;
	}

	public void setAtme(int atme) {
		this.atme = atme;
	}

	public int getDigg() {
		return digg;
	}

	public void setDigg(int digg) {
		this.digg = digg;
	}

	public int getFollower() {
		return follower;
	}

	public void setFollower(int follower) {
		this.follower = follower;
	}

	public int getWeibaComment() {
		return weibaComment;
	}

	public void setWeibaComment(int weibaComment) {
		this.weibaComment = weibaComment;
	}

	@Override
	public boolean checkValid() {
		return isValid;
	}

	@Override
	public String getUserface() {
		return null;
	}
}
