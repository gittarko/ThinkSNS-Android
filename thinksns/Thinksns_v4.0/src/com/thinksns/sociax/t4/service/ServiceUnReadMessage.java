package com.thinksns.sociax.t4.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.api.ApiCheckin;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelNotification;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-11-26
 * @version 1.0
 */
public class ServiceUnReadMessage extends Service {
	private static final String TAG = "MessageService";
	//获取未读消息的时间间隔
	private static final int DEFAULT_UNREADMSG_SLEEP_TIME = 3000;

	private NotificationManager notificationManager;
	private ModelNotification mdNotifyCation;
	private Handler handler;
	private ExecutorService threadPool;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		threadPool = Executors.newSingleThreadExecutor();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				threadPool.submit(new MyThread());
			}
		};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Message Server start ....");
		threadPool.submit(new CheckInThread());
		handler.sendEmptyMessage(1);
		return super.onStartCommand(intent, flags, startId);
	}

	//获取未读消息线程
	private class MyThread implements Runnable {
		@Override
		public void run() {
			getUnreadCount();
		}
	}

	//获取签到线程
	private class CheckInThread implements Runnable {

		@Override
		public void run() {
			try {
				Object result = new Api.CheckinApi().getCheckInfo();
				Thinksns.setCheckIn(result);
				JSONObject jsonData = new JSONObject(result.toString());
				if (jsonData.getBoolean("ischeck")) {
					//已经签到
					getRankTask();
				}
			} catch (ApiException e) {
				e.printStackTrace();
			}catch(JSONException e) {
					e.printStackTrace();
			}
		}
	}

	//获取签到排行榜
	private void getRankTask() {
		ApiCheckin apiCheckin = new Api.CheckinApi();
		try {
			Object object = apiCheckin.getCheckRankList();
			Thinksns.setRankInfo(object);
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean stopService(Intent name) {
		Log.i("mythread", "合理的关掉了这个线程哈");
		return super.stopService(name);
	}

	/***
	 * 获取未读消息
	 */
	public void getUnreadCount() {
		Thinksns app = (Thinksns) this.getApplicationContext();
		mdNotifyCation = new ModelNotification();
		if (app.getMessages() != null)
			try {
				mdNotifyCation = (ModelNotification) app.getMessages()
						.getUnreadCount();
			} catch (VerifyErrorException e) {
				e.printStackTrace();
			} catch (DataInvalidException e) {
				e.printStackTrace();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		// 发送广播
		if (mdNotifyCation.checkValid()) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("content", mdNotifyCation);
			intent.putExtras(bundle);
			intent.setAction(StaticInApp.SERVICE_NEW_NOTIFICATION);
			sendBroadcast(intent);
		} else {
			Log.v("ServiceUnReadMessage", "mdMotification is unv alid");
		}

		handler.sendEmptyMessageDelayed(1, DEFAULT_UNREADMSG_SLEEP_TIME);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
