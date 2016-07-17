package com.thinksns.sociax.t4.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.thinksns.sociax.modle.UserApprove;
import com.thinksns.sociax.t4.android.function.FunctionPingYing;
import com.thinksns.sociax.t4.android.widget.ContactItemInterface;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 搜索出来的人 本类中的变量因各种类型搜索结果不同而不同，不要随便删减本类中变量，可以添加
 * 
 * @author wz
 * @date 2014-10-30
 * @version 1.0
 */
public class ModelSearchUser extends SociaxItem implements Serializable,ContactItemInterface {
	int follow_id,// 分页请求的id
			uid;
	int id;
	String sortLetters; // 显示数据拼音的首字母
	
	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	String uname, intro, uface, following,// 是否我的关注 ，1是 0否
			follower;// 是否我的粉丝 1是，0否

	String phone;// 电话
	
	UserApprove userApprove;

	boolean isSelect = false;// 是否被选择

	public UserApprove getUserApprove() {
		return userApprove;
	}
	
	public void setUserApprove(UserApprove userApprove) {
		this.userApprove = userApprove;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	String distinct;// 距离

	public String getDistinct() {
		return distinct;
	}

	public void setDistinct(String distinct) {
		this.distinct = distinct;
	}

	public ModelSearchUser(JSONObject data) {
		try {
			//用户距离
			if(data.has("distance")) {
				this.setDistinct(data.getString("distance"));
			}
			if (data.has("avatar"))
				this.setUface(data.getString("avatar"));
			if (data.has("follow_id"))
				this.setFollow_id(data.getInt("follow_id"));
			//用户Id
			if (data.has("uid"))
				this.setUid(data.getInt("uid"));
			if (data.has("user_group")) {
				// 用户认证信息
				try {
					this.setUserApprove(new UserApprove(data));
				} catch (Exception e) {
					
				}
			}
			//个人简介
			if (data.has("intro"))
				this.setIntro(data.getString("intro"));
			//用户名称
			if (data.has("uname"))
				this.setUname(data.getString("uname"));
			if(data.has("username"))
				this.setUname(data.getString("username"));
			//用户关注状态
			if (data.has("follow_status")) {
				this.setFollowing(data.getJSONObject("follow_status")
						.getString("following"));
				this.setFollower(data.getJSONObject("follow_status").getString(
						"follower"));
			}
			if (data.has("followStatus")) {
				this.setFollowing(data.getJSONObject("followStatus")
						.getString("following"));
				this.setFollower(data.getJSONObject("followStatus").getString(
						"follower"));
			}
			
			if (data.has("tel"))
				this.setPhone(data.getString("tel"));
			if (data.has("id"))
				this.setId(data.getInt("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ModelSearchUser() {

	}

	/**
	 * 专门用于群聊添加/删除成员的时候作为添加/删除图标用
	 * 
	 * @param type
	 *            add id=-1; delete id=-1
	 */
	public ModelSearchUser(String type) {
		if (type.equals("add")) {
			this.setId(-1);
		} else if (type.equals("delete")) {
			this.setId(-2);
		}
	}

	public int getFollow_id() {
		return follow_id;
	}

	public void setFollow_id(int follow_id) {
		this.follow_id = follow_id;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getUface() {
		return uface;
	}

	public void setUface(String uface) {
		this.uface = uface;
	}

	public String getFollowing() {
		return following;
	}

	public void setFollowing(String following) {
		this.following = following;
	}

	public String getFollower() {
		return follower;
	}

	public void setFollower(String follower) {
		this.follower = follower;
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return getUface();
	}

	@Override
	public String getItemForIndex() {
		//将汉字转换为拼音
		String pinyin = FunctionPingYing.getPingYingString(uname.replaceAll("　", ""));
		Log.e("ModelSearchUser", uname + " convert to pinyin:" + pinyin);
		return pinyin;
	}

	@Override
	public String getFirstLetter() {
		String pinyin = getItemForIndex();
		if(pinyin.length() == 0) {
			return "#";
		}
		return String.valueOf(pinyin.charAt(0)).toUpperCase();
	}

	@Override
	public String getDisplayInfo() {
		return uname;
	}

	@Override
	public String toString() {
		return uname;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		return this.getUid() == ((ModelSearchUser)o).getUid();
	}

	@Override
	public int compareTo(SociaxItem another) {
		return 0;
	}
}
