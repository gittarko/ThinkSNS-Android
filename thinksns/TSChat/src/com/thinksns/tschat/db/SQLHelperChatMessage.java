package com.thinksns.tschat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.thinksns.tschat.bean.Entity;
import com.thinksns.tschat.bean.ListData;
import com.thinksns.tschat.bean.ModelChatMessage;
import com.thinksns.tschat.bean.ModelChatUserList;
import com.thinksns.tschat.chat.TSChatManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 类说明： 聊天信息数据库
 *
 */
public class SQLHelperChatMessage {
	private static final String TAG = "ChatMsgSqlhelper";
	private static final String DB_NAME = "thinksns";
	private static final int VERSION = 12;

	public static final String tbChatList = "tb_chat_List";// 聊天列表
	public static final String tbRoomList = "tb_room_List";// 聊天房间

	private static SQLHelperChatMessage instance;
	private static ThinksnsTableSqlHelper msgSqlHelper;
	private static int loginUid;

	public SQLHelperChatMessage(Context context) {
		this.msgSqlHelper = new ThinksnsTableSqlHelper(context, DB_NAME, null,VERSION);
		initChat();
	}

	//初始化登录用户的信息
	public static void initData(int uid) {
		loginUid = uid;
		initUnsendMessage();
	}

	//初始化所有未发送成功的消息,将所有之前发送中的消息置为发送失败
	private static int initUnsendMessage() {
		ContentValues map = new ContentValues();
		map.put("send_state", 3);
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbChatList, map, " login_uid = ? and send_state = ?",
				new String[] { loginUid + "", 2 + ""});
		return result;
	}

	public static SQLHelperChatMessage getInstance(Context context) {
		if (instance == null) {
			instance = new SQLHelperChatMessage(context);
		}
		return instance;
	}

	//设置当前使用的数据库对应的用户id
	public void setUid(int uid) {
		loginUid = uid;
		initUnsendMessage();
	}

	//创建本地消息数据库
	private void initChat() {
		/**
		 * 消息列表数据库
		 */
		msgSqlHelper.getWritableDatabase().execSQL("CREATE TABLE if not exists "
				+ tbChatList
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, login_uid INTEGER, message_id INTEGER,from_uid INTEGER ,type TEXT,room_id INTEGER,"
				+ " content TEXT,uid INTEGER,from_uface TEXT,from_uname TEXT,attach_url TEXT,to_uid INTEGER,length INTEGER,latitude TEXT,longitude TEXT,location TEXT,attach_id TEXT,localPath TEXT,imgWidth TEXT," +
				"imgHeight TEXT,imgSendState TEXT,mtime INTEGER,card_uname TEXT,card_avatar TEXT,card_intro TEXT,card_uid INTEGER," +
				"notify_type TEXT,quit_uid INTEGER,quit_uname TEXT,room_master_uid INTEGER,title TEXT,room_add_uid INTEGER,room_add_uname TEXT," +
				"room_del_uid INTEGER,room_del_uname TEXT,isNew INTEGER,master_uname TEXT, description TEXT, is_send INTEGER, send_state INTEGER, pack_id TEXT)");

		/**
		 * 房间列表数据库
		 */
		msgSqlHelper.getWritableDatabase().execSQL("CREATE TABLE if not exists "
				+ tbRoomList
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, login_uid INTEGER, room_id INTEGER,master_uid INTEGER ,is_group INTEGER,title TEXT,"
				+ " mtime INTEGER,self_index INTEGER,content TEXT,type TEXT,from_uid INTEGER,from_uname TEXT,from_uface TEXT,from_uface_url TEXT," +
				"to_name TEXT,to_uid INTEGER,member_num INTEGER,isNew INTEGER, group_face TEXT, logo_id INTGER)");
	}

	/**
	 * 判断表是否存在
	 * @param table
	 * @return
     */
	public boolean tableExits(String table){
		boolean exits = false;
		String sql = "select * from sqlite_master where name=" + "'" + table + "'";
		Cursor cursor = msgSqlHelper.getReadableDatabase().rawQuery(sql, null);

		if(cursor.getCount()!=0){
			exits = true;
		}
		return exits;
	}

	/**
	 * 往聊天详情数据库添加聊天信息
	 * 
	 * @param message
	 * @return
	 */
	public long addChatMessage(ModelChatMessage message) {
		ContentValues map = message.toContentValues();
		long result = msgSqlHelper.getWritableDatabase().insert(
				ThinksnsTableSqlHelper.tbChatList, null, map);
		Log.d("sql", "SQLHelperChatMessage-->addMessage return " + result);
		return result;
	}

	/**
	 * 往房间列表数据库添加房间信息
	 * 
	 * @param room
	 * @return
	 */
	public long addRoomInfo(ModelChatUserList room) {
		ContentValues map = room.toContentValues();
		long result = msgSqlHelper.getWritableDatabase().insert(
				ThinksnsTableSqlHelper.tbRoomList, null, map);
		Log.d("sql", "SQLHelperChatMessage-->addMessage return " + result);
		return result;
	}

	/**
	 * 往消息列表数据库更新信息
	 * 
	 * @param message
	 * @return
	 */
	public synchronized long addChatMessagetoChatListById(ModelChatMessage message,long _id) {
		Log.e("SQLHelperChatMessage", "update message :" + message.getContent());
		ContentValues map = message.insertChatListValues();
		long result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbChatList, map, " _id = ?",
				new String[] { _id + "" });
		if (result <= 0) {
			msgSqlHelper.getWritableDatabase().insert(
					ThinksnsTableSqlHelper.tbChatList, null, map);
		}
		return result;
	}
	
	/**
	 * 往消息列表数据库更新信息
	 * @param message
	 * @return
	 */
	public synchronized static long addChatMessagetoChatList(ModelChatMessage message, int message_id) {
		Log.v(TAG, "ADD MESSAGE INTO CHAT: conent:" + message.getContent() + ", login uid:" + loginUid
				+ ", message id:" + message_id + ", packid:" + message.getPackid());
		ContentValues map = message.insertChatListValues();
		map.put("login_uid", loginUid);
		map.put("uid", loginUid);
		long result = 0;
		if(message.getMessage_id() <= 0) {
				//未发送成功的消息
				result = msgSqlHelper.getWritableDatabase().update(
						ThinksnsTableSqlHelper.tbChatList, map, " login_uid = ? and pack_id = ?",
						new String[]{loginUid + "", message.getPackid() + ""});
		}else {
			result = msgSqlHelper.getWritableDatabase().update(
						ThinksnsTableSqlHelper.tbChatList, map, " login_uid = ? and message_id = ?",
						new String[]{loginUid + "", message_id + ""});
		}

		if (result <= 0) {
			result = msgSqlHelper.getWritableDatabase().insert(
					ThinksnsTableSqlHelper.tbChatList, null, map);
			Log.v(TAG, "MESSAGE NOT EXIST, insert message result:" + result);
		}

		return result;
	}

	//更新发送中状态的消息
	public synchronized static long updateUnSendMessage(ModelChatMessage message) {
		Log.v(TAG, "ADD MESSAGE INTO CHAT: conent:" + message.getContent() + ", login uid:" + loginUid
				+ ", packid:" + message.getPackid());
		ContentValues map = message.insertChatListValues();
		map.put("login_uid", loginUid);
		map.put("uid", loginUid);
		long result = msgSqlHelper.getWritableDatabase().update(
					ThinksnsTableSqlHelper.tbChatList, map, " login_uid = ? and pack_id = ?",
					new String[]{loginUid + "", message.getPackid() + ""});

		if (result <= 0) {
			result = msgSqlHelper.getWritableDatabase().insert(
					ThinksnsTableSqlHelper.tbChatList, null, map);
			Log.v(TAG, "MESSAGE NOT EXIST, insert message result:" + result);
		}

		return result;
	}
	/**
	 * 更新本地图片地址
	 * @param message
     * @return
     */
	public synchronized long updateMessageImageInfo(ModelChatMessage message) {
		ContentValues map = new ContentValues();
		map.put("localPath", message.getLocalPath());
		map.put("imgWidth", message.getImgWidth());
		map.put("imgHeight", message.getImgHeight());

		long result = msgSqlHelper.getWritableDatabase().update(ThinksnsTableSqlHelper.tbChatList, map,
				"login_uid = ? and message_id = ?", new String[]{loginUid + "", message.getMessage_id() + ""});
		return result;
	}
	/**
	 * 删除表
	 */
	public void deleteTable(String table) {
		msgSqlHelper.getWritableDatabase().execSQL(
				"DROP TABLE if exists " + table);
	}
	
	public void createRoom() {
		msgSqlHelper.createRoomTable();
	}

	/**
	 * 往房间列表数据库更新房间信息
	 * 
	 * @param room
	 * @param room_id
	 * @return
	 */
	public static long addRoomToRoomList(ModelChatUserList room, int room_id) {
		int result = 0;
		synchronized (msgSqlHelper) {
			ContentValues map = room.insertChatListValues();
			result = msgSqlHelper.getWritableDatabase().update(
					ThinksnsTableSqlHelper.tbRoomList, map, " login_uid = ? and room_id = ?",
					new String[]{loginUid + "", room_id + ""});
			if (result <= 0) {
				msgSqlHelper.getWritableDatabase().insert(
						ThinksnsTableSqlHelper.tbRoomList, null, map);
			}
		}

		return result;
	}

	/**
	 * 插入更新的房间信息，已有房间
	 * 是否是群聊字段不更新
	 */
	public static long updateHaveRoomToRoomList(ModelChatUserList room, int room_id) {
		int[] result = getRoomNewCount(room);
		if(result != null) {
			room.setIsNew(result[0] + room.getIsNew());
			ContentValues map = room.insertRoomListValues();
			int count = msgSqlHelper.getWritableDatabase().update(
					ThinksnsTableSqlHelper.tbRoomList, map, " login_uid = ? and room_id = ?",
					new String[] { loginUid + "", room_id + "" });
			return count;
		}else {
			return 0;
		}
	}

	/**
	 * 更新房间头像
	 * @param room_id
	 * @param logoId
     * @return
     */
	public static long updateRoomLogo(String room_id, int logoId) {
		ContentValues map = new ContentValues();
		map.put("logo_id", logoId);
		int count = msgSqlHelper.getWritableDatabase().update(
					ThinksnsTableSqlHelper.tbRoomList, map, " login_uid = ? and room_id = ?",
					new String[] { loginUid + "", room_id + "" });
		return count;
	}

	//更新聊天对象信息
	public long updateRoomByUser(ModelChatUserList user) {
		ContentValues map = user.insertRoomListValues();
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, " login_uid = ? and room_id = ?",
				new String[] { loginUid + "", user.getRoom_id() + "" });
		return result;
	}

	public static long updateRommUserFace(ModelChatUserList user) {
		ContentValues map = new ContentValues();
		map.put("from_uface", user.getFrom_uface());
		map.put("from_uface_url", user.getFrom_uface_url());
		if(user.getIs_group() == 0)
			map.put("group_face", user.getGroupFace());
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, " login_uid = ? and room_id = ?",
				new String[] { loginUid + "", user.getRoom_id() + "" });
		return result;
	}

	public long updateRommUserFaceById(int room_id, String url) {
		ContentValues map = new ContentValues();
		map.put("group_face", url);
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, " login_uid = ? and room_id = ?",
				new String[] { loginUid + "", room_id + "" });
		return result;
	}

	/**
	 * 更新房间最后一条内容，时间
	 * @param room
	 * @return
     */
	public static long updateRoomContent(ModelChatUserList room) {
		ContentValues map = new ContentValues();
		map.put("mtime", room.getMtime());
		map.put("content", room.getContent());

		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, " login_uid = ? and room_id = ?",
				new String[] { loginUid + "", room.getRoom_id() + "" });
		return result;

	}

	//房间列表是否存在该消息
	/**
	 * 相同时间，相同房间号的消息视为同一条消息
	 * @param room_id
	 * @param mtime
	 * @return
	 */
	public boolean haveRoomMsg(int room_id, int mtime) {
		Cursor cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbRoomList, 
				null, " room_id = ? and mtime = ?", new String[]{room_id + "", mtime + ""}, 
				null, null, null);

		if(cursor.moveToFirst()) {
			return true;
		}
		cursor.close();
		return false;
	}

	/**
	 *
	 * 指定的房间号是否存在
	 * @param room_id
     * @return
     */
	public static boolean isRoomExist(int room_id) {
		Cursor cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbRoomList,
				null, " login_uid = ? and room_id = ?",
				new String[]{loginUid + "", room_id + ""},
				null, null, null);
		if(cursor.moveToFirst()) {
			return true;
		}
		cursor.close();
		return false;
	}

	/**
	 * 获取指定房间未读消息数
	 * @param room
	 * @return
	 */
	public static int[] getRoomNewCount(ModelChatUserList room) {
		Cursor cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbRoomList, 
				new String[]{"isNew", "is_group"}, " login_uid = ? and room_id = ?",
				new String[]{loginUid + "", room.getRoom_id() + ""}, null, null, null);
		int count = 0;
		int [] result = null;
		if(cursor.moveToFirst()) {
			result = new int[2];
			count = cursor.getInt(cursor.getColumnIndex("isNew"));
			result[0] = count;
			result[1] = cursor.getInt(cursor.getColumnIndex("is_group"));
		}
		Log.e("SQLHelperChatMessage", "get room isNew " + count);

		return result;
	}
	
	public long clearRoomChat(ModelChatUserList room) {
		room.setIsNew(0);
		ContentValues map = room.insertRoomListValues();
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, " login_uid = ? and room_id = ?",
				new String[] { loginUid + "", room.getRoom_id() + "" });
		
		return result;
	}

	/**
	 * 清除房间未读消息数
	 * @param room_id
	 * @return
     */
	public long clearRoomUnreadMsg(int room_id) {
		ContentValues map = new ContentValues();
		map.put("isNew", 0);
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, " login_uid = ? and room_id = ?",
				new String[] { loginUid + "", room_id + "" });

		return result;
	}
	//删除房间消息
	public void deleteMessageById(int room_id) {
		clearRoom(room_id);
		//删除详细聊天记录
		deleteChat(room_id);
	}

	//根据房间ID查找房间信息
	public static ModelChatUserList getRoommById(int room_id) {
		Cursor cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbRoomList,
				null, " login_uid = ? and room_id = ?",
				new String[]{loginUid + "", room_id + ""}, null, null, null);
		ModelChatUserList room = null;
		if(cursor.moveToFirst()) {
			room = new ModelChatUserList();
			appendRoomData(room, cursor);
		}

		if(cursor != null) {
			cursor.close();
		}

		return  room;
	}


	/**
	 * 插入更新的房间信息，未聊过的房间
	 */
	public long updateNoRoomToRoomList(ModelChatUserList room, int room_id) {
		ContentValues map = room.insertChatListValues();
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, " login_uid = ? and room_id = ?",
				new String[] { loginUid + "", room_id + "" });
		return result;
	}

	/**
	 * 
	 * @param list_id
	 *            消息列表对应的id，获取该id下未读的详细消息数
	 * @return
	 */
	public int getNoReadChatMes(int list_id) {
		int counts = 0;
		Cursor cursor = null;
		try {
			String tablename = ThinksnsTableSqlHelper.tbChatList;
			cursor = msgSqlHelper.getReadableDatabase().query(tablename, null,
					"list_id = ?", new String[] { list_id + "" }, null, null,
					null);
			if (cursor.moveToFirst())
				counts = cursor.getInt(cursor.getColumnIndex("msg_counts"));
		} catch (Exception e) {
			Log.d(TAG, "Exceptioncounts=" + counts);
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
		Log.d(TAG, "counts=" + counts);
		return counts;
	}

	/**
	 * 获取房间名称，单聊
	 * @return
	 */
	public String getRoomToName(int room_id){
		
		Cursor cursor = null;
		cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tbRoomList, new String[]{"to_name"},
				" login_uid = ? and room_id = ?", new String[] { loginUid + "", room_id + "" }, null,null, null);
		String to_name=null;
		while (cursor.moveToNext()) {
			to_name=cursor.getString(cursor.getColumnIndex("to_name"));
		}
		cursor.close();
		return to_name;
	}
	
	/**
	 * 获取房间名称，群聊
	 * @return
	 */
	public String getRoomTitle(int room_id){
		
		Cursor cursor = null;
		cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tbRoomList, new String[]{"title"},
				"login_uid = ? and room_id = ?",
				new String[] { loginUid + "", room_id + "" }, null,null, null);
		String title=null;
		while (cursor.moveToNext()) {
			title=cursor.getString(cursor.getColumnIndex("title"));
		}
		cursor.close();
		return title;
	}
	
	/**
	 * 获取房间聊天形式
	 * @return
	 */
	public int getRoomIsGroup(int room_id){
		
		Cursor cursor = null;
		cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tbRoomList, new String[]{"is_group"},
				"login_uid = ? and room_id = ?", new String[] { loginUid + "", room_id + "" }, null,null, null);
	
		int is_group=0;
		while (cursor.moveToNext()) {
			try{
				is_group= cursor.getInt(cursor.getColumnIndex("is_group"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		cursor.close();
		return is_group;
	}
	
	/**
	 * 获取该message_id对应的attach_url
	 * @return
	 */
	public String getMessageAttachUrl(int message_id,int room_id){
		Cursor cursor = null;
		cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbChatList, 
				new String[]{"attach_url"}, " message_id = ? and room_id = ?", new String[]{message_id + "", room_id + ""}, 
				null, null,null);
		String attach_url=null;
		while (cursor.moveToNext()) {
			try{
				attach_url= cursor.getString(cursor.getColumnIndex("attach_url"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		cursor.close();
		return attach_url;
	}
	
	/**
	 * 获取该message_id对应的card_uid
	 * @return
	 */
	public int getMessageCardUid(int message_id,int room_id){
		Cursor cursor = null;
		cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbChatList, 
				new String[]{"card_uid"}, " message_id = ? and room_id = ?", new String[]{message_id + "", room_id + ""}, 
				null, null,null);
		int card_uid=0;
		while (cursor.moveToNext()) {
			try{
				card_uid= cursor.getInt(cursor.getColumnIndex("card_uid"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		cursor.close();
		return card_uid;
	}
	
	/**
	 * 获取该message_id对应的localpath
	 * @return
	 */
	public String getMessageLocalPath(int message_id,int room_id){
		Cursor cursor = null;
		cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbChatList, 
				new String[]{"localPath"}, " message_id = ? and room_id = ?", new String[]{message_id + "", room_id + ""}, 
				null, null,null);
		String localPath=null;
		while (cursor.moveToNext()) {
			try{
				localPath= cursor.getString(cursor.getColumnIndex("localPath"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		cursor.close();
		return localPath;
	}
	
	
	/**
	 * 单人聊天的时候
	 * 
	 * 根据某个用户id来获取跟他所有对话信息;
	 * 
	 * 获取最新的记录before time=after time=0 获取某条记录time之后的记录 before time=0，after
	 * time=time 获取某条记录time之前的记录 before time=time，after time=0
	 *
	 * @param afterTime
	 *            用于聊天adapter中定时刷新footer时候，判断在time之后的聊天信息，放空则默认最新的20条
	 * @return 返回20条记录
	 */

	public ListData<Entity> getChatMessageListByChatToUid(int room_id,
														  int afterTime, int message_id) {
		
		ListData<Entity> returnList = new ListData<Entity>();
		ListData<Entity> queyList = new ListData<Entity>();

		Cursor cursor = null;
		if (message_id != 0) {
			try{
				cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbChatList, 
						null, " login_uid = ? and room_id = ? and message_id < ?",
						new String[]{loginUid + "", room_id + "", message_id + ""},
						null, null, "message_id DESC");
				
				while (cursor.moveToNext() && queyList.size() < 20) {
					ModelChatMessage cMsg = new ModelChatMessage();
					appendChatData(cMsg, cursor);
					queyList.add(cMsg);
				}
				cursor.close();
				Log.d(TAG, "messageDatas=" + queyList.size());
				// 因为查询的是按照message_id降序的前20条，listview显示效果应该是按照message_id升序
				for (int i = queyList.size() - 1; i >= 0; i--) {
					returnList.add(queyList.get(i));
				}
			}catch(Exception e){
				Log.v("sqlException", "--------sqlException------------"+e.getMessage());
			}
		} else if (afterTime != 0) {
			// 查询time之后的记录
			cursor = msgSqlHelper.getWritableDatabase().query(
					ThinksnsTableSqlHelper.tbChatList,
					null,
					"login_uid = '" + loginUid
							+ "' and chatUser_id = '" + room_id
							+ "'and mtime > " + afterTime
							+ " and msgtype != 'record'", null, null, null,
					"mtime ASC");
			while (cursor.moveToNext()) {
				ModelChatMessage cMsg = new ModelChatMessage();
				appendChatData(cMsg, cursor);
				returnList.add(cMsg);
			}
			cursor.close();
			Log.d(TAG, "messageDatas=" + returnList.size());

		} else {
			// 查询最新的记录
			cursor = msgSqlHelper.getWritableDatabase().query(
					ThinksnsTableSqlHelper.tbChatList, null,
					"login_uid = " + loginUid + " and room_id =" + room_id, null, null, null, "message_id DESC");

			while (cursor.moveToNext() && queyList.size() < 20) {
				ModelChatMessage cMsg = new ModelChatMessage();
				appendChatData(cMsg, cursor);
				queyList.add(cMsg);
			}
			if (cursor != null) {
				cursor.close();
			}
			Log.d(TAG, "messageDatas=" + queyList.size());
			// 因为查询的是按照message_id降序的前20条，listview显示效果应该是按照message_id升序
			for (int i = queyList.size() - 1; i >= 0; i--) {
				returnList.add(queyList.get(i));
			}
		}
		
		return returnList;
	}
	
	//查询发送的消息对象
	public static ModelChatMessage getMessageById(int message_id){
		// 查询最新的记录
		Cursor	cursor = msgSqlHelper.getWritableDatabase().query(
							ThinksnsTableSqlHelper.tbChatList, null,
							"login_uid = " + loginUid + " and message_id =" + message_id, null, null, null, null);

		ModelChatMessage cMsg = null;
		if (cursor.moveToNext()) {
				cMsg = new ModelChatMessage();
				appendChatData(cMsg, cursor);
		}
		if (cursor != null) {
			cursor.close();
		}
		return cMsg;
	}

	//查询该房间内的历史记录
	public ListData<Entity> getChatMessageListById(int room_id, int message_id) {
		ListData<Entity> queyList = new ListData<Entity>();
		Cursor cursor = null;
		if (message_id != 0) {
			try{
				//查询message_id之前的消息记录
				cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbChatList, 
						null, " login_uid = ? and room_id = ? and message_id < ?",
						new String[]{loginUid + "", room_id + "", message_id + ""},
						null, null, "mtime DESC");
			}catch(Exception e){
				Log.v("sqlException", "--------sqlException------------"+e.getMessage());
			}
		} else {
			// 查询最新的记录
			cursor = msgSqlHelper.getWritableDatabase().query(
					ThinksnsTableSqlHelper.tbChatList, null,
					"login_uid = " + loginUid + " and room_id =" + room_id, null, null, null, "mtime DESC");
		}

		if(cursor != null) {
			while (cursor.moveToNext() && queyList.size() <= 20) {
				ModelChatMessage cMsg = new ModelChatMessage();
				appendChatData(cMsg, cursor);
				queyList.add(0, cMsg);
			}

			cursor.close();
		}

		Log.d(TAG, "messageDatas=" + queyList.size());
		return queyList;

	}

	public static ArrayList<ModelChatUserList> getRoomList(int mtime, int limit) {
		ArrayList<ModelChatUserList> queyList = new ArrayList<ModelChatUserList>();
		try {
			Cursor cursor = null;
			if(limit != 0) {
				if (mtime == 0) {
					// 查询最新的记录
					cursor = msgSqlHelper.getReadableDatabase().query(
							ThinksnsTableSqlHelper.tbRoomList, null, "login_uid = ?", new String[]{loginUid + ""},
							null, null, "mtime DESC");
				} else {
					cursor = msgSqlHelper.getReadableDatabase().query(
							ThinksnsTableSqlHelper.tbRoomList, null, "login_uid = ? and mtime < ?",
							new String[]{loginUid + "", mtime + ""},
							null, null, "mtime DESC");
				}
			}else {
				//查询全部记录
				cursor = msgSqlHelper.getReadableDatabase().query(
						ThinksnsTableSqlHelper.tbRoomList, null, "login_uid = ?", new String[]{loginUid + ""},
						null, null, "mtime DESC");
			}

			while (cursor.moveToNext() && queyList.size() < 20) {
				ModelChatUserList room = new ModelChatUserList();
				appendRoomData(room, cursor);
				if(TextUtils.isEmpty(room.getContent()))
					continue;
				queyList.add(room);
			}

			if (cursor != null) {
				cursor.close();
			}
			Log.d(TAG, "load local room data:" + queyList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return queyList;
	}

	public ListData<Entity> getRoomListByRoomId(int room_id) {

		ListData<Entity> returnList = new ListData<Entity>();
		ListData<Entity> queyList = new ListData<Entity>();

		Cursor cursor = null;
		// 查询最新的记录
		cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tbRoomList, null, "login_uid = " + loginUid + " and room_id =" + room_id,
				null, null, null, "mtime ASC");

		while (cursor.moveToNext() && queyList.size() < 20) {
			ModelChatUserList room = new ModelChatUserList();
			appendRoomData(room, cursor);
			queyList.add(room);
		}
		if (cursor != null) {
			cursor.close();
		}
		Log.d(TAG, "messageDatas=" + queyList.size());
		// 因为查询的是按照时间降序的前20条，listview显示效果应该是按照时间升序
		for (int i = queyList.size() - 1; i >= 0; i--) {
			returnList.add(queyList.get(i));
		}
		return returnList;
	}

	//查询当前用户所有发送失败的消息
	public static List<ModelChatMessage> getAllSendFailedMessage() {
		List<ModelChatMessage> queyList = new ArrayList<ModelChatMessage>();
		Cursor cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tbChatList, null, "login_uid = " + loginUid + " and send_state = 3",
				null, null, null, "mtime ASC");

		if(cursor != null) {
			while (cursor.moveToNext() && queyList.size() < 20) {
				ModelChatMessage message = new ModelChatMessage();
				appendChatData(message, cursor);
				queyList.add(message);
			}

			cursor.close();
		}

		return queyList;

	}

	//查询房间最后一条的消息
	public synchronized static ModelChatMessage getLastMessageInRoom(int room_id) {
		Cursor cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tbChatList, null, "login_uid = " + loginUid + " and room_id = " + room_id,
				null, null, null, "message_id DESC", "1");
		if(cursor != null) {
			if(cursor.moveToNext()) {
				ModelChatMessage message = new ModelChatMessage();
				appendChatData(message, cursor);
				return message;
			}

			cursor.close();
		}

		return null;

	}

	/**
	 * 把数据库取的数据映射到消息对象上
	 * 
	 * @param cMsg
	 * @param cursor
	 */
	private static void appendChatData(ModelChatMessage cMsg, Cursor cursor) {
		int from_uid = cursor.getInt(cursor.getColumnIndex("from_uid"));
		if (from_uid != 0) {
			cMsg.setFrom_uid(from_uid);
		}

		String from_uname = cursor.getString(cursor.getColumnIndex("from_uname"));
		if (from_uname != null && !from_uname.equals("") && !from_uname.equals("null")) {
			cMsg.setFrom_uname(from_uname);
		}
		String content = cursor.getString(cursor.getColumnIndex("content"));
		if (content != null && !content.equals("") && !content.equals("null")) {
			cMsg.setContent(content);
		}
		String from_uface = cursor.getString(cursor.getColumnIndex("from_uface"));
		if (from_uface != null && !from_uface.equals("")&& !from_uface.equals("null")) {
			cMsg.setFrom_uface(from_uface);
		}
		String master_uname = cursor.getString(cursor.getColumnIndex("master_uname"));
		if (master_uname != null && !master_uname.equals("")&& !master_uname.equals("null")) {
			cMsg.setMaster_uname(master_uname);
		}
		int message_id = cursor.getInt(cursor.getColumnIndex("message_id"));
		if (message_id != 0) {
			cMsg.setMessage_id(message_id);
		}

		Log.v(TAG, "message is " + message_id + ", content is " + content);
		String type = cursor.getString(cursor.getColumnIndex("type"));
		if (type != null && !type.equals("") && !type.equals("null")) {
			cMsg.setType(type);
		}
		int room_id = cursor.getInt(cursor.getColumnIndex("room_id"));
		if (room_id != 0) {
			cMsg.setRoom_id(room_id);
		}
		int uid = cursor.getInt(cursor.getColumnIndex("uid"));
		if (uid != 0) {
			cMsg.setUid(uid);
		}
		int length = cursor.getInt(cursor.getColumnIndex("length"));
		if (length != 0) {
			cMsg.setLength(length);
		}
		String attach_id = cursor.getString(cursor.getColumnIndex("attach_id"));
		if (attach_id != null && !attach_id.equals("")
				&& !attach_id.equals("null")) {
			cMsg.setAttach_id(attach_id);
		}
		String localPath = cursor.getString(cursor.getColumnIndex("localPath"));
		if (localPath != null && !localPath.equals("")
				&& !localPath.equals("null")) {
			cMsg.setLocalPath(localPath);
		}
		float imgWidth = cursor.getFloat(cursor.getColumnIndex("imgWidth"));
		if (imgWidth != 0 ) {
			cMsg.setImgWidth(imgWidth);
		}
		float imgHeight = cursor.getFloat(cursor.getColumnIndex("imgHeight"));
		if (imgHeight != 0 ) {
			cMsg.setImgHeight(imgHeight);
		}
		String imgSendState = cursor.getString(cursor.getColumnIndex("imgSendState"));
		if (imgSendState != null && !imgSendState.equals("")&& !imgSendState.equals("null")) {
			cMsg.setImgSendState(imgSendState);
		}
		String attach_url = cursor.getString(cursor.getColumnIndex("attach_url"));
		if (attach_url != null && !attach_url.equals("")&& !attach_url.equals("null")) {
			cMsg.setAttach_url(attach_url);
		}
		int mtime = cursor.getInt(cursor.getColumnIndex("mtime"));
		if (mtime != 0) {
			cMsg.setMtime(mtime);
		}
		String card_avatar = cursor.getString(cursor
				.getColumnIndex("card_avatar"));
		if (card_avatar != null && !card_avatar.equals("")
				&& !card_avatar.equals("null")) {
			cMsg.setCard_avatar(card_avatar);
		}
		String card_uname = cursor.getString(cursor
				.getColumnIndex("card_uname"));
		if (card_uname != null && !card_uname.equals("")
				&& !card_uname.equals("null")) {
			cMsg.setCard_uname(card_uname);
		}
		String card_intro = cursor.getString(cursor
				.getColumnIndex("card_intro"));
		if (card_intro != null && !card_intro.equals("")
				&& !card_intro.equals("null")) {
			cMsg.setCard_intro(card_intro);
		}
		int card_uid = cursor.getInt(cursor.getColumnIndex("card_uid"));
		if (card_uid != 0) {
			cMsg.setCard_uid(card_uid);
		}
		String notify_type = cursor.getString(cursor
				.getColumnIndex("notify_type"));
		if (notify_type != null) {
			cMsg.setNotify_type(notify_type);
		}
		int quit_uid = cursor.getInt(cursor.getColumnIndex("quit_uid"));
		if (quit_uid != 0) {
			cMsg.setQuit_uid(quit_uid);
		}
		String quit_uname = cursor.getString(cursor
				.getColumnIndex("quit_uname"));
		if (quit_uname != null) {
			cMsg.setQuit_uname(quit_uname);
		}
		int room_master_uid = cursor.getInt(cursor
				.getColumnIndex("room_master_uid"));
		if (room_master_uid != 0) {
			cMsg.setRoom_master_uid(room_master_uid);
		}
		int room_add_uid = cursor.getInt(cursor.getColumnIndex("room_add_uid"));
		if (room_add_uid != 0) {
			cMsg.setRoom_add_uid(room_add_uid);
		}
		int room_del_uid = cursor.getInt(cursor.getColumnIndex("room_del_uid"));
		if (room_del_uid != 0) {
			cMsg.setRoom_del_uid(room_del_uid);
		}
		String room_add_uname = cursor.getString(cursor
				.getColumnIndex("room_add_uname"));
		if (room_add_uname != null) {
			cMsg.setRoom_add_uname(room_add_uname);
		}
		String room_del_uname = cursor.getString(cursor
				.getColumnIndex("room_del_uname"));
		if (room_del_uname != null) {
			cMsg.setRoom_del_uname(room_del_uname);
		}
		double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
		if (latitude != 0) {
			cMsg.setLatitude(latitude);
		}
		double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
		if (longitude != 0) {
			cMsg.setLongitude(longitude);
		}
		String location = cursor.getString(cursor.getColumnIndex("location"));
		if (location != null) {
			cMsg.setLocation(location);
		}
		int isNew = cursor.getInt(cursor.getColumnIndex("isNew"));
		if (isNew != 0) {
			cMsg.setIsNew(isNew);
		}
		cMsg.setDescription(cursor.getString(cursor.getColumnIndex("description")));
		//是否是自己发送的消息
		cMsg.setSend(cursor.getInt(cursor.getColumnIndex("is_send")) ==0 ? false : true);
		//设置消息的发送状态
		int state = cursor.getInt(cursor.getColumnIndex("send_state"));
		cMsg.setSendState(state);
		cMsg.setPackid(cursor.getString(cursor.getColumnIndex("pack_id")));

	}

	/**
	 * 把数据库取的数据映射到房间对象上
	 * 
	 * @param room
	 * @param cursor
	 */
	private static void appendRoomData(ModelChatUserList room, Cursor cursor) {
		int room_id = cursor.getInt(cursor.getColumnIndex("room_id"));
		if (room_id != 0) {
			room.setRoom_id(room_id);
		}
		int master_uid = cursor.getInt(cursor.getColumnIndex("master_uid"));
		if (master_uid != 0) {
			room.setMaster_uid(master_uid);
		}
		int is_group = cursor.getInt(cursor.getColumnIndex("is_group"));
		room.setIs_group(is_group);
		String title = cursor.getString(cursor.getColumnIndex("title"));
		if (title != null && !title.equals("") && !title.equals("null")) {
			room.setTitle(title);
		}
		int mtime = cursor.getInt(cursor.getColumnIndex("mtime"));
		if (mtime != 0) {
			room.setMtime(mtime);
		}
		int self_index = cursor.getInt(cursor.getColumnIndex("self_index"));
		if (self_index != 0) {
			room.setSelf_index(self_index);
		}
		String content = cursor.getString(cursor.getColumnIndex("content"));
		if (content != null && !content.equals("") && !content.equals("null")) {
			room.setContent(content);
		}
		String type = cursor.getString(cursor.getColumnIndex("type"));
		if (type != null && !type.equals("") && !type.equals("null")) {
			room.setType(type);
		}
		int from_uid = cursor.getInt(cursor.getColumnIndex("from_uid"));
		if (from_uid != 0) {
			room.setFrom_uid(from_uid);
		}
		String from_uname = cursor.getString(cursor
				.getColumnIndex("from_uname"));
		if (from_uname != null) {
			room.setFrom_uname(from_uname);
		}
		String from_uface = cursor.getString(cursor.getColumnIndex("from_uface"));
		if (from_uface != null) {
			room.setFrom_uface(from_uface);
		}
		String from_uface_url = cursor.getString(cursor
				.getColumnIndex("from_uface_url"));
		if (from_uface_url != null) {
			room.setFrom_uface_url(from_uface_url);
		}
		String to_name = cursor.getString(cursor.getColumnIndex("to_name"));
		if (to_name != null) {
			room.setTo_name(to_name);
		}
		int to_uid = cursor.getInt(cursor.getColumnIndex("to_uid"));
		if (to_uid != 0) {
			room.setTo_uid(to_uid);
		}
		int member_num = cursor.getInt(cursor.getColumnIndex("member_num"));
		if (member_num != 0) {
			room.setMember_num(member_num);
		}
		int isNew = cursor.getInt(cursor.getColumnIndex("isNew"));
		if (isNew != 0) {
			room.setIsNew(isNew);
		}

		ModelChatMessage lastMessage = new ModelChatMessage();
		lastMessage.setContent(content);
		lastMessage.setMtime(mtime);
		lastMessage.setType(type);
		lastMessage.setFrom_uid(from_uid);
		lastMessage.setFrom_uname(from_uname);
		//默认最后一条消息发送成功
		lastMessage.setSendState(4);
		room.setLastMessage(lastMessage);
		room.setGroupFace(cursor.getString(cursor.getColumnIndex("group_face")));
		room.setLogoId(cursor.getInt(cursor.getColumnIndex("logo_id")));

	}

	/**
	 * 多人聊天的时候
	 * 
	 * 根据Room_id来获取跟他所有对话信息;默认返回20条 获取最新的记录before time=after time=0
	 * 获取某条记录time之后的记录 before time=0，after time=time 获取某条记录time之前的记录 before
	 * time=time，after time=0
	 * 
	 * @param room_id
	 *            房间id
	 * @param afterTime
	 *            用于聊天adapter中定时刷新footer时候，判断在time之后的聊天信息，0则默认最新的20条
	 * @param beforeTime
	 *            用于聊天adapter中查看历史记录，不为0则表示查询time之前的历史记录
	 * @return 返回20条记录
	 */

	public ListData<Entity> getChatMessageListByChatRoom_id(int room_id,
			int afterTime, int beforeTime) {
		ListData<Entity> returnList = new ListData<Entity>();
		ListData<Entity> queyList = new ListData<Entity>();

		Cursor cursor = null;
		Log.d("sql", "SQLHelperChat--getChatMessageList id=" + room_id
				+ " aftime= " + afterTime + " beftime=" + beforeTime);
		if (beforeTime != 0) {
			// 查询time之前的历史记录
			cursor = msgSqlHelper.getReadableDatabase().query(
					ThinksnsTableSqlHelper.tbChatList,
					null,
					"login_uid = '" + loginUid
							+ "' and room_id = '" + room_id + "' and time < "
							+ beforeTime + " and msgtype != 'record'", null,
					null, null, "time DESC");
			while (cursor.moveToNext() && queyList.size() < 20) {
				ModelChatMessage cMsg = new ModelChatMessage();
				appendChatData(cMsg, cursor);
				queyList.add(cMsg);
			}
			cursor.close();
			Log.d(TAG, "messageDatas=" + queyList.size());
			Log.d("slq",
					"SQLHelperChatMessage--getChatMessageList  return list .size="
							+ queyList.size());
			// 因为查询的是按照时间降序的前20条，listview显示效果应该是按照时间升序
			for (int i = queyList.size() - 1; i >= 0; i--) {
				returnList.add(queyList.get(i));
			}
		} else if (afterTime != 0) {
			// 查询time之后的记录
			cursor = msgSqlHelper.getReadableDatabase().query(
					ThinksnsTableSqlHelper.tbChatList,
					null,
					"login_uid = '" + loginUid
							+ "' and room_id = '" + room_id + "'and time > "
							+ afterTime + " and msgtype != 'record'", null,
					null, null, "time ASC");
			while (cursor.moveToNext()) {
				ModelChatMessage cMsg = new ModelChatMessage();
				appendChatData(cMsg, cursor);
				returnList.add(cMsg);
			}
			cursor.close();
			Log.d(TAG, "messageDatas=" + returnList.size());
		} else {
			// 查询最新的记录
			cursor = msgSqlHelper.getReadableDatabase().query(
					ThinksnsTableSqlHelper.tbChatList,
					null,
					"login_uid = '" + loginUid
							+ "' and room_id = '" + room_id + "'"
							+ " and msgtype != 'record'", null, null, null,
					"time DESC");

			while (cursor.moveToNext() && queyList.size() < 20) {
				ModelChatMessage cMsg = new ModelChatMessage();

				appendChatData(cMsg, cursor);
				queyList.add(cMsg);
			}
			cursor.close();
			Log.d(TAG, "messageDatas=" + queyList.size());
			// 因为查询的是按照时间降序的前20条，listview显示效果应该是按照时间升序
			for (int i = queyList.size() - 1; i >= 0; i--) {
				returnList.add(queyList.get(i));
			}

		}
		return returnList;
	}

	/**
	 * 删除我的聊天的数据库缓存
	 */
	public void clearCacheDB() {
//		msgSqlHelper.getWritableDatabase().execSQL(
//				"delete from tb_chat_detal where site_id = "
//						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
//						+ Thinksns.getMy().getUid());
	}
	
	/**
	 * 删除聊天房间
	 * @param room_id
	 */
	public void clearRoom(int room_id) {
		try {
			int column = msgSqlHelper.getReadableDatabase().delete(
					ThinksnsTableSqlHelper.tbRoomList,
					"login_uid = '" + loginUid + "' and " + "room_id = '" + room_id + "' ", null);
			Log.v(TAG, "DELETE ROOM: room id is " + room_id + ", result " + column);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		msgSqlHelper.close();
	}

	/**
	 * 获取聊天列表，以聊天用户room_id为组，时间降序，获取最新的一条聊天数据 根据room_type区分单聊和群聊
	 * 
	 * @return
	 */
	public ListData<Entity> getChatList() {
		ListData<Entity> returnList = new ListData<Entity>();
		Cursor cursor = null;
		cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tbChatList, null,
				"login_uid = '" + loginUid + "' ", null, "room_id",
				null, "mtime DESC");
		while (cursor.moveToNext()) {
			ModelChatMessage cMsg = new ModelChatMessage();
			appendChatData(cMsg, cursor);
			returnList.add(cMsg);
		}
		cursor.close();
		return returnList;
	}

	/**
	 * 清理历史记录 需要保存一条空记录来记录房间信息
	 *   聊天的最后一条信息,
	 * @return
	 */
	public int clearChatMessageHistory(ModelChatMessage last) {
		// TODO Auto-generated method stub
		try {
			// 执行删除历史记录
			int uid = TSChatManager.getLoginUser().getUid();
			msgSqlHelper.getReadableDatabase().delete(
					ThinksnsTableSqlHelper.tbChatList,
					"login_uid = '" + loginUid + "' and "
							+ "room_id = '" + last.getRoom_id() + "' ", null);

			// 记录历史记录，根据最后一条了聊天信息来获取room_id和名字
			ModelChatMessage record = new ModelChatMessage();
			record.setRoom_id(last.getRoom_id());
			record.setMsgtype("record");
			record.setUid_loginUser(uid);
			record.setRoom_type(last.getRoom_type());
			record.setTime(last.getTime());
			if (last.getRoom_type().equals("group")) {// 清理的是群组聊天的话，需要记录群组title
				record.setRoom_title(last.getRoom_title());
			} else {// 个人聊天需要记录对方的uid，还有uface
				record.setUid_chatUser(last.getUid_chatUser());
				record.setChatUerFace(last.getChatUserFace());
				record.setChatUserName(last.getChatUserName());
			}
			// 执行添加
			return (int) this.addChatMessage(record);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 删除某个聊天
	 * @return true 成功 false失败
	 */
	public boolean deleteChat(int room_id) {
		// 执行删除历史记录
		try {
			int column = msgSqlHelper.getReadableDatabase().delete(
					ThinksnsTableSqlHelper.tbChatList,
					"login_uid = '" + loginUid + "' and "
							+ "room_id = '" + room_id + "' ", null);
			Log.v(TAG, "DELETE MESSAGE FROM ROOM: room id is " + room_id + ", result " + column);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除我的所有聊天记录
	 * 
	 * @return
	 */
	public boolean clearMyChatList() {
		// 执行删除历史记录
		try {
			int column = msgSqlHelper.getReadableDatabase().delete(
					ThinksnsTableSqlHelper.tbChatList,
					"login_uid = '" + loginUid + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据room_id修改群名称
	 * 实际上是插入一条msgtype为record的特殊聊天信息就可以，因为每次获取聊天列表都是按照room_id获取最新的聊天列表
	 * 
	 * @param title
	 * @param room_id
	 */
	public int changeChatName(String title, int room_id) {
		ModelChatMessage record = new ModelChatMessage();
		record.setRoom_id(room_id);
		record.setMsgtype("record");
		record.setUid_loginUser(TSChatManager.getLoginUser().getUid());
		record.setRoom_type("group");
		record.setRoom_title(title);
		record.setTime((int) (System.currentTimeMillis() / 1000));
		record.setContent("管理员将群名修改为\"" + title + "\"");
		return (int) this.addChatMessage(record);
	}
}
