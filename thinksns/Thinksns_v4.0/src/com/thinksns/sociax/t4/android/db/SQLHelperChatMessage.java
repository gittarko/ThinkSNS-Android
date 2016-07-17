package com.thinksns.sociax.t4.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner.Result;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.thinksns.sociax.db.SqlHelper;
import com.thinksns.sociax.db.ThinksnsTableSqlHelper;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelChatMessage;
import com.thinksns.sociax.t4.model.ModelChatUserList;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 聊天信息数据库
 * 
 * @author wz
 * @date 2014-10-21
 * @version 1.0
 */
public class SQLHelperChatMessage extends SqlHelper {
	private static final String TAG = "ChatMsgSqlhelper";
	public static final String tbChatList = "tb_chat_List";// 聊天列表
	public static final String tbRoomList = "tb_room_List";// 聊天房间

	private static SQLHelperChatMessage instance;
	private ThinksnsTableSqlHelper msgSqlHelper;
	private ListData<SociaxItem> messageDatas;

	public SQLHelperChatMessage(Context context) {
		this.msgSqlHelper = new ThinksnsTableSqlHelper(context, null);
		initChat();
	}

	public static SQLHelperChatMessage getInstance(Context context) {
		if (instance == null) {
			instance = new SQLHelperChatMessage(context);
		}
		return instance;
	}

	//创建本地消息数据库
	private void initChat() {
		/**
		 * 消息列表数据库
		 */
		msgSqlHelper.getWritableDatabase().execSQL("CREATE TABLE if not exists "
				+ tbChatList
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,message_id INTEGER,from_uid INTEGER ,type TEXT,room_id INTEGER,"
				+ " content TEXT,uid INTEGER,from_uface TEXT,from_uname TEXT,attach_url TEXT,to_uid INTEGER,length INTEGER,latitude TEXT,longitude TEXT,location TEXT,attach_id TEXT,localPath TEXT,imgWidth TEXT,imgHeight TEXT,imgSendState TEXT,mtime INTEGER,card_uname TEXT,card_avatar TEXT,card_intro TEXT,card_uid INTEGER,notify_type TEXT,quit_uid INTEGER,quit_uname TEXT,room_master_uid INTEGER,title TEXT,room_add_uid INTEGER,room_add_uname TEXT,room_del_uid INTEGER,room_del_uname TEXT,isNew INTEGER,master_uname TEXT)");

		/**
		 * 房间列表数据库
		 */
		msgSqlHelper.getWritableDatabase().execSQL("CREATE TABLE if not exists "
				+ tbRoomList
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,room_id INTEGER,master_uid INTEGER ,is_group INTEGER,title TEXT,"
				+ " mtime INTEGER,self_index INTEGER,content TEXT,type TEXT,from_uid INTEGER,from_uname TEXT,from_uface TEXT,from_uface_url TEXT," +
				"to_name TEXT,to_uid INTEGER,member_num INTEGER,isNew INTEGER, group_face TEXT)");
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
	 * 
	 * @param message
	 * @return
	 */
		public synchronized long addChatMessagetoChatList(ModelChatMessage message,int message_id) {
		Log.e("SQLHelperChatMessage", "update message :" + message.getContent());
		ContentValues map = message.insertChatListValues();
		
		long result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbChatList, map, " message_id = ?",
				new String[] { message_id + "" });
		if (result <= 0) {
			msgSqlHelper.getWritableDatabase().insert(
					ThinksnsTableSqlHelper.tbChatList, null, map);
		}
		return result;
	}

	/**
	 * 删除表
	 */
	public void deleteTable(String table) {
		msgSqlHelper.getWritableDatabase().execSQL(
				"DROP TABLE if exists " + table);
	}

