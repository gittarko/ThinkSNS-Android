//package com.thinksns.sociax.t4.android.chat;
//
//import android.app.Activity;
//import android.app.Fragment;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.net.ConnectivityManager;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//
//import com.thinksns.sociax.android.R;
//import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
//import com.thinksns.sociax.t4.android.Thinksns;
//import com.thinksns.sociax.t4.android.db.SQLHelperChatMessage;
//import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
//import com.thinksns.sociax.t4.model.ListData;
//import com.thinksns.sociax.t4.model.ModelUser;
//import com.thinksns.sociax.t4.model.SociaxItem;
//
//import org.json.JSONObject;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * Created by dong.he on 15/12/1.
// * 聊天管理
// */
//public class TSChatManager {
//    //上下文环境
//    private static Context mContext;
//
//    private static TSChatManager instance;
//
//    //待加入聊天的用户
//    private static ModelUser loginUser;
//
//    //聊天服务器地址
//    private static URI socketServer;
//
//    //socket
//    private static ChatSocketClient socketClient;
//
//    //本地消息数据库
//    private static SQLHelperChatMessage chatDb;
//
//    //socket监听器
//    private static ChatSocketClient.WebSocketConnectListener socketListener;
//
//    //socket是否连接成功
//    private static boolean isConnected = false;
//
//    //是否已经登录上服务器
//    private static boolean isLogin = false;
//
//    //消息房间列表
//    private static FragmentSociax roomAdapter;
//    //当前聊天房间
//    private static FragmentSociax chatAdapter;
//
//    private static int actionType = 0; //消息操作类型：0 房间列表 1：消息列表
//
//    private static Handler mHandler;    //维护一个与UI通信的handler
//
//    public static TSChatManager getInstance(Context context) {
//        if(instance == null) {
//            init(context);
//        }
//        return instance;
//    }
//
//    private TSChatManager(Context context) {
//        mContext = context;
//        //创建消息数据库
//        chatDb = SQLHelperChatMessage.getInstance(context);
//        //启动socket
//        String [] host = context.getResources().getStringArray(R.array.site_url);
//        try {
//            socketServer = new URI(host[2]);
//            socketClient = new ChatSocketClient(socketServer, context);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//
//        initHandler();
//        //初始化socket未连接，用户没有登录系统
//        isConnected = false;
//        isLogin = false;
//    }
//
//    //初始化数据
//    //用户登录配置、数据库简历、socket连接
//    public static void init(Context context) {
//        instance = new TSChatManager(context);
//    }
//
//    //创建与ui线程通信的handler
//    private void initHandler() {
//        mHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if(msg.what == 1) {
//                    //重连socket
//                    login(loginUser);
//                }else if(msg.what == 2) {
//
//                }
//            }
//        };
//    }
//
//    //启动socket，连接服务器
//    public static void login(final ModelUser user) {
//        if(isNetWorkOn()) {
//            if (loginUser == null && user == null) {
//                Log.e("TSChatManager", "当前没有登记用户信息");
//                return;
//            }else if(user != null) {
//                //重置用户登录信息
//                loginUser = user;
//            }
//            connect();
//        }else {
//            //未连接网络
//            Log.e("TSChatManager", "没有可用的网络连接");
//        }
//    }
//
//    //开启socket连接
//    private static void connect() {
//        if(socketClient == null) {
//            Log.e("TSChatManager", "当前未注册socket连接");
//            return;
//        }
//
//        if(loginUser == null) {
//            Log.e("TSChatManager", "当前未登记用户信息");
//            return;
//        }
//
//        if(socketClient != null && !socketClient.isClosed()) {
//            if(isConnected && isLogin) {
//                //当前已经与服务器连接并且已登录
//                return;
//            }else if(isConnected){
//                //已连接未登录
//                doLoginServer();
//                return;
//            }else {
//                socketClient.close();
//            }
//        }
//
//        //开启一个socket连接
//        socketClient = null;
//        socketClient = new ChatSocketClient(socketServer, mContext);
//
//        //添加连接监听器
//        if(socketListener == null) initListener();
//        socketClient.setWebSocketConnectListener(socketListener);
//        socketClient.connect();
//
//    }
//
//    private static void initListener() {
//        socketListener = new ChatSocketClient.WebSocketConnectListener() {
//            @Override
//            public void onConnected() {
//                //已经与服务器建立连接,开始登录
//                Log.e("TSChageManager", "socket is connected to server");
//                isConnected = true;
//                if(timer != null) {
//                    timer.cancel();
//                    timer = null;
//                }
//                doLoginServer();
//            }
//
//            @Override
//            public void onConnectError(String error) {
//                //连接失败,可能是本地网络原因，可能是服务器地址不正确
//                Log.e("TSChatManager", "socket is connect error:" + error);
//                isConnected = false;
//                //如果不是手动关闭socket执行重练
//                if(socketClient.isClosed()) {
//                    //1秒后重连
//                    mHandler.sendEmptyMessageDelayed(1, 2000);
//                }
//            }
//
//            @Override
//            public void onSocketClose(boolean auto) {
//                Log.e("TSChatManager", "socket is closed!");
////                if(timer != null) {
////                    timer.cancel();
////                    timer = null;
////                }
//
//                isConnected = false;
//                isLogin = false;
//                if(!auto) {
//                    //1秒后重连
//                    mHandler.sendEmptyMessageDelayed(1, 1000);
//                }
//            }
//
//            @Override
//            public void onSocketOpen() {
//                Log.e("TSChatManager", "socket is opened!");
//            }
//
//            @Override
//            public void onLoginSuccess() {
//                //登录成功
//                isLogin = true;
//                Log.e("TSChatManager", "user login success!");
//                //获取当前登录用户历史聊天记录或最新消息
//                //1.如果当前用户是首次使用软件，则从服务器获取所有历史聊天记录
//                //2.否则不主动获取，服务器会主动推送最新未读消息，历史纪录从本地获取即可
//                if(!haveMoreTime()) {
//                    //首次使用软件,成功获取历史纪录更新使用次数
//                    socketClient.getRoomList("all");
//                }
//            }
//
//            @Override
//            public void onLoginError(String error) {
//                //登录失败
//            }
//
//            @Override
//            public void onReceiveComplete(Object result) {
//                //主界面实现回掉更新UI,判断是更新房间还是更新聊天
//                if(result instanceof ArrayList) {
//                    if(actionType == 0 && roomAdapter != null) {
//                        //当前切换到房间列表页
////                        roomAdapter.executeDataSuccess((ListData<SociaxItem>)result);
//                        roomAdapter.doRefreshUpdata();
//                    }
//                    else if(actionType == 1 && chatAdapter != null) {
//                        //当前切换到消息详情页
//                        chatAdapter.executeDataSuccess((ListData<SociaxItem>)result);
//                        roomAdapter.doRefreshUpdata();
//                    }else {
//                        //处在其他页面
//                        if(roomAdapter != null)
//                            roomAdapter.doRefreshUpdata();
//                    }
//                }
//            }
//
//            @Override
//            public void onSendComplete() {
//
//            }
//
//            @Override
//            public void onReceiveError(Object object) {
//
//            }
//
//            @Override
//            public void onSendError(Object object) {
//
//            }
//        };
//
//    }
//
//    //当前登录用户是否第一次使用聊天系统
//    private static boolean haveMoreTime() {
//        SharedPreferences preferences = mContext.getSharedPreferences("tschat", mContext.MODE_PRIVATE);
//        int useCount = preferences.getInt("use", 0);
//        return useCount != 0;
//    }
//
//    //连接远程服务器
//    private static void doLoginServer() {
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("type", "login");
//        map.put("uid", loginUser.getUid() + "");
//        map.put("oauth_token", loginUser.getToken());
//        map.put("oauth_token_secret", loginUser.getSecretToken());
//        socketClient.send(new JSONObject(map).toString());
//    }
//
//    public static void initRoom(FragmentSociax room) {
//        roomAdapter = room;
//        //是否已经与服务器连接成功
//        if(isLogin && !haveMoreTime()) {
//            actionType = 0;
//            //是否是第一次使用
//            socketClient.setRoomAdapter(room.getAdapter());
//            socketClient.getRoomList("all");
//        }else if(isLogin) {
//            //socket登录完成
//        }
//        else if(socketClient == null || socketClient.isClosed()){
//            //socket未打开或者已关闭
//            connect();
//        }else {
//            //socket正在连接,完成连接以后发送广播通知
//            registerRecevier(room.getActivity(), "getAllHistory");
//        }
//    }
//
//    //注册广播
//    private static void registerRecevier(Activity activity, String s) {
//
//    }
//
//    //初始化聊天详情内容
//    public static void initChat(FragmentSociax chat, Activity activity) {
//        chatAdapter = chat;
//        socketClient.setActivity(activity);
//    }
//
//    /**
//     * 判断网络状态
//     *
//     * @return
//     */
//    public static boolean isNetWorkOn() {
//        boolean netSataus = false;
//        if(mContext != null) {
//            ConnectivityManager cwjManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (cwjManager != null && cwjManager.getActiveNetworkInfo() != null) {
//                netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
//            }
//        }else {
//            Log.e("TSChatManager", "当前未注册TSChat");
//        }
//
//        return netSataus;
//    }
//
//    /**
//     * 获取App安装包信息
//     *
//     * @return PackageInfo
//     */
//    public PackageInfo getPackageInfo() {
//        PackageInfo info = null;
//        try {
//            info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace(System.err);
//        }
//        if (info == null)
//            info = new PackageInfo();
//        return info;
//    }
//
//    //连接定时器
//    private static TimerTask task;
//    private static Timer timer;
//
//    public static void startTimer() {
//        // 如果前一个timer还在，需要先取消
//        if (timer != null) {
//            timer.cancel();
//        }
//
//        timer = new Timer();
//
//        task = new ReconnectTimerTask();
//        // 每10秒重新连接一下
//        timer.schedule(task, 0, 10000);
//
//    }
//
//    static class ReconnectTimerTask extends TimerTask {
//        @Override
//        public void run() {
//            if(socketClient.isConnecting()) {
//                //如果socket正在进行连接则不重试连接
//                Log.e("TSChatManager", "one socket have connecting!");
//                return;
//            }
//            Log.e("TSChatManager", "socket start connect!");
//            connect();
//        }
//    }
//
//    /**
//     * Socket相关操作
//     */
//
//    /**
//     * 获取当前登录用户历史聊天记录
//     * @param room  存放历史记录的消息列表
//     */
//    private static void getAllHistoryMessage(AdapterSociaxList room) {
//        socketClient.setRoomAdapter(room);
//        socketClient.getRoomList("all");
//    }
//
//    /**
//     * 根据房间号获取历史聊天记录
//     * @param room_id
//     * @param count
//     * @param id
//     */
//    public static void getHistoryChatList(int room_id, int count, int id) {
//        if(isLogin && !haveMoreChat(room_id, id)){
//            actionType = 1;
//            socketClient.getHistory(room_id, count, id);
//        }else if(isLogin) {
//            //已经与服务器完成数据获取，无需再次请求
//        }
//        else if(socketClient == null || socketClient.isClosed()) {
//            connect();
//        }else {
//            //
//        }
//    }
//
//    //没有历史记录存在以下情况
//    // 1.首次使用软件没有主动从服务器获取，对方也没有离线消息发送过来
//    //2.从服务器获取任然没有数据，说明与对方没有任何聊天记录
//    //如果从服务器没有获取到历史记录，则标记为以后不用主动获取记录
//    //标记格式为room_roomd_id_id
//    /**
//     *
//     * @param room_id  来自哪个房间的历史记录
//     * @param id 消息截止id
//     * @return
//     */
//    private static boolean haveMoreChat(int room_id, int id) {
//        SharedPreferences preferences = mContext.getSharedPreferences("tschat_detail", mContext.MODE_PRIVATE);
//        int useCount = preferences.getInt("room_" + room_id + "_" + id, 0);
//        return useCount != 0;
//    }
//
//}
