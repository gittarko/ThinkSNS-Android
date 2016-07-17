package com.thinksns.sociax.t4.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.tschat.chat.TSChatManager;


/**
 * Created by dong.he on 15/12/3.
 * 监听软件当前网络连接状态
 */

public class NetWrokConnectReceiver extends BroadcastReceiver {
    private static final String NETWORK_CHANGE_SATE = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(NETWORK_CHANGE_SATE.equals(intent.getAction())) {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //手机网络连接状态
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            //WIFI连接状态
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                //当前无可用的网络
            }else {
                //如果socket网络已断开，则执行重连
                try {
                    TSChatManager.retry("NETWORK CONNECTED");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
