package com.thinksns.sociax.t4.model;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明： 微博/聊天等附件
 * 
 * @author wz
 * @date 2014-10-27
 * @version 1.0
 */
public class ModelAttach implements Serializable {
	private String attach_id;
	String image_url;
	int Image_width, image_height;
	String voice_url;

	public String getVoice_url() {
		return voice_url;
	}

	public void setVoice_url(String voice_url) {
		this.voice_url = voice_url;
	}

	public ModelAttach() {

	}

	public ModelAttach(JSONObject data) {
		try {
			if (data.has("attach_id"))
				this.setAttach_id(data.getString("attach_id"));
			if (data.has("image_url"))
				this.setImage_url(data.getString("image_url"));
			if (data.has("image_width"))
				this.setImage_width(data.getInt("image_width"));
			if (data.has("image_height"))
				this.setImage_height(data.getInt("image_height"));
			if (data.has("voice_url"))
				this.setVoice_url(data.getString("voice_url"));

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public ModelAttach(JSONArray data) {
		try {
			this.setAttach_id(data.getString(0));
			this.setImage_url(data.getString(1));
			this.setImage_width(data.getInt(2));
			this.setImage_height(data.getInt(3));

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public String getAttach_id() {
		return attach_id;
	}

	public void setAttach_id(String attach_id) {
		this.attach_id = attach_id;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public int getImage_width() {
		return Image_width;
	}

	public void setImage_width(int image_width) {
		Image_width = image_width;
	}

	public int getImage_height() {
		return image_height;
	}

	public void setImage_height(int image_height) {
		this.image_height = image_height;
	}
}
