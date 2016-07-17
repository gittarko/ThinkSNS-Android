package com.thinksns.sociax.t4.android.api;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;

/**
 * 类说明：资讯
 * Created by Zoey on 2016-04-27.
 */
public interface ApiInformation {

    static final String MOD_NAME = "Information";
    static final String GET_CATE = "newsCate";//获取分类
    static final String GET_CATE_LIST = "newsList";//获取分类列表

    /**
     * 获取资讯分类
     *
     * @return
     * @throws ApiException
     */
    public void getCate(final ApiHttpClient.HttpResponseListener listener) throws ApiException;

//    /**
//     * 获取分类列表
//     *
//     * @param cid      分类id
//     * @param max_id     最后一条数据的id
//     * @param listener
//     * @return
//     * @throws ApiException
//     */
//    public void getCateList(int cid, int max_id, final ApiHttpClient.HttpResponseListener listener) throws ApiException;
    /**
     * 获取分类列表
     *
     * @param cid      分类id
     * @param max_id     最后一条数据的id
     * @param handler
     * @return
     * @throws ApiException
     */
    public void getCateList(int cid, int max_id, final AsyncHttpResponseHandler handler);

}
