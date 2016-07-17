package com.thinksns.sociax.modle;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/**
 * 类说明：
 * 
 * @author povol
 * @date Nov 20, 2012
 * @version 1.0
 */
public class Posts extends SociaxItem implements Serializable {

	private int postId;
	private int weibaId;
	private int postUid;
	private String title;
	private String content;
	private String postTime;
	private int replyCount; // 回复数
	private int readCount;
	private int feedId;
	private int top; // 2 全局置顶 1 吧内置顶
	private int digest; // 1 精华
	private int recommentd; // 1 推荐
	private int favorite; // 收藏

	private ModelUser postUser;

	public Posts() {
	}

	public Posts(JSONObject data) throws DataInvalidException, JSONException {
		super(data);
		if (data.has("post_id"))
			setPostId(data.getInt("post_id"));

		if (data.has("weiba_id"))
			setWeibaId(data.getInt("weiba_id"));

		if (data.has("post_uid"))
			setPostUid(data.getInt("post_uid"));

		if (data.has("title"))
			setTitle(data.getString("title"));

		if (data.has("content"))
			setContent(data.getString("content"));

		if (data.has("post_time"))
			setPostTime(data.getString("post_time"));

		if (data.has("reply_count"))
			setReplyCount(data.getInt("reply_count"));

		if (data.has("read_count"))
			setReadCount(data.getInt("read_count"));

		if (data.has("feed_id"))
			setFeedId(data.getInt("feed_id"));

		if (data.has("top"))
			setTop(data.getInt("top"));

		if (data.has("digest"))
			setDigest(data.getInt("digest"));

		if (data.has("recommend"))
			setRecommentd(data.getInt("recommend"));

		if (data.has("favorite"))
			setFavorite(data.getInt("favorite"));
		if (data.has("author_info"))
			setPostUser(new ModelUser(data.getJSONObject("author_info")));
		if (data.has("source_user_info"))
			setPostUser(new ModelUser(data.getJSONObject("source_user_info")));
	}

	@Override
	public boolean checkValid() {
		return false;
	}

	@Override
	public String getUserface() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public int getWeibaId() {
		return weibaId;
	}

	public void setWeibaId(int weibaId) {
		this.weibaId = weibaId;
	}

	public int getPostUid() {
		return postUid;
	}

	public void setPostUid(int postUid) {
		this.postUid = postUid;
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

	public String getPostTime() {
		return postTime;
	}

	public void setPostTime(String postTime) {
		this.postTime = postTime;
	}

	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}

	public int getFeedId() {
		return feedId;
	}

	public void setFeedId(int feedId) {
		this.feedId = feedId;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getDigest() {
		return digest;
	}

	public void setDigest(int digest) {
		this.digest = digest;
	}

	public int getRecommentd() {
		return recommentd;
	}

	public void setRecommentd(int recommentd) {
		this.recommentd = recommentd;
	}

	public ModelUser getPostUser() {
		return postUser;
	}

	public void setPostUser(ModelUser postUser) {
		this.postUser = postUser;
	}

	public int getFavorite() {
		return favorite;
	}

	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}

}
