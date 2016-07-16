package com.thinksns.tschat.inter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by hedong on 16/3/15.
 */
public abstract class ChatCoreResponseHandler implements ResponseInterface {
    //消息回调常量
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;

    private static final String LOG_TAG = "ChatCoreResponseHandler";
    private Handler handler;

    private Looper looper = null;
    private WeakReference<Object> TAG = new WeakReference<Object>(null);

    public ChatCoreResponseHandler() {
        this(null);
    }

    public ChatCoreResponseHandler(Looper looper) {
        this.looper = (looper == null ? Looper.myLooper() : looper);
        this.handler = new ResponderHandler(this, this.looper);
    }


    public abstract void onSuccess(Object object);

    public abstract void onFailure(Object object);

    //任务开始执行
    public void onStart(Object object) {

    }

    //任务结束执行
    public void onFinish(Object object) {

    }

    //任务执行的进度
    public void onProgress(Object object) {

    }

    @Override
    public Object getTag() {
        return this.TAG.get();
    }

    @Override
    public void setTag(Object tag) {
        this.TAG = new WeakReference<Object>(tag);
    }

    //发送一条消息
    protected void sendMessage(Message msg) {
        if (!Thread.currentThread().isInterrupted()) {
            handler.sendMessage(msg);
        }
    }

    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case SUCCESS_MESSAGE:
                Log.v("ChatCoreResponseHandler", "处理成功消息....");
                onSuccess(msg.obj);
                break;
            case FAILURE_MESSAGE:
                Log.v("ChatCoreResponseHandler", "处理失败消息....");
                onFailure(msg.obj);
                break;
            case START_MESSAGE:
                onStart(msg.obj);
                break;
            case FINISH_MESSAGE:
                onFinish(msg.obj);
                break;
        }
    }

    @Override
    public void sendCommitMessage() {

    }

    @Override
    public void sendSuccessMessage(Object result) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, result));
    }

    @Override
    public void sendFailureMessage(Object result) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, result));
    }

    @Override
    public void sendStartMessage(Object result) {
        sendMessage(obtainMessage(START_MESSAGE, result));
    }

    @Override
    public void sendFinishMessage(Object result) {
        sendMessage(obtainMessage(FINISH_MESSAGE, result));
    }

    protected Message obtainMessage(int responseMessageId, Object responseMessageData) {
        return Message.obtain(handler, responseMessageId, responseMessageData);
    }

    private static class ResponderHandler extends Handler {
        private final ChatCoreResponseHandler mResponder;

        ResponderHandler(ChatCoreResponseHandler mResponder, Looper looper) {
            super(looper);
            this.mResponder = mResponder;
        }

        @Override
        public void handleMessage(Message msg) {
            mResponder.handleMessage(msg);
        }
    }

}
