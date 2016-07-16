package com.thinksns.tschat.chat;

import android.content.Context;
import android.net.Uri;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.thinksnsbase.utils.Bimp;
import com.thinksns.sociax.thinksnsbase.utils.FormFile;
import com.thinksns.sociax.thinksnsbase.utils.FormPost;
import com.thinksns.tschat.api.MessageApi;
import com.thinksns.tschat.api.RequestResponseHandler;
import com.thinksns.tschat.bean.Entity;
import com.thinksns.tschat.bean.ListData;
import com.thinksns.tschat.bean.ModelChatMessage;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.bean.ModelMemberList;
import com.thinksns.tschat.bean.UserLogin;
import com.thinksns.tschat.constant.TSChat;
import com.thinksns.tschat.db.SQLHelperChatMessage;
import com.thinksns.tschat.eventbus.SocketLoginEvent;
import com.thinksns.tschat.fragment.FragmentChatDetail;
import com.thinksns.tschat.inter.ChatRetryHandler;
import com.thinksns.tschat.inter.ResponseInterface;
import com.thinksns.tschat.ui.ActivityChatDetail;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hedong on 16/3/15.
 */
//socket消息处理类
public class ChatSocketRequest implements Runnable {
    private static final String TAG = ChatSocketRequest.class.getSimpleName();
    private static final int DEFAULT_MAX_RETRIES = 10;
    private static final int DEFAULT_RETRY_SLEEPTIME = 2000;


    private int executionCount = 0;
    protected ResponseInterface responseHandler;
    protected ResponseParams params;    //发送参数或返回的消息结果
    protected static Object lock = new Object();

    private final AtomicBoolean isCancelled = new AtomicBoolean();
    private volatile boolean isFinished;

    private enum MSG_OPERATION {
        SEND_SUCCESS, SOCKET_ERROR, FILE_ERROR, IO_ERROR, SERVER_ERROR, SEND_ERROR, DATA_ERROR
    }

    private MSG_OPERATION operationType = MSG_OPERATION.SEND_SUCCESS;

    public ChatSocketRequest(ResponseParams params, ResponseInterface responseHandler) {
        this.responseHandler = responseHandler;
        this.params = params;
        setRequestTag(params.tag);
    }

    //设置请求标识
     public void setRequestTag(Object tag) {
        if(this.responseHandler != null) {
            this.responseHandler.setTag(tag);
        }
     }

    public Object getRequestTag() {
        if(this.responseHandler == null)
            return null;
        return this.responseHandler.getTag();
    }

    public ResponseParams getParams() {
        return params;
    }

    public void setParams(ResponseParams params) {
        this.params = params;
    }

    public void setResponseHandler(ResponseInterface responseHandler) {
        this.responseHandler = responseHandler;
    }

    public ResponseInterface getResponseHandler() {
        return responseHandler;
    }

    @Override
    public void run() {
        //如果取消直接返回
        if(isCancelled()) {
            return;
        }

        if (params.isSend) {
            sendChatMessage();
        } else {
            responseChatMessage();
        }
    }

