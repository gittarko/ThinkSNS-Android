package com.thinksns.sociax.t4.android.function;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.model.ModelBackMessage;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

import org.json.JSONObject;

/**
 * 类说明： 修改微博状态
 * 
 * @author wz
 * @date 2014-12-25
 * @version 1.0
 */
public class FunctionChangeSociaxItemStatus extends FunctionSoicax {
	public FunctionChangeSociaxItemStatus(Context context) {
		super(context);
	}

	@Override
	protected void initUiHandler() {
		handlerUI = new UIHandler();
	}

	public class UIHandler extends Handler {

		public UIHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case StaticInApp.CHANGE_POST_FAVOURITE:
				try {
					JSONObject result = new JSONObject(msg.obj.toString());
					if (result.getString("status").equals("1")) {
						listener.onTaskSuccess();
						
						Log.v("digState", "----json-----dig---------");
					} else {
						listener.onTaskError();
					}
//					Toast.makeText(context, result.getString("msg"),Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
					listener.onTaskError();
					Log.v("digState", "------digState---e-----------"+e.getMessage());
					Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				}
			case StaticInApp.CHANGE_USERINFO_FOLLOW:
				try {
					JSONObject result = new JSONObject(msg.obj.toString());
					if (result.getString("status").equals("1")) {
						listener.onTaskSuccess();
					} else {
						listener.onTaskError();
					}
					Toast.makeText(context, result.getString("msg"),
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
					listener.onTaskError();
					Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				}
				break;
			case StaticInApp.CHANGE_CHANNEL_FOLLOW:
				try {
					JSONObject result = new JSONObject(msg.obj.toString());
					if (result.getString("status").equals("1")) {
						listener.onTaskSuccess();
					} else {
						listener.onTaskError();
					}
					Toast.makeText(context, result.getString("msg"),
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
					listener.onTaskError();
					Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				}
				break;
			case StaticInApp.CHANGE_WEIBO_DIGG:
				try {
					ModelBackMessage result = (ModelBackMessage) msg.obj;
					if (result.getStatus()==1) {
						listener.onTaskSuccess();
					} else {
						listener.onTaskError();
					}
				} catch (Exception e) {
					e.printStackTrace();
					listener.onTaskError();
					Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	}

	/**
	 * 
	 * 修改帖子收藏状态
	 * 
	 * @param post_id
	 *            帖子id
	 * @param preStatus
	 *            当前收藏状态 1已经收藏，0还未收藏
	 */
	public void changePostFavourite(final int post_id, final int weiba_id,
			final int post_uid, final String preStatus) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = StaticInApp.CHANGE_POST_FAVOURITE;
				try {
					msg.obj = app.getWeibaApi().getChangePostFavourite(post_id,
							weiba_id, post_uid, preStatus);
				} catch (ApiException e) {
					e.printStackTrace();
				}
				handlerUI.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 修改帖子的赞的状态
	 * 
	 * @param post_id
	 * @param weiba_id
	 * @param post_uid
	 * @param preStatus
	 */
	public void changePostDigg(final int post_id, final int weiba_id,
			final int post_uid, final String preStatus) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = StaticInApp.CHANGE_POST_FAVOURITE;
				try {
					msg.obj = app.getWeibaApi().getChangePostDigg(post_id,
							weiba_id, post_uid, preStatus);
				} catch (ApiException e) {
					e.printStackTrace();
				}
				handlerUI.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 
	 * 修改个人主页的关注状态
	 * 
	 * @param uid
	 *            修改的uid
	 * @param preFollowStatus
	 *            修改前状态
	 */
	public void changeUserInfoFollow(final int uid,
			final boolean preFollowStatus) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Message msg = new Message();
					msg.what = StaticInApp.CHANGE_USERINFO_FOLLOW;
					msg.obj = thread.getApp().getUsers()
							.changeFollowing(uid, preFollowStatus ? 0 : 1);
					handlerUI.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	protected void initActivtyHandler() {
	}

	/**
	 * 修改频道的关注状态
	 * 
	 * @param id
	 *            频道id
	 * @param is_follow
	 *            频道关注
	 */
	public void changeChannelFollow(final int id, final int is_follow) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Message msg = new Message();
					msg.what = StaticInApp.CHANGE_CHANNEL_FOLLOW;
					msg.obj = thread
							.getApp()
							.getChannelApi()
							.changeFollow(id + "",
									is_follow==1 ? "0" : "1");
					handlerUI.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * 修改微博赞的状态
	 * 
	 * @param weibo_id
	 *            微博id
	 * @param preDiggStatus
	 *            之前的赞状态
	 */
	public void changeWeiboDigg(final int weibo_id, final int preDiggStatus) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Message msg = new Message();
					msg.what = StaticInApp.CHANGE_WEIBO_DIGG;
					msg.obj = thread.getApp().getStatuses()
								.changeWeiboDigg(weibo_id, preDiggStatus);
					handlerUI.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
