package com.thinksns.sociax.t4.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;


/**
 * 类说明： 微吧
 * @author wz
 * @date 2014-12-20
 * @version 1.0
 */
public class ModelWeiba extends SociaxItem implements Parcelable{

	private String logo;	// 微吧的logo
	private int weiba_id,	// 微吧id
			uid,			// 用户id
			admin_uid,		// 管理员id
			cid;
	private String weiba_name,	// 微吧名字
			ctime, intro, who_can_post, who_can_reply,
			follower_count,
			thread_count, recommend, status, is_del, notify,
			avatar_big,
			avatar_middle, new_count, new_day, info, province, city,
			area,
			input_city;
	private String weibaJson;
	private JSONObject jsonObject;

	private boolean isFollow = false;	//是否已经关注贴吧，默认否
	public boolean isFollow() {
		return isFollow;
	}

	public void setFollow(boolean isFollow) {
		this.isFollow = isFollow;
	}


	public ModelWeiba(JSONObject data) {
		try {
			this.jsonObject = data;
			this.weibaJson = data.toString();
			if (data.has("weiba_id")) {
				this.setWeiba_id(data.getInt("weiba_id"));
			}
			if (data.has("logo")) {
				this.setLogo(data.getString("logo"));
			}
			if (data.has("uid")) {
				this.setUid(data.getInt("uid"));
			}
			if (data.has("admin_uid")) {
				this.setAdmin_uid(data.getInt("admin_uid"));
			}
			if (data.has("cid")) {
				this.setCid(data.getInt("cid"));
			}
			if (data.has("weiba_name")) {
				this.setWeiba_name(data.getString("weiba_name"));
			}
			if (data.has("ctime")) {
				this.setCtime(data.getString("ctime"));
			}
			if (data.has("intro")) {
				this.setIntro(data.getString("intro"));
			}
			if (data.has("who_can_post")) {
				this.setWho_can_post(data.getString("who_can_post"));
			}
			if (data.has("who_can_reply")) {
				this.setWho_can_reply(data.getString("who_can_reply"));
			}
			//关注数
			if (data.has("follower_count")) {
				this.setFollower_count(data.getString("follower_count"));
			}
			//帖子数
			if (data.has("thread_count")) {
				this.setThread_count(data.getString("thread_count"));
			}
			if (data.has("recommend")) {
				this.setRecommend(data.getString("recommend"));
			}
			if (data.has("status")) {
				this.setStatus(data.getString("status"));
			}
			if (data.has("is_del")) {
				this.setIs_del(data.getString("is_del"));
			}
			if (data.has("notify")) {
				this.setNotify(data.getString("notify"));
			}
			if (data.has("avatar_big")) {
				this.setAvatar_big(data.getString("avatar_big"));
			}
			if (data.has("avatar_middle")) {
				this.setAvatar_middle(data.getString("avatar_middle"));
			}
			if (data.has("new_count")) {
				this.setNew_count(data.getString("new_count"));
			}
			if (data.has("new_day")) {
				this.setNew_day(data.getString("new_day"));
			}
			if (data.has("info")) {
				this.setInfo(data.getString("info"));
			}
			if (data.has("province")) {
				this.setProvince(data.getString("province"));
			}
			if (data.has("city")) {
				this.setCity(data.getString("city"));
			}
			if (data.has("area")) {
				this.setArea(data.getString("area"));
			}
			if (data.has("input_city")) {
				this.setInput_city(data.getString("input_city"));
			}
			if(data.has("following")){
				this.setFollow(data.getString("following").equals("1"));
			} else if (data.has("follow")) {
				this.setFollow(data.getString("follow").equals("1"));
			} else {
				this.setFollow(true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ModelWeiba() {
	}

	/**
	 * 空微吧，仅仅显示一个part
	 * 
	 * @param str_firstpartname
	 */
	public ModelWeiba(String str_firstpartname) {
		this.setFirstInPart(true);
		this.setStr_partName(str_firstpartname);
	}

	public int getWeiba_id() {
		return weiba_id;
	}

	public void setWeiba_id(int weiba_id) {
		this.weiba_id = weiba_id;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getAdmin_uid() {
		return admin_uid;
	}

	public void setAdmin_uid(int admin_uid) {
		this.admin_uid = admin_uid;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getWeiba_name() {
		return weiba_name;
	}

	public void setWeiba_name(String weiba_name) {
		this.weiba_name = weiba_name;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getWho_can_post() {
		return who_can_post;
	}

	public void setWho_can_post(String who_can_post) {
		this.who_can_post = who_can_post;
	}

	public String getWho_can_reply() {
		return who_can_reply;
	}

	public void setWho_can_reply(String who_can_reply) {
		this.who_can_reply = who_can_reply;
	}

	public String getFollower_count() {
		return follower_count;
	}

	public void setFollower_count(String follower_count) {
		this.follower_count = follower_count;
	}

	public String getThread_count() {
		return thread_count;
	}

	public void setThread_count(String thread_count) {
		this.thread_count = thread_count;
	}

	public String getRecommend() {
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIs_del() {
		return is_del;
	}

	public void setIs_del(String is_del) {
		this.is_del = is_del;
	}

	public String getNotify() {
		return notify;
	}

	public void setNotify(String notify) {
		this.notify = notify;
	}

	public String getAvatar_big() {
		return avatar_big;
	}

	public void setAvatar_big(String avatar_big) {
		this.avatar_big = avatar_big;
	}

	public String getAvatar_middle() {
		return avatar_middle;
	}

	public void setAvatar_middle(String avatar_middle) {
		this.avatar_middle = avatar_middle;
	}

	public String getNew_count() {
		return new_count;
	}

	public void setNew_count(String new_count) {
		this.new_count = new_count;
	}

	public String getNew_day() {
		return new_day;
	}

	public void setNew_day(String new_day) {
		this.new_day = new_day;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getInput_city() {
		return input_city;
	}

	public void setInput_city(String input_city) {
		this.input_city = input_city;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return null;
	}

	private boolean isFirstInPart = false;// 用于标记列表分栏，每一个分栏的第一个iem

	public boolean isFirstInPart() {
		return isFirstInPart;
	}

	public void setFirstInPart(boolean isFirstInPart) {
		this.isFirstInPart = isFirstInPart;
	}

	public String getStr_partName() {
		return str_partName;
	}

	public void setStr_partName(String str_partName) {
		this.str_partName = str_partName;
	}

	private String str_partName = "";// 用与标记列表分栏，分栏的名称

	public String getWeibaJson() {
		return weibaJson;
	}

	public void setWeibaJson(String weibaJson) {
		this.weibaJson = weibaJson;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(weiba_id);
		dest.writeString(weiba_name);
		dest.writeString(avatar_middle);
		dest.writeInt(isFollow ? 1 : 0);
		dest.writeString(getFollower_count());
		dest.writeString(getThread_count());
	}

	public static final Parcelable.Creator<ModelWeiba> CREATOR = new Creator<ModelWeiba>() {
		@Override
		public ModelWeiba createFromParcel(Parcel source) {
			return new ModelWeiba(source);
		}

		@Override
		public ModelWeiba[] newArray(int size) {
			return new ModelWeiba[size];
		}
	};

	public ModelWeiba(Parcel source) {
		this.weiba_id = source.readInt();
		this.weiba_name = source.readString();
		this.avatar_middle = source.readString();
		this.isFollow = source.readInt() == 1 ? true :false;
		this.follower_count = source.readString();
		this.thread_count = source.readString();
	}

}