    private void responseChatMessage() {
        Log.v(TAG, "RECEIVE MESSAGE-->message tag is " + params.tag);
        if(params.status == 0) {
                //消息发送失败
                if (responseHandler != null) {
                    //服务端反馈的操作失败
                    responseHandler.sendFailureMessage("消息发送失败");
                }
            return;
        }

        int requestType = getRequestType();
        switch (requestType) {
            case ResponseParams.GET_ROOM_LIST:
                doGetRoomList(params.getResponse());
                TSChatManager.cancelRequest(params.tag);
                break;
            case ResponseParams.GET_MESSAGE_LIST:
            case ResponseParams.PUSH_MESSAGE:
                doPushMessage(params.getResponse());
                break;
            case ResponseParams.SET_CLEAR_UNRADS:
                doClearUnreads();
                break;
            case ResponseParams.SET_INPUT_STATUS:
                doInputStatus();
                break;
            case ResponseParams.SENDING_MSG:
                doSendMessage();
                break;
            case ResponseParams.SET_ROOM_FACE:
                try {
                    JSONObject json = params.getResponse();
                    int logoId = json.getInt("logo");
                    //更新房间头像
                    String room_id = params.getString("room_id");
                    SQLHelperChatMessage.updateRoomLogo(room_id, logoId);
                    if(responseHandler != null) {
                        responseHandler.sendSuccessMessage(logoId);
                    }
                }catch(JSONException e) {
                    e.printStackTrace();
                }
                TSChatManager.cancelRequest(params.tag);
                break;
            case ResponseParams.SET_ROOM_TITLE:
                if(responseHandler != null) {
                    responseHandler.sendSuccessMessage("名称设置成功");
                }
                break;
            case ResponseParams.ADD_GROUP_MEMBER:
                if(responseHandler != null)
                    responseHandler.sendSuccessMessage("添加成员成功");
                break;
            case ResponseParams.DEL_GROUP_MEMBER:
                if(responseHandler != null)
                    responseHandler.sendSuccessMessage("删除成员成功");
                break;
            case ResponseParams.QUIT_GROUP_ROOM:
                try{
                    JSONObject jsonObject = params.getResponse();
                    if(jsonObject.getInt("status") == 1) {
                        //退出操作成功
                        if(responseHandler != null) {
                            responseHandler.sendSuccessMessage("退出房间成功");
                        }
                    }else {
                        if(responseHandler != null) {
                            responseHandler.sendSuccessMessage("退出房间失败");
                        }
                    }
                }catch(JSONException e) {
                    e.printStackTrace();
                    if(responseHandler != null) {
                        responseHandler.sendSuccessMessage("数据解析错误");
                    }
                }
                break;
            case ResponseParams.CREATE_ROOM:
                doCreateRoom();
                break;
        }
    }

    private void doCreateRoom() {
        ModelChatUserList room = (ModelChatUserList)params.getObject("room_info");
        try {
            JSONObject resultObject = params.getResponse();
            int mtime = resultObject.getInt("mtime");
            int room_id = resultObject.getInt("room_id");
            if(room.isSingle()) {
//                int to_uid = resultObject.getInt("to_uid");
            }else {
                //群主id
                int master_uid = resultObject.getInt("master_uid");
                //群聊成员个数
                int member_num = resultObject.getInt("member_num");
                room.setMaster_uid(master_uid);
                room.setMember_num(member_num);
                room.setContent(TSChatManager.getLoginUser().getUserName() + "创建了房间");
            }

            room.setRoom_id(room_id);
            room.setMtime(mtime);
            if(responseHandler != null) {
                responseHandler.sendSuccessMessage(room);
            }

            //如果本地没有房间记录则插入一条
            SQLHelperChatMessage.addRoomToRoomList(room, room.getRoom_id());
                //通知UI更新
//                EventBus.getDefault().post(new ArrayList<ModelChatUserList>().add(room));
            return;
        }catch(JSONException e) {
            e.printStackTrace();
        }

        if(responseHandler != null) {
            responseHandler.sendFailureMessage("数据解析错误");
        }
    }

    //消息发送
    private void sendChatMessage() {

        Log.v(TAG, "SEND MESSAGE-->message tag is " + params.tag);
        if(isCancelled()) {
            return;
        }

        responseHandler.sendStartMessage("消息准备发送");
        Exception call = null;

        ModelChatMessage message = (ModelChatMessage) params.getObject("message");
        if(message != null) {
            //更新房间列表最后一条消息内容
            ModelChatUserList room = message.getCurrentRoom();//SQLHelperChatMessage.getRoommById(message.getRoom_id());
            if (room != null) {
//                room.setMtime(message.getMtime());
//                room.setContent(message.getContent());
//                room.setLastMessage(message);
                SQLHelperChatMessage.updateRoomContent(room);
//                EventBus.getDefault().post(room);
            }
            //记录本条消息至本地
            SQLHelperChatMessage.addChatMessagetoChatList(message, message.getMessage_id());
        }

        if(isCancelled())
            return;
        try{
            readySendMessage();
        }catch(Exception e) {
            call = e;
        }

        if(isCancelled())
            return;

        if(message != null) {
            if(operationType == MSG_OPERATION.DATA_ERROR) {
                //消息发送失败
                message.setSendState(ModelChatMessage.SEND_STATE.SEND_ERROR);
                //从队列中移除
                TSChatManager.cancelRequest(params.tag);
            }
            //保存当前发送失败的消息至本地
            SQLHelperChatMessage.addChatMessagetoChatList(message, message.getMessage_id());

        }

        if(call != null) {
            if(!isCancelled()) {
                responseHandler.sendFailureMessage(message);
            }else {
                Log.v(TAG, "ready send message error");
            }
            call.printStackTrace();
        }

        responseHandler.sendFinishMessage("任务执行完成");

        if(isCancelled())
            return;

        isFinished = true;

    }

