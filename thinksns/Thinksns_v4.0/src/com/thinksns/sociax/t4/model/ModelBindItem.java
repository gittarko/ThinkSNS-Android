package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONObject;


/**
 * 类说明： 账号绑定类型
 * 
 * @author wz
 * @date 2014-12-3
 * @version 1.0
 */
public class ModelBindItem extends SociaxItem {
	private String type="",// 英文，例如phone/sina
			name="";// 中文名称
	private boolean isBind=false;

	public ModelBindItem(JSONObject data) {
		try {
			if (data.has("type")) {
				this.setType(data.getString("type"));
			}
			if(data.has("name")){
				this.setName(data.getString("name"));
			}
			if(data.has("isBind")){
				this.setBind(data.getString("isBind").equals("1")?true:false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isBind() {
		return isBind;
	}

	public void setBind(boolean isBind) {
		this.isBind = isBind;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
