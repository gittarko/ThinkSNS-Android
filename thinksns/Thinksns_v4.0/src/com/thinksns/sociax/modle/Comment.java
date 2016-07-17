package com.thinksns.sociax.modle;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.exception.CommentContentEmptyException;
import com.thinksns.sociax.t4.exception.CommentDataInvalidException;
import com.thinksns.sociax.t4.exception.UpdateException;
import com.thinksns.sociax.t4.exception.WeiboDataInvalidException;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

@SuppressWarnings("serial")
public class Comment extends ModelComment {
	protected ModelWeibo status;
	protected int replyUid;
	protected int replyCommentId = -1;
	protected int weiboId;
	protected String replyJson;

	protected String appName;
	protected Comment replyComment;

	public Comment(JSONObject data) throws DataInvalidException {
		super(data);
		try {
			if (data.has("type")) {
				String typeTemp = data.getString("type");
				this.setCommentType(typeTemp);
			}
			if (data.has("user_info")) {
				JSONObject jo = data.getJSONObject("user_info");
				this.setUid(jo.getString("uid"));			// 用户id
				this.setUname(jo.getString("uname"));		// 用户名
				this.setHeadUrl(jo.getJSONObject("avatar").getString(
						"avatar_middle"));				// 用户头像

				if (jo.has("user_group")) {
					//用户认证信息
					setUserApprove(new UserApprove(jo));
				}
			}

			if (data.has("comment_id")) {
				this.setComment_id(Integer.parseInt(data.getString("comment_id")));
			}

			if (data.has("ctime"))
				this.setTimestemp(data.getString("ctime"));

			if (data.has("content"))
				this.setContent(data.getString("content"));

			if (data.has("sourceInfo"))
				this.setStatus(new ModelWeibo(data.getJSONObject("sourceInfo"), 1));

			if (data.has("reply_comment_id"))
				this.setReplyCommentId(data.getInt("reply_comment_id"));
			if (data.has("reply_uid"))
				this.setReplyUid(data.getInt("reply_uid"));
			if (data.has("weibo_id"))
				this.setWeiboId(data.getInt("weibo_id"));
			if (data.has("row_id") && !data.get("row_id").equals("[]"))
				this.setWeiboId(data.getInt("row_id"));

//			if (this.getType() == Type.COMMENT
//					&& !data.getString("comment").equals("false")) {
//				this.setReplyJson(data.getJSONObject("comment").toString());
//				this.setReplyComment(new Comment(data.getJSONObject("comment")));
//			}
		} catch (JSONException e) {
			Log.d(AppConstant.APP_TAG, "json error " + e.toString());
			throw new CommentDataInvalidException();
		}
	}

	public Comment() {
	}

	public boolean isNullForUid() {
		return Integer.parseInt(this.getUid()) <= 0;
	}

	public boolean isNullForTimestemp() {
		return this.getTimestemp() <= 0;
	}

	public boolean isNullForReplyUid() {
		return this.getReplyUid() <= 0;
	}

	public boolean isNullForCommentId() {
		return this.getComment_id() <= 0;
	}

	public boolean isNullForReplyCommentId() {
		return this.getReplyCommentId() <= 0;
	}

	public boolean isNullForWeiboId() {
		return this.getWeiboId() <= 0;
	}

	public boolean isNullForContent() {
		String temp = this.getContent();
		return temp == null || temp.equals("");
	}

	public boolean isNullForReplyComment() {

		Comment temp = this.getReplyComment();
		return temp == null || temp.equals("");
	}

	public boolean isNullForType() {
		Type temp = this.getType();
		return temp == null;
	}

	public boolean isNullForStatus() {
		ModelWeibo temp = this.getStatus();
		return temp == null;
	}

	public int getTimestemp() {
		return Integer.parseInt(getCtime());
	}

	public void setTimestemp(String timestemp) {
		setCtime(timestemp);
	}

	public ModelWeibo getStatus() {
		return status;
	}

	public void setStatus(ModelWeibo status) {
		this.status = status;
	}

	public int getReplyUid() {
		return replyUid;
	}

	public void setReplyUid(int replyUid) {
		this.replyUid = replyUid;
	}



	public int getReplyCommentId() {
		return replyCommentId;
	}

	public void setReplyCommentId(int replyCommentId) {
		this.replyCommentId = replyCommentId;
	}

	@Override
	public void setCommentType(String type) {
		if (type.equals("comment"))
			this.type = Type.COMMENT;
		else if (type.equals("weibo"))
			this.type = Type.WEIBO;
		else {
			this.type = Type.SENDING;
		}
	}

	public Comment getReplyComment() {
		return replyComment;
	}

	public void setReplyComment(Comment replyComment) {
		this.replyComment = replyComment;
	}

	public void checkCommentCanAdd() throws DataInvalidException,
			UpdateException {
		if (this.isNullForContent())
			throw new CommentContentEmptyException();
		if (this.isNullForStatus())
			throw new WeiboDataInvalidException();
	}

	@Override
	public boolean checkValid() {
		boolean result = true;
		result = result
				&& !(this.isNullForContent() || this.isNullForCommentId() || this
						.isNullForUid());
		if (!this.isNullForType() && this.getType() == Type.COMMENT) {
			result = result && !this.isNullForReplyComment();
		}
		if (this.getType() == Type.COMMENT) {
			result = result && !this.isNullForReplyComment();
		}
		return result;
	}

	public int getWeiboId() {
		return weiboId;
	}

	public void setWeiboId(int weiboId) {
		this.weiboId = weiboId;
	}

	public String getHeadUrl() {
		return getUface();
	}

	public void setHeadUrl(String headUrl) {
		setUface(headUrl);
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}


	public String getReplyJson() {
		return replyJson;
	}

	public void setReplyJson(String replyJson) {
		this.replyJson = replyJson;
	}

}
