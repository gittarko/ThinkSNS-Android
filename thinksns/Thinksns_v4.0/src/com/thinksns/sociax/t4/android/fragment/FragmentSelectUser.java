package com.thinksns.sociax.t4.android.fragment;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.t4.adapter.AdapterSelectUser;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.erweima.units.SideBar;
import com.thinksns.sociax.t4.model.ModelGift;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * 类说明： 选择好友,默认为多选，特殊情况如果activity的intent传入int StaticInApp.SELECT_GIFT_RESEND,表示礼物转赠，单选
 * @author wz
 * @date 2014-11-18
 * @version 1.0
 */
public class FragmentSelectUser extends FragmentSociax {
	protected ModelUser selectUser;
	protected int selectpostion;
	protected ListHandler mHandler;
	ListData<SociaxItem> selectUserList;
	private LinearLayout title_layout;
	private SideBar sidrbar;
	private PullToRefreshListView pullToRefreshListView;

	@Override
	public void initView() {
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		listView = pullToRefreshListView.getRefreshableView();
		listView.setDivider(new ColorDrawable(0xffdddddd));
		listView.setDividerHeight(1);

		adapter = createAdapter();
		listView.setAdapter(adapter);
		mHandler = new ListHandler();
		
		title_layout=(LinearLayout)findViewById(R.id.title_layout);
		sidrbar = (SideBar)findViewById(R.id.sidrbar);
		
		title_layout.setVisibility(View.GONE);
		sidrbar.setVisibility(View.GONE);
	}

	@Override
	public AdapterSociaxList createAdapter() {
		list = new ListData<SociaxItem>();
		return new AdapterSelectUser(this, list, uid,getActivity().getIntent().getIntExtra("select_type", StaticInApp.SELECT_CHAT_USER)==StaticInApp.SELECT_GIFT_RESEND);
	}

	@Override
	public void initIntentData() {
	}

	@Override
	public void initListener() {
	}

	@Override
	public void initData() {
		adapter.loadInitData();
	}

	@SuppressLint("HandlerLeak")
	public class ListHandler extends Handler {

		public ListHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case StaticInApp.SEND_GIFT:
				try {
					JSONObject result = new JSONObject(msg.obj.toString());
					Toast.makeText(getActivity(), result.getString("msg"),
							Toast.LENGTH_SHORT).show();
					if (result.getString("status").equals("1")) {
						getActivity().finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case StaticInApp.RESEND_GIFT:
				try {
					JSONObject result = new JSONObject(msg.obj.toString());
					Toast.makeText(getActivity(), result.getString("msg"),
							Toast.LENGTH_SHORT).show();
					if (result.getString("status").equals("1")) {
						getActivity().finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			}
			
		}
	}

	/**
	 * 发送礼物
	 */
	public void sendGift() {
		if (list.size() != 0) {
			selectUserList = new ListData<SociaxItem>();
			String uids = "";
			for (int i = 0; i < list.size(); i++) {
				if (((ModelSearchUser) list.get(i)).isSelect()) {
					selectUserList.add(list.get(i));
					uids += ((ModelSearchUser) list.get(i)).getUid() + ",";
				}
				Log.v("FragmentSelectUser--sendGift", "wztest uids=" + uids);
			}
			if (selectUserList.size() == 0) {
				Toast.makeText(getActivity(), "请先选择好友", Toast.LENGTH_SHORT).show();
			} else {
				final String uid = uids.substring(0, uids.lastIndexOf(","));

				Log.v("FragmentSelectUser--sendGift", "wztest uid=" + uid);
				new Thread(new Runnable() {

					@Override
					public void run() {
						Message msg = new Message();
						msg.what = StaticInApp.SEND_GIFT;
						Thinksns app = (Thinksns) getActivity()
								.getApplication().getApplicationContext();
						try {
							msg.obj = app.getApiGift()
									.sentGift(
											((ModelGift) getActivity()
													.getIntent()
													.getSerializableExtra(
															"gift")).getId(),
											uid, null, null);
						} catch (Exception e) {
							e.printStackTrace();
						}
						mHandler.sendMessage(msg);

					}
				}).start();
			}
		}
	}

	/**
	 * 获取被选择的用户列表
	 * 
	 * @return
	 */
	public ListData<SociaxItem> getSelectUser() {
		selectUserList = new ListData<SociaxItem>();
		selectUserList.clear();
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				if (((ModelSearchUser) list.get(i)).isSelect()) {
					selectUserList.add(list.get(i));
				}
			}
		}
		return selectUserList;
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_chat_userlist;
	}
	/**
	 * 转赠给好友
	 */
	public void resendGift() {
		if (list.size() != 0) {
			selectUserList = new ListData<SociaxItem>();
			String uids = "";
			for (int i = 0; i < list.size(); i++) {
				if (((ModelSearchUser) list.get(i)).isSelect()) {
					selectUserList.add(list.get(i));
					uids += ((ModelSearchUser) list.get(i)).getUid() + ",";
				}
				Log.v("FragmentSelectUser--sendGift", "wztest uids=" + uids);
			}
			if (selectUserList.size() == 0) {
				Toast.makeText(getActivity(), "请先选择好友", Toast.LENGTH_SHORT)
						.show();
			} else {
				final String uid = uids.substring(0, uids.lastIndexOf(","));
				Log.v("FragmentSelectUser--sendGift", "wztest uid=" + uid);
				new Thread(new Runnable() {

					@Override
					public void run() {
						Message msg = new Message();
						msg.what = StaticInApp.RESEND_GIFT;
						Thinksns app = (Thinksns) getActivity()
								.getApplication().getApplicationContext();
						ModelGift gift=(ModelGift) getActivity().getIntent().getSerializableExtra("gift");
						try {
							msg.obj = app.getApiGift()
									.resentGift(gift.getGiftId(),gift.getId(),
											uid, null, null);
						} catch (Exception e) {
							e.printStackTrace();
						}
						mHandler.sendMessage(msg);

					}
				}).start();
			}
		}
	}
}
