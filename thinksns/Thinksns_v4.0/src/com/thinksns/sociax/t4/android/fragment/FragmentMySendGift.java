package com.thinksns.sociax.t4.android.fragment;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import com.thinksns.sociax.t4.adapter.AdapterMySendGift;
import com.thinksns.sociax.t4.android.Listener.ListenerRefreshComplete;
import com.thinksns.sociax.t4.component.ListMyGift;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明： 我的礼物
 * 
 * @author Zoey
 * @date 2015年9月21日
 * @version 1.0
 */
public class FragmentMySendGift extends FragmentSociax {

	protected boolean isRefreshing = false;
	
//	@Override
//	public OnTouchListListener getListView() {
//		return listView;
//	}

	@Override
	public void initView() {
		loadingView = (LoadingView) findViewById(LoadingView.ID);
		listView = (ListMyGift)findViewById(R.id.listView);
		list = new ListData<SociaxItem>();
		adapter = new AdapterMySendGift(this,list,1+"");
		listView.setAdapter(adapter);
	}
	
	@Override
	public void initIntentData() {
	}

	@Override
	public void initListener() {
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
								Anim.refresh(getActivity(), iv);
								HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) listView.getAdapter();
								AdapterMySendGift giftAdapter = (AdapterMySendGift) headerAdapter.getWrappedAdapter();
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

	@Override
	public void initData() {
		adapter.loadInitData();
	}

	@Override
	public void doRefreshFooter() {
		adapter.doRefreshFooter();

	}

	@Override
	public void doRefreshHeader() {
		adapter.doRefreshHeader();
	}

	@Override
	public int getLayoutId() {
		return R.layout.t4_fragment_my_send_gift;
	}
}
