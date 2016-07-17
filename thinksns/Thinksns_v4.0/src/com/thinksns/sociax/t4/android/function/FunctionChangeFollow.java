package com.thinksns.sociax.t4.android.function;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.db.DbHelperManager;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明： 修改关注状态
 * @author wz
 * @date 2014-10-30
 * @version 1.0
 */
public class FunctionChangeFollow extends FunctionSoicax {
	View view;
	ModelSearchUser follow;

	ListHandler handlerList;
	BaseAdapter adapter;

	public FunctionChangeFollow(Context context) {
		super(context);
		
	}

	/**
	 * 从用户列表中生成的关注图标修改关注功能
	 * 
	 * @param context
	 * @param adapter
	 *            被操作的列表
	 * @param v
	 *            被操作的view，需要setTag r.id.tag_position r.id.tag_follow
	 */
	public FunctionChangeFollow(Context context, AdapterSociaxList adapter,
			View v) {
		super(context);
		this.view = v;
		this.adapter = adapter;
		this.follow = (ModelSearchUser) adapter.getItem((Integer) v
				.getTag(R.id.tag_position));
		this.handlerList = new ListHandler();
	}

	public FunctionChangeFollow(Context context, BaseAdapter adapter,
								View v) {
		super(context);
		this.view = v;
		this.adapter = adapter;
		this.follow = (ModelSearchUser) adapter.getItem((Integer) v
				.getTag(R.id.tag_position));
		this.handlerList = new ListHandler();
	}

	/**
	 * 修改列表中的关注状态
	 */
	public void changeListFollow() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Message msg = new Message();
					msg.what = StaticInApp.CHANGE_LISTFOLLOW;
					msg.obj = thread
							.getApp()
							.getUsers()
							.changeFollowing(follow.getUid(),follow.getFollowing().equals("1") ? 0 : 1);
					handlerList.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 用于修改列表数据
	 * 
	 * @author wz
	 * 
	 */
	@SuppressLint("HandlerLeak")
	public class ListHandler extends Handler {

		public ListHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			view.setClickable(true);
			switch (msg.what) {
			case StaticInApp.CHANGE_LISTFOLLOW:
				if (msg.obj == null) {
					Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				} else {
					JSONObject result = (JSONObject) msg.obj;
					try {
						if (result.getString("status").equals("1")) {
							follow.setFollowing(follow.getFollowing().equals("1") ? "0" : "1");
							
							//取消关注了某人，则朋友圈不再显示他的微博
							if (follow.getFollowing().equals("0")) {
								
								Log.v("deleteWeibo", "------deleteWeibo----follow.getUid()------"+follow.getUid());
								
								DbHelperManager.getInstance(Api.mContext, ListData.DataType.FRIENDS_WEIBO).deleteSomeOne(follow.getUid(),Thinksns.getMy().getUid());
								//发送广播至朋友圈，更新页面
								Intent intent = new Intent();  
								intent.setAction(StaticInApp.NOTIFY_WEIBO);  
								context.sendBroadcast(intent);  
							}else {
								//发送广播至朋友圈，更新页面
								Intent intent = new Intent();  
								intent.setAction(StaticInApp.NOTIFY_WEIBO);  
								context.sendBroadcast(intent);  
							}
							
							adapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					try {
						Toast.makeText(context, result.getString("msg"),
								Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			}
		}
	}
	
	@Override
	protected void initUiHandler() {

	}

	@Override
	protected void initActivtyHandler() {
		
	}
}
