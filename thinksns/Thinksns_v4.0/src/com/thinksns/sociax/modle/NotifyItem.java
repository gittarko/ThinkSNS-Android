package com.thinksns.sociax.modle;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class NotifyItem extends SociaxItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String type;
	private String name;
	private String icon;
	private int count;
	private String Data;
	private String content;
	private String fromUname;
	private String face;
	private String timesTmap;
	private Message message;

	public NotifyItem() {
	}

	public NotifyItem(int id, String type, String name, String icon, int count) {

		this.type = type;
		this.name = name;
		this.icon = icon;
		this.count = count;

	}

	public NotifyItem(JSONObject notifyItemData) throws JSONException {

		this.type = notifyItemData.getString("type");
		this.name = notifyItemData.getString("name");
		this.icon = notifyItemData.getString("icon");
		this.count = notifyItemData.getInt("count");

		if (notifyItemData.getString("data") != null
				&& !notifyItemData.getString("data").equals("")) {

			JSONObject data = notifyItemData.getJSONObject("data");

			this.content = data.getString("content");
			// this.face = data.getString("from_face");
			if (data.has("last_message")) {
				data = data.getJSONObject("last_message");
				if (data.has("user_info")) {
					data = data.getJSONObject("user_info");
					this.face = data.getString("avatar_middle");
					System.out.println("user head" + this.face);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "NotifyItem [id=" + id + ", type=" + type + ", name=" + name
				+ ", icon=" + icon + ", count=" + count + ", Data=" + Data
				+ ", content=" + content + ", fromUname=" + fromUname
				+ ", face=" + face + ", timesTmap=" + timesTmap + "]";
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getData() {
		return Data;
	}

	public void setData(String data) {
		Data = data;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFromUname() {
		return fromUname;
	}

	public void setFromUname(String fromUname) {
		this.fromUname = fromUname;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getTimesTmap() {
		return timesTmap;
	}

	public void setTimesTmap(String timesTmap) {
		this.timesTmap = timesTmap;
	}

}