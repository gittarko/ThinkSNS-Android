package com.thinksns.tschat.bean;

import android.content.ContentValues;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.thinksns.tschat.chat.TSChatManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 类说明： 聊天信息
 * 
 * @author wz
 * @date 2014-10-21
 * @version 1.0
 */
public class ModelChatMessage extends Entity {
	private String chat_user_face,
			chat_user_name;			// 聊天对方的姓名/头像url，
	private int uid_loginUser = 0,// 聊天创建人uid，默认登陆用户uid，用于数据库查询
			uid_chatUser = 0,// 被聊天用户的id，
			to_uid = 0,// 消息发送方一般为loginUser/chatUser
			uid = 0, from_uid = 0;// 消息接收方，一般为loginUser/chatUser
	// String type,// 类型 标记接受/发送 目前有say/group
	String type,// 类型 标记接受/发送 目前有send_message/push_message
			to_client_id,// 发送到客户端id
			min_max,// 标记to_uid--from_uid
			msgtype,// 消息的类型 "text/link/position/card/image/voice"
					// 特殊类型record作为清理历史记录之后遗留的一条用于记录聊天信息的消息
			from_uname,// 消息发送方名字
			from_uface,// 消息发送方头像地址
			to_uname,// 消息接收方名字
			to_uface,// 消息接收方头像地址
			content;
	String room_type;// 标记聊天房间类型，目前有chat/group区分单聊/群聊

	int time = 0;// 消息时间
	int room_id = 0;// 聊天房间id
	int from_client_id = 0;// 来自客户端
	String attach_id;// 附件id
	String imageUrl;//
	String voiceUrl;// 语音地址（如果有的话）
	int length;

	double latitude;// 经度
	double longitude;
	String location;
	String poi_name;// 地理位置名称 eg：北京西二旗
	String poi_image;// 地理位置图片

	int image_width;// 照片宽度高度（如果有的话）
	int image_height;//

	private ImageView ivAudio;
	private AnimationDrawable animation;

	private int isNew;	// 标记消息是否为新，1表示新消息，从web端接收过来都标记成新的，0表示不是新消息
	private boolean isSend;	//是发送消息还是接收消息
	private int sendState;	//消息发送状态 0-等待发送 1-正在发送 2-发送失败 3-发送成功
	private String members;// 群聊时候的uid（，号分隔，包括自己）
	private String act;// 群聊的时候act
	private String room_title;// 群聊的时候的title

	private int message_id, mtime;

	// 当msgtype==card的时候有下面几个字段
	private String card_avatar,// 卡片地址
			card_uname,// 卡片名字
			card_intro;// 卡片简介
	private int card_uid;// 卡片uid
	private String packid;
	private String localPath = "";// 附件本地地址
	private String attach_url = "";// 附件网络地址
	private float imgWidth,imgHeight;//图片的宽高
	private String imgSendState;//图片的发送状态
	private String notify_type;// 消息通知类型
	private String room_info;
	private int room_master_uid, room_add_uid, room_del_uid, quit_uid;
	private String room_add_uname, room_del_uname,quit_uname,master_uname;
	private String description = "";		//改参数用于消息提示

	private boolean showTime = false; // 是否显示时间
	private boolean isOriginal = false;	//当消息是发送的图片时，此标志位表示是否发送原图
	private ModelChatUserList currentRoom;		//当前消息所在房间

	public ModelChatUserList getCurrentRoom() {
		return currentRoom;
	}

	public void setCurrentRoom(ModelChatUserList currentRoom) {
		this.currentRoom = currentRoom;
	}

	public boolean isOriginal() {
		return isOriginal;
	}

	public void setOriginal(boolean original) {
		isOriginal = original;
	}

	//消息发送状态
	public enum SEND_STATE {
		DEFAULT, SEND_READY, SENDING, SEND_ERROR, SEND_OK
	}

	//消息类型
	public enum MSG_TYPE {
		TEXT, VOICE, IMAGE, POSITION, CARD, NOTIFY
	}

	public MSG_TYPE getMsgType() {
		String type = getType();
		if (type == null || type.equals("text")) {
			return MSG_TYPE.TEXT;
		} else if (type.equals("voice")) {
			return MSG_TYPE.VOICE;
		} else if (type.equals("image")) {
			return MSG_TYPE.IMAGE;
		} else if (type.equals("position")) {
			return MSG_TYPE.POSITION;
		} else if (type.equals("card")) {
			return MSG_TYPE.CARD;
		} else if (type.equals("notify")) {
			return MSG_TYPE.NOTIFY;
		}

		return MSG_TYPE.TEXT;
	}

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

