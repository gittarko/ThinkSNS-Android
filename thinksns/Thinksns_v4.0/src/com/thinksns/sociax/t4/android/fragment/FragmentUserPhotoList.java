package com.thinksns.sociax.t4.android.fragment;

import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterUserImageGridView;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.t4.model.ModelUserPhoto;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * 类说明：
 *
 * @author wz
 * @version 1.0
 * @date 2014-11-24
 */
public class FragmentUserPhotoList extends FragmentSociax implements PullToRefreshBase.OnRefreshListener<GridView>{
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView gv_imgs;
    private EmptyLayout empty_layout;
    private AdapterUserImageGridView adapter;

    private List<ModelUserPhoto> list;

    @Override
    public void initView() {
        mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
        mPullRefreshGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPullRefreshGridView.setOnRefreshListener(this);
        gv_imgs = mPullRefreshGridView.getRefreshableView();
        gv_imgs.setNumColumns(4);
        empty_layout = (EmptyLayout)findViewById(R.id.empty_layout);
    }

    @Override
    public void initIntentData() {
    }

    @Override
    public void initListener() {
    }

    private void loadImages() {
        try {
            //默认一次加载50条内容
            new Api.Users().getUserPhoto(uid, adapter.getMaxId(), 50, new ApiHttpClient.HttpResponseListener() {
                @Override
                public void onSuccess(Object result) {
                    List<ModelUserPhoto> result1 = (List<ModelUserPhoto>) result;
                    if(result1 == null)
                        result1 = new ArrayList<ModelUserPhoto>();
                    if (result1.size() + adapter.getCount() == 0) {
                        //显示空白缺省图
                        empty_layout.setVisibility(View.VISIBLE);
//                        empty_layout.setErrorImag(R.drawable.ic_no_nr);
                        empty_layout.setNoDataContent(getResources().getString(R.string.empty_content));
                        mPullRefreshGridView.setVisibility(View.GONE);
                    }else if(result1.size() == 0){
                        //没有更多内容了
                        Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_SHORT).show();
                    }else {
                        adapter.addData(result1);
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
        }
    }

    @Override
    public void initData() {
        list = new ArrayList<ModelUserPhoto>();
        adapter = new AdapterUserImageGridView(getActivity(), list);
        gv_imgs.setAdapter(adapter);

        empty_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
        loadImages();

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_images;
    }

    @Override
    public void onRefresh(PullToRefreshBase<GridView> refreshView) {
        loadImages();
    }
}