	/**
	 * 删除聊天房间表
	 */
	public void delRoom() {
		deleteTable(ThinksnsTableSqlHelper.tbRoomList);
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
	public long addRoomToRoomList(ModelChatUserList room, int room_id) {
		
		ContentValues map = room.insertChatListValues();
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, "room_id = ?",
				new String[] { room_id + "" });
		if (result <= 0) {
			msgSqlHelper.getWritableDatabase().insert(
					ThinksnsTableSqlHelper.tbRoomList, null, map);
		}
		return result;
	}

	/**
	 * 插入更新的房间信息，已有房间
	 */
	public long updateHaveRoomToRoomList(ModelChatUserList room, int room_id) {
//		if(haveRoomMsg(room_id, room.getMtime()))
//			return -1;
		int count = getRoomNewCount(room);
		int newCount = room.getIsNew();
		room.setIsNew(count + newCount);
		Log.e("updateRoom", "count:" + count + ", new count:" + room.getIsNew());
		ContentValues map = room.insertRoomListValues();
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, " room_id = ?",
				new String[] { room_id + "" });
		return result;
	}

	//更新聊天对象信息
	public long updateRoomByUser(ModelChatUserList user) {
		ContentValues map = user.insertRoomListValues();
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, " room_id = ?",
				new String[] { user.getRoom_id() + "" });
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
	 * 获取指定房间未读消息数
	 * @param room
	 * @return
	 */
	public int getRoomNewCount(ModelChatUserList room) {
		Cursor cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbRoomList, 
				new String[]{"isNew"}, " room_id = ?", new String[]{room.getRoom_id() + ""}, null, null, null);
		int count = 0;
		if(cursor.moveToFirst()) {
			count = cursor.getInt(cursor.getColumnIndex("isNew"));
		}
		Log.e("SQLHelperChatMessage", "get room count:" + count);
		return count;
	}
	
	public long clearRoomChat(ModelChatUserList room) {
		room.setIsNew(0);
		ContentValues map = room.insertRoomListValues();
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, "room_id = ?",
				new String[] { room.getRoom_id() + "" });
		
		return result;
	}
	
	/**
	 * 插入更新的房间信息，未聊过的房间
	 */
	public long updateNoRoomToRoomList(ModelChatUserList room, int room_id) {
		ContentValues map = room.insertChatListValues();
		int result = msgSqlHelper.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tbRoomList, map, "room_id = ?",
				new String[] { room_id + "" });
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
				"room_id = ?", new String[] { room_id + "" }, null,null, null);
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
				"room_id = ?", new String[] { room_id + "" }, null,null, null);
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
				"room_id = ?", new String[] { room_id + "" }, null,null, null);
	
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
	 * @param chatUser_id
	 *            用户对话id
	 * @param afterTime
	 *            用于聊天adapter中定时刷新footer时候，判断在time之后的聊天信息，放空则默认最新的20条
	 * @param beforeTime
	 *            用于聊天adapter中查看历史记录，不为空则表示查询time之前的历史记录
	 * @return 返回20条记录
	 */

	public ListData<SociaxItem> getChatMessageListByChatToUid(int room_id,
			int afterTime, int message_id) {
		
		ListData<SociaxItem> returnList = new ListData<SociaxItem>();
		ListData<SociaxItem> queyList = new ListData<SociaxItem>();

		Cursor cursor = null;
		if (message_id != 0) {
			try{
				cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbChatList, 
						null, " room_id = ? and message_id < ?", new String[]{room_id + "", message_id + ""}, 
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
					"loginUser_id = '" + Thinksns.getMy().getUid()
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
					"room_id =" + room_id, null, null, null, "message_id DESC");

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
	public ListData<SociaxItem> getMessageById(int message_id){
		
		ListData<SociaxItem> returnList = new ListData<SociaxItem>();
		ListData<SociaxItem> queyList = new ListData<SociaxItem>();
		// 查询最新的记录
		Cursor	cursor = msgSqlHelper.getWritableDatabase().query(
							ThinksnsTableSqlHelper.tbChatList, null,
							"message_id =" + message_id, null, null, null, null);

		while (cursor.moveToNext() && queyList.size() < 20) {
				ModelChatMessage cMsg = new ModelChatMessage();
				appendChatData(cMsg, cursor);
				queyList.add(cMsg);
		}
		if (cursor != null) {
			cursor.close();
		}
		for (int i = queyList.size() - 1; i >= 0; i--) {
			returnList.add(queyList.get(i));
		}
		return returnList;
	}
	
	public ListData<SociaxItem> getChatMessageListById(int room_id,
			int afterTime, int message_id) {
		
		ListData<SociaxItem> returnList = new ListData<SociaxItem>();
		ListData<SociaxItem> queyList = new ListData<SociaxItem>();

		Cursor cursor = null;
		if (message_id != 0) {
			try{
				cursor = msgSqlHelper.getWritableDatabase().query(ThinksnsTableSqlHelper.tbChatList, 
						null, " room_id = ? and message_id < ?", new String[]{room_id + "", message_id + ""}, 
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
					"loginUser_id = '" + Thinksns.getMy().getUid()
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
					"room_id =" + room_id, null, null, null, "message_id DESC");

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

	/**
	 * 单人聊天的时候
	 * 
	 * 根据某个用户id来获取跟他所有对话信息;
	 * 
	 * 获取最新的记录before time=after time=0 获取某条记录time之后的记录 before time=0，after
	 * time=time 获取某条记录time之前的记录 before time=time，after time=0
	 * 
	 * @param chatUser_id
	 *            用户对话id
	 * @param afterTime
	 *            用于聊天adapter中定时刷新footer时候，判断在time之后的聊天信息，放空则默认最新的20条
	 * @param beforeTime
	 *            用于聊天adapter中查看历史记录，不为空则表示查询time之前的历史记录
	 * @return 返回20条记录
	 */

	public ListData<SociaxItem> getRoomList() {

		ListData<SociaxItem> returnList = new ListData<SociaxItem>();
		ListData<SociaxItem> queyList = new ListData<SociaxItem>();

		try {
			Cursor cursor = null;
			// 查询最新的记录
			cursor = msgSqlHelper.getReadableDatabase().query(
					ThinksnsTableSqlHelper.tbRoomList, null, null, null, null,
					null, "mtime ASC");

			while (cursor.moveToNext()) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return returnList;
	}

	public ListData<SociaxItem> getRoomListByRoomId(int room_id) {

		ListData<SociaxItem> returnList = new ListData<SociaxItem>();
		ListData<SociaxItem> queyList = new ListData<SociaxItem>();

		Cursor cursor = null;
		// 查询最新的记录
		cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tbRoomList, null, "room_id =" + room_id,
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

	
	/**
	 * 把数据库取的数据映射到消息对象上
	 * 
	 * @param cMsg
	 * @param cursor
	 */
	private void appendChatData(ModelChatMessage cMsg, Cursor cursor) {
		int from_uid = cursor.getInt(cursor.getColumnIndex("from_uid"));
		if (from_uid != 0) {
			cMsg.setFrom_uid(from_uid);
		}
//		String packid = cursor.getString(cursor.getColumnIndex("packid"));
//		if (packid != null && !packid.equals("") && !packid.equals("null")) {
//			cMsg.setPackid(packid);
//		}
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
	}

	/**
	 * 把数据库取的数据映射到房间对象上
	 * 
	 * @param room
	 * @param cursor
	 */
	private void appendRoomData(ModelChatUserList room, Cursor cursor) {
		int room_id = cursor.getInt(cursor.getColumnIndex("room_id"));
		if (room_id != 0) {
			room.setRoom_id(room_id);
		}
		int master_uid = cursor.getInt(cursor.getColumnIndex("master_uid"));
		if (master_uid != 0) {
			room.setMaster_uid(master_uid);
		}
		int is_group = cursor.getInt(cursor.getColumnIndex("is_group"));
		if (is_group != 0) {
			room.setIs_group(is_group);
		}
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
		String from_uface = cursor.getString(cursor
				.getColumnIndex("from_uface"));
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

	public ListData<SociaxItem> getChatMessageListByChatRoom_id(int room_id,
			int afterTime, int beforeTime) {
		ListData<SociaxItem> returnList = new ListData<SociaxItem>();
		ListData<SociaxItem> queyList = new ListData<SociaxItem>();

		Cursor cursor = null;
		Log.d("sql", "SQLHelperChat--getChatMessageList id=" + room_id
				+ " aftime= " + afterTime + " beftime=" + beforeTime);
		if (beforeTime != 0) {
			// 查询time之前的历史记录
			cursor = msgSqlHelper.getReadableDatabase().query(
					ThinksnsTableSqlHelper.tbChatList,
					null,
					"loginUser_id = '" + Thinksns.getMy().getUid()
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
					"loginUser_id = '" + Thinksns.getMy().getUid()
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
					"loginUser_id = '" + Thinksns.getMy().getUid()
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
		msgSqlHelper.getWritableDatabase().execSQL(
				"delete from tb_chat_detal where site_id = "
						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
						+ Thinksns.getMy().getUid());
	}
	
	/**
	 * 删除聊天房间
	 * 
	 * @param room_id
	 */
	public void clearRoom(int room_id) {
		msgSqlHelper.getWritableDatabase().execSQL(
				"delete from " + ThinksnsTableSqlHelper.tbRoomList
						+ " where room_id = " + room_id);
	}

	@Override
	public void close() {
		msgSqlHelper.close();
	}

	/**
	 * 获取聊天列表，以聊天用户room_id为组，时间降序，获取最新的一条聊天数据 根据room_type区分单聊和群聊
	 * 
	 * @return
	 */
	public ListData<SociaxItem> getChatList() {
		ListData<SociaxItem> returnList = new ListData<SociaxItem>();
		Cursor cursor = null;
		cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tbChatList, null,
				"uid = '" + Thinksns.getMy().getUid() + "' ", null, "room_id",
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
	 * 
	 * @param first
	 *            聊天的最后一条信息,
	 * @return
	 */
	public int clearChatMessageHistory(ModelChatMessage last) {
		// TODO Auto-generated method stub
		try {
			// 执行删除历史记录
			msgSqlHelper.getReadableDatabase().delete(
					ThinksnsTableSqlHelper.tbChatList,
					"loginUser_id = '" + Thinksns.getMy().getUid() + "' and "
							+ "room_id = '" + last.getRoom_id() + "' ", null);

			// 记录历史记录，根据最后一条了聊天信息来获取room_id和名字
			ModelChatMessage record = new ModelChatMessage();
			record.setRoom_id(last.getRoom_id());
			record.setMsgtype("record");
			record.setUid_loginUser(Thinksns.getMy().getUid());
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
	 * 
	 * @param first
	 *            聊天的其中一条信息
	 * @return true 成功 false失败
	 */
	public boolean deleteChat(int room_id) {
		// TODO Auto-generated method stub
		// 执行删除历史记录
		try {
			int column = msgSqlHelper.getReadableDatabase().delete(
					ThinksnsTableSqlHelper.tbChatList,
					"loginUser_id = '" + Thinksns.getMy().getUid() + "' and "
							+ "room_id = '" + room_id + "' ", null);
			Log.v("SQLHeplerChatMessage--deleteChat", "delete cloumn=" + column);
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
					"loginUser_id = '" + Thinksns.getMy().getUid() + "'", null);
			Log.v("SQLHeplerChatMessage--deleteChat", "delete cloumn=" + column);
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
		record.setUid_loginUser(Thinksns.getMy().getUid());
		record.setRoom_type("group");
		record.setRoom_title(title);
		record.setTime((int) (System.currentTimeMillis() / 1000));
		record.setContent("管理员将群名修改为\"" + title + "\"");
		return (int) this.addChatMessage(record);
	}
}
