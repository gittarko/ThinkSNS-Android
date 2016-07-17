package com.thinksns.sociax.api;

import com.thinksns.sociax.modle.CommentPost;
import com.thinksns.sociax.modle.Posts;
import com.thinksns.sociax.modle.Weiba;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.t4.exception.UpdateException;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelBackMessage;
import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ExceptionIllegalParameter;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;
import com.thinksns.sociax.thinksnsbase.utils.FormFile;

import org.json.JSONException;

import java.util.List;




/**
 * 类说明：
 * 
 * @author povol
 * @date Nov 19, 2012
 * @version 1.0
 */
public interface ApiWeiba {

	// act 名称
	public static String GET_WEIBAS = "get_weibas"; // 获取微吧列表
	public static String CREATE = "create"; // 关注微吧
	public static String DESTROY = "destroy"; // 取消关注微吧
	public static String CREATE_POST = "create_post"; // 发布帖子
	public static String GET_POSTS = "get_posts"; // 获取帖子列表
	public static String POST_FAVORITE = "post_favorite"; // 收藏
	public static String POST_UNFAVORITE = "post_unfavorite"; // 取消收藏
	public static String COMMENT_LIST = "comment_list";
	public static String COMMENT_POST = "comment_post"; // 评论帖子
	public static String REPLY_COMMENT = "reply_comment"; // 回复评论
	public static String DELETE_COMMENT = "delete_comment"; // 删除评论
	public static String FOLLOWING_POSTS = "following_posts"; // 我关注的帖子
	public static String FAVORITE_POSTS = "favorite_list"; // 我收藏的帖子
	public static String POSTEDS = "posteds"; // 我发布的帖子
	public static String COMMENTEDS = "commenteds"; // 我评论的帖子
	public static String SEARCH_WEIBA = "search_weiba"; // 搜索微吧
	public static String SEARCH_POST = "search_post"; // 搜索帖子
    public static String GET_WEIBA_URL = "get_weiba_url";
	String GET_DIGEST_ALL = "digest_all";


    // 获取微吧列表
	public List<SociaxItem> getWeibas() throws ApiException;

	public List<SociaxItem> getWeibasHeader(Weiba weiba, int page, int count)
			throws ApiException;

	public List<SociaxItem> getWeibasFooter(Weiba weiba, int page, int count)
			throws ApiException;

	public boolean create(int weibaId) throws ApiException;

	public boolean destroy(int weibaId) throws ApiException;


	// 获取帖子列表
	public List<SociaxItem> getPosts(int weibaId) throws ApiException;

	public List<SociaxItem> getPostsHeader(int weibaId, int page, int count)
			throws ApiException;

	public List<SociaxItem> getPostsFooter(int weibaId, int page, int count)
			throws ApiException;

	public Posts postDetail(int postsId) throws ApiException;

	// 获取评论列表
	public List<SociaxItem> getCommentList(int postId) throws ApiException;

	public List<SociaxItem> getCommentListHeader(int postId, int page, int count)
			throws ApiException;

	public List<SociaxItem> getCommentListFooter(int postId, int page, int count)
			throws ApiException;

	public boolean commentPost(CommentPost cPost) throws ApiException;

	public boolean favoritePost(int postId) throws ApiException;

	public boolean unfavoritePost(int postId) throws ApiException;

	public boolean replyComment(CommentPost cPost) throws ApiException;

	public boolean deleteComment() throws ApiException;

	// 我关注的
	public List<SociaxItem> followingPosts() throws ApiException;

	public List<SociaxItem> followingPostsHeader(int page, int count)
			throws ApiException;

	public List<SociaxItem> followingPostsFooter(int page, int count)
			throws ApiException;

	// 我发布的
	public List<SociaxItem> posteds(int uid) throws ApiException;

	public List<SociaxItem> postedsHeader(int page, int count)
			throws ApiException;

	public List<SociaxItem> postedsFooter(int page, int count)
			throws ApiException;

	// 我评论的
	public List<SociaxItem> commenteds(int uid) throws ApiException;

	public List<SociaxItem> commentedsHeader(int page, int count)
			throws ApiException;

	public List<SociaxItem> commentedsFooter(int page, int count)
			throws ApiException;

	// 我收藏的
	public List<SociaxItem> favoritePostsList(int uid) throws ApiException;

	public List<SociaxItem> favoritePostsListHeader(int page, int count)
			throws ApiException;

	public List<SociaxItem> favoritePostsListFooter(int page, int count)
			throws ApiException;

	public List<SociaxItem> searchWeiba(String key) throws ApiException;

	public List<SociaxItem> searchWeibaHeader(String key, int page, int count)
			throws ApiException;

