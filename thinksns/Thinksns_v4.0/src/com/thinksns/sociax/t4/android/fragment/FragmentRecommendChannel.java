package com.thinksns.sociax.t4.android.fragment;

import android.widget.GridView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterChannelRecList;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import java.util.List;

public class FragmentRecommendChannel extends FragmentSociax {

    private GridView gv_rcd_friend;
    private AdapterChannelRecList adapter;
    private LoadingView loading;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recommend_friend;
    }

    @Override
    public void initView() {
        gv_rcd_friend = (GridView) findViewById(R.id.gv_rcd_friend);
        loading = (LoadingView)findViewById(LoadingView.ID);
        list = new ListData<>();
        adapter = new AdapterChannelRecList(this, list);
        gv_rcd_friend.setAdapter(adapter);
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
