package com.thinksns.sociax.t4.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterBasePostList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.presenter.PostListPresenter;
import com.thinksns.sociax.t4.android.weiba.ActivityPostDetail;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;

/**
 * Created by hedong on 16/3/1.
 * 帖子列表基类;帖子列表包含两种，一种是带有头部详情的微吧，一种是无头部的帖子列表
 */
public class BaseFragmentPostList extends BaseListFragment<ModelPost> {
    protected int weibaId;      //微吧ID
    protected int postType;      //帖子类型
    protected View headerView;  //头部信息块

    public static BaseFragmentPostList newInstance(Bundle bundle) {
        BaseFragmentPostList fragmentPostList = new BaseFragmentPostList();
        fragmentPostList.setArguments(bundle);
        return fragmentPostList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            weibaId = getArguments().getInt("weiba_id", 0);
        }
    }

    //自定义实现帖子控制器
    @Override
    protected void initPresenter() {
        mPresenter = new PostListPresenter(getActivity(), this);
        mPresenter.setCacheKey("weiba" + weibaId + "_post_list");
    }

    @Override
    public void initView(View view) {
        super.initView(view);
    }

    //自定义设置列表的样式
    @Override
    protected void initListViewAttrs() {
        super.initListViewAttrs();
        if(haveHeaderView()) {
            headerView = mInflater.inflate(R.layout.header_postlist, null);
            initHeaderView(headerView);
            mListView.addHeaderView(headerView);
        }
    }

    @Override
    protected ListBaseAdapter<ModelPost> getListAdapter() {
        return new AdapterBasePostList(getActivity());
    }

    //是否包含头部信息
    protected boolean haveHeaderView() {
        return false;
    }

    protected void initHeaderView(View view) {

    }

    //获取微吧ID
    public int getWeibaId() {
        return weibaId;
    }

    //获取帖子列表类型
    public Fragment setRequestPostType(int type) {
        this.postType = type;
        return this;
    }

    public int getRequestPostType() {
        return this.postType;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
    }

    @Override
    public void onLoadDataSuccess(ListData<ModelPost> data) {
        if(postType == PostListPresenter.MY_COLLECT_POST) {
            mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_collection));
        }else {
            mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_content));
        }
        super.onLoadDataSuccess(data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ModelPost post = mAdapter.getItem((int)id);
        if (post != null && post.getUser() != null) {
            Bundle data = new Bundle();
            data.putParcelable("post", post);
            ActivityStack.startActivity(getActivity(), ActivityPostDetail.class, data);
        }
    }
}
