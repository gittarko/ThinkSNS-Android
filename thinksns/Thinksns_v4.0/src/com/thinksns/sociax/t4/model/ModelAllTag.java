package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年10月23日
 * @version 1.0
 */
public class ModelAllTag extends SociaxItem {

	String id;
	String pid;
	String title;
	int level;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public ModelAllTag() {
		super();
	}

	public ModelAllTag(JSONObject data) throws DataInvalidException {
		super(data);
			try {
				if(data.has("id")) this.setId(data.getString("id"));
				if(data.has("pid")) this.setPid(data.getString("pid"));
				if(data.has("title")) this.setTitle(data.getString("title"));
				if(data.has("level")) this.setLevel(data.getInt("level"));
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
