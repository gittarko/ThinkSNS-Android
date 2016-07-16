package com.thinksns.tschat.fragment;

import android.view.View;

/**
 * Created by hedong on 16/1/29.
 * 描述：消息页面View接口
 */

public interface ChatRoomView {
    /**
     * 开始刷新/显示刷新效果
     */
    void onRefreshStart();

    /**
     * 设置空白/缺省页面
     */
    void showEmptyView();

    /**
     * 显示网络错误提示
     */
    void showNetworkError();

    /**
     * 刷新完毕结束刷新效果
     */
    void onRefreshFinish();

    /**
     * 显示消息列表数据
     */
    void onLoadDataSuccess();

    /**
     * 显示文字提示
     * @param content
     */
    void showToast(String content);

    /**
     * 设置列表头部View
     */
    void setListHeaderView();

    /**
     * 设置列表底部View
     */
    void setListFooterView();
}
