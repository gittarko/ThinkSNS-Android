package com.thinksns.sociax.t4.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 类说明：
 * 
 * @author povol
 * @date Feb 28, 2014
 * @version 1.0
 */
public class ModelPhoto implements Parcelable {
	// private static final long serialVersionUID = 1L;
	int id;			//照片id
	String name;	//照片名称
	String url;		//照片url
	String oriUrl;	//照片原始url
	String middleUrl;	//中等图URL
	String des;
	int type; 		// 0 默认，1 URL;
	int isDigg;

	public ModelPhoto(JSONObject jo) throws JSONException {
		setUrl(jo.getString("attach_url"));
		setId(jo.getInt("attach_id"));
		setName(jo.getString("attach_name"));
		setOriUrl(jo.getString("attachurl_big"));
	}

	public ModelPhoto(JSONObject jo, String type) throws JSONException {
		setUrl(jo.getString("attachurl_big"));
		setId(jo.getInt("eventId"));
		setName(jo.getString("des"));
		setOriUrl(jo.getString("attachurl"));
		setIsDigg(jo.getInt("isdigg"));
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriUrl() {
		return oriUrl;
	}

	public void setOriUrl(String oriUrl) {
		this.oriUrl = oriUrl;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public int getIsDigg() {
		return isDigg;
	}

	public void setIsDigg(int isDigg) {
		this.isDigg = isDigg;
	}

	public String getMiddleUrl() {
		return middleUrl;
	}

	public void setMiddleUrl(String middleUrl) {
		this.middleUrl = middleUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(url);
		dest.writeString(oriUrl);
		dest.writeString(middleUrl);
		dest.writeString(des);
		dest.writeInt(type);
		dest.writeInt(isDigg);
	}

	public static final Parcelable.Creator<ModelPhoto> CREATOR = new Parcelable.Creator<ModelPhoto>() {

		@Override
		public ModelPhoto createFromParcel(Parcel source) {
			ModelPhoto p = new ModelPhoto();
			p.setId(source.readInt());
			p.setName(source.readString());
			p.setUrl(source.readString());
			p.setOriUrl(source.readString());
			p.setMiddleUrl(source.readString());
			p.setDes(source.readString());
			p.setType(source.readInt());
			p.setIsDigg(source.readInt());
			return p;
		}

		@Override
		public ModelPhoto[] newArray(int size) {
			return new ModelPhoto[size];
		}
	};

	public ModelPhoto() {
	}

}
