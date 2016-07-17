package com.thinksns.sociax.db;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.modle.Posts;
import com.thinksns.sociax.modle.UserApprove;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.erweima.units.SideBar;
import com.thinksns.sociax.t4.exception.WeiboDataInvalidException;
import com.thinksns.sociax.t4.model.ModelImageAttach;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import java.util.SortedSet;

public class WeiboSqlHelper extends SqlHelper {
	private static WeiboSqlHelper instance;
	private static ThinksnsTableSqlHelper weiboTable;

	private ListData<SociaxItem> weiboDatas;

	private WeiboSqlHelper(Context context) {
		this.weiboTable = new ThinksnsTableSqlHelper(context,null);
	}

	public static WeiboSqlHelper getInstance(Context context) {
		if (instance == null) {
			instance = new WeiboSqlHelper(context);
		}
		return instance;
	}

	/**
	 * 添加微博
	 * @param weibo
	 * @return
	 */
	public static long addWeibo(ModelWeibo weibo) {
		ContentValues map = new ContentValues();
		map.put(ThinksnsTableSqlHelper.uid, weibo.getUid());
		map.put(ThinksnsTableSqlHelper.weiboId, weibo.getWeiboId());
		map.put(ThinksnsTableSqlHelper.content, weibo.getContent());
		map.put(ThinksnsTableSqlHelper.cTime, weibo.getCtime());
		map.put(ThinksnsTableSqlHelper.weiboJson, weibo.getWeiboJsonString());
//		map.put(ThinksnsTableSqlHelper.from, weibo.getFrom());
//		map.put(ThinksnsTableSqlHelper.timeStamp, weibo.getTimestamp());
//		map.put(ThinksnsTableSqlHelper.comment, weibo.getCommentCount());
//		map.put(ThinksnsTableSqlHelper.type, weibo.getType());
//		map.put(ThinksnsTableSqlHelper.attach, weibo.hasImage() == true ? 0 : 1);

//		if (weibo.getAttachImage() != null)
//			for (int i = 0; i < weibo.getAttachImage().size(); i++) {
//				attachSqlHelper.addAttach((ModelImageAttach) weibo.getAttachImage().get(i), 2);
//			}
//		attachSqlHelper.close();

//		if (!weibo.isNullForTranspondId()) {
//			map.put(ThinksnsTableSqlHelper.transpond, weibo.getSourceWeibo()
//					.getWeiboJsonString());
//		}

//		map.put(ThinksnsTableSqlHelper.transpondCount,
//				weibo.getTranspondCount());
//		map.put(ThinksnsTableSqlHelper.userface, weibo.getUserface());
//		map.put(ThinksnsTableSqlHelper.transpondId, weibo.getIsRepost());
//		map.put(ThinksnsTableSqlHelper.favorited,
//				transFavourt(weibo.isFavorited()));
//		map.put(ThinksnsTableSqlHelper.diggnum, (weibo.getDiggNum()));
//		map.put(ThinksnsTableSqlHelper.digg, (weibo.getIsDigg()));
//		map.put(ThinksnsTableSqlHelper.isdel, (weibo.isWeiboIsDelete()));
//		map.put(ThinksnsTableSqlHelper.weiboJson, weibo.getWeiboJsonString());
//		map.put("site_id", Thinksns.getMySite().getSite_id());
//		map.put("my_uid", Thinksns.getMy().getUid());

		int result = weiboTable.getWritableDatabase().update(ThinksnsTableSqlHelper.weiboTable, map,
				"uid=? and weiboId=?",
				new String[]{String.valueOf(weibo.getUid()), String.valueOf(weibo.getWeiboId())});
		if(result == 0) {
			result = (int)weiboTable.getWritableDatabase().insert(
					ThinksnsTableSqlHelper.weiboTable, null, map);
		}

		return result;
	}

