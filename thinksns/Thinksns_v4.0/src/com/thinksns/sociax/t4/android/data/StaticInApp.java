package com.thinksns.sociax.t4.android.data;

/**
 * 类说明： 应用内部使用变量 本方法内的变量尽量不要修改
 *
 * @author wz
 * @version 1.0
 * @date 2014-10-15
 */
public class StaticInApp {
    public final static String cache = "thinksns_cache";// 照片存放地址

    /*******
     * 聊天内标记
     ********/
    public final static int REQUEST_CODE_CAMERA = 107;// 照相
    public final static int REQUEST_CODE_LOCAL = 108;// 本地图片
    public static final int REQUEST_CODE_MAP = 109;// 地图
    public final static int REQUEST_CODE_SELECT_CARD = 110;// 本地文件

    public static final int UPLOAD_FILE = 111;// 上传文件

    public static final int CHANGE_LISTFOLLOW = 112;// 修改关注状态
    /***********
     * 找人
     *********/
    public static final int FINDPEOPLE_NEARBY = 113;// 附近的人
    public static final int FINDPEOPLE_TAG = 114;// 标签用户
    public static final int FINDPEOPLE_VERIFY = 115;// 官方验证用户
    public static final int FINDPEOPLE_AREA = 116;// 地区用户
    public static final int FINDPEOPLE_CONTACTS = 117;// 通讯录
    public static final int FINDPEOPLE_CITY = 118;// 根据城市
    public static final int FINDPEOPLE_KEY = 119;// 根据key搜索
    public static final int WEIBO_DIGG_LIST = 400;

    public static final int GET_TAG_LIST = 120;// 获取标签列表
    public static final int GET_CITY_LIST = 121;// 获取城市列表

    public static final int CHANGE_USER = 99;
    public static final int CHANGE_USER_NAME = 122;// 修改名字
    public static final int CHANGE_USER_CITY = 123;// 修改城市
    public static final int CHANGE_USER_INTRO = 124;// 修改用户简介
    public static final int CHANGE_USER_SEX = 204;// 修改用户性别
    public static final int GET_MY_SCORE = 125;// 我的积分

    public static final int GET_AREA_LIST = 126;// 获取地区列表
    public static final int SHOW_USER = 127;// 展示用户信息
    public static final int REMOVE_BLACKLIST = 128;// 修改黑名单
    public static final int EXCHARGE_GIFT = 129;// 兑换自己的礼物为积分
    public static final int SEND_GIFT = 130;// 赠送礼物
    public static final int CHANGE_TASKSTATUS = 131;// 修改任务状态

    public static final String STOPVIDEOINTENT = "zhishisoft.stopvideo";
    public static final String ACTION_UP_INTENT = "zhishisoft.action.up";
    public static final String STOPVIDEOBYOTHERSINTENT = "zhishisoft.stopvideobyothers";
    public static final String RESUMEVIDEOBYOTHERSINTENT = "zhishisoft.resumevideobyothers";

    public static final String SERVICE_NEW_NOTIFICATION = "com.zhishisort.sociax.t4.service.ServiceUnReadMessage";// ServiceUnReadMessage广播通知的action

    public static final int SELECT_GIFT_RECEIVER = 132;// 选人区分被选人用于接收礼物
    public static final int SELECT_CHAT_USER = 133;// 选人区分被选人用于发起聊天

    public static final int CREATE_GROUP_CHAT = 134;// 创建聊天

    public static final int CHAT_SIMPLE = 135;// 单人聊天
    public static final int CHAT_GROUP = 136;// 群组聊天

    public static final String SERVICE_NEW_MESSAGE = "com.zhishisort.sociax.t4.service.chatsocketcilent.newmessage";// 新的聊天消息通知

    public static final int CHAT_INFO = 137;// 聊天详情

    public static final int CHAT_CLEAR_HISTORY = 138;// 清理聊天历史记录

    public static final int CHAT_CLEAR_AND_DELETE = 139;// 删除并且退出

    public static final int CHANGE_CHAT_NAME = 140;// 修改聊天名字

    public static final int CHAT_ADD_USER = 141;// 添加群成员

    public static final int CHAT_DELETE_USER = 142;// 删除群成员

    public static final int GET_PAKAGE_INFO = 143;//获取应用信息

    public static final int GET_USER_BIND = 144;//获取用户绑定情况

    public static final int BIND_OTHER_QQ = 145;//QQ绑定
    public static final int BIND_OTHER_WEICHAT = 146;//微信绑定
    public static final int BIND_OTHER_SINA = 147;//新浪微博绑定
    public static final int BIND_OTHER_PHONE = 148;//手机绑定

    public static final int UNBIND_OTHER_QQ = 149;//QQ解除绑定
    public static final int UNBIND_OTHER_WEICHAT = 150;//微信解除绑定
    public static final int UNBIND_OTHER_SINA = 151;//新浪微博解除绑定
    public static final int UNBIND_OTHER_PHONE = 152;//手机解除绑定

