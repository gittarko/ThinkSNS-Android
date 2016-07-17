package com.thinksns.sociax.t4.android.interfaces;

import android.view.View;

import com.thinksns.sociax.t4.model.ModelChannel;

/**
 * Created by hedong on 16/3/7.
 */
public interface ChannelViewInterface {
    //关注频道
    public void postAddFollow(View view, ModelChannel channel);

    public void responseAddFollow();

}
