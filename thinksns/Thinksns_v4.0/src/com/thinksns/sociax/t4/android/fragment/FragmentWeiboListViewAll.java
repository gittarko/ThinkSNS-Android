package com.thinksns.sociax.t4.android.fragment;

import android.content.IntentFilter;
import android.os.Bundle;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterWeiboAll;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hedong on 16/2/19.
 * 首页-全部微博
 */
public class FragmentWeiboListViewAll extends FragmentWeiboListViewNew{

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
    protected ListBaseAdapter<ModelWeibo> getListAdapter() {
        return new AdapterWeiboAll(getActivity(), this, mListView);
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter filter_update_weibo = new IntentFilter();
        filter_update_weibo.addAction(StaticInApp.NOTIFY_WEIBO);
        filter_update_weibo.addAction(StaticInApp.NOTIFY_CREATE_WEIBO);
        filter_update_weibo.addAction(StaticInApp.UPDATE_SINGLE_WEIBO);
        filter_update_weibo.addAction(StaticInApp.NOTIFY_FOLLOW_USER);
        return filter_update_weibo;
    }

    @Override
    protected void initListViewAttrs() {
        super.initListViewAttrs();
        mListView.setDividerHeight(UnitSociax.dip2px(getActivity(), 0.5f));
        mListView.setSelector(R.drawable.list_selector);
    }

    @Override
    protected String getCacheKey() {
        return "all_weibo";
    }

    @Override
    public void onTabClickListener() {

    }
}
