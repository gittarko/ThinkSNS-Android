package com.thinksns.sociax.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * 类说明：
 * 
 * @author xhs
 * @date 2014-10-15
 * @version 1.0
 */
public class T4ContentProvider extends ContentProvider {
	private static ThinksnsTableSqlHelper mOpenHelper;
	private static final int CHATS = 1;
	private static final int CHATLIST = 2;
	public static final String UTOHORITY = "com.thinksns.sociax.db.T4ContentProvider";
	private static final UriMatcher sURLMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	public static final String CONTENT_URI = "content://" + UTOHORITY + "/";
	private static final String TAG = "T4ContentProvider";

	static {
		sURLMatcher
				.addURI("UTOHORITY", ThinksnsTableSqlHelper.tbChatMsg, CHATS);
		sURLMatcher.addURI("UTOHORITY",
				ThinksnsTableSqlHelper.tbChatMsg + "/#", CHATS);
		sURLMatcher.addURI("UTOHORITY", ThinksnsTableSqlHelper.tbChatList,
				CHATLIST);
		sURLMatcher.addURI("UTOHORITY", ThinksnsTableSqlHelper.tbChatList
				+ "/#", CHATLIST);
	}

	@Override
	public boolean onCreate() {
		Log.d(TAG, "onCreate");
		mOpenHelper = new ThinksnsTableSqlHelper(getContext(), null);
		Log.d(TAG, "mOpenHelper=" + mOpenHelper.toString());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "mOpenHelper=" + mOpenHelper);
		SQLiteDatabase mSQLiteDatabase = mOpenHelper.getWritableDatabase();

		Cursor cursor = null;
		int type = sURLMatcher.match(uri);
		switch (type) {
		case CHATS:
			break;
		case CHATLIST:
			cursor = mSQLiteDatabase.query(ThinksnsTableSqlHelper.tbChatList,
					null, selection, selectionArgs, null, null, sortOrder);
			break;
		}

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase mSQLiteDatabase = mOpenHelper.getWritableDatabase();
		int type = sURLMatcher.match(uri);
		switch (type) {
		case CHATS:
			break;
		case CHATLIST:
			long result = mSQLiteDatabase.insert(
					ThinksnsTableSqlHelper.tbChatList, null, values);
			if (result > 0) {
				Uri stuUri = ContentUris.withAppendedId(uri, result);
				// resolver.notifyChange(stuUri, null);//数据发送变化时候，发出通知给注册了相应uri的
				return stuUri;
			}
			break;
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase mSQLiteDatabase = mOpenHelper.getWritableDatabase();
		int type = sURLMatcher.match(uri);
		int result = 0;
		switch (type) {
		case CHATS:
			break;
		case CHATLIST:
			result = mSQLiteDatabase.delete(ThinksnsTableSqlHelper.tbChatList,
					selection, selectionArgs);
			break;

		}
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase mSQLiteDatabase = mOpenHelper.getWritableDatabase();
		int type = sURLMatcher.match(uri);
		switch (type) {
		case CHATS:
			break;
		case CHATLIST:
			int result = mSQLiteDatabase.update(
					ThinksnsTableSqlHelper.tbChatList, values, selection,
					selectionArgs);
			break;
		}
		return 0;
	}

}
