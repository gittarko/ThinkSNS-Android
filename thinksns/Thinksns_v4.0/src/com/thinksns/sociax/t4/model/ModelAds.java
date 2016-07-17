package com.thinksns.sociax.t4.model;

import java.io.Serializable;

import org.json.JSONObject;

import android.util.Log;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

public class ModelAds extends SociaxItem implements Serializable{

	private String title;
	
	private String imageUrl;
	
	private String type;
	
	private String data;

	public ModelAds() {}
	
	public ModelAds(JSONObject data){
		try {
			if (data.has("title")) {
				this.setTitle(data.getString("title"));
			}
			if (data.has("image")) {
				this.setImageUrl(data.getString("image"));
			}
			if (data.has("type")) {
				this.setType(data.getString("type"));
			}
			if (data.has("data")) {
				this.setData(data.getString("data"));
			}
		} catch (Exception e) {
			Log.e(null, "解析轮播图片数据出错");
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	
	
}
