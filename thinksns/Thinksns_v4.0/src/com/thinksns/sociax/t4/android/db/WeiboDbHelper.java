package com.thinksns.sociax.t4.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.thinksns.sociax.db.AttachSqlHelper;
import com.thinksns.sociax.modle.Posts;
import com.thinksns.sociax.modle.UserApprove;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.exception.WeiboDataInvalidException;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 类说明：
 * @author  zhiyichuangxiang
 * @date    2015-8-24
 * @version 1.0
 */
public class WeiboDbHelper extends DbHelper {
	private AttachSqlHelper attachSqlHelper;
	private ModelWeibo weibo;
	private ListData.DataType weibo_type;
	private int lastId = 0;		//数据查询最后一条id

	public static final String weiboId = "weibo_id";
	public static final String loginId = "login_id";
	public static final String uid = "uid";
	public static final String userName = "userName";
	public static final String userface = "userface";
	public static final String content = "content";
	public static final String cTime = "cTime";					//发表时间
	public static final String from = "weiboFrom";
	public static final String timeStamp = "timestamp";			//发表时间戳
	public static final String commentnum = "commentnum";		//评论数
	public static final String diggnum = "diggnum";
	public static final String type = "type";
	public static final String weiboType = "weiboType";
	public static final String attach = "attach";
	public static final String picUrl = "picUrl";
	public static final String thumbMiddleUrl = "thumbMiddleUrl";
	public static final String thumbUrl = "thumUrl";
	public static final String transpond = "transpone";
	public static final String transpondCount = "transpondCount";
	public static final String transpondId = "transpondId";
	public static final String favorited = "favorited";
	public static final String isdigg = "isdigg";
	public static final String isdel = "isdel";
	public static final String weiboJson = "weiboJson";		//微博Json串
	public static final String sourceName = "source_name";
	public static final String title = "title";


	public WeiboDbHelper(Context context, ListData.DataType type) {
		super(context, "weibo");
		attachSqlHelper = new AttachSqlHelper(context);
		weibo_type = type;
		createTable();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable();
	}

	private void createTable() {
		//创建微博表
		String sql = "Create Table if not exists " + table_name + " ("
				+ weiboId + " integer," + uid + " integer," + userName
				+ " varchar," + content + " text," + cTime + " varchar," + from
				+ " text," + timeStamp + " integer," + commentnum + " integer,"
				+ type + " text," + weiboType + " integer,"+ attach + " INETGER," + picUrl + " text, "
				+ thumbMiddleUrl + " text," + thumbUrl + " text," + transpond
				+ " text," + transpondCount + " integer," + userface + " text,"
				+ transpondId + " integer," + favorited + " integer, " + isdigg
				+ " integer, " + diggnum + " integer, " + isdel + " integer, "
				+ weiboJson + " text," + loginId + " integer, " + sourceName + " text,"
				+ title + " text" + ")";

		execSQL(sql);
	}

	public synchronized void execSQL(String sql){
		try{
			getWritableDatabase().execSQL(sql);
		}catch(Exception e){
			e.printStackTrace();
			Log.v("sqlException", "------------sqlException----------------"+e.getMessage());
		}
	}

	//插入新数据
	public synchronized long saveData(SociaxItem weibo) {
		long count = 0;
		if(weibo instanceof ModelWeibo) {
			this.weibo = (ModelWeibo)weibo;
			if(exist(this.weibo)) {
				count = updateData(this.weibo);
			}else {
				count = insertData(this.weibo);
			}
		}

		return count;
	}

