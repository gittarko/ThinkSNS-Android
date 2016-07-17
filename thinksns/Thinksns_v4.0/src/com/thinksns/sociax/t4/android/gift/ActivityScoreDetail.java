package com.thinksns.sociax.t4.android.gift;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;

import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.adapter.AdapterMyScore;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.Listener.ListenerRefreshComplete;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.component.ListScoreDetail;
import com.thinksns.sociax.t4.component.ListSociax;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明：积分明细
 * 
 * @author Zoey
 * @date 2015年9月21日
 * @version 1.0
 */
public class ActivityScoreDetail extends ThinksnsAbscractActivity {

	private ListSociax listView;
	private ListData<SociaxItem> list;
	private AdapterSociaxList adapter;
	private ImageButton tv_title_left;
	protected boolean isRefreshing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);

		initIntentData();
		initView();
		initListener();
		initData();
	}

	private void initIntentData() {

	}

	private void initView() {
		tv_title_left= (ImageButton) findViewById(R.id.tv_title_left);
		listView = (ListScoreDetail) findViewById(R.id.lv_score_detail);
		list = new ListData<SociaxItem>();
		adapter = new AdapterMyScore(this, list, 20);
		listView.setAdapter(adapter);
		adapter.loadInitData();
		
		IntentFilter filter_del_room = new IntentFilter();
		filter_del_room.addAction(StaticInApp.UPDATE_SCORE_DETAIL);
		this.registerReceiver(broad_update_score, filter_del_room);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(broad_update_score);  
		super.onDestroy();
	}
	
	protected BroadcastReceiver broad_update_score = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(StaticInApp.UPDATE_SCORE_DETAIL)) {
				adapter.doUpdataList();
			}
		}
	};
	
	private void initListener() {
		tv_title_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				// 当不滚动时
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 判断滚动到底部
					if (listView.getLastVisiblePosition() == (listView.getCount() - 1)) {
						if (!isRefreshing) {
							// 如果当前没有刷新操作正在进行才执行刷新操作
							ImageView iv = (ImageView) view.findViewById(R.id.anim_view);
							if (iv != null) {
								iv.setVisibility(View.VISIBLE);
								Anim.refresh(ActivityScoreDetail.this, iv);
								HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) listView.getAdapter();
								AdapterMyScore giftAdapter = (AdapterMyScore) headerAdapter.getWrappedAdapter();
								giftAdapter.animView = iv;
								giftAdapter.doRefreshFooter();
							}
							isRefreshing = true;
						}
					}

					// 判断滚动到顶部
					if (listView.getFirstVisiblePosition() == 0) {
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}

		});

		// 监听是否正在刷新
		adapter.setOnCompleteListener(new ListenerRefreshComplete() {

			@Override
			public void onRefreshComplete() {
				isRefreshing = false;
			}
		});
	}

	private void initData() {

	}
	
	@Override
	public OnTouchListListener getListView() {
		return listView;
	}

	@Override
	public String getTitleCenter() {
		return null;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_score_detail;
	}
}
