package com.thinksns.sociax.t4.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年10月23日
 * @version 1.0
 */
public class ModelMyTag extends SociaxItem {

	int tag_id;
	String tag_name;
	
	public int getTag_id() {
		return tag_id;
	}

	public void setTag_id(int tag_id) {
		this.tag_id = tag_id;
	}

	public String getTag_name() {
		return tag_name;
	}

	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}
	
	public ModelMyTag() {
		super();
	}

	public ModelMyTag(JSONObject data) throws DataInvalidException {
		super(data);
			try {
				if(data.has("tag_id")) this.setTag_id(data.getInt("tag_id"));
				if(data.has("tag_name")) {
					this.setTag_name(data.getString("tag_name"));
				}else if(data.has("name")) {
					this.setTag_name(data.getString("name"));
				}
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