	/**
	 * 获取登录用户的微博列表
	 * 
	 * @return
	 */
	public ListData<SociaxItem> getWeiboList() {
		Cursor cursor = weiboTable.getReadableDatabase().query(ThinksnsTableSqlHelper.weiboTable,
				null, "uid= "
						+ Thinksns.getMy().getUid(), null, null, null,
				ThinksnsTableSqlHelper.weiboId + " DESC");

		weiboDatas = new ListData<SociaxItem>();

		if (cursor.moveToFirst())
			do {

				String weiboJson = cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.weiboJson));
				try {
					ModelWeibo weibo = new ModelWeibo(new JSONObject(weiboJson));
					weiboDatas.add(weibo);
				}catch(JSONException e) {
					e.printStackTrace();
				} catch (WeiboDataInvalidException e) {
					e.printStackTrace();
				}

//				int weiboId = cursor.getInt(cursor.getColumnIndex(ThinksnsTableSqlHelper.weiboId));
//				weibo.setWeiboId(weiboId);
//				weibo.setUid(cursor.getInt(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.uid)));
//				weibo.setUsername(cursor.getString(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.userName)));
//				weibo.setContent(cursor.getString(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.content)));
//				weibo.setCtime(cursor.getString(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.cTime)));
//				weibo.setFrom(cursor.getString(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.from)));
//				weibo.setTimestamp(cursor.getInt(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.timeStamp)));
//				weibo.setCommentCount(cursor.getInt(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.comment)));
//				weibo.setType(cursor.getString(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.type)));
//				weibo.setIsDigg(cursor.getInt(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.digg)));
//				weibo.setDiggNum(cursor.getInt(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.diggnum)));
//				weibo.setWeiboIsDelelet(cursor.getInt(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.isdel)));
//
//				weibo.setAttachImage(attachSqlHelper.getAttachsByWeiboId(weiboId));
//				attachSqlHelper.close();
//
//				// weibo.setPicUrl(cursor.getString(cursor.getColumnIndex(weiboTable.picUrl)));
//				// weibo.setThumbMiddleUrl(cursor.getString(cursor.getColumnIndex(weiboTable.thumbMiddleUrl)));
//				weibo.setTranspondId(cursor.getInt(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.transpondId)));
//				// weibo.setThumbUrl(cursor.getString(cursor.getColumnIndex(weiboTable.thumbUrl)));
//				if (cursor.getString(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.transpond)) != null) {
//					try {
//						weibo.setSourceWeibo(new ModelWeibo(
//								new JSONObject(
//										cursor.getString(cursor
//												.getColumnIndex(ThinksnsTableSqlHelper.transpond)))));
//					} catch (WeiboDataInvalidException e) {
//						e.printStackTrace();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				weibo.setTranspondCount(cursor.getInt(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.transpondCount)));
//				weibo.setFavorited(isFavourt(cursor.getInt(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.favorited))));
//				weibo.setUserface(cursor.getString(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.userface)));
//				weibo.setTempJsonString(cursor.getString(cursor
//						.getColumnIndex(ThinksnsTableSqlHelper.weiboJson)));
//
//				JSONObject weiboData;
//				try {
//					weiboData = new JSONObject(weibo.getTempJsonString());
//					// 用户认证信息
//					if (weiboData.has("user_group")) {
//						weibo.setUsApprove(new UserApprove(weiboData));
//					}
//
//					if (weiboData.has("api_source")
//							&& !weiboData.isNull("api_source")) {
//						try {
//							weibo.setPosts(new Posts(weiboData
//									.getJSONObject("api_source")));
//						} catch (DataInvalidException e) {
//							e.printStackTrace();
//						}
//					}
//
//				} catch (JSONException e) {
//					Log.e("WeiboDB", e.toString());
//				}
			} while (cursor.moveToNext());
		cursor.close();
		return weiboDatas;
	}

	//根据UID获取对应用户的微博列表
	public static ListData<SociaxItem> getWeiboListByUid(int uid) {
		Cursor cursor = weiboTable.getReadableDatabase().query(ThinksnsTableSqlHelper.weiboTable,
				null, "uid = " + uid, null, null, null,
				ThinksnsTableSqlHelper.weiboId + " DESC");

		ListData<SociaxItem> weiboDatas = new ListData<SociaxItem>();

		while(cursor.moveToNext()) {
			String weiboJson = cursor.getString(cursor.getColumnIndex(ThinksnsTableSqlHelper.weiboJson));
			try {
				ModelWeibo weibo = new ModelWeibo(new JSONObject(weiboJson));
				weiboDatas.add(weibo);
			}catch(JSONException e) {
				e.printStackTrace();
			} catch (WeiboDataInvalidException e) {
				e.printStackTrace();
			}
		}

		cursor.close();

		return weiboDatas;
	}

	/**
	 * 获取微博数量
	 * 
	 * @return
	 */
	public int getDBWeiboSize() {
		Cursor cursor = weiboTable.getWritableDatabase().rawQuery(
				"select count(*) from home_weibo where site_id = "
						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
						+ Thinksns.getMy().getUid(), null);
		if (cursor.moveToFirst()) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	/**
	 * 删除指定条数的微博
	 * 
	 * @param count
	 * @return
	 */
	public void deleteWeibo(int count) {
		if (count >= 20) {
			weiboTable.getWritableDatabase().execSQL(
					"delete from home_weibo where site_id = "
							+ Thinksns.getMySite().getSite_id()
							+ " and my_uid = " + Thinksns.getMy().getUid());
		} else if (count > 0 && count < 20) {
			String sql = "delete from home_weibo where weiboId in (select weiboId from home_weibo where site_id = "
					+ Thinksns.getMySite().getSite_id()
					+ " and my_uid = "
					+ Thinksns.getMy().getUid()
					+ " order by weiboId limit "
					+ count + ")";
			weiboTable.getWritableDatabase().execSQL(sql);
		}
	}

	/**
	 * 删除指定一条微博
	 * 
	 * @param weiboId
	 * @return
	 */
	public boolean deleteWeiboById(int weiboId) {
		try {
			weiboTable.getWritableDatabase().execSQL(
					"delete from home_weibo where weiboId=" + weiboId
							+ " and site_id = "
							+ Thinksns.getMySite().getSite_id()
							+ " and my_uid = " + Thinksns.getMy().getUid());
			return true;
		} catch (Exception e) {
			Log.e(AppConstant.APP_TAG,
					"delete weibo error ---------->" + e.toString());
			return false;
		}
	}

	public void updateDigg(int weiboId, int num) {
		weiboTable.getWritableDatabase().execSQL(
				"update home_weibo" + " set isdigg = " + 1 + ", diggnum = "
						+ num + " where weiboId = " + weiboId
						+ " and site_id = " + Thinksns.getMySite().getSite_id()
						+ " and my_uid = " + Thinksns.getMy().getUid());
	}

	public void updateCountNum(int weiboId, int commentNum, int transpondCount) {
		System.err.println("update home_weibo" + " set comment = " + commentNum
				+ ", transpondCount = " + transpondCount + " where weiboId = "
				+ weiboId + " and site_id = "
				+ Thinksns.getMySite().getSite_id() + " and my_uid = "
				+ Thinksns.getMy().getUid());
		weiboTable.getWritableDatabase().execSQL(
				"update home_weibo" + " set comment = " + commentNum
						+ ", transpondCount = " + transpondCount
						+ " where weiboId = " + weiboId + " and site_id = "
						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
						+ Thinksns.getMy().getUid());
	}

	/**
	 * 删除数据库缓存
	 */
	public void clearCacheDB() {
		// attachSqlHelper.addAttach(iAttach);
//		attachSqlHelper.close();
		weiboTable.getWritableDatabase().execSQL(
				"delete from home_weibo where site_id = "
						+ Thinksns.getMySite().getSite_id() + " and my_uid = "
						+ Thinksns.getMy().getUid());
	}

	@Override
	public void close() {
		weiboTable.close();
	}

	private int transFavourt(boolean favorited) {
		if (favorited) {
			return 1;
		} else {
			return 0;
		}
	}

	private boolean isFavourt(int is) {
		if (is == 1) {
			return true;
		} else {
			return false;
		}
	}
}
