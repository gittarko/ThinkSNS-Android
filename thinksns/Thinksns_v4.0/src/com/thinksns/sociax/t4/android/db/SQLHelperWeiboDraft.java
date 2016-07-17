package com.thinksns.sociax.t4.android.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.thinksns.sociax.db.SqlHelper;
import com.thinksns.sociax.db.ThinksnsTableSqlHelper;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelDraft;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

/**
 * 类说明：   
 * @author  wz    
 * @date    2015-1-26
 * @version 1.0
 */
public class SQLHelperWeiboDraft extends SqlHelper {

	private static SQLHelperWeiboDraft instance;
	private ThinksnsTableSqlHelper tableSqlHelper;

	public SQLHelperWeiboDraft(Context context) {
		this.tableSqlHelper = new ThinksnsTableSqlHelper(context, null);
	}

	public static SQLHelperWeiboDraft getInstance(Context context) {
		if (instance == null) {
			instance = new SQLHelperWeiboDraft(context);
		}
		return instance;
	}

	/**
	 * 插入新的草稿箱
	 * @param isNew 是否新的草稿
	 * @param md 草稿
	 */
	public long addWeiboDraft(boolean isNew, ModelDraft md) {
		long result = 0;
		if (isNew) {
			ContentValues values = new ContentValues();
			values.put("content", md.getContent());
			values.put("time", System.currentTimeMillis() / 1000);
//			values.put("weiboId", md.getWeiboId());
//			values.put("channel_id", md.getChannel_id());
			values.put("has_image", md.isHasImage());
			values.put("has_video", md.isHasVideo());
			values.put("title", md.getTitle());
			values.put("image_list", md.getImageListToString());
			values.put("video_path", md.getVideoPath());
			values.put("is_send", md.isDraftSend());
			values.put("my_uid", Thinksns.getMy().getUid());
			values.put("latitude", md.getLatitude());
			values.put("longitude", md.getLongitude());
			values.put("address", md.getAddress());
			values.put("feed_id", md.getFeed_id());
			values.put("channel_id", md.getChannel_id());
			values.put("type", md.getType());
			result = tableSqlHelper.getWritableDatabase().insert(ThinksnsTableSqlHelper.tbWeiboDraft, null, values);
		} else {
			result = updateWeiboDraft(md);
		}

		return result;
	}
	/**
	 * 获取所有草稿
	 * @param id 上一次最后一条的id
	 * @param pageCount  总数
	 * @return
	 */
	public ListData<ModelDraft> getAllWeiboDraft(int pageCount, int id) {
		if(Thinksns.getMy() == null) {
			return new ListData<ModelDraft>();
		}

		SQLiteDatabase database = tableSqlHelper.getWritableDatabase();
		int uid = Thinksns.getMy().getUid();
		ListData<ModelDraft> lAttachs=null;
		if (uid!=0) {
			String sql = "select * from " + ThinksnsTableSqlHelper.tbWeiboDraft + " where my_uid = " + uid
					+ " order by time desc";
			Cursor cursor = database.rawQuery(sql, null);
			lAttachs = new ListData<ModelDraft>();
			if (cursor.moveToFirst()) {
				do {
					ModelDraft weibDraft = new ModelDraft();
					weibDraft.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))));
					weibDraft.setTitle(cursor.getString(cursor.getColumnIndex("title")));
					weibDraft.setContent(cursor.getString(cursor.getColumnIndex("content")));
					weibDraft.setCtime(cursor.getString(cursor.getColumnIndex("time")));
//					weibDraft.setWeiboid(cursor.getInt(cursor.getColumnIndex("weiboId")));
					weibDraft.setHasImage(cursor.getInt(cursor.getColumnIndex("has_image"))==1);
					weibDraft.setHasVideo(cursor.getInt(cursor.getColumnIndex("has_video"))==1);
					weibDraft.setDraftSend(cursor.getInt(cursor.getColumnIndex("is_send"))==1);
					weibDraft.setImageList(cursor.getString(cursor.getColumnIndex("image_list")));
					weibDraft.setVideoPath((cursor.getString(cursor.getColumnIndex("video_path"))==null)?
							"":cursor.getString(cursor.getColumnIndex("video_path")));
					weibDraft.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
					weibDraft.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
					weibDraft.setAddress(cursor.getString(cursor.getColumnIndex("address")));
					weibDraft.setFeed_id(cursor.getInt(cursor.getColumnIndex("feed_id")));
					weibDraft.setChannel_id(cursor.getInt(cursor.getColumnIndex("channel_id")));
					weibDraft.setType(cursor.getInt(cursor.getColumnIndex("type")));

					lAttachs.add(weibDraft);
				} while (cursor.moveToNext());
			}
			cursor.close();
		}

		return lAttachs;
	}
	/**
	 * 更新草稿
	 * @param md
	 * @return
	 */
	public int updateWeiboDraft(ModelDraft md) {
		SQLiteDatabase database = tableSqlHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("content", md.getContent());
		values.put("time", System.currentTimeMillis() / 1000);
//		values.put("weiboId", md.getWeiboId());
//		values.put("channel_id", md.getChannel_id());
		values.put("has_image", md.isHasImage());
		values.put("has_video", md.isHasVideo());
		values.put("title", md.getTitle());
		values.put("image_list", md.getImageListToString());
		values.put("video_path", md.getVideoPath());
		values.put("is_send", md.isDraftSend());
		values.put("my_uid", Thinksns.getMy().getUid());
		values.put("latitude", md.getLatitude());
		values.put("longitude", md.getLongitude());
		values.put("address", md.getAddress());
		values.put("feed_id", md.getFeed_id());
		values.put("channel_id", md.getChannel_id());
		values.put("type", md.getType());

		int colunm = -1;
		try {
			colunm = database.update(ThinksnsTableSqlHelper.tbWeiboDraft,values, "_id" + "=" + md.getId(), null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.close(); 		// 关闭数据库
		}

		return colunm;
	}

	public void delWeiboDraft(int id) {
		tableSqlHelper.getWritableDatabase().execSQL("delete from " + ThinksnsTableSqlHelper.tbWeiboDraft + " where _id = " + id + "");
	}

	public void clearWeiboDraft() {
		tableSqlHelper.getWritableDatabase().execSQL("delete from " + ThinksnsTableSqlHelper.tbWeiboDraft + " where my_uid = " + Thinksns.getMy().getUid() + "");
	}

	@Override
	public void close() {
		tableSqlHelper.close();
	}
}
