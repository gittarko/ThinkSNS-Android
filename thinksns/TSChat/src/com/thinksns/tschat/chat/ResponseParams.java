package com.thinksns.tschat.chat;

import com.thinksns.tschat.bean.Entity;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hedong on 16/3/15.
 * 对消息操作的内容进行管理
 * 规定每条操作请求携带一个令牌tag，当处理请求返回的结果时用该令牌去查找监听器
 */
public class ResponseParams implements Serializable{
    public static final int TRY_LOGIN = -1;     //连接聊天服务器
    public static final int GET_LOGIN = -2;     //登录回调
    public static final int GET_ROOM_LIST = 0;  //获取历史房间列表
    public static final int GET_ROOM_INFO = 1;  //获取单个房间信息
    public static final int PUSH_MESSAGE = 2;
    public static final int GET_MESSAGE_LIST = 3;
    public static final int SET_ROOM_TITLE = 4; //修改群组房间名称
    public static final int SET_ROOM_FACE = 5;  //设置群组头像
    public static final int SET_CLEAR_UNRADS = 6;   //清除未读消息
    public static final int SET_INPUT_STATUS = 7;   //发送输入状态
    public static final int SENDING_MSG = 8;        //发送普通消息
    public static final int ADD_GROUP_MEMBER = 9;
    public static final int DEL_GROUP_MEMBER = 10;
    public static final int QUIT_GROUP_ROOM = 11;   //退出房间
    public static final int CREATE_ROOM = 12;       //创建房间

    public static final int OBSERVER = 100;     //观察者

    public String tag = ""; //令牌
    public int status = 0;  //0-任务执行失败 1-任务执行成功
    public int requestType; //任务执行的类型
    public boolean isSend = false;  //任务是发送还是接收

    public ResponseParams(int status, String tag) {
        this.status = status;
        this.tag = tag;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    protected final static String LOG_TAG = "ResponseParams";

    protected final ConcurrentHashMap<String, String> chatParams = new ConcurrentHashMap<String, String>();
    protected final ConcurrentHashMap<String, Object> chatParamsWithObjects = new ConcurrentHashMap<String, Object>();
    private WeakReference<JSONObject> response = new WeakReference<JSONObject>(null);

    public ResponseParams() {
        this((Map<String, String>)null);
    }

    public ResponseParams(Map<String, String> source) {
        if (source != null) {
            for (Map.Entry<String, String> entry : source.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void put(String key, String value) {
        if(key != null && value != null) {
            chatParams.put(key, value);
        }
    }

    public void put(String key, Object value) {
        if(key != null && value != null) {
            chatParamsWithObjects.put(key, value);
        }
    }

    public void put(String key, int value) {
        if(key != null) {
            chatParams.put(key, String.valueOf(value));
        }
    }

    public void put(String key, long value) {
        if (key != null) {
            chatParams.put(key, String.valueOf(value));
        }
    }

    public void putResponse(JSONObject json){
        this.response = new WeakReference<JSONObject>(json);
    }

    public JSONObject getResponse() {
        return this.response.get();
    }

    public void add(String key, String value) {
        if (key != null && value != null) {
            Object params = chatParamsWithObjects.get(key);
            if (params == null) {
                params = new HashSet<String>();
                this.put(key, params);
            }
            if (params instanceof List) {
                ((List<Object>) params).add(value);
            } else if (params instanceof Set) {
                ((Set<Object>) params).add(value);
            }
        }
    }

    public void remove(String key) {
        chatParamsWithObjects.remove(key);
        chatParams.remove(key);
    }

    public boolean has(String key) {
        return chatParams.get(key) != null
                || chatParamsWithObjects.get(key) != null;
    }

    public Object getObject(String key) {
        return chatParamsWithObjects.get(key);
    }

    public String getString(String key) {
        return chatParams.get(key);
    }

}
