package com.thinksns.sociax.t4.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.ApiUsers;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.modle.Posts;
import com.thinksns.sociax.t4.android.Listener.ListenerRefreshComplete;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.video.VideoWithPlayButtonView;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类说明：
 *
 * @author wz
 * @version 1.0
 * @date 2014-10-15
 */
public abstract class AdapterSociaxList extends BaseAdapter {
    protected static final String TAG = "AdapterSociaxList";

    /**
     * 获取分页最后的id; 需要注意getLast return null的情况
     */
    public abstract int getMaxid();

    protected ListData<SociaxItem> list;            // 当前的列表，后期要修改成SociaxData对应的列表
    protected HolderSociax holder;
    protected List<VideoWithPlayButtonView> videoList;
    protected List<ImageView> videoList2;
    protected FragmentSociax fragment;
    protected ThinksnsAbscractActivity context;
    protected ListView mListView;
    protected LayoutInflater inflater;
    public static final int LIST_FIRST_POSITION = 0;
    protected static View refresh;
    protected static Worker thread;
    protected ActivityHandler handler;
    protected ResultHandler resultHander;
    protected static String Type;
    public static final int REFRESH_HEADER = 0;// 头部刷新
    public static final int REFRESH_FOOTER = 1;// 脚部刷新
    public static final int REFRESH_NEW = 2;// 第一次刷新
    public static final int SEARCH_NEW = 3;// 第一次搜索
    public static final int UPDATA_LIST = 4;// 更新列表
    public static final int UPDATA_LIST_ID = 5;
    public static final int UPDATA_LIST_TYPE = 6;
    public static final int SEARCH_NEW_BY_ID = 7;
    public static final int FAV_STATE = 8;
    public static final int NOTIFY_ADAPTER = 11;
    public static final int PAGE_COUNT = 20;// 每次refresh更新的数目
    public static final int SUCCESS = 9;// 更新成功
    public static final int ERROR = 10;// 更新失败
    protected int refreshState = REFRESH_NEW;    //当前刷新状态
    //列表状态
    public static final int NO_NEW_DATA = 12;   //没有最新
    public static final int NET_ERROR = 13;    //网络错误
    public static final int NO_MORE_DATA = 14; //没有更多
    public static final int LOAD_MORE = 15;     //加载更多
    public static final int LESS_ONE_PAGE = 16;
    public static final int STATE_IDLE = 17;
    public static final int STATE_LOADING = 18; //正在加载
    protected int adapterState = STATE_IDLE;
    protected static LoadingView loadingView;
    public boolean hasRefreshFootData;
    public boolean isHideFootToast = false;
    public int lastNum;
    public String isRefreshActivity;
    public ImageView animView;
    public boolean isShowFooter = false;// 是否需要显示更多，一般用于一个listview多个adpater轮流切换时候判断，例如ListUserInfo

    public static ListData<SociaxItem> cache;
    public ListenerRefreshComplete completeListener;

    //适配器的状态
    public int getAdapterState() {
        return adapterState;
    }

    public int getRefreshState() {
        return refreshState;
    }

    public void setAdapterState(int adapterState) {
        this.adapterState = adapterState;
    }

    //设置adapter状态
    public void setState(int state) {
        this.adapterState = state;
    }

    public boolean haveMore() {
        return !(adapterState == NO_MORE_DATA || adapterState == LESS_ONE_PAGE);
    }

    //网络请求回调
    protected ApiHttpClient.HttpResponseListener httpListener = null;

    protected void initHttpResponseListener() {
        httpListener = new ApiHttpClient.HttpResponseListener() {

            @Override
            public void onSuccess(Object result) {
                Message mainMsg = new Message();
                mainMsg.what = SUCCESS;
                mainMsg.obj = result;
                mainMsg.arg1 = refreshState;
                resultHander.sendMessage(mainMsg);
            }

            @Override
            public void onError(Object result) {
                Message mainMsg = Message.obtain();
                mainMsg.what = ERROR;
                mainMsg.obj = "连接超时，请稍后重试";
                resultHander.sendMessage(mainMsg);
            }
        };
    }

    public void setOnCompleteListener(ListenerRefreshComplete listener) {
        this.completeListener = listener;
    }


