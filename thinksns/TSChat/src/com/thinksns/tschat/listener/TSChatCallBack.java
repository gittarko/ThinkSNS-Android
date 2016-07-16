package com.thinksns.tschat.listener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hedong on 16/3/14.
 * ThinkSNS聊天管理器监听接口
 */
public class TSChatCallBack {

    private static Map<String, SocketCallBack> socketCallBackMap;

    static {
        socketCallBackMap = new HashMap<String, SocketCallBack>();
    }

    //保存监听
    public void join(SocketCallBack callBack) {
        //产生一个唯一随机数
    }

    //取出监听
    public SocketCallBack find(String packid) {
        return socketCallBackMap.get(packid);
    }

    //监听聊天连接状态
    public interface SocketCallBack {
        //已登录到服务器
        public void onSuccess(Object object);

        //与服务器断开
        public void onFailure(Object error);

    }

    //创建聊天监听
    public interface CreateCallBack {
        public void onCreateSuccess();

        public void onCreateFailure();
    }

    public interface ChatCallBack {
        //未读消息
        public void unreadMessageCount(int count);
    }
}
