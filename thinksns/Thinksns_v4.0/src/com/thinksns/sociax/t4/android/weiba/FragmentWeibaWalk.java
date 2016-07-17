package com.thinksns.sociax.t4.android.weiba;

import android.os.Bundle;

import com.thinksns.sociax.t4.android.fragment.BaseFragmentPostList;
import com.thinksns.sociax.t4.android.presenter.PostListPresenter;

/**
 * Created by hedong on 16/4/6.
 * 逛一逛微吧
 */
public class FragmentWeibaWalk extends BaseFragmentPostList {

    public static FragmentWeibaWalk newInstance(Bundle args) {
        FragmentWeibaWalk fragment = new FragmentWeibaWalk();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getRequestPostType() {
        return PostListPresenter.WEIBA_WALK;
    }
}
