package com.thinksns.sociax.t4.android.info;

import android.support.v4.app.Fragment;

/**
 * Created by Zoey on 2016-04-28.
 */
public abstract class BaseFragment extends Fragment {
    protected boolean isVisible;

    protected boolean isFirst = true;

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        if (isFirst) {
            lazyLoad();
            isFirst = false;
        }

    }

    protected abstract void lazyLoad();

    protected void onInvisible() {
    }
}
