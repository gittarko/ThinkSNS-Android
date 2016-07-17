package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONException;
import org.json.JSONObject;


/** 
 * 类说明：   用户级别详情
 * @author  wz    
 * @date    2015-1-24
 * @version 1.0
 */
public class ModelUserCredit extends SociaxItem {
	 String experience_value,//经验值,
	 experience_alias,//经验别名
	 score_value,//积分值
	 score_alias,//积分别名
	 charm_value ,//魅力值
	 charm_alias;//魅力别名
	public ModelUserCredit(JSONObject data){
		if(data.has("experience")){
			try {
				this.setExperience_value(data.getJSONObject("experience").getString("value"));
				this.setExperience_alias(data.getJSONObject("experience").getString("alias"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(data.has("score")){
			try {
				this.setScore_value(data.getJSONObject("score").getString("value"));
				this.setScore_alias(data.getJSONObject("score").getString("alias"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(data.has("charm")){
			try {
				this.setCharm_value(data.getJSONObject("charm").getString("value"));
				this.setCharm_alias(data.getJSONObject("charm").getString("alias"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
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
	
	/**
	 * 获取用户经验值
	 * @return
	 */
	public String getExperience_value() {
		return experience_value;
	}
	public void setExperience_value(String experience_value) {
		this.experience_value = experience_value;
	}
	public String getExperience_alias() {
		return experience_alias;
	}
	public void setExperience_alias(String experience_alias) {
		this.experience_alias = experience_alias;
	}
	/**
	 * 获取用户积分值
	 * @return
	 */
	public String getScore_value() {
		return score_value;
	}
	public void setScore_value(String score_value) {
		this.score_value = score_value;
	}
	public String getScore_alias() {
		return score_alias;
	}
	public void setScore_alias(String score_alias) {
		this.score_alias = score_alias;
	}
	/**
	 * 获取用户魅力值
	 * @return
	 */
	public String getCharm_value() {
		return charm_value;
	}
	public void setCharm_value(String charm_value) {
		this.charm_value = charm_value;
	}
	public String getCharm_alias() {
		return charm_alias;
	}
	public void setCharm_alias(String charm_alias) {
		this.charm_alias = charm_alias;
	}
	
}
