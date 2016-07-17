package com.thinksns.sociax.t4.android.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.video.VideoWithPlayButtonView;
import com.thinksns.sociax.t4.component.ListSociax;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.activity.widget.EmptyLayout;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.apache.http.Header;

import java.lang.reflect.Field;
import java.util.List;


/**
 * 类说明：fragment基类，尽量不要随便修改，需要修改的可以使用重写
 *
 * @author wz
 * @version 1.0
 * @date 2014-10-15
 */
public abstract class FragmentSociax extends Fragment {
    protected AdapterSociaxList adapter;// 数据adapter
    protected ListView listView;
    protected ListData<SociaxItem> list;// 数据list
    private View view;// 用于缓存fragment的view，防止oncreateView的时候重复加载
    protected ModelUser user;// 当前fragment所属用户
    protected int uid;// 当前fragment所属uid
    protected LoadingView loadingView;// 部分fragment需要用到loadingview
    protected Thinksns app;// app
    protected LayoutInflater inflater;

    protected AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              byte[] responseBytes) {
            if (isAdded()) {
                executeParserTask(responseBytes);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            if (isAdded()) {

            }
        }
    };

    private ParserResponseDataTask mParserTask;

    private void executeParserTask(byte[] data) {
        cancelParserTask();
        mParserTask = new ParserResponseDataTask(data);
        mParserTask.execute();
    }

    private void cancelParserTask() {
        if (mParserTask != null) {
            mParserTask.cancel(true);
            mParserTask = null;
        }
    }

    class ParserResponseDataTask extends AsyncTask<Void, Void, String> {

        private final byte[] reponseData;
        private boolean parserError;
        private ListData<SociaxItem> list;

        public ParserResponseDataTask(byte[] data) {
            this.reponseData = data;
            Log.v("FragmentSoicax", "response data:" + new String(data));
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                list = parseList(new String(
                        reponseData));
            } catch (Exception e) {
                e.printStackTrace();

                parserError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (parserError) {
            } else {
                executeDataSuccess(list);
            }
        }
    }

    protected ListData<SociaxItem> parseList(String result) throws Exception {
        return null;
    }

    public void executeDataSuccess(ListData<SociaxItem> list) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragmentUser();
        app = (Thinksns) getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        if (view == null) {
            view = inflater.inflate(getLayoutId(), null);

            try {
                initIntentData();
            } catch (Exception e) {
                finishByErr("init intentData excetion");
                e.printStackTrace();
            }
            initView();
            initListener();
            initData();
//            if (getPullRefreshView() != null && getDefaultView() != null) {
//                getPullRefreshView().setEmptyView(getDefaultView());
//            }
        } else {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;

        }
        return view;
    }


    /**
     * 给出layoutId,获取layout
     */
    public abstract int getLayoutId();

    public AdapterSociaxList createAdapter() {
        return null;
    }

    /**
     * 获取当前fragment的listview
     */
    public ListSociax getListView() {
        return null;
    }

    public PullToRefreshListView getPullRefreshView() {
        return null;
    }

    public View getDefaultView() {
        return null;
    }

    //获取默认缺省图
    public EmptyLayout getEmptyLayout() {
        return null;
    }

    /**
     * 获取当前fragment的adapter
     */
    public AdapterSociaxList getAdapter() {
        return adapter;
    }

    public void setAdapter(AdapterSociaxList adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * fragment执行头部刷新，交给adapter执行
     */
    public void doRefreshHeader() {
        if (adapter != null)
            adapter.doRefreshHeader();
    }

    /**
     * fragment执行尾部刷新，交给adapter执行
     */
    public void doRefreshFooter() {
        if (adapter != null)
            adapter.doRefreshFooter();
    }

    public void doRefreshUpdata() {
        if (adapter != null)
            adapter.doUpdataList();
    }

    /**
     * 初始化view
     */
    public abstract void initView();

    /**
     * 初始化intentdata
     */
    public abstract void initIntentData();

    /**
     * 初始化监听
     */
    public abstract void initListener();

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 从fragment中找出loadingview， 需要在initView中讲fragmentView设置成view
     */
    public LoadingView findLoadingViewById(int id) {
        return (LoadingView) getActivity().findViewById(LoadingView.ID);
    }

    /**
     * 初始化用户数据，如果子类fragment涉及到用户的话，需要至少传入intent user或者uid
     */
    public void initFragmentUser() {
        if (getActivity().getIntent().hasExtra("user")) {// 首先判断是否传入了user，如果有，则获取user的uid
            this.user = (ModelUser) getActivity().getIntent()
                    .getSerializableExtra("user");
            if (user.getUid() != 0) {// 如果用户id不为0则设置uid
                this.uid = user.getUid();
            }
        }
        if (uid == 0 && getActivity().getIntent().hasExtra("uid")) {// 如果uid为0，则表示没传入user
            // 尝试获取uid
            this.uid = getActivity().getIntent().getIntExtra("uid", 0);
        }
        if (uid == 0 || uid == Thinksns.getMy().getUid()) {// 如果uid仍然为0或者就是本人的uid，，那么使用当前登录用户信息，不需要再获取
            if (Thinksns.getMy() != null) {
                uid = Thinksns.getMy().getUid();
                user = Thinksns.getMy();
//            } else {
//                finishByErr("initFragment no user nor uid");
//                System.exit(0);
            }
        } else {// 如果uid为他人uid，则根据uid获取他人信息，这一步一般不会执行,因为一般传入详细的user或者至少传入uid
        }
    }

    /**
     * fragment生成错误，一般是intent传值错误
     *
     * @param info log内提示的信息
     */
    protected void finishByErr(String info) {
        Toast.makeText(getActivity(), "读取错误", Toast.LENGTH_SHORT).show();
        onStop();
    }

    /**
     * 根据id查找组件
     */
    protected View findViewById(int id) {
        return view.findViewById(id);
    }

    // caoliga 增加，切换页面时暂停旧页面的视频播放
    public void pauseVideo() {
        if (adapter != null) {
            List<VideoWithPlayButtonView> videos = adapter.getVideoList();
            for (VideoWithPlayButtonView videoWithPlayButtonView : videos) {
                if (videoWithPlayButtonView.isPlaying()) {
                    videoWithPlayButtonView.pause();
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ListData<SociaxItem> getList() {
        return list;
    }
}
