package com.thinksns.sociax.modle;

import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import java.util.ArrayList;
import java.util.List;

public class Message extends SociaxItem {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "Message";
	// add by xhs
	private int room_id;
	private int from_client_id;
	private int from_uid;
	private String from_uname;
	private String min_max;
	private String msgtype;
	private String to_client_id;
	private int to_uid;
	private String to_uname;
	private String type;
	private String content;
	private int time;
	private int my_uid;

	// add by xhs
	private int listId;

	// private int memberUid;
	private int forNew = 1;
	private int messageNum;
	// private int toUid;
	// private String toName;
	private String toUserUrl;// 用户图标
	// private String ctime;
	// private int listCtime;
	// private int fromUid;
	// private String type;//1文字，2语音，3图片,4地图
	// private String title;
	// private int memeberNum;
	// private String minMax;
	// private int mtime;
	// private String content;
	// private Message LastMessage;
	// private String fromUname;
	private String fromFace;
	// private int timeStmap;
	private int meesageId;
	// private int newMsg;
	private int degree;// 照片的旋转度数
	private double latitude;
	private double longitude;

	// private String infoTitle;

	public Message() {
	}

	public Message(JSONObject data, boolean type) throws DataInvalidException {
		// super(data);
		// try {
		// if (type) {
		// this.setFromUid(data.getInt("from_uid"));
		//
		// if (data.has("content"))
		// this.setContent(data.getString("content"));
		//
		// if (data.has("to_uid") && !data.isNull("to_uid")) {
		// if (data.getJSONArray("to_uid").length() != 0)
		// this.setToUid(Integer.valueOf((data
		// .getJSONArray("to_uid").get(0)).toString()));
		// }
		// } else {
		// this.setListId(data.getInt("list_id"));
		// this.setFromUid(data.getInt("from_uid"));
		// this.setMeesageId(data.getInt("message_id"));
		// this.setContent(data.getString("content"));
		// this.setMtime(data.getInt("mtime"));
		// this.setFromFace(data.getString("from_face"));
		// this.setFromUname(data.getString("from_uname"));
		// this.setTimeStmap(data.getInt("timestmap"));
		// this.setCtime(data.getString("ctime"));
		// if (data.has("new"))
		// this.setNewMsg(data.getInt("new"));
		// }
		//
		// } catch (JSONException e) {
		// Log.d("Message class construct", type + "");
		// Log.d("Message class construct", e.toString());
		// }
	}

	public Message(JSONObject data) throws DataInvalidException {
		// super(data);
		// try {
		// this.setListId(data.getInt("list_id"));
		// this.setMemberUid(data.getInt("member_uid"));
		// this.setForNew(data.getInt("new"));
		// this.setMessageNum(data.getInt("message_num"));
		// this.setCtime(data.getString("ctime"));
		// this.setListCtime(data.getInt("list_ctime"));
		// this.setFromUid(data.getInt("from_uid"));
		// // this.setType(data.getInt("type"));
		// this.setTitle(data.getString("title"));
		// this.setMemeberNum(data.getInt("member_num"));
		// this.setMinMax(data.getString("min_max").equals("") ? "" : data
		// .getString("min_max"));
		// this.setMtime(data.getInt("mtime"));
		//
		// if (data.has("content"))
		// this.setContent(data.getString("content"));
		// this.setFromUname(data.getString("from_uname"));
		// this.setFromFace(data.getString("from_face"));
		// if (data.getString("last_message") != "") {
		// this.setLastMessage(new Message(data
		// .getJSONObject("last_message"), true));
		// if (data.getJSONObject("last_message").getJSONArray("to_uid")
		// .length() != 0)
		// this.setToUid(Integer.valueOf((data.getJSONObject(
		// "last_message").getJSONArray("to_uid").get(0))
		// .toString()));
		// }
		// if (data.has("to_user_info")) {
		// JSONObject jsonTemp = new JSONObject(
		// data.getString("to_user_info"));
		//
		// for (@SuppressWarnings("rawtypes")
		// Iterator iterator = jsonTemp.keys(); iterator.hasNext();) {
		// String key = (String) iterator.next();
		// JSONObject jsonToUser = jsonTemp.getJSONObject(key);
		// setToName(jsonToUser.getString("uname"));
		// setToUserUrl(jsonToUser.getString("avatar_middle"));
		// }
		// }
		// } catch (JSONException e) {
		// Log.d("Message class construct", e.toString());
		// }
	}

