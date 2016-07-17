package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年9月9日
 * @version 1.0
 */
public class ModelMedals extends SociaxItem {

	private String id;
	private String name;
	private String desc;
	private String icon;
	private String show;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}
	
	
	public ModelMedals(JSONObject data) throws DataInvalidException {
		super(data);
		
			try {
				if (data.has("id")) this.setId(data.getString("id"));
				if (data.has("name")) this.setName(data.getString("name"));
				if (data.has("desc")) this.setDesc(data.getString("desc"));
				if (data.has("icon")) this.setIcon(data.getString("icon"));
				if (data.has("show")) this.setShow(data.getString("show"));
				
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
