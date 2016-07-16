package com.thinksns.sociax.thinksnsbase.base;

import android.content.Context;
import android.os.AsyncTask;import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.cache.CacheManager;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

import org.apache.http.Header;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by hedong on 16/2/19.
 * 基类Presenter，封装网络请求和缓存读取与保存操作
 */
public abstract class BaseListPresenter<T extends SociaxItem> {

    protected IBaseListView<T> baseListView;
    protected Context mContext;

    protected String cacheKey;
    protected int mCurrentPage = 0;
    protected int maxId = 0;    //用于分页的最大ID
    /**是否读取缓存***/
    protected boolean isReadCache = true;
    //缓存线程（用于保存服务端数据至本地）
    private AsyncTask<String, Void, ListData<T>> mCacheTask;
    //网络请求线程
    private ParserTask mParserTask;
    /***每次刷新内容的条数***/
    private static final int PAGE_SIZE = 20;
    /***正在从网络获取内容****/
    public static final int NETWORK_LOADING = 21;
    /***从网络获取内容完成***/
    public static final int NETWORK_DONE = 22;
    /***首次发起网络请求***/
    public static final int REFRESH_FIRST = 23;

    public int refreshState = REFRESH_FIRST;
    protected boolean isSaveCahe = true;

    public BaseListPresenter(Context context, IBaseListView<T> baseListView) {
        this.baseListView = baseListView;
        this.mContext = context;
    }

    //设置分页id
    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    //当前正在请求第几页
    public void setCurrentPage(int currentPage) {
        this.mCurrentPage = currentPage;
    }

    public int getMaxId() {
        return maxId;
    }

    /**
     * 请求网络数据
     * @param refresh 是否主动刷新
     */
    public void requestData(boolean refresh) {
        String cacheKey = getCacheKey();
        if(!UnitSociax.isNetWorkON(mContext)
                && isReadCache
                &&CacheManager.isExistDataCache(mContext, cacheKey)) {
            //如果没有网络需要获取缓存数据
            readCacheData(cacheKey);
            return;
        }

        /***首次加载网络优先从缓存读取内容***/
//        if(refreshState == REFRESH_FIRST) {
            if(isReadCache &&
                    CacheManager.isExistDataCache(mContext, cacheKey)) {
                //如果缓存存在从缓存加载
                readCacheData(cacheKey);
            }
//        }

        if(isReadCacheData(refresh)) {
            readCacheData(cacheKey);
        }

        if(refresh){
            //如果主动刷新,则从服务器获取最新数据
            loadNetData();
            refreshState = NETWORK_LOADING;
        }
    }

    //是否读取缓存
    protected boolean isReadCacheData(boolean refresh) {
        String key = getCacheKey();
        //如果没有网络需要获取缓存数据
        if(!UnitSociax.isNetWorkON(mContext)) {
            return true;
        }
        //强制不读取缓存
        if(!isReadCache)
            return false;

        //如果缓存存在且第一页数据没有主动刷新则优先从缓存加载
        if(CacheManager.isExistDataCache(mContext, key) &&
                !refresh && maxId == 0) {
            return true;
        }

        //其他页数如果缓存存在则优先加载缓存数据
        if(maxId != 0 && CacheManager.isExistDataCache(mContext, key)) {
            return true;
        }

        return false;
    }

    //从缓存中获取数据
    private void readCacheData(String cacheKey) {
        cancelReadCacheTask();
        mCacheTask = new CacheTask(mContext).execute(cacheKey);
    }

    private void cancelParserTask() {
        if (mParserTask != null) {
            mParserTask.cancel(true);
            mParserTask = null;
        }
    }

