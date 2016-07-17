package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONObject;



/** 
 * 类说明：   用户组标签
 * @author  wz    
 * @date    2014-12-24
 * @version 1.0
 */
public class ModleUserGroup extends SociaxItem {
	int user_group_id;
	String user_group_name,
	ctime,
	user_group_icon,
	user_group_type,
	app_name,
	is_authenticate,
	user_group_icon_url;
	
	public ModleUserGroup(JSONObject data) {
		// TODO Auto-generated method stub
		try{
			if(data.has("user_group_id"))this.setUser_group_id(data.getInt("user_group_id"));
			if(data.has("user_group_name"))this.setUser_group_name(data.getString("user_group_name"));
			if(data.has("ctime"))this.setCtime(data.getString("ctime"));
			if(data.has("user_group_icon"))this.setUser_group_icon(data.getString("user_group_icon"));
			if(data.has("user_group_type"))this.setUser_group_type(data.getString("user_group_type"));
			if(data.has("app_name"))this.setApp_name(data.getString("app_name"));
			if(data.has("is_authenticate"))this.setIs_authenticate(data.getString("is_authenticate"));
			if(data.has("user_group_icon_url"))this.setUser_group_icon_url(data.getString("user_group_icon_url"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    public ModleUserGroup() {
		// TODO Auto-generated method stub
	}
	public int getUser_group_id() {
		return user_group_id;
	}

	public void setUser_group_id(int user_group_id) {
		this.user_group_id = user_group_id;
	}

	public String getUser_group_name() {
		return user_group_name;
	}

	public void setUser_group_name(String user_group_name) {
		this.user_group_name = user_group_name;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getUser_group_icon() {
		return user_group_icon;
	}

	public void setUser_group_icon(String user_group_icon) {
		this.user_group_icon = user_group_icon;
	}

	public String getUser_group_type() {
		return user_group_type;
	}

	public void setUser_group_type(String user_group_type) {
		this.user_group_type = user_group_type;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getIs_authenticate() {
		return is_authenticate;
	}

	public void setIs_authenticate(String is_authenticate) {
		this.is_authenticate = is_authenticate;
	}

	public String getUser_group_icon_url() {
		return user_group_icon_url;
	}

	public void setUser_group_icon_url(String user_group_icon_url) {
		this.user_group_icon_url = user_group_icon_url;
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
