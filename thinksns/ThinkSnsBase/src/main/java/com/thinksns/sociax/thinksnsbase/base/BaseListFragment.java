package com.thinksns.sociax.thinksnsbase.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.thinksnsbase.R;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.fragment.BaseFragment;

import java.util.List;

/**
 * Created by hedong on 16/2/19.
 */
public abstract class BaseListFragment<T extends SociaxItem> extends BaseFragment
            implements PullToRefreshBase.OnRefreshListener, AdapterView.OnItemClickListener,
            AbsListView.OnScrollListener, IBaseListView<T> ,AdapterView.OnItemLongClickListener{

    //下拉刷新组件
    protected PullToRefreshListView pullToRefreshListView;
    protected ListView mListView;
    protected EmptyLayout mEmptyLayout;

    protected ListBaseAdapter<T> mAdapter;
    protected BaseListPresenter<T> mPresenter;

    //当前数据加载状态
    protected int mEmptyState = -1;

    /**
     * 重写基类布局资源ID，每个列表Fragment可灵活拥有属于自己的样式
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_listview;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPresenter();
        initView(view);
        initReceiver();
        initListener();
        initData();
    }

    protected void initReceiver() {

    }

    protected void unregisterReceiver() {

    }

    protected void initPresenter() {
    }

    protected void initListener() {
    }

    @Override
    public void initView(View view) {
        //初始化列表基类的UI控件
        pullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.pull_refresh_list);
        setRefreshMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullToRefreshListView.setOnRefreshListener(this);

        mListView = pullToRefreshListView.getRefreshableView();
        initListViewAttrs();
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnScrollListener(this);

        mEmptyLayout = (EmptyLayout)view.findViewById(R.id.error_layout);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setCurrentPage(0);
                mState = STATE_REFRESH;
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                mPresenter.requestData(true);
            }
        });

        if(mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }else {
            mAdapter = getListAdapter();
            mListView.setAdapter(mAdapter);
            if(requestDataIfViewCreated()) {
                //正在请求数据
                if(loadingInPageCenter()) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mState = STATE_NONE;
                    mPresenter.requestData(true);
                }else {
                    setRefreshing(true);
                }
            }else {
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        }

        if(mEmptyState != -1) {
            mEmptyLayout.setErrorType(mEmptyState);
        }
    }

    /**
     * 加载样式分两种方式，一种是顶部的下拉刷新，
     * 一种是首次进入页面中部显示的加载图标
     * @return
     */
    protected boolean loadingInPageCenter() {
        return false;
    }

    @Override
    public void onDestroy() {
        mPresenter.cancelRequest();
        unregisterReceiver();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        mEmptyState = mEmptyLayout.getErrorState();
        super.onDestroyView();
    }

    //设置刷新模式（默认是支持下拉和上拉刷新）
    protected void setRefreshMode(PullToRefreshBase.Mode mode) {
        pullToRefreshListView.setMode(mode);
    }

    /**
     * 初始化列表的属性,例如设置分割线颜色，高度，单击后效果等
     */
    protected void initListViewAttrs() {
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.bg_ios)));
        mListView.setDividerHeight(2);
    }

    protected abstract ListBaseAdapter<T> getListAdapter();


    /**
     * 是否在UI创建完成之后开始请求网络数据(默认true)
     * @return
     */
    protected boolean requestDataIfViewCreated() {
        return true;
    }

    @Override
    public void setRefreshing(boolean refresh) {
        if(pullToRefreshListView != null)
            pullToRefreshListView.setRefreshing(true);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if(mState == STATE_REFRESH) {
            return;
        }
        mListView.setSelection(0);
        setRefreshLoadingState();
        mPresenter.setMaxId(0);
        mPresenter.setCurrentPage(0);
        mState = STATE_REFRESH;
        //主动刷新，从服务器获取最新数据
        mPresenter.requestData(true);
    }


    @Override
    public void setRefreshLoadingState() {
        if (pullToRefreshListView != null) {
            // 防止多次重复刷新
            pullToRefreshListView.setEnabled(false);
        }
    }

    @Override
    public void setRefreshLoadedState() {
        if (pullToRefreshListView != null &&
                mPresenter.refreshState == BaseListPresenter.NETWORK_DONE) {
            //网络请求已经完成
            pullToRefreshListView.setEnabled(true);
            pullToRefreshListView.onRefreshComplete();
        }
    }

    @Override
    public void onLoadComplete() {
        setRefreshLoadedState();
        mState = STATE_NONE;
    }

    @Override
    public void onLoadDataSuccess(ListData<T> data) {
        if (data == null) {
            data = new ListData<T>();
        }

        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

        if (mPresenter.getMaxId() == 0) {
            //如果是请求第一页数据或者请求最新数据则清空列表
            //如果主动刷新了会更新本地缓存
            mAdapter.clear();
        }else {
            //如果是加载更多，当从主动从服务器获取到最新数据后要更新本地缓存
        }

        //默认是空的
        int adapterState = ListBaseAdapter.STATE_LOAD_MORE;
        if(mPresenter.refreshState == BaseListPresenter.NETWORK_DONE) {
            //如果刷新完毕或没有主动刷新服务端内容
            if ((mAdapter.getCount() + data.size()) == 0) {
                adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
            } else if (data.size() < mPresenter.getPageSize()
                    && mPresenter.getMaxId() == 0) {
                //加载列表第一页，请求数少于一页
                adapterState = ListBaseAdapter.STATE_LESS_ONE_PAGE;
                //设置刷新模式只允许下拉刷新
                pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }else if(data.size() < mPresenter.getPageSize()
                    && mPresenter.getMaxId() > 0) {
                //加载更多页出现没有更多内容
                adapterState = ListBaseAdapter.STATE_NO_MORE;
            }else {
                //data.size >= 20
            }
            filterDuplicate(data, false);
        }else {
            filterDuplicate(data, true);
        }

        mAdapter.setState(adapterState);
        mAdapter.addData(data);

        // 判断等于是因为最后有一项是listview的状态
        if (mAdapter.getCount() == 1
                && adapterState == ListBaseAdapter.STATE_LOAD_MORE) {
            //列表数只有1个与状态表示有更多互斥,说明此列表内容为空
            if (needShowEmptyNoData()) {
                mEmptyLayout.setErrorType(EmptyLayout.NODATA);
            }else {
                mAdapter.setState(ListBaseAdapter.STATE_EMPTY_ITEM);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    /**
     * 过滤列表中重复的内容或更新列表某一项
     * @param data   待过滤的的内容
     * @param is_cache 是否是缓存内容
     */
    private void filterDuplicate(ListData<T> data, boolean is_cache) {
        for (int i = 0; i < data.size(); i++) {
            int pos;
            T obj = (T)data.get(i);
            if ((pos = mAdapter.getItemForPosition(obj)) != -1) {
                //列表已存在条目
                if(!is_cache) {
                    //如果是主动刷新，从网络获取到数据,则更新本地数据
                    mAdapter.setItem(pos, obj);
                }
                data.remove(i);
                i--;
            }
        }
    }

    @Override
    public void onLoadDataError(String error) {
        if (error != null) {
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        } else {
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            if(mPresenter.refreshState == BaseListPresenter.NETWORK_DONE) {
                mAdapter.setState(ListBaseAdapter.STATE_NETWORK_ERROR);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestNetworkSuccess() {
        if(isAdded()) {

        }
    }

    @Override
    public boolean isFragmentAdded() {
        return isAdded();
    }

    /**
     * 是否需要隐藏listview，显示无数据状态
     *
     */
    protected boolean needShowEmptyNoData() {
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, final long id) {
        return false;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }
        // 数据已经全部加载，或数据为空时，或正在加载网 或正在进行网络请求,不处理滚动事件
        if (mState == STATE_LOADMORE || mState == STATE_REFRESH ||
                mPresenter.refreshState == BaseListPresenter.NETWORK_LOADING) {
            return;
        }
        // 判断是否滚动到底部
        boolean scrollEnd = false;
        try {

            int lastPos = view.getLastVisiblePosition() - mListView.getHeaderViewsCount(); //包含刷新组件的头部视图
            int footerIndex = -1;
            if(mAdapter.getFooterView() != null) {
                footerIndex = mAdapter.getCount();
                if (footerIndex == lastPos) {
                    //lastPos - 1是因为列表头部包含下拉刷新视图
                    scrollEnd = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            scrollEnd = false;
        }

        if (mState == STATE_NONE && scrollEnd) {
            if (mAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE
                    || mAdapter.getState() == ListBaseAdapter.STATE_NETWORK_ERROR) {
                mPresenter.setMaxId(mAdapter.getMaxId());
                mState = STATE_LOADMORE;
                //主动刷新从服务器加载更多数据
                mPresenter.requestData(true);
                mAdapter.setFooterViewLoading();
            }
        }
    }
}
