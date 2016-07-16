package com.thinksns.tschat.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.thinksns.tschat.base.BaseListFragment;
import com.thinksns.tschat.base.ListBaseAdapter;
import com.thinksns.tschat.bean.Entity;
import com.thinksns.tschat.bean.ListData;
import com.thinksns.tschat.bean.ModelChatMessage;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.db.SQLHelperChatMessage;
import com.thinksns.tschat.eventbus.SocketLoginEvent;
import com.thinksns.tschat.ui.ActivityChatDetail;
import com.thinksns.tschat.unit.Bimp;
import com.thinksns.tschat.unit.Downloader;
import com.thinksns.tschat.unit.TDevice;
import com.thinksns.tschat.unit.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 类说明： 聊天客户端socket
 *
 * @author ZhiShi
 * @date 2014-10-15
 * @version 1.0
 */
public class ChatSocketClient extends WebSocketClient {

	private static final String TAG = "ChatSocketClient";
	public final static String cache = "thinksns_cache";		// 照片存放地址
	/**默认连接超时时间**/
	private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

	public static final String PREFERENCES_NAME = "room_id";
	public static final String PRE_UNREAD_MESSAGE = "preferences_of_unread_message";
	public static final int INTENT_TO_DETAIL_GROUP = 187;		//群聊跳转到聊天详情页
	public static final String DEL_ROOM = "del_room";//删除房间
	public static final int INTENT_TO_DETAIL_SINGLE =186;//单聊跳转到聊天详情页
	public static final int UPDATE_MSG = 188;		//刷新消息
	public static final int NOTIFY_MSG = 189;		//系统通知消息
	public static final int CLEAR_MSG = 190;
	public static final int QUIT_ROOM = 120;			//退出房间

	public static final String GOT_THE_PIC="GOT_IT";		//发送成功
	public static final String SOCKET_ERROR="SOCKET_ERROR";	//socket出错
	public final static int GET_SERVICE_IMG_WH = 222;	//获取网络图片的宽高
	public static final int DOWN_LOAD_ATTACH =183;//下载附件
	public static final int SAVE_LOAD_ATTACH =184;//保存附件
	public static final String UPDATE_CHAT_LIST ="update_chat_list";//更新房间列表页
	public final static int IMG_TO_BITMAP = 221;//将图片转换为bitmap
	public final static int MSG_SEND_UPATE = 222;	//消息发送成功后更新
	public final static int CREATE_CHAT_RESPONSE = 223;

	public static ChatSocketClient ChatSocketClientSingle;
	private SQLHelperChatMessage msgSqlHelper;
	private Context mContext;

	private static ListBaseAdapter msgAdapter;
	private static ListBaseAdapter roomAdapter;

	private Activity chatActivity; 					// 当进入聊天详情的时候，设置聊天activity为当前activity，用于退出聊天
	private static BaseListFragment roomFragment;          //消息房间列表
	private static BaseListFragment chatFragment;          //当前聊天房间列表
	private static int currentRoomId = 0;					//当前聊天房间id

	private String mName = null;
	private String mUid = null;
	private String mRoom_id = null;
	private String mToken = null;
	private String mTokenSecret = null;

	private long rowId = -1;
	private static DownAttachHandler downAttachHandler;
	private static IntentHandler intentHandler;

	private HashMap<String, ModelChatMessage> sendList;	//记录正在发送的消息
	private HashMap<String, TSChatListener> listeners;
	private HashMap<Integer, TSChatListener> getRoomListeners;	//获取房间信息监听器
	private boolean pingFirst = true;		//第一收到服务端发来的ping消息
	boolean isStopByCheckOut = false;		// 判断是否用户退出登陆或者退出app导致的close

	/****连接状态查看定时器*****/
	private static final int DEFAULT_CHECK_TIME = 10 * 1000;
	private TimerTask mConnTask;
	private Timer mConnTimer;

	//通用socket回调接口
	public interface TSChatListener {
		public void onSuccess(Object result);

		public void onError(String error);
	}


	public static ChatSocketClient getInstance() {
		return ChatSocketClientSingle;
	}

	public static ChatSocketClient getChatSocketClient(URI serverURI,Context mContext,
													   int time_out) {
		if (ChatSocketClientSingle == null) {
			synchronized (ChatSocketClient.class) {
				if(ChatSocketClientSingle == null)
					ChatSocketClientSingle = new ChatSocketClient(serverURI, mContext, time_out);
			}
		}

		return ChatSocketClientSingle;
	}