    /**
     * 通过Activity生成的列表调用这个类
     */
    public AdapterSociaxList(ThinksnsAbscractActivity context,
                             ListData<SociaxItem> list) {
        if(list == null)
            list = new ListData<SociaxItem>();
        else
            this.list = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        try {
            refresh = this.context.getCustomTitle().getRight();
        } catch (Exception e) {
            Log.d(TAG,
                    "sociaxlistadapter construct method get rigth res of custom title error "
                            + e.toString());
        }

        AdapterSociaxList.thread = new Worker(
                (Thinksns) context.getApplicationContext(), Type + " Refresh");
        handler = new ActivityHandler(thread.getLooper(), context);
        resultHander = new ResultHandler();
        initHttpResponseListener();

        videoList = new ArrayList<VideoWithPlayButtonView>();
        videoMap = new HashMap<String, VideoWithPlayButtonView>();

    }

    /**
     * 通过Fragment类生成的列表调用这个类
     */
    public AdapterSociaxList(FragmentSociax fragment, ListData<SociaxItem> list) {
        if(list == null)
            this.list = new ListData<SociaxItem>();
        else
            this.list = list;

        this.fragment = fragment;
        this.context = (ThinksnsAbscractActivity) fragment.getActivity();
        this.inflater = LayoutInflater.from(context);
        try {
            refresh = this.context.getCustomTitle().getRight();
        } catch (Exception e) {
            Log.d(TAG, "sociaxlistadapter construct method get rigth res of custom title error "
                    + e.toString());
        }
        AdapterSociaxList.thread = new Worker(
                (Thinksns) context.getApplicationContext(), Type + " Refresh");
        handler = new ActivityHandler(thread.getLooper(), context);
        resultHander = new ResultHandler();
        initHttpResponseListener();

        videoList = new ArrayList<VideoWithPlayButtonView>();
        videoMap = new HashMap<String, VideoWithPlayButtonView>();

    }

    /**
     * List列表头部刷新调用的接口
     */
    public ListData<SociaxItem> refreshHeader(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException, ApiException {
        return null;
    }

    ;

    public ListData<SociaxItem> refreshHeader(SociaxItem obj, HttpResponseListener listener) {
        return null;
    }

    /**
     * List列表更多刷新调用的接口
     */
    public ListData<SociaxItem> refreshFooter(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return null;
    }

    ;

    /**
     * List列表刷新调用的接口
     */
    public ListData<SociaxItem> refreshNew(int count)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return null;
    }