	public static List<SociaxItem> parserCursor(Cursor cursor) {
		List<SociaxItem> data = new ArrayList<SociaxItem>();
		while (cursor.moveToNext()) {

			Message message = new Message();
			message.setType(cursor.getString(cursor.getColumnIndex("type")));
			message.setListId(cursor.getInt(cursor.getColumnIndex("list_id")));
			message.setMeesageId(cursor.getInt(cursor
					.getColumnIndex("message_id")));
			message.setFrom_uid((cursor.getInt(cursor
					.getColumnIndex("from_uid"))));
			message.setContent(cursor.getString(cursor
					.getColumnIndex("content")));
			message.setMin_max(cursor.getString(cursor
					.getColumnIndex("min_max")));
			// message.setTitle(cursor.getString(cursor
			// .getColumnIndex("title")));
			message.setTo_uid(cursor.getInt(cursor.getColumnIndex("to_uid")));
			message.setFromFace(cursor.getString(cursor
					.getColumnIndex("from_face")));
			message.setMsgtype(cursor.getString(cursor
					.getColumnIndex("msgtype")));
			message.setTo_client_id(cursor.getString(cursor
					.getColumnIndex("to_client_id")));
			message.setFrom_uname(cursor.getString(cursor
					.getColumnIndex("from_uname")));
			message.setTime(cursor.getInt(cursor.getColumnIndex("time")));
			// message.setCtime(cursor.getString(cursor
			// .getColumnIndex("ctime")));
			message.setDegree(cursor.getInt(cursor.getColumnIndex("degree")));
			message.setLongitude(cursor.getDouble(cursor
					.getColumnIndex("longitude")));
			message.setLatitude(cursor.getDouble(cursor
					.getColumnIndex("latitude")));
			data.add(message);
		}
		return data;
	}

	public long getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public double getLatitude() {
		return latitude;
	}

	public String getFromFace() {
		return fromFace;
	}

	public void setFromFace(String fromFace) {
		this.fromFace = fromFace;
	}

	public String getToUserUrl() {
		return toUserUrl;
	}

	public void setToUserUrl(String toUserUrl) {
		this.toUserUrl = toUserUrl;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public int getMessageNum() {
		return messageNum;
	}

	public void setMessageNum(int messageNum) {
		this.messageNum = messageNum;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		Log.d(TAG, this.content);
	}

	//
	// public boolean isNullForMinMax() {
	// return this.minMax.equals(null) || this.minMax.equals("");
	// }

	public int getRoom_id() {
		return room_id;
	}

	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}

	public int getFrom_client_id() {
		return from_client_id;
	}

	public void setFrom_client_id(int from_client_id) {
		this.from_client_id = from_client_id;
	}

	public int getFrom_uid() {
		return from_uid;
	}

	public void setFrom_uid(int from_uid) {
		this.from_uid = from_uid;
	}

	public String getFrom_uname() {
		return from_uname;
	}

	public void setFrom_uname(String from_uname) {
		this.from_uname = from_uname;
	}

	public String getMin_max() {
		return min_max;
	}

	public void setMin_max(String min_max) {
		this.min_max = min_max;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public String getTo_client_id() {
		return to_client_id;
	}

	public void setTo_client_id(String to_client_id) {
		this.to_client_id = to_client_id;
	}

	public int getTo_uid() {
		return to_uid;
	}

	public void setTo_uid(int to_uid) {
		this.to_uid = to_uid;
	}

	public String getTo_uname() {
		return to_uname;
	}

	public void setTo_uname(String to_uname) {
		this.to_uname = to_uname;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getForNew() {
		return forNew;
	}

	public void setForNew(int forNew) {
		this.forNew = forNew;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public int getMy_uid() {
		return my_uid;
	}

	public void setMy_uid(int my_uid) {
		this.my_uid = my_uid;
	}

	public int getMeesageId() {
		return meesageId;
	}

	public void setMeesageId(int meesageId) {
		this.meesageId = meesageId;
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

	@Override
	public String toString() {
		return "Message [room_id=" + room_id + ", from_client_id="
				+ from_client_id + ", from_uid=" + from_uid + ", from_uname="
				+ from_uname + ", min_max=" + min_max + ", msgtype=" + msgtype
				+ ", to_client_id=" + to_client_id + ", to_uid=" + to_uid
				+ ", to_uname=" + to_uname + ", type=" + type + ", content="
				+ content + ", time=" + time + ", my_uid=" + my_uid
				+ ", listId=" + listId + ", forNew=" + forNew + ", messageNum="
				+ messageNum + ", toUserUrl=" + toUserUrl + ", fromFace="
				+ fromFace + ", meesageId=" + meesageId + ", degree=" + degree
				+ ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}

}
