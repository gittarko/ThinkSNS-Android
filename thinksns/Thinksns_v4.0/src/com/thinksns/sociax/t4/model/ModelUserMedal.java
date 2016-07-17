package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 类说明： 用户勋章
 * 
 * @author wz
 * @date 2015-1-26
 * @version 1.0
 */
public class ModelUserMedal extends SociaxItem {
	/**
	 * 勋章id
	 */
	int id;
	/**
	 * 勋章名称
	 */
	String name;
	/**
	 * 勋章描述
	 */
	String desc;
	/**
	 * 勋章图片名称
	 */
	String src;
	/**
	 * 图片地址
	 */
	String small_src;

	public ModelUserMedal(JSONObject data) {
		try {
			if (data.has("id")) {
				this.setId(data.getInt("id"));
			}
			if (data.has("name")) {
				this.setName(data.getString("name"));
			}
			if (data.has("desc")) {
				this.setDesc(data.getString("desc"));
			}
			if (data.has("src")) {
				this.setSrc(data.getString("src"));
			}
			if (data.has("small_src")) {
				this.setSmall_src(data.getString("small_src"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getSmall_src() {
		return small_src;
	}

	public void setSmall_src(String small_src) {
		this.small_src = small_src;
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
