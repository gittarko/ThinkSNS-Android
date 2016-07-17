package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONException;
import org.json.JSONObject;


/** 
 * 类说明：   反馈类型
 * @author  wz    
 * @date    2015-1-26
 * @version 1.0
 */
public class ModelFeedBack extends SociaxItem {
   int type_id;
   String type_name;
   
   public ModelFeedBack(JSONObject data){
	   if(data.has("type_id")){
		   try {
			this.setType_id(data.getInt("type_id"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	   if(data.has("type_name")){
		   try {
			this.setType_name(data.getString("type_name"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
   }
	public int getType_id() {
	return type_id;
}

public void setType_id(int i) {
	this.type_id = i;
}

public String getType_name() {
	return type_name;
}

public void setType_name(String type_name) {
	this.type_name = type_name;
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
