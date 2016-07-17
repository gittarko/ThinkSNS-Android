package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.adapter.AdapterWeiboDigg;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;
import com.thinksns.sociax.t4.model.ModelDiggUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

public class FragmentWeiboDigg extends FragmentSociax implements PullToRefreshBase.OnRefreshListener2<ListView> {

    private PullToRefreshListView pullToRefreshListView;
    private int weibo_id;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_userlist;
    }

    @Override
    public void initView() {
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_ios)));
        listView.setDividerHeight(1);
        listView.setSelector(getResources().getDrawable(R.drawable.listitem_selector));

        adapter = createAdapter();
        listView.setAdapter(adapter);
    }

    @Override
    public AdapterSociaxList createAdapter() {
        list = new ListData<SociaxItem>();
        return new AdapterWeiboDigg(FragmentWeiboDigg.this, list, weibo_id);
    }

    @Override
    public void initIntentData() {
        weibo_id = getActivity().getIntent().getIntExtra("weibo_id", -1);
    }

    @Override
    public void initListener() {
        pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ModelDiggUser user = (ModelDiggUser) adapter.getItem((int) id);
				Intent intent = new Intent(getActivity(), ActivityUserInfo_2.class);
				intent.putExtra("uid", user.getUid());
				startActivity(intent);
			}
		});
    }

    @Override
    public void initData() {
        adapter.loadInitData();
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
}
