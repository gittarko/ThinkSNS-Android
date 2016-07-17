package com.thinksns.sociax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thinksns.sociax.modle.ApproveSite;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

public class SitesSqlHelper extends SqlHelper {
	private static SitesSqlHelper instance;
	private ThinksnsTableSqlHelper sitesTable;
	private ListData<SociaxItem> sitesDatas;

	private SitesSqlHelper(Context context) {
		this.sitesTable = new ThinksnsTableSqlHelper(context, null);
	}

	public static SitesSqlHelper getInstance(Context context) {
		if (instance == null) {
			instance = new SitesSqlHelper(context);
		}

		return instance;
	}

	public long addSites(ApproveSite site) {
		ContentValues map = new ContentValues();
		map.put("site_id", site.getSite_id());
		map.put("name", site.getName());
		map.put("url", site.getUrl());
		map.put("logo", site.getLogo());
		map.put("ctime", site.getCtime());
		map.put("status", tranBoolean(site.isStatus()));
		map.put("denied_reason", site.getDenied_reason());
		map.put("status_mtime", site.getStatus_mtime());
		map.put("description", site.getDescription());
		map.put("email", site.getEmail());
		map.put("phone", site.getPhone());
		map.put("uid", site.getUid());
		map.put("isused", 1);
		long l = sitesTable.getWritableDatabase().insert(
				ThinksnsTableSqlHelper.siteList, null, map);
		return l;
	}

	// 设置某个某个网站为当前使用
	public void setSiteForUsed(ApproveSite site) {
		sitesTable.getWritableDatabase().execSQL(
				"update " + ThinksnsTableSqlHelper.siteList
						+ " set isused=0 where isused=1");
		sitesTable.getWritableDatabase().execSQL(
				"update " + ThinksnsTableSqlHelper.siteList
						+ " set isused=1 where site_id=" + site.getSite_id());
	}

	public int hasSites() {
		SQLiteDatabase database = sitesTable.getWritableDatabase();
		String sql = " select count(*) from " + ThinksnsTableSqlHelper.siteList;
		Cursor cursor = database.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			return cursor.getInt(0);
		} else {
			cursor.close();
			return 0;
		}
	}

	// 获取站点
	public ListData<SociaxItem> getSitesList(String sql) {
		Cursor cursor = sitesTable.getReadableDatabase().query(
				ThinksnsTableSqlHelper.siteList, null, sql, null, null, null,
				null);
		sitesDatas = new ListData<SociaxItem>();

		if (cursor == null) {
			return null;
		}
		while (!cursor.isAfterLast()) {
			ApproveSite sites = new ApproveSite();
			sites.setSite_id(cursor.getInt(cursor.getColumnIndex("site_id")));
			sites.setName(cursor.getString(cursor.getColumnIndex("name")));
			sites.setLogo(cursor.getString(cursor.getColumnIndex("logo")));
			cursor.moveToNext();
			sitesDatas.add(sites);
		}
		return sitesDatas;
	}

	/**
	 * 获取使用过的站点
	 * 
	 * @return
	 */
	public ApproveSite getInUsed() {
		Cursor cursor = sitesTable.getReadableDatabase().query(
				ThinksnsTableSqlHelper.siteList, null, "isused=1", null, null,
				null, null);
		ApproveSite site = null;
		if (cursor != null && cursor.moveToFirst()) {
			site = new ApproveSite();
			site.setSite_id(cursor.getInt(cursor.getColumnIndex("site_id")));
			site.setName(cursor.getString(cursor.getColumnIndex("name")));
			site.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			site.setLogo(cursor.getString(cursor.getColumnIndex("logo")));
		}
		cursor.close();
		return site;
	}

	public long addSiteUser(String userName) {
		ContentValues map = new ContentValues();
		map.put("u_name", userName);
		return sitesTable.getWritableDatabase().insert(
				ThinksnsTableSqlHelper.tbSiteUser, null, map);
	}

	// 清除站点
	public void clearSite() {
		sitesTable.getWritableDatabase().execSQL(
				"delete from " + ThinksnsTableSqlHelper.siteList);
	}

	// 获取网站列表
	@Override
	public void close() {
	}
}
