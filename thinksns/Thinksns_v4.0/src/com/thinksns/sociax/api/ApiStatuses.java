package com.thinksns.sociax.api;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONException;

import android.graphics.Bitmap;

import com.thinksns.sociax.modle.Comment;
import com.thinksns.sociax.modle.Follow;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.t4.exception.UpdateException;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.exception.WeiboDataInvalidException;
import com.thinksns.sociax.t4.model.ModelBackMessage;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;
import com.thinksns.sociax.thinksnsbase.utils.FormFile;

public interface ApiStatuses {
	static final String MOD_NAME = "Weibo";
	static final String SHOW = "show";
	static final String PUBLIC_TIMELINE = "public_timeline";
	static final String FRIENDS_TIMELINE = "friends_timeline";
	static final String CHANNEL_TIMELINE = "channels_timeline";
	static final String USER_TIMELINE = "user_timeline";
	static final String MENTION = "mentions_feed";
	static final String SEARCH = "weibo_search_weibo";
	static final String COMMENT_TIMELINE = "comments_timeline";
	static final String COMMENT_BY_ME = "comments_by_me";
	static final String COMMENT_RECEIVE_ME = "comments_to_me";
	static final String COMMENTS = "weibo_comments";
	static final String FOOLOWING = "following";
	static final String FOLLOWERS = "followers";
	static final String FOLLOWEACH = "friends";
	static final String SEARCH_USER = "weibo_search_user";
	static final String UPDATE = "update";
	static final String UPLOAD = "upload";

	static final String CREATE_TEXT_WEIBO = "post_weibo";// 发布文字微博
	static final String CREATE_IMAGE_WEIBO = "upload_photo";// 发布图片微博
	static final String CREATE_VIDEO_WEIBO = "upload_video";// 发布视频微博
	static final String COMMENT = "comment_weibo";// 评论微博
	static final String REPOST = "repost_weibo"; // 转发微博
	static final String REPOST_WEIBA = "comment_post"; // 转发微吧
	static final String DESTROY = "destroy";
	static final String COMMENT_DESTROY = "comment_destroy";
	static final String UN_READ = "unread";
	static final String ADD_DIGG = "digg_weibo"; // 添加赞
	static final String DEL_DIG = "undigg_weibo"; // 取消赞
	static final String FAVORITE = "favorite_weibo";// 收藏微博
	static final String UNFAVORITE = "unfavorite_weibo";// 取消收藏微博
	static final String DELETE = "del_weibo";// 删除微博
	static final String WEIBO_DETAIL = "weibo_detail";// 通过id获取weibo详情
	static final String DENOUNCE = "denounce_weibo";// 举报微博
	static final String DENOUNCE_POST = "denounce_weiba";// 举报帖子
	static final String WEIBO_PHOTO = "weibo_photo"; // 微博配图

	static final String WEIBO_SEARCH_TOPIC = "weibo_search_topic"; // 微博配图
	static final String OAUTH = "Oauth";
//	static final String REGISTER_VERIFY = "sendCodeByPhone";
	static final String REGISTER_VERIFY = "send_register_code";
	static final String CHECK_REGISTER_VERIFY = "check_register_code";
//	static final String FINBACK_VERIFY = "send_findpwd_code";
	static final String FINBACK_VERIFY = "sendCodeByPhone";
	static final String CHECK_FINDBACK_VERIFY = "checkCodeByPhone";
//	static final String CHECK_FINDBACK_VERIFY = "check_password_code";
	static final String SAVA_USER_PWD = "saveUserPasswordByPhone";
//	static final String SAVA_USER_PWD = "save_user_pwd";
	static final String PRIVACY = "User";
	static final String GET_PRIVACY = "user_privacy";
	static final String SAVE_PRIVACY = "save_user_privacy";
	static final String DIG_LIST = "weibo_diggs";
	static final String CHANNEL = "Channel";
	static final String ALL_CHANNEL = "get_all_channel";
	static final String THIDR_REG_INFO = "get_other_login_info";
	static final String BIND_VERIFY = "send_bind_code";

	public ModelWeibo show(int id) throws ApiException, WeiboDataInvalidException,
			VerifyErrorException;

