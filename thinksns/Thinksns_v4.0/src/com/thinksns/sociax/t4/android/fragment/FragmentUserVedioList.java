package com.thinksns.sociax.t4.android.fragment;

import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterUserImageGridView;
import com.thinksns.sociax.t4.adapter.AdapterUserVideoGridView;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelUserPhoto;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.t4.model.ModelVideo;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * 类说明： 用户视频列表 需要传入int uid
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-24
 */
public class FragmentUserVedioList extends FragmentSociax implements PullToRefreshBase.OnRefreshListener<GridView>{
    private PullToRefreshGridView mPullRefreshGridView;
    private EmptyLayout empty_layout;
    private AdapterUserVideoGridView adapter;

    private List<ModelVideo> list;
    private GridView gv_videos;

    @Override
    public void initView() {
        mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
        mPullRefreshGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPullRefreshGridView.setOnRefreshListener(this);
        gv_videos = mPullRefreshGridView.getRefreshableView();
        gv_videos.setNumColumns(4);
        empty_layout = (EmptyLayout)findViewById(R.id.empty_layout);
    }

    @Override
    public void initIntentData() {
    }

    @Override
    public void initListener() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadVideos() {
        try {
            new Api.Users().getUserVedio(uid, adapter.getMaxId(), 50, new ApiHttpClient.HttpResponseListener() {
                @Override
                public void onSuccess(Object result) {
                    List<ModelVideo> list = (List<ModelVideo>) result;
                    if(list == null)
                        list = new ArrayList<ModelVideo>();
                    if (list.size() + adapter.getCount() == 0) {
                        //显示空白缺省图
                        empty_layout.setVisibility(View.VISIBLE);
//                        empty_layout.setErrorImag(R.drawable.ic_no_nr);
                        empty_layout.setNoDataContent(getResources().getString(R.string.empty_content));
                        mPullRefreshGridView.setVisibility(View.GONE);
                    }else if(list.size() == 0){
                        //没有更多内容了
                        Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_SHORT).show();
                    }else {
                        adapter.addData(list);
                        empty_layout.setVisibility(View.GONE);
                    }

                    mPullRefreshGridView.onRefreshComplete();
                }

                @Override
                public void onError(Object result) {
                    mPullRefreshGridView.onRefreshComplete();
                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
            mPullRefreshGridView.onRefreshComplete();
        }
    }

    @Override
    public void initData() {
        list = new ArrayList<ModelVideo>();
        adapter = new AdapterUserVideoGridView(getActivity(), list);
        gv_videos.setAdapter(adapter);
        empty_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
        loadVideos();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user_pv;
    }

    @Override
    public void onRefresh(PullToRefreshBase<GridView> refreshView) {
        loadVideos();
    }
}
