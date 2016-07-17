package com.thinksns.sociax.t4.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年9月9日
 * @version 1.0
 */
public class ModelDailyOrMainTask extends SociaxItem {

	private String name;
	private String desc;
	private String status;
	private String progress_rate;
	private int exp;
	private int score;
	private String icon;
	
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProgress_rate() {
		return progress_rate;
	}
	public void setProgress_rate(String progress_rate) {
		this.progress_rate = progress_rate;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	public ModelDailyOrMainTask() {
		super();
	}
	
	public ModelDailyOrMainTask(JSONObject data) throws DataInvalidException {
		super(data);
		
			try {
				if(data.has("name")) this.setName(data.getString("name"));
				if(data.has("desc")) this.setDesc(data.getString("desc"));
				if(data.has("status")) this.setStatus(data.getString("status"));
				if(data.has("progress_rate")) this.setProgress_rate(data.getString("progress_rate"));
				if(data.has("exp")) this.setExp(data.getInt("exp"));
				if(data.has("score")) this.setScore(data.getInt("score"));
				if(data.has("icon")) this.setIcon(data.getString("icon"));
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
