package com.thinksns.sociax.t4.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.modle.UserApprove;
import com.thinksns.sociax.net.Request;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.UserDataInvalidException;

public class ModelUser extends SociaxItem implements Serializable {
    private static final String TAG = "ModelUser";
    protected String mUserName;
    protected String mPassword;
    protected String mToken;
    protected String mSecretToken;
    protected int mUid;

    protected String dipartment;
    protected String userEmail;
    protected String userPhone;
    protected String tel;
    protected String userTag;
    protected String QQ;
    protected String intro;
    protected String verifyInfo;

    protected String mProvince;
    protected String mCity;
    protected String mLocation;
    protected String mFace;
    protected String mSex;
    protected boolean isInBlackList;
    protected int mWeiboCount;
    protected int mFollowersCount;
    protected int mFollowedCount;
    protected boolean isFollowed;
    protected boolean isVerified;
    protected ModelWeibo lastWeibo;
    protected String userJson;

    protected String photoCount;// 相片数
    protected String vdeioCount;// 视频数

    protected String gift_count,// 礼物数目
            authenticate;// 认证信息

    protected UserApprove userApprove; // caoligai 添加，将旧版的 ModelApprove 改为 UserApprove

    protected String certInfo;    // 认证信息

    protected String cover; // 自定义背景图片 url

    protected String is_admin;//是否是管理员 1：管理员 0：非管理员

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCertInfo() {
        return certInfo;
    }

    public void setCertInfo(String certInfo) {
        this.certInfo = certInfo;
    }

    public UserApprove getUserApprove() {
        return userApprove;
    }

