package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * 类说明：   
 * @author  Zoey    
 * @date    2015年9月10日
 * @version 1.0
 */
public class ModelCopyTaskCons extends SociaxItem {

	boolean status;
	String desc;
	
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public ModelCopyTaskCons() {
		super();
	}

	public ModelCopyTaskCons(JSONObject data) throws DataInvalidException {
		super(data);
		
			try {
				if(data.has("status")) this.setStatus(data.getBoolean("status"));
				if(data.has("desc")) this.setDesc(data.getString("desc"));
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