    public static final int DO_THIRD_LOGIN = 151;//第三方登录
    public static final int GET_THIRD_REG_INFO = 152;//获取第三方登录信息
    public static final int DO_THIRD_SHARE = 153;//第三方分享
    public static final int DO_THIRD_BIND = 154;//第三方绑定
    public static final int CAMERA_IMAGE = 155;//相机拍照
    public final static int LOCAL_IMAGE = 156;//本地图片
    public final static int ZOOM_IMAGE = 157;//裁剪图片


    public static final int CHANGE_WEIBA_FOLLOW = 158;//微吧修改关注
    public static final int GET_POST_DETAIL = 159;//微吧详情
    public static final int CHANGE_POST_FAVOURITE = 160;//修改帖子收藏
    public static final int POST_DIGEST = 161;//精华帖
    public static final int WEIBA_FIND = 162;//发现微吧，注意区别搜索微吧WEIBA_SEARCH
    public static final int WEIBA_SEARCH = 163;//搜索微吧

    public static final int BLOG_LIST = 164;//日志列表
    public static final int BLOG_SEARCH = 165;//日志搜索
    public static final int GET_BLOG_DETAIL = 166;//日志详情

    public static final int WEIBA_ALL = 167;//所有微吧
    public static final int POST_HOT = 168;//热门帖子
    public static final int UPLOAD_WEIBO = 169;//上传微博
    public static final int CHANGE_USER_PWD = 170;//修改密码
    public static final int CHANGE_USERINFO_FOLLOW = 171;//个人主页修改关注
    public static final int CHANGE_CHANNEL_FOLLOW = 172;//修改频道关注
    public static final int FINDPEOPLE_TOPLIST = 173;//找人风云榜
    public static final int CREATE_POST = 174;//创建帖子
    public static final int POST_TRANSPORT = 175;//转发帖子
    public static final int POST_COMMENT = 176;//评论帖子
    public static final int CHANGE_WEIBO_DIGG = 177;//微博添加赞
    public static final int GET_FEEDBACK_TYPE = 178;//获取反馈类型

    public static final int WEIBO_EDIT_DRAFT = 179;//编辑草稿
    public static final int POST_ALL = 180;//逛一逛所有帖子
    public static final int SELECT_GIFT_RESEND = 181;//转赠
    public static final int RESEND_GIFT = 182;//转赠动作
    public static final int DOWN_LOAD_ATTACH = 183;//下载附件
    public static final int SAVE_LOAD_ATTACH = 184;//保存附件

    public static final int SELECT_CARD = 185;//选择名片
    public static final int INTENT_TO_DETAIL_SINGLE = 186;//单聊跳转到聊天详情页
    public static final int INTENT_TO_DETAIL_GROUP = 187;//群聊跳转到聊天详情页
    public static final int UPDATE_MSG = 188;        //刷新消息
    public static final int UPLOAD_PIC = 188;//上传图片
    public static final int SHOW_ABOUT_US = 189;//关于我们
    public static final int UPDATE_FRAGMENT_MY = 190;//个人中心
    public static final int GET_ALL_MEDALS = 191;//所有勋章
    public static final int GET_MY_MEDAL = 192;//用户勋章
    public static final int GET_DAILY_TASK = 193;//每日任务
    public static final int GET_MAIN_TASK = 194;//主线任务
    public static final int GET_COPY_TASK = 195;//副本任务
    public static final int GET_COMMENT_DETAIL = 196;//获取评论详情
    public static final int GET_GIFT_DETAIL = 197;//获取礼物详情
    public static final int EXCHANGE_NOW = 198;//立即兑换
    public static final int GET_USER_FRIENDS_LIST = 199;//获取用户好友列表
    public static final int TRANSFER_SCORE = 200;//转让积分
    public final static int REQUEST_CODE_SELET_GIFT_RECEIVER = 200;// 选择礼物接收者
    public final static int RESULT_CODE_SELET_GIFT_RECEIVER = 201;// 选择礼物接收者
    public final static int TRANSFER_GIFT = 202;// 转赠礼物
    public final static int WEIBA_COMMENT_REPLY = 203;// 微吧评论
    public final static int CREATE_CHARGE = 204;//创建订单
    public final static int SAVE_CHARGE = 205;// 设置订单状态
    public final static int CHANGE_MY_TAG = 206;// 修改我的标签
    public final static int TAG_SELECT = 207;//选择标签
    public final static int GET_ALL_TAG = 208;//所有标签
    public final static int GET_MY_TAG = 209;//我的标签
    public final static int ADD_MY_TAG = 210;//添加标签
    public final static int DEL_MY_TAG = 211;//删除标签
    public final static int CHAT_GET_PIC_FROM_LOCAL = 212;//聊天发送本地图片
    public final static int WEIBO_GET_PIC_FROM_LOCAL = 213;//微博发送本地图片
    public final static int REQUEST_CHAT_CODE_LOCAL = 214;//聊天发送本地图片
    public final static int RESULT_CHAT_CODE_LOCAL = 216;//聊天发送本地图片
    public final static int GET_FRIENDS_EACHOTHER = 217;//获取互相关注的人
    public final static int CONTACTS_LIST_FRIENDS = 218;//联系人列表之好友
    public final static int CONTACTS_LIST_CHAT = 219;//联系人列表之聊天
    public final static int CONTACTS_LIST_FIND = 220;//联系人列表之找人
    public final static int IMG_TO_BITMAP = 221;//将图片转换为bitmap
    public final static int GET_SERVICE_IMG_WH = 222;//获取网络图片的宽高
    public final static int HEADER_GET_PIC_FROM_LOCAL = 300;
    public final static int CHANGE_BUTTOM_POST_DETAIL_DIG = 301;//修改帖子详情点赞的UI
    public final static int CHANGE_BUTTOM_POST_DETAIL_DIG_NOT_OK = 302;//修改帖子详情点赞的UI失败
    public final static int FROM_INFORMATION = 303;//从资讯列表页跳转而来
    public static final String NOTIFY_FRIEND_WEIBO = "notify_friend_weibo";//更新朋友圈微博
    public static final String NOTIFY_ALL_WEIBO = "notify_all_weibo";//更新所有微博
    public static final String NOTIFY_AT_ME_WEIBO = "notify_at_me_weibo";//更新与我相关微博
    public static final String NOTIFY_RECOMMEND_WEIBO = "notify_recommend_weibo";//更新推荐的微博
    public static final String NOTIFY_WEIBO = "notify_weibo";//更新微博
    public static final String NOTIFY_DRAFT = "notify_draft";//更新草稿
    public static final String NOTIFY_CREATE_WEIBO = "create_new_weibo";    //发布了新微博
    public static final String UPDATE_SINGLE_WEIBO = "update_single_weibo"; //更新单条微博
    public static final String NOTIFY_FOLLOW_USER = "anxiniuhui.update_follow_user";     //更新用户关注状态
    public static final String GET_VEDIO_URL = "get_vedio_url";//获取视频链接

