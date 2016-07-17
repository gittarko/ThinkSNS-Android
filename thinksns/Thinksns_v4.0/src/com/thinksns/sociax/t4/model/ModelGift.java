package com.thinksns.sociax.t4.model;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-9-15
 * @version 1.0
 */
public class ModelGift extends SociaxItem implements Serializable {
	private String giftName, giftPrice, giftPicurl, id, num;
	private String giftId;

	public String getGiftId() {
		return giftId;
	}

	public void setGiftId(String giftId) {
		this.giftId = giftId;
	}

	public ModelGift(JSONObject data) {
		try {

			if (data.has("id"))
				this.setId(data.getString("id"));
			if (data.has("num"))
				this.setNum(data.getString("num"));
			if (data.has("image"))
				this.setGiftPicurl(data.getString("image"));
			if (data.has("price")) {
				this.setGiftPrice(data.getString("price"));
			}
			if(data.has("giftId")){
				this.setGiftId(data.getString("giftId"));
			}
			if (data.has("name")) {
				this.setGiftName(data.getString("name"));
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

	public String getGiftPicurl() {
		return giftPicurl;
	}

	public void setGiftPicurl(String giftPicurl) {
		this.giftPicurl = giftPicurl;
	}

	public String getGiftPrice() {
		return giftPrice;
	}

	public void setGiftPrice(String giftPrice) {
		this.giftPrice = giftPrice;
	}

	public String getGiftName() {
		return giftName;
	}

	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}


}