	/**
	 * 删除某一条微博
	 * @param weibo
	 * @return
	 */
	@Override
	public boolean deleteData(SociaxItem item){
		try {
			getReadableDatabase().delete(table_name,weiboId + " = ? and " + loginId + " = ?",
					new String[]{((ModelWeibo)item).getWeiboId()+"", Thinksns.getMy().getUid() +""});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除朋友圈中某人的微博
	 */
	@Override
	public boolean deleteSomeBodyWeibo(int uid,int loginUid) {
		try {
			if (haveThisWeibo(uid,loginUid)) {
				int cloumn=getReadableDatabase().delete(table_name,uid + " = ? and " + loginId + " = ?",
							new String[]{uid+"", Thinksns.getMy().getUid() +""});
				Log.v("deleteWeibo", "------deleteWeibo----------"+cloumn);
				return true;
			}else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 数据库里是否有这条数据
	 * @return
	 */
	public boolean haveThisWeibo(int uid,int loginUid) {
		Cursor cursor =getWritableDatabase().query(table_name,
				null, uid + " = ? and " + loginId + " = ?", new String[]{uid+"", Thinksns.getMy().getUid() +""},
				null, null, null);

		if(cursor.moveToFirst()) {
			return true;
		}
		cursor.close();
		return false;
	}

	/**
	 * 查询该条微博是否已保存
	 * @param weibo
	 * @return
	 */
	private synchronized boolean exist(ModelWeibo weibo) {
		SQLiteDatabase database = getReadableDatabase();
		Cursor cursor = database.query(table_name, null, weiboId + " = ? and " + loginId + " = ?",
				new String[] {weibo.getWeiboId() +"", Thinksns.getMy().getUid() + ""}, null, null, null);
		if(cursor.moveToFirst())
			return true;
		cursor.close();

		return false;
	}

	private synchronized long insertData(ModelWeibo weibo) {
		SQLiteDatabase database = getWritableDatabase();
		ContentValues values = initValues(weibo);
		long count = (int) database.insert(table_name, null, values);

		return count;
	}



	/**
	 * 更新微博
	 */
	public synchronized int updateData(ModelWeibo weibo) {
		SQLiteDatabase database = getWritableDatabase();
		ContentValues values = initValues(weibo);
		int count = database.update(table_name, values, weiboId + " = ? and " + loginId + " = ?",
				new String[]{weibo.getWeiboId()+"", Thinksns.getMy().getUid() +""});

		return count;
	}

	/**
	 * 获取指定数的微博列表
	 *
	 * @return
	 */
	@Override
	public ListData<SociaxItem> getHeaderList(int count) {

		ListData<SociaxItem> weiboDatas = new ListData<SociaxItem>();
		try {
			Cursor cursor = getReadableDatabase().query(table_name,
					null, weiboType + " = " + transWeiboType(weibo_type)
							+ " and " + loginId + " = "
							+ Thinksns.getMy().getUid(), null, null, null,
					weiboId + " DESC limit " + count);
			if (cursor.moveToFirst())
				do {
					ModelWeibo weibo = initWeibo(cursor);
					weiboDatas.add(weibo);
					lastId = weibo.getWeiboId();
				} while (cursor.moveToNext());

			cursor.close();
			attachSqlHelper.close();
		}catch(Exception e) {
			e.printStackTrace();
		}

		return weiboDatas;
	}

	public ListData<SociaxItem> getHeaderByUser(int count ,int user_id) {
		Cursor cursor = getReadableDatabase().query(table_name,
				null, uid + " = " + user_id , null, null, null,
				weiboId + " DESC limit " + count);
		ListData<SociaxItem> weiboDatas = new ListData<SociaxItem>();

		if (cursor.moveToFirst())
			do {
				ModelWeibo weibo = initWeibo(cursor);
				weiboDatas.add(weibo);
				lastId = weibo.getWeiboId();
			} while (cursor.moveToNext());

		cursor.close();
		attachSqlHelper.close();

		return weiboDatas;
	}

	/**
	 * 获取用户微博
	 * @param count
	 * @param user_id
	 * @return
	 */
	public ListData<SociaxItem> getFooterByUser(int count, int user_id) {
		Cursor cursor = getReadableDatabase().query(table_name,
				null, uid + " = " + user_id + " and "
						+ weiboId + " < " + lastId, null, null, null,
				weiboId + " DESC limit " + count);
		ListData<SociaxItem> weiboDatas = new ListData<SociaxItem>();

		if (cursor.moveToFirst())
			do {
				ModelWeibo weibo = initWeibo(cursor);
				weiboDatas.add(weibo);
				lastId = weibo.getWeiboId();
			} while (cursor.moveToNext());

		cursor.close();
		attachSqlHelper.close();

		return weiboDatas;
	}
	/**
	 * 构造一条微博
	 * @param cursor
	 * @return
	 */
	private ModelWeibo initWeibo(Cursor cursor) {
		String json = cursor.getString(cursor.getColumnIndex(weiboJson));
		ModelWeibo weibo = null;
		try {
			weibo = new ModelWeibo(new JSONObject(json));
		} catch (WeiboDataInvalidException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

//		int weibo_id = cursor.getInt(cursor.getColumnIndex(weiboId));
//		weibo.setWeiboId(weibo_id);
//		weibo.setUid(cursor.getInt(cursor.getColumnIndex(uid)));
//		weibo.setUsername(cursor.getString(cursor.getColumnIndex(userName)));
//		weibo.setContent(cursor.getString(cursor
//				.getColumnIndex(content)));
//		weibo.setCtime(cursor.getString(cursor
//				.getColumnIndex(cTime)));
//		weibo.setFrom(cursor.getString(cursor
//				.getColumnIndex(from)));
//		weibo.setTimestamp(cursor.getInt(cursor
//				.getColumnIndex(timeStamp)));
//		weibo.setCommentCount(cursor.getInt(cursor
//				.getColumnIndex(commentnum)));
//		weibo.setType(cursor.getString(cursor
//				.getColumnIndex(type)));
//		weibo.setIsDigg(cursor.getInt(cursor
//				.getColumnIndex(isdigg)));
//		weibo.setDiggNum(cursor.getInt(cursor
//				.getColumnIndex(diggnum)));
//		weibo.setWeiboIsDelelet(cursor.getInt(cursor
//				.getColumnIndex(isdel)));
//		//获取图片附件
//		weibo.setAttachImage(attachSqlHelper.getAttachsByWeiboId(weibo_id));
//
//		weibo.setTranspondId(cursor.getInt(cursor
//				.getColumnIndex(transpondId)));
//		if (cursor.getString(cursor
//				.getColumnIndex(transpond)) != null) {
//			try {
//				weibo.setSourceWeibo(new ModelWeibo(
//						new JSONObject(
//								cursor.getString(cursor
//										.getColumnIndex(transpond)))));
//			} catch (WeiboDataInvalidException e) {
//				Log.e("WeiboDbHelper", e.toString());
//			} catch (Exception e) {
//				Log.e("WeiboDbHelper", e.toString());
//			}
//		}
//		weibo.setTranspondCount(cursor.getInt(cursor
//				.getColumnIndex(transpondCount)));
//		weibo.setFavorited(isFavourt(cursor.getInt(cursor
//				.getColumnIndex(favorited))));
//		weibo.setUserface(cursor.getString(cursor
//				.getColumnIndex(userface)));
//		weibo.setTempJsonString(cursor.getString(cursor
//				.getColumnIndex(weiboJson)));
//		weibo.setSource_name(cursor.getString(cursor.getColumnIndex(sourceName)));
//		weibo.setTitle(cursor.getString(cursor.getColumnIndex(title)));
//
//		JSONObject weiboData;
//		try {
//			weiboData = new JSONObject(weibo.getTempJsonString());
//			// 用户认证信息
//			if (weiboData.has("user_group")) {
//				weibo.setUsApprove(new UserApprove(weiboData));
//			}
//
//			if (weiboData.has("api_source")
//					&& !weiboData.isNull("api_source")) {
//				try {
//					weibo.setPosts(new Posts(weiboData
//							.getJSONObject("api_source")));
//				} catch (DataInvalidException e) {
//					Log.d("WeiboDbHelper", "weibo construc method error get post data "
//									+ e.toString());
//				}
//			}
//
//		} catch (JSONException e) {
//			Log.e("WeiboDB", e.toString());
//		}

		return weibo;
	}

	/**
	 * 获取小于指定微博id后的count条数据
	 * @return
	 */
	private synchronized ListData<SociaxItem> getList(int count, int maxId) {
		Cursor cursor = getReadableDatabase().query(table_name,
				null, weiboType + " = " + transWeiboType(weibo_type)
						+ " and " + loginId + " = "
						+ Thinksns.getMy().getUid() + " and "
						+ weiboId + " < " + maxId, null, null, null,
				weiboId + " DESC limit " + count);
		ListData<SociaxItem> weiboDatas = new ListData<SociaxItem>();

		if (cursor.moveToFirst())
			do {
				ModelWeibo weibo = initWeibo(cursor);
				weiboDatas.add(weibo);
				lastId = weibo.getWeiboId();
			} while (cursor.moveToNext());

		cursor.close();
		attachSqlHelper.close();

		return weiboDatas;
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

	private int transWeiboType(ListData.DataType type) {
		switch(type) {
		case ALL_WEIBO:
			return 0;
		case FRIENDS_WEIBO:
			return 1;
		case RECOMMEND_WEIBO:
			return 2;
		case WEIBO:
			return 3;
        case CHANNELS_WEIBO:
            return 4;
		default:
			return -1;
		}
	}

	private ContentValues initValues(ModelWeibo weibo) {
		ContentValues map = new ContentValues();
		//保存服务端JSON串
		map.put(weiboJson, weibo.getWeiboJsonString());
		map.put(loginId, Thinksns.getMy().getUid());
		map.put(weiboId, weibo.getWeiboId());
		map.put(weiboType, transWeiboType(weibo_type));		//存放的微博类型

//		map.put(weiboId, weibo.getWeiboId());
//		map.put(uid, weibo.getUid());		//微博发布人ID
//		map.put(userName, weibo.getUsername());
//		map.put(content, weibo.getContent());
//		map.put(cTime, weibo.getCtime());
//		map.put(from, weibo.getFrom());
//		map.put(timeStamp, weibo.getTimestamp());
//		map.put(commentnum, weibo.getCommentCount());
//		map.put(type, weibo.getType());
//		map.put(attach, weibo.hasImage() ? 0 : 1);		//是否存在附件
//
//		if (weibo.getAttachImage() != null) {
//			int size = weibo.getAttachImage().size();
//			for (int i = 0; i < size; i++) {
//				attachSqlHelper.addAttach((ModelImageAttach) weibo.getAttachImage().get(i), 2);
//			}
//		}
//		attachSqlHelper.close();
//		if (weibo.isNullForTranspondId()) {
//			//如果是转发微博
//			map.put(transpond, weibo.getSourceWeibo().getWeiboJsonString());
//		}
//		map.put(transpondCount, weibo.getTranspondCount());
//		map.put(userface, weibo.getUserface());
//		map.put(transpondId, weibo.getIsRepost());
//		map.put(favorited, weibo.isFavorited() ? 1 : 0);
//		map.put(diggnum, (weibo.getDiggNum()));
//		map.put(isdigg, (weibo.getIsDigg()));
//		map.put(isdel, (weibo.isWeiboIsDelete()));
////		map.put("site_id", Thinksns.getMySite().getSite_id());
//		if (weibo.getSourceWeibo() != null) {
//			map.put(sourceName, weibo.getSourceWeibo().getSource_name());
//			map.put(title, weibo.getSourceWeibo().getTitle());
//		}
//		if (weibo.getSource_name() != null) {
//			map.put(sourceName, weibo.getSource_name());
//		}
//		if (weibo.getTitle() != null) {
//			map.put(title, weibo.getTitle());
//		}
		return map;
	}


	@Override
	public ListData<SociaxItem> getFooterList(int count, int lastId) {
		Log.e("WeiboDbHelper", "last id is " + lastId);
		if(lastId == 0)
			return getHeaderList(count);
		else {
			return getList(count, lastId);
		}

	}

}
