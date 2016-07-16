package com.thinksns.tschat.eventbus;

/**
 * Created by hedong on 16/3/14.
 * socket登录状态事件
 */
public class SocketLoginEvent {
    public enum LOGIN_STATUS {
        LOGIN_SUCCESS, LOGIN_ERROR
    }

    private LOGIN_STATUS status;

    public LOGIN_STATUS getStatus() {
        return status;
    }

    public void setStatus(LOGIN_STATUS status) {
        this.status = status;
    }

    public SocketLoginEvent(LOGIN_STATUS status) {
        setStatus(status);
    }
}
