package com.thinksns.tschat.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModelUser extends Entity implements Parcelable{
	private static final String TAG = "ModelUser";
	protected String mUserName;
	protected String mPassword;
	protected int mUid;
	protected String userEmail;
	protected String userPhone;
	protected String tel;
	protected String userTag;
	protected String QQ;
	protected String intro;

	protected String mProvince;
	protected String mCity;
	protected String mLocation;
	protected String mFace;
	protected String mSex;
	protected boolean isInBlackList;
	protected boolean isFollowed;
	protected String userJson;
	protected String cover; // 自定义背景图片 url
	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	/**
	 * 解析成JSONObject之前的string信息
	 */
	protected String jsonString;
	protected JSONArray photo, vedio;

	protected int isMyContact;

	protected List<String[]> otherFiled;
	protected String department;

	//是否被选中，用于创建群聊，发送卡片时用
	private boolean isSelect;

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public ModelUser() {
		super();
	}

	/**
	 * 专门用于群聊添加/删除成员的时候作为添加/删除图标用
	 *
	 * @param type
	 *            add id=-1; delete id=-1
	 */
	public ModelUser(String type) {
		if (type.equals("add")) {
			this.setId(-1);
		} else if (type.equals("delete")) {
			this.setId(-2);
		}
	}

	public ModelUser(JSONObject data) throws JSONException {
			// 把内容保存到jsonString内
			this.jsonString = data.toString();
			// 初始化用户信息
			this.initUserInfo(data);
	}

	public void initUserInfo(JSONObject data) throws JSONException {
		if (data != null) {
			try {
				if (data.has("status") && data.getString("status").equals("0")) {
					// 错误用户数据
					String msg = data.getString("msg");
					if (msg.equals("")) {
						msg = "用户读取错误";
					}
					throw new JSONException(msg);
				}
			} catch (JSONException e) {
				Log.d(TAG, "initUserInfo(JSONObject data)-->用户数据错误");
			}
		}
		try {
			if (data.has("uid")) {
				this.setUid(data.getInt("uid"));
			}
			if (data.has("uname")) {
				this.setUserName(data.getString("uname"));
			}
			if (data.has("intro")) {
				this.setIntro(data.getString("intro"));
				
				Log.v("tagMsg", "----tagMsg getIntro--json------"+data.getString("intro"));
			}
			if (data.has("sex")) {
				this.setSex(data.getString("sex"));
			}

			if (data.has("avatar")) {
				this.setFace(data.getString("avatar"));
				try {
					this.setPostUface(data.getJSONObject("avatar").getString("avatar_middle"));
				} catch (Exception e) {
					e.printStackTrace();
					this.setPostUface(data.getString("avatar"));
				}
			}
			if (data.has("cover")) {
				this.setCover(data.getString("cover"));
			}

			if (data.has("location")) {
				this.setLocation(data.getString("location"));
			}
			if (data.has("follow_status")) {
				try {
					JSONObject countInfo = data.getJSONObject("follow_status");
					this.setFollowed(countInfo.getString("following").equals(
							"0") ? false : true);
				} catch (Exception e) {
					Log.d(TAG, (getUserName().equals("") ? "" : getUserName())
							+ "set follow_status err");
				}
			}
			if (data.has("is_in_blacklist")) {
				try {
					this.setIsInBlackList(data.getString("is_in_blacklist")
							.equals("1") ? true : false);
				} catch (Exception e1) {
					Log.d(TAG, (getUserName().equals("") ? "" : getUserName())
							+ "set is_in_blacklist err");
				}
			}

			if (data.has("province")) {
				this.setProvince(data.getString("province"));
			}
			
			if (data.has("city")) {
				this.setCity(data.getString("city"));
			}

			if (data.has("email")) {
				this.setUserEmail(data.getString("email"));
			}

			if (data.has("avatar_middle")) {
				this.setFace(data.getString("avatar_middle"));
			}

			if (data.has("profile") && (!data.getString("profile").equals(""))) {
				JSONObject profile = data.getJSONObject("profile");
				if (profile.has("tel")) {
					this.setTel(profile.getJSONObject("tel").getString("value"));
					profile.remove("tel");
				}
				if (profile.has("mobile")) {
					this.setUserPhone(profile.getJSONObject("mobile")
							.getString("value"));
					profile.remove("mobile");
				}

				if (profile.has("department")) {
					this.setDepartment(profile.getJSONObject("department")
							.getString("value"));
					profile.remove("department");
				}

				if (profile.has("email")) {
					profile.remove("email");
				}

				otherFiled = new ArrayList<String[]>();
				for (Iterator iterator = profile.keys(); iterator.hasNext();) {

					String[] ofiled = new String[2];
					String key = (String) iterator.next();

					JSONObject temp = profile.getJSONObject(key);
					ofiled[0] = temp.getString("name");
					ofiled[1] = temp.getString("value");
					otherFiled.add(ofiled);
				}
				this.setOtherFiled(otherFiled);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	public ModelUser(int uid, String userName, String password) {
		this.setUserName(userName);
		this.setPassword(password);
		this.setUid(uid);
	}


	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public boolean getIsInBlackList() {
		return isInBlackList;
	}

	public void setIsInBlackList(boolean isInBlackList) {
		this.isInBlackList = isInBlackList;
	}

	public int getUid() {
		return mUid;
	}

	public void setUid(int mUid) {
		this.mUid = mUid;
	}

	public String getProvince() {
		return mProvince;
	}

	public void setProvince(String mProvince) {
		this.mProvince = mProvince;
	}

	public String getCity() {
		return mCity;
	}

	public void setCity(String mCity) {
		this.mCity = mCity;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}

	public String getFace() {
		return mFace;
	}

	public void setFace(String mFace) {
		this.mFace = mFace;
	}

	public String getSex() {
		if (mSex == null)
			return "1";
		return mSex;
	}

	public void setSex(String mSex) {
		this.mSex = mSex;
	}


	/**
	 * 是否已经关注该用户
	 * 
	 * @return
	 */
	public boolean isFollowed() {
		return isFollowed;
	}

	public void setFollowed(boolean isFollowed) {
		this.isFollowed = isFollowed;
	}

	public void setUserTag(String userTag) {
		this.userTag = userTag;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getQQ() {
		return QQ;
	}

	public void setQQ(String qQ) {
		QQ = qQ;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getUserface() {
		return this.getFace();
	}
	protected String postUface="";
	

	public String getPostUface() {
		return postUface;
	}

	public void setPostUface(String postUface) {
		this.postUface = postUface;
	}

	public List<String[]> getOtherFiled() {
		return otherFiled;
	}

	public void setOtherFiled(List<String[]> otherFiled) {
		this.otherFiled = otherFiled;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}


	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof ModelUser))
			return false;
		return ((ModelUser)o).getUid() == this.getUid();
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mUid);
		dest.writeString(mUserName);
		dest.writeString(mFace);
	}

	public static final Parcelable.Creator<ModelUser> CREATOR = new Creator<ModelUser>() {
		@Override
		public ModelUser createFromParcel(Parcel source) {
			return new ModelUser(source);
		}

		@Override
		public ModelUser[] newArray(int size) {
			return new ModelUser[size];
		}
	};

	ModelUser(Parcel source) {
		this.mUid = source.readInt();
		this.mUserName = source.readString();
		this.mFace = source.readString();
	}
}