	public List<SociaxItem> searchWeibaFooter(String key, int page, int count)
			throws ApiException;

	public List<SociaxItem> searchPost(String key) throws ApiException;

	public List<SociaxItem> searchPostHeader(String key, int page, int count)
			throws ApiException;

	public List<SociaxItem> searchPostFooter(String key, int page, int count)
			throws ApiException;
	
	/*************************t4*****************************/
	
	public static String MOD_NAME = "Weiba"; // MOD 名称
	public static String MY_WEIBA = "weiba_detail";//微吧详情
	public static String WEIBA_COMMENTS = "weiba_comments";//帖子评论列表
	public static String RECOMMED_WEIBA="_weiba_recommend";//推荐微吧
	public static String GET_WEIBA_POST_LIST = "detail";//贴吧详情
	public static String UNFOLLOW = "unFollowWeiba";//取消关注	
	public static String DOFOLLOW = "doFollowWeiba";//关注
	public static String POST_DETAIL = "post_detail"; // 获取贴子详细信息
	public static String UN_FAVOURITE = "unfavorite";//取消收藏
	public static String FAVOURITE = "favorite";//收藏
	public static String UN_DIGG ="del_post_digg";//取消赞
	public static String DIGG = "add_post_digg";//赞
	public static String DITAIL_DIGEST = "detail_digest";//精华帖列表
	public static String ALL_WEIBA = "all_wieba";//发现里面，全部微吧列表
	public static String RECOMMEND_TOPIC = "recommend_topic";//推荐帖子
	public static String HOT_POST = "recommend_all";//热门帖子
	public static String ADD_POST = "add_post";//添加帖子
	public static String REPLY_POST = "comment_post";//评论或者转发帖子
	public static String DEL_POST = "del_post";//删除帖子

	public static String UPLOAD_POST = "upload_photo";
	public static String ALL_POST = "post_all";//所有帖子，逛一逛
    public static String DETAIL = "detail";
	public static String FIND_WEIBA = "findWeiba";// 搜索微吧
	public static String SEARCH_TOPIC = "search_topic";// 搜索帖子
	public static String COLLECT_POST = "getUserFavorite";

	/**
	 * 我的微吧列表
	 * @param count
	 * @param max_id
	 * @return  null 或者ListData(ModelWeiba)
	 * @throws ApiException
	 */
	public ListData<SociaxItem> getMyWeibaList(int count, int max_id, final HttpResponseListener listener)throws ApiException;
	
	/**
	 * 推荐微吧
	 * @param count 推荐的数目
	 * @return  null 或者ListData(ModelWeiba)
	 * @throws ApiException
	 */
	public ListData<SociaxItem> getRecommendWeibaList(int limit,int max_id)throws ApiException;
	
	/**
	 * 某个微吧的帖子列表
	 * @param weiba_id 微吧id 必须 
	 * @param max_id 最后一条帖子的id  可选
	 * @return
	 * @throws ApiException
	 */
	Object getWeibaPostList(int weiba_id,int count,int max_id, HttpResponseListener listener)throws ApiException;
	
	/**
	 * 修改关注状态
	 * @param weiba_id
	 * @param isfollow  当前是否已经关注
	 * @return
	 * @throws ApiException
	 */
	Object changeWeibaFollow(int weiba_id, boolean isfollow) throws ApiException;
	
	/**
	 * 获取某个帖子的评论列表
	 * @param pageCount  获取数目
	 * @param feed_id  帖子的feed_id
	 * @param maxid  上次返回最后一评论的comment_id 第一次传0
	 * @return
	 * @throws ExceptionIllegalParameter 
	 * @throws DataInvalidException 
	 * @throws ListAreEmptyException 
	 * @throws ApiException 
	 * @throws VerifyErrorException 
	 */
	ListData<SociaxItem> getPostCommentList(int pageCount, int feed_id,
			int maxid, HttpResponseListener listener) throws VerifyErrorException, ApiException,
			ListAreEmptyException, DataInvalidException, ExceptionIllegalParameter;
	/**
	 * 获取帖子详情
	 * @param post_id
	 * @return
	 * @throws ApiException
	 */
	Object getPostDetail(int post_id) throws ApiException;
	/**
	 * 收藏、取消收藏帖子
	 * @param post_id 必须
	 * @param weiba_id 必须
	 * @param post_uid 必须
	 * @param preStatus 之前微吧的状态，传1表示之前已经收藏，则取消收藏，传0表示之前没有收藏，则收藏
	 * @return
	 * @throws ApiException
	 */
	Object getChangePostFavourite(int post_id, int weiba_id, int post_uid,
			String preStatus) throws ApiException;
	/**
	 *
	 * 赞帖子/取消赞帖子
	 * @param post_id 必须
	 * @param weiba_id 必须
	 * @param post_uid 必须
	 * @param preStatus 之前微吧的状态，传1表示之前已经赞，则取消赞，传0表示之前没有赞，则赞
	 */
	Object getChangePostDigg(int post_id, int weiba_id, int post_uid,
			String preStatus) throws ApiException;
	/**
	 * 贴吧的精华帖
	 * @param weiba_id  必须
	 * @param pageCount 每次取数目，可选，默认20条
	 * @param max_id 分页最后一条post_id
	 * @return
	 * @throws VerifyErrorException
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws ExceptionIllegalParameter
	 */
	ListData<SociaxItem> getPostDigest(int weiba_id, int pageCount, int max_ids, final HttpResponseListener listener)throws VerifyErrorException, ApiException, ListAreEmptyException, DataInvalidException, ExceptionIllegalParameter;
	
