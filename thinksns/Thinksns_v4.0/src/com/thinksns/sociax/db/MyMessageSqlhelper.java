package com.thinksns.sociax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thinksns.sociax.modle.Message;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

public class MyMessageSqlhelper extends SqlHelper {
	private static MyMessageSqlhelper instance;
	private ThinksnsTableSqlHelper msgSqlHelper;
	private ListData<SociaxItem> messageDatas;

	private MyMessageSqlhelper(Context context) {
		this.msgSqlHelper = new ThinksnsTableSqlHelper(context, null);
	}

	public static MyMessageSqlhelper getInstance(Context context) {
		if (instance == null) {
			instance = new MyMessageSqlhelper(context);
		}

		return instance;
	}

	/**
	 * 添加消息
	 * 
	 * @param message
	 *            消息对象
	 * @return
	 */
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
		// map.put("member_uid", message.getMemberUid());
		// map.put("message_id", message.getMeesageId());
		map.put("new", message.getForNew());
		// map.put("message_num", message.getMessageNum());
		// map.put("member_num", message.getMemeberNum());
		// map.put("list_ctime", message.getListCtime());
		// map.put("last_message", message.getLastMessage().toString());
		// map.put("from_uid", message.getFromUid());
		// map.put("to_uid", message.getToUid());
		// map.put("content", message.getContent());
		// map.put("title", message.getTitle());
		// map.put("from_uname", message.getFromUname());
		map.put("from_face", message.getFromFace());
		// map.put("mtime", message.getMtime());
		// map.put("ctime", message.getCtime());
		map.put("type", message.getType());
		map.put("degree", message.getDegree());
		map.put("latitude", message.getLatitude());
		map.put("longitude", message.getLongitude());
		map.put("site_id", Thinksns.getMySite().getSite_id());
		map.put("my_uid", Thinksns.getMy().getUid());

		return msgSqlHelper.getWritableDatabase().insert(
				ThinksnsTableSqlHelper.myMessageTable, null, map);
	}

	/**
	 * 获取缓存的消息
	 * 
	 * @return
	 */
	public ListData<SociaxItem> selectMessage() {
		Cursor cursor = msgSqlHelper.getReadableDatabase().query(
				ThinksnsTableSqlHelper.myMessageTable,
				null,
				"site_id = " + Thinksns.getMySite().getSite_id()
						+ " and my_uid = " + Thinksns.getMy().getUid(), null,
				null, null, "mtime DESC");
		messageDatas = new ListData<SociaxItem>();

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
				// message.setTitle(cursor.getString(cursor
				// .getColumnIndex("title")));
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
				// message.setListId(cursor.getInt(cursor
				// .getColumnIndex("list_id")));
				// message.setFromUid(cursor.getInt(cursor
				// .getColumnIndex("from_uid")));
				// message.setContent(cursor.getString(cursor
				// .getColumnIndex("content")));
				// message.setTitle(cursor.getString(cursor
				// .getColumnIndex("title")));
				// message.setToUid(cursor.getInt(cursor.getColumnIndex("to_uid")));
				// message.setFromFace(cursor.getString(cursor
				// .getColumnIndex("from_face")));
				// message.setToUserUrl(cursor.getString(cursor
				// .getColumnIndex("to_face")));
				// message.setFromUname(cursor.getString(cursor
				// .getColumnIndex("from_uname")));
				// message.setToName(cursor.getString(cursor
				// .getColumnIndex("to_uname")));
				// message.setCtime(cursor.getString(cursor
				// .getColumnIndex("ctime")));
				// message.setMtime(cursor.getInt(cursor.getColumnIndex("mtime")));
				messageDatas.add(message);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return messageDatas;
	}

	/**
	 * 获取消息数量
	 * 
	 * @return
	 */
	public int getMsgListSize() {
		Cursor cursor = msgSqlHelper.getWritableDatabase().rawQuery(
				"select count(*) from user_message where site_id = "
						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
						+ Thinksns.getMy().getUid(), null);
		if (cursor.moveToFirst()) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	public boolean hasMessList(int listId) {
		SQLiteDatabase database = msgSqlHelper.getWritableDatabase();
		String sql = "select * from user_message where list_id = ?";
		Cursor cursor = database.rawQuery(sql,
				new String[] { String.valueOf(listId) });

		boolean result = cursor.moveToFirst();
		cursor.close();
		return result;
	}

	public boolean deleteMessage(int count) {
		if (count > 19) {
			msgSqlHelper.getWritableDatabase().execSQL(
					"delete from user_message where site_id = "
							+ Thinksns.getMySite().getSite_id()
							+ " and my_uid = " + Thinksns.getMy().getUid());
		} else if (count > 0 && count < 20) {
			String sql = "delete from user_message where list_id in (select list_id from user_message where site_id = "
					+ Thinksns.getMySite().getSite_id()
					+ " and my_uid = "
					+ Thinksns.getMy().getUid()
					+ " order by list_id limit "
					+ count + ")";
			msgSqlHelper.getWritableDatabase().execSQL(sql);
		}
		return false;
	}

	/**
	 * 删除数据库缓存
	 */
	public void clearCacheDB() {
		msgSqlHelper.getWritableDatabase().execSQL(
				"delete from user_message where site_id = "
						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
						+ Thinksns.getMy().getUid());
	}

	@Override
	public void close() {
		msgSqlHelper.close();
	}

}
