package com.thinksns.sociax.t4.model;

import org.json.JSONObject;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-10-20
 * @version 1.0
 */
public class ModelChat extends SociaxItem {
	protected String ctime,// 聊天时间
			content,// 聊天内容
			newCount;// 最新数目
	protected ModelUser chatuser;// 聊天来自用户

	public ModelChat() {
		try {
			this.setCtime("2014-10-20 17:20");
			this.setContent("最近忙什么呢");
			this.setNewCount("0");
			this.setChatuser(new ModelUser(
					2,
					"海虾",
					"http://tsimg.tsurl.cn/avatar/c8/1e/72/original.jpg!middle.avatar.jpg?v1367497151"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNewCount() {
		return newCount;
	}

	public void setNewCount(String newCount) {
		this.newCount = newCount;
	}

	public ModelUser getChatuser() {
		return chatuser;
	}

	public void setChatuser(ModelUser chatuser) {
		this.chatuser = chatuser;
	}

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserface() {
		// TODO Auto-generated method stub
		return getChatuser().getPassword();
	}

}