    public static final String DEL_ROOM = "del_room";//删除房间
    public static final String UPDATE_SCORE_DETAIL = "update_score_detail";//更新积分详情
    public static final String MEMBERS_UIDS = "members_uids";//群聊成员id
    public static final String UPDATE_FOLLOW_COUNT = "update_follow_count";//更新个人主页粉丝或者关注的数量
    public static final String UPDATE_CHAT_LIST = "update_chat_list";//更新房间列表页
    public static final String TRANSFER_GIFT_OK = "transfer_gift_ok";//转赠礼物
    public static final String UPDATE_WEIBA_DETAIL = "update_weiba_detail";//微吧详情刷新
    public static final String CREATE_NEW_WEIBA_COMMENT = "weiba_new_comment";//微吧新评论
    public static final String UPDATE_UNREAD_MSG = "update_unread_msg";//更新未读消息

    public static final String TAG_CLOUD = "tag_cloud";//标签
    public static final String TAG_CLOUD_MINE = "tag_cloud_mine";//标签

    public static final String HAS_NEW_CHAT_INFO = "com.zhishisort.sociax.t4.service.chat.newmessage.has";//新的聊天信息
    public static final String NO_NEW_CHAT_INFO = "com.zhishisort.sociax.t4.service.chat.newmessage.no";//没有新的聊天信息

    public static final String PREFERENCES_NAME = "room_id";
    public static final String PRE_UNREAD_MESSAGE = "preferences_of_unread_message";

    public static final String UPDATE_USER_HOME_TAG = "update_user_home_tag";//更新个人主页tag信息

    //聊天发送图片的状态
    public static final String SEND_THE_PIC = "SEND";//刚发送
    public static final String HTTP_ERROR = "HTTP_ERROR";//http出错
    public static final String SOCKET_ERROR = "SOCKET_ERROR";//socket出错
    public static final String GOT_THE_PIC = "GOT_IT";//发送成功

    //微吧删除发送广播更新界面
    public static final String UPDATA_WEIBA = "update_weiba";

    //消息未读数类型
    public static final int UNREAD_WEIBA = 0x100;
    public static final int UNREAD_FOLLOW = 0x101;
    public static final int UNREAD_COMMENT = 0x102;
    public static final int UNREAD_DIGG = 0x103;

    //微博操作类型
    /**关注/取消关注微博发布人***/
    public static final int UPDATE_FOLLOW_USER = 0x104;
    /***点赞/取消点赞微博****/
    public static final int UPDATE_DIG_WEIBO = 0x105;
    /***删除微博****/
    public static final int DELETE_WEIBO = 0x106;

}
