package com.thinksns.sociax.constant;

/**
 * 存放一些内部常量，通常用于hanlder回掉或者activity之间区分传值
 * 
 * @author wz
 * 
 */
public class AppConstant {
	public static final boolean isUseUMeng = false;
	public static final String APP_TAG = "Sociax";

	public static final int SHOW_USER = 10;// 查看用户
	public static final int REPLY_COMMENT = 11;// 回复某条评论
	public static final int DEL_COMMENT = 12;// 删除某条评论
	public static final int REPLY_MESSAGE = 13;// 回复聊天
	public static final int CREATE_MESSAGE = 14;// 创建聊天
	public static final int WEIBO = 15;// 微博
	public static final int COMMENT = 16; // 评论
	public static final int TRANSPOND = 17;// 转发
	public static final int FAVORITE = 18;// 收藏
	public static final int UNFAVORITE = 19;// 取消收藏
	public static final int DELETEWEIBO = 20;// 取消收藏
	public static final int GETWEIBO = 21;// 取消收藏
	public static final int DENOUNCE = 22;// 取消收藏

	public static final int CREATE_TEXT_WEIBO = 23;			// 发布普通文字微博
	public static final int CREATE_ALBUM_WEIBO = 26;
	public static final int CREATE_IMAGE_WEIBO = 24;	// 发布图片微博
	public static final int CREATE_VIDEO_WEIBO = 25;	// 发布视频微博
	public static final int CREATE_WEIBA_POST = 27;		//发布微吧帖子
	public static final int CREATE_TRANSPORT_WEIBO = 28;	//转发微博
	public static final int CREATE_TRANSPORT_POST = 29;		//转发帖子
	public static final int WEIBO_EDIT_TEXT_DRAFT = 30;		//编辑文字微博草稿
	public static final int WEIBO_EDIT_IMAGE_DRAFT = 31;	//编辑图片微博草稿
	public static final int WEIBO_EDIT_VIDEO_DRAFT = 32;	//编辑视频微博草稿
	public static final int WEIBO_EDIT_TRANSPORT_DRAFT = 33;	//编辑转发微博
	public static final int POST_EDIT_TRANSPORT_DRAFT = 34;		//编辑转发帖子
	public static final int CREATE_CHANNEL_WEIBO= 35;		//发布频道微博
	public static final int WEIBO_EDIT_CHANNEL_DRAFT = 36;	//编辑频道微博
	public static final int CREAT_TRANSPORT_CHANNEL = 37;	//发布转发频道微博
	public static final int CHANNEL_EDIT_TRANSPORT_DRAFT = 38;	//编辑转发频道微博
	public static final int CREATE_TOPIC_WEIBO = 39;		//发布话题微博
	// listhandler
	public static final int ADD_DIG = 26;// 列表添加赞
	public static final int DEL_DIG = 27;// 列表删除赞
	public static final int COMMENT_VISIBILITY = 28;// 列表修改评论可见
	public static final int CHANGE_WEIBO_DIGG = 29;// 修改微博详情赞
	public static final int CHANGE_WEIBO_FAVOURITE = 30;// 修改微博详情收藏
	public static final int GET_WEIBO_DIGG = 31;// 获取微博详情赞列表
	public static final int HAS_DIGG_USER = 32;// 有赞用户
	public static final int NO_DIGG_USER = 33;// 没有赞的用户
	public static final int COMMENT_SUCCESS = 34;// 评论成功
	public static final int CHANNEL_FOLLOW = 35;// 修改关注
	public static final int DELETE_COMMENT = 36;		//删除评论
	/**
	 * 特殊数据
	 */

	public static final String IMAGE_CACHE_DIR = "thumbs"; // 头像
	public static final String CONTET_IMAGE_CACHE_DIR = "cthumbs"; // 内容图片目录
	public static final int LIST_FOOT_VIEW_ID = 1111;// footerview的id
	public static final int LISTVIEW_PAGESIZE = 20;// listview每页数据数
	public static final int weiboLenght = 140;
	public static final int TRANSPOND_LAYOUT = 100;
	public static final int IMAGE_VIEW = 101;
	public static final int IMAGE_LAYOUT = 102;
	public static final int WEIBA_VIEW = 103;
}
