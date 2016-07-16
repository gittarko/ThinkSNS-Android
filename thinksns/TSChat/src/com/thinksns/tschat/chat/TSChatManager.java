package com.thinksns.tschat.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.thinksns.sociax.thinksnsbase.base.BaseApplication;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;
import com.thinksns.tschat.base.BaseListFragment;
import com.thinksns.tschat.bean.Entity;
import com.thinksns.tschat.bean.ListData;
import com.thinksns.tschat.bean.ModelChatMessage;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.bean.ModelMemberList;
import com.thinksns.tschat.bean.ModelUser;
import com.thinksns.tschat.bean.UserLogin;
import com.thinksns.tschat.constant.TSChat;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.db.SQLHelperChatMessage;
import com.thinksns.tschat.eventbus.SocketLoginEvent;
import com.thinksns.tschat.inter.ChatCoreResponseHandler;
import com.thinksns.tschat.inter.ChatRetryHandler;
import com.thinksns.tschat.inter.ResponseInterface;
import com.thinksns.tschat.listener.TSChatCallBack;
import com.thinksns.tschat.notify.MessageNotifier;
import com.thinksns.tschat.ui.ActivityChatDetail;
import com.thinksns.tschat.unit.FunctionCreateChat;
import com.thinksns.tschat.unit.TDevice;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dong.he on 15/12/1.
 * 聊天管理
 */
public class TSChatManager {
    private static final String TAG = TSChatManager.class.getSimpleName();
    private static final String OBSERVER = "observer";  //任务观察者
    public static final String LOGIN_TAG = "chat_login";

    public static final int INIT_DATA = 0x10;           //数据初始化
    public static final int CONNECT_SOCKET = 0x11;      //连接socket
    public static final int TRY_CONNECT = 0x12;         //重连socket
    public static final int CONNECT_OK = 0x13;          //连接socket成功
    public static final int PROCESS_DATA = 0x14;
    public static final int READY_SEND = 0x15;
    public static final int START_CONNECT = 0x20;
    public static final int PROCESS_MSG = 0x21;
    public static final int FINISH_MSG = 0x22;
    public static final int CONNECT_ERROR = 0x23;
    public static final int CONNECTING_SOCKET = 0x24;
    private static final int NOTIFY_NEW_MSG = 0x25;

    /****连接超时的最大时间****/
    private static final int MAX_CONNECT_TIME_OUT = 1000 * 10;

    //上下文环境
    private static BaseApplication mContext;
    private static TSChatManager instance;
    //待加入聊天的用户
    private static UserLogin loginUser;
    //socket
    private static ChatSocketClient socketClient;
    //本地消息数据库
    private static SQLHelperChatMessage chatDb;
    //此监听器用于外部初次登录聊天时的回调函数
    /**
     *
     * example:
     * TSChatManager.login(user, new SocketCallBack() {
     *     //已登录到服务器
            public void onSuccess(Object object) {

            }

            //与服务器断开
            public void onFailure(Object error) {

            }
     * });
     */
    private static TSChatCallBack.SocketCallBack loginCallBack;
    private static Handler workHandler;
    private HandlerThread workThread;     //工作线程
    private static Handler uiHandler;

    //是否已经登录上服务器
    private static boolean isLogin = false;
    //消息房间列表
    private static BaseListFragment roomAdapter;
    //当前聊天房间
    private static BaseListFragment chatAdapter;
    //当前聊天房间号
    private static int roomId = -1;
    //消息通知器
    private static MessageNotifier notifier;
    //未读消息计数
    private static Map<Integer, Integer> unreads;
    //当前任务请求队列
    private static Map<Object, ChatSocketRequest> requestMap;
    //任务执行线程池
    private static ExecutorService threadPool;

    //初始化聊天管理类
    public static void initialize(Context context) {
        if(instance == null) {
            synchronized (TSChatManager.class) {
                if(instance == null) {
                    instance = new TSChatManager(context);
                }
            }
        }
    }

    public static TSChatManager getInstance() {
        return instance;
    }

