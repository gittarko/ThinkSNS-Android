package com.thinksns.tschat.bean;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.thinksns.tschat.chat.TSChatManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 类说明 消息列表Modle
 * @date 2015-8-4
 * @version 1.0
 */
public class ModelChatUserList extends Entity implements Parcelable {

	private int room_id;
	private int master_uid;
	private int is_group = 0;	//数据返回为true和false，用整型代替boolean存入数据库，0代表true，1代表false
	private String title;
	private int mtime;
	int self_index;
	private String content;
	private String type;
	//最后一条消息内容
	private int from_uid;
	private String from_uname;
	private String from_uface;
	private String from_uface_url;
	private int lastMsgId;		//最后一条消息ID
	private ModelChatMessage lastMessage;		//最后一条消息
	private int member_num;
	private List<ModelMemberList> memList;
	private String to_name;
	private int to_uid;
	private int isNew;
	private String groupFace;	//群头像
	private int logoId;			//群头像id
	private String groupId;		//创建群房间时加入成员的id

	public ModelChatMessage getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(ModelChatMessage lastMessage) {
		this.lastMessage = lastMessage;
	}

	public int getLastMsgId() {
		return lastMsgId;
	}

	public void setLastMsgId(int lastMsgId) {
		this.lastMsgId = lastMsgId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<ModelMemberList> getMemList() {
		return memList;
	}

	public void setMemList(List<ModelMemberList> memList) {
		this.memList = memList;
	}

	public int getRoom_id() {
		return room_id;
	}

	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}

	public int getMaster_uid() {
		return master_uid;
	}