    public void setUserApprove(UserApprove userApprove) {
        this.userApprove = userApprove;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    /********************************** wz ********************************/
    /**
     * 礼物列表，是一个jsonArray的String字符串
     */
    protected String gift_list;
    /**
     * 勋章列表，是一个jsonArray 的String字符串
     */
    protected String medals;
    /**
     * 解析成JSONObject之前的string信息
     */
    protected String jsonString;
    /**
     * 粉丝代表
     */
    protected SerializableJSONArray follower_t4;
    /**
     * 关注代表
     */
    protected SerializableJSONArray following_t4;
    /**
     * 用户经验详情
     */
    protected ModelUserCredit userCredit;
    /**
     * 用户等级详情
     */
    protected ModelUserLevel userLevel;

    public String getGift_count() {
        return gift_count;
    }

    public void setGift_count(String gift_count) {
        this.gift_count = gift_count;
    }

    public String getAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(String authenticate) {
        this.authenticate = authenticate;
    }

    /**
     * 获取勋章列表
     *
     * @return 勋章列表jsonArray 的string字符串
     */
    public String getMedals() {
        return medals;
    }

    public void setMedals(String medals) {
        this.medals = medals;
    }

    /**
     * 获取礼物列表
     *
     * @return 礼物列表jsonArray的string字符串
     */
    public String getGift_list() {
        return gift_list;
    }

    public void setGift_list(String gift_list) {
        this.gift_list = gift_list;
    }

    protected ListData<ModleUserGroup> userGroup;// 用户标签组

    public ListData<ModleUserGroup> getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(ListData<ModleUserGroup> userGroup) {
        this.userGroup = userGroup;
    }

    /**
     * 粉丝列表
     *
     * @return
     */
    public JSONArray getFollower_t4() {
        return follower_t4.getJSONArray();
    }

    public void setFollower_t4(JSONArray follower) {
        this.follower_t4 = new SerializableJSONArray(follower);
    }

    /**
     * 关注列表
     *
     * @return
     */
    public JSONArray getFollowing_t4() {
        return following_t4.getJSONArray();
    }

    public void setFollowing_t4(JSONArray following) {
        this.following_t4 = new SerializableJSONArray(following);
    }

    protected SerializableJSONArray photo, vedio;

    protected int isMyContact;

    protected List<String[]> otherFiled;
    protected String department;

    protected UserApprove usApprove;

    public String getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(String photoCount) {
        this.photoCount = photoCount;
    }

    public String getVdeioCount() {
        return vdeioCount;
    }

    public void setVdeioCount(String vdeioCount) {
        this.vdeioCount = vdeioCount;
    }

    public JSONArray getPhoto() {
        return photo.getJSONArray();
    }

    public void setPhoto(JSONArray photo) {
        this.photo = new SerializableJSONArray(photo);
    }

    public JSONArray getVedio() {
        return vedio.getJSONArray();
    }

    public void setVedio(JSONArray vedio) {
        this.vedio = new SerializableJSONArray(vedio);
    }

    public String getUserJson() {
        return userJson;
    }

    public void setUserJson(String userJson) {
        this.userJson = userJson;
    }

    public ModelUser() {
        super();
    }

    public ModelUser(JSONObject data) throws DataInvalidException {
        super(data);
        try {
            // 把内容保存到jsonString内
            this.jsonString = data.toString();
            // 初始化用户信息
            this.initUserInfo(data);
            if (data.has("status") && !data.getString("status").equals("")) {
                this.setLastWeibo(new ModelWeibo(data.getJSONObject("status")));
            }
            if (data.has("weibo") && !data.getString("status").equals("")) {
                this.setLastWeibo(new ModelWeibo(data.getJSONObject("weibo")));
            }
            if (data.has("mini") && !data.getString("mini").equals("null")) {
                this.setLastWeibo(new ModelWeibo(data.getJSONObject("mini")));
            }
            // 是否收藏为联系人
            if (data.has("isMyContact")) {
                this.setIsMyContact(data.getInt("isMyContact"));
            }
        } catch (JSONException e) {
            throw new UserDataInvalidException(data.toString());
        }
    }

    public void initUserInfo(JSONObject data) throws DataInvalidException {
        if (data != null) {
            try {
                if (data.has("status") && data.getString("status").equals("0")) {
                    // 错误用户数据
                    String msg = data.getString("msg");
                    if (msg.equals("")) {
                        msg = "用户读取错误";
                    }
                    throw new DataInvalidException(msg);
                }
            } catch (JSONException e) {
                Log.d(TAG, "initUserInfo(JSONObject data)-->用户数据错误");
            }
        }
        try {
            //用户权限
            if (data.has("is_admin")) {
                this.setIs_admin(data.getString("is_admin"));
            }
            //用户ID
            if (data.has("uid")) {
                this.setUid(data.getInt("uid"));
            }
            //用户名
            if (data.has("uname")) {
                this.setUserName(data.getString("uname"));
            }
            if (data.has("intro")) {
                this.setIntro(data.getString("intro"));
            }
            if (data.has("sex")) {
                this.setSex(data.getString("sex"));
            }
            if (data.has("weibo_count")) {
                this.setWeiboCount(data.getInt("weibo_count"));
            }
            if (data.has("follower_count")) {
                this.setFollowedCount(data.getInt("follower_count"));
            }
            if (data.has("following_count")) {
                this.setFollowersCount(data.getInt("following_count"));
            }
            if (data.has("avatar")) {
                this.setFace(data.getString("avatar"));
                try {
                    //判断是否是jsonobject
                    if (data.getString("avatar").startsWith("{"))
                        this.setPostUface(data.getJSONObject("avatar").getString("avatar_middle"));
                    else {
                        this.setPostUface(data.getString("avatar"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //用户封面
            if (data.has("cover")) {
                this.setCover(data.getString("cover"));
            }
            if (data.has("photo_count")) {
                this.setPhotoCount(data.getString("photo_count"));
            }
            if (data.has("photo")) {
                this.setPhoto(data.getJSONArray("photo"));
            }
            if (data.has("video")) {
                this.setVedio(data.getJSONArray("video"));
            }
            if (data.has("video_count")) {
                this.setVdeioCount(data.getString("video_count"));
            }
            if (data.has("follower")) {
                this.setFollower_t4(data.getJSONArray("follower"));
            }
            if (data.has("following")) {
                this.setFollowing_t4(data.getJSONArray("following"));
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
            if (data.has("user_group")
                    && !data.getString("user_group").equals("")
                    && !data.getString("user_group").equals(null)) {
                // caoligai 修改，之前的版本用 ModelApprove 作为 MedolUser 的用户组字段，在下面解析
                // json 的时候会报异常（但是不强退，因为异常处理只是打印了一个错误信息），导致调用 show() 方法返回的
                // ModelUser 的 Approve 字段为空,现在改为用 ModelApprove 解析 "user_group" 字段

                // try {
                // Log.d(TAG, "该 json 数据为 : " + data.toString());
                // JSONArray user_group = data.getJSONArray("user_group");
                // if (user_group.length() > 0) {
                // ListData<ModleUserGroup> list = new
                // ListData<ModleUserGroup>();
                // for (int i = 0; i < user_group.length(); i++) {
                // ModleUserGroup md = new
                // ModleUserGroup(user_group.getJSONObject(i));
                // list.add(md);
                // }
                // this.setUserGroup(list);
                // }
                // } catch (Exception e) {
                // Log.d(TAG, (getUserName().equals("") ? "" : getUserName())+
                // "set user_group err");
                // Log.e("jsonError", "---------e-----------"+e.getMessage());
                // }

                // 用户认证信息
                if (data.has("certInfo")) {
                    this.setCertInfo(data.getString("certInfo"));
                }
                try {
                    this.setUserApprove(new UserApprove(data));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "解析用户组 json 数据出现错误");
                }

            }
            if (data.has("authenticate")) {
                this.setAuthenticate(data.getString("authenticate"));
            }
            if (data.has("medals")) {
                this.setMedals(data.getString("medals"));
            }
            if (data.has("gift_count")) {
                this.setGift_count(data.getString("gift_count"));
            }
            if (data.has("gift_list")) {
                this.setGift_list(data.getString("gift_list"));
            }
            if (data.has("province")) {
                this.setProvince(data.getString("province"));
            }
            if (data.has("user_credit")) {
                try {
                    this.setUserCredit(new ModelUserCredit(data.getJSONObject("user_credit").getJSONObject("credit")));
                } catch (Exception e) {
                    Log.d(TAG, (getUserName().equals("") ? "" : getUserName()) + "set credit err");
                }
                try {
                    this.setUserLevel(new ModelUserLevel(data.getJSONObject("user_credit").getJSONObject("level")));
                } catch (Exception e) {
                    Log.d(TAG, (getUserName().equals("") ? "" : getUserName())
                            + "set level err");
                }
            }

            if (data.has("city")) {
                this.setCity(data.getString("city"));
            }

            if (data.has("email")) {
                this.setUserEmail(data.getString("email"));
            }

            if (data.has("verify_info")) {
                this.setVerifyInfo(data.getString("verify_info"));
            }

            if (data.has("department")) {
                if (!data.getString("department").equals("")) {
                    setDepartment(data.getJSONObject("department").getString(
                            "title"));
                }
            }
            if (data.has("tags")) {
//				{
//					this.setUserTag(data.getString("user_tag"));
//				}

                //key不确定的情况下，先遍历找到相应的key，再用key去取value
                JSONObject json = data.optJSONObject("tags");
                if (json != null) {
                    @SuppressWarnings("unchecked")
                    Iterator<String> iterator = json.keys();
                    StringBuffer buffer = new StringBuffer();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String value = json.optString(key);
                        buffer.append(value + "、");
                    }
                    if (buffer != null && buffer.length() > 0) {
                        this.setUserTag((buffer.toString()).substring(0, buffer.length() - 1));
                    }
                }
            }
            if (data.has("avatar_middle")) {
                {
                    this.setFace(data.getString("avatar_middle"));
                }
            }
            if (data.has("count_info")) {
                JSONObject countInfo = data.getJSONObject("count_info");
                if (countInfo.has("weibo_count")
                        && !countInfo.isNull("weibo_count")) {
                    this.setWeiboCount(countInfo.getInt("weibo_count"));
                }
                if (countInfo.has("follower_count")) {
                    this.setFollowersCount(countInfo
                            .getString("follower_count").equals("false") ? 0
                            : countInfo.getInt("follower_count"));
                }
                if (countInfo.has("following_count")) {
                    this.setFollowedCount(countInfo
                            .getString("following_count").equals("false") ? 0
                            : countInfo.getInt("following_count"));
                }
            }
            if (data.has("user_data")) {
                this.setFollowersCount(data.getJSONObject("user_data").getInt(
                        "follower_count"));
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
                for (Iterator iterator = profile.keys(); iterator.hasNext(); ) {

                    String[] ofiled = new String[2];
                    String key = (String) iterator.next();

                    JSONObject temp = profile.getJSONObject(key);
                    ofiled[0] = temp.getString("name");
                    ofiled[1] = temp.getString("value");
                    otherFiled.add(ofiled);
                }
                this.setOtherFiled(otherFiled);
            }
            // T3用户认证信息
            // if (data.has("user_group")) {
            // setUsApprove(new UserApprove(data));
            // }

        } catch (JSONException e) {
            Log.d(AppConstant.APP_TAG, "User-->解析出错JSONException ");
        }
    }

    public ModelUser(int uid, String userName, String password, String token,
                     String secretToken) {
        this.setUserName(userName);
        this.setPassword(password);
        this.setToken(token);
        this.setSecretToken(secretToken);
        this.setUid(uid);
    }

    public ModelUser(int uid, String userName, String password) {
        this.setUserName(userName);
        this.setPassword(password);
        this.setUid(uid);
    }

    public boolean isNullForUid() {
        return this.getUid() == 0;
    }

    /**
     * 获取解析成JSON之前的String字符串信息
     *
     * @return
     */
    public String toJSON() {
        return this.jsonString;
    }

    public boolean isNullForUserName() {
        String temp = this.getUserName();
        return temp == null || temp.equals(NULL);
    }

    public boolean isNullForProvince() {
        String temp = this.getProvince();
        return temp == null || temp.equals(NULL);
    }

    public boolean isNullForCity() {
        String temp = this.getCity();
        return temp == null || temp.equals(NULL);
    }

    public boolean isNullForLocation() {
        String temp = this.getLocation();
        return temp == null || temp.equals(NULL);
    }

    public boolean isNullForFace() {
        String face = this.getFace();
        return face == null || face.equals(NULL);
    }

    public boolean isNullForSex() {
        String temp = this.getSex();
        return temp == null || temp.equals(NULL);
    }

    public boolean isNullForWeiboCount() {
        return this.getWeiboCount() == 0;
    }

    public boolean isNullForFollowersCount() {
        return this.getFollowersCount() == 0;
    }

    public boolean isNullForFollowedCount() {
        return this.getFollowedCount() == 0;
    }

    public boolean isNullForLastWeibo() {
        return this.getLastWeibo() == null || !this.getLastWeibo().checkValid();
    }

    public boolean isNullForToken() {
        String temp = this.getToken();
        return temp == null || temp.equals(NULL);
    }

    public boolean isNullForSecretToken() {
        String temp = this.getSecretToken();
        return temp == null || temp.equals(NULL);
    }

    public boolean hasVerifiedInAndroid() {
        return !(this.isNullForToken() || this.isNullForSecretToken() || this
                .isNullForUid());
    }

    @Override
    public boolean checkValid() {
        boolean result = true;
        result = result
                && !(this.isNullForUid() || this.isNullForUserName() || this
                .isNullForSex());
        return result;
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

    public String getToken() {
        return mToken;
    }

    public void setToken(String mToken) {
        Request.setToken(mToken);
        this.mToken = mToken;
    }

    public boolean getIsInBlackList() {
        return isInBlackList;
    }

    public void setIsInBlackList(boolean isInBlackList) {
        this.isInBlackList = isInBlackList;
    }

    public String getSecretToken() {
        return mSecretToken;
    }

    public void setSecretToken(String mSecretToken) {
        Request.setSecretToken(mSecretToken);
        this.mSecretToken = mSecretToken;
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

    public int getWeiboCount() {
        return mWeiboCount;
    }

    public void setWeiboCount(int mWeiboCount) {
        this.mWeiboCount = mWeiboCount;
    }

    public int getFollowersCount() {
        return mFollowersCount;
    }

    public void setFollowersCount(int mFollowersCount) {
        this.mFollowersCount = mFollowersCount;
    }

    public int getFollowedCount() {
        return mFollowedCount;
    }

    public void setFollowedCount(int mFollowedCount) {
        this.mFollowedCount = mFollowedCount;
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

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public ModelWeibo getLastWeibo() {
        return lastWeibo;
    }

    public void setLastWeibo(ModelWeibo lastWeibo) {
        this.lastWeibo = lastWeibo;
    }

    public String getUserTag() {
        return userTag;
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

    @Override
    public String getUserface() {
        return this.getFace();
    }

    protected String postUface = "";


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

    public String getVerifyInfo() {
        return verifyInfo;
    }

    public void setVerifyInfo(String verifyInfo) {
        this.verifyInfo = verifyInfo;
    }

    public int getIsMyContact() {
        return isMyContact;
    }

    public void setIsMyContact(int isMyContact) {
        this.isMyContact = isMyContact;
    }

    public UserApprove getUsApprove() {
        return usApprove;
    }

    public void setUsApprove(UserApprove usApprove) {
        this.usApprove = usApprove;
    }

    /************************************ wz *********************************/
    /**
     * 获取用户各种经验详情
     *
     * @return
     */
    public ModelUserCredit getUserCredit() {
        return userCredit;
    }

    public void setUserCredit(ModelUserCredit userCredit) {
        this.userCredit = userCredit;
    }

    /**
     * 获取用户等级详情
     *
     * @return
     */
    public ModelUserLevel getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(ModelUserLevel userLevel) {
        this.userLevel = userLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ModelUser))
            return false;
        return ((ModelUser) o).getUid() == this.getUid();

    }

}
