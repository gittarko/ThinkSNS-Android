package com.thinksns.sociax.db;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.UserDataInvalidException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class UserSqlHelper extends SqlHelper {
	private static UserSqlHelper instance;
	private static ThinksnsTableSqlHelper handler;

	private UserSqlHelper(Context context) {
		this.handler = new ThinksnsTableSqlHelper(context,null);
	}

	public static UserSqlHelper getInstance(Context context) {
		if (instance == null) {
			instance = new UserSqlHelper(context);
		}

		return instance;
	}

	public long addUser(ModelUser user, boolean isLogin) {
		ContentValues map = new ContentValues();
		map.put(ThinksnsTableSqlHelper.id, user.getUid());
		map.put(ThinksnsTableSqlHelper.uname, user.getUserName());
		map.put(ThinksnsTableSqlHelper.token, user.getToken());
		map.put(ThinksnsTableSqlHelper.secretToken, user.getSecretToken());
		map.put(ThinksnsTableSqlHelper.province, user.getProvince());
		map.put(ThinksnsTableSqlHelper.city, user.getCity());
		map.put(ThinksnsTableSqlHelper.location, user.getLocation());
		map.put(ThinksnsTableSqlHelper.face, user.getFace());
		map.put(ThinksnsTableSqlHelper.cover, user.getCover());
		map.put(ThinksnsTableSqlHelper.sex, user.getSex());
		map.put(ThinksnsTableSqlHelper.department, user.getDepartment());
		map.put(ThinksnsTableSqlHelper.usertel, user.getTel());
		map.put(ThinksnsTableSqlHelper.userEmail, user.getUserEmail());
		map.put(ThinksnsTableSqlHelper.userPhone, user.getUserPhone());
		map.put(ThinksnsTableSqlHelper.QQ, user.getQQ());
		map.put(ThinksnsTableSqlHelper.userInfo, user.getIntro());
		map.put(ThinksnsTableSqlHelper.userTag, user.getUserTag());

		map.put(ThinksnsTableSqlHelper.weiboCount, user.getWeiboCount());
		map.put(ThinksnsTableSqlHelper.followersCount, user.getFollowersCount());
		map.put(ThinksnsTableSqlHelper.followedCount, user.getFollowedCount());
		map.put(ThinksnsTableSqlHelper.isFollowed, user.isFollowed());
		if (!user.isNullForLastWeibo()) {
			map.put(ThinksnsTableSqlHelper.lastWeiboId, user.getLastWeibo()
					.getWeiboId());
			map.put(ThinksnsTableSqlHelper.myLastWeibo, user.getLastWeibo()
					.getWeiboJsonString());
		}
		map.put(ThinksnsTableSqlHelper.userJson, user.toJSON());
		map.put(ThinksnsTableSqlHelper.isLogin, isLogin);

		return handler.getWritableDatabase().insert(
				ThinksnsTableSqlHelper.tableName, null, map);
	}

	public void clear() {
		handler.getWritableDatabase().execSQL(
				"delete from " + ThinksnsTableSqlHelper.tableName);
	}

	public ModelUser getLoginedUser() throws UserDataInvalidException {

		Cursor result = handler.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tableName, null,
				ThinksnsTableSqlHelper.isLogin + "=1", null, null, null, null);

		ModelUser user = null;
		if (result != null) {
			// 游标不为空
			if (result.moveToFirst()) {
				//直接读取用户JSON数据
				String userJson = result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.userJson));
				try {
					user = new ModelUser(new JSONObject(userJson));
					user.setToken(result.getString(result
							.getColumnIndex(ThinksnsTableSqlHelper.token)));
					user.setSecretToken(result.getString(result
							.getColumnIndex(ThinksnsTableSqlHelper.secretToken)));
				} catch (DataInvalidException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		result.close();
		return user;
	}

	public ModelUser getUser(String sql) throws UserDataInvalidException {
		Cursor result = handler.getReadableDatabase().query(
				ThinksnsTableSqlHelper.tableName, null, sql, null, null, null,
				null);
		if (result != null) {
			// 游标不为空
			if (result.moveToFirst()) {
				String userJson = result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.userJson));
				ModelUser user = null;
				try {
					user = new ModelUser(new JSONObject(userJson));
					user.setUid(result.getInt(result
							.getColumnIndex(ThinksnsTableSqlHelper.id)));
					user.setUserName(result.getString(result
							.getColumnIndex(ThinksnsTableSqlHelper.uname)));
					user.setToken(result.getString(result
							.getColumnIndex(ThinksnsTableSqlHelper.token)));
					user.setSecretToken(result.getString(result
							.getColumnIndex(ThinksnsTableSqlHelper.secretToken)));
					return user;
				} catch (DataInvalidException e) {
					e.printStackTrace();
				}catch(JSONException e) {
					e.printStackTrace();
				}

//				user.setProvince(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.province)));
//				user.setCity(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.city)));
//				user.setLocation(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.location)));
//				user.setFace(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.face)));
//				user.setCover(result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.cover)));
//				user.setSex(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.sex)));
//
//				user.setDepartment(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.department)));
//				user.setTel(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.usertel)));
//				user.setUserEmail(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.userEmail)));
//				user.setUserPhone(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.userPhone)));
//				user.setQQ(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.QQ)));
//				user.setIntro(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.userInfo)));
//				user.setUserTag(result.getString(result
//						.getColumnIndex(ThinksnsTableSqlHelper.userTag)));
//
//				user.setWeiboCount(result.getInt(result
//						.getColumnIndex(ThinksnsTableSqlHelper.weiboCount)));
//				user.setFollowedCount(result.getInt(result
//						.getColumnIndex(ThinksnsTableSqlHelper.followedCount)));
//				user.setFollowersCount(result.getInt(result
//						.getColumnIndex(ThinksnsTableSqlHelper.followersCount)));
//				user.setFollowed(result.getInt(result
//						.getColumnIndex(ThinksnsTableSqlHelper.isFollowed)) == 0);
//				int lastWeiboId = result.getInt(result.getColumnIndex(ThinksnsTableSqlHelper.lastWeiboId));
//				if (result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.myLastWeibo)) != null) {
//					try {
//						user.setLastWeibo(new ModelWeibo(
//								new JSONObject(
//										result.getString(result.getColumnIndex(ThinksnsTableSqlHelper.myLastWeibo)))));
//					} catch (WeiboDataInvalidException e) {
//						e.printStackTrace();
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
			} else {
				result.close();
				throw new UserDataInvalidException();
			}

			result.close();
		}

		throw new UserDataInvalidException();
	}

	//根据用户UID查找用户
	public ModelUser getUserById(int uid) {
		String sql = "uid = " + uid;
		try {
			return getUser(sql);
		} catch (UserDataInvalidException e) {
			e.printStackTrace();
		}

		return null;
	}

	//根据用户UID查找用户
	public ModelUser getUserByName(String name) {
		String sql = "uname = '" + name + "'";
		try {
			return getUser(sql);
		} catch (UserDataInvalidException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void close() {
		handler.close();
	}

	public static int updateUser(ModelUser user) {
		ContentValues map = new ContentValues();
		map.put(ThinksnsTableSqlHelper.uid, user.getUid());
		map.put(ThinksnsTableSqlHelper.uname, user.getUserName());
		map.put(ThinksnsTableSqlHelper.token, user.getToken());
		map.put(ThinksnsTableSqlHelper.secretToken, user.getSecretToken());
		map.put(ThinksnsTableSqlHelper.province, user.getProvince());
		map.put(ThinksnsTableSqlHelper.city, user.getCity());
		map.put(ThinksnsTableSqlHelper.location, user.getLocation());
		map.put(ThinksnsTableSqlHelper.face, user.getFace());
		map.put(ThinksnsTableSqlHelper.cover, user.getCover());
		map.put(ThinksnsTableSqlHelper.sex, user.getSex());
		map.put(ThinksnsTableSqlHelper.department, user.getDepartment());
		map.put(ThinksnsTableSqlHelper.usertel, user.getTel());
		map.put(ThinksnsTableSqlHelper.userEmail, user.getUserEmail());
		map.put(ThinksnsTableSqlHelper.userPhone, user.getUserPhone());
		map.put(ThinksnsTableSqlHelper.QQ, user.getQQ());
		map.put(ThinksnsTableSqlHelper.userInfo, user.getIntro());
		map.put(ThinksnsTableSqlHelper.userTag, user.getUserTag());

		map.put(ThinksnsTableSqlHelper.weiboCount, user.getWeiboCount());
		map.put(ThinksnsTableSqlHelper.followersCount, user.getFollowersCount());
		map.put(ThinksnsTableSqlHelper.followedCount, user.getFollowedCount());
		map.put(ThinksnsTableSqlHelper.isFollowed, user.isFollowed());
		if (!user.isNullForLastWeibo()) {
			map.put(ThinksnsTableSqlHelper.lastWeiboId, user.getLastWeibo()
					.getWeiboId());
			map.put(ThinksnsTableSqlHelper.myLastWeibo, user.getLastWeibo()
					.getWeiboJsonString());
		}

		map.put(ThinksnsTableSqlHelper.userJson, user.toJSON());

		long i = handler.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tableName, map,
				ThinksnsTableSqlHelper.uid + "=" + user.getUid(), null);
		if(i == 0) {
			//插入信息
			i = handler.getWritableDatabase().insert(
					ThinksnsTableSqlHelper.tableName, null, map);
		}
		return (int)i;
	}

	//更新用户头像
	public int updateUserFace(ModelUser user) {
		ContentValues map = new ContentValues();
		map.put(ThinksnsTableSqlHelper.face, user.getFace());
		int i = handler.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tableName, map,
				ThinksnsTableSqlHelper.uid + "=" + user.getUid(), null);
		return i;
	}

	//更新用户封面
	public int updateUserCover(ModelUser user) {
		ContentValues map = new ContentValues();
		map.put(ThinksnsTableSqlHelper.cover, user.getCover());
		int i = handler.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tableName, map,
				ThinksnsTableSqlHelper.uid + "=" + user.getUid(), null);
		return i;
	}

	//设置用户退出登录
	public int setUserLogout(ModelUser user) {
		ContentValues map = new ContentValues();
		map.put(ThinksnsTableSqlHelper.isLogin, false);
		int i = handler.getWritableDatabase().update(
				ThinksnsTableSqlHelper.tableName, map,
				ThinksnsTableSqlHelper.uid + "=" + user.getUid(), null);
		return i;
	}

	public long addSiteUser(String userName) {
		ContentValues map = new ContentValues();
		map.put("u_name", userName);
		return handler.getWritableDatabase().insert(
				ThinksnsTableSqlHelper.tbSiteUser, null, map);
	}

	public boolean hasUname(String userName) {
		Cursor cursor = handler.getWritableDatabase().rawQuery(
				"select * from " + ThinksnsTableSqlHelper.tbSiteUser
						+ " where u_name = ? ", new String[] { userName });
		boolean result = cursor.moveToFirst();
		cursor.close();
		return result;
	}

	/**
	 * 获取用户名称列表
	 * 
	 * @return
	 */
	public ArrayList<String> getUnameList() {
		ArrayList<String> unameList = new ArrayList<String>();
		Cursor cursor = handler.getWritableDatabase().rawQuery(
				"select * from " + ThinksnsTableSqlHelper.tbSiteUser, null);
		if (cursor.moveToFirst()) {
			do {
				unameList
						.add(cursor.getString(cursor.getColumnIndex("u_name")));
			} while (cursor.moveToNext());
		}
		return unameList;
	}

}
