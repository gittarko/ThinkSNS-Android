package com.thinksns.sociax.api;

import java.io.File;

import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.modle.ReceiveComment;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

public interface ApiGroup {
	static final String MOD_NAME = "Group";
	static final String SHOW_STATUSES_TYPE = "showStatusType"; //
	static final String SHOW_STATUSES = "showStatuses";
	static final String SHOW_ATME_STATUSES = "showAtmeStatuses";
	static final String SHOW_STATUS_COMMENTS = "showStatusComments"; // 群组内评论我的
	static final String GROUP_MEMBERS = "groupMembers"; //

	static final String WEIBO_DETAI = "weiboDetai";
	static final String WEIBO_COMMENTS = "WeiboComments";

	static final String UPDATE_STATUS = "updateStatus";
	static final String UPLOAD_STATUS = "uploadStatus";
	static final String REPOST_STATUSES = "repostStatuses";

	static final String COMMENT_STATUSES = "commentStatuses";

	public ListData<SociaxItem> showStatuesType() throws ApiException;

	public ListData<SociaxItem> showStatuses(int count, int type)
			throws ApiException;

	public ListData<SociaxItem> showStatusesHeader(ModelWeibo item, int count,
			int type) throws ApiException;

	public ListData<SociaxItem> showStatusesFooter(ModelWeibo item, int count,
			int type) throws ApiException;

	public ListData<SociaxItem> showAtmeStatuses(int count) throws ApiException;

	public ListData<SociaxItem> showAtmeStatusesHeader(ModelWeibo item, int count)
			throws ApiException;

	public ListData<SociaxItem> showAtmeStatusesFooter(ModelWeibo item, int count)
			throws ApiException;

	public ListData<SociaxItem> showStatusComments(int count)
			throws ApiException;

	public ListData<SociaxItem> showStatusCommentsHeader(ReceiveComment item,
			int count) throws ApiException;

	public ListData<SociaxItem> showStatusCommentsFooter(ReceiveComment item,
			int count) throws ApiException;

	public ListData<SociaxItem> groupMembers(int count) throws ApiException;

	public ListData<SociaxItem> groupMembersHeader(ModelUser user, int count)
			throws ApiException;

	public ListData<SociaxItem> groupMembersFooter(ModelUser user, int count)
			throws ApiException;

	public ListData<SociaxItem> weiboComments(ModelWeibo item, Comment comment,
			int count) throws ApiException;

	public ListData<SociaxItem> weiboCommentsHeader(ModelWeibo item,
			Comment comment, int count) throws ApiException;

	public ListData<SociaxItem> weiboCommentsFooter(ModelWeibo item,
			Comment comment, int count) throws ApiException;

	public boolean updateStatus(ModelWeibo weibo) throws ApiException;

	public boolean uploadStatus(ModelWeibo weibo, File file) throws ApiException;

	public boolean repostStatuses(ModelWeibo weibo, boolean isComment)
			throws ApiException;

	public boolean commentStatuses(Comment comment) throws ApiException;
}