    private void readySendMessage() throws Exception{
        boolean retry = true;
        Exception call = null;
        try {
            while (retry) {
                try {
                    makeRequest();
                    return;
                }catch(IOException e) {
                    call = new Exception(e.toString());
                    //文件，网络连接错误
                    retry = retryRequest(++executionCount);
                }catch(NotYetConnectedException e) {
                    call = new Exception("Socket未连接");
                    //socket断开
                    retry = retryRequest(++executionCount);
                }catch(NullPointerException e) {
                    call = new Exception(e.toString());
                    if(isCancelled())
                        return;
                    retry = retryRequest(++executionCount);
                }
                catch(JSONException e) {
                    //服务器返回的内容解析错误
                    call = new Exception(e.toString());
                    retry = false;
                    operationType = MSG_OPERATION.DATA_ERROR;
                }

                Object data = params.getObject("message");
                if(retry && data != null) {
                    //只对正常的消息重新发送更新状态
                    Log.v(TAG, "retry send message count " + executionCount);
                    if(executionCount == 1) {
                        //只发送一次失败消息，目的是为了更新UI的进度状态
                        ModelChatMessage message = (ModelChatMessage)params.getObject("message");
                        message.setSendState(ModelChatMessage.SEND_STATE.SENDING);
                        responseHandler.sendFailureMessage(message);
                    }
                }
//                else {
//                    TSChatManager.cancelRequest(params.tag);
//                }

            }
        }catch(Exception e) {
            call = e;
        }

        throw (call);

    }

    private boolean retryRequest(int executeCount) {
        boolean retry = true;
        if(executeCount > DEFAULT_MAX_RETRIES) {
            retry = false;
        }

        if(retry) {
            SystemClock.sleep(DEFAULT_RETRY_SLEEPTIME);
        }

        return retry;
    }

    private void makeRequest() throws NotYetConnectedException, JSONException, IOException{
        if(isCancelled())
            return;

        int requestType = getRequestType();
            switch (requestType) {
                case ResponseParams.GET_ROOM_LIST:
                    //获取房间列表
                    ChatSocketClient.ChatSocketClientSingle.sendMessage(params.chatParams);
                    break;
                case ResponseParams.PUSH_MESSAGE:
                case ResponseParams.GET_MESSAGE_LIST:
                    //获取聊天房间消息记录
                    ChatSocketClient.ChatSocketClientSingle.sendMessage(params.chatParams);
                    break;
                case ResponseParams.SET_CLEAR_UNRADS:
                    //标记服务端消息已读
                    ChatSocketClient.ChatSocketClientSingle.sendMessage(params.chatParams);
                    break;
                case ResponseParams.SET_INPUT_STATUS:
                    //发送输入状态消息
                    ChatSocketClient.ChatSocketClientSingle.sendMessage(params.chatParams);
                    break;
                case ResponseParams.SENDING_MSG:
                    sendMessageByType();
                    break;
                case ResponseParams.SET_ROOM_TITLE:
                    //修改群组标题
                    ChatSocketClient.ChatSocketClientSingle.sendMessage(params.chatParams);
                    break;
                case ResponseParams.SET_ROOM_FACE:
                    //修改群组头像
                    sendGroupFace();
                    break;
                case ResponseParams.ADD_GROUP_MEMBER:
                    ChatSocketClient.ChatSocketClientSingle.sendMessage(params.chatParams);
                    break;
                case ResponseParams.DEL_GROUP_MEMBER:
                    ChatSocketClient.ChatSocketClientSingle.sendMessage(params.chatParams);
                    break;
                case ResponseParams.QUIT_GROUP_ROOM:
                    ChatSocketClient.ChatSocketClientSingle.sendMessage(params.chatParams);
                    break;
                case ResponseParams.CREATE_ROOM:
                    ChatSocketClient.ChatSocketClientSingle.sendMessage(params.chatParams);
                    break;
            }
    }

