package com.thinksns.sociax.t4.android.fragment;

import android.content.Context;
import android.os.Bundle;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.interfaces.WeiboListViewClickListener;
import com.thinksns.sociax.t4.android.presenter.WeiboListListPresenter;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

/**
 * Created by hedong on 16/2/29.
 * 用户收藏的微博列表
 */
public class FragmentWeiboCollectList extends FragmentWeiboListViewAll {
    int uid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            uid = getArguments().getInt("uid", 0);
        }

        if(uid == 0)
            uid = Thinksns.getMy().getUid();

    }

    @Override
    protected String getCacheKey() {
        return "weibo_collect";
    }

    @Override
    protected void initPresenter() {
        mPresenter = new WeiboCollectPresenter(getActivity(), this, this);
        mPresenter.setCacheKey(getCacheKey());
    }

    private class WeiboCollectPresenter extends WeiboListListPresenter {

        public WeiboCollectPresenter(Context context, IBaseListView<ModelWeibo> baseListView, WeiboListViewClickListener listViewClickListener) {
            super(context, baseListView, listViewClickListener);
        }

        @Override
        public void loadNetData() {
            new Api.WeiboApi().collectWeibo(uid, getPageSize(), getMaxId(), mHandler);
        }
    }

    @Override
    public void onLoadDataSuccess(ListData<ModelWeibo> data) {
//        mEmptyLayout.setErrorImag(R.drawable.ic_no_sc);
        mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_collection));
        super.onLoadDataSuccess(data);
    }
}
