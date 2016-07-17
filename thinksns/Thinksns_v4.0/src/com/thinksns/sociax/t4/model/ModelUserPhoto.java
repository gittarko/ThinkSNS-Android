package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONObject;


/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-11-24
 * @version 1.0
 */
public class ModelUserPhoto extends SociaxItem {
	private String imageId, imgUrl;

	public ModelUserPhoto(JSONObject data) {
		try {
			if (data.has("image_id")) {
				this.setImageId(data.getString("image_id"));
			}
			if (data.has("image_url")) {
				this.setImgUrl(data.getString("image_url"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

}