    private void sendMessageByType() throws IOException, NotYetConnectedException, JSONException {
        ModelChatMessage message = (ModelChatMessage) params.getObject("message");
        boolean isOk = false;
        String result = null;
        switch (message.getMsgType()) {
            case IMAGE:
                result = uploadFile(message, message.getLocalPath(),
                        "uploadImage");
                break;
            case POSITION:
                result = uploadFile(message, message.getAttach_url(),
                        "uploadImage");
                break;
            case VOICE:
                result = uploadFile(message, message.getAttach_url(),
                        "uploadVoice");
                break;
            default:
                isOk = true;
                break;
        }

        //解析上传结果
        if (result != null) {
            JSONObject json = new JSONObject(result);
            if (json.getInt("status") == 1) {
                String attach_id = (String) json.getJSONArray("list").get(0);
                if (!TextUtils.isEmpty(attach_id)) {
                    message.setAttach_id(attach_id);
                    isOk = true;
                }
            }else {
                operationType = MSG_OPERATION.DATA_ERROR;
            }

        } else {
            operationType = MSG_OPERATION.IO_ERROR;
        }

        //如果文件上传成功进行socket发送消息到聊天服务器
        if (isOk) {
            //如果socket连接失败尝试等待重连之后继续发送
            ChatSocketClient.ChatSocketClientSingle.sendMes(message);
        }
    }

    public boolean isCancelled() {
        boolean cancelled = isCancelled.get();
        return cancelled;
    }

    public boolean isDone() {
        return isCancelled() || isFinished;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        isCancelled.set(true);
        return isCancelled();
    }

    //发送群组头像
    private void sendGroupFace() throws IOException, JSONException, NotYetConnectedException{
        String result = uploadFile(Integer.parseInt(params.getString("room_id")),
                    params.getString("group_face"), "uploadGroupLogo", false);
        if(result != null) {
            JSONObject json = new JSONObject(result);
            if (json.getInt("status") == 1) {
                String logo = json.getString("logo");
                params.put("logo", logo);
                ChatSocketClient.ChatSocketClientSingle.sendMessage(params.chatParams);
                if(responseHandler != null) {
                    responseHandler.sendStartMessage("开始发送消息");
                }
                return;
            }
        }

    }

    /**
     * 上传文件
     * @param path      文件路径
     * @param act       接口标识
     * @return
     */
    private String uploadFile(ModelChatMessage message, String path, String act) throws IOException {
        Uri.Builder uri = ApiHttpClient.createUrlBuild("app", "Message", act);
        File file = new File(path);

        FormFile formFile = null;
        if(message.getMsgType() == ModelChatMessage.MSG_TYPE.IMAGE
                || message.getMsgType() == ModelChatMessage.MSG_TYPE.POSITION) {
            formFile = new FormFile(Bimp.getInputStreamFromLocal(path, message.isOriginal()),
                    file.getName(), "audio", "application/octet-stream");
        }else {
            formFile = new FormFile(new FileInputStream(path),
                    file.getName(), "audio", "application/octet-stream");
        }

        Map<String, String> param = new HashMap<String, String>();
        param.put("list_id", String.valueOf(message.getRoom_id()));
        param.put("from", "Android App");
        return FormPost.post(uri.toString(), param, formFile);
    }

    private String uploadFile(int list_id, String path, String act, boolean isOriginal) throws IOException {
        Uri.Builder uri = ApiHttpClient.createUrlBuild("app", "Message", act);
        File file = new File(path);

        FormFile formFile = new FormFile(Bimp.getInputStreamFromLocal(path, isOriginal),
                    file.getName(), "audio", "application/octet-stream");
        Map<String, String> param = new HashMap<String, String>();
        param.put("list_id", String.valueOf(list_id));
        param.put("from", "Android App");
        return FormPost.post(uri.toString(), param, formFile);
    }

