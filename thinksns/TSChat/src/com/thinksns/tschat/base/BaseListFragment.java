package com.thinksns.tschat.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;
import com.thinksns.tschat.R;
import com.thinksns.tschat.api.RequestResponseHandler;
import com.thinksns.tschat.bean.Entity;
import com.thinksns.tschat.bean.ListEntity;
import com.thinksns.tschat.cache.CacheManager;
import com.thinksns.tschat.chat.TSChatManager;
import com.thinksns.tschat.unit.TDevice;

import org.apache.http.Header;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public abstract class BaseListFragment<T extends Entity> extends BaseFragment
        implements OnItemClickListener, AdapterView.OnItemLongClickListener, OnScrollListener,
        PullToRefreshBase.OnRefreshListener2<ListView>{

    protected PullToRefreshListView pullToRefreshListView;
    protected ListView mListView;
    protected ListBaseAdapter<T> mAdapter;

//    protected int mStoreEmptyState = -1;

    public int mCurrentPage = 0;
    protected boolean readCache = false;
    protected boolean loadFinish = false;

    private AsyncTask<String, Void, ListEntity<T>> mCacheTask;
    private ParserTask mParserTask;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_listview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(View view) {
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setOnRefreshListener(this);
        mListView = pullToRefreshListView.getRefreshableView();
        initListViewAttrs();

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnScrollListener(this);

        if(getListHeaderView() != null) {
            mListView.addHeaderView(getListHeaderView());
        }

        if(getListFooterView() != null) {
            mListView.addFooterView(getListFooterView());
        }

        if (mAdapter != null) {
            mAdapter.setListView(mListView);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter = getListAdapter();
            mAdapter.setListView(mListView);
            mListView.setAdapter(mAdapter);
            if (requestDataIfViewCreated()) {
                //加载即获取网络数据
                mState = STATE_NONE;
                requestData(false);
            } else {

            }

        }
//        if (mStoreEmptyState != -1) {
//        }
    }

    protected void initListViewAttrs() {
        mListView.setDivider(new ColorDrawable(0x72cccccc));
        mListView.setDividerHeight(UnitSociax.dip2px(getActivity(), 0.2f));
    }

    /**
     * 获取列表头部视图
     * @return
     */
    protected abstract  View getListHeaderView();

    protected abstract View getListFooterView();

    public View getDefaultView() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        cancelReadCacheTask();
        cancelParserTask();
        super.onDestroy();
    }

    public abstract ListBaseAdapter<T> getListAdapter();

    //自动刷新
    public void onRefresh() {
        if (mState == STATE_REFRESH) {
            return;
        }
        // 设置顶部正在刷新
        mListView.setSelection(0);
        setSwipeRefreshLoadingState();
        mCurrentPage = 0;
        mState = STATE_REFRESH;
        requestData(true);
    }


    /**
     * 下拉刷新
     * @param refreshView
     */
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    /**
     * 上拉加载更多
     * @param refreshView
     */
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    protected boolean requestDataIfViewCreated() {
        return true;
    }

    protected String getCacheKeyPrefix() {
        return null;
    }

    protected ListEntity<T> parseList(Object reponseData) throws Exception {
        return null;
    }

    protected ListEntity<T> readList(Serializable seri) {
        return null;
    }

    /**
     * 从本地刷新缓存数据
     */
    public void refreshCacheData() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {}

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    private String getCacheKey() {
        return new StringBuilder(getCacheKeyPrefix()).append("_")
                .append(mCurrentPage).toString();
    }

    // 是否需要自动刷新
    protected boolean needAutoRefresh() {
        return true;
    }

    /***
     * 获取列表数据
     *
     * @return void
     * @param refresh
     */
    protected void requestData(boolean refresh) {
        String key = getCacheKey();
        if (isReadCacheData(refresh)) {
            readCacheData(key);
        } else {
            // 取新的数据
            sendRequestData();
        }
    }

    /***
     * 判断是否需要读取缓存的数据
     *
     * @return boolean
     * @param refresh
     * @return
     */
    protected boolean isReadCacheData(boolean refresh) {
        String key = getCacheKey();

        if (!TDevice.hasInternet(context)) {
            //暂时没有加入缓存，返回false
            return false;
        }
//        // 第一页若不是主动刷新，缓存存在，优先取缓存的
//        if (CacheManager.isExistDataCache(getActivity(), key) && !refresh
//                && mCurrentPage == 0) {
//            return true;
//        }
//        // 其他页数的，缓存存在以及还没有失效，优先取缓存的
//        if (CacheManager.isExistDataCache(getActivity(), key)
//                && !CacheManager.isCacheDataFailure(getActivity(), key)
//                && mCurrentPage != 0) {
//            return true;
//        }

        return false;
    }

    // 是否到时间去刷新数据了
    private boolean onTimeRefresh() {
//        String lastRefreshTime = AppContext.getLastRefreshTime(getCacheKey());
//        String currTime = StringUtils.getCurTimeStr();
//        long diff = StringUtils.calDateDifferent(lastRefreshTime, currTime);
//        return needAutoRefresh() && diff > getAutoRefreshTime();
        return false;
    }

    /***
     * 自动刷新的时间
     *
     * 默认：自动刷新的时间为半天时间
     * @return long
     * @return
     */
    protected long getAutoRefreshTime() {
        return 12 * 60 * 60;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onTimeRefresh()) {
            onRefresh();
        }
    }

    //请求数据更新
    public void sendRequestData() {}

    //是否完成数据加载
    public void setLoadFinish(boolean finish) {
        mAdapter.setLoadFinish(finish);
        this.loadFinish = finish;
    }

    private void readCacheData(String cacheKey) {
        cancelReadCacheTask();
        mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
    }

    private void cancelReadCacheTask() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }

    private class CacheTask extends AsyncTask<String, Void, ListEntity<T>> {
        private final WeakReference<Context> mContext;

        private CacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected ListEntity<T> doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(mContext.get(),
                    params[0]);
            if (seri == null) {
                return null;
            } else {
                return readList(seri);
            }
        }

        @Override
        protected void onPostExecute(ListEntity<T> list) {
            super.onPostExecute(list);
            if (list != null) {
                executeOnLoadDataSuccess(list.getList());
            } else {
                //没有缓存
                executeOnLoadDataError(null);
            }
            executeOnLoadFinish();
        }
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = seri;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }
    }

    protected RequestResponseHandler mHandler = new RequestResponseHandler() {
        @Override
        public void onSuccess(Object result) {
            if(isAdded()) {
                executeParserTask(result);
            }
        }

        @Override
        public void onFailure(Object errorResult) {
            if(isAdded()) {
                executeOnLoadDataError(null);
            }
        }

        @Override
        public void onSuccess(int i, Header[] headers, byte[] responseBody) {
            if (mCurrentPage == 0 && needAutoRefresh()) {
                //设置完成刷新时间
            }
            if (isAdded()) {
                if (mState == STATE_REFRESH) {
                    //网络刷新完成
                    onRefreshNetworkSuccess();
                }
                //解析网络回调数据
                executeParserTask(new String(responseBody));
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            if (isAdded()) {
                readCacheData(getCacheKey());
            }
        }
    };

    //获取到网络数据
    protected void executeOnLoadDataSuccess(List<T> data) {
        if (data == null) {
            data = new ArrayList<T>();
        }

//        if (mCurrentPage == 0) {
//            mAdapter.clear();
//        }

        if(needOverrideData()) {
            for (int i = 0; i < data.size(); i++) {
                if (compareTo(mAdapter.getData(), data.get(i))) {
                    data.remove(i);
                    i--;
                }
            }
        }

        int adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
        if ((mAdapter.getCount() + data.size()) == 0) {
            //列表与网络均无数据
            adapterState = ListBaseAdapter.STATE_NO_DATA;
        } else if (data.size() == 0
                || (data.size() < getPageSize() && mCurrentPage == 0)) {
            //网络数据没有更多了
            adapterState = ListBaseAdapter.STATE_NO_MORE;
        } else {
            adapterState = ListBaseAdapter.STATE_LOAD_MORE;
        }

        mAdapter.setState(adapterState);
        mAdapter.addData(data);

    }

    /**
     * 是否需要隐藏listview，显示无数据状态
     *
     */
    protected boolean needShowEmptyNoData() {
        return true;
    }

    /**
     * 是否需要过滤重复数据
     * @return
     */
    protected boolean needOverrideData() {
        return true;
    }

    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (enity.getId() == data.get(i).getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected int getPageSize() {
        return 20;
    }

    protected void onRefreshNetworkSuccess() {}

    //解析数据失败
    protected void executeOnLoadDataError(String error) {
        if (mCurrentPage == 0
                && !CacheManager.isExistDataCache(getActivity(), getCacheKey())) {
        } else {
            mAdapter.setState(ListBaseAdapter.STATE_NETWORK_ERROR);
            mAdapter.notifyDataSetChanged();
        }
    }

    // 完成网络获取
    protected void executeOnLoadFinish() {
        setSwipeRefreshLoadedState();
        mState = STATE_NONE;
    }

    /** 设置顶部正在加载的状态 */
    protected void setSwipeRefreshLoadingState() {
        if (pullToRefreshListView != null) {
            pullToRefreshListView.setRefreshing(true);
            // 防止多次重复刷新
            pullToRefreshListView.setEnabled(false);
        }
    }

    /** 设置顶部加载完毕的状态 */
    protected void setSwipeRefreshLoadedState() {
        if (pullToRefreshListView != null) {
            pullToRefreshListView.setEnabled(true);
            pullToRefreshListView.onRefreshComplete();
        }
    }

    //解析数据结果集
    public void executeParserTask(Object data) {
        cancelParserTask();
        mParserTask = new ParserTask(data);
        mParserTask.execute();
    }

    private void cancelParserTask() {
        if (mParserTask != null) {
            mParserTask.cancel(true);
            mParserTask = null;
        }
    }

    //数据解析
    class ParserTask extends AsyncTask<Void, Void, Serializable> {

        private final Object reponseData;
        private boolean parserError;
        private List<T> list;

        public ParserTask(Object data) {
            this.reponseData = data;
        }

        @Override
        protected Serializable doInBackground(Void... params) {
            try {
                ListEntity<T> data = parseList(reponseData);
                list = data.getList();
                return data;
            } catch (Exception e) {
                e.printStackTrace();
                parserError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Serializable result) {
            super.onPostExecute(result);
            if (parserError) {
                readCacheData(getCacheKey());
            } else {
                //加入缓存
                executeOnLoadDataSuccess(list);
                executeOnLoadFinish();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }
        // 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
        if (mState == STATE_LOADMORE || mState == STATE_REFRESH) {
            return;
        }

        // 判断是否滚动到底部
        boolean scrollEnd = false;
        try {
            if(mAdapter.getCount() == view.getLastVisiblePosition() - 2) {
                /**
                 * 减2的原因是listview包含刷新视图和网络错误提示视图
                 */
                scrollEnd = true;
            }
        } catch (Exception e) {
            scrollEnd = false;
        }

        Log.e("BaseListFragment", "scrollEnd:" + scrollEnd + ", lastVisiblePosition:" + view.getLastVisiblePosition());
        if (mState == STATE_NONE && scrollEnd) {
            if (mAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE
                    || mAdapter.getState() == ListBaseAdapter.STATE_NETWORK_ERROR) {
                mCurrentPage++;
                mState = STATE_LOADMORE;
                requestData(false);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    }

}
