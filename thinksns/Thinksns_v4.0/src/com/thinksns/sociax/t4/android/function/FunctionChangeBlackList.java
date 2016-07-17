package com.thinksns.sociax.t4.android.function;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明：黑名单修改
 * 
 * @author wz
 * @date 2014-11-12
 * @version 1.0
 */
public class FunctionChangeBlackList extends FunctionSoicax {
	View view;// 需要setTag r.id.tag_position r.id.tag_follow
	ModelSearchUser follow;// 用户列表中的用户

	UIHandler handlerUI;
	ListHandler handlerList;
	AdapterSociaxList adapter;
	ListData<SociaxItem> list;

	public FunctionChangeBlackList(Context context) {
		super(context);
	}

	/**
	 * 从用户列表中生成的黑名单图标删除功能
	 * 
	 * @param context
	 * @param adapter
	 *            被操作的列表
	 * @param v
	 *            被操作的view，需要setTag r.id.tag_position r.id.tag_follow
	 * @param list
	 */
	public FunctionChangeBlackList(Context context, AdapterSociaxList adapter,
			View v, ListData<SociaxItem> list) {
		super(context);
		this.view = v;
		this.adapter = adapter;
		this.follow = (ModelSearchUser) adapter.getItem((Integer) v
				.getTag(R.id.tag_position));
		this.handlerList = new ListHandler();
		this.handlerUI = new UIHandler();
		this.list = list;
	}

	/**
	 * 修改列表中的关注状态
	 */
	public void deleteFromBlackList() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Message msg = new Message();
					msg.what = StaticInApp.REMOVE_BLACKLIST;
					msg.obj = thread.getApp().getUsers()
							.removeBlackList(follow.getUid());
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
			switch (msg.what) {
			case StaticInApp.REMOVE_BLACKLIST:
				if (msg.obj == null) {
					Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				} else {
					JSONObject result = (JSONObject) msg.obj;
					try {
						Log.v("Fucntion ChangeBlackList", result.toString());

						if (result.getString("status").equals("1")) {

							int position = (Integer) view
									.getTag(R.id.tag_position);
							ListData<SociaxItem> tempList = new ListData<SociaxItem>();
							for (int i = 0; i < list.size(); i++) {
								if (i == position)
									continue;
								tempList.add(list.get(i));
							}
							list.clear();
							list.addAll(tempList);
							adapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						Toast.makeText(context, result.getString("msg"),
								Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			}
		}
	}

	/**
	 * 用于修改UI
	 * 
	 * @author wz
	 * 
	 */
	@SuppressLint("HandlerLeak")
	public class UIHandler extends Handler {

		public UIHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Object digMsg[] = null;
			if (msg.obj instanceof Object[]) {
				digMsg = (Object[]) msg.obj;
			}
			switch (msg.what) {
			}
		}
	}

	@Override
	protected void initUiHandler() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initActivtyHandler() {
		// TODO Auto-generated method stub
		
	}

}
