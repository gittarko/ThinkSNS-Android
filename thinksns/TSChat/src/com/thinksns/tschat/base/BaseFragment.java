package com.thinksns.tschat.base;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 */
public abstract  class BaseFragment extends Fragment implements
        View.OnClickListener {

    public static final int STATE_NONE = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADMORE = 2;
    public static final int STATE_NOMORE = 3;
    public static final int STATE_PRESSNONE = 4;    // 正在下拉但还没有到刷新的状态
    public static int mState = STATE_NONE;

    protected LayoutInflater mInflater;
    protected Context context;
    private View mView;

    public Application getApplication() {
        return getActivity().getApplication();
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if(mView != null) {
            ViewGroup parent = (ViewGroup)mView.getParent();
            if(parent != null)
                parent.removeView(mView);
            return mView;
        }

        this.mInflater = inflater;
        mView = inflater.inflate(getLayoutId(), container, false);
        context = mView.getContext();
        //初始化Activity Intent传递的数据
        initIntentData();
        //初始化视图
        initView(mView);
        //初始化界面事件
        initListener();
        initReceiver();
        //初始化数据
        initData();

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void initReceiver() {

    }

    protected void destroyReceiver() {

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
    public void onDestroy() {
        destroyReceiver();
        super.onDestroy();
    }

    protected abstract int getLayoutId();

    protected View inflateView(int resId) {
        return this.mInflater.inflate(resId, null);
    }

    protected View findViewById(int id) {
        return mView.findViewById(id);
    }

    public abstract  void initView(View view);

    public void initIntentData() {

    }

    public void initData() {

    }

    public void initListener() {

    }

    @Override
    public void onClick(View v) {

    }
}
