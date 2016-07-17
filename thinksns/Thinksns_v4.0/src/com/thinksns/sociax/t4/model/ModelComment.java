package com.thinksns.sociax.t4.model;

import android.util.Log;

import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.modle.UserApprove;
import com.thinksns.sociax.t4.exception.CommentDataInvalidException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明： 评论
 * 
 * @author wz
 * @date 2014-10-17
 * @version 1.0
 */
public class ModelComment extends SociaxItem {
	public static final String TAG = "TSTAG_ModelComment";

	protected int comment_id;		// 评论id
	protected int replyCommentId;	//回复的评论id
	protected String comment_type,	// 评论类型
				content,			// 评论内容
				ctime,				// 提交时间
				from;				// 来自
	protected String toName;		//被回复人的姓名
	protected String attach_info;	// 附加内容
	protected ModelWeibo weibo;		// 被评论的微博
	protected ModelUser user;		// 用户
	protected String uid,			// 评论者uid
				uname,				// 评论者名字
				uface;				// 评论者头像
	protected String weibo_bg;		// 原微博的背景图
	protected ListData<ModleUserGroup> usergroup;// 用户组标签

	protected String floor;// 回复所在楼层
	protected String isDigg;// 是否已赞
	protected int diggCount;// 赞的数目
	protected int digg_id;//
	protected int feed_id;

	protected UserApprove userApprove; // caoligai 添加，将旧版的 ModelApprove 改为
										// UserApprove

	public UserApprove getUserApprove() {
		return userApprove;
	}

	public void setUserApprove(UserApprove userApprove) {
		this.userApprove = userApprove;
	}

	public int getFeed_id() {
		return feed_id;
	}

	public void setFeed_id(int feed_id) {
		this.feed_id = feed_id;
	}

	public int getDigg_id() {
		return digg_id;
	}

	public void setDigg_id(int digg_id) {
		this.digg_id = digg_id;
	}

	@Override
	public int compareTo(SociaxItem another) {
		return 0;
	}

	public enum Type {
		COMMENT, WEIBO, SENDING //正在发表的评论
	}

	protected Type type;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getReplyCommentId() {
		return replyCommentId;
	}

	public void setReplyCommentId(int replyCommentId) {
		this.replyCommentId = replyCommentId;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public ModelComment(JSONObject data) throws DataInvalidException {
		super(data);
		try {
			if (data.has("type")) {
				String typeTemp = data.getString("type");
				this.setCommentType(typeTemp);
			}
			if (data.has("user_info")) {
				JSONObject jo = data.getJSONObject("user_info");
				if (jo.has("uid"))
					this.setUid(jo.getString("uid"));
				if (jo.has("uname"))
					this.setUname(jo.getString("uname"));
				if (jo.has("avatar")
						&& jo.getJSONObject("avatar").has("avatar_middle")) {
					try {
						this.setUface(jo.getJSONObject("avatar").getString(
								"avatar_middle"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (data.has("feed_id")) {
					this.setFeed_id(data.getInt("feed_id"));
				}
				if (jo.has("user_group")) {
					// 用户认证信息
					try {
						// 这个地方和 ModelUser 不一样，因为 ModelComment 返回的数据格式是将
						// "user_group" 字段放在 "avatar" 里面，所以传的参数是 "avatar"
						// jsonObject(这里是 jo) 而不是直接传 data
						this.setUserApprove(new UserApprove(jo));
					} catch (Exception e) {
						e.printStackTrace();
						Log.d(TAG, "解析用户组 json 数据出现错误");
					}
				}

				this.setUser(new ModelUser(jo));
			}
			if (data.has("from"))
				this.setFrom(data.getString("from"));

			if (data.has("comment_id")) {
				this.setComment_id(data.getInt("comment_id"));
			}

			if (data.has("ctime"))
				this.setCtime(data.getString("ctime"));

			if (data.has("content")) {
				//处理返回的评论内容
				String content = data.getString("content");
				if(content.contains("@回复")) {
					content.replaceFirst("：", ": ");
				}
				this.setContent(content);
			}

			if (data.has("attach_info")) {
				this.setAttach_info(data.getString("attach_info"));
			}

			if (data.has("floor")) {
				this.setFloor(data.getString("floor"));
			}
			if (data.has("is_digg")) {
				this.setIsDigg(data.getString("is_digg"));
			}
			if (data.has("digg_count")) {
				this.setDiggCount(data.getInt("digg_count"));
			}
			if (data.has("digg_id")) {
				this.setDigg_id(data.getInt("digg_id"));
			}

			if (data.has("feed_info")) {
				try {
					try {
						if (!data.getString("feed_info").equals("[]"))
							this.setWeibo(new ModelWeibo(data
									.getJSONObject("feed_info")));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (weibo != null)
						if ((weibo.hasFile() || weibo.hasImage())) {

							this.setWeibo_bg(((ModelImageAttach) weibo
									.getAttachImage().get(0)).getSmall());
						} else if (weibo.hasVideo()) {
							this.setWeibo_bg(weibo.getAttachVideo()
									.getVideoImgUrl());

						}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (JSONException e) {
			Log.d(AppConstant.APP_TAG, "json error " + e.toString());
			throw new CommentDataInvalidException();
		}
	}

	public ModelComment() {
	}

	public String getComment_type() {
		return comment_type;
	}

	public void setComment_type(String comment_type) {
		this.comment_type = comment_type;
	}

	public ListData<ModleUserGroup> getUsergroup() {
		return usergroup;
	}

	public void setUsergroup(ListData<ModleUserGroup> usergroup) {
		this.usergroup = usergroup;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getIsDigg() {
		return isDigg;
	}

	public void setIsDigg(String isDigg) {
		this.isDigg = isDigg;
	}

	public int getDiggCount() {
		return diggCount;
	}

	public void setDiggCount(int diggCount) {
		this.diggCount = diggCount;
	}

	public String getWeibo_bg() {
		return weibo_bg;
	}

	public void setWeibo_bg(String weibo_bg) {
		this.weibo_bg = weibo_bg;
	}

	public ModelUser getUser() {
		return user;
	}

	public void setUser(ModelUser user) {
		this.user = user;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getUface() {
		return uface;
	}

	public void setUface(String uface) {
		this.uface = uface;
	}

	public int getComment_id() {
		return comment_id;
	}

	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	public String getCommentType() {
		return comment_type;
	}

	public void setCommentType(String type) {
		this.comment_type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getAttach_info() {
		return attach_info;
	}

	public void setAttach_info(String attach_info) {
		this.attach_info = attach_info;
	}

	public ModelWeibo getWeibo() {
		return weibo;
	}

	public void setWeibo(ModelWeibo weibo) {
		this.weibo = weibo;
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		return uface;
	}

	private String isShareFeed;

	public String getIsShareFeed() {
		return isShareFeed;
	}

	public void setIsShareFeed(String string) {
		// TODO Auto-generated method stub
		this.isShareFeed = string;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof ModelComment))
			return false;

		return ((ModelComment)o).getComment_id() == this.getComment_id();
	}
}