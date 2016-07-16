package com.thinksns.tschat.inter;

/**
 * Created by hedong on 16/3/15.
 */
public interface ResponseInterface {

    void sendCommitMessage();

    //以下两个方法用于主线程中
    void sendSuccessMessage(Object result);
    void sendFailureMessage(Object result);
    void sendStartMessage(Object result);
    void sendFinishMessage(Object result);

    void setTag(Object tag);

    Object getTag();

}
