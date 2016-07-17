//package com.thinksns.sociax.t4.android.chat;
//
//import java.io.File;
//import java.net.URI;
//import java.nio.channels.NotYetConnectedException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.exceptions.WebsocketNotConnectedException;
//import org.java_websocket.handshake.ServerHandshake;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.thinksns.sociax.api.Api;
//import com.thinksns.sociax.api.ApiMessage;
//import com.thinksns.sociax.component.TimeUtils;
//import com.thinksns.sociax.t4.adapter.AdapterChatDetailList;
//import com.thinksns.sociax.t4.adapter.AdapterChatRoomList;
//import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
//import com.thinksns.sociax.t4.android.ActivityHome;
//import com.thinksns.sociax.t4.android.Thinksns;
//import com.thinksns.sociax.t4.android.data.StaticInApp;
//import com.thinksns.sociax.t4.android.db.SQLHelperChatMessage;
//import com.thinksns.sociax.t4.android.down.Downloader;
//import com.thinksns.sociax.t4.android.img.Bimp;
//import com.thinksns.sociax.t4.android.interfaces.OnWebSocketClientFinishListener;
//import com.thinksns.sociax.t4.model.ListData;
//import com.thinksns.sociax.t4.model.ModelChatMessage;
//import com.thinksns.sociax.t4.model.ModelChatUserList;
//import com.thinksns.sociax.t4.model.ModelMemberList;
//import com.thinksns.sociax.t4.model.ModelSearchUser;
//import com.thinksns.sociax.t4.model.SociaxItem;
//import com.thinksns.sociax.t4.unit.UnitSociax;
//
///**
// * 类说明： 聊天客户端socket
// *
// * @author ZhiShi
// * @date 2014-10-15
// * @version 1.0
// */
//public class ChatSocketClient extends WebSocketClient implements
//		ChatReceiverInterface {
//	public static final String PREFERENCES_NAME="room_id";
//	public static final String PRE_UNREAD_MESSAGE="preferences_of_unread_message";
//
//	public static ChatSocketClient ChatSocketClientSingle;
//	private static int msgMentionCount = 0;
//	private static final String TAG = "ChatSocketClient";
//	private static SQLHelperChatMessage msgSqlHelper;
//	private Context mContext;
//	private static AdapterChatDetailList msgAdapter; // 当进入聊天详情的时候，设置聊天详情的adapter为当前adapter，用于清理聊天记录/添加群成员/删除群成员
//	private static AdapterChatRoomList roomAdapter; // 当进入聊天详情的时候，设置聊天详情的adapter为当前adapter，用于清理聊天记录/添加群成员/删除群成员
//	private ActivityChatDetail chatActivity; // 当进入聊天详情的时候，设置聊天activity为当前activity，用于退出聊天
//
//	private String mName = Thinksns.getMy().getUserName();
//	private String mUid = Thinksns.getMy().getUid() + "";
//	private String mRoom_id = null;
//	private boolean isLogin = false; // 判断是否已经登陆成功
//	private static ModelChatMessage tempMsg;
//	private long rowId = -1;
//	private static DownAttachHandler downAttachHandler;
//	private static IntentHandler intentHandler;
//	private String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//	private SharedPreferences pref = null;
//	private SharedPreferences prefUnread = null;
//	private Editor editorUnread;
//	private static int TYPE = 0;
//
//	private List<OnChatListener> chatListeners = new ArrayList<OnChatListener>();
//
//	//socket监听器
//	public interface WebSocketConnectListener {
//		public void onConnected();			//与服务器连接成功,等待用户登录确认
//		public void onConnectError(String error);		//socket连接错误
//		public void onSocketClose(boolean auto) ;		//socket已关闭
//		public void onSocketOpen();		//socket已打开
//		public void onLoginSuccess();	//登录成功,服务端返回登录用户信息
//		public void onLoginError(String error);	//登录失败,返回失败信息
//
//		//socket数据收发
//		public void onReceiveComplete(Object result);		//数据接收完成
//
//		public void onSendComplete();			//数据发送完成
//
//		public void onReceiveError(Object object);	//数据接收错误
//
//		public void onSendError(Object object);	//数据发送错误
//
//	}
//
//	private static WebSocketConnectListener socketListener;
//
//	public void setWebSocketConnectListener(
//			WebSocketConnectListener socketListener) {
//		this.socketListener = socketListener;
//	}
//
//	public static ChatSocketClient getChatSocketClient(URI serverURI,
//			Context mContext) {
//		if (ChatSocketClientSingle == null) {
//			ChatSocketClientSingle = new ChatSocketClient(serverURI, mContext);
//		}
//		return ChatSocketClientSingle;
//	}
//
//	public synchronized static ChatSocketClient getChatSocketClient() {
//		return ChatSocketClientSingle;
//	}
//
//	public ChatSocketClient(URI serverURI, Context mContext) {
//		super(serverURI);
//		this.uri = serverURI;
//		this.mContext = mContext;
//		msgSqlHelper = SQLHelperChatMessage.getInstance(mContext);
//		app = (Thinksns) mContext.getApplicationContext();
//
//		downAttachHandler = new DownAttachHandler();
//		intentHandler = new IntentHandler();
//
//		pref = mContext.getSharedPreferences(StaticInApp.PREFERENCES_NAME,
//				Context.MODE_PRIVATE);
//		prefUnread = mContext.getSharedPreferences(StaticInApp.PRE_UNREAD_MESSAGE,
//				Context.MODE_PRIVATE);
//	}
//
//	/**
//	 * 心跳包
//	 *
//	 * @return
//	 */
//	@SuppressWarnings("unused")
//	private Object responseJson() {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("type", "pong");
//		JSONObject pongJson = null;
//		pongJson = new JSONObject(map);
//		if (pongJson == null) {
//			responseJson();
//		}
//		return pongJson;
//	}
//
//	@Override
//	public void onClose(int arg0, String arg1, boolean arg2) {
//		Log.d(TAG, "onClose");
//		if (!isStopByCheckOut) {
////			reconnect();
//		} else {
//			System.out.println("用户退出登陆，结束长连接");
////			ChatSocketClientSingle = null;
//		}
//		if(socketListener != null)
//			socketListener.onSocketClose(isStopByCheckOut);
//	}
//
//	public void exit() {
//		isStopByCheckOut = true;
//		close();
//		ChatSocketClientSingle = null;
//	}
//
//	@Override
//	public void onError(Exception arg0) {
////		reconnect();
//		if(socketListener != null) {
//			socketListener.onConnectError(arg0.toString());
//		}
//	}
//
//	@Override
//	public void onMessage(String arg0) {
//		JSONObject jsonObject = null;
//		try {
//			jsonObject = new JSONObject(arg0);
//			Log.d(TAG, "onMessage----->" + jsonObject.toString());
//			// 当服务器发送心跳包来，也要回复一个心跳包保持在线
//			if (jsonObject.getString("type").equals("ping")) {
//				this.send(responseJson().toString());
//			}else if (jsonObject.getString("type").equals("connect")) {
//				//服务器等待连接
//				if(socketListener != null) {
//					socketListener.onConnected();
//				}
//			}else if(jsonObject.getString("type").equals("login")) {
//				//服务器等待用户确认登录
//				String status = jsonObject.getString("status");
//				if(status.equals("1003")) {
//				}else if(status.equals("0")) {
//					if(socketListener != null) {
//						isLogin = true;
//						socketListener.onLoginSuccess();
//						return;
//					}
//				}
//				if(socketListener != null)
//					socketListener.onLoginError(jsonObject.getString("msg"));
//				isLogin = false;
//
//			}else if (jsonObject.getString("type").equals("get_room")) {
//				//创建单聊房间
//				int status = jsonObject.getInt("status");
//				String result = jsonObject.getString("result");
//				JSONObject resultObject = new JSONObject(result);
//				// String packid = resultObject.getString("packid");
//				int mtime = resultObject.getInt("mtime");
//				int room_id = resultObject.getInt("room_id");
//				int to_uid = resultObject.getInt("to_uid");
//
//				if (status == 0 && to_uid != 0 && room_id != 0) {
//
//					ModelChatMessage msg = new ModelChatMessage();
//					msg.setRoom_id(room_id);
//					msg.setTo_uid(to_uid);
//
//					// 获取房间名称
//					getRoomTitle(msg);
//				} else {
//					Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
//				}
//			}
//			// 创建群聊房间
//			else if (jsonObject.getString("type").equals("create_group_room")) {
//				int status = jsonObject.getInt("status");
//				String result = jsonObject.getString("result");
//				JSONObject resultObject = new JSONObject(result);
//
//				// String packid =resultObject.getString("packid");
//				int master_uid = resultObject.getInt("master_uid");
//				String title = resultObject.getString("title");
//				boolean is_group = resultObject.getBoolean("is_group");
//				int mtime = resultObject.getInt("mtime");
//				int room_id = resultObject.getInt("room_id");
//				int member_num = resultObject.getInt("member_num");
//
//				if (status == 0) {
//					ModelChatUserList room = new ModelChatUserList();
//
//					room.setMaster_uid(master_uid);
//					room.setTitle(title);
//
//					if (is_group == true) {
//						room.setIs_group(0);
//					} else if (is_group == false) {
//						room.setIs_group(1);
//					}
//
//					room.setMtime(mtime);
//					room.setRoom_id(room_id);
//					room.setMember_num(member_num);
//
//					// 将房间列表信息添加到房间列表数据库
//					msgSqlHelper.addRoomInfo(room);
//					try {
//						Message msg_intent = intentHandler.obtainMessage(StaticInApp.INTENT_TO_DETAIL_GROUP);
//						msg_intent.arg1 = room.getRoom_id();
//						msg_intent.obj = room.getTitle();
//						msg_intent.sendToTarget();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			else if (jsonObject.getString("type").equals("quit_group_room")) {
//				//退出房间操作
//				int status = jsonObject.getInt("status");
//				String result = jsonObject.getString("result");
//				JSONObject resultObject = new JSONObject(result);
//
//				if (status == 0) {
//					int quit_uid = resultObject.getInt("quit_uid");
//					int room_id = resultObject.getInt("room_id");
//
//					if (quit_uid == Thinksns.getMy().getUid()) {
//						msgSqlHelper.clearRoom(room_id);
//						// 删除房间后更新房间列表
//						if (roomAdapter != null) {
//							roomAdapter.doUpdataList();
//						}
//					}
//				}
//			}
//			else if (jsonObject.getString("type").equals("push_message")
//					|| jsonObject.getString("type").equals("get_message_list")) {
//				//接收到的新消息或者获取到历史消息
//				Log.e(TAG, "收到消息，消息类型:" + jsonObject.getString("type"));
//				doPushMessage(jsonObject);
//			}
//			else if (jsonObject.getString("type").equals("clear_message")) {
//				// 删除消息
//				int status = jsonObject.getInt("status");
//				JSONObject result = jsonObject.optJSONObject("result");
//
//				if (status == 0) {
//					if (result != null && !result.equals("null")) {
//						int room_id = result.getInt("room_id");
//						msgSqlHelper.clearRoom(room_id);
//						// 发送广播至界面，更新ui
//						Intent intent = new Intent(StaticInApp.DEL_ROOM);
//						mContext.sendBroadcast(intent);
//					} else {
//						// 移除已读消息
//					}
//				}
//			}
//			else if (jsonObject.getString("type").equals("send_message")) {
//				// 发送的消息
//				try {
//
//					int status = jsonObject.getInt("status");
//					String result = jsonObject.getString("result");
//					JSONObject resultObject = new JSONObject(result);
//					String packid = resultObject.getString("packid");
//					int message_id = resultObject.getInt("message_id");
//					int mtime = resultObject.getInt("mtime");
//
//					if (status == 0) {
//
//						ModelChatMessage msg = new ModelChatMessage();
//						msg.setMessage_id(message_id);
//						msg.setMtime(mtime);
//
//						if (tempMsg != null) {
//							msg.setLength(tempMsg.getLength());
//							msg.setLatitude(tempMsg.getLatitude());
//							msg.setLongitude(tempMsg.getLongitude());
//							msg.setLocation(tempMsg.getLocation());
//							msg.setAttach_id(tempMsg.getAttach_id());
//
//							msg.setUid(tempMsg.getUid());
//							msg.setContent(tempMsg.getContent());
//							msg.setRoom_id(tempMsg.getRoom_id());
//							msg.setFrom_uid(Thinksns.getMy().getUid());
//							msg.setType(tempMsg.getMsgtype());
//
//							msg.setCard_uid(tempMsg.getCard_uid());
//							msg.setCard_uname(tempMsg.getCard_uname());
//							msg.setCard_avatar(tempMsg.getCard_avatar());
//							msg.setCard_intro(tempMsg.getCard_intro());
//
//							msg.setLocalPath(tempMsg.getLocalPath());
//							msg.setImgWidth(tempMsg.getImgWidth());
//							msg.setImgHeight(tempMsg.getImgHeight());
//
//							ModelChatUserList room = new ModelChatUserList();
//							room.setContent(tempMsg.getContent());
//							room.setRoom_id(tempMsg.getRoom_id());
//							room.setMtime(mtime);
//							room.setIsNew(0);
//							if (pref != null) {
//								room.setTitle(pref.getString("mTitle", ""));
//							}
//							long updateResult = msgSqlHelper.updateHaveRoomToRoomList(room,tempMsg.getRoom_id());
//							if (updateResult <= 0) {
//								// 刷新房间列表
//								getRoomList(tempMsg.getRoom_id() + "");
//							}
//						}
//
//						msg.setAttach_url("");
//						// msg.setLocalPath("");
//						// 设置自己的头像
//						msg.setFrom_uface(Thinksns.getMy().getFace());
//
//						// 添加到数据库
//						if (this.rowId != -1) {
//							// 发送成功
//							if (packid.equals(tempMsg.getPackid())) {
//								msg.setImgSendState(StaticInApp.GOT_THE_PIC);
//
//								long addResult = msgSqlHelper.addChatMessagetoChatListById(msg, rowId);
//								boolean equals=(msgAdapter==null);
//								// 改变消息详情列表数据
//								if (equals==false && addResult <= 0) {
//									msgAdapter.doRefreshFooter(msg);
//								}else if (equals == false && addResult > 0) {
//									msgAdapter.doUpdataList();
//								}
//							}
//							// 发送失败，稍后加上重发机制
//							else {
//								Toast.makeText(mContext, "发送失败",Toast.LENGTH_SHORT).show();
//								msg.setImgSendState(StaticInApp.SOCKET_ERROR);
//
//								long addResult = msgSqlHelper.addChatMessagetoChatListById(msg, rowId);
//								// 改变消息详情列表数据
//								if (msgAdapter != null && addResult <= 0) {
//									msgAdapter.doRefreshFooter(msg);
//								}
//							}
//						}
//
////						// 获取附件
////						if (tempMsg.getAttach_id() != null&& !tempMsg.getAttach_id().equals("")&&
////								!tempMsg.getAttach_id().equals("null")) {
////							getAttach(tempMsg.getAttach_id(), msg);
////						}
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			else if (jsonObject.getString("type").equals("get_room_list")) {
//				//获取历史聊天用户列表
////				String result = jsonObject.getString("result");
////				JSONObject resultObject = new JSONObject(result);
//				String room_list = jsonObject.getJSONObject("result").getString("list");
//
//				if (room_list != null && !room_list.equals("null")) {
//					try {
//						JSONArray list = new JSONArray(room_list);
//						ListData<SociaxItem> roomList = new ListData<SociaxItem>();
//						for (int i = 0; i < list.length(); i++) {
//							ModelChatUserList room = new ModelChatUserList(list.getJSONObject(i));
//							List<ModelMemberList> memberList = room.getMemList();
//							int self_index = room.getSelf_index();
//							int is_group = room.getIs_group();
//							// 单聊
//							if (is_group == 1) {
//								// 我在单聊列表的第一个，列表需要的是对方的信息
//								try {
//									int index = (self_index == 0 ? 1 : 0);
//									//用户id,用户名
//									int uid = memberList.get(index).getUid();
//									String uname = memberList.get(index).getUname();
//									//记录聊天对象
//									room.setTo_uid(uid);
//									room.setTo_name(uname);
////									msgSqlHelper.addRoomToRoomList(room,room.getRoom_id());
//									// 请求对方的头像,这里暂时把获取用户头像放在列表里显示的时候获取
//									//按需索取
////									if (uid != 0) {
////										getFace(uid, null, room,jsonObject.getString("type"));
////									}
//								}catch (Exception e) {
//									e.printStackTrace();
//								}
//
//							}
//							// 群聊
//							else {
//								//暂时不获取群组成员头像
//							}
//
//							/**
//							 * 将房间列表信息添加到房间列表数据库，调用房间列表页接口的情况分为两种
//							 * 一种是第一次进入房间列表页，获取全部房间 第二种为没有聊过的对象发送消息过来
//							 */
//
//								int updateResult = msgSqlHelper.getRoomNewCount(room);
////								Message message = intentHandler.obtainMessage(StaticInApp.UPDATE_MSG);
////								message.obj = room;
//								if (updateResult > 0) {
//									//本地已经记录该房间消息
//									room.setIsNew(updateResult);
////									if (ActivityHome.offline) {
////										message.arg1 = 0;
////									} else {
////										message.arg1 = 1;
////									}
//								} else {
//									room.setIsNew(0);
////									message.arg1 = 0;
//								}
//
//								//更新该房间的消息数据
//								msgSqlHelper.addRoomToRoomList(room, room.getRoom_id());
//								roomList.add(room);
////								intentHandler.sendMessage(message);
//						}
//						//数据解析完毕，通知主界面可以更新显示了
//						Message message = intentHandler.obtainMessage(StaticInApp.UPDATE_MSG);
//						message.obj = roomList;
//						intentHandler.sendMessage(message);
//						SharedPreferences preferences = mContext.getSharedPreferences("tschat",
//								mContext.MODE_PRIVATE);
//						Editor edit=preferences.edit();
//						edit.putInt("use", 1);
//						edit.commit();
//
////						Thinksns.isFirstSignIn = false;
//					} catch (JSONException e) {
//						e.printStackTrace();
//						Log.v(TAG,"----get_room_list--------JSONException-----------"+ e.getMessage());
//					}
//				}else {
//					Log.e(TAG, "------get_room_list data is error!!!");
//				}
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			Log.v(TAG,"----big--------JSONException-----------" + e.getMessage());
//		}
//	}
//
//	/**
//	 * 当前是否正在聊天
//	 */
//	private synchronized boolean isChating(int room_id) {
//		if (chatActivity != null && !chatActivity.isFinishing())
//			return true;
//		return false;
//	}
//
//	/**
//	 * 处理push_message或get_message_list
//	 *
//	 * @param jsonObject
//	 * @throws JSONException
//	 */
//	private synchronized void doPushMessage(JSONObject jsonObject)
//			throws JSONException {
//		int status = jsonObject.optInt("status");
//		JSONObject resultObject = jsonObject.optJSONObject("result");
//		//返回的消息数目
//		int list_length = resultObject.getInt("length");
//		//服务端返回的消息列表
//		JSONArray listArray = resultObject.optJSONArray("list");
//		try {
//			if (status == 0) {
//				String message_ids = "";		//移除消息，用于给服务器反馈表示已经收到此消息
//				ListData<SociaxItem> chatList = new ListData<>();
//				for (int i = 0; i < list_length; i++) {
////					Message message = intentHandler.obtainMessage(StaticInApp.UPDATE_MSG);
////					message.arg1 = 0; // 是否设置消息提醒：0 不提醒，1提醒
//
//					JSONObject obj = listArray.optJSONObject(i);
//					// 创建消息列表
//					ModelChatMessage msg = createMessageBody(obj);
//
//					message_ids += msg.getMessage_id() + ",";
//
//					Log.v("chatAdapter", "socket/message_id/"+msg.getMessage_id()+"/content/"+msg.getContent());
//
//					//目前未读消息接收到了两个一模一样的pushmessage，为防止重复显示
//					prefUnread = mContext.getSharedPreferences(StaticInApp.PRE_UNREAD_MESSAGE, Context.MODE_PRIVATE);
//					editorUnread = prefUnread.edit();
//
//					if (prefUnread != null) {
//						int msgIdPref = prefUnread.getInt("message_id" + msg.getMessage_id(), -1);
//						if (msgIdPref ==-1 ||(msgIdPref!= -1 && msgIdPref != msg.getMessage_id()))
//						{
//
//							// 消息是否是当前聊天对象发出
//							boolean isChating = isChating(msg.getRoom_id());
//							int newMsg = (isChating == true) ? 0 : 1;
//
//							msg.setIsNew(newMsg);
//							// 移除消息
////							if (newMsg > 0) {
////								// 移除消息推送，作用是防止服务端每次都把全部消息推送过来
////								removeMsg(msg.getMessage_id(), msg.getRoom_id());
////							} else {
////								removeMsg(0, msg.getRoom_id());
////							}
//
//							// 创建房间
//							ModelChatUserList room = new ModelChatUserList();
//							room.setContent(msg.getContent());
//							room.setRoom_id(msg.getRoom_id());
//							room.setMtime(msg.getMtime());
//
//							if (pref != null) {
//								room.setTitle(pref.getString("mTitle", ""));
//							}
//
//							String pushType = jsonObject.getString("type");
//
//							String notify_type = msg.getNotify_type();
//							if(notify_type != null && !notify_type.isEmpty()) {
//								int group_action = 0; // 群动态操作类型：1：增加成员 2：删除成员
//								if (notify_type.equals("add_group_member")) {
//									group_action = 1;
//								} else if (notify_type.equals("remove_group_member")) {
//									group_action = 2;
//								} else if (notify_type.equals("create_group_room")) {
//									group_action = 3;
//								} else if (notify_type.equals("set_room")) {
//									group_action = 4;
//								} else if (notify_type.equals("quit_group_room")) {
//									group_action = 5;
//								}
//								// 添加群成员
//								if (group_action != 0 && (group_action == 1 || group_action == 2)) {
//									//添加群成员或删除群成员
////								if (pref != null) {
////									room.setTitle(pref.getString("mTitle", ""));
////								}
//									//成员列表
//									JSONArray memberArray = obj.getJSONArray("member_list");
//									for (int j = 0; j < memberArray.length(); j++) {
//										JSONObject memberObject = memberArray.getJSONObject(j);
//										int change_uid = memberObject.optInt("uid");
//										String change_uname = memberObject.optString("uname");
//										if (group_action == 1) {
//											//添加群成员
//											msg.setRoom_add_uid(change_uid);
//											msg.setRoom_add_uname(change_uname);
//											if (listener != null)
//												listener.isAddMemberSuccess(true, msg.getRoom_id());
//										} else if (group_action == 2) {
//											//删除群成员
//											msg.setRoom_del_uid(change_uid);
//											msg.setRoom_del_uname(change_uname);
//											if (listener != null)
//												listener.isDeleteMemberSuccess(change_uid, true, msg.getRoom_id());
//										}
//									}
//								}
//								// 设置群信息
//								else if (notify_type.equals("set_room")) {
//									JSONObject room_infoObj = obj.getJSONObject("room_info");
//									String title = room_infoObj.getString("title");
//									msg.setRoom_title(title);
//									msg.setFrom_uname(obj.getString("from_uname"));
//									room.setTitle(title);
//									// 当前正在聊天房间中
//									if (isChating) {
//										//更新房间名
//										getActivity().setmTitle(title);
//										// 发送广播至界面，更新ui
//										Intent intent = new Intent(StaticInApp.UPDATE_CHAT_LIST);
//										mContext.sendBroadcast(intent);
//									}
//								}
//								// 退出群房间
//								else if (notify_type.equals("quit_group_room")) {
//									//退出成员id
//									int quit_uid = obj.getInt("quit_uid");
//									String quit_uname = obj.getString("quit_uname");
//									msg.setQuit_uid(quit_uid);
//									msg.setQuit_uname(quit_uname);
////								if (pref != null) {
////									room.setTitle(pref.getString("mTitle", ""));
////								}
//								}
//								// 创建群房间
//								else if (notify_type.equals("create_group_room")) {
//									room.setFrom_uname(obj.optString("from_uname"));
//									room.setFrom_uid(msg.getFrom_uid());
////								room.setTitle("");
//									// 群主名称，在刚创建房间时服务器推送的房间创建信息里有用
//									msg.setMaster_uname(obj.optString("from_uname"));
//								}
//							}else {
//								room.setIsNew(newMsg);
//							}
////								msg.setIsNew(newMsg);
//							// 更新消息列表
//							long updateResult = msgSqlHelper.addChatMessagetoChatList(msg, msg.getMessage_id());
//							// 改变消息详情列表数据
////							if (updateResult <= 0) {
////								// 请求对方的头像
////								getFace(msg.getFrom_uid(), msg, null, pushType);
////							}
//
//							//将消息显示到adapter里，占位，以防错乱
////							if (msgAdapter!=null) {
////								msgAdapter.doRefreshFooter(msg);
////							}
////
//
//							// 更新房间列表
//							updateResult = msgSqlHelper.updateHaveRoomToRoomList(room, msg.getRoom_id());
//							if(newMsg == 1) {
//								//收到新消息且不是聊天房间的消息
//								//暂时关闭消息通知
////								message.arg1 = 1;
//							}
//							if (updateResult == 0) {
////								// 请求对方的头像
////								getFace(msg.getFrom_uid(), msg, null, pushType);
//								Log.e("ChatSocketClient", "get room list");
////								if (group_action == 0) {
////									// 新用户消息到达，刷新房间列表
////									getRoomList(msg.getRoom_id() + "");
////								}
//								//第一次接收到用户发来的消息
//								room.setTo_uid(msg.getFrom_uid());
//								room.setTo_name(msg.getFrom_uname());
//								// 保存房间列表记录
//								msgSqlHelper.addRoomToRoomList(room, msg.getRoom_id());
////								return;
//							}
////							else if ((updateResult > 0) && (newMsg == 1)) {
////								//收到新消息
////								Log.e("ChatSocketClient", "push new message");
////								message.arg1 = 1;
////							}
//
//							// 创建房间不进行提醒
////							if (!msg.getNotify_type().equals("create_group_room")) {
////								// 设置通知消息,刷新主界面
//////								message.obj = msg;
//////								intentHandler.sendMessage(message);
////
////							}
//							if(isChating) {
//								//加入当前聊天列表
//								chatList.add(msg);
//							}
//
//							editorUnread.putInt("message_id" + msg.getMessage_id(), msg.getMessage_id());
//							editorUnread.commit();
//						}
//					}
//				}
//
//				//接收消息处理完成
//				Message message = intentHandler.obtainMessage(StaticInApp.UPDATE_MSG);
//				message.arg1 = 0; // 是否设置消息提醒：0 不提醒，1提醒
//				message.obj = chatList;
//				intentHandler.sendMessage(message);
//				//移除消息
//				removeMsg(message_ids);
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.v(TAG,"-------Exception-----doPushMessage-----------"+ e.getMessage());
//		}
//	}
//
//	/**
//	 * 创建消息体
//	 *
//	 * @param obj
//	 * @return
//	 */
//	private ModelChatMessage createMessageBody(JSONObject obj) {
//		ModelChatMessage msg = new ModelChatMessage();
//		try {
//			int from_uid = obj.getInt("from_uid");
//			//消息内容
//			String content = "";
//			int message_id = obj.getInt("message_id");
//			// 消息时间
//			int mtime = obj.getInt("mtime");
//			int room_id = obj.getInt("room_id");
//			//消息类型
//			String type = obj.optString("type");
//			String notify_type = obj.optString("notify_type");
//			if (type.equals("text")) {
//				content = obj.getString("content");
//			} else if (type.equals("voice")) { // 语音消息
//				msg.setLength(obj.optInt("length"));
//				content = "[语音]";
//			} else if (type.equals("image")) { // 图片消息
//				content = "[图片]";
//			} else if (type.equals("position")) { // 位置消息
//				double latitude = obj.optDouble("latitude");
//				double longitude = obj.optDouble("longitude");
//
//				msg.setLatitude(latitude);
//				msg.setLongitude(longitude);
//				msg.setLocation(obj.optString("location"));
//
//				content = "[位置]";
//			} else if (type.equals("card")) {
//				int uid = obj.optInt("uid");
//				msg.setCard_uid(uid);
//				content = "[名片]";
//			} else if(type.equals("notify")) {
//				msg.setNotify_type(notify_type);
//			}
//			//消息为image,voice,position
//			String attach_id = obj.optString("attach_id");
//
//			msg.setMessage_id(message_id);
//			msg.setFrom_uid(from_uid);
//			msg.setType(type);
//			msg.setContent(content);
//			msg.setRoom_id(room_id);
//			msg.setMtime(mtime);
//			msg.setAttach_id(attach_id);
//
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		return msg;
//	}
//
//	public boolean isCreateRoomBefore(int roomid) {
//		boolean isNewRoom = false;
//		SQLHelperChatMessage chatHelper = app.getSQLHelperChatMessage();
//		List<SociaxItem> list = chatHelper.getRoomListByRoomId(roomid);
//		if (list.size() != 0) {
//			Toast.makeText(mContext, "该房间已经存在", Toast.LENGTH_SHORT).show();
//		} else {
//			Toast.makeText(mContext, "房间创建成功", Toast.LENGTH_SHORT).show();
//		}
//		return isNewRoom;
//	}
//
//	public void getRoomTitle(final ModelChatMessage msg) {
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
//	}
//
//
//
//
//
//
//	// 请求附件及头像
//	public static String getPost(String url, Map<String, String> params) {
//
//		String result = null;
//		String CharSet = "utf-8";
//
//		// 创建连接
//		HttpPost httpRequest = new HttpPost(url);
//		// 使用NameValuePair来保存要传递的Post参数
//		List<NameValuePair> nameparams = new ArrayList<NameValuePair>();
//		// 添加要传递的参数
//		Set<String> keyset = params.keySet();
//		for (Iterator<String> it = keyset.iterator(); it.hasNext();) {
//			String key = it.next();
//			String value = params.get(key);
//			nameparams.add(new BasicNameValuePair(key, value));
//		}
//		try {
//			// 设置字符集
//			HttpEntity httpentity = new UrlEncodedFormEntity(nameparams,CharSet);
//			// 请求httpRequest
//			httpRequest.setEntity(httpentity);
//			// 取得默认的HttpClient
//			HttpClient httpclient = new DefaultHttpClient();
//			// 取得HttpResponse
//			HttpResponse httpResponse = httpclient.execute(httpRequest);
//			// HttpStatus.SC_OK表示连接成功
//			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				// 取得返回的字符串
//				result = EntityUtils.toString(httpResponse.getEntity(), CharSet);
//			} else {
//				result = "";
//			}
//			httpRequest.abort();
//		} catch (Exception e) {
//			e.printStackTrace();
//			if ((e.getMessage()).indexOf("Connection")!=-1||(e.getMessage()).indexOf("refused")!=-1) {
//				getPost(url, params);
//			}
//			Log.v(TAG,"-------Exception-----getPost-----------" + e.getMessage());
//		}
//		return result;
//	}
//
//	// 移除消息
//	public void removeMsg(String message_ids) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("type", "remove_push_message");
//		if (message_ids != null) {
//			map.put("message_ids", message_ids);
//		}
////		map.put("current_room_id", room_id);
//		this.send(new JSONObject(map).toString());
//	}
//
//	@Override
//	public void send(String text) throws NotYetConnectedException {
//		Log.v("ChatSocketClient--send", "text " + text);
//		try {
//			super.send(text);
//		} catch (WebsocketNotConnectedException e) {
//			e.printStackTrace();
//			Toast.makeText(mContext, "服务连接失败", Toast.LENGTH_SHORT).show();
//			Log.v(TAG, "/WebsocketNotConnectedException/"+e.getMessage());
////			reconnect();
//			close();
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//			Log.v(TAG, "/IllegalStateException/"+e.getMessage());
//		}
//	}
//
//	Thinksns app;
//
//	/**
//	 * 断线之后重新连接
//	 */
//	private void reconnect() {
//		Log.d(TAG, "reconnect");
//		if (isConnecting()) {// 已经是连接状态或者有线程正在连接，不需要重新连接
//			return;
//		}
//		if (!isStopByCheckOut) {
//			startTimer();
//		}
//	}
//
//	/**
//	 * 链接上之后操作
//	 */
//	@Override
//	public void onOpen(ServerHandshake arg0) {
//		if (socketListener != null)
//			socketListener.onSocketOpen();
////		if (!isLogin)
////			doLoginRoom();
//	}
//
//	/**
//	 * 登录房间
//	 */
//
//	public void doLoginRoom() {
//
////		Map<String, String> map = new HashMap<String, String>();
////		map.put("type", "login");
////		map.put("uid", Thinksns.getMy().getUid() + "");
////		map.put("oauth_token", Thinksns.getMy().getToken());
////		map.put("oauth_token_secret", Thinksns.getMy().getSecretToken());
////
////		this.send(new JSONObject(map).toString());
//		isLogin = true;
//	}
//
//	/**
//	 * 海生做的发送聊天消息，已经弃用，暂时保留
//	 *
//	 * @param to_uname
//	 * @param to_uid
//	 * @param content
//	 * @param type
//	 */
//	public void sendMes(String to_uname, int to_uid, String content, String type) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("type", "say");
//		if (mRoom_id != null) {
//			map.put("room_id", mRoom_id);
//		}
//		map.put("msgtype", type);
//		map.put("from_uname", mName);
//		map.put("from_uid", mUid);
//		map.put("to_uname", to_uname);
//		map.put("to_uid", to_uid);
//		map.put("content", content);
//		try {
//			this.send(new JSONObject(map).toString());
//		} catch (NotYetConnectedException e) {
//			e.printStackTrace();
//		}
//	}
//
//	//获取历史聊天记录
//	public void getRoomList(String limit) {
//		try {
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("type", "get_room_list");
//			map.put("room_id", limit);
//			this.send(new JSONObject(map).toString());
//		} catch (NotYetConnectedException e) {
//			Log.d(TAG, "NotYetConnectedException");
//			e.printStackTrace();
//		}
//
//		Log.v(TAG,"getRoomList--->"+ limit);
//
//	}
//
//	/**
//	 * FragmentChatDetail发送聊天消息
//	 *
//	 * @parModelChatMessage @param type
//	 */
//	public void sendMes(ModelChatMessage msg, long rowId) {
//		try {
//			this.send(msg.toJSONString());
//			tempMsg = msg;
//			this.rowId = rowId;
//
//		} catch (NotYetConnectedException e) {
//			Log.d(TAG, "NotYetConnectedException");
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 创建单聊房间
//	 *
//	 * @param toUid
//	 */
//	public void creatRoom(int toUid) {
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("type", "get_room");
//		map.put("uid", toUid);
//		// map.put("packid", CreatPackId.createPackId());
//
//		try {
//			this.send(new JSONObject(map).toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 创建群组聊天对话，上传群组成员的id和title
//
//	 */
//	public void createGroupChat(String uids) {
//
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("type", "create_group_room");
//		map.put("uid_list", uids.substring(0, uids.lastIndexOf(",")));
//		// map.put("packid", CreatPackId.createPackId());
//
//		try {
//			this.send(new JSONObject(map).toString());
//		} catch (NotYetConnectedException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 修改群名
//	 *
//	 * @param room_id
//	 * @param newTitle
//	 */
//	public void changeTitle(int room_id, String newTitle) {
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("type", "set_room");
//		map.put("title", newTitle);
//		map.put("room_id", room_id);
//
//		try {
//			this.send(new JSONObject(map).toString());
//		} catch (NotYetConnectedException e) {
//			e.printStackTrace();
//			Log.v(TAG,
//					"-------Exception-----changeTitle-----------"
//							+ e.getMessage());
//		}
//	}
//
//	/**
//	 * 群组添加成员
//	 *
//	 * @param room_id
//	 */
//	public void addMembers(int room_id, ListData<ModelSearchUser> list) {
//		String uids = "";
//		for (int i = 0; i < list.size(); i++) {
//			uids += ((ModelSearchUser) list.get(i)).getUid() + ",";
//		}
//
//		// 查询该成员是否已在房间成员列表里
//		SharedPreferences preferences = Thinksns.getContext()
//				.getSharedPreferences(StaticInApp.MEMBERS_UIDS,
//						Context.MODE_PRIVATE);
//		String bre_uids = preferences.getString("uids", "");
//		if (bre_uids.indexOf(uids) != -1) {
//			Toast.makeText(Thinksns.getContext(), "该好友已在房间列表中", 1).show();
//			return;
//		} else {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("type", "add_group_member");
//			map.put("room_id", room_id);
//			map.put("member_uids", uids.substring(0, uids.lastIndexOf(",")));
//
//			try {
//				this.send(new JSONObject(map).toString());
//			} catch (NotYetConnectedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/**
//	 * 删除群成员
//	 *
//	 * @param room_id
//	 * @param uid
//	 */
//	public void deleteMember(int room_id, int uid) {
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("type", "remove_group_member");
//		map.put("room_id", room_id);
//		map.put("member_uids", uid + "");
//
//		try {
//			this.send(new JSONObject(map).toString());
//		} catch (NotYetConnectedException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 清空消息
//	 *
//	 * @param room_id
//	 *
//	 */
//	public void clearMessage(int room_id, String clear_type) {
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("type", "clear_message");
//		map.put("room_id", room_id);
//		map.put("clear_type", clear_type);
//
//		try {
//			this.send(new JSONObject(map).toString());
//		} catch (NotYetConnectedException e) {
//			e.printStackTrace();
//			Log.v(TAG,
//					"-------Exception-----clearMessage-----------"
//							+ e.getMessage());
//		}
//	}
//
//	/**
//	 * 退出群房间
//	 *
//	 * @param room_id
//	 *
//	 */
//	public void quitRoom(int room_id) {
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("type", "quit_group_room");
//		map.put("room_id", room_id);
//
//		try {
//			this.send(new JSONObject(map).toString());
//		} catch (NotYetConnectedException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 获取某个房间的历史聊天详情
//	 * @param room_id
//	 * @param limit
//	 * @param message_id  当message_id = 0表示首次获取服务器记录
//     */
//	public void getHistory(int room_id, int limit, int message_id) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("type", "get_message_list");
//		map.put("room_id", room_id);
//		if (message_id != 0) {
//			map.put("message_id", message_id);
//		}else {
//			//不指定message_id则表示获取最新的消息列表
//		}
//		//指定获取消息条数
//		map.put("limit", limit);
//		try {
//			this.send(new JSONObject(map).toString());
//		} catch (NotYetConnectedException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public OnWebSocketClientFinishListener listener;
//
//	/**
//	 * 设置socketlistener
//	 *
//	 * @param listener
//	 */
//	public void setOnWebSocketClientFinishListener(
//			OnWebSocketClientFinishListener listener) {
//		this.listener = listener;
//	}
//
//	/**
//	 * 操作群组
//	 *
//	 * @param act
//	 * @param isSuccess
//	 * @param uid
//	 *            被操作的用户id 加人时放空即可，减人时候需要记录id
//	 */
//	private void onFinish(String act, boolean isSuccess, JSONObject data) {
//
//		// if (act.equals("addUser")) {
//		// try {
//		// listener.isAddMemberSuccess(isSuccess,
//		// data.getInt("list_id"));
//		// } catch (JSONException e) {
//		//
//		// e.printStackTrace();
//		// }
//		// } else
//		if (act.equals("moveUser")) {
//			// try {
//			// listener.isDeleteMemberSuccess(data.getInt("uid"),
//			// isSuccess,data.getInt("list_id"));
//			// } catch (JSONException e) {
//			//
//			// e.printStackTrace();
//			// }
//		}
//	}
//
//	/**
//	 * 获取当前socket绑定的activity，一般进入聊天之后会有，退出聊天的时候移除
//	 *
//	 * @return
//	 */
//	public ActivityChatDetail getActivity() {
//		return chatActivity;
//	}
//
//	/**
//	 * 设置当前socket绑定的activity，进入聊天的时候绑定，退出时候设置成空
//	 *
//	 * @param activity
//	 */
//	public void setActivity(Activity activity) {
//		this.chatActivity = (ActivityChatDetail)activity;
//	}
//
//	/**
//	 * 获取当前socket是否有绑定的msgAdapter
//	 *
//	 * @return
//	 */
//	// public AdapterChatMessageList getMsgAdapter() {
//	// return msgAdapter;
//	// }
//
//	// public AdapterChatMsgList getMsgAdapter() {
//	// return msgAdapter;
//	// }
//
//	public AdapterChatDetailList getMsgAdapter() {
//		return msgAdapter;
//	}
//
//	/**
//	 * 获取当前socket是否有绑定的roomAdapter
//	 *
//	 * @return
//	 */
//	public AdapterChatRoomList getRoomAdapter() {
//		return roomAdapter;
//	}
//
//	/**
//	 * 设置socket绑定的聊天的adapter 进入聊天的时候绑定，退出的时候设置成空
//	 *
//	 */
//	// public void setMsgAdapter(AdapterChatMessageList msgAdapter) {
//	// this.msgAdapter = msgAdapter;
//	// }
//
//	// public void setMsgAdapter(AdapterChatMsgList msgAdapter) {
//	// this.msgAdapter = msgAdapter;
//	// }
//
//	public void setMsgAdapter(AdapterSociaxList msgAdapter) {
//		this.msgAdapter = (AdapterChatDetailList)msgAdapter;
//	}
//
//	/**
//	 * 设置socket绑定的房间的adapter 进入页面的时候绑定，退出的时候设置成空
//	 *
//	 *
//	 */
//	public void setRoomAdapter(AdapterSociaxList roomAdapter) {
//		this.roomAdapter = (AdapterChatRoomList)roomAdapter;
//	}
//
//	/*************************************/
//	TimerTask task;
//	Timer timer;
//
//	public void startTimer() {
//		// 如果前一个timer还在，需要先取消
//		if (timer != null) {
//			timer.cancel();
//		}
//		timer = new Timer();
//		/**
//		 * 定时器内容
//		 */
//		task = new ReconnectTimerTask();
//		// 每15秒重新连接一下
//		timer.schedule(task, 0, 15000);
//	}
//
//	class ReconnectTimerTask extends TimerTask {
//		@Override
//		public void run() {
//			if (app.isNetWorkOn()) {
//				app.startSocketClient();
//				if (msgAdapter != null && app.getChatSocketClient() != null) {
//					app.getChatSocketClient().setMsgAdapter(msgAdapter);
//				}
//				if (roomAdapter != null && app.getChatSocketClient() != null) {
//					app.getChatSocketClient().setRoomAdapter(roomAdapter);
//				}
//				if (chatActivity != null && app.getChatSocketClient() != null) {
//					app.getChatSocketClient().setActivity(chatActivity);
//					chatActivity.setNewChatClient();
//				}
//			} else {
//				// 无网状态
//				System.out.println("连接失败，20秒内重新连接");
//				// ChatSocketClientSingle.close();
//			}
//
//			if (app.isConnected())
//				stopTimer();
//		}
//	};
//
//	/**
//	 */
//	public void stopTimer() {
//		if (timer != null) {
//			timer.cancel();
//		}
//	}
//
//	boolean isStopByCheckOut = false;// 判断是否用户退出登陆或者退出app导致的close
//
//	public boolean isStopByCheckOut() {
//		return isStopByCheckOut;
//	}
//
//	public void setStopByCheckOut(boolean isStopByCheckOut) {
//		this.isStopByCheckOut = isStopByCheckOut;
//	}
//
//	/**
//	 * 下载附件
//	 *
//	 * @author Zoey
//	 *
//	 */
//	class DownAttachHandler extends Handler {
//
//		ModelChatMessage message = null;
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if (msg.what == StaticInApp.DOWN_LOAD_ATTACH) {
//
//				try {
//					if (msg.obj == null) {
//					} else {
//
//						message = (ModelChatMessage) msg.obj;
//						int type = msg.arg1;
//						final Downloader downloader = new Downloader();
//
//						File file = new File(
//								Environment.getExternalStorageDirectory(),
//								StaticInApp.cache);
//						if (!file.exists()) {
//							file.mkdirs();
//						}
//						final String path = file.getAbsolutePath();
//
//						if (message.getAttach_url() != null&&path!=null) {
//							// 下载图片
//							if (type == 1) {
//								new Thread(new Runnable() {
//									@Override
//									public void run() {
//										String fileName = "thinksns4_"+ TimeUtils.getTimestamp()+ ".jpg";
//										File file_img = downloader.downLoadFile(message.getAttach_url(), path,fileName);
//										if (file_img==null) {
//											return;
//										}
//										String img_path = file_img.getAbsolutePath();
//
//										if (img_path != null) {
//											message.setLocalPath(img_path);
//											message.setImgWidth((Bimp.getLocalPicWH(img_path,mContext)).first);
//											message.setImgHeight((Bimp.getLocalPicWH(img_path,mContext)).second);
//										}
//
//										// 保存至数据库
//										if (message.getMessage_id() != 0) {
//											long result = msgSqlHelper.addChatMessagetoChatList(
//													message,
//													message.getMessage_id());
//											if (result > 0) {
//
//											} else {
//
//											}
//										}
//									}
//								}).start();
//							}
//							// 下载语音
//							else if (type == 2) {
//								new Thread(new Runnable() {
//									@Override
//									public void run() {
//										String fileName = "thinksns4_"+ TimeUtils.getTimestamp()+ ".mp3";
//										File file_voice = downloader.downLoadFile(message.getAttach_url(), path,fileName);
//										String voice_path = file_voice.getAbsolutePath();
//
//										if (voice_path != null) {
//											message.setLocalPath(voice_path);
//										}
//
//										// 保存至数据库
//										if (message.getMessage_id() != 0) {
//											long result = msgSqlHelper.addChatMessagetoChatList(
//													message,
//													message.getMessage_id());
//											if (result > 0) {
//
//											} else {
//
//											}
//										}
//									}
//								}).start();
//							}
//						}
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}else if (msg.what ==StaticInApp.GET_SERVICE_IMG_WH) {
//				try {
//
//					Object[] objects=(Object[]) msg.obj;
//					final Bitmap bitmap=(Bitmap) objects[0];
//					final ModelChatMessage message_wh=(ModelChatMessage) objects[1];
//					final int type=(Integer) objects[2];
//
//					//获取屏幕宽度
//					int windowWidth=UnitSociax.getWindowWidth(mContext);
//					final int maxWidth=(windowWidth*2)/5;//设置图片的最大宽度为屏幕的2/5
//					int maxHeight=maxWidth;//设置图片的最大高度为屏幕的2/5
//
//					//拿到图片的宽度的高度
//					float picWidth=bitmap.getWidth();
//					float picheight=bitmap.getHeight();
//					float scale=picWidth/picheight;
//
//					picWidth=(Bimp.getScaleImgWH(picWidth,picheight,maxWidth,maxHeight,scale)).first;
//					picheight=(Bimp.getScaleImgWH(picWidth,picheight,maxWidth,maxHeight,scale)).second;
//
//					message_wh.setImgWidth(picWidth);
//					message_wh.setImgHeight(picheight);
//
//					// 保存至数据库
//					if (message_wh.getMessage_id() != 0) {
//						long result = msgSqlHelper.addChatMessagetoChatList(message_wh,message_wh.getMessage_id());
//					}
//
//					Message attach_msg = downAttachHandler.obtainMessage(StaticInApp.DOWN_LOAD_ATTACH);
//					attach_msg.obj = message_wh;
//					attach_msg.arg1 = type;
//					attach_msg.sendToTarget();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	class IntentHandler extends Handler {
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if (msg.what == StaticInApp.INTENT_TO_DETAIL_SINGLE) {
//				try {
//					int room_id = msg.arg1;
//					int to_uid = msg.arg2;
//					String to_name = (String) msg.obj;
//
//					Intent intent = new Intent(mContext,ActivityChatDetail.class);
//					intent.putExtra("room_id", room_id);
//					intent.putExtra("to_uid", to_uid);
//					intent.putExtra("to_name", to_name);
//					intent.putExtra("is_group", 1);
//
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					mContext.startActivity(intent);
//					((Activity) mContext).finish();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			} else if (msg.what == StaticInApp.INTENT_TO_DETAIL_GROUP) {
//				try {
//					int room_id = msg.arg1;
//					String title = (String) msg.obj;
//
//					Intent intent = new Intent(mContext,
//							ActivityChatDetail.class);
//					intent.putExtra("room_id", room_id);
//					intent.putExtra("title", title);
//					intent.putExtra("is_group", 0);
//
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					mContext.startActivity(intent);
//					((Activity) mContext).finish();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			} else if (msg.what == StaticInApp.UPDATE_MSG) {
//				if (msg.arg1 == 1) {
//					// 新消息
////					chatDataChanged(1);
//					if (msg.obj instanceof ModelChatMessage){
//						ActivityHome.getNotifier().onNewMsg((ModelChatMessage) msg.obj);
//
//						Intent intent = new Intent(StaticInApp.UPDATE_UNREAD_MSG);
//						intent.putExtra("count", 1+"/");
//						mContext.sendBroadcast(intent);
//					}
//				}else if(msg.arg1 == 0){
//					//首次获取历史聊天对象时roomAdapter=null
//					if(socketListener != null) {
//						socketListener.onReceiveComplete(msg.obj);
//					}
//				}
//
////				if (roomAdapter != null) {
////					roomAdapter.doUpdataList();
////				}
//			}else if (msg.what == StaticInApp.IMG_TO_BITMAP) {
//				try {
//
//					Object[] objects=(Object[]) msg.obj;
//					final String url=(String) objects[0];
//					final ModelChatMessage message=(ModelChatMessage) objects[1];
//					final int type=(Integer) objects[2];
//
//					if (type==2) {
//						Message attach_msg = downAttachHandler.obtainMessage(StaticInApp.DOWN_LOAD_ATTACH);
//						attach_msg.obj = msg;
//						attach_msg.arg1 = type;
//						attach_msg.sendToTarget();
//					}else if(type==1){
//						new Thread(new Runnable() {
//							@Override
//							public void run() {
//								Bitmap bitmap=Bimp.getBitmap(url);
//
//								Message imgMsg = downAttachHandler.obtainMessage(StaticInApp.GET_SERVICE_IMG_WH);
//								Object objs[]=new Object[3];
//								objs[0]=bitmap;
//								objs[1]=message;
//								objs[2]=type;
//								imgMsg.obj =objs;
//								downAttachHandler.sendMessage(imgMsg);
//							}
//						}).start();
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	// 请求头像
//	public synchronized void getFace(final int uid, final ModelChatMessage msg,
//					final ModelChatUserList room, final String type) {
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
//			}
//
//
//	@Override
//	public void addChatListener(OnChatListener listener) {
//		if (listener != null)
//			chatListeners.add(listener);
//	}
//
//	@Override
//	public void chatDataChanged(int count) {
//		for (OnChatListener listener : chatListeners) {
//			listener.update(count);
//			Log.v("chatSocket", "update");
//		}
//	}
//}
