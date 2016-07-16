package com.thinksns.tschat.api;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;


/**
 * Created by hedong on 15/12/3.
 */
public abstract  class RequestResponseHandler extends AsyncHttpResponseHandler {
    //该方法用于非执行网络请求的回调
    public abstract void onSuccess(Object result);

    public abstract void onFailure(Object errorResult);

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

    }

    @Override
    public void onSuccess(int i, Header[] headers, byte[] bytes) {

    }
}