	public ListData<ModelWeibo> publicTimeline(int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	public ListData<ModelWeibo> publicHeaderTimeline(ModelWeibo item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<ModelWeibo> publicFooterTimeline(ModelWeibo item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> userTimeline(ModelUser user, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> userHeaderTimeline(ModelUser user, ModelWeibo item,
			int count) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> userFooterTimeline(ModelUser user, ModelWeibo item,
			int count) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	// 获取好友微博（第一页）
	public ListData<SociaxItem> friendsTimeline(int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> friendsHeaderTimeline(ModelWeibo item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	// 获取好友微博（加载更多）
	public ListData<SociaxItem> friendsFooterTimeline(ModelWeibo item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	// 获取频道微博（第一页）
	public ListData<SociaxItem> channelTimeline(int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	// 获取频道微博（加载更多）
	public ListData<SociaxItem> channelFooterTimeline(ModelWeibo item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> mentions(int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> mentionsHeader(ModelWeibo item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> mentionsFooter(ModelWeibo item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> search(String key, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> searchHeader(String key, ModelWeibo item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> searchFooter(String key, ModelWeibo item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<Comment> commentTimeline(int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	public ListData<Comment> commentHeaderTimeline(Comment item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<Comment> commentFooterTimeline(Comment item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> commentMyTimeline(int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> commentMyHeaderTimeline(Comment item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> commentMyFooterTimeline(Comment item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> commentReceiveMyTimeline(int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> commentReceiveMyHeaderTimeline(Comment item,
			int count) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> commentReceiveMyFooterTimeline(Comment item,
			int count) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> commentForWeiboTimeline(ModelWeibo item, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> commentForWeiboHeaderTimeline(ModelWeibo item,
			Comment comment, int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> commentForWeiboFooterTimeline(ModelWeibo item,
			Comment comment, int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> following(ModelUser user, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> followingHeader(ModelUser user, Follow firstUser,
			int count) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> followingFooter(ModelUser user, Follow lastUser,
			int count) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> followers(ModelUser user, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> followersHeader(ModelUser user, Follow firstUser,
			int count) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> followersFooter(ModelUser user, Follow lastUser,
			int count) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> followEach(ModelUser user, int count)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> followEachHeader(ModelUser user, Follow firstUser,
			int count) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> followEachFooter(ModelUser user, Follow lastUser,
			int count) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> searchUser(String user, int count, int page)
			throws ApiException, ListAreEmptyException, DataInvalidException,
			VerifyErrorException;

	public ListData<SociaxItem> searchHeaderUser(String user, ModelUser firstUser,
			int count, int page) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public ListData<SociaxItem> searchFooterUser(String user, ModelUser lastUser,
			int count, int page) throws ApiException, ListAreEmptyException,
			DataInvalidException, VerifyErrorException;

	public int update(ModelWeibo weibo) throws ApiException, VerifyErrorException,
			UpdateException;

	public boolean upload(ModelWeibo weibo, File file) throws ApiException,
			VerifyErrorException, UpdateException;

	public boolean repost(ModelWeibo weibo, boolean comment) throws ApiException,
			VerifyErrorException, UpdateException, DataInvalidException;

	// 发布文字微博
	public ModelBackMessage createNewTextWeibo(ModelWeibo weibo) throws ApiException,
			VerifyErrorException, UpdateException;

	// 发布图片微博
	public ModelBackMessage createNewImageWeibo(ModelWeibo weibo, FormFile[] filelist)
			throws ApiException, VerifyErrorException, UpdateException;

	// 发送包含位置的文字微博
	ModelBackMessage createNewTextWeibo(ModelWeibo weibo, double longitude, double latitude, String address)
			throws ApiException, VerifyErrorException, UpdateException;
	/**
	 * 发布视频微博
	 * @param weibo
	 * @param file1 预览的图片
	 * @param file2 图片地址
	 * @return
	 * @throws ApiException
	 * @throws VerifyErrorException
	 * @throws UpdateException
	 */
	public ModelBackMessage createNewVideoWeibo(ModelWeibo weibo, Bitmap file1, File file2)
			throws ApiException, VerifyErrorException, UpdateException;

	// 评论一条微博
	public int comment(Comment comment) throws ApiException,
			VerifyErrorException, UpdateException, DataInvalidException,
			JSONException;

	// 转发一条微博
	public ModelBackMessage transpond(Comment comment) throws ApiException,
			VerifyErrorException, UpdateException, DataInvalidException,
			JSONException;

	public boolean destroyComment(Comment coment) throws ApiException,
			VerifyErrorException, DataInvalidException;

	public boolean destroyWeibo(ModelWeibo weibo) throws ApiException,
			VerifyErrorException, DataInvalidException;

	public int unRead() throws ApiException, VerifyErrorException,
			DataInvalidException;

	// 添加赞
	public int addDig(int feedId) throws ApiException, JSONException;

	// 取消赞
	public int delDigg(int feedId) throws ApiException, JSONException;

	public ListData<SociaxItem> getWeiboPhoto(int uid, int count, int page)
			throws ApiException;

	public ListData<SociaxItem> getTopicWeiboList(String key, int page)
			throws ApiException;

	// 收藏微博
	public ModelBackMessage favWeibo(ModelWeibo weibo, HttpResponseListener listener) throws ApiException, JSONException;

	// 取消收藏微博
	public ModelBackMessage unFavWeibo(ModelWeibo weibo, HttpResponseListener listener) throws ApiException,
			JSONException;

	// 删除微博
	public ModelBackMessage deleteWeibo(ModelWeibo weibo) throws ApiException,
			JSONException;

	// 获取微博
	public ModelWeibo getWeiboById(int id) throws ApiException, JSONException,
			WeiboDataInvalidException;

	// 举报微博
	public ModelBackMessage denounceWeibo(int id, String reason)
			throws ApiException, JSONException;

	ListData<SociaxItem> userTimeline(int count) throws ApiException,
			ListAreEmptyException, DataInvalidException, VerifyErrorException;

	Object getDiggList(int feedId, int max_id) throws ApiException,
			JSONException;

	/**
	 * 赞和取消赞微博
	 * @param feedId
	 * @param prestatus
	 * @return
	 * @throws ApiException
	 * @throws JSONException
	 */
	ModelBackMessage changeWeiboDigg(int feedId, int prestatus)
			throws ApiException, JSONException;
}
