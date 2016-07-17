package com.thinksns.sociax.t4.android.interfaces;

import android.view.View;

import com.thinksns.sociax.t4.model.ModelComment;
import com.thinksns.sociax.t4.model.ModelWeibo;

/**
 * Created by hedong on 16/2/19.
 */
public interface WeiboListViewClickListener {
    /**
     * 以下几个方法主要用于向业务层请求数据
     * @param position
     */
    //对微博点赞
    public void onDigWeibo(int position);

    //对微博评论
    public void onCommentWeibo(ModelWeibo weibo, ModelComment comment);

    //点击微博更多选项
    public void onWeiboMoreClick(int position);

    //刷新微博
    public void onRefresh();

    //关注微博好友
    public void onFollowWeibo(ModelWeibo weibo);

    /**
     * 以下几个方法主要用于更新UI
     *
     */
    //关注成功状态(1:成功 0：失败)
    void onFollowWeiboStatus(int type);

    void onDigWeiboStatus(int type);

    void onCommentWeiboStatus(int type);

    void onDeleteWeiboStatus(int type);

    void onCollectWeiboStatus(int type);

    //删除微博评论
    void onDeleteWeiboComment(int status);

}