	/**
	 * 获取所有微吧
	 * @param pageCount  数目
	 * @param maxid  最后一条微吧的id
	 * @return
	 * @throws ApiException
	 */
	public ListData<SociaxItem> getAllWeibaList(int pageCount, int maxid, HttpResponseListener listener)throws ApiException;
	/**
	 * 搜索微吧
	 * @param pageCount  数目
	 * @param maxid  最后一条微吧的id
	 * @param key  关键字，必须
	 * @return
	 * @throws ApiException
	 */
	public ListData<SociaxItem> searchWeibaList(int pageCount, int maxid,String key)throws ApiException;

	/**
	 * 推荐帖子
	 * @param pageCount
	 * @param max_id
	 * @return
	 * @throws VerifyErrorException
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws ExceptionIllegalParameter
	 */
	ListData<SociaxItem> getRecommendTopic(int pageCount, int max_id, HttpResponseListener listener)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException, ExceptionIllegalParameter;
	/**
	 * 热门帖子
	 * @param pageCount
	 * @param max_id
	 * @return
	 * @throws VerifyErrorException
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws ExceptionIllegalParameter
	 */
	ListData<SociaxItem> getPostHot(int pageCount, int max_id)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException, ExceptionIllegalParameter;
	

	// 发布帖子
	public boolean cretePost(ModelPost posts) throws ApiException;
	/**
	 * 回复帖子
	 * @param comment
	 * @return
	 * @throws ApiException
	 * @throws VerifyErrorException
	 * @throws UpdateException
	 */
	ModelBackMessage replyPost(ModelComment comment) throws ApiException,
			VerifyErrorException, UpdateException;
	/**
	 * 回复评论
	 * @param comment
	 * @return
	 * @throws ApiException
	 * @throws VerifyErrorException
	 * @throws UpdateException
	 */
	ModelBackMessage replyComment(ModelComment comment,String comment_user) throws ApiException,
			VerifyErrorException, UpdateException;
	/**
	 * 发布带图片的帖子
	 * @param posts
	 * @param filelist
	 * @return
	 * @throws ApiException
	 * @throws VerifyErrorException
	 * @throws UpdateException
	 */
	ModelBackMessage createNewPostWithImage(ModelPost posts, FormFile[] filelist)
			throws ApiException, VerifyErrorException, UpdateException;
	/**
	 * 发布帖子
	 * @param posts
	 * @return
	 * @throws ApiException
	 */
	Object creteNewPost(ModelPost posts) throws ApiException;
	/**
	 * 逛一逛所有帖子
	 * @param weiba_id
	 * @param pageCount 取的数目
	 * @param max_id 最后一条post的id第一次传0
	 * @return
	 * @throws VerifyErrorException
	 * @throws ApiException
	 * @throws ListAreEmptyException
	 * @throws DataInvalidException
	 * @throws ExceptionIllegalParameter
	 */
	ListData<SociaxItem> getPostAll(int weiba_id, int pageCount, int max_id)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException, ExceptionIllegalParameter;

    String getWeiba(int id, final HttpResponseListener listener) throws ApiException;

	/**
	 * 删除帖子
	 * @param post_id 帖子id
	 * @param listener
	 * @return
	 * @throws ApiException
     */
	ModelBackMessage delPost(int post_id, final HttpResponseListener listener) throws ApiException;

    String getWeibaUrl(int id, final HttpResponseListener listener) throws ApiException;

	public ListData<SociaxItem> findWeiba(int pageCount, String categories, int maxid, int count, HttpResponseListener listener)throws ApiException;

	public ListData<SociaxItem> searchTopic(String key, HttpResponseListener listener)throws ApiException;
	public void favPost(ModelPost post, final HttpResponseListener listener) throws ApiException, JSONException;
}
