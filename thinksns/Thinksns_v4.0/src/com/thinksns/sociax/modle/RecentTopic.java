package com.thinksns.sociax.modle;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明：
 * 
 * @author povol
 * @date Jan 21, 2013
 * @version 1.0
 */
public class RecentTopic extends SociaxItem {

	private String name;
	int topic_id;//话题id

	public int getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(int topic_id) {
		this.topic_id = topic_id;
	}

	public RecentTopic() {
	}

	public RecentTopic(JSONObject data) throws JSONException {
		if(data.has("topic_name")){
		setName(data.getString("topic_name"));}
		if(data.has("topic_id"))
		setTopic_id(data.getInt("topic_id"));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
