package com.thinksns.sociax.t4.android.presenter;

import android.content.Context;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.t4.android.interfaces.WeiboListViewClickListener;

import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.base.IBaseListView;

/**
 * Created by hedong on 16/2/20.
 * 推荐微博Presenter
 */
public class WeiboRecommendListPresenter extends WeiboListListPresenter{

    public WeiboRecommendListPresenter(Context context, IBaseListView<ModelWeibo> baseListView, WeiboListViewClickListener listViewClickListener) {
        super(context, baseListView, listViewClickListener);
    }

    @Override
    public void loadNetData() {
        new Api.WeiboApi().recommendTimeline(getPageSize(), getMaxId(), mHandler);
    }

}
