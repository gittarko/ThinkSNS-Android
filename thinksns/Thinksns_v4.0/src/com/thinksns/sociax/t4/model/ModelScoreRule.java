package com.thinksns.sociax.t4.model;

import org.json.JSONException;
import org.json.JSONObject;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/** 
 * 类说明：   
 * 
 * @author  Zoey    
 * @date    2015年9月29日
 * @version 1.0
 */
public class ModelScoreRule extends SociaxItem {

	private String name;
	private String alias;
	private String score;
	private String experience;
	private String score_alias;
	private String experience_alias;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getScore_alias() {
		return score_alias;
	}

	public void setScore_alias(String score_alias) {
		this.score_alias = score_alias;
	}

	public String getExperience_alias() {
		return experience_alias;
	}

	public void setExperience_alias(String experience_alias) {
		this.experience_alias = experience_alias;
	}
	
	public ModelScoreRule() {
		super();
	}

	public ModelScoreRule(JSONObject data) throws DataInvalidException {
		super(data);
		try {
			if(data.has("name")) this.setName(data.getString("name"));
			if(data.has("alias")) this.setAlias(data.getString("alias"));
			if(data.has("score")) this.setScore(data.getString("score"));
			if(data.has("experience")) this.setExperience(data.getString("experience"));
			if(data.has("score_alias")) this.setScore_alias(data.getString("score_alias"));
			if(data.has("experience_alias")) this.setExperience_alias(data.getString("experience_alias"));
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
