package com.thinksns.sociax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelImageAttach;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

public class AttachSqlHelper extends SqlHelper {

	private static AttachSqlHelper instance;
	private ThinksnsTableSqlHelper tableSqlHelper;

	public AttachSqlHelper(Context context) {
		this.tableSqlHelper = new ThinksnsTableSqlHelper(context,
				null);
	}

	public static AttachSqlHelper getInstance(Context context) {
		if (instance == null) {
			instance = new AttachSqlHelper(context);
		}
		return instance;
	}

	public long addAttach(ModelImageAttach iAttach, int type) {
		ContentValues values = new ContentValues();
		values.put("weiboId", iAttach.getWeiboId());
		values.put("type", type);
		values.put("name", iAttach.getName());
		values.put("small", iAttach.getSmall());
		values.put("middle", iAttach.getMiddle());
		values.put("normal", iAttach.getOrigin());
		values.put("attach_width", iAttach.getAttach_origin_width());
		values.put("attach_height", iAttach.getAttach_origin_height());
		values.put("site_id", Thinksns.getMySite().getSite_id());
		values.put("my_uid", Thinksns.getMy().getUid());
		if(getAttachByWeiboId(iAttach.getWeiboId())) {
			//更新
			return tableSqlHelper.getWritableDatabase().update(
					ThinksnsTableSqlHelper.attachTable, values, "weiboId = ? ", 
					new String[] {iAttach.getWeiboId() + ""});
		}

		long result = tableSqlHelper.getWritableDatabase().insert(
				ThinksnsTableSqlHelper.attachTable, null, values);
		return result;
	}


	
	public boolean getAttachByWeiboId(int weiboId) {

		SQLiteDatabase database = tableSqlHelper.getWritableDatabase();
		String sql = "select * from attach where weiboId = ?";
		Cursor cursor = database.rawQuery(sql,
				new String[] { String.valueOf(weiboId) });

		boolean result = cursor.moveToFirst();
		cursor.close();

		return result;
	}

	/**
	 * 根据微博id来获得微博的附件
	 * 
	 * @param weiboId
	 * @return
	 */
	public ListData<ModelImageAttach> getAttachsByWeiboId(int weiboId) {
		SQLiteDatabase database = tableSqlHelper.getWritableDatabase();
		String sql = "select * from attach where weiboId = ?";
		Cursor cursor = database.rawQuery(sql,
				new String[] { String.valueOf(weiboId) });
		ListData<ModelImageAttach> lAttachs = null;
		ModelImageAttach iAttach = null;
		if (cursor.moveToFirst()) {
			lAttachs = new ListData<ModelImageAttach>();
			do {
				iAttach = new ModelImageAttach();
				iAttach.setWeiboId(Integer.parseInt(cursor.getString(cursor
						.getColumnIndex("weiboId"))));
				iAttach.setName(cursor.getString(cursor.getColumnIndex("name")));
				iAttach.setMiddle(cursor.getString(cursor
						.getColumnIndex("middle")));
				iAttach.setSmall(cursor.getString(cursor.getColumnIndex("small")));
				iAttach.setOrigin(cursor.getString(cursor.getColumnIndex("normal")));
				iAttach.setAttach_origin_width(cursor.getString(cursor.getColumnIndex("attach_width")));
				iAttach.setAttach_origin_height(cursor.getString(cursor.getColumnIndex("attach_height")));
				lAttachs.add(iAttach);
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		return lAttachs;
	}

	public void clearCacheDB(int type) {
		tableSqlHelper.getWritableDatabase().execSQL(
				"delete from attach where type = " + type + "  and site_id = "
						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
						+ Thinksns.getMy().getUid());
	}

	/**
	 * 删除数据库缓存
	 */
	public void clearCacheDB() {
		tableSqlHelper.getWritableDatabase().execSQL(
				"delete from attach where site_id = "
						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
						+ Thinksns.getMy().getUid());
	}

	@Override
	public void close() {
		tableSqlHelper.close();
	}

}
