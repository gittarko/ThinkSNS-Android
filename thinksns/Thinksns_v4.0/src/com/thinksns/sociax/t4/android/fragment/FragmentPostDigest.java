package com.thinksns.sociax.t4.android.fragment;

import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterPostDigestList;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;


/** 
 * 类说明：  精华帖，需要传入intent weiba_id
 * @author  wz    
 * @date    2014-12-26
 * @version 1.0
 */
public class FragmentPostDigest extends FragmentPostList implements
		PullToRefreshBase.OnRefreshListener2<ListView> {
	private int weiba_id = -1;
	private PullToRefreshListView pullToRefreshListView;
	private EmptyLayout emptyLayout;

	@Override
	public void initView() {
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		listView = pullToRefreshListView.getRefreshableView();
		listView.setBackgroundColor(getResources().getColor(R.color.bg_ios));
		listView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_listview_divider)));
		listView.setDividerHeight(1);
		listView.setSelector(R.drawable.list_selector);

		emptyLayout = (EmptyLayout)findViewById(R.id.empty_layout);
		emptyLayout.setNoDataContent(getResources().getString(R.string.empty_content));

		list = new ListData<SociaxItem>();
		adapter = new AdapterPostDigestList(this, list, weiba_id);
		listView.setAdapter(adapter);
	}

	@Override
	public void initIntentData() {
		weiba_id = getActivity().getIntent().getIntExtra("weiba_id", -1);
		if(weiba_id == -1){
			Log.e("FragmentPostDigest", "FragmentWeibaDigest needs intent weiba_id");
		}
	}

	@Override
	public void initListener() {
		pullToRefreshListView.setOnRefreshListener(this);
		emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	@Override
	public EmptyLayout getEmptyLayout() {
		return emptyLayout;
	}

	@Override
	public void initData() {
		adapter.loadInitData();
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_common_post_list;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
		if (adapter != null) {
			adapter.doRefreshHeader();
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
		if (adapter != null) {
			adapter.doRefreshFooter();
		}
	}

	@Override
	public PullToRefreshListView getPullRefreshView() {
		return pullToRefreshListView;
	}

	@Override
	public void executeDataSuccess(ListData<SociaxItem> list) {
		if(list.size() < AdapterSociaxList.PAGE_COUNT) {
			if(adapter.getLast() != null) {
				pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
				Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
			}
		}else {

		}

		super.executeDataSuccess(list);
	}
}
