package com.thinksns.sociax.t4.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明： 帖子 本类在调用序列化时候使用parcleable方法
 * 
 * @author wz
 * @date 2014-12-22
 * @version 1.0
 */
public class ModelPost extends SociaxItem implements Parcelable {
	int post_id,// 帖子id
		weiba_id,// 帖子所在微吧id
		post_uid,// 发帖人
			feed_id,// 获取帖子的评论时候需要用到这个feed_id
			reply_count,// 评论数
			read_count,// 浏览数
			top;
	private String title,// 帖子标题
			content,	// 帖子内容
			post_time,	// 帖子发布时间
			last_reply_uid,// 最后回复的人的uid
			last_reply_time,// 最后回复时间
			digest, lock, recommend,
			recommend_time,
			is_del, reply_all_count,// 总共回复楼层数目
			attach, from;// 来自哪里
	protected String uface;
	private int praise;		//点赞数
	boolean is_favourite;	// 是否已经收藏 ，同is_digg,只是服务端调用返回。1是已经赞
	boolean isFirstInPart = false;// 是否在分类的第一个，用于标记是否显示第一条item的分类名称
	private String str_part_name;	// 分类名称
	boolean is_digg;		// 是否已经赞，同praise

	private ModelUser user;			// 发帖人的信息,
	private JSONArray imgs;			// 附带的图片地址（如果有的话）
	private ListData<SociaxItem> commentInfoList = new ListData<SociaxItem>();// 评论列表信息
	private ListData<ModelDiggUser> diggInfoList;// 赞列表信息
	private ModelWeiba weiba;// 帖子所在微吧
	private boolean isFromWeiba = false;	//帖子信息是否来自微吧详情中的接口
	private String postJson;

	public boolean isFromWeiba() {
		return isFromWeiba;
	}

	public void setFromWeiba(boolean fromWeiba) {
		isFromWeiba = fromWeiba;
	}

	public ModelWeiba getWeiba() {
		return weiba;
	}

	public void setWeiba(ModelWeiba weiba) {
		this.weiba = weiba;
	}

	public boolean isFirstInPart() {
		return isFirstInPart;
	}

	public void setFirstInPart(boolean isFirstInPart) {
		this.isFirstInPart = isFirstInPart;
	}

	public String getStr_part_name() {
		return str_part_name;
	}

	public void setStr_part_name(String str_part_name) {
		this.str_part_name = str_part_name;
	}

	public boolean isIs_favourite() {
		return is_favourite;
	}

	public void setIs_favourite(boolean is_favourite) {
		this.is_favourite = is_favourite;
	}

	public String getPostJson() {
		return postJson;
	}

	public void setPostJson(String postJson) {
		this.postJson = postJson;
	}

	public ModelPost(String str_part_name) {
		this.setFirstInPart(true);
		this.setStr_part_name(str_part_name);

	}

