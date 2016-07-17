package com.thinksns.sociax.t4.eventbus;

import com.thinksns.sociax.t4.model.ModelWeibo;

/**
 * Created by hedong on 16/3/2.
 */
public class WeiboEvent {
    public ModelWeibo weibo;
    public int position;

    public WeiboEvent(int position, ModelWeibo weibo) {
        this.position = position;
        this.weibo = weibo;
    }
}
