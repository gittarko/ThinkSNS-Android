package com.thinksns.sociax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thinksns.sociax.modle.MobileApp;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

public class MobileAppSqlHelper extends SqlHelper {

	private static MobileAppSqlHelper instance;
	private ThinksnsTableSqlHelper tableSqlHelper;

	private MobileAppSqlHelper(Context context) {
		this.tableSqlHelper = new ThinksnsTableSqlHelper(context,
				null);
	}

	public static MobileAppSqlHelper getInstance(Context context) {
		if (instance == null) {
			instance = new MobileAppSqlHelper(context);
		}
		return instance;
	}

	public long addMobileApp(MobileApp mApp) {
		ContentValues values = new ContentValues();
		values.put("appid", mApp.getAppId());
		values.put("name", mApp.getAppName());
		values.put("icon", mApp.getAppIconUrl());
		values.put("type", mApp.getAppType());
		values.put("link", mApp.getAppLink());
		values.put("tag", mApp.getTag());

		long result = tableSqlHelper.getWritableDatabase().insert(
				ThinksnsTableSqlHelper.appInfo, null, values);
		return result;
	}

	public boolean isInstall(int appId) {

		SQLiteDatabase database = tableSqlHelper.getWritableDatabase();
		String sql = "select * from app_info where appid = ?";
		Cursor cursor = database.rawQuery(sql,
				new String[] { String.valueOf(appId) });

		boolean result = cursor.moveToFirst();
		cursor.close();

		/*
		 * int count = cursor.getInt(0); cursor.close(); return count == 0;
		 */
		return result;
	}

	public boolean deleteMobileApp(int appId) {
		SQLiteDatabase database = tableSqlHelper.getWritableDatabase();
		int result = database.delete(ThinksnsTableSqlHelper.appInfo, "appid=?",
				new String[] { String.valueOf(appId) });
		return result > 0;
	}

	/**
	 * 获得所有的MobileApp
	 * 
	 * @return
	 */
	public ListData<SociaxItem> getAllMobileApp() {
		SQLiteDatabase database = tableSqlHelper.getWritableDatabase();
		String sql = "select * from app_info order by appid asc";
		Cursor cursor = database.rawQuery(sql, null);
		ListData<SociaxItem> mAppList = null;
		MobileApp mobileApp = null;
		if (cursor.moveToFirst()) {
			mAppList = new ListData<SociaxItem>();
			do {
				mobileApp = new MobileApp();
				mobileApp.setAppId(Integer.parseInt(cursor.getString(cursor
						.getColumnIndex("appid"))));
				mobileApp.setAppName(cursor.getString(cursor
						.getColumnIndex("name")));
				mobileApp.setAppIconUrl(cursor.getString(cursor
						.getColumnIndex("icon")));
				mobileApp.setAppType(cursor.getString(cursor
						.getColumnIndex("type")));
				mobileApp.setAppLink(cursor.getString(cursor
						.getColumnIndex("link")));
				mobileApp
						.setTag(cursor.getString(cursor.getColumnIndex("tag")));
				// mobileApp.setTag(tag)
				mobileApp.setAppClazz();
				mAppList.add(mobileApp);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return mAppList;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