    private void doSendMessage() {
        try {
            JSONObject resultObject = params.getResponse();
            ModelChatMessage message = (ModelChatMessage)params.getObject("message");
            //发送成功
            int message_id = resultObject.getInt("message_id");
            int mtime = resultObject.getInt("mtime");
            message.setMessage_id(message_id);
            message.setMtime(mtime);
            message.setSendState(4);       //发送成功
//            ModelChatUserList room = message.getCurrentRoom();
//            if(room == null) {
//                //更新消息列表
//                room = new ModelChatUserList();
//                room.setRoom_id(message.getRoom_id());
//            }

//            room.setContent(message.getContent());
//            room.setMtime(mtime);
//            room.setLastMessage(message);
            //更新发送中的消息状态
            SQLHelperChatMessage.updateUnSendMessage(message);
//            SQLHelperChatMessage.updateRoomContent(room);
            //更新房间列表
            if(responseHandler != null) {
                responseHandler.sendSuccessMessage(message);
            }

            //更新房间列表
//            EventBus.getDefault().post(room);
            //更新聊天详情单条消息
            EventBus.getDefault().post(message);

        }catch(JSONException e) {
            e.printStackTrace();
        }
    }

    //发送输入状态
    private void doInputStatus() {
        JSONObject jsonObject = params.getResponse();
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            boolean input_status = result.getBoolean("status");
            String extend = result.getString("extend");
            if(!input_status) {
                extend = "";
            }
            if (responseHandler != null) {
                responseHandler.sendSuccessMessage(extend);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            responseHandler.sendFailureMessage("数据解析错误");
        }

    }

