package com.thinksns.sociax.t4.android.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksns.sociax.android.R;

/**
 * Created by Zoey on 2016-04-28.
 */
public class CustomListFragment extends BaseFragment {

    private static final String FRAGMENT_INDEX = "fragment_index";
    private final int FIRST_FRAGMENT = 0;
    private final int SECOND_FRAGMENT = 1;
    private final int THIRD_FRAGMENT = 2;

    private View view;

    private int mCurIndex = -1;
    /** 标志位，标志已经初始化完成 */
    private boolean isPrepared;
    /** 是否已被加载过一次，第二次就不再去请求数据了 */
    private boolean mHasLoadedOnce;

    /**
     * 创建新实例
     *
     * @param index
     * @return
     */
    public static CustomListFragment newInstance(int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_INDEX, index);
        CustomListFragment fragment = new CustomListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = (View) inflater.inflate(R.layout.fragment_pull_refresh_listview, container, false);
            //获得索引值
            Bundle bundle = getArguments();
            if (bundle != null) {
                mCurIndex = bundle.getInt(FRAGMENT_INDEX);
            }
            isPrepared = true;
            lazyLoad();
        }

        //因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup)view.getParent();
        if(parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
    }

    private void setView() {
        // 根据索引加载不同视图
        switch (mCurIndex) {
            case FIRST_FRAGMENT:
                break;

            case SECOND_FRAGMENT:
                break;

            case THIRD_FRAGMENT:
                break;
        }
    }
}