	public void setMaster_uid(int master_uid) {
		this.master_uid = master_uid;
	}
	public int getIs_group() {
		return is_group;
	}
	public void setIs_group(int is_group) {
		this.is_group = is_group;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getMtime() {
		return mtime;
	}

	public void setMtime(int mtime) {
		this.mtime = mtime;
	}

	public int getSelf_index() {
		return self_index;
	}

	public void setSelf_index(int self_index) {
		this.self_index = self_index;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getFrom_uface() {
		return from_uface;
	}

	public void setFrom_uface(String from_uface) {
		this.from_uface = from_uface;
	}

	public String getFrom_uface_url() {
		return from_uface_url;
	}

	public void setFrom_uface_url(String from_uface_url) {
		this.from_uface_url = from_uface_url;
	}

	public int getMember_num() {
		return member_num;
	}

	public void setMember_num(int member_num) {
		this.member_num = member_num;
	}
	
	public String getTo_name() {
		return to_name;
	}

	public void setTo_name(String to_name) {
		this.to_name = to_name;
	}
	
	public int getTo_uid() {
		return to_uid;
	}

	public void setTo_uid(int to_uid) {
		this.to_uid = to_uid;
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public int getLogoId() {
		return logoId;
	}

	public void setLogoId(int logoId) {
		this.logoId = logoId;
	}

	public String getGroupFace() {
		if(groupFace == null || groupFace.equals("null") || groupFace.isEmpty())
			return null;
		return groupFace;
	}

	public void setGroupFace(String groupFace) {
		this.groupFace = groupFace;
	}

	public ModelChatUserList() {
		
	}
	public ModelChatUserList(JSONObject object) {
		setData(object);
	}

	public void setData(JSONObject object) {
		try {
			int room_id = object.getInt("room_id");
			int master_uid = object.getInt("master_uid");
			int logo = object.getInt("logo");
			boolean is_group = object.getBoolean("is_group");
			String title = object.getString("title");		//群名称
			int mtime = object.getInt("mtime");				//房间内最后一条消息的时间
			int self_index = object.optInt("self_index");
			int member_num = object.getInt("member_num");

			lastMessage = new ModelChatMessage();
			//最后一条消息
			String tmpStr = object.get("last_message").toString();
			String content = "";
			if(!tmpStr.equals("[]")) {
				JSONObject lastMsg = object.optJSONObject("last_message");
				if (lastMsg.has("content"))
					content = lastMsg.optString("content");
				int lastId = lastMsg.getInt("message_id");
				String type = lastMsg.optString("type");        //消息类型
				int from_uid = lastMsg.optInt("from_uid");
				String from_uname = lastMsg.optString("from_uname");

				lastMessage.setMessage_id(lastId);
				lastMessage.setFrom_uid(from_uid);
				lastMessage.setFrom_uname(from_uname);
				lastMessage.setContent(content);
				lastMessage.setType(type);

				this.lastMsgId = lastId;

			}

			lastMessage.setSendState(ModelChatMessage.SEND_STATE.SEND_OK);
			lastMessage.setMtime(mtime);

			JSONArray memberArray = object.getJSONArray("member_list");
			memList = new ArrayList<ModelMemberList>();
			for (int i = 0; i < memberArray.length(); i++) {
				ModelMemberList member = new ModelMemberList(memberArray.getJSONObject(i));
				memList.add(member);
			}
			this.setRoom_id(room_id);
			//如果是群组，设置群主ID
			this.setMaster_uid(master_uid);
			if (is_group) {
				this.setIs_group(0);
			}else {
				this.setIs_group(1);
			}
			//设置群标题
			this.setTitle(title);
			this.setMtime(mtime);
			this.setSelf_index(self_index);
			this.setMember_num(member_num);
			this.setType(type);
			//设置最后一条消息内容
			this.setContent(content);
			this.setFrom_uid(from_uid);
			this.setFrom_uname(from_uname);

			this.setMemList(memList);
			//设置群头像
			this.setLogoId(logo);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ContentValues toContentValues() {
		ContentValues map = new ContentValues();
		map.put("login_uid", TSChatManager.getLoginUser().getUid());
		map.put("room_id", getRoom_id());
		map.put("master_uid", getMaster_uid());
		map.put("is_group", getIs_group());
		map.put("title", getTitle());
		map.put("mtime", getMtime());
		map.put("self_index", getSelf_index());
		map.put("content", getContent());
		map.put("type", getType());
		map.put("from_uid", getFrom_uid());
		map.put("from_uname", getFrom_uname());
//		map.put("from_uface", getFrom_uface());
//		map.put("from_uface_url", getFrom_uface_url());
//		map.put("to_name", getTo_name());
		map.put("member_num", getMember_num());
		
		return map;
	}

	// 插入房间列表数据
	public ContentValues insertChatListValues() {

		ContentValues map = new ContentValues();
		map.put("login_uid", TSChatManager.getLoginUser().getUid());
		map.put("room_id", getRoom_id());
		map.put("master_uid", getMaster_uid());
		map.put("is_group", getIs_group());
		if(getTitle() != null)
			map.put("title", getTitle());
		map.put("mtime", getMtime());
		map.put("self_index", getSelf_index());
		map.put("content", getContent());
		map.put("type", getType());
		map.put("from_uid", getFrom_uid());
		map.put("from_uname", getFrom_uname());
		map.put("from_uface", getFrom_uface());		//本地头像地址
		map.put("from_uface_url", getFrom_uface_url());
		map.put("to_name", getTo_name());
		map.put("to_uid", getTo_uid());
		map.put("member_num", getMember_num());
		map.put("isNew", getIsNew());
		map.put("group_face", getGroupFace());
		map.put("logo_id", getLogoId());

		return map;
	}
	
	// 插入房间列表数据
	public ContentValues insertRoomListValues() {
			ContentValues map = new ContentValues();
			map.put("login_uid", TSChatManager.getLoginUser().getUid());
			map.put("room_id", getRoom_id());
			map.put("mtime", getMtime());
			map.put("content", getContent());
			map.put("isNew", getIsNew());
			map.put("title", getTitle());
			return map;
		}

	//是否单聊
	public boolean isSingle() {
		return is_group == 1 ? true : false;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(room_id);
		parcel.writeInt(mtime);
		parcel.writeString(title);
		parcel.writeString(content);
		parcel.writeInt(is_group);
		parcel.writeInt(to_uid);
		parcel.writeString(to_name);
		parcel.writeString(from_uface_url);
		parcel.writeString(groupFace);
		parcel.writeInt(logoId);
	}

	public static final Parcelable.Creator<ModelChatUserList> CREATOR = new Parcelable.Creator<ModelChatUserList>() {

		@Override
		public ModelChatUserList createFromParcel(Parcel parcel) {
			return new ModelChatUserList(parcel);
		}

		@Override
		public ModelChatUserList[] newArray(int i) {
			return new ModelChatUserList[i];
		}
	};

	ModelChatUserList(Parcel parcel) {
		room_id = parcel.readInt();
		mtime = parcel.readInt();
		title = parcel.readString();
		content = parcel.readString();
		is_group = parcel.readInt();
		to_uid = parcel.readInt();
		to_name = parcel.readString();
		from_uface_url = parcel.readString();
		groupFace = parcel.readString();
		logoId = parcel.readInt();

	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof ModelChatUserList)) {
			return false;
		}
		return ((ModelChatUserList)o).getRoom_id() == this.getRoom_id();
	}
}