    //清除消息记录
    private void doClearUnreads() {
        try {
            JSONObject json = params.getResponse();
            if (json.getInt("status") == 0) {
                if (responseHandler != null)
                    responseHandler.sendSuccessMessage(json);
            } else {
                responseHandler.sendFailureMessage(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            responseHandler.sendFailureMessage("数据解析错误");
        }
    }

    //获取历史聊天用户列表
    private void doGetRoomList(JSONObject jsonObject) {
        try {
            List<ModelChatUserList> roomList = new ArrayList<ModelChatUserList>();
            String room_list = jsonObject.getJSONObject("result").getString("list");
            if (TextUtils.isEmpty(room_list)) {
                if(responseHandler != null) {
                    responseHandler.sendSuccessMessage(roomList);
                }
                return;
            }

            //消息任务接收完成
            if(responseHandler != null) {
                responseHandler.sendFinishMessage(jsonObject);
            }

            JSONArray list = new JSONArray(room_list);
            int size = list.length();
            for (int i = 0; i < size; i++) {
                JSONObject item = list.getJSONObject(i);
                int mtime = item.getInt("mtime");
                int room_id = item.getInt("room_id");
                ModelChatUserList room = SQLHelperChatMessage.getRoommById(room_id);
                if(room == null) {
                    room = new ModelChatUserList(item);
                }else if(room.getMtime() == mtime) {
                    //最后一条消息时间一样,基本说明该房间没有内容改变
                    room.setData(item);
                }else {
                    getEarlyMsg(room);
                }

                List<ModelMemberList> memberList = room.getMemList();
                // 单聊
                if (room.isSingle()) {
                    // 我在单聊列表的第一个，列表需要的是对方的信息
                    int index = room.getSelf_index() == 1 ? 0 : 1;
                    int uid = memberList.get(index).getUid();
                    String uname = memberList.get(index).getUname();
                    //设置未读消息
                    int messageNew = memberList.get(room.getSelf_index()).getMessage_new();
                    room.setIsNew(messageNew);
                    //记录聊天对象
                    room.setTo_uid(uid);
                    room.setTo_name(uname);
                } else {
                    //获取群组成员头像
                    //查找登录用户所在位置的MessageNew
                    ModelMemberList member = getMemberInfo(memberList, TSChatManager.getLoginUser().getUid());
                    room.setIsNew(member.getMessage_new());
                }

                long update = SQLHelperChatMessage.addRoomToRoomList(room, room.getRoom_id());
                roomList.add(room);
                TSChatManager.addUnreadCount(room.getRoom_id(), room.getIsNew());

            }

            //更新UI未读消息
            TSChatManager.sendUnreadsMsg();
            if (responseHandler != null) {
                responseHandler.sendSuccessMessage(roomList);
            }else {
                //通过EventBus通知UI更新
                EventBus.getDefault().post(roomList);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(jsonObject);
            }
        }

        TSChatManager.cancelRequest(params.tag);
    }

    //获取房间更早消息
    private void getEarlyMsg(final ModelChatUserList room) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //查询本地该房间的最后一条消息，如果比服务器的消息早则从服务器获取最新的消息
                ModelChatMessage message = SQLHelperChatMessage.getLastMessageInRoom(room.getRoom_id());
                if(message != null && message.getMessage_id() < room.getLastMsgId()) {
                    //获取聊天房间消息记录
                    Log.v(TAG, "GET HISTORY MESSAGE IN ROOM：" + room.getRoom_id() + ", content:" + room.getContent());
                    //默认获取更早之前的100条记录
                    TSChatManager.getHistoryChatList(room.getRoom_id(), room.getLastMsgId(), 100, null);
                }
            }
        }).start();

    }

    //获取成员信息
    private ModelMemberList getMemberInfo(List<ModelMemberList> memberList, int uid) {
        if(memberList == null || memberList.size() == 0)
            return null;
        for(ModelMemberList member : memberList)
        {
            if(member.getUid() == uid)
                return member;
        }
        return null;
    }

    //获取新房间信息
    private void getRoomInfo(final int room_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.v(TAG, "GET ROOM INFO:" + room_id);
                    ChatSocketClient.ChatSocketClientSingle.getRoomInfo(room_id, String.valueOf(System.currentTimeMillis()));
                }catch (Exception e) {
                    e.printStackTrace();
                    if(responseHandler != null)
                        responseHandler.sendFailureMessage("socket未连接");
                }
            }
        }).start();
    }
    //处理服务端主动推送过来的消息
    private void doPushMessage(JSONObject jsonObject) {
        try {
            JSONObject resultObject = jsonObject.optJSONObject("result");
                int list_length = resultObject.getInt("length");
                if (list_length == 0) {
                    if (responseHandler != null)
                        responseHandler.sendSuccessMessage(new ListData<ModelChatMessage>());
                    return;
                }

                int status = jsonObject.optInt("status");
                if (status != 0) {
                    if (responseHandler != null)
                        responseHandler.sendFailureMessage(resultObject);
                    return;
                }

                //服务端返回的消息列表
//            String type = jsonObject.getString("type");
                List<ModelChatUserList> roomList = new ArrayList<ModelChatUserList>(); //其他房间的消息
                JSONArray listArray = resultObject.optJSONArray("list");
                ListData<ModelChatMessage> chatList = new ListData<ModelChatMessage>(); //当前房间的消息
                //移除消息，用于给服务器反馈表示已经收到此消息
                String message_ids = "";
                for (int i = 0; i < list_length; i++) {
                    JSONObject obj = listArray.optJSONObject(i);
                    //创建消息列表
                    ModelChatMessage msg = ModelChatMessage.createMessageBody(obj);
                    if(i != list_length -1)
                        message_ids += msg.getMessage_id() + ",";
                    else
                        message_ids += msg.getMessage_id();

                    // 消息是否是当前聊天对象发出
                    boolean isChating = (msg.getRoom_id() == TSChatManager.getCurrentChatRoom());
                    ModelChatUserList room = SQLHelperChatMessage.getRoommById(msg.getRoom_id());
                    boolean roomExist = false;
                    // 创建房间
                    if (room == null) {
                        room = new ModelChatUserList();
                        room.setRoom_id(msg.getRoom_id());
                    } else {
                        roomExist = true;
                    }

                room.setLastMessage(msg);
                room.setContent(msg.getContent());
                room.setMtime(msg.getMtime());

                int group_action = 0;
                String notify_type = msg.getNotify_type();
                if (!TextUtils.isEmpty(notify_type)) {
                    group_action = dealGroupNotify(notify_type, room, msg, obj);
                }

                if (isChating) {
                    chatList.add(msg);
                } else {
                    msg.setIsNew(1);
                    room.setIsNew(room.getIsNew() + 1);
//                  //加入新消息列表
                    roomList.add(room);
                    TSChatManager.addUnreadCount(room.getRoom_id(), room.getIsNew());
                }

                // 更新消息列表
                if (group_action != 3) {
                    //如果不是创建群组的消息,更新房间列表
                    // 保存房间列表记录
                    SQLHelperChatMessage.addRoomToRoomList(room, msg.getRoom_id());
                    if (!roomExist) {
                        getRoomInfo(room.getRoom_id());
                    }
                }

                //保存消息
                SQLHelperChatMessage.addChatMessagetoChatList(msg, msg.getMessage_id());
            }

            //刷新当天聊天列表
            if (chatList.size() > 0) {
                if (responseHandler != null) {
                    responseHandler.sendSuccessMessage(chatList);
                }
            }
            //发送通知给外部主线程
            if (roomList.size() > 0) {
                //服务端推送消息
                EventBus.getDefault().post(roomList);
                TSChatManager.getInstance().notifyNesMsg(roomList);
            }

            //通知UI更新未读消息数
            TSChatManager.sendUnreadsMsg();

            //移除消息
            try {
                ChatSocketClient.ChatSocketClientSingle.removeMsg(message_ids);
            }catch(Exception e) {
                e.printStackTrace();
            }

        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    //任务执行的类型
    private int dealGroupNotify(String notify_type, ModelChatUserList room, ModelChatMessage msg, JSONObject obj) {
        int group_action = 0; // 群动态操作类型：1：增加成员 2：删除成员
        if (notify_type.equals("add_group_member")) {
            group_action = 1;
        } else if (notify_type.equals("remove_group_member")) {
            group_action = 2;
        } else if (notify_type.equals("create_group_room")) {
            group_action = 3;
        } else if (notify_type.equals("set_room")) {
            //更改了房间名称
            group_action = 4;
        } else if (notify_type.equals("quit_group_room")) {
            group_action = 5;
        }
        try {
            // 添加群成员
            if ((group_action == 1 || group_action == 2)) {
                //添加群成员或删除群成员
                //成员列表
                JSONArray memberArray = obj.getJSONArray("member_list");
                for (int j = 0; j < memberArray.length(); j++) {
                    JSONObject memberObject = memberArray.getJSONObject(j);
                    int change_uid = memberObject.optInt("uid");
                    String change_uname = memberObject.optString("uname");
                    if (group_action == 1) {
                        //添加群成员
                        msg.setRoom_add_uid(change_uid);
                        msg.setRoom_add_uname(change_uname);
                    } else if (group_action == 2) {
                        //删除群成员
                        msg.setRoom_del_uid(change_uid);
                        msg.setRoom_del_uname(change_uname);
                    }
                }
            }
            // 设置群信息
            else if (group_action == 4) {
                JSONObject room_infoObj = obj.getJSONObject("room_info");
                //群聊房间名称
                String title = room_infoObj.getString("title");
                msg.setRoom_title(title);
                msg.setFrom_uname(obj.getString("from_uname"));
                if (room_infoObj.has("group_type")) {
                    int action_type = room_infoObj.getInt("group_type");
                    if (action_type == 1) {
                        msg.setDescription("修改群标题为" + title);
                    } else if (action_type == 2) {
                        msg.setDescription("修改了群头像");
                    }
                }
                room.setTitle(title);
            }
            // 退出群房间
            else if (group_action == 5) {
                //退出成员id
                int quit_uid = obj.getInt("quit_uid");
                String quit_uname = obj.getString("quit_uname");
                msg.setQuit_uid(quit_uid);
                msg.setQuit_uname(quit_uname);
            }
            // 创建群房间
            else if (group_action == 3) {
                room.setFrom_uname(obj.optString("from_uname"));
                room.setFrom_uid(msg.getFrom_uid());
//			room.setTitle("");
                // 群主名称，在刚创建房间时服务器推送的房间创建信息里有用
                msg.setMaster_uname(obj.optString("from_uname"));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return group_action;
    }

    protected int getRequestType() {
        return params.requestType;
    }

    //回调返回内容,该方法在线程中执行
//    public void sendResponse(ResponseParams response) {
//        if(responseHandler != null) {
//            if(response == null) {
//                responseHandler.sendFailureMessage("没有返回内容可以处理");
//            }else if(response.status == 0) {
//                responseHandler.sendFailureMessage(response.data[0]);
//            }else if(response.status == 1) {
//                responseHandler.sendSuccessMessage(response.data[0]);
//            }
//        }
//    }

    //发送空消息
    public void sendEmptyMessage() {
        if(responseHandler != null)
            responseHandler.sendFailureMessage("无可用回调数据");
    }
}