	public ModelPost(JSONObject data) {
        try {
			this.postJson = data.toString();
			if (data.has("post_id"))
				this.setPost_id(data.getInt("post_id"));
			if (data.has("weiba_id"))
				this.setWeiba_id(data.getInt("weiba_id"));
			if (data.has("post_uid"))
				this.setPost_uid(data.getInt("post_uid"));
			if (data.has("feed_id"))
				this.setFeed_id(data.getInt("feed_id"));
			if (data.has("reply_count"))
				this.setReply_count(data.getInt("reply_count"));
			if (data.has("read_count"))
				this.setRead_count(data.getInt("read_count"));
			if (data.has("top"))
				this.setTop(data.getInt("top"));
			if (data.has("title"))
				this.setTitle(data.getString("title"));
			if (data.has("content"))
				this.setContent(data.getString("content"));
			if (data.has("post_time"))
				this.setPost_time(data.getString("post_time"));
			if (data.has("last_reply_time"))
				this.setLast_reply_time(data.getString("last_reply_time"));
			if (data.has("last_reply_uid"))
				this.setLast_reply_uid(data.getString("last_reply_uid"));
			if (data.has("digest"))
				this.setDigest(data.getString("digest"));
			if (data.has("reply_all_count"))
				this.setReply_all_count(data.getString("reply_all_count"));
			if (data.has("attach"))
				this.setAttach(data.getString("attach"));
			if (data.has("lock"))
				this.setLock(data.getString("lock"));
			if (data.has("recommend"))
				this.setRecommend(data.getString("recommend"));
			if (data.has("recommend_time"))
				this.setRecommend_time(data.getString("recommend_time"));
			if (data.has("is_del"))
				this.setIs_del(data.getString("is_del"));
			if (data.has("praise"))
				this.setPraise(data.getInt("praise"));
			if (data.has("from"))
				this.setFrom(data.getString("from"));
			if (data.has("img")) {
				//帖子图片集合
				this.setImg(data.getJSONArray("img"));
			}
			if (data.has("is_digg"))
				this.setDigg(data.getString("is_digg").equals("1"));
			//是否点赞，用在微吧详情里面的帖子列表
			if(data.has("digg")) {
				this.setDigg(data.getString("digg").equals("digg"));
			}
			if (data.has("is_favorite")) {
				this.setIs_favourite(data.getString("is_favorite").equals("1"));
			}
			if (data.has("user_info")) {
					try {
						this.setUser(new ModelUser(data.getJSONObject("user_info")));
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						this.setUface(data.getJSONObject("user_info").getJSONObject("avatar").getString("avatar_middle"));
					} catch (Exception e) {
						e.printStackTrace();
					}

			}
			if (data.has("comment_info")) {
				try {
					JSONArray commentInfo = data.getJSONArray("comment_info");
					if (commentInfo.length() > 0) {
						ListData<SociaxItem> list = new ListData<SociaxItem>();
						for (int i = 0; i < commentInfo.length(); i++) {
							ModelComment md = new ModelComment(
									commentInfo.getJSONObject(i));
							list.add(md);
						}
						this.setCommentInfoList(list);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (data.has("digg_info")) {
				try {
					JSONArray digg_info = data.getJSONArray("digg_info");
					if (digg_info.length() > 0) {
						ListData<ModelDiggUser> list = new ListData<ModelDiggUser>();
						for (int i = 0; i < digg_info.length(); i++) {
							ModelDiggUser md = new ModelDiggUser(
									digg_info.getJSONObject(i));
							list.add(md);
						}
						this.setDiggInfoList(list);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (data.has("weiba")) {
				try {
					this.setWeiba(new ModelWeiba(data.getJSONObject("weiba")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getUface() {
		return uface;
	}

	public void setUface(String uface) {
		this.uface = uface;
	}

	public int getPost_id() {
		return post_id;
	}

	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	public int getWeiba_id() {
		return weiba_id;
	}

	public void setWeiba_id(int weiba_id) {
		this.weiba_id = weiba_id;
	}

	public int getPost_uid() {
		return post_uid;
	}

	public void setPost_uid(int post_uid) {
		this.post_uid = post_uid;
	}

	public int getFeed_id() {
		return feed_id;
	}

	public void setFeed_id(int feed_id) {
		this.feed_id = feed_id;
	}

	public int getReply_count() {
		return reply_count;
	}

	public void setReply_count(int reply_count) {
		this.reply_count = reply_count;
	}

	public int getRead_count() {
		return read_count;
	}

	public void setRead_count(int read_count) {
		this.read_count = read_count;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPost_time() {
		return post_time;
	}

	public void setPost_time(String post_time) {
		this.post_time = post_time;
	}

	public String getLast_reply_uid() {
		return last_reply_uid;
	}

	public void setLast_reply_uid(String last_reply_uid) {
		this.last_reply_uid = last_reply_uid;
	}

	public String getLast_reply_time() {
		return last_reply_time;
	}

	public void setLast_reply_time(String last_reply_time) {
		this.last_reply_time = last_reply_time;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}

	public String getRecommend() {
		if(recommend == null)
			return "0";
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	public String getRecommend_time() {
		return recommend_time;
	}

	public void setRecommend_time(String recommend_time) {
		this.recommend_time = recommend_time;
	}

	public String getIs_del() {
		return is_del;
	}

	public void setIs_del(String is_del) {
		this.is_del = is_del;
	}

	public String getReply_all_count() {
		return reply_all_count;
	}

	public void setReply_all_count(String reply_all_count) {
		this.reply_all_count = reply_all_count;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public int getPraise() {
		return praise;
	}

	public void setPraise(int praise) {
		this.praise = praise;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public JSONArray getImg() {
		return imgs;
	}

	public void setImg(JSONArray img) {
		this.imgs = img;
	}

	public boolean isDigg() {
		return is_digg;
	}

	public void setDigg(boolean digg) {
		this.is_digg = digg;
	}

	public ModelUser getUser() {
		return user;
	}

	public void setUser(ModelUser user) {
		this.user = user;

	}

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserface() {
		// TODO Auto-generated method stub
		return getUser().getUserface();
	}

	public ListData<ModelDiggUser> getDiggInfoList() {
		return diggInfoList;
	}

	public void setDiggInfoList(ListData<ModelDiggUser> diggInfoList) {
		this.diggInfoList = diggInfoList;
	}

	public ListData<SociaxItem> getCommentInfoList() {
		return commentInfoList;
	}

	public void setCommentInfoList(ListData<SociaxItem> commentInfoList) {
		this.commentInfoList = commentInfoList;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(post_id);
		dest.writeInt(weiba_id);
		dest.writeInt(post_uid);
		dest.writeInt(feed_id);
		dest.writeInt(reply_count);
		dest.writeInt(read_count);
		dest.writeInt(top);
		dest.writeString(title);
		dest.writeString(content);
		dest.writeString(post_time);
		dest.writeString(last_reply_uid);
		dest.writeString(last_reply_time);
		dest.writeString(digest);
		dest.writeString(lock);
		dest.writeString(recommend);
		dest.writeString(recommend_time);
		dest.writeString(is_del);
		dest.writeString(reply_all_count);
		dest.writeString(attach);
		dest.writeString(from);
		dest.writeInt(praise);
		dest.writeString(is_digg ? "1" : "0");
		dest.writeString(uface);
		dest.writeString(imgs==null?"[]":imgs.toString());
		dest.writeValue(user);
		dest.writeValue(commentInfoList);
		dest.writeValue(diggInfoList);
		dest.writeInt(isFromWeiba ? 1 : 0);
		dest.writeParcelable(weiba, flags);
	}

	public ModelPost(){

	}

	// 重写Creator
	public static final Parcelable.Creator<ModelPost> CREATOR = new Parcelable.Creator<ModelPost>() {
		@SuppressWarnings("unchecked")
		@Override
		public ModelPost createFromParcel(Parcel dest) {
			ModelPost md = new ModelPost();
			try {
				md.setPost_id(dest.readInt());
				md.setWeiba_id(dest.readInt());
				md.setPost_uid(dest.readInt());
				md.setFeed_id(dest.readInt());
				md.setReply_count(dest.readInt());
				md.setRead_count(dest.readInt());
				md.setTop(dest.readInt());
				md.setTitle(dest.readString());
				md.setContent(dest.readString());
				md.setPost_time(dest.readString());
				md.setLast_reply_uid(dest.readString());
				md.setLast_reply_time(dest.readString());
				md.setDigest(dest.readString());
				md.setLock(dest.readString());
				md.setRecommend(dest.readString());
				md.setRecommend_time(dest.readString());
				md.setIs_del(dest.readString());
				md.setReply_all_count(dest.readString());
				md.setAttach(dest.readString());
				md.setFrom(dest.readString());
				md.setPraise(dest.readInt());
				md.setDigg(dest.readString().equals("1"));
				md.setUface(dest.readString());
				try {
					md.setImg(new JSONArray(dest.readString()));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				md.setUser((ModelUser) dest.readValue(null));
				try {
					md.setCommentInfoList((ListData<SociaxItem>) dest.readValue(ListData.class.getClassLoader()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					md.setDiggInfoList((ListData<ModelDiggUser>) dest.readValue(ListData.class.getClassLoader()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				md.setFromWeiba(dest.readInt()== 1 ? true : false);
				md.setWeiba((ModelWeiba)dest.readParcelable(ModelWeiba.class.getClassLoader()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return md;
		}

		@Override
		public ModelPost[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	boolean isLastInPart = false;// 专门用于标记人们嘴贱最后一条帖子，以显示查看全部

	public void setLastInPart(boolean isLastInPart) {
		// TODO Auto-generated method stub
		this.isLastInPart = isLastInPart;
	}

	public boolean isLastInPart() {
		return isLastInPart;
	}

	@Override
	public int compareTo(SociaxItem another) {
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof ModelPost))
			return false;
		return ((ModelPost)o).getPost_id() == this.getPost_id();
	}
}
