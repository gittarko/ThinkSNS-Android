package com.thinksns.sociax.t4.model;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import android.util.Log;
import android.widget.ImageView;

/**
 * 类说明：
 * 
 * @author qcj
 * @date 2015年7月9日
 * @version 1.0
 */
public class ModelChatContent extends SociaxItem {
	/**
	 * "message_id":"11538", "list_id":"3483", "from_uid":"33815",
	 * "msgtype":"text", "content":"5555555555555555555555555",
	 * "time":"1436349276", "is_member":"", "room_type":"chat",
	 * "from_uname":"啊该", "from_avatar":
	 * "http://demo.thinksns.com/ts4/data/upload/avatar/f7/c5/21/original_200_200.jpg?v1435544312"
	 * 
	 * */
	private String message_id;  //信息id
	private String list_id;  //等于room_id
	private String from_uid;  //自己的id
	private String msgtype;  //信息的类型
	private String content;  //内容
	private String time;  //发送的时间
	private String is_member; // 
	private String room_type; // 房间的类型
	private String from_uname;// 名字
	private String from_avatar;// 头像链接
	private String min_max;//
	
	int room_id = 0;// 聊天房间id
	int from_client_id = 0;// 来自客户端
	int attach_id = 0;// 附件id
	String imageUrl;//
	String voiceUrl;// 语音地址（如果有的话）
	int voice_Length = 0;

	String latitude;// 经度
	String longitude;
	String poi_name;// 地理位置名称 eg：北京西二旗
	String poi_image;// 地理位置图片

	int image_width;// 照片宽度高度（如果有的话）
	int image_height;//

	private ImageView ivAudio;

	private int isNew;// 标记消息是否为新，1表示新消息，从web端接收过来都标记成新的，0表示不是新消息

	private String members;// 群聊时候的uid（，号分隔，包括自己）
	private String act;// 群聊的时候act
	private String room_title;// 群聊的时候的title
	private File file;
	
	
	//当msgtype==card的时候有下面几个字段
	private String card_avatar,//卡片地址
	card_uname,//卡片名字
	card_intro;//卡片简介
	private int card_uid;//卡片uid
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

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

	public int getAttach_id() {
		return attach_id;
	}

	public void setAttach_id(int attach_id) {
		this.attach_id = attach_id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getVoiceUrl() {
		return voiceUrl;
	}

	public void setVoiceUrl(String voiceUrl) {
		this.voiceUrl = voiceUrl;
	}

	public int getVoice_Length() {
		return voice_Length;
	}

	public void setVoice_Length(int voice_Length) {
		this.voice_Length = voice_Length;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getPoi_name() {
		return poi_name;
	}

	public void setPoi_name(String poi_name) {
		this.poi_name = poi_name;
	}

	public String getPoi_image() {
		return poi_image;
	}

	public void setPoi_image(String poi_image) {
		this.poi_image = poi_image;
	}

	public int getImage_width() {
		return image_width;
	}

	public void setImage_width(int image_width) {
		this.image_width = image_width;
	}

	public int getImage_height() {
		return image_height;
	}

	public void setImage_height(int image_height) {
		this.image_height = image_height;
	}

	public ImageView getIvAudio() {
		return ivAudio;
	}

	public void setIvAudio(ImageView ivAudio) {
		this.ivAudio = ivAudio;
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public String getMembers() {
		return members;
	}

	public void setMembers(String members) {
		this.members = members;
	}

	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public String getRoom_title() {
		return room_title;
	}

	public void setRoom_title(String room_title) {
		this.room_title = room_title;
	}

	public String getCard_avatar() {
		return card_avatar;
	}

	public void setCard_avatar(String card_avatar) {
		this.card_avatar = card_avatar;
	}

	public String getCard_uname() {
		return card_uname;
	}

	public void setCard_uname(String card_uname) {
		this.card_uname = card_uname;
	}

	public String getCard_intro() {
		return card_intro;
	}

	public void setCard_intro(String card_intro) {
		this.card_intro = card_intro;
	}

	public int getCard_uid() {
		return card_uid;
	}

	public void setCard_uid(int card_uid) {
		this.card_uid = card_uid;
	}


	public String getMin_max() {
		return min_max;
	}

	public void setMin_max(String min_max) {
		this.min_max = min_max;
	}

	public ModelChatContent() {

	}

	// type=1 获取聊天内容，type获取最近的一条消息
	public ModelChatContent(JSONObject data) {
		
//		Log.v("chatDetail", "-----------chatDetail-------------"+data.toString());
//		Log.v("chatDetail", "-----------token-------------"+Thinksns.getMy().getToken());
		
		try {
			if (data.has("message_id")) {
				this.setMessage_id(data.getString("message_id"));
			}
			if (data.has("list_id")) {
				this.setList_id(data.getString("list_id"));
			}
			if (data.has("from_uid")) {
				this.setFrom_uid(data.getString("from_uid"));
			}
			if (data.has("msgtype")) {
				this.setMsgtype(data.getString("msgtype"));
			}
			if (data.has("content")) {
				this.setContent(data.getString("content"));
			}
			if (data.has("time")) {
				this.setTime(data.getString("time"));
			}
			if (data.has("is_member")) {
				this.setIs_member(data.getString("is_member"));
			}
			if (data.has("room_type")) {
				this.setRoom_type(data.getString("room_type"));
			}
			if (data.has("from_uname")) {
				this.setFrom_uname(data.getString("from_uname"));
			}
			if (data.has("from_avatar")) {
				this.setFrom_avatar(data.getString("from_avatar"));
			}
			if (data.has("last_message")) {
				this.setContent((data.getString("last_message")));
			}
			if (data.has("min_max")) {
				this.setMin_max(data.getString("min_max"));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "ModelChatContent [message_id=" + message_id + ", list_id="
				+ list_id + ", from_uid=" + from_uid + ", msgtype=" + msgtype
				+ ", content=" + content + ", time=" + time + ", is_member="
				+ is_member + ", room_type=" + room_type + ", from_uname="
				+ from_uname + ", from_avatar=" + from_avatar + ", min_max="
				+ min_max + "]";
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getList_id() {
		return list_id;
	}

	public void setList_id(String list_id) {
		this.list_id = list_id;
	}

	public String getFrom_uid() {
		return from_uid;
	}

	public void setFrom_uid(String from_uid) {
		this.from_uid = from_uid;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getIs_member() {
		return is_member;
	}

	public void setIs_member(String is_member) {
		this.is_member = is_member;
	}

	public String getRoom_type() {
		return room_type;
	}

	public void setRoom_type(String room_type) {
		this.room_type = room_type;
	}

	public String getFrom_uname() {
		return from_uname;
	}

	public void setFrom_uname(String from_uname) {
		this.from_uname = from_uname;
	}

	public String getFrom_avatar() {
		return from_avatar;
	}

	public void setFrom_avatar(String from_avatar) {
		this.from_avatar = from_avatar;
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return null;
	}
}
