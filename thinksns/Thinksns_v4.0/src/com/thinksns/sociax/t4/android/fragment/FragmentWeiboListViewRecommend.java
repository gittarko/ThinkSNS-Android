package com.thinksns.sociax.t4.android.fragment;

import android.content.IntentFilter;
import android.os.Bundle;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterWeiboAll;
import com.thinksns.sociax.t4.android.presenter.WeiboRecommendListPresenter;
import com.thinksns.sociax.t4.eventbus.WeiboEvent;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by hedong on 16/2/20.
 * 推荐微博类
 */
public class FragmentWeiboListViewRecommend extends FragmentWeiboListViewNew {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    protected IntentFilter getIntentFilter() {
        return null;
    }

    @Override
    protected String getCacheKey() {
        return "recommend_weibo";
    }

    @Override
    protected void initPresenter() {
        mPresenter = new WeiboRecommendListPresenter(getActivity(), this, this);
        mPresenter.setCacheKey(getCacheKey());
    }

    @Override
    protected void initListViewAttrs() {
        super.initListViewAttrs();
        mListView.setDividerHeight(UnitSociax.dip2px(getActivity(), 0.5f));
        mListView.setSelector(R.drawable.list_selector);
    }

    @Override
    protected boolean requestDataIfViewCreated() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onTabClickListener() {
        if(mAdapter.getData().size() == 0)
            mPresenter.loadInitData(true);
    }

    @Override
    protected ListBaseAdapter<ModelWeibo> getListAdapter() {
        return new AdapterWeiboAll(getActivity(), this, mListView);
    }
}
