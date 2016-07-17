package com.thinksns.sociax.db;

import com.thinksns.sociax.modle.Weiba;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelWeiba;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author povol
 * @date Dec 19, 2012
 * @version 1.0
 */
public class WeibaSqlHelper extends ThinksnsTableSqlHelper {
	private static final String TABLE_NAME = "tbWeiba";
	private static final String weibaId = "weibaId";
	private static final String weibaName = "weibaName";
	private static final String weibaIntro = "weibaIntro";
	private static final String loginUid = "loginUid";
	private static final String isFollow = "isFollow";
	private static final String weibaJson = "weibaJson";


	private static WeibaSqlHelper instance;
	public WeibaSqlHelper(Context context) {
		super(context, null);
		onCreate(this.getReadableDatabase());
	}

	public static WeibaSqlHelper getInstance(Context context) {
		if (instance == null) {
			instance = new WeibaSqlHelper(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建微吧表
		String sql = "create Table if not exists " + TABLE_NAME + " ( " + weibaId + " integer, " + loginUid + " integer, " +
				weibaName + " varchar, " + weibaIntro + " TEXT, " + isFollow + " boolean, " + weibaJson + " text)";
		db.execSQL(sql);
	}

	//保存一条微吧数据
	public long addWeiba(ModelWeiba weiba) {
		ContentValues values = new ContentValues();
		values.put(weibaId, weiba.getWeiba_id());
		values.put(weibaName, weiba.getWeiba_name());
		values.put(weibaIntro, weiba.getIntro());
		values.put(isFollow, weiba.isFollow());
		values.put(loginUid, Thinksns.getMy().getUid());
		values.put(weibaJson, weiba.getWeibaJson());
		//先更新本地数据库
		long update = getWritableDatabase().update(TABLE_NAME, values, weibaId + " = ?", new String[]{weiba.getWeiba_id() + ""});
		if(update <= 0) {
			//没有则插入数据库
			update = getWritableDatabase().insert(TABLE_NAME, null, values);
		}
		return update;
	}

	/**
	 *
	 * @param weiba
     */
	public void delWeiba(ModelWeiba weiba) {
		SQLiteDatabase database = getWritableDatabase();
		String sql = "delete * from " + tableName + " where " + weibaId + " = " + weiba.getWeiba_id(); // order by _id
		database.execSQL(sql);
		database.close();
	}

	public void deleteWeiba(int count) {
		if (count >= 20) {
			getWritableDatabase().execSQL(
					"delete from " + ThinksnsTableSqlHelper.tbWeiba
							+ " where site_id = "
							+ Thinksns.getMySite().getSite_id()
							+ " and my_uid = " + Thinksns.getMy().getUid());
		} else if (count > 0 && count < 20) {
			String sql = "delete from " + ThinksnsTableSqlHelper.tbWeiba
					+ " where weiba_id in (select weiba_id from "
					+ ThinksnsTableSqlHelper.tbWeiba + " where site_id = "
					+ Thinksns.getMySite().getSite_id() + " and my_uid = "
					+ Thinksns.getMy().getUid() + " order by weiba_id limit "
					+ count + ")";
			getWritableDatabase().execSQL(sql);
		}
	}

	public int getDBWeibaSize() {
		Cursor cursor = getWritableDatabase().rawQuery(
				"select count(*) from " + ThinksnsTableSqlHelper.tbWeiba
						+ " where site_id = "
						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
						+ Thinksns.getMy().getUid(), null);
		if (cursor.moveToFirst()) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	/**
	 * 是否存在某个微吧
	 * @param weiba_id
	 * @return
     */
	public boolean hasWeiba(int weiba_id) {
		SQLiteDatabase database = getWritableDatabase();
		String sql = "select " + weibaId + " from " + TABLE_NAME + " where " + weibaId;
		Cursor cursor = database.rawQuery(sql,
				new String[] {String.valueOf(weiba_id)});
		boolean result = cursor.moveToFirst();
		cursor.close();
		return result;
	}

	/**
	 * 获取我关注的微吧列表
	 * @return
     */
	public ListData<SociaxItem> getMyWeibaList() {
		SQLiteDatabase database = getReadableDatabase();
		String sql = "select * from " + TABLE_NAME + " where " + loginUid + " = "
				+ Thinksns.getMy().getUid() + " and " + isFollow + " = 1";
		Cursor cursor = database.rawQuery(sql, null);

		ListData<SociaxItem> weibaList = new ListData<SociaxItem>();
		if (cursor.moveToFirst()) {
			do {
				String json = cursor.getString(cursor.getColumnIndex(weibaJson));
				try {
					ModelWeiba weiba = new ModelWeiba(new JSONObject(json));
					weiba.setWeiba_id(cursor.getInt(cursor
							.getColumnIndex(weibaId)));
					weiba.setWeiba_name(cursor.getString(cursor
							.getColumnIndex(weibaName)));
					weiba.setIntro(cursor.getString(cursor.getColumnIndex(weibaIntro)));
					weiba.setFollow(cursor.getInt(cursor
							.getColumnIndex(isFollow)) == 1);
					weibaList.add(weiba);
				}catch(JSONException e) {
					e.printStackTrace();
				}
			} while (cursor.moveToNext());
		}

		cursor.close();
		return weibaList;
	}

	/**
	 * 获取一条微吧信息
	 * @return
     */
	public ModelWeiba getWeibaInfo(int weiba_id) {
		SQLiteDatabase database = getWritableDatabase();
		String sql = "select * from " + TABLE_NAME + " where " + weibaId + " = " + weiba_id;
		Cursor cursor = database.rawQuery(sql, null);

		ModelWeiba weiba = null;
		if (cursor.moveToFirst()) {
			String json = cursor.getString(cursor.getColumnIndex(weibaJson));
			try {
				JSONObject jsonObject = new JSONObject(json);
				weiba = new ModelWeiba(jsonObject);
				weiba.setWeiba_id(cursor.getInt(cursor
						.getColumnIndex(weibaId)));
				weiba.setWeiba_name(cursor.getString(cursor
						.getColumnIndex(weibaName)));
				weiba.setIntro(cursor.getString(cursor.getColumnIndex(weibaIntro)));
				int follow = cursor.getInt(cursor
						.getColumnIndex(isFollow));
				//查询这条微吧所属的登录用户
				int lastLoginUid = cursor.getInt(cursor.getColumnIndex(loginUid));
				if(follow == 1 &&
						lastLoginUid == Thinksns.getMy().getUid()) {
					//当微吧被关注且关注者是自己
					weiba.setFollow(true);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		cursor.close();

		return weiba;
	}

	/**
	 * 更新微吧关注状态
	 * @param weibaId
	 * @param state
     */
	public void updateFollowState(int weibaId, boolean state) {
		getWritableDatabase().execSQL(
				"update " + TABLE_NAME + " set " + isFollow + " = "
						+ state + " where " + weibaId + " = " + weibaId
						+ " and " + loginUid + " = " + Thinksns.getMy().getUid());
	}

}
