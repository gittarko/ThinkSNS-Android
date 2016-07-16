package com.thinksns.sociax.thinksnsbase.base;

import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/**
 * Created by hedong on 16/2/19.
 * 列表基类接口
 */
public interface IBaseListView<T extends SociaxItem> {
    //显示刷新
    void setRefreshing(boolean refresh);
    //设置正在刷新状态
    public void setRefreshLoadingState();

    //设置刷新完毕
    void setRefreshLoadedState();

    //数据解析完成
    void onLoadComplete();

    //数据解析完毕
    void onLoadDataSuccess(ListData<T> data);

    //数据解析错误
    void onLoadDataError(String error);

    void onRequestNetworkSuccess();

    boolean isFragmentAdded();

}
