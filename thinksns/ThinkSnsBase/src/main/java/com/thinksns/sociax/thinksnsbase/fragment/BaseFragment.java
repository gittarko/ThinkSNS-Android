package com.thinksns.sociax.thinksnsbase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hedong on 16/2/19.
 */
public class BaseFragment extends Fragment implements View.OnClickListener {
    public static final int STATE_NONE = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADMORE = 2;
    public static final int STATE_NOMORE = 3;
    public static final int STATE_PRESSNONE = 4;// 正在下拉但还没有到刷新的状态
    public int mState = STATE_NONE;

    protected LayoutInflater mInflater;
    protected ViewGroup rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mInflater = inflater;
        if(rootView != null) {
            if(rootView.getParent() != null)
                ((ViewGroup)rootView.getParent()).removeView(rootView);
        }

        rootView = (ViewGroup)inflater.inflate(getLayoutId(), container, false);
        return rootView;
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
        super.onDestroy();
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View inflateView(int resId) {
        return this.mInflater.inflate(resId, null);
    }


    @Override
    public void onClick(View v) {

    }

    public void initView(View view) {

    }

    public void initData() {

    }

}
