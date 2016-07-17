package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONObject;


/** 
 * 类说明：   话题
 * @author  wz    
 * @date    2014-12-15
 * @version 1.0
 */
public class ModelTopic extends SociaxItem {
	int topic_id;//话题id
	String  topic_name,
	count,
	ctime,
	status,
	lock,
	domain,
	recommend,//1推荐话题，0普通话题
	recommend_time,//推荐时间
	des,//描述
	outlink,
	pic,//图片
	essence,
	note,
	topic_user,//话题用户列表
	top_feed;
	private boolean isFirst=false;//标记是否第一个话题
	
	
	public boolean isFirst() {
		return isFirst;
	}
	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}
	public ModelTopic(){}
	public ModelTopic(JSONObject data){
		try {
			if(data.has("topic_id")){
				this.setTopic_id(data.getInt("topic_id"));
			}
			if(data.has("topic_name")){
				this.setTopic_name(data.getString("topic_name"));
			}
			if(data.has("count")){
				this.setCount(data.getString("count"));
			}
			if(data.has("ctime")){
				this.setCtime(data.getString("ctime"));
			}
			if(data.has("status")){
				this.setStatus(data.getString("status"));
			}
			if(data.has("lock")){
				this.setLock(data.getString("lock"));
			}
			if(data.has("domain")){
				this.setDomain(data.getString("domain"));
			}
			if(data.has("recommend")){
				this.setRecommend(data.getString("recommend"));
			}
			if(data.has("recommend_time")){
				this.setRecommend_time(data.getString("recommend_time"));
			}
			if(data.has("des")){
				this.setDes(data.getString("des"));
			}
			if(data.has("outlink")){
				this.setOutlink(data.getString("outlink"));
			}
			if(data.has("pic")){
				this.setPic(data.getString("pic"));
			}
			if(data.has("essence")){
				this.setEssence(data.getString("essence"));
			}
			if(data.has("note")){
				this.setNote(data.getString("note"));
			}
			if(data.has("topic_user")){
				this.setTopic_user(data.getString("topic_user"));
			}
			if(data.has("top_feed")){
				this.setTop_feed(data.getString("top_feed"));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	

	public int getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(int topic_id) {
		this.topic_id = topic_id;
	}

	public String getTopic_name() {
		return topic_name;
	}

	public void setTopic_name(String topic_name) {
		this.topic_name = topic_name;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getRecommend() {
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public String getRecommend_time() {
		return recommend_time;
	}

	public void setRecommend_time(String recommend_time) {
		this.recommend_time = recommend_time;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getOutlink() {
		return outlink;
	}

	public void setOutlink(String outlink) {
		this.outlink = outlink;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getEssence() {
		return essence;
	}

	public void setEssence(String essence) {
		this.essence = essence;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getTopic_user() {
		return topic_user;
	}

	public void setTopic_user(String topic_user) {
		this.topic_user = topic_user;
	}

	public String getTop_feed() {
		return top_feed;
	}

	public void setTop_feed(String top_feed) {
		this.top_feed = top_feed;
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
