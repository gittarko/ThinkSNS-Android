package com.thinksns.sociax.t4.android.fragment;

import android.widget.GridView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterRecommendFriend;

import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.util.List;

public class FragmentRecommendFriend extends FragmentSociax {

    private GridView gv_rcd_friend;
    private AdapterRecommendFriend adapter;
    private LoadingView loading;

    private static final int DEFAULT_RCD_FRIEND_COUNT = 20;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recommend_friend;
    }

    @Override
    public void initView() {
        gv_rcd_friend = (GridView) findViewById(R.id.gv_rcd_friend);
        list = new ListData<>();
        adapter = new AdapterRecommendFriend(this, list, 0, DEFAULT_RCD_FRIEND_COUNT);
        gv_rcd_friend.setAdapter(adapter);
        loading = (LoadingView)findViewById(LoadingView.ID);
    }

    public List<SociaxItem> getData() {
        return list;
    }

    @Override
    public void initIntentData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        loading.show(gv_rcd_friend);
        adapter.loadInitData();
    }

    public void loadDataDone() {
        loading.hide(gv_rcd_friend);
    }
}
