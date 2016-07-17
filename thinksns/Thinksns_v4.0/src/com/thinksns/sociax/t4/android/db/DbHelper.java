package com.thinksns.sociax.t4.android.db;

import com.thinksns.sociax.db.SqlHelper;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** 
 * 类说明：   
 * @author  zhiyichuangxiang    
 * @date    2015-8-24
 * @version 1.0
 */
public abstract class DbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "thinksns";
	private static final int VERSION = SqlHelper.VERSION;
	//数据库表名
	protected String table_name = "";
	
	public DbHelper(Context context, String table_name) {
		super(context, DB_NAME, null, VERSION);
		this.table_name = table_name;
	}

	@Override
	public abstract void onCreate(SQLiteDatabase db);

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		
	}

	@Override
	public synchronized void close() {
		super.close();
	}
	
	/**
	 * 保存数据
	 * @param item
	 * @return
	 */
	public abstract long saveData(SociaxItem item);
	/**
	 * 删除一条微博
	 * @param item
	 * @return
	 */
	public abstract boolean deleteData(SociaxItem item);
	
	/**
	 * 删除朋友圈里某人的微博
	 * @param item
	 * @return
	 */
	public abstract boolean deleteSomeBodyWeibo(int uid,int loginUid);
	
	/**
	 * 获取数据集合
	 * @return
	 */
	public abstract ListData<SociaxItem> getHeaderList(int count);

	public abstract ListData<SociaxItem> getFooterList(int count, int lastId);
	
	public abstract ListData<SociaxItem> getHeaderByUser(int count , int userId);
	
	public abstract ListData<SociaxItem> getFooterByUser(int count, int userId);
	
}
