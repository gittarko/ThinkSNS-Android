package com.thinksns.sociax.t4.android.Listener;

/**
 * Created by hedong on 16/4/13.
 */
public interface UnreadMessageListener {
    /**
     * 清除消息未读数
     * @param unread 待清除的个数
     * @param type  未读消息类型
     */
    public void clearUnreadMessage(int type, int unread);
}