    /**
     * 初始化用于连接socket的工作线程
     */
    private void initThreadHandler() {
        workThread = new HandlerThread("TSChatManager");
        workThread.start();
        workHandler = new Handler(workThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case INIT_DATA:
                        initNotifer(mContext);
                        initUiHandler();
                        threadPool = getDefaultThreadPool();
                        requestMap = Collections.synchronizedMap(new WeakHashMap<Object, ChatSocketRequest>());
                        isLogin = false;
                        unreads = Collections.synchronizedMap(new WeakHashMap<Integer, Integer>());
                        break;
                    case CONNECT_SOCKET:
                        Object loginInfo = msg.obj;
                        if(loginInfo == null) {
                            uiHandler.sendMessage(uiHandler.obtainMessage(CONNECT_ERROR, "CONNECT ERROR: 缺少登录信息"));
                            return;
                        }
                        Log.v(TAG, "Beginning to connect socket...");
                        try {
                            //登录前需传递登录信息的参数
                            loginUser = invokeUser(loginInfo);
                            //初始化本地未读消息
                            initUnreadMsg();
                            //开始执行连接socket
                            connect(loginUser);
                        } catch (Exception e) {
                            e.printStackTrace();
                            uiHandler.sendMessage(uiHandler.obtainMessage(CONNECT_ERROR,
                                    "CONNECT ERROR: "+ e.toString()));
                        }
                        break;
                    case TRY_CONNECT:
                        //取消正在发送消息
                        uiHandler.removeMessages(CONNECTING_SOCKET);
                        Log.e(TAG, "Trying to connect socket...");
                        connect(loginUser);
                        break;
                    case CONNECT_OK:
                        uiHandler.removeMessages(CONNECTING_SOCKET);
                        //解析登录成功后的内容
                        ResponseParams response = (ResponseParams)msg.obj;
                        JSONObject json = response.getResponse();
                        try{
                            if (!TextUtils.isEmpty(json.getString("status"))) {
                                String status = json.getString("status");
                                if(status.equals("0")) {
                                    isLogin = true;
                                    //登录成功
                                    EventBus.getDefault().post(new SocketLoginEvent(SocketLoginEvent.LOGIN_STATUS.LOGIN_SUCCESS));
                                    //返回结果
                                    uiHandler.sendMessage(uiHandler.obtainMessage(CONNECT_OK, json.get("result")));
                                    return;
                                }else {
                                    uiHandler.sendMessage(uiHandler.obtainMessage(CONNECT_ERROR, "LOGIN ERROR: " + json.getString("msg")));
                                }
                            }else {
                                uiHandler.sendMessage(uiHandler.obtainMessage(CONNECT_ERROR, "SERVER ERROR: 服务端未返回正确状态码"));
                            }

                            EventBus.getDefault().post(new SocketLoginEvent(SocketLoginEvent.LOGIN_STATUS.LOGIN_ERROR));

                        }catch(JSONException e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new SocketLoginEvent(SocketLoginEvent.LOGIN_STATUS.LOGIN_ERROR));
                            uiHandler.sendMessage(uiHandler.obtainMessage(CONNECT_ERROR, "LOGIN ERROR: 解析服务端返回内容出错"));
                        }
                        break;
                    case READY_SEND:
                        TSChatManager.postSubmit((ChatSocketRequest)msg.obj, msg.arg1);
                        break;
                    case PROCESS_DATA:
                        postProcess((ResponseParams)msg.obj);
                        break;
                }
            }
        };

        //发送数据初始化消息
        workHandler.sendEmptyMessage(INIT_DATA);
    }

    //创建线程池
    private ExecutorService getDefaultThreadPool() {
        return Executors.newCachedThreadPool();
    }

    private void initUiHandler() {
        uiHandler = new Handler() {
            int time = 0;
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONNECT_OK:
                        Log.v(TAG, "SOCKET is connect ok....");
                        if(loginCallBack != null)
                            loginCallBack.onSuccess(msg.obj);
                        time = 0;
                        //重发队列中的失败消息
                        sendFailedMessage();
                        break;
                    case START_CONNECT:
                        //正在连接
                        Log.v(TAG, "SOCKET is start connect");
                        sendEmptyMessageDelayed(CONNECTING_SOCKET, 3000);
                        break;
                    case CONNECTING_SOCKET:
                        //这里可以加入一个socket连接超时时间
                        time += 3;
                        Log.v(TAG, "SOCKET is connecting, cost time " + time + "s");
                        if(time >= 10) {
                            //主动断开socket连接,socket连接超时目前最常见的因素是未与地址连接成功
                            Log.v(TAG, "SOCKET CONNECT TIME OUT");
                            if(loginCallBack != null) {
                                loginCallBack.onFailure("socket服务连接超时,请检查您的网络设置");
                            }
                            workHandler.removeMessages(TRY_CONNECT);
                            workHandler.removeMessages(CONNECT_SOCKET);
                            time = 0;
                            return;
                        }
                        sendEmptyMessageDelayed(CONNECTING_SOCKET, 3000);
                        break;
                    case CONNECT_ERROR:
                        if(msg.obj != null) {
                            Log.v(TAG, "SOCEKT CONNECT ERROR:" + msg.obj.toString());
                        }
                        time = 0;
                        break;
                    case NOTIFY_NEW_MSG:
                        notifier.onNewMesg((List<ModelChatUserList>)msg.obj);
                        break;
                }
            }
        };
    }

    private void sendFailedMessage() {
        Iterator<Map.Entry<Object, ChatSocketRequest>> entries = requestMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Object, ChatSocketRequest> entry = entries.next();
            ChatSocketRequest request = entry.getValue();
            if(!request.isDone()) {
                //没有结束或正在发送中
                continue;
            } else {
                postSubmit(request, 500);
            }
        }
    }

    //通过反射获取用户登录信息
    private UserLogin invokeUser(Object object) throws Exception {
        //获取用户id
        try {
            Method method = object.getClass().getMethod("getUid");
            int uid = (Integer)method.invoke(object);
            method = object.getClass().getMethod("getUserName");
            String uname = (String)method.invoke(object);
            method = object.getClass().getMethod("getToken");
            String token = (String)method.invoke(object);
            method = object.getClass().getMethod("getSecretToken");
            String token_secret = (String)method.invoke(object);
            method = object.getClass().getMethod("getUserface");
            String uface = (String)method.invoke(object);
            return new UserLogin(uid, uname, token, token_secret, uface);
        } catch (NoSuchMethodException e) {
            throw new Exception(e);
        } catch(IllegalAccessException e) {
            throw new Exception(e);
        } catch (InvocationTargetException e) {
            throw new Exception(e);
        }
    }

    //连接socket
    private void connect(UserLogin loginUser) {
        if(!UnitSociax.isNetWorkON(mContext)) {
            uiHandler.sendMessage(uiHandler.obtainMessage(CONNECT_ERROR, "CONNECT ERROR: 网络未连接,中断连接"));
            return;
        }

        if(socketClient != null && socketClient.isOpen()) {
            Log.v(TAG, "CONNECT SOCKET: one socket is opened");
            return;
        }

        isLogin = false;

        try {
            if (loginUser != null) {
//                socketClient = ChatSocketClient.getChatSocketClient(mContext.getSocketURI(),mContext,
//                        MAX_CONNECT_TIME_OUT);
                socketClient = new ChatSocketClient(mContext.getSocketURI(), mContext, MAX_CONNECT_TIME_OUT);
                socketClient.setUid(String.valueOf(loginUser.getUid()));
                socketClient.setToken(loginUser.getToken());
                socketClient.setTokenSecret(loginUser.getSecretToken());
//                uiHandler.sendEmptyMessage(START_CONNECT);
                socketClient.connect();
            } else {
                uiHandler.sendMessage(uiHandler.obtainMessage(CONNECT_ERROR,
                        "CONNECT ERROR: 用户信息为空"));
            }
        }catch(Exception e) {
            e.printStackTrace();
            uiHandler.removeMessages(CONNECTING_SOCKET);
            uiHandler.sendEmptyMessage(CONNECT_ERROR);
        }
    }

    private TSChatManager(Context context) {
        mContext = (BaseApplication)context.getApplicationContext();
        initThreadHandler();
    }

    //重新连接socket
    public static void retry(String source) {
        Log.v(TAG, "retry connect by " + source);
        retry(0);
    }

    /**
     * 重新启动socket连接
     * @param delay  延迟 delay s 执行
     */
    public static void retry(long delay) {
        workHandler.sendEmptyMessageDelayed(TRY_CONNECT,delay);
    }

    //启动socket，连接服务器
    public static void login(Object object){
        initLogin(object, null);
    }

    //带回调的登录方法
    public static void login(Object object, TSChatCallBack.SocketCallBack callBack) {
        initLogin(object, callBack);
    }

    /**
     * 初始化登录
     * @param object
     * @param callBack
     */
    private static void initLogin(Object object, TSChatCallBack.SocketCallBack callBack) {
        if(loginCallBack == null)
            loginCallBack = callBack;

        workHandler.sendMessage(workHandler.obtainMessage(CONNECT_SOCKET, object));
    }

    //保存任务请求
    private static void join(ChatSocketRequest request) {
        synchronized (requestMap) {
            Object tag = request.getRequestTag();
            if(tag == null) {
                //没有标识的请求默认设置为任务监观察者
                request.setRequestTag(OBSERVER);
            }

            requestMap.put(request.getRequestTag(), request);
        }
    }

    /**
     * 关闭socket连接
     */
    public static void close() {
        roomId = 0;
        //清空消息未读列表
        unreads.clear();
        //清空任务队列
        requestMap.clear();
        //退出聊天登录
        if(socketClient != null)
            socketClient.exit();
    }

    //用户是否已经登录聊天系统
    public static boolean isLogin() {
        return isLogin && socketClient != null && socketClient.isOpen();
    }

    //获取登录用户信息
    public static UserLogin getLoginUser() {
        return loginUser;
    }

    //消息发送请求是否存在
    public static boolean requestExist(ModelChatMessage message) {
        if(TextUtils.isEmpty(message.getPackid()))
            return false;
        return requestMap.get(message.getPackid()) == null ?  false : true;
    }

    //取消某个请求
    public static void cancelRequest(String tag) {
        synchronized (requestMap) {
            if (requestMap.get(tag) != null) {
                requestMap.remove(tag);
            }
        }
    }

    //处理socket返回的内容
    public static void sendResponse(ResponseParams response) {
        if(response.requestType == ResponseParams.GET_LOGIN) {
            workHandler.sendMessage(workHandler.obtainMessage(CONNECT_OK, response));
        }
        else {
            workHandler.sendMessage(workHandler.obtainMessage(PROCESS_DATA, response));
        }
    }

    //接收来自socket返回的内容
    private static void postProcess(ResponseParams message) {
        Log.v(TAG, "接收到socket信息....");

        ChatSocketRequest request = null;
        if(message.tag != null) {
            request = requestMap.get(message.tag);
        }

        if (request != null) {
            //处理带有令牌的消息,此种情况常用于客户端主动向服务端发起请求，服务端做出回应
            makeRequestTask(message, request.getResponseHandler(), 0);
        } else {
            //不带令牌的消息，此种情况多用于服务端主动推送给客户端消息
            int observerCount = 0;
            for (ChatSocketRequest requestOne : requestMap.values()) {
                //将消息推送给所有观察者
                if (requestOne.getRequestTag() != null
                        && requestOne.getRequestTag().equals(OBSERVER)) {
                    makeRequestTask(message, requestOne.getResponseHandler(), 0);
                    observerCount++;
                }
            }

            if (observerCount == 0) {
                //没有观察者则随意创建一个回调任务
                makeRequestTask(message, null, 0);
            }
        }
    }

    //通过令牌获取队列中的任务请求
    public static ChatSocketRequest getRequest(String tag) {
        synchronized (requestMap) {
            if(requestMap != null) {
                return requestMap.get(tag);
            }
            return null;
        }
    }

    //创建回调线程任务
    private static void makeRequestTask(ResponseParams message, ResponseInterface response, int delay) {
        ChatSocketRequest task = new ChatSocketRequest(message, response);
        if(message.requestType != ResponseParams.OBSERVER) {
            postSubmit(task, delay);
        }else {
            Log.e(TAG, "makeRequest-->observer");
        }
        join(task);
    }

    //延迟提交任务到队列中
    private static void postSubmit(final ChatSocketRequest task, int delay) {
        if(task != null) {
            workHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    threadPool.submit(task);
                }
            }, delay);
        }
    }

    //此方法用于需要对聊天返回的消息进行监听
    public static void register(ResponseInterface response) {
        makeRequestTask(null, response, 0);
    }

    //注册带令牌的监听
    public static void register(String tag, ResponseInterface responseInterface) {
        ResponseParams params = new ResponseParams(1, tag);
        params.requestType = ResponseParams.OBSERVER;
        post(params, responseInterface, 0);
    }

    //发起一个消息请求
    public static void post(ResponseParams params, ResponseInterface handler, int delay) {
        if(params != null) {
            if(params.tag == null)
                params.tag = (String.valueOf(System.currentTimeMillis()));
        }else {
            //默认给不携带参数的任务设置为观察者
            params = new ResponseParams(0, OBSERVER);
            params.requestType = ResponseParams.OBSERVER;
        }

        makeRequestTask(params, handler, delay);
    }

    //初始化房间列表
    public static void initRoom(BaseListFragment room) {
        roomAdapter = room;
    }

    //设置当前聊天房间ID
    public static void initChat(int room_id) {
        roomId = room_id;
    }


    public static int getCurrentChatRoom() {
        return roomId;
    }

    /**
     * 获取服务器消息房间里列表
     */
    public static void getRoomList(int roomId,int mtime, int count,
                                   final ChatCoreResponseHandler handler) {
        final ResponseParams params = new ResponseParams();
        params.put("type", "get_room_list");
        if(roomId == 0)
            params.put("room_id", "all");
        else
            params.put("room_id", roomId);
        params.put("mtime", mtime);
        params.put("limit", count);
        params.tag = "get_room_list";
        params.isSend = true;
        params.requestType = ResponseParams.GET_ROOM_LIST;
        if(Looper.myLooper() == Looper.getMainLooper()) {
            //如果当前环境是在主线程中直接执行任务提交
            TSChatManager.post(params, handler, 0);
        }else {
            //如果是在线程中通过handler发送到队列中执行
            workHandler.post(new Runnable() {
                @Override
                public void run() {
                    TSChatManager.post(params, handler, 0);
                }
            });
        }
    }


    /**
     * 根据房间号获取历史聊天记录
     */
    public static void getHistoryChatList(int room_id, int message_id, int count,
                                          final ChatCoreResponseHandler handler) {
        final ResponseParams params = new ResponseParams();
        params.put("room_id", room_id);
        params.put("message_id", message_id);
        params.put("limit", count);
        params.put("type", "get_message_list");
        params.tag = "get_message_list";
        params.isSend = true;
        params.requestType = ResponseParams.GET_MESSAGE_LIST;
        if(Looper.myLooper() == Looper.getMainLooper()) {
            TSChatManager.post(params, handler, 0);
        }else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    TSChatManager.post(params, handler, 0);
                }
            });
        }
    }

    //消息发送
    //增加回掉
    public static void sendMessage(ModelChatMessage message, ChatCoreResponseHandler handler, int delay) {
        ResponseParams params = new ResponseParams(1, message.getPackid());
        params.put("message", message);
        params.requestType = ResponseParams.SENDING_MSG;
        params.isSend = true;
        post(params, handler, delay);
    }

    /**
     * 创建聊天
     * @param room
     * @param handler
     */
    public static void createNewChat(ModelChatUserList room, ChatCoreResponseHandler handler) {
        String packid = String.valueOf(System.currentTimeMillis());
        ResponseParams params = new ResponseParams(1, packid);
        if(room.isSingle()) {
            //创建单聊
            params.put("type", "get_room");
            params.put("uid", room.getTo_uid());
        }else {
            params.put("type", "create_group_room");
            params.put("uid_list", room.getGroupId());
            params.put("title", room.getTitle());
        }

        params.put("packid", packid);
        params.put("room_info", room);
        params.isSend =  true;
        params.requestType = ResponseParams.CREATE_ROOM;
        post(params, handler, 0);

    }

    /**
     * Socket相关操作
     */

    /**
     * 选择创建聊天
     * @param context
     */
    public static void createChat(Context context) {
        Intent intent = new Intent(context, com.thinksns.tschat.ui.ActivitySelectUser.class);
        intent.putExtra("select_type", TSConfig.SELECT_CHAT_USER);
        context.startActivity(intent);
    }

    /**
     * 创建一对一聊天
     * @param uid
     */
    public static void createSingleChat(final int uid, final String name,
                                        final String face) {
        ModelUser user = new ModelUser();
        user.setUid(uid);
        user.setUserName(name);
        user.setFace(face);
        List<ModelUser> users = new ArrayList<ModelUser>();
        users.add(user);
        FunctionCreateChat fc = new FunctionCreateChat(users);
        fc.createChat(new ChatCoreResponseHandler() {
            @Override
            public void onSuccess(Object object) {
                Log.v(TAG, "CREATE ROOM--->onSuccess");
                //前往聊天详情
                Intent intent = new Intent(mContext, ActivityChatDetail.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ModelChatUserList chat = (ModelChatUserList)object;
                ActivityChatDetail.initChatInfo(chat);
                mContext.startActivity(intent);
            }

            @Override
            public void onFailure(Object object) {
                Log.v(TAG, "CREATE SINGE---->onFailure");
            }
        });
    }

    //没有历史记录存在以下情况
    // 1.首次使用软件没有主动从服务器获取，对方也没有离线消息发送过来
    //2.从服务器获取任然没有数据，说明与对方没有任何聊天记录
    //如果从服务器没有获取到历史记录，则标记为以后不用主动获取记录
    //标记格式为room_roomd_id_id,默认有历史纪录

    /**
     * 邀请成员加入聊天
     * @param room_id
     * @param uids
     * @param handler
     */
    public static void addMembers(int room_id, String uids, ChatCoreResponseHandler handler) {
        String packid = String.valueOf(System.currentTimeMillis());
        ResponseParams params = new ResponseParams(1, packid);
        params.put("type", "add_group_member");
        params.put("room_id", room_id);
        params.put("member_uids", uids.substring(0, uids.lastIndexOf(",")));
        params.put("packid", packid);
        params.requestType = ResponseParams.ADD_GROUP_MEMBER;
        params.isSend = true;
        post(params, handler, 0);
    }

    /**
     * 修改群聊房间名称
     * @param room
     */
    public static void changeRoomTitle(ModelChatUserList room, int type, ChatCoreResponseHandler handler) {
        String packid = String.valueOf(System.currentTimeMillis());
        ResponseParams params = new ResponseParams(1, packid);
        params.isSend = true;
        if(type == 2) {
            //修改头像
            params.requestType = ResponseParams.SET_ROOM_FACE;
            params.put("group_face", room.getGroupFace());
        }else {
            //修改标题
            params.requestType = ResponseParams.SET_ROOM_TITLE;
            params.put("logo", room.getLogoId());
        }

        params.put("type", "set_room");
        params.put("title", room.getTitle());
        params.put("room_id", room.getRoom_id());
        params.put("group_type", type);		//1：修改名称 2：修改头像 3标题和头像都修改了
        params.put("packid", packid);
        post(params, handler, 0);

    }

    /**
     * 删除群成员
     * @param room_id
     * @param uid
     * @param handler
     */
    public static void deleteMembers(int room_id, String uid, ChatCoreResponseHandler handler) {
        String packid = String.valueOf(System.currentTimeMillis());
        ResponseParams params = new ResponseParams(1, packid);
        params.requestType = ResponseParams.DEL_GROUP_MEMBER;
        params.put("type", "remove_group_member");
        params.put("room_id", room_id);
        params.put("member_uids", uid);
        params.put("packid", packid);
        params.isSend = true;
        post(params, handler, 0);
    }

    /**
     * 退出群聊
     * @param roomId
     * @param handler
     */
    public static void exitGroupChat(int roomId, ChatCoreResponseHandler handler) {
        String packid = String.valueOf(System.currentTimeMillis());
        ResponseParams params = new ResponseParams(1, packid);
        params.requestType = ResponseParams.QUIT_GROUP_ROOM;
        params.put("room_id", roomId);
        params.put("type", "quit_group_room");
        params.put("packid", packid);
        params.isSend = true;
        post(params, handler, 0);

    }


    //通知消息到通知栏
    public  void notifyNesMsg(List<ModelChatUserList> rooms) {
        Message msg = uiHandler.obtainMessage(NOTIFY_NEW_MSG, rooms);
        uiHandler.sendMessage(msg);
        sendUnreadsMsg();
    }

    public static void clearUnreadMsgNotify(int room_id, int count) {
        notifier.clearNotification(room_id, count);
    }

    public static void addUnreadCount(int roomId, int count) {
        unreads.put(roomId, count);
    }

    public static void addUnreadMsgCount(Integer room_id, int count) {
        if(count <= 0)
            return;
        unreads.put(room_id, count);
        sendUnreadsMsg();
    }

    public static boolean hasUnread(Integer room_id) {
        if(unreads == null)
            return false;
        if(unreads.get(room_id) != null) {
            return true;
        }

        return false;
    }

    /**
     * 清空本地未读消息
     * @param room_id
     */
    public static void sendClearUnreadMsg(int room_id, String clear_type, ResponseInterface handler) {
        String tag = String.valueOf(System.currentTimeMillis());
        ResponseParams params = new ResponseParams(1, tag);
        params.put("type", "clear_message");
        params.put("clear_type", clear_type);
        params.put("room_id", room_id);
        params.put("packid", tag);
        params.requestType = ResponseParams.SET_CLEAR_UNRADS;
        params.isSend = true;
        post(params, handler, 0);
    }

    //清空本地该房间的未读消息
    public static void clearUnreadMessage(int room_id, String type) {
        if(unreads.get(room_id) != null) {
            clearUnreadMsgNotify(room_id, unreads.get(room_id));
            unreads.remove(room_id);
        }else {

        }

        sendUnreadsMsg();

        if(type.equals("unread")) {
            //清空本地记录
            chatDb.clearRoomUnreadMsg(room_id);
        }else{
            chatDb.deleteMessageById(room_id);
        }
    }

    /**
     * 获取所有未读消息数
     * @return
     */
    public static int getUnreadMsg() {
        if (unreads == null)
            return 0;
        int unread = 0;
        for (Integer value : unreads.values()) {
                unread += value;
        }
        return unread;
    }

    //通知主线程未读消息
    public static void sendUnreadsMsg() {
        int unread = getUnreadMsg();
        Intent intent = new Intent();
        intent.setAction(TSChat.RECEIVE_NEW_MSG);
        intent.putExtra(TSChat.NEW_MSG_COUNT, unread);
        mContext.sendBroadcast(intent);
    }


    //查询本地未读消息
    private static void initUnreadMsg() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(chatDb == null) {
                    //创建消息数据库
                    chatDb = SQLHelperChatMessage.getInstance(mContext);
                }

                SQLHelperChatMessage.initData(loginUser.getUid());
                // 获取所有未读消息
                if(unreads == null)
                    unreads = new HashMap<Integer, Integer>();
                ArrayList<ModelChatUserList> chatList = SQLHelperChatMessage.getRoomList(0, 0);
                for (int i = 0, j = chatList.size(); i < j; i++) {
                    ModelChatUserList room = chatList.get(i);
                    if(room.getIsNew() > 0) {
                        unreads.put(room.getRoom_id(), room.getIsNew());
                    }
                }
                //通知UI更新未读消息数目
                sendUnreadsMsg();
            }
        };

        threadPool.submit(runnable);
    }

    private void initNotifer(Context context) {
        notifier = createNotifier();
        notifier.init(context);
        notifier.setNotificationInfoProvider(getNotificationListener());
    }


    public MessageNotifier.NotificationInfoProvider getNotificationListener() {
        return new MessageNotifier.NotificationInfoProvider() {
            @Override
            public String getDisplayedText(ModelChatUserList message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
//                String ticker = message.getContent();
//                if (message.getType().equals("text")) {
//                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
//                }
//                String username = "";// message.getFrom_uname()
//                return username + ": " + ticker;
                return null;
            }

            @Override
            public String getLatestText(ModelChatUserList message, int fromUsersNum, int messageNum) {
                return null;
            }

            @Override
            public String getTitle(ModelChatUserList message) {
                return null;
            }

            @Override
            public int getSmallIcon(ModelChatUserList message) {
                return 0;
            }

            @Override
            public Intent getLaunchIntent(ModelChatUserList message) {
                return null;
            }
        };
    }

    // 创建消息提示体
    public static MessageNotifier createNotifier() {
        return new MessageNotifier();
    }

    /**
     * 发送输入状态
     */
    public static void sendChatingState(int roomId, int to_uid, String tips, int status, ChatCoreResponseHandler handler) {
        ResponseParams params = new ResponseParams(1, "input_status");
        params.put("type", "input_status");
        params.put("room_id", roomId);	//房间ID
        params.put("to_uid", to_uid);	//要告知的对象
        params.put("status", status);	//当前输入状态 1：正在输入 0：放弃输入
        params.put("extend", tips);		//发送自定义提示内容
        params.isSend = true;
        params.requestType = ResponseParams.SET_INPUT_STATUS;
        post(params, handler, 0);
    }


}
