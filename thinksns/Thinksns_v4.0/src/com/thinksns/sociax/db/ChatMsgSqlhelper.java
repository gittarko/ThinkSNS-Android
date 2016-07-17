package com.thinksns.sociax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.thinksns.sociax.modle.Message;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

public class ChatMsgSqlhelper extends SqlHelper {
	private static final String TAG = "ChatMsgSqlhelper";
	private static ChatMsgSqlhelper instance;
	private ThinksnsTableSqlHelper msgSqlHelper;
	private ListData<SociaxItem> messageDatas;

	public ChatMsgSqlhelper(Context context) {
		this.msgSqlHelper = new ThinksnsTableSqlHelper(context, null);
	}

	public static ChatMsgSqlhelper getInstance(Context context) {
		if (instance == null) {
			instance = new ChatMsgSqlhelper(context);
		}

		return instance;
	}

	public long addMessage(Message message) {
		ContentValues map = new ContentValues();
		map.put("list_id", message.getListId());
		map.put("room_id", message.getRoom_id());
		map.put("from_client_id", message.getFrom_client_id());
		map.put("from_uid", message.getFrom_uid());
		map.put("from_uname", message.getFrom_uname());
		map.put("min_max", message.getMin_max());
		map.put("msgtype", message.getMsgtype());
		map.put("to_client_id", message.getTo_client_id());
		map.put("to_uid", message.getTo_uid());
		map.put("to_uname", message.getTo_uname());
		map.put("type", message.getType());
		map.put("content", message.getContent());
		map.put("time", message.getTime());
		map.put("forNew", message.getForNew());
		map.put("fromFace", message.getFromFace());
		map.put("type", message.getType());
		map.put("degree", message.getDegree());
		map.put("latitude", message.getLatitude());
		map.put("longitude", message.getLongitude());
		Log.d(TAG, "Thinksns.getMySite()=" + Thinksns.getMySite());
		if (Thinksns.getMySite() != null) {
			map.put("site_id", Thinksns.getMySite().getSite_id());

		} else {
			map.put("site_id", 0);
		}
		map.put("my_uid", Thinksns.getMy().getUid());

		return msgSqlHelper.getWritableDatabase().insert(
				ThinksnsTableSqlHelper.tbChatMsg, null, map);
	}

	public ListData<SociaxItem> getMessageChatList() {
		ListData<SociaxItem> messageDatas = new ListData<SociaxItem>();
		String Sql = "SELECT * FROM " + ThinksnsTableSqlHelper.tbChatMsg
				+ " WHERE login_uid = '" + Thinksns.getMy().getUid() + "'"
				+ " GROUP BY to_uid,from_uid  HAVING  MAX(time)";

		// String Sql = "SELECT * FROM "+ ThinksnsTableSqlHelper.tbChatMsg
		// +" AS list GROUP BY to_uid,from_uid  HAVING time =(SELECT MAX(time) FROM list WHERE "
		// +
		// "from_uid = list.from_uid AND to_uid = list.to_uid)";
		Cursor cursor = msgSqlHelper.getReadableDatabase().rawQuery(Sql, null);
		while (cursor.moveToNext()) {
			Message message = new Message();
			message.setType(cursor.getString(cursor.getColumnIndex("type")));
			message.setListId(cursor.getInt(cursor.getColumnIndex("list_id")));
			message.setMeesageId(cursor.getInt(cursor
					.getColumnIndex("message_id")));
			message.setFrom_uid((cursor.getInt(cursor
					.getColumnIndex("from_uid"))));
			message.setForNew((cursor.getInt(cursor.getColumnIndex("forNew"))));
			message.setContent(cursor.getString(cursor
					.getColumnIndex("content")));
			message.setMin_max(cursor.getString(cursor
					.getColumnIndex("min_max")));
			message.setTo_uid(cursor.getInt(cursor.getColumnIndex("to_uid")));
			message.setFromFace(cursor.getString(cursor
					.getColumnIndex("fromFace")));
			message.setMsgtype(cursor.getString(cursor
					.getColumnIndex("msgtype")));
			message.setTo_client_id(cursor.getString(cursor
					.getColumnIndex("to_client_id")));
			message.setFrom_uname(cursor.getString(cursor
					.getColumnIndex("from_uname")));
			message.setTime(cursor.getInt(cursor.getColumnIndex("time")));
			message.setDegree(cursor.getInt(cursor.getColumnIndex("degree")));
			message.setLongitude(cursor.getDouble(cursor
					.getColumnIndex("longitude")));
			message.setLatitude(cursor.getDouble(cursor
					.getColumnIndex("latitude")));
			Log.d(TAG, "message,,,," + message.toString());
			messageDatas.add(message);
		}
		;
		cursor.close();
		// cursor2.close();
		Log.d(TAG, "messageDatas=" + messageDatas.size());
		return messageDatas;
	}