	/***新创建Socket方法****/
	/**
	 * @param serverURI		聊天地址
	 * @param context		环境变量
	 * @param time_out		连接服务端的超时时间，默认为{DEFAULT_CONNECT_TIMEOUT}
	 */
	public static ChatSocketClient newInstance(URI serverURI, Context context, int time_out) {
		//如果已经存在一个socket则关闭
		if(ChatSocketClientSingle != null)	ChatSocketClientSingle.exit();
		ChatSocketClient socketClient = new ChatSocketClient(serverURI, context, time_out);
		return socketClient;
	}

	public ChatSocketClient(URI serverURI, Context context, int time_out) {
		super(serverURI, new Draft_17(), null, time_out==0 ? DEFAULT_CONNECT_TIMEOUT : time_out);
		this.mContext = context;
		if(ChatSocketClientSingle != null)	ChatSocketClientSingle.exit();
		ChatSocketClientSingle = this;
		msgSqlHelper = SQLHelperChatMessage.getInstance(context);
		downAttachHandler = new DownAttachHandler();
		intentHandler = new IntentHandler();
		listeners = new HashMap<String, TSChatListener>();
	}

	public String getUid() {
		return mUid;
	}

	public void setUid(String mUid) {
		this.mUid = mUid;
		msgSqlHelper.setUid(Integer.parseInt(mUid));
	}

	public String getRoom_id() {
		return mRoom_id;
	}

	public void setRoom_id(String mRoom_id) {
		this.mRoom_id = mRoom_id;
	}

	public String getToken() {
		return mToken;
	}

	public void setToken(String mToken) {
		this.mToken = mToken;
	}

	public String getTokenSecret() {
		return mTokenSecret;
	}

	public void setTokenSecret(String mTokenSecret) {
		this.mTokenSecret = mTokenSecret;
	}

	/**
	 * 心跳包
	 * @return
	 */
	@SuppressWarnings("unused")
	private Object responseJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", "pong");
		return new JSONObject(map);
	}

	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		Log.d(TAG, "onClose--->" + arg1);
		ChatSocketClientSingle = null;
