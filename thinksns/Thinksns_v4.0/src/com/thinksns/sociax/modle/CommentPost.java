package com.thinksns.sociax.modle;

import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

/**
 * 类说明： 帖子评论Modle
 * 
 * @author povol
 * @date Nov 20, 2012
 * @version 1.0
 */
public class CommentPost extends SociaxItem {

	private int replyId;
	private int weibaId;
	private int postId;
	private int postUid;
	private String ctime;
	private String content;
	private int isDel;
	private int storey;
	private ModelUser author;

	public CommentPost() {
	}

	public CommentPost(JSONObject data) throws DataInvalidException,
			JSONException {
		super(data);
		setReplyId(data.getInt("reply_id"));
		setWeibaId(data.getInt("weiba_id"));
		setPostId(data.getInt("post_id"));
		setPostUid(data.getInt("post_uid"));
		setContent(data.getString("content"));
		setCtime(data.getString("ctime"));
		setIsDel(data.getInt("is_del"));
		setStorey(data.getInt("storey"));
		setAuthor(new ModelUser(data.getJSONObject("author_info")));
	}

	public int getReplyId() {
		return replyId;
	}

	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}

	public int getWeibaId() {
		return weibaId;
	}

	public void setWeibaId(int weibaId) {
		this.weibaId = weibaId;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public int getPostUid() {
		return postUid;
	}

	public void setPostUid(int postUid) {
		this.postUid = postUid;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getIsDel() {
		return isDel;
	}

	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}

	public int getStorey() {
		return storey;
	}

	public void setStorey(int storey) {
		this.storey = storey;
	}

	public ModelUser getAuthor() {
		return author;
	}

	public void setAuthor(ModelUser author) {
		this.author = author;
	}

	@Override
	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserface() {
		// TODO Auto-generated method stub
		return null;
	}

}
