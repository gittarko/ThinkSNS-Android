package com.thinksns.sociax.t4.android.interfaces;

/**
 * Created by hedong on 16/4/19.
 * 注册流程数据加载回调接口
 */
public interface OnRegisterLoadListener {
    /**数据成功加载***/
    public void onLoadCompelete();
    /***数据加载失败***/
    public void onLoadError();
}
