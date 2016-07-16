package com.thinksns.tschat.constant;

/**
 * Created by hedong on 15/12/5.
 */
public class TSConfig {
    public static final int CAMERA_IMAGE = 155;//相机拍照
    public final static int LOCAL_IMAGE = 156;  //本地图片
    public final static int ZOOM_IMAGE =157;    //裁剪图片

    //单聊
    public static final int CHAT_SINGLE = 0x01;
    //群聊
    public static final int CHAT_GROUP = 0x02;
    public static final int REQUEST_CODE_CAMERA = 107;// 照相
    public static final int REQUEST_CODE_LOCAL = 108;// 本地图片
    public static final int REQUEST_CODE_MAP = 109;// 地图
    public static final int REQUEST_CODE_SELECT_CARD = 110;// 本地文件
    public static final int SELECT_CARD =185;//选择名片
    public static final int UPLOAD_FILE = 111;// 上传文件

    //聊天发送图片的状态
    public static final String SEND_THE_PIC = "SEND";//刚发送
    public static final String HTTP_ERROR = "HTTP_ERROR";//http出错
    public static final String SOCKET_ERROR = "SOCKET_ERROR";//socket出错
    public static final String GOT_THE_PIC= "GOT_IT";//发送成功
    public static final String UPDATE_CHAT_LIST= "UPDATA_CHAT_LIST";//更新房间列表

    public static final int MSG_RECORDING_START = 1;
    public static final int MSG_RECORDING_STOP = 2;
    public static final int MSG_RECORDING_STATE_ERROR = 3;
    public static final int MSG_RECORDING_EXCEPTION = 4;
    public static final int MSG_RECORDING_RELEASE = 5;
    public final static int CHAT_GET_PIC_FROM_LOCAL = 212;//聊天发送本地图片
    public final static int WEIBO_GET_PIC_FROM_LOCAL = 213;//微博发送本地图片
    public final static int REQUEST_CHAT_CODE_LOCAL = 214;//聊天发送本地图片
    public final static int RESULT_CHAT_CODE_LOCAL = 216;//聊天发送本地图片

    public static final int SELECT_CHAT_USER = 133;// 选人区分被选人用于发起聊天
    public static final int CREATE_GROUP_CHAT = 134;// 创建聊天
    public static final int CHAT_CLEAR_HISTORY = 138;// 清理聊天历史记录
    public static final int CHAT_CLEAR_AND_DELETE = 139;// 删除并且退出
    public static final int CHANGE_CHAT_NAME = 140;// 修改聊天名字
    public static final int CHAT_ADD_USER = 141;// 添加群成员
    public static final int CHAT_DELETE_USER = 142;// 删除群成员

    // 照片存放地址
    public final static String CACHE_PATH = "thinksns/image_cache";
    public static final String VOICE_PATH = "thinksns/voice_cache";

}
