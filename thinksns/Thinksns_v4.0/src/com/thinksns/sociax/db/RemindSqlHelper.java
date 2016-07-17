package com.thinksns.sociax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thinksns.sociax.modle.NotifyItem;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

public class RemindSqlHelper extends SqlHelper {

	private static RemindSqlHelper instance;
	private ThinksnsTableSqlHelper tableSqlHelper;
	private ListData<SociaxItem> sitesDatas;

	private RemindSqlHelper(Context context) {
		this.tableSqlHelper = new ThinksnsTableSqlHelper(context,
				null);
	}

	public static RemindSqlHelper getInstance(Context context) {
		if (instance == null) {
			instance = new RemindSqlHelper(context);
		}
		return instance;
	}

	public long addRemindMessage(NotifyItem msg) {
		ContentValues map = new ContentValues();
		map.put("name", msg.getName());
		map.put("type", msg.getType());
		map.put("icon", msg.getIcon());
		map.put("counts", msg.getCount());
		map.put("ctime", msg.getTimesTmap());
		map.put("data", msg.getContent());

		long l = tableSqlHelper.getWritableDatabase().insert(
				ThinksnsTableSqlHelper.remindTable, null, map);
		return l;
	}

	public boolean isHasRemind(String name) {
		SQLiteDatabase database = tableSqlHelper.getWritableDatabase();
		String sql = "select * from remind_message where name = ?";
		Cursor cursor = database.rawQuery(sql, new String[] { name });

		boolean result = cursor.moveToFirst();
		cursor.close();

		return result;
	}

	public boolean updataRemind(NotifyItem msg) {
		SQLiteDatabase database = tableSqlHelper.getWritableDatabase();
		// String sql =
		// "update remind_message set counts = 5 , ctime = '13213232123',data ='update1' where name ='atme'  ;";

		ContentValues values = new ContentValues();
		values.put("counts", msg.getCount());
		values.put("ctime", msg.getTimesTmap());
		values.put("data", msg.getContent());
		int result = database.update(ThinksnsTableSqlHelper.remindTable,
				values, "name=?", new String[] { msg.getName() });

		return result > 0;
	}

	public boolean clearCountNum() {
		SQLiteDatabase database = tableSqlHelper.getWritableDatabase();
		// String sql =
		// "update remind_message set counts = 5 , ctime = '13213232123',data ='update1' where name ='atme'  ;";

		ContentValues values = new ContentValues();
		values.put("counts", 0);
		int result = database.update(ThinksnsTableSqlHelper.remindTable,
				values, null, null);
		return result > 0;
	}

	/**
	 * 获得所有的remindMessage
	 * 
	 * @return
	 */
	public ListData<SociaxItem> getAllRemind() {
		SQLiteDatabase database = tableSqlHelper.getWritableDatabase();
		String sql = " select * from remind_message order by ctime desc";
		Cursor cursor = database.rawQuery(sql, null);
		ListData<SociaxItem> remindList = null;
		NotifyItem notifyItem = null;
		if (cursor.moveToFirst()) {
			remindList = new ListData<SociaxItem>();
			do {
				notifyItem = new NotifyItem();
				notifyItem.setName(cursor.getString(cursor
						.getColumnIndex("name")));
				notifyItem.setType(cursor.getString(cursor
						.getColumnIndex("type")));
				notifyItem.setIcon(cursor.getString(cursor
						.getColumnIndex("icon")));
				notifyItem.setCount(cursor.getInt(cursor
						.getColumnIndex("counts")));
				// 设置remind提示信息
				notifyItem.setContent(cursor.getString(cursor
						.getColumnIndex("data")));
				notifyItem.setTimesTmap(cursor.getString(cursor
						.getColumnIndex("ctime")));

				remindList.add(notifyItem);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return remindList;
	}

	public void clearCacheDB() {
		tableSqlHelper.getWritableDatabase().execSQL(
				"delete from " + ThinksnsTableSqlHelper.tbWeiba
						+ " where site_id = "
						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
						+ Thinksns.getMy().getUid());
	}

	@Override
	public void close() {
		tableSqlHelper.close();
	}

}