	public SEND_STATE getSendState() {
		switch (sendState) {
			case 0:
				return SEND_STATE.DEFAULT;
			case 1:
				return SEND_STATE.SEND_READY;
			case 2:
				return SEND_STATE.SENDING;
			case 3:
				return SEND_STATE.SEND_ERROR;
			case 4:
				return SEND_STATE.SEND_OK;
		}

		return SEND_STATE.SEND_READY;
	}

	public void setSendState(SEND_STATE sendState) {
		switch (sendState) {
			case DEFAULT:
				this.sendState = 0;
			case SEND_READY:
				this.sendState = 1;
				break;
			case SENDING:
				this.sendState = 2;
				break;
			case SEND_ERROR:
				this.sendState = 3;
				break;
			case SEND_OK:
				this.sendState = 4;
				break;
		}
	}

	public void setSendState(int sendState) {
		this.sendState = sendState;
	}

	public String getDescription() {
		if(description == null)
			description = "";
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMaster_uname() {
		return master_uname;
	}

	public void setMaster_uname(String master_uname) {
		this.master_uname = master_uname;
	}

	public String getAttach_url() {
		return attach_url;
	}

	public void setAttach_url(String attach_url) {
		this.attach_url = attach_url;
	}

	public float getImgWidth() {
		return imgWidth;
	}

	public void setImgWidth(float imgWidth) {
		this.imgWidth = imgWidth;
	}

	public float getImgHeight() {
		return imgHeight;
	}

	public void setImgHeight(float imgHeight) {
		this.imgHeight = imgHeight;
	}

	public String getImgSendState() {
		return imgSendState;
	}

	public void setImgSendState(String imgSendState) {
		this.imgSendState = imgSendState;
	}

	public boolean isShowTime() {
		return showTime;
	}

	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getMessage_id() {
		return message_id;
	}

	public void setMessage_id(int message_id) {
		this.message_id = message_id;
	}

	public int getMtime() {
		return mtime;
	}

	public void setMtime(int mtime) {
		this.mtime = mtime;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getPackid() {
		return packid;
	}

	public void setPackid(String packid) {
		this.packid = packid;
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

	public String getRoom_title() {
		return room_title;
	}

	public void setRoom_title(String room_title) {
		this.room_title = room_title;
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

	public void setTime(int time) {
		this.time = time;
	}

	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}

	public void setFrom_client_id(int from_client_id) {
		this.from_client_id = from_client_id;
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

	public String getVoiceUrl() {
		return voiceUrl;
	}

	public void setVoiceUrl(String voiceUrl) {
		this.voiceUrl = voiceUrl;
	}

	public ModelChatMessage() {
	}

	public ImageView getIvAudio() {
		return ivAudio;
	}

	public AnimationDrawable getAnimation() {
		return animation;
	}

	public void setAnimation(AnimationDrawable drawable) {
		this.animation = drawable;
	}

	public void setIvAudio(ImageView ivAudio) {
		this.ivAudio = ivAudio;
	}

	public String getNotify_type() {
		return notify_type;
	}

	public void setNotify_type(String notify_type) {
		this.notify_type = notify_type;
	}

	public String getRoom_info() {
		return room_info;
	}

	public void setRoom_info(String room_info) {
		this.room_info = room_info;
	}

	public int getQuit_uid() {
		return quit_uid;
	}

	public void setQuit_uid(int quit_uid) {
		this.quit_uid = quit_uid;
	}

	public String getQuit_uname() {
		return quit_uname;
	}

	public void setQuit_uname(String quit_uname) {
		this.quit_uname = quit_uname;
	}

	public int getRoom_master_uid() {
		return room_master_uid;
	}

	public void setRoom_master_uid(int room_master_uid) {
		this.room_master_uid = room_master_uid;
	}

	public int getRoom_add_uid() {
		return room_add_uid;
	}

	public void setRoom_add_uid(int room_add_uid) {
		this.room_add_uid = room_add_uid;
	}

	public int getRoom_del_uid() {
		return room_del_uid;
	}

	public void setRoom_del_uid(int room_del_uid) {
		this.room_del_uid = room_del_uid;
	}

	public String getRoom_add_uname() {
		return room_add_uname;
	}

	public void setRoom_add_uname(String room_add_uname) {
		this.room_add_uname = room_add_uname;
	}

	public String getRoom_del_uname() {
		return room_del_uname;
	}

	public void setRoom_del_uname(String room_del_uname) {
		this.room_del_uname = room_del_uname;
	}

	public ModelChatMessage(JSONObject data) {
		try {
			this.setUid_loginUser(TSChatManager.getLoginUser().getUid());	// 默认发起人是本人
			if (data.has("to_uid") && data.has("from_uid")) {
				// 设置和谁的聊天，如果to_uid是我，则from_uid是对方，否则to_uid是对方
				this.setUid_chatUser(data.getInt("to_uid") == TSChatManager.getLoginUser()
						.getUid() ? data.getInt("from_uid") : data
						.getInt("to_uid"));
			}
			if (data.has("type"))
				this.setType(data.getString("type"));
			if (data.has("from_client_id"))
				this.setFrom_client_id(data.getInt("from_client_id"));
			if (data.has("to_client_id"))
				this.setTo_client_id(data.getString("to_client_id"));
			if (data.has("room_id"))
				this.setRoom_id(data.getInt("room_id"));
			if (data.has("min_max"))
				this.setMin_max(data.getString("min_max"));
			if (data.has("message_type"))
				this.setMsgtype(data.getString("message_type"));
			if (data.has("from_uid"))
				this.setFrom_uid(data.getInt("from_uid"));
			if (data.has("from_uname"))
				this.setFrom_uname(data.getString("from_uname"));
			if (data.has("from_avatar"))
				this.setFrom_uface(data.getString("from_avatar"));
			if (data.has("to_uid")) {
				this.setTo_uid(data.getInt("to_uid"));
			}
			if (data.has("to_uname"))
				this.setTo_uname(data.getString("to_uname"));
			if (data.has("to_avatar"))
				this.setTo_uface(data.getString("to_avatar"));
			if (data.has("content"))
				this.setContent(data.getString("content"));
			if (data.has("attach_id"))
				this.setAttach_id(data.getString("attach_id"));
			if (data.has("time"))
				this.setTime(data.getInt("time"));
			if (data.has("image_url"))
				this.setImageUrl(data.getString("image_url"));
			if (data.has("voice_url"))
				this.setVoiceUrl(data.getString("voice_url"));
			if (data.has("image_width"))
				this.setImage_width(data.getInt("image_width"));
			if (data.has("image_height"))
				this.setImage_height(data.getInt("image_height"));
			if (data.has("poi_name"))
				this.setPoi_name(data.getString("poi_name"));
			if (data.has("poi_lat"))
				this.setLatitude(data.getDouble("poi_lat"));
			if (data.has("poi_lng"))
				this.setLongitude(data.getDouble("poi_lng"));
			if (data.has("poi_image"))
				this.setPoi_image(data.getString("poi_image"));
			if (data.has("members")) {
				this.setMembers(data.getString("members"));
			}
			if (data.has("room_type")) {
				this.setRoom_type(data.getString("room_type"));
			}
			if (data.has("act")) {
				this.setAct(data.getString("act"));
			}
			if (data.has("room_title")) {
				this.setRoom_title(data.getString("room_title"));
			}
			if (data.has("length")) {
				this.setLength(data.getInt("length"));
			}
			if (data.has("notify_type")) {
				this.setNotify_type(data.getString("notify_type"));
			}
			if (data.has("room_info")) {
				this.setRoom_info(data.getString("room_info"));
			}
			if (data.has("quit_uid")) {
				this.setQuit_uid(data.getInt("quit_uid"));
			}
			if (data.has("quit_uname")) {
				this.setQuit_uname(data.getString("quit_uname"));
			}
			if (data.has("room_master_uid")) {
				this.setRoom_master_uid(data.getInt("room_master_uid"));
			}
			if (data.has("card_intro")) {
				this.setCard_intro(data.getString("card_intro"));
			}
			if (data.has("card_uname")) {
				this.setCard_uname(data.getString("card_uname"));
			}
			if (data.has("card_uid")) {
				this.setCard_uid(data.getInt("card_uid"));
			}
			if (data.has("card_avatar")) {
				this.setCard_avatar(data.getString("card_avatar"));
			}
			// 如果from_uid是聊天对方id，则是对方发过来的消息，那么获取from_uname作为聊天名字，否则取to_uname作为聊天名字；uface同理
			this.setChatUserName(getFrom_uid() == getUid_chatUser() ? getFrom_uname()
					: getTo_uname());
			this.setChatUerFace(getFrom_uid() == getUid_chatUser() ? getFrom_uface()
					: getTo_uface());

			this.setIsNew(1);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建消息体
	 *
	 * @param obj
	 * @return
	 */
	public static ModelChatMessage createMessageBody(JSONObject obj) {
		ModelChatMessage msg = new ModelChatMessage();
		try {
			int from_uid = obj.getInt("from_uid"); //消息发送人
			if(from_uid == TSChatManager.getLoginUser().getUid()) {
				//消息是自己发送的
				msg.isSend = true;
			}
			int message_id = obj.getInt("message_id");
			// 消息时间
			int mtime = obj.getInt("mtime");
			int room_id = obj.getInt("room_id");
			//消息类型
			String type = obj.optString("type");
			//消息内容
			String content = "";
			if (type.equals("text")) {
				content = obj.getString("content");
			} else if (type.equals("voice")) { // 语音消息
				msg.setLength(obj.optInt("length"));
				content = "[语音]";
			} else if (type.equals("image")) { // 图片消息
				content = "[图片]";
			} else if (type.equals("position")) { // 位置消息
				double latitude = obj.optDouble("latitude");
				double longitude = obj.optDouble("longitude");

				msg.setLatitude(latitude);
				msg.setLongitude(longitude);
				msg.setLocation(obj.optString("location"));
				content = "[位置]";
			} else if (type.equals("card")) {
				int uid = obj.optInt("uid");
				msg.setCard_uid(uid);
				//名片头像，名片姓名异步请求
				content = "[名片]";
			} else if(type.equals("notify")) {
				String notify_type = obj.optString("notify_type");
				msg.setNotify_type(notify_type);
				content = "[动态]";
			}

			//消息为image,voice,position
			String attach_id = obj.optString("attach_id");

			msg.setMessage_id(message_id);
			msg.setFrom_uid(from_uid);
			msg.setType(type);
			msg.setContent(content);
			msg.setRoom_id(room_id);
			msg.setMtime(mtime);
			msg.setAttach_id(attach_id);
			//收到的消息默认是发送成功的
			msg.setSendState(SEND_STATE.SEND_OK);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return msg;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAttach_id() {
		return attach_id;
	}

	public void setAttach_id(String attach_id) {
		this.attach_id = attach_id;
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public String getUserface() {
		return TSChatManager.getLoginUser().getUserFace();
	}

	public int getUid_loginUser() {
		return uid_loginUser;
	}

	public void setUid_loginUser(int uid_loginUser) {
		this.uid_loginUser = uid_loginUser;
	}

	public int getUid_chatUser() {
		return uid_chatUser;
	}

	public void setUid_chatUser(int uid_chatUser) {
		this.uid_chatUser = uid_chatUser;
	}

	public int getTo_uid() {
		return to_uid;
	}

	public void setTo_uid(int to_uid) {
		this.to_uid = to_uid;
	}

	public int getFrom_uid() {
		return from_uid;
	}

	public void setFrom_uid(int from_uid) {
		this.from_uid = from_uid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getFrom_client_id() {
		return from_client_id;
	}

	public void setFrom_client_id(Integer integer) {
		this.from_client_id = integer;
	}

	public String getTo_client_id() {
		return to_client_id;
	}

	public void setTo_client_id(String to_client_id) {
		this.to_client_id = to_client_id;
	}

	public Integer getRoom_id() {
		return room_id;
	}

	public void setRoom_id(Integer integer) {
		this.room_id = integer;
	}

	public String getMin_max() {
		return min_max;
	}

	public void setMin_max(String min_max) {
		this.min_max = min_max;
	}

	/**
	 * 消息的类型
	 */
	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public String getFrom_uname() {
		return from_uname;
	}

	public void setFrom_uname(String from_uname) {
		this.from_uname = from_uname;
	}

	public String getFrom_uface() {
		return from_uface;
	}

	public void setFrom_uface(String from_uface) {
		this.from_uface = from_uface;
	}

	public String getTo_uname() {
		return to_uname;
	}

	public void setTo_uname(String to_uname) {
		this.to_uname = to_uname;
	}

	public String getTo_uface() {
		return to_uface;
	}

	public void setTo_uface(String to_uface) {
		this.to_uface = to_uface;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer integer) {
		this.time = integer;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	/**
	 * 转化成map 存进数据库
	 *
	 * @return
	 */
	public ContentValues toContentValues() {
		ContentValues map = new ContentValues();
		map.put("login_uid", TSChatManager.getLoginUser().getUid());
		map.put("message_id", getMessage_id());
		map.put("from_uid", getFrom_uid());
		map.put("type", getType());
		map.put("room_id", getRoom_id());
		map.put("content", getContent());
		map.put("uid", TSChatManager.getLoginUser().getUid());
		map.put("length", getLength());
		map.put("latitude", getLatitude());
		map.put("longitude", getLongitude());
		map.put("location", getLocation());
		map.put("attach_id", getAttach_id());
		map.put("mtime", getMtime());
		map.put("isNew", getIsNew());
		map.put("from_uface", getFrom_uface());
		map.put("attach_url", getAttach_url());
		map.put("localPath", getLocalPath());

		map.put("imgWidth", getImgWidth());
		map.put("imgHeight", getImgHeight());

		map.put("imgSendState", getImgSendState());

		map.put("card_uid", getCard_uid());
		map.put("card_uname", getCard_uname());
		map.put("card_intro", getCard_intro());
		map.put("card_avatar", getCard_avatar());
		map.put("notify_type", getNotify_type());
//		map.put("room_info", getRoom_info());
		map.put("quit_uid", getQuit_uid());
		map.put("quit_uname", getQuit_uname());
		map.put("room_master_uid", getRoom_master_uid());
		map.put("room_add_uid", getRoom_add_uid());
		map.put("room_del_uid", getRoom_del_uid());
		map.put("room_add_uname", getRoom_add_uname());
		map.put("room_del_uname", getRoom_del_uname());
		map.put("quit_uid", getQuit_uid());
		map.put("quit_uname", getQuit_uname());
		map.put("to_uid", getTo_uid());
		map.put("master_uname", getMaster_uname());
		map.put("is_send", isSend==true ? 1 : 0);
		map.put("send_state", sendState);

		return map;
	}

	/**
	 * websocket发送之前转换成JSONstring 反斜杠和双引号不能从map之间转换成JSON，所以创建这个方法
	 *
	 * @return
	 */
	public String toJSONString() {
		Map<String, Object> map = new HashMap<String, Object>();
		if (getType() != null) {
			map.put("type", "send_message");
			map.put("message_type", getType());
		}
		if (getRoom_id() != 0) {
			map.put("room_id", getRoom_id());
		}
		if (getNotify_type() != null)
			map.put("notify_type", getNotify_type());
		if (getQuit_uid() != 0)
			map.put("quit_uid", getQuit_uid());
		if (getQuit_uname() != null)
			map.put("quit_uname", getQuit_uname());
		if (getRoom_master_uid() != 0)
			map.put("room_master_uid", getRoom_master_uid());
		if (getRoom_add_uid() != 0)
			map.put("room_add_uid", getRoom_add_uid());
		if (getRoom_add_uname() != null)
			map.put("room_add_uname", getRoom_add_uname());
		if (getRoom_del_uid() != 0)
			map.put("room_del_uid", getRoom_del_uid());
		if (getRoom_del_uname() != null)
			map.put("room_del_uname", getRoom_del_uname());
		 if (getFrom_uname() != null)
			 map.put("from_uname", getFrom_uname());
		 if (getTo_uid() != 0)
			 map.put("to_uid", getTo_uid() + "");
		if (getMessage_id() != 0) {
			map.put("message_id", getMessage_id());
		}
		if (getPackid() != null) {
			map.put("packid", getPackid());
		}
		try {
			JSONObject json = new JSONObject(map.toString());
			if (getAct() != null) {
				json.accumulate("act", getAct());
			}
			if (getMembers() != null) {
				json.accumulate("members", getMembers());
			}
			if (getContent() != null)
				json.accumulate("content", getContent());
			if (getType() != null) {
				if (getType().equals("image")) {
					json.accumulate("attach_id", getAttach_id());
				} else if (getType().equals("voice")) {
					json.accumulate("attach_id", getAttach_id());
					json.accumulate("length", getLength());
				} else if (getType().equals("position")) {
					json.accumulate("location", getLocation());
					json.accumulate("latitude", getLatitude());
					json.accumulate("longitude", getLongitude());
					json.accumulate("attach_id", getAttach_id());
				} else if (getType().equals("card")) {
					json.accumulate("uid", getCard_uid());
				}
			}
			return json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	// 插入消息列表数据
	public ContentValues insertChatListValues() {
		ContentValues map = new ContentValues();
		map.put("message_id", getMessage_id());
		map.put("from_uid", getFrom_uid());
		map.put("type", getType());
		map.put("room_id", getRoom_id());
		map.put("content", getContent());
		map.put("from_uface", getFrom_uface());
		map.put("from_uname", getFrom_uname());
		map.put("length", getLength());
		map.put("latitude", getLatitude());
		map.put("longitude", getLongitude());
		map.put("location", getLocation());
		map.put("attach_id", getAttach_id());
		map.put("mtime", getMtime());
		map.put("localPath", getLocalPath());

		map.put("imgWidth", getImgWidth());
		map.put("imgHeight", getImgHeight());

		map.put("imgSendState", getImgSendState());

		map.put("attach_url", getAttach_url());
		map.put("card_avatar", getCard_avatar());
		map.put("card_uname", getCard_uname());
		map.put("card_intro", getCard_intro());
		map.put("card_uid", getCard_uid());
		map.put("notify_type", getNotify_type());
		map.put("quit_uid", getQuit_uid());
		map.put("quit_uname", getQuit_uname());
		map.put("room_master_uid", getRoom_master_uid());
		map.put("room_add_uid", getRoom_add_uid());
		map.put("room_del_uid", getRoom_del_uid());
		map.put("room_add_uname", getRoom_add_uname());
		map.put("room_del_uname", getRoom_del_uname());
		map.put("quit_uid", getQuit_uid());
		map.put("quit_uname", getQuit_uname());
		map.put("isNew", getIsNew());
		map.put("to_uid", getTo_uid());
		map.put("master_uname", getMaster_uname());
		map.put("pack_id",getPackid());
		map.put("description", getDescription());
		map.put("is_send", isSend == true ? 1 : 0);
		map.put("send_state", sendState);

		return map;
	}

	/***
	 * 获取和谁聊天 如果fromUid和登录uid相同，则表示由登录用户发出去的信息，那么chatUserName为Touname
	 *
	 * @return
	 */
	public String getChatUserName() {
		return this.chat_user_name;
	}

	public void setChatUserName(String string) {
		this.chat_user_name = string;
	}

	public void setChatUerFace(String url) {
		this.chat_user_face = url;
	}

	/**
	 * 获取聊天对方的uface
	 *
	 * @return
	 */
	public String getChatUserFace() {
		return this.chat_user_face;
	}

	public void setMembers(String members) {
		this.members = members;
	}

	public String getMembers() {
		return members;
	}

	/**
	 * 设置调用方法 createList/addUser/moveUser/changeTitle
	 *
	 * @param act
	 */
	public void setAct(String act) {
		this.act = act;
	}

	/**
	 * 获取调用方法 createList/addUser/moveUser/changeTitle
	 *
	 * @return
	 */
	public String getAct() {
		return act;
	}

	/**
	 * 消息聊天类型 chat--单聊 group--群聊
	 *
	 * @return
	 */
	public String getRoom_type() {
		return room_type;
	}

	/**
	 * 消息聊天类型 chat--单聊 group--群聊
	 *
	 * @return
	 */
	public void setRoom_type(String room_type) {
		this.room_type = room_type;
	}

	@Override
	public String toString() {
		return "ModelChatMessage [chat_user_face=" + chat_user_face
				+ ", chat_user_name=" + chat_user_name + ", uid_loginUser="
				+ uid_loginUser + ", uid_chatUser=" + uid_chatUser
				+ ", to_uid=" + to_uid + ", from_uid=" + from_uid + ", type="
				+ type + ", to_client_id=" + to_client_id + ", min_max="
				+ min_max + ", msgtype=" + msgtype + ", from_uname="
				+ from_uname + ", from_uface=" + from_uface + ", to_uname="
				+ to_uname + ", to_uface=" + to_uface + ", content=" + content
				+ ", room_type=" + room_type + ", time=" + time + ", room_id="
				+ room_id + ", from_client_id=" + from_client_id
				+ ", attach_id=" + attach_id + ", imageUrl=" + imageUrl
				+ ", voiceUrl=" + voiceUrl + ", length=" + length
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", poi_name=" + poi_name + ", poi_image=" + poi_image
				+ ", image_width=" + image_width + ", image_height="
				+ image_height + ", ivAudio=" + ivAudio + ", isNew=" + isNew
				+ ", members=" + members + ", act=" + act + ", room_title="
				+ room_title + "]";
	}

	/**
	 * 消息监听器
	 */
	public interface OnMessageListener {
		public void onSuccess(String result);
		public void onError(String reason);
	}

	private OnMessageListener messageListener;
	public void addOnMessageListener(OnMessageListener listener) {
		this.messageListener = listener;
	}

	public OnMessageListener getOnMessageListener() {
		return messageListener;
	}

}