    /**
     * 执行头部刷新
     */
    public void doRefreshHeader() {
        // 首先判断网络状态
        if (!UnitSociax.isNetWorkON(context)) {
            Toast.makeText(context, context.getResources().getText(R.string.net_fail), Toast.LENGTH_SHORT).show();
            hiddenPromptView();
            adapterState = NET_ERROR;
            if(getEmptyLayout() != null){
                getEmptyLayout().setErrorType(EmptyLayout.NETWORK_ERROR);
            }
            return;
        }
        // 这部分内容应该可以删除 wz 9.30
        AdapterSociaxList.thread = new Worker(
                (Thinksns) context.getApplicationContext(), Type + " Refresh");
        handler = new ActivityHandler(thread.getLooper(), context);
        resultHander = new ResultHandler();

        // 得到头部右部分
        if (refresh != null) {
            refresh.clearAnimation();
            // 设置头部右边刷新
            if (null != isRefreshActivity
                    && isRefreshActivity.equals("ThinksnsMyWeibo"))
                Anim.refresh(
                        context,
                        refresh,
                        com.thinksns.sociax.android.R.drawable.spinner_black);
            if (null != isRefreshActivity
                    && isRefreshActivity.equals("ThinksnsTopicActivity"))
                Anim.refresh(
                        context,
                        refresh,
                        com.thinksns.sociax.android.R.drawable.spinner_black);
            if (null != isRefreshActivity
                    && isRefreshActivity.equals("ThinksnsSiteList"))
                Anim.refresh(
                        context,
                        refresh,
                        com.thinksns.sociax.android.R.drawable.spinner_black);
            refresh.setClickable(false);
        }

        if (getListView() != null) {
            getListView().headerRefresh();
            getListView().headerShow();
        }

        if (httpListener == null)
            initHttpResponseListener();
        if (this.getFirst() == null) {
            // 如果第一条信息为空，则调用新刷新
            refreshState = REFRESH_HEADER;
            try {
                refreshNew(PAGE_COUNT);
            } catch (VerifyErrorException e) {
                e.printStackTrace();
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (ListAreEmptyException e) {
                e.printStackTrace();
            } catch (DataInvalidException e) {
                e.printStackTrace();
            }
        } else {
            // 否则获取第一条微博，刷新头部的时候需要用到第一条微博的id作为分界线
            refreshState = REFRESH_HEADER;
            try {
                refreshHeader(getFirst());
            } catch (VerifyErrorException e) {
                e.printStackTrace();
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (ListAreEmptyException e) {
                e.printStackTrace();
            } catch (DataInvalidException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "doRefreshHeader .....");
//		handler.sendMessage(msg);
    }

    //头部添加内容
    public void addHeader(ListData<SociaxItem> list) {
        if(list == null)
            list = new ListData<SociaxItem>();

        if (list.size() > 0) {
            //过滤重复信息
            for(int i=0; i<list.size(); i++) {
                if(this.list.contains(list.get(i))) {
                    list.remove(i);
                    i--;
                }
            }

            this.list.addAll(0, list);
            this.notifyDataSetChanged();

        } else {
            if (!isHideFootToast)
                Toast.makeText(context, com.thinksns.sociax.android.R.string.refresh_error,Toast.LENGTH_SHORT).show();
            adapterState = NO_NEW_DATA;
        }

        if (fragment != null) {
            fragment.executeDataSuccess(list);
        }

        if (context != null)
            context.executeDataSuccess(list);

    }

    public ListData<SociaxItem> searchNew(String key) throws ApiException {
        return null;
    }

    public ListData<SociaxItem> searchNew(int key) throws ApiException {
        return null;
    }

    public ListData<SociaxItem> refreshNew(int count, String key)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return null;
    }

    public ListData<SociaxItem> refreshNew(SociaxItem obj)
            throws VerifyErrorException, ApiException, ListAreEmptyException,
            DataInvalidException {
        return null;
    }

    public void refreshNew(SociaxItem obj, ApiHttpClient.HttpResponseListener listener) {
    }

    public Object refresState(int key) throws ApiException {
        return null;
    }

    public Activity getContext() {
        return this.context;
    }

    public SociaxItem getFirst() {
        return (list == null) || list.size()==0 ? null : this.list.get(LIST_FIRST_POSITION);
    }

    public SociaxItem getLast() {
        if (list != null && list.size() > 0) {
            return this.list.get(this.list.size() - 1);
        } else
            return null;
    }

    public int getCount() {
        return this.list == null ? 0 : this.list.size();
    }

    //获取数据集合大小
    public int getDataSize() {
        return list == null ? 0 : list.size();
    }

    public SociaxItem getItem(int position) {
        return this.list.get(position);
    }

    /**
     * 删除Item
     */
    public void deleteItem(int position) {

        if (list.size() > 0)
            this.list.remove(position);
        this.notifyDataSetChanged();
    }

    /**
     * 底部追加信息
     */
    public void addFooter(ListData<SociaxItem> list) {
        if(list == null)
            list = new ListData<SociaxItem>();


        if (fragment != null) {
            fragment.executeDataSuccess(list);
        }else if (context != null)
            context.executeDataSuccess(list);

        if (list.size() == 0) {
            adapterState = NO_MORE_DATA;
            if(getFirst() == null) {
                //如果是第一页刷新
                if(getEmptyLayout() != null) {
                    getEmptyLayout().setErrorType(EmptyLayout.NODATA);
                }
            }
        } else {
            if(getEmptyLayout() != null) {
                getEmptyLayout().setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
            if (list.size() < PAGE_COUNT) {
                adapterState = LESS_ONE_PAGE;
            } else {
                adapterState = LOAD_MORE;
            }

            hasRefreshFootData = true;

            //对返回的内容去重
            for(int i=0; i<list.size(); i++) {
                if(this.list.contains(list.get(i))) {
                    list.remove(i);
                    i--;
                }
            }
            //底部追加内容
            this.list.addAll(list);
            lastNum = this.list.size();
        }

        // 数据修改好之后更新列表
        this.notifyDataSetChanged();
        hiddenPromptView();


    }

    /**
     * 显示默认视图
     */
    protected void showDefaultView(boolean isShow) {
        if (getDefaultView() != null) {
            if (isShow) {
                getDefaultView().setVisibility(View.VISIBLE);
            } else {
                getDefaultView().setVisibility(View.GONE);
            }
        }
    }

    public void changeListData(ListData<SociaxItem> list) {
        this.list.clear();
        if (null != list) {
            if (list.size() > 0) {
                hasRefreshFootData = true;
                this.list.clear();
                this.list.addAll(list);
                lastNum = this.list.size();
                this.notifyDataSetChanged();
            }
        }

        if (list == null || list.size() == 0 || list.size() < 20) {
            if (getListView() != null)
                getListView().hideFooterView();
        }

        if (this.list.size() == 0 && !isHideFootToast) {
            Toast.makeText(context,
                    com.thinksns.sociax.android.R.string.refresh_error,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void changeListDataNew(ListData<SociaxItem> list) {

        if (null != list) {
            if (list.size() > 0) {
                hasRefreshFootData = true;
                this.list = list;
                lastNum = this.list.size();
                this.notifyDataSetChanged();

            } else {
                this.list.clear();
                this.notifyDataSetChanged();
            }
        }
        if (list == null || list.size() == 0 || list.size() < 20) {
            if (getListView() != null)
                getListView().hideFooterView();
        }

        if (this.list.size() == 0 && !isHideFootToast) {
            Toast.makeText(context,
                    com.thinksns.sociax.android.R.string.refresh_error,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public long getItemId(int position) {
        return position;
    }

    public void doRefreshFooter() {

        AdapterSociaxList.thread = new Worker(
                (Thinksns) context.getApplicationContext(), Type + " Refresh");
        handler = new ActivityHandler(thread.getLooper(), context);
        resultHander = new ResultHandler();
        if (getListView() != null)
            getListView().footerShow();
        if (refresh != null) {
            refresh.setClickable(false);
        }
        if (this.list == null || this.list.size() == 0) {
            return;
        }
        try {
            refreshState = REFRESH_FOOTER;
            refreshFooter(getLast());
        } catch (VerifyErrorException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ListAreEmptyException e) {
            e.printStackTrace();
        } catch (DataInvalidException e) {
            e.printStackTrace();
        }
    }

    public void cacheHeaderPageCount() {

        cache = new ListData<SociaxItem>();
//		ListData<SociaxItem> cache = new ListData<SociaxItem>();
        if (this.list != null) {
            for (int i = 0; i < PAGE_COUNT; i++) {
//			cache.add(0, this.list.get(i));
                cache.add(this.list.get(i));
            }
            Thinksns.setLastWeiboList(cache);
        }
    }

    /**
     * 执行更新列表
     */
    public void refreshNewSociaxList() {
        if (refresh != null) {
            if (null != isRefreshActivity
                    && isRefreshActivity.equals("ThinksnsMyWeibo"))
                Anim.refresh(context, refresh, R.drawable.spinner_black);
            refresh.setClickable(false);
        }

        if (httpListener == null)
            initHttpResponseListener();
        refreshState = REFRESH_NEW;
        try {
            //开始加载网络数据
            if(getEmptyLayout() != null) {
                getEmptyLayout().setErrorType(EmptyLayout.NETWORK_LOADING);
            }
            refreshNew(PAGE_COUNT);
        } catch (VerifyErrorException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ListAreEmptyException e) {
            e.printStackTrace();
        } catch (DataInvalidException e) {
            e.printStackTrace();
        }
    }

    public void doUpdataList() {
        Message msg = handler.obtainMessage();
        msg.what = UPDATA_LIST;
        handler.sendMessage(msg);
    }

    public void doUpdataList(String type) {
        if (type.equals("taskCate")) {
            if (loadingView != null)
                if (getListView() != null)
                    loadingView.show((View) getListView());
            if (context.getOtherView() != null) {
                loadingView.show(context.getOtherView());
            }
        }
        Message msg = handler.obtainMessage();
        msg.what = UPDATA_LIST;
        handler.sendMessage(msg);
    }

    private LoadingView getLoadingView() {
        return loadingView;
    }

    public void doUpdataListById() {

        Message msg = handler.obtainMessage();
        msg.what = UPDATA_LIST_ID;
        handler.sendMessage(msg);
    }

    public void doUpdataListByType(SociaxItem sociaxItem) {

        if (loadingView != null)
            if (getListView() != null)
                loadingView.show((View) getListView());
        if (context.getOtherView() != null) {
            loadingView.show(context.getOtherView());
        }

        Message msg = handler.obtainMessage();
        msg.obj = sociaxItem;
        msg.what = UPDATA_LIST_ID;
        handler.sendMessage(msg);
    }

    public void doSearchNew(String key) {

        Message msg = handler.obtainMessage();
        msg.what = SEARCH_NEW;
        msg.obj = key;
        handler.sendMessage(msg);
    }

    public void doSearchNewById(int key) {

        Message msg = handler.obtainMessage();
        msg.what = SEARCH_NEW_BY_ID;
        msg.arg1 = key;
        handler.sendMessage(msg);
    }

    public void updateState(int key) {

        Message msg = handler.obtainMessage();
        msg.what = FAV_STATE;
        msg.arg1 = key;
        handler.sendMessage(msg);
    }

    class ActivityHandler extends Handler {
        private Context context = null;

        public ActivityHandler(Looper looper, Context context) {
            super(looper);
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ListData<SociaxItem> newData = null;
            Message mainMsg = new Message();
            mainMsg.what = ERROR;
            Log.d(TAG, "mainMsg.what=" + mainMsg.what);
            try {
                switch (msg.what) {
                    case REFRESH_HEADER:
                        newData = refreshHeader((SociaxItem) msg.obj);
                        if (newData != null) {
                            Log.v("SociaxListAdapter", newData.toString() + "XXXX");
                        }
                        Log.d(TAG, "refresh header ....");
                        break;
                    case REFRESH_FOOTER:
                        newData = refreshFooter((SociaxItem) msg.obj);
                        Log.d(TAG, "refresh footer ....");
                        break;
                    case REFRESH_NEW:
                        Log.d(TAG, "refresh new  ....");
                        newData = refreshNew(PAGE_COUNT);
                        break;
                    case SEARCH_NEW:
                        Log.d(TAG, "seache new  ....");
                        newData = searchNew((String) msg.obj);
                        break;
                    case SEARCH_NEW_BY_ID:
                        Log.d(TAG, "seache new  ....");
                        newData = searchNew(msg.arg1);
                        break;
                    case UPDATA_LIST:
                        Log.d(TAG, "updata list  ....");
                        newData = refreshNew(PAGE_COUNT);
                        break;
                    case UPDATA_LIST_ID:
                        Log.d(TAG, "updata list  ....");
                        newData = refreshNew((SociaxItem) msg.obj);
                        break;
                    case UPDATA_LIST_TYPE:
                        Log.d(TAG, "updata list  ....");
                        newData = refreshNew((SociaxItem) msg.obj);
                        break;
                    case FAV_STATE:
                        mainMsg.arg2 = ((Posts) (refresState(mId))).getFavorite();
                        break;
                }

                mainMsg.what = SUCCESS;
                mainMsg.obj = newData;
                mainMsg.arg1 = msg.what;
            } catch (VerifyErrorException e) {
                mainMsg.obj = e.getMessage();
            } catch (ApiException e) {
                mainMsg.what = 2;
                mainMsg.obj = e.getMessage();
            } catch (ListAreEmptyException e) {
                mainMsg.obj = e.getMessage();
            } catch (DataInvalidException e) {
                mainMsg.obj = e.getMessage();
            }
            resultHander.sendMessage(mainMsg);
        }
    }

    @SuppressLint("HandlerLeak")
    class ResultHandler extends Handler {

        public ResultHandler() {
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            hiddenPromptView();
            if (msg.what == SUCCESS) {
                switch (msg.arg1) {
                    case REFRESH_NEW:
                        addFooter((ListData<SociaxItem>) msg.obj);
                        break;
                    case REFRESH_HEADER:
                        addHeader((ListData<SociaxItem>) msg.obj);
                        if (getListView() != null)
                            getListView().headerHiden();
                        Log.d(TAG, "refresh header load ....");
                        break;
                    case REFRESH_FOOTER:
                        if (hasMore(list))
                            addFooter((ListData<SociaxItem>) msg.obj);
                        else {
                            Toast.makeText(context, "暂无更多", Toast.LENGTH_SHORT).show();
                            if (getPullRefreshView() != null) {
                                getPullRefreshView().setMode(PullToRefreshBase.Mode.DISABLED);
                            }
                        }
                        Log.d(TAG, "refresh heiden load ....");
                        break;
                    case SEARCH_NEW:
                        changeListDataNew((ListData<SociaxItem>) msg.obj);
                        Log.d(TAG, "refresh heiden load ....");
                        break;
                    case SEARCH_NEW_BY_ID:
                        changeListDataNew((ListData<SociaxItem>) msg.obj);
                        Log.d(TAG, "refresh heiden load ....");
                        break;
                    case UPDATA_LIST:
                        changeListData((ListData<SociaxItem>) msg.obj);
                        Log.d(TAG, "refresh heiden load ....");
                        break;
                    case UPDATA_LIST_ID:
                        changeListDataNew((ListData<SociaxItem>) msg.obj);
                        Log.d(TAG, "refresh heiden load ....");
                        break;
                    case UPDATA_LIST_TYPE:
                        changeListDataNew((ListData<SociaxItem>) msg.obj);
                        Log.d(TAG, "refresh heiden load ....");
                        break;
                    case FAV_STATE:
                        context.updateView(mUpdateView, msg.arg2);
                        break;
                    case NOTIFY_ADAPTER:
                        notifyDataSetChanged();
                        break;
                    case StaticInApp.CHAT_CLEAR_HISTORY:// 聊天清理历史记录
                        clearList();
                        break;
                    case StaticInApp.CHAT_CLEAR_AND_DELETE:// 聊天删除并且退出
                        deleteList();
                        break;

                }
            }else {
                if(msg.obj != null) {
                    //打印错误信息
                    Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            Anim.cleanAnim(animView);
            if (loadingView != null && getListView() != null) {
                loadingView.hide((View) getListView());
            }
            if (context.getOtherView() != null) {
                loadingView.hide(context.getOtherView());
            }
            if (refresh != null)
                cleanRightButtonAnim(refresh);
        }
    }

    protected boolean hasMore(List list) {
//        return list != null && list.size() % 2 == 0;
        return true;
    }

    /**
     * 清除动画
     */
    protected void cleanRightButtonAnim(View v) {
        v.setClickable(true);
        // v.setBackgroundResource(context.getCustomTitle().getRightResource());
        v.clearAnimation();
    }

    /**
     * 删除列表，暂时只用于聊天删除
     */
    public void deleteList() {
        notifyDataSetChanged();
    }

    /**
     * 清理列表
     */
    public void doClearList() {

        Message msg = resultHander.obtainMessage();
        msg.what = StaticInApp.CHAT_CLEAR_HISTORY;
        resultHander.sendMessage(msg);
    }

    public void clearList() {
        this.list.clear();
        notifyDataSetChanged();
    }

    /**
     * 第一次加载初始数据
     */
    public void loadInitData() {
        // 判断网路是否可用
        if (!UnitSociax.isNetWorkON(context)) {
            Toast.makeText(context, R.string.net_fail, Toast.LENGTH_SHORT).show();
            hiddenPromptView();
            if(getEmptyLayout() != null) {
                getEmptyLayout().setErrorType(EmptyLayout.NETWORK_ERROR);
            }
            return;
        }

        cache = Thinksns.getLastWeiboList();
        if (cache != null) {
            this.addHeader(cache);
        } else {
            adapterState = STATE_LOADING;
            refreshNewSociaxList();
        }
    }

    /**
     * 设置加载动画，如果从frangment中生成，一般在frangment中获取，否则从activity中获取
     */
    protected void setLoadingView() {

        if (fragment != null) {
            loadingView = (LoadingView) fragment.findLoadingViewById(LoadingView.ID);
            if (loadingView == null) {
                loadingView = (LoadingView) context.findViewById(LoadingView.ID);
            }
        } else {
            loadingView = (LoadingView) context.findViewById(LoadingView.ID);
        }

    }

    private int mId;
    private int mState;
    private View mUpdateView;

    public void loadInitData(View updateView, int id, int state) {

        if (!UnitSociax.isNetWorkON(context)) {
            Toast.makeText(context, R.string.net_fail, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mId = id;
        mState = state;
        mUpdateView = updateView;
        if (this.getCount() == 0) {
            ListData<SociaxItem> cache = Thinksns.getLastWeiboList();
            if (cache != null) {
                this.addHeader(cache);
            } else {
                setLoadingView();
                if (loadingView != null)
                    loadingView.show(null);
                if (context.getOtherView() != null) {
                    loadingView.show(context.getOtherView());
                }
                refreshNewSociaxList();
                updateState(mId);
            }
        }
    }

    public int getMyUid() {
        Thinksns app = thread.getApp();
        return Thinksns.getMy().getUid();
    }

    public ApiUsers getApiUsers() {
        Thinksns app = thread.getApp();
        return app.getUsers();
    }

    public int getMySite() {
        Thinksns app = thread.getApp();
        if (Thinksns.getMySite() == null) {
            return 0;
        } else {
            return Thinksns.getMySite().getSite_id();
        }
    }


    /****** t4 *********/
    /**
     * 获取视频列表
     */
    public List<VideoWithPlayButtonView> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<VideoWithPlayButtonView> videoList) {
        this.videoList = videoList;
    }

    public List<ImageView> getVideoList2() {
        return videoList2;
    }

    public void setVideoList2(List<ImageView> videoList) {
        this.videoList2 = videoList;
    }

    protected Map<String, VideoWithPlayButtonView> videoMap;
    protected Map<String, ImageView> videoMap2;

    public Map<String, VideoWithPlayButtonView> getVideoMap() {
        return videoMap;
    }

    public void setVideoMap(Map<String, VideoWithPlayButtonView> videoMap) {
        this.videoMap = videoMap;
    }

    public Map<String, ImageView> getVideoMap2() {
        return videoMap2;
    }

    public void setVideoMap2(Map<String, ImageView> videoMap) {
        this.videoMap2 = videoMap;
    }

    public OnTouchListListener getListView() {
        if (fragment != null) {
            return fragment.getListView();
        } else if (context != null) {
            return context.getListView();
        }
        return null;
    }

    /**
     * 获取默认视图
     */
    public View getDefaultView() {
        if (fragment != null) {
            return fragment.getDefaultView();
        } else if (context != null) {
            return context.getDefaultView();
        }
        return null;
    }

    /**
     * 获取默认缺省图
     *
     */
    public EmptyLayout getEmptyLayout() {
        if(fragment != null) {
            return fragment.getEmptyLayout();
        }

        return null;
    }

    /**
     * 获取下拉刷新控件
     */
    public PullToRefreshListView getPullRefreshView() {
        if (fragment != null) {
            return fragment.getPullRefreshView();
        } else if (context != null) {
            return context.getPullRefreshView();
        }
        return null;
    }

    /**
     * 关闭or隐藏提示view
     */
    private void hiddenPromptView() {
        if (getPullRefreshView() != null) {
            getPullRefreshView().onRefreshComplete();
        }

        if (getLoadingView() != null &&
                getLoadingView().getVisibility() == View.VISIBLE) {
            getLoadingView().setVisibility(View.GONE);
        }
    }

    /**
     * 当前adapter是否需要显示更多
     */
    public boolean isShowFooter() {
        return isShowFooter;
    }

    /**
     * 当前adapter是否需要更多，只是判断是否有更多，不代表一定显示
     */
    public void setShowFooter(boolean isShowFooter) {
        this.isShowFooter = isShowFooter;
    }

    public ListData<SociaxItem> getList() {
        return list;
    }

    public void setList(ListData<SociaxItem> list) {
        this.list = list;
    }

    public FragmentSociax getFragment() {
        return fragment;
    }

    public void setFragment(FragmentSociax fragment) {
        this.fragment = fragment;
    }
}