    private void cancelReadCacheTask() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }

    /**
     * 取消数据请求
     */
    public void cancelRequest() {
        cancelReadCacheTask();
        cancelParserTask();
    }

    /**
     * 缓存解析或读取线程
     */
    private class CacheTask extends AsyncTask<String, Void, ListData<T>> {
        private final WeakReference<Context> context;

        private CacheTask(Context context) {
            this.context = new WeakReference<Context>(context);
        }

        @Override
        protected ListData<T> doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(context.get(),
                    params[0]);
             if (seri == null) {
                return null;
             } else {
                    return readList(seri);
             }
        }

        @Override
        protected void onPostExecute(ListData<T> list) {
            super.onPostExecute(list);
            if (list != null) {
                baseListView.onLoadDataSuccess(list);
            } else {
                if (getMaxId() == 0
                        && !CacheManager.isExistDataCache(mContext, getCacheKey())) {
                    //空字符串则显示默认缺省
                    baseListView.onLoadDataError("");
                }else {
                    //null则显示列表底部视图
                    baseListView.onLoadDataError(null);
                }
            }

            baseListView.onLoadComplete();
        }
    }

    /**
     * 保存缓存线程
     */
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

    @SuppressWarnings("WrongThread")
    class ParserTask extends AsyncTask<Void, Void, String> {

        private final byte[] reponseData;
        private boolean parserError;
        private ListData<T> list;

        public ParserTask(byte[] data) {
            this.reponseData = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                if(reponseData != null) {
                    list = parseList(new String(reponseData));
                }else {
                    list = parseList(null);
                }
                if(needSaveCache()) {
                    saveCaCheData(list);
                }
            } catch (Exception e) {
                e.printStackTrace();
                parserError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            refreshState = NETWORK_DONE;
            if (parserError) {
                //数据解析错误
                readCacheData(getCacheKey());
            } else {
                baseListView.onLoadDataSuccess(list);
                baseListView.onLoadComplete();
            }
        }
    }

    /**是否需要保存缓存***/
    private boolean needSaveCache() {
        return isSaveCahe;
    }

    public void setSaveCache(boolean isSave) {
        this.isSaveCahe = isSave;
    }

    //保存列表对象到本地
    public void saveCaCheData(Serializable seri){
        new SaveCacheTask(mContext, seri, getCacheKey()).execute();
    }

    /**
     * 解析网络返回的数据
     * @param result
     * @return
     */
    public abstract ListData<T> parseList(String result);

    /**
     * 解析缓存数据
     * @param seri
     * @return
     */
    protected abstract ListData<T> readList(Serializable seri);

    //获取缓存标识
    public String getCacheKey() {
        return new StringBuilder(cacheKey).append("_").append(getCachePrefix()).append(maxId).toString();
    }

    public abstract String getCachePrefix();

    /**
     * 此方法最好设置数据
     * @param key
     */
    public void setCacheKey(String key) {
        this.cacheKey = key;
    }

    /**
     * 请求网络数据
     */
    public abstract void loadNetData();

    /**
     * 初始化网络数据
     * @param refresh 是否显示下拉刷新
     */
    public void loadInitData(boolean refresh) {
        if(refresh) {
            //主动刷新
            this.baseListView.setRefreshing(true);
        }else {
            //从本地刷新数据
            requestData(false);
        }
    }

    protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              byte[] responseBody) {
            if (mCurrentPage == 0 && needAutoRefresh()) {
//                AppContext.putToLastRefreshTime(getCacheKey(),
//                        StringUtils.getCurTimeStr());
            }
            Log.v("BaseListPresenter", "onSuccess:" + new String(responseBody));
            baseListView.onRequestNetworkSuccess();
            if (baseListView.isFragmentAdded()) {
                executeParserTask(responseBody);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            //表示网络请求已完成
            refreshState = BaseListPresenter.NETWORK_DONE;
            baseListView.onRequestNetworkSuccess();
            if (baseListView.isFragmentAdded()) {
                readCacheData(getCacheKey());
            }
        }
    };

    /**
     * 解析网络回掉数据
     * @param data
     */
    protected void executeParserTask(byte[] data) {
        cancelParserTask();
        mParserTask = new ParserTask(data);
        mParserTask.execute();
    }

    /**
     * 过滤重复数据
     * @param data
     * @param enity
     * @return
     */
    protected boolean compareTo(List<? extends SociaxItem> data, SociaxItem enity) {
        return false;
    }


    protected int getPageSize() {
        return PAGE_SIZE;
    }

    // 是否需要自动刷新
    protected boolean needAutoRefresh() {
        return true;
    }


    public  static  String  inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos   =   new   ByteArrayOutputStream();
        int i = -1;
        while((i=is.read())!=-1){
            baos.write(i);
        }

        return   baos.toString();
    }

}