//		if(arg1.contains("Host is unresolved")) {
//			//未能与聊天服务器连接成功
//			isStopByCheckOut = true;
//		}

		if (!isStopByCheckOut) {
			//2s后重连socket
			pingFirst = true;
			TSChatManager.retry(2000);
		} else {
			Log.d(TAG, "onClose--->stop TSChatSocket");
		}
	}

	//主动关闭socket
	public void exit() {
		isStopByCheckOut = true;
		close();
	}

	@Override
	public void onError(Exception arg0) {
		if(arg0 != null) {
			Log.e(TAG, "onError---->" + arg0.toString());
		}else {
			Log.e(TAG, "onError---->");
		}
	}

	@Override
	public void onMessage(String arg0) {
		Log.d(TAG, "onMessage----->" + arg0);
		JSONObject jsonObject = null;
		int responseStatus = 1;
		try {
			jsonObject = new JSONObject(arg0);
			// 当服务器发送心跳包来，也要回复一个心跳包保持在线
			if (jsonObject.getString("type").equals("ping")) {
				//收到ping消息标明可以正常通信了
				if(pingFirst) {
					EventBus.getDefault().post(new SocketLoginEvent(SocketLoginEvent.LOGIN_STATUS.LOGIN_SUCCESS));
				}else {
					pingFirst = false;
				}

				this.send(responseJson().toString());
			}else if (jsonObject.getString("type").equals("connect")) {
				//服务器等待连接
				doLoginRoom();
			}else if(jsonObject.getString("type").equals("login")) {
				//创建一个带有登录的令牌参数体
				ResponseParams params = new ResponseParams(responseStatus, TSChatManager.LOGIN_TAG);
				params.putResponse(jsonObject);
				params.requestType = ResponseParams.GET_LOGIN;
				TSChatManager.sendResponse(params);
			}else if (jsonObject.getString("type").equals("get_room")
					|| jsonObject.getString("type").equals("create_group_room")) {
				//创建单聊房间
				int status = jsonObject.getInt("status");
				String result = jsonObject.getString("result");
				JSONObject resultObject = new JSONObject(result);
				String packid = resultObject.getString("packid");
				ChatSocketRequest request = TSChatManager.getRequest(packid);
				if(request != null) {
					request.getParams().putResponse(resultObject);
					request.getParams().isSend = false;
					TSChatManager.sendResponse(request.getParams());
				}
			}
			else if (jsonObject.getString("type").equals("quit_group_room")) {
				//退出房间操作
				JSONObject resultObject = jsonObject.getJSONObject("result");
				String packid = resultObject.getString("packid");
				ChatSocketRequest request = TSChatManager.getRequest(packid);
				if(request != null) {
					request.getParams().putResponse(jsonObject);
					request.getParams().isSend = false;
					TSChatManager.sendResponse(request.getParams());
				}
//				int status = jsonObject.getInt("status");
//				String result = jsonObject.getString("result");
//				JSONObject resultObject = new JSONObject(result);
//				if (status == 0) {
//					//退出用户
//					int quit_uid = resultObject.getInt("quit_uid");
//					//退出房间
//					int room_id = resultObject.getInt("room_id");
//					if (quit_uid == Integer.parseInt(mUid)) {
//						msgSqlHelper.clearRoom(room_id);
//						String packid = resultObject.getString("packid");
//						Message message = intentHandler.obtainMessage(QUIT_ROOM);
//						message.arg1 = 1;
//						message.obj = packid;
//						intentHandler.sendMessage(message);
//					}
//				}
			}
			else if (jsonObject.getString("type").equals("push_message")) {
				//接收到的新消息或者获取到历史消息
				ResponseParams params = new ResponseParams(1,"push_message");
				params.putResponse(jsonObject);
				params.requestType = ResponseParams.PUSH_MESSAGE;
				TSChatManager.sendResponse(params);
			}else if(jsonObject.getString("type").equals("get_message_list")) {
				ResponseParams params = new ResponseParams(1,"get_message_list");
				params.putResponse(jsonObject);
				params.requestType = ResponseParams.GET_MESSAGE_LIST;
				TSChatManager.sendResponse(params);
			}
			else if (jsonObject.getString("type").equals("clear_message")) {
				// 删除消息
				JSONObject result = jsonObject.optJSONObject("result");
				String packid = result.getString("packid");		//取出服务端返回的令牌
				ResponseParams params = new ResponseParams(1, packid);
				params.putResponse(jsonObject);
				params.requestType = ResponseParams.SET_CLEAR_UNRADS;
				TSChatManager.sendResponse(params);
			}
			else if (jsonObject.getString("type").equals("send_message")) {
				// 发送的消息
				try {
					int status = jsonObject.getInt("status");
					JSONObject resultObject = jsonObject.getJSONObject("result");
					String packid = resultObject.getString("packid");
					ChatSocketRequest request = TSChatManager.getRequest(packid);
					if(request != null) {
						if(status == 0) {
							request.getParams().putResponse(resultObject);
							request.getParams().isSend = false;
							TSChatManager.sendResponse(request.getParams());
						}else {
							request.getResponseHandler().sendFailureMessage(resultObject);
						}
					}
				} catch (JSONException e) {
					//数据解析错误
					e.printStackTrace();
				}
			}
			else if (jsonObject.getString("type").equals("get_room_list")) {
				ResponseParams params = new ResponseParams(1, "get_room_list");
				params.putResponse(jsonObject);
				params.requestType = ResponseParams.GET_ROOM_LIST;
				TSChatManager.sendResponse(params);
			}else if(jsonObject.getString("type").equals("add_group_member")
					|| jsonObject.getString("type").equals("remove_group_member")
					|| jsonObject.getString("type").equals("set_room")) {

				JSONObject resultObject = jsonObject.getJSONObject("result");
				String packid = resultObject.getString("packid");
				ChatSocketRequest request = TSChatManager.getRequest(packid);
				if(request != null) {
					request.getParams().putResponse(resultObject);
					request.getParams().isSend = false;
					TSChatManager.sendResponse(request.getParams());
				}
			}else if(jsonObject.getString("type").equals("input_status")) {
				ResponseParams params = new ResponseParams(1, "input_status");
				params.putResponse(jsonObject);
				params.requestType = ResponseParams.SET_INPUT_STATUS;
				TSChatManager.sendResponse(params);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.v(TAG,"----big--------JSONException-----------" + e.getMessage());
		}
	}

	/**
	 * 当前是否正在聊天
	 */
	private synchronized boolean isChating(int room_id) {
		//消息房间号是否与当前聊天一样
		if (chatActivity != null && !chatActivity.isFinishing()
				&& currentRoomId == room_id) {
			return true;
		}
		return false;
	}

	public boolean isCreateRoomBefore(int roomid) {
		boolean isNewRoom = false;
		List<Entity> list = msgSqlHelper.getRoomListByRoomId(roomid);
		if (list.size() != 0) {
			Toast.makeText(mContext, "该房间已经存在", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext, "房间创建成功", Toast.LENGTH_SHORT).show();
		}
		return isNewRoom;
	}

	public void getRoomTitle(final ModelChatMessage msg) {
		//注释
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//
//				Uri.Builder uri = new Uri.Builder();
//				uri.scheme("http");
//				uri.authority(Api.getHost());
//				uri.appendEncodedPath(Api.getPath());
//
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("mod", ApiMessage.MOD_NAME);
//				params.put("act", ApiMessage.GET_USERINFO);
//				params.put("method", "url");
//				params.put("uid", msg.getTo_uid() + "");
//				params.put("oauth_token_secret", Thinksns.getMy().getSecretToken());
//				params.put("oauth_token", Thinksns.getMy().getToken());
//
//				String jsonResult = getPost(uri.toString(), params);
//				try {
//					JSONObject obj = new JSONObject(jsonResult);
//
//					Message msg_intent = intentHandler.obtainMessage(StaticInApp.INTENT_TO_DETAIL_SINGLE);
//					msg_intent.arg1 = msg.getRoom_id();
//					msg_intent.arg2 = msg.getTo_uid();
//					msg_intent.obj = obj.getString("uname");
//					msg_intent.sendToTarget();
//
//				} catch (JSONException e) {
//					e.printStackTrace();
//					Log.v(TAG, "-------Exception-----getRoomTitle-----------"
//							+ e.getMessage());
//				}
//			}
//		}).start();
	}


	// 移除消息
	public void removeMsg(String message_ids) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", "remove_push_message");
		if (message_ids != null) {
			map.put("message_ids", message_ids);
		}
		try {
			this.send(new JSONObject(map).toString());
		}catch (NotYetConnectedException e) {
			e.printStackTrace();
			close();
			throw new Exception("socket未连接");
		}
	}

	@Override
	public void send(String text) throws NotYetConnectedException {
		Log.v("ChatSocketClient--send", "text " + text);
		try {
			super.send(text);
		} catch (WebsocketNotConnectedException e) {
			e.printStackTrace();
			throw new NotYetConnectedException();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Log.v(TAG, "/IllegalStateException/"+e.getMessage());
		}
	}

	/**
	 * 链接上之后操作
	 */
	@Override
	public void onOpen(ServerHandshake arg0) {
		Log.e("ChatSocketClient", "打开Socket端口...");
	}

	/**
	 *用户登录聊天系统
	 */
	public synchronized void doLoginRoom() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("type", "login");
		map.put("uid",  getUid());
		map.put("oauth_token", getToken());
		map.put("oauth_token_secret", getTokenSecret());
		this.send(new JSONObject(map).toString());
	}

	/**
	 * 海生做的发送聊天消息，已经弃用，暂时保留
	 *
	 * @param to_uname
	 * @param to_uid
	 * @param content
	 * @param type
	 */
	public void sendMes(String to_uname, int to_uid, String content, String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", "say");
		if (mRoom_id != null) {
			map.put("room_id", mRoom_id);
		}
		map.put("msgtype", type);
		map.put("from_uname", mName);
		map.put("from_uid", mUid);
		map.put("to_uname", to_uname);
		map.put("to_uid", to_uid);
		map.put("content", content);
		try {
			this.send(new JSONObject(map).toString());
		} catch (NotYetConnectedException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(Map<String, String> data) throws NotYetConnectedException{
		try{
			this.send(new JSONObject(data).toString());
		}catch(NotYetConnectedException e) {
			close();
			throw new NotYetConnectedException();
		}
	}

	//获取历史聊天记录
	public void getRoomList(Map<String, String> data) throws Exception{
		try {
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("type", "get_room_list");
//			if(room_id == 0)
//				map.put("room_id", "all");
//			if(limit != 0) {
//				map.put("limit", limit + "");
//			}
//			if(mtime != 0)
//				map.put("mtime", mtime + "");
			send(new JSONObject(data).toString());

		} catch (NotYetConnectedException e) {
			e.printStackTrace();
			close();
			throw new Exception("socket未连接成功");
		}

	}

	//获取某个房间的信息
	public void getRoomInfo(int room_id, String packid) throws Exception{
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("type", "get_room_list");
			if (room_id == 0)
				map.put("room_id", "all");
			else
				map.put("room_id", String.valueOf(room_id));
			if(!TextUtils.isEmpty(packid))
				map.put("packid", packid);
			this.send(new JSONObject(map).toString());
		} catch (NotYetConnectedException e) {
			e.printStackTrace();
			close();
			throw new Exception("socket未连接");
		}
	}

	/**
	 * 设置群头像
	 * @param roomId
	 * @param path
     */
	public void setGroupFace(int roomId, String path, final TSChatListener listener) {
		synchronized (ChatSocketClient.class) {
			String packid = "";//MessageBody.createPackId();
			if(listeners.get(packid) == null) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("type", "set_room");
				map.put("room_id", roomId);
				map.put("logo", path);
				map.put("packid", packid);
				try {
					this.send(new JSONObject(map).toString());
					listeners.put(packid, listener);
				} catch (NotYetConnectedException e) {
					e.printStackTrace();
					listener.onError("socket连接失败");
					listeners.remove(listener);
				}
			}
		}
	}

	/**
	 * @ModelChatMessage
	 */
	public void sendMes(ModelChatMessage msg) throws NotYetConnectedException{
		try {
			send(msg.toJSONString());
		} catch (NotYetConnectedException e) {
			e.printStackTrace();
			throw new NotYetConnectedException();
		}
	}

	/**
	 * 创建单聊房间
	 *
	 * @param toUid
	 */
	public void creatRoom(String toUid, final TSChatListener listener) {
		Map<String, Object> map = new HashMap<String, Object>();
		String packid = "";//MessageBody.createPackId();
		map.put("type", "get_room");
		map.put("uid", toUid);
		map.put("packid", packid);
		try {
			this.send(new JSONObject(map).toString());
			listeners.put(packid, listener);
		} catch (Exception e) {
			e.printStackTrace();
			listener.onError("socket连接失败");
			listeners.remove(packid);
		}
	}

	/**
	 * 创建群组聊天对话，上传群组成员的id和title
	 *
	 */
	public void createGroupChat(String[] userInfo, final TSChatListener listener) {
		synchronized (ChatSocketClient.class) {

			Map<String, String> map = new HashMap<String, String>();
			String packid = "";//MessageBody.createPackId();
			if(listeners.get(packid) != null) {
				//已经存在该任务
				return;
			}
			String uids = userInfo[0].substring(0, userInfo[0].lastIndexOf(","));
			map.put("type", "create_group_room");
			map.put("uid_list", uids);
			map.put("title", userInfo[1]);
			map.put("packid", packid);
			try {
				this.send(new JSONObject(map).toString());
				listeners.put(packid, listener);
			} catch (NotYetConnectedException e) {
				e.printStackTrace();
				listener.onError("socket连接失败");
				listeners.remove(packid);
			}
		}
	}

	/**
	 * 修改群名
	 *
	 * @param room_id
	 * @param newTitle
	 */
	public void changeTitle(int room_id, String newTitle, String logo,
							int type, final TSChatListener listener) {
		synchronized (ChatSocketClient.class) {
			String packid = "";//MessageBody.createPackId();
			if(listeners.get(packid) == null) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("type", "set_room");
				map.put("title", newTitle);
				if(logo == null || logo.isEmpty())
					map.put("logo", 0);
				else
					map.put("logo", logo);

				map.put("room_id", room_id);
				map.put("group_type", type);		//1：修改名称 2：修改头像 3标题和头像都修改了
				map.put("packid", packid);
				try {
					this.send(new JSONObject(map).toString());
					listeners.put(packid, listener);
				} catch (NotYetConnectedException e) {
					e.printStackTrace();
					Log.v(TAG,
							"-------Exception-----changeTitle-----------"
									+ e.getMessage());
					listener.onError("socket连接失败");
					listeners.remove(listener);
				}
			}
		}
	}

	/**
	 * 群组添加成员
	 *
	 * @param room_id
	 */
	//注释
	public void addMembers(int room_id, String uids, final TSChatListener listener) {
		String packid = "";//MessageBody.createPackId();
		if(listeners.get(packid) != null) {
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", "add_group_member");
		map.put("room_id", room_id);
		map.put("member_uids", uids.substring(0, uids.lastIndexOf(",")));
		map.put("packid", packid);
		try {
			this.send(new JSONObject(map).toString());
			listeners.put(packid, listener);
		} catch (NotYetConnectedException e) {
			e.printStackTrace();
			listener.onError("连接失败");
		}

	}

	/**
	 * 删除群成员
	 *
	 * @param room_id
	 * @param uid
	 */
	public void deleteMember(int room_id, String uid, final TSChatListener listener) {
		synchronized (ChatSocketClient.class) {
			String packid = "";//MessageBody.createPackId();
			if(listeners.get(packid) == null) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("type", "remove_group_member");
				map.put("room_id", room_id);
				map.put("member_uids", uid);
				map.put("packid", packid);
				try {
					this.send(new JSONObject(map).toString());
					listeners.put(packid, listener);
				} catch (NotYetConnectedException e) {
					e.printStackTrace();
					listener.onError("socket连接失败");
					listeners.remove(packid);
				}
			}else {

			}
		}
	}

	/**
	 * 清空消息
	 *
	 * @param room_id
	 * @param clear_type
	 */
	public void clearMessage(int room_id, String clear_type, String packid) throws Exception{
		synchronized (ChatSocketClient.class) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", "clear_message");
			map.put("room_id", room_id);
			map.put("clear_type", clear_type);
			map.put("packid", packid);
			try {
				this.send(new JSONObject(map).toString());
			} catch (NotYetConnectedException e) {
				e.printStackTrace();
				close();
				throw new Exception("socket未连接");

			}
		}
	}

	/**
	 * 退出群房间
	 *
	 * @param room_id
	 *
	 */
	public void quitRoom(int room_id, final TSChatListener listener) {
		synchronized (ChatSocketClient.class) {
			String packid = "";//MessageBody.createPackId();
			if(listeners.get(packid) != null)
				return;

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", "quit_group_room");
			map.put("room_id", room_id);
			map.put("packid", packid);
			try {
				this.send(new JSONObject(map).toString());
				listeners.put(packid, listener);
			} catch (NotYetConnectedException e) {
				e.printStackTrace();
				listener.onError("socket暂未连接");
				listeners.remove(listener);
			}
		}
	}

	/**
	 * 获取某个房间的历史聊天详情
	 * @param room_id
	 * @param limit
	 * @param message_id  当message_id = 0表示首次获取服务器记录
	 */
	public synchronized void getHistory(int room_id, int limit, int message_id) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", "get_message_list");
		map.put("room_id", room_id);
		if (message_id != 0) {
			map.put("message_id", message_id);
		}

		//指定获取消息条数
		map.put("limit", limit);
		try {
			send(new JSONObject(map).toString());
		} catch (NotYetConnectedException e) {
			e.printStackTrace();
			close();
			throw new Exception("socket未连接");
		}
	}

	//发送输入状态
	public synchronized void sendChatingState(int roomId, int to_uid, String tips, int status) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", "input_status");
		map.put("room_id", roomId);		//房间ID
		map.put("to_uid", to_uid);		//要告知的对象
		map.put("status", status);		//当前输入状态 1：正在输入 0：放弃输入
		map.put("extend", tips);		//发送自定义提示内容
		try {
			this.send(new JSONObject(map).toString());
		} catch (NotYetConnectedException e) {
			e.printStackTrace();
			close();
			throw new Exception("socket未连接");
		}
	}


	/**
	 * 操作群组
	 *
	 * @param act
	 * @param isSuccess
	 *            被操作的用户id 加人时放空即可，减人时候需要记录id
	 */
	private void onFinish(String act, boolean isSuccess, JSONObject data) {

		// if (act.equals("addUser")) {
		// try {
		// listener.isAddMemberSuccess(isSuccess,
		// data.getInt("list_id"));
		// } catch (JSONException e) {
		//
		// e.printStackTrace();
		// }
		// } else
		if (act.equals("moveUser")) {
			// try {
			// listener.isDeleteMemberSuccess(data.getInt("uid"),
			// isSuccess,data.getInt("list_id"));
			// } catch (JSONException e) {
			//
			// e.printStackTrace();
			// }
		}
	}

	/**
	 * 获取当前socket绑定的activity，一般进入聊天之后会有，退出聊天的时候移除
	 *
	 * @return
	 */
	public Activity getActivity() {
		return chatActivity;
	}

	/**
	 * 设置当前socket绑定的activity，进入聊天的时候绑定，退出时候设置成空
	 *
	 * @param activity
	 */
	public void setActivity(Activity activity) {
		this.chatActivity = activity;
	}

	public void setMsgAdapter(BaseListFragment chatFragment, int room_id) {
		if(msgAdapter == null) {
			this.msgAdapter = chatFragment.getListAdapter();
		}
			this.chatActivity = chatFragment.getActivity();
			this.chatFragment = chatFragment;
			this.currentRoomId = room_id;
	}


	public static void initRoom(BaseListFragment room) {
		roomFragment = room;
		roomAdapter = room.getListAdapter();
	}

	public ListBaseAdapter getMsgAdapter() {
		return msgAdapter;
	}

	/**
	 * 获取当前socket是否有绑定的roomAdapter
	 *
	 * @return
	 */
	public ListBaseAdapter getRoomAdapter() {
		return roomAdapter;
	}

	public void startTimer() {
		// 如果前一个timer还在，需要先取消
		if (mConnTimer != null) {
			mConnTimer.cancel();
		}

		mConnTimer = new Timer();
		mConnTask = new ConnectStateTimerTask();
		// 每10秒检查连接一下
		mConnTimer.schedule(mConnTask, 0, 10000);
	}

	class ConnectStateTimerTask extends TimerTask {
		@Override
		public void run() {

		}
	};


	/**
	 * 下载附件
	 *
	 * @author Zoey
	 *
	 */
	class DownAttachHandler extends Handler {

		ModelChatMessage message = null;

		@SuppressLint("NewApi") @Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == DOWN_LOAD_ATTACH) {

				try {
					if (msg.obj == null) {
					} else {

						message = (ModelChatMessage) msg.obj;
						int type = msg.arg1;
						final Downloader downloader = new Downloader();

						File file = new File(
								Environment.getExternalStorageDirectory(), cache);
						if (!file.exists()) {
							file.mkdirs();
						}
						final String path = file.getAbsolutePath();

						if (message.getAttach_url() != null&&path!=null) {
							// 下载图片
							if (type == 1) {
								new Thread(new Runnable() {
									@SuppressLint("NewApi") @Override
									public void run() {
										String fileName = "thinksns4_"+ TimeUtils.getTimestamp()+ ".jpg";
										File file_img = downloader.downLoadFile(message.getAttach_url(), path,fileName);
										if (file_img==null) {
											return;
										}
										String img_path = file_img.getAbsolutePath();

										if (img_path != null) {
											message.setLocalPath(img_path);
											message.setImgWidth((Bimp.getLocalPicWH(img_path,mContext)).first);
											message.setImgHeight((Bimp.getLocalPicWH(img_path,mContext)).second);
										}

										// 保存至数据库
										if (message.getMessage_id() != 0) {
											long result = msgSqlHelper.addChatMessagetoChatList(
													message,
													message.getMessage_id());
											if (result > 0) {

											} else {

											}
										}
									}
								}).start();
							}
							// 下载语音
							else if (type == 2) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										String fileName = "thinksns4_"+ TimeUtils.getTimestamp()+ ".mp3";
										File file_voice = downloader.downLoadFile(message.getAttach_url(), path,fileName);
										String voice_path = file_voice.getAbsolutePath();

										if (voice_path != null) {
											message.setLocalPath(voice_path);
										}

										// 保存至数据库
										if (message.getMessage_id() != 0) {
											long result = msgSqlHelper.addChatMessagetoChatList(
													message,
													message.getMessage_id());
											if (result > 0) {

											} else {

											}
										}
									}
								}).start();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if (msg.what == GET_SERVICE_IMG_WH) {
				try {

					Object[] objects=(Object[]) msg.obj;
					final Bitmap bitmap=(Bitmap) objects[0];
					final ModelChatMessage message_wh=(ModelChatMessage) objects[1];
					final int type=(Integer) objects[2];

					//获取屏幕宽度
					float windowWidth = TDevice.getScreenWidth(mContext);
					final int maxWidth = (int)(windowWidth*2)/5; //设置图片的最大宽度为屏幕的2/5
					int maxHeight = maxWidth;//设置图片的最大高度为屏幕的2/5

					//拿到图片的宽度的高度
					float picWidth=bitmap.getWidth();
					float picheight=bitmap.getHeight();
					float scale=picWidth/picheight;

					picWidth = (Bimp.getScaleImgWH(picWidth,picheight,maxWidth,maxHeight,scale)).first;
					picheight = (Bimp.getScaleImgWH(picWidth,picheight,maxWidth,maxHeight,scale)).second;

					message_wh.setImgWidth(picWidth);
					message_wh.setImgHeight(picheight);

					// 保存至数据库
					if (message_wh.getMessage_id() != 0) {
						long result = msgSqlHelper.addChatMessagetoChatList(message_wh,message_wh.getMessage_id());
					}

					Message attach_msg = downAttachHandler.obtainMessage(DOWN_LOAD_ATTACH);
					attach_msg.obj = message_wh;
					attach_msg.arg1 = type;
					attach_msg.sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	class IntentHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == INTENT_TO_DETAIL_SINGLE) {
				try {
					int room_id = msg.arg1;
					int to_uid = msg.arg2;
					String to_name = (String) msg.obj;
					//注释
//					Intent intent = new Intent(mContext, ActivityChatDetail.class);
//					intent.putExtra("room_id", room_id);
//					intent.putExtra("to_uid", to_uid);
//					intent.putExtra("to_name", to_name);
//					intent.putExtra("is_group", 1);
//
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					mContext.startActivity(intent);
//					((Activity) mContext).finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == INTENT_TO_DETAIL_GROUP) {
				try {
					int room_id = msg.arg1;
					String title = (String) msg.obj;
					//注释
//					Intent intent = new Intent(mContext,
//							ActivityChatDetail.class);
//					intent.putExtra("room_id", room_id);
//					intent.putExtra("title", title);
//					intent.putExtra("is_group", 0);
//
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					mContext.startActivity(intent);
//					((Activity) mContext).finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (msg.what == UPDATE_MSG) {
				if (msg.arg1 == 1) {

				}else if(msg.arg1 == 0){
					//有新消息到达，这里可以加入广播
//					if(socketListener != null) {
//						socketListener.onReceiveComplete(msg.obj);
//					}
				}

			} else if(msg.what == NOTIFY_MSG) {
				String packid = (String)msg.obj;
				if(listeners.get(packid) != null) {
					listeners.get(packid).onSuccess("群管理操作");
					listeners.remove(packid);
				}else {

				}
			} else if(msg.what == CLEAR_MSG) {
				String packid = (String)msg.obj;
				if(packid != null && listeners.get(packid) != null) {
					listeners.get(packid).onSuccess("清空消息");
				}
			} else if(msg.what == QUIT_ROOM) {
				String packid = (String)msg.obj;
				if(packid != null && listeners.get(packid) != null) {
					listeners.get(packid).onSuccess("退出群聊");
				}else {

				}
			}
			else if (msg.what == IMG_TO_BITMAP) {
				try {

					Object[] objects=(Object[]) msg.obj;
					final String url=(String) objects[0];
					final ModelChatMessage message=(ModelChatMessage) objects[1];
					final int type=(Integer) objects[2];

					if (type==2) {
						Message attach_msg = downAttachHandler.obtainMessage(DOWN_LOAD_ATTACH);
						attach_msg.obj = msg;
						attach_msg.arg1 = type;
						attach_msg.sendToTarget();
					}else if(type==1){
						new Thread(new Runnable() {
							@Override
							public void run() {
								Bitmap bitmap = Bimp.getBitmap(url);

								Message imgMsg = downAttachHandler.obtainMessage(GET_SERVICE_IMG_WH);
								Object objs[]=new Object[3];
								objs[0]=bitmap;
								objs[1]=message;
								objs[2]=type;
								imgMsg.obj =objs;
								downAttachHandler.sendMessage(imgMsg);
							}
						}).start();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(msg.what == MSG_SEND_UPATE) {
				ModelChatMessage chat = (ModelChatMessage)msg.obj;
				if(chat.getOnMessageListener() != null) {
					if(msg.arg1 == 1) {
						chat.getOnMessageListener().onSuccess("发送成功");
					}else {
						chat.getOnMessageListener().onError("发送失败");
					}
				}
				//从消息队列中移除
//				sendList.remove(chat.getPackid());
			}else if(msg.what == CREATE_CHAT_RESPONSE) {
				Object [] data = (Object[]) msg.obj;
				Object result = data[1];
				String packid = (String)data[0];
				//回调主线程通知房间创建完毕
				listeners.get(packid).onSuccess(result);
				listeners.remove(packid);
			}
		}
	}

	// 请求头像
	public synchronized void getFace(final int uid, final ModelChatMessage msg,
									 final ModelChatUserList room, final String type) {
//				if (uid <= 0)
//					return;
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//
//						Uri.Builder uri = new Uri.Builder();
//						uri.scheme("http");
//						uri.authority(Api.getHost());
//						uri.appendEncodedPath(Api.getPath());
//
//						Map<String, String> params = new HashMap<String, String>();
//						params.put("mod", ApiMessage.MOD_NAME);
//						params.put("act", ApiMessage.GET_USERFACE);
//						params.put("method", "url");
//						params.put("uid", uid + "");
//						params.put("oauth_token_secret", Thinksns.getMy().getSecretToken());
//						params.put("oauth_token", Thinksns.getMy().getToken());
//
//						String jsonResult = getPost(uri.toString(), params);
//						if (jsonResult == null) {
//							return;
//						}
//						try {
//							JSONObject object = new JSONObject(jsonResult);
//							int status = object.getInt("status");
//							if (status == 1) {
//								String userFace = object.getString("url");
//
//								// 更新消息列表数据库
//								if (type.equals("push_message")|| type.equals("get_message_list")) {
//									if (msg != null) {
//										msg.setFrom_uface(userFace);
//
//										msgSqlHelper.addChatMessagetoChatList(msg,msg.getMessage_id());
//
//										if (msgAdapter != null&& msgAdapter.getRoom_id() == msg.getRoom_id()) {
////											msgAdapter.doRefreshFooter(msg);
//											msgAdapter.doUpdataList();
//										}
//									}
//									if (room != null) {
//										room.setFrom_uface_url(userFace);
//										msgSqlHelper.addRoomToRoomList(room,room.getRoom_id());
//									}
//								}
//								else if (type.equals("get_room_list")) {
//									//保存用户头像并更新数据库
//									room.setFrom_uface_url(userFace);
//									msgSqlHelper.addRoomToRoomList(room,room.getRoom_id());
//								}
//							} else {
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//
//							Log.v(TAG,
//									"-------Exception-----getFace-----------"
//											+ e.getMessage());
//						}
//					}
//				}).start();
	}

}
