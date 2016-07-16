package com.thinksns.tschat.inter;

/**
 * Created by hedong on 16/3/24.
 */
public class ChatRetryHandler {
    private final int maxRetries;
    private final int retrySleepTime;

    ChatRetryHandler(int maxRetries, int retrySleepTime) {
        this.maxRetries = maxRetries;
        this.retrySleepTime = retrySleepTime;
    }

    public boolean retryRequest() {
        boolean retry = false;

        return retry;

    }

}
