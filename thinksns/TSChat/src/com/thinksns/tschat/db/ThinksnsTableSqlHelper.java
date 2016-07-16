package com.thinksns.tschat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ThinksnsTableSqlHelper extends SQLiteOpenHelper {
	public ThinksnsTableSqlHelper(Context context, String name,
								  CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	public static final String TAG = ThinksnsTableSqlHelper.class.getSimpleName();
	public static final String tbChatList = "tb_chat_List";// 聊天列表
	public static final String tbRoomList = "tb_room_List";// 聊天房间

	private static SQLiteDatabase db = null;
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		ThinksnsTableSqlHelper.db = db;
		Log.v(TAG, "onCreate");
	}
	
	/**
	 * 更新数据库的时候用下面的方法
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE User ADD COLUMN myLastWeibo text");
		// db.execSQL("ALTER TABLE home_weibo ADD COLUMN weiboJson text");
		// db.execSQL("ALTER TABLE User ADD COLUMN userJson text");
	}
	
	
	public void createRoomTable() {
		this.getWritableDatabase().execSQL("CREATE TABLE if not exists "
				+ tbRoomList
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,room_id INTEGER,master_uid INTEGER ,is_group INTEGER,title TEXT,"
				+ " mtime INTEGER,self_index INTEGER,content TEXT,type TEXT,from_uid INTEGER,from_uname TEXT,from_uface TEXT,from_uface_url TEXT,to_name TEXT,to_uid INTEGER,member_num INTEGER,isNew INTEGER)");
	}
	
}
