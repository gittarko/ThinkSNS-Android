package com.thinksns.sociax.t4.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * 类说明：
 * 
 * @author Zoey
 * @date 2015-8-4
 * @version 1.0
 */
public class ModelChatUserList extends SociaxItem {

	int room_id;
	int master_uid;
	int is_group;//数据返回为true和false，用整型代替boolean存入数据库，0代表true，1代表false
	String title;
	int mtime;
	int self_index;
	String content;
	String type;
	/**
	 * 最后一条信息相关信息
	 */
	int from_uid;
	String from_uname;
	String from_uface;
	String from_uface_url;
	int member_num;
	List<ModelMemberList> memList;
	String to_name;
	int to_uid;
	int isNew;
	private String groupFace;

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

	public String getGroupFace() {
		if(groupFace == null || groupFace.isEmpty() || groupFace.equals("null"))
			return null;
		return groupFace;
	}

	public void setGroupFace(String groupFace) {
		this.groupFace = groupFace;
	}

	public ModelChatUserList() {
		
	}
	public ModelChatUserList(JSONObject object) {
	
		try {
			int room_id=object.getInt("room_id");
			int master_uid=object.getInt("master_uid");
			boolean is_group=object.getBoolean("is_group");
			String title=object.getString("title");
			int mtime=object.getInt("mtime");
			int self_index=object.optInt("self_index");
			int member_num=object.getInt("member_num");
			
			String content=object.optJSONObject("last_message").optString("content");
			String type=object.optJSONObject("last_message").optString("type");
			int from_uid=object.optJSONObject("last_message").optInt("from_uid");
			String from_uname=object.optJSONObject("last_message").optString("from_uname");
			String groupFace = "";

			String memberString=object.getString("member_list");
			if (memberString != null&&!memberString.equals("null")) {
				JSONArray memberArray =new JSONArray(memberString);
				memList=new ArrayList<ModelMemberList>();
				String uids = "";
				for (int i = 0; i < memberArray.length(); i++) {
					ModelMemberList member = new ModelMemberList(memberArray.getJSONObject(i));
					memList.add(member);
					uids +=member.getUid();
				}
				//把uid存起来
				SharedPreferences preferences=Thinksns.getContext().getSharedPreferences(StaticInApp.MEMBERS_UIDS, Context.MODE_PRIVATE);
				Editor editor=preferences.edit();
				editor.putString("uids", uids);  
			    editor.commit();  
			}
			
			this.setRoom_id(room_id);
			this.setMaster_uid(master_uid);
			if (is_group==true) {
				this.setIs_group(0);
			}else if(is_group==false){
				this.setIs_group(1);
			}
			this.setTitle(title);
			this.setMtime(mtime);
			this.setSelf_index(self_index);
			this.setMember_num(member_num);
			this.setContent(content);
			this.setType(type);
			this.setFrom_uid(from_uid);
			this.setFrom_uname(from_uname);
			this.setMemList(memList);
			this.setGroupFace(groupFace);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ContentValues toContentValues() {

		ContentValues map = new ContentValues();

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
		map.put("from_uface", getFrom_uface());
		map.put("from_uface_url", getFrom_uface_url());
		map.put("to_name", getTo_name());
		map.put("to_uid", getTo_uid());
		map.put("member_num", getMember_num());
		map.put("isNew", getIsNew());
		map.put("group_face", getGroupFace());
		return map;
	}
	
	// 插入房间列表数据
		public ContentValues insertRoomListValues() {

			ContentValues map = new ContentValues();

			map.put("room_id", getRoom_id());
			map.put("mtime", getMtime());
			map.put("content", getContent());
			map.put("isNew", getIsNew());
			map.put("title", getTitle());

			return map;
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
