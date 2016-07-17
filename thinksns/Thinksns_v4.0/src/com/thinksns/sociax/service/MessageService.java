package com.thinksns.sociax.service;

import com.thinksns.sociax.android.home.SettingsActivity;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.db.RemindSqlHelper;
import com.thinksns.sociax.modle.NotifyItem;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MessageService extends Service {

	private static final String TAG = "MessageService";

	private boolean flag = true;

	private NotificationManager notificationManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// registerReceiver(receiver, filter)
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Message Server start ....");
		new Thread(new Runnable() {
			boolean tag = true;

			@Override
			public void run() {
				while (tag) {
					if (SettingsActivity.isAutoRemind(MessageService.this)) {
						try {
							Long interval = SettingsActivity
									.getTimeInterval(MessageService.this);
							Thread.sleep(interval);
							getMessageCount();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}

	public void getMessageCount() {
		int messageCount = 0;
		Thinksns app = (Thinksns) this.getApplicationContext();

		try {
			if (Thinksns.getMy() != null) {
				messageCount = app.getApiNotifytion().getMessageCount();
				// if (messageCount > 0) {
				Intent intent = new Intent();
				intent.setAction("maingrid.messagereceiver");
				intent.putExtra("messageCount", messageCount);
				intent.putExtra("hasMsg", true);
				sendBroadcast(intent);
				Log.d(AppConstant.APP_TAG, "server===> messagecount ===>"
						+ messageCount);
				// }
			}
		} catch (ApiException e) {
			Log.d(AppConstant.APP_TAG,
					"server===> messagecount ===> wm" + e.toString());
		}
	}

	public void getNotifyCount() {
		ListData<SociaxItem> notifyList = null;
		Thinksns app = (Thinksns) this.getApplicationContext();
		RemindSqlHelper reHelper = app.getRemindSql();
		try {
			if (Thinksns.getMy() != null) {
				notifyList = app.getApiNotifytion().getNotifyByCount(0);
				if (notifyList != null) {
					NotifyItem notifyItem;
					for (int i = 0; i < notifyList.size(); i++) {
						notifyItem = (NotifyItem) notifyList.get(i);
						notifyItem
								.setTimesTmap(System.currentTimeMillis() + "");
						if (reHelper.isHasRemind(notifyItem.getName())) {
							reHelper.updataRemind(notifyItem);
							Log.d(AppConstant.APP_TAG,
									"updataRemindMessage ....");
						} else {
							reHelper.addRemindMessage(notifyItem);
							Log.d(AppConstant.APP_TAG, "addRemindMessage ....");
						}
					}
					Intent intent = new Intent();
					intent.setAction("maingrid.messagereceiver");
					intent.putExtra("notifyList", notifyList);
					intent.putExtra("hasMsg", true);
					sentNotify(notifyList); // 发送通知
					sendBroadcast(intent);
					Log.d(TAG, "getNotifyCount()---" + notifyList.toString());
				} else {
					reHelper.clearCountNum();
				}
			}
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}

	static final int ATME_NOTIFY = 1;
	static final int COMMENT_NOTIFY = 2;
	static final int MESSAGE_NOTIFY = 3;

	int atNum = 0;
	int comNum = 0;
	int mesNum = 0;

	@SuppressWarnings("deprecation")
	void sentNotify(ListData<SociaxItem> notifyList) {

		Log.d(TAG, "send notification ...   ");
		for (SociaxItem soxItem : notifyList) {

			NotifyItem notifyItem = (NotifyItem) soxItem;
//			getIntentStart(notifyItem);

			if (notifyItem.getType().equals("atme")) {

				int temp = notifyItem.getCount();
				if (temp != atNum) {
					atNum = temp;
					Notification notification = new Notification();
					setNotification(notification);
					notification.tickerText = "有" + notifyItem.getCount()
							+ "条提到我的微博";
					notification.number = temp;
					Intent intent = null;//getIntentStart(notifyItem);
					PendingIntent pIntent = PendingIntent.getActivity(
							getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					Log.d(TAG, "atmePendingIntent" + pIntent.toString());
//					notification.setLatestEventInfo(getApplicationContext(),
//							"有" + notifyItem.getCount() + "条提到我的微博", null,
//							pIntent);
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					notificationManager.notify(ATME_NOTIFY, notification);
				}

			} else if (notifyItem.getType().equals("comment")) {

				int temp = notifyItem.getCount();
				if (temp != comNum) {
					comNum = temp;
					Notification notification = new Notification();
					setNotification(notification);
					notification.tickerText = "您有" + notifyItem.getCount()
							+ "条评论";
					notification.number = temp;
					Intent intent = null;//getIntentStart(notifyItem);
					PendingIntent pIntent = PendingIntent.getActivity(
							getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					Log.d(TAG, "commentPendingIntent" + pIntent);
					//将target设置成23后暂时注释
//					notification
//							.setLatestEventInfo(getApplicationContext(), "您有"
//									+ notifyItem.getCount() + "条评论", null,
//									pIntent);
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					notificationManager.notify(COMMENT_NOTIFY, notification);
				}
			} else if (notifyItem.getType().equals("message")) {

				int temp = notifyItem.getCount();
				if (temp != mesNum) {
					mesNum = temp;
					Notification notification = new Notification();
					setNotification(notification);
					notification.tickerText = "有" + notifyItem.getCount()
							+ "条私信";
					notification.number = temp;
					Intent intent = null;//getIntentStart(notifyItem);
					PendingIntent pIntent = PendingIntent.getActivity(
							getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
//					notification.setLatestEventInfo(getApplicationContext(),
//							"有" + notifyItem.getCount() + "条私信", null, pIntent);
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					notificationManager.notify(MESSAGE_NOTIFY, notification);
				}
			}

		}
	}

	/**
	 * 设置通知的基本属性 如 icon，是否震动，铃声
	 * 
	 * @param notification
	 */
	private void setNotification(Notification notification) {
		notification.icon = R.drawable.icon;
		notification.defaults = Notification.DEFAULT_VIBRATE;
	}


}
