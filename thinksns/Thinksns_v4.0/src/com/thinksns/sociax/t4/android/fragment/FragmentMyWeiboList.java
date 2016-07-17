package com.thinksns.sociax.t4.android.fragment;

import android.content.Context;
import android.os.Bundle;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.adapter.AdapterWeiboAll;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.interfaces.WeiboListViewClickListener;
import com.thinksns.sociax.t4.android.presenter.WeiboListListPresenter;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

import java.io.Serializable;

/**
 * Created by hedong on 16/2/29.
 * 我的微博
 */
public class FragmentMyWeiboList extends FragmentWeiboListViewNew {
    int uid;

    @Override
    protected ListBaseAdapter<ModelWeibo> getListAdapter() {
        return new AdapterWeiboAll(getActivity(), this, mListView);
    }

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
        return "my_weibo";
    }

    @Override
    protected void initPresenter() {
        mPresenter = new WeiboMyPresenter(getActivity(), this, this);
        mPresenter.setCacheKey(getCacheKey());
    }

    @Override
    protected boolean loadingInPageCenter() {
        return true;
    }

    @Override
    public void onTabClickListener() {

    }

    private class WeiboMyPresenter extends WeiboListListPresenter {

        public WeiboMyPresenter(Context context, IBaseListView<ModelWeibo> baseListView,
                                WeiboListViewClickListener listViewClickListener) {
            super(context, baseListView, listViewClickListener);
        }

        @Override
        public String getCachePrefix() {
            return "weibo_list";
        }

        @Override
        public void loadNetData() {
            new Api.WeiboApi().myWeibo(uid, getPageSize(), getMaxId(), mHandler);
        }
    }

    @Override
    public void onLoadDataSuccess(ListData<ModelWeibo> data) {
        super.onLoadDataSuccess(data);
    }
}