	public ListData<Message> getMessageList() {
		Cursor cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tbChatMsg,
				null,
				"to_uid = '" + Thinksns.getMySite().getUid()
						+ "' and to_uname = '" + Thinksns.getMy().getUserName()
						+ "'", null, null, null, "message_id ASC");
		ListData<Message> messageDatas = new ListData<Message>();

		if (cursor.moveToFirst()) {
			do {
				Message message = new Message();
				message.setType(cursor.getString(cursor.getColumnIndex("type")));
				message.setListId(cursor.getInt(cursor
						.getColumnIndex("list_id")));
				message.setMeesageId(cursor.getInt(cursor
						.getColumnIndex("message_id")));
				message.setFrom_uid((cursor.getInt(cursor
						.getColumnIndex("from_uid"))));
				message.setContent(cursor.getString(cursor
						.getColumnIndex("content")));
				message.setMin_max(cursor.getString(cursor
						.getColumnIndex("min_max")));
				message.setTo_uid(cursor.getInt(cursor.getColumnIndex("to_uid")));
				message.setFromFace(cursor.getString(cursor
						.getColumnIndex("from_face")));
				message.setMsgtype(cursor.getString(cursor
						.getColumnIndex("msgtype")));
				message.setTo_client_id(cursor.getString(cursor
						.getColumnIndex("to_client_id")));
				message.setFrom_uname(cursor.getString(cursor
						.getColumnIndex("from_uname")));
				message.setTime(cursor.getInt(cursor.getColumnIndex("time")));
				// message.setCtime(cursor.getString(cursor
				// .getColumnIndex("ctime")));
				message.setDegree(cursor.getInt(cursor.getColumnIndex("degree")));
				message.setLongitude(cursor.getDouble(cursor
						.getColumnIndex("longitude")));
				message.setLatitude(cursor.getDouble(cursor
						.getColumnIndex("latitude")));
				messageDatas.add(message);
			} while (cursor.moveToNext());
		}
		return messageDatas;
	}

	public ListData<SociaxItem> getMessageListById() {
		Cursor cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tbChatMsg, null,
				"my_uid = '" + Thinksns.getMy().getUid() + "'", null, null,
				null, "message_id ASC");
		ListData<SociaxItem> messageDatas = new ListData<SociaxItem>();
		Log.d("ChatInfoActivity",
				"cursor.getColumnCount()=" + cursor.getColumnCount());

		while (cursor.moveToNext()) {
			Message message = new Message();
			message.setType(cursor.getString(cursor.getColumnIndex("type")));
			message.setListId(cursor.getInt(cursor.getColumnIndex("list_id")));
			message.setMeesageId(cursor.getInt(cursor
					.getColumnIndex("message_id")));
			message.setFrom_uid((cursor.getInt(cursor
					.getColumnIndex("from_uid"))));
			message.setContent(cursor.getString(cursor
					.getColumnIndex("content")));
			message.setMin_max(cursor.getString(cursor
					.getColumnIndex("min_max")));
			message.setTo_uid(cursor.getInt(cursor.getColumnIndex("to_uid")));
			message.setFromFace(cursor.getString(cursor
					.getColumnIndex("fromFace")));
			message.setMsgtype(cursor.getString(cursor
					.getColumnIndex("msgtype")));
			message.setTo_client_id(cursor.getString(cursor
					.getColumnIndex("to_client_id")));
			message.setFrom_uname(cursor.getString(cursor
					.getColumnIndex("from_uname")));
			message.setTime(cursor.getInt(cursor.getColumnIndex("time")));
			message.setDegree(cursor.getInt(cursor.getColumnIndex("degree")));
			message.setLongitude(cursor.getDouble(cursor
					.getColumnIndex("longitude")));
			message.setLatitude(cursor.getDouble(cursor
					.getColumnIndex("latitude")));
			messageDatas.add(message);
		}
		Log.d("ChatInfoActivity", "messageDatas" + messageDatas.size());
		return messageDatas;
	}

	public int getMsgListSize(int listId) {
		if (Thinksns.getMySite() != null) {
			Cursor cursor = msgSqlHelper.getWritableDatabase().rawQuery(
					"select count(*) from " + ThinksnsTableSqlHelper.tbChatMsg
							+ " where site_id = "
							+ Thinksns.getMySite().getSite_id()
							+ " and from_uid = " + Thinksns.getMy().getUid()
							+ " and list_id =" + listId, null);
			if (cursor.moveToFirst()) {
				return cursor.getInt(0);
			} else {
				return 0;
			}
		}
		return 0;
	}

	public boolean hasMessage(int message_id) {
		SQLiteDatabase database = msgSqlHelper.getWritableDatabase();
		String sql = "select * from " + ThinksnsTableSqlHelper.tbChatMsg
				+ " where message_id = ?";
		Cursor cursor = database.rawQuery(sql,
				new String[] { String.valueOf(message_id) });

		boolean result = cursor.moveToFirst();
		cursor.close();
		return result;
	}

	public boolean deleteMsg(int count, int listId) {
		if (count > 19) {
			msgSqlHelper.getWritableDatabase().execSQL(
					"delete from " + ThinksnsTableSqlHelper.tbChatMsg
							+ " where site_id = "
							+ Thinksns.getMySite().getSite_id()
							+ " and my_uid = " + Thinksns.getMy().getUid()
							+ " and list_id =" + listId);
		} else if (count > 0 && count < 20) {
			String sql = "delete from " + ThinksnsTableSqlHelper.tbChatMsg
					+ " where message_id in (select weiboId from "
					+ ThinksnsTableSqlHelper.tbChatMsg + " where site_id = "
					+ Thinksns.getMySite().getSite_id() + " and my_uid = "
					+ Thinksns.getMy().getUid() + " and list_id =" + listId
					+ " order by list_id limit " + count + ")";
			msgSqlHelper.getWritableDatabase().execSQL(sql);
		}
		return false;
	}

	/**
	 * 删除数据库缓存
	 */
	public void clearCacheDB() {
		msgSqlHelper.getWritableDatabase().execSQL(
				"delete from tb_chat_detal where site_id = "
						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
						+ Thinksns.getMy().getUid());
	}

	@Override
	public void close() {
		msgSqlHelper.close();
	}

	private int isread(boolean isread) {
		if (isread) {
			return 1;
		} else {
			return 0;
		}
	}

	private boolean forRead(int isread) {
		if (isread == 1) {
			return true;
		} else {
			return false;
		}
	}

	private int isLast(boolean islast) {
		if (islast) {
			return 1;
		} else {
			return 0;
		}
	}

	private boolean forLast(int islast) {
		if (islast == 1) {
			return true;
		} else {
			return false;
		}
	}

	private int isOnlyOne(boolean isOnly) {
		if (isOnly) {
			return 1;
		} else {
			return 0;
		}
	}

	private boolean forOnlyOne(int isOnly) {
		if (isOnly == 1) {
			return true;
		} else {
			return false;
		}
	}

}
