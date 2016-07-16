
package com.thinksns.tschat.notify;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.thinksns.tschat.bean.ListData;
import com.thinksns.tschat.bean.ModelChatUserList;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MessageNotifier {
    private final static String TAG = "notify";
    private Ringtone ringtone = null;

    protected final static String[] msg_eng = { 
    				"sent a message", 
    				"sent a picture", 
    				"sent a voice",
    				"sent location message", 
    				"sent a video", 
    				"sent a file", 
    				"%1 contacts sent %2 messages"
                                              };
    protected final static String[] msg_ch = { 
    				"发来一条消息", "发来一张图片", 
    				"发来一段语音", "发来位置信息", 
    				"发来一个视频", "发来一个文件",
    				"%1个联系人发来%2条消息"};

    protected static int notifyID = 0525; // start notification id
    protected static int foregroundNotifyID = 0555;

    protected NotificationManager notificationManager = null;

    protected HashSet<String> fromUsers = new HashSet<String>();
    protected int notificationNum = 0;
    //未读消息数
    protected int unreadTotalNum = 0;
    
    protected Context appContext;
    protected String packageName;
    protected String[] msgs;
    protected long lastNotifiyTime;
    protected AudioManager audioManager;
    protected Vibrator vibrator;
    protected NotificationInfoProvider notificationInfoProvider;

    public MessageNotifier() {
    }
    
    /**
     * 开发者可以重载此函数
     * this function can be override
     * @param context
     * @return
     */
    public MessageNotifier init(Context context){
        appContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        packageName = appContext.getApplicationInfo().packageName;
        if (Locale.getDefault().getLanguage().equals("zh")) {
            msgs = msg_ch;
        } else {
            msgs = msg_eng;
        }

        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
        
        return this;
    }
    
    /**
     * 开发者可以重载此函数
     * this function can be override
     */
    public void reset(){
        resetNotificationCount();
        cancelNotificaton();
    }

    void resetNotificationCount() {
        notificationNum = 0;
        fromUsers.clear();
    }
    
    void cancelNotificaton() {
        if (notificationManager != null)
            notificationManager.cancel(notifyID);
    }

    /**
     * 清除未读消息
     * @param room_id
     */
    public void clearNotification(int room_id, int count) {
        if(notificationNum >= count) {
            notificationNum -= count;
        }else {
            notificationNum = 0;
        }
        fromUsers.remove(room_id + "");
    }

    /**
     * 处理新收到的消息，然后发送通知
     * 
     * 开发者可以重载此函数
     * this function can be override
     * 
     * @param message
     */
    public synchronized void onNewMsg(ModelChatUserList message) {
        // 判断app是否在后台
        if (!isAppRunningForeground(appContext)) {
            Log.d(TAG, "app is running in backgroud");
            sendNotification(message, false);
        } else {
            sendNotification(message, true);
        }
        //声音提示
//        viberateAndPlayTone(message);
    }
    
    //未读消息数
    public synchronized void setUnreadMsg(int count) {
    	unreadTotalNum += count;
    	if(unreadTotalNum <0)
    		unreadTotalNum = 0;
    }
    
    public int getUnreadNum() {
    	return unreadTotalNum;
    }
    
    public static boolean isAppRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName() + ", appProcess pid:" + appProcess.pid);
                if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
    
    public synchronized void onNewMesg(List<ModelChatUserList> messages) {
        // 判断app是否在后台
        if (!isAppRunningForeground(appContext)) {
            Log.d(TAG, "app is running in backgroud");
            sendNotification(messages, false);
        } else {
            sendNotification(messages, true);
        }

//        viberateAndPlayTone(messages.get(messages.size()-1));
    }

    /**
     * 发送通知栏提示
     * This can be override by subclass to provide customer implementation
     * @param messages
     * @param isForeground
     */
    protected void sendNotification (List<ModelChatUserList> messages,
    		boolean isForeground){
        for(ModelChatUserList message : messages){
            if(!isForeground){
                notificationNum++;
                fromUsers.add(message.getRoom_id() + "");
            }
        }

//        sendNotification(messages.get(messages.size()-1), isForeground, false);
    }
    
    protected void sendNotification (ModelChatUserList message, boolean isForeground){
        sendNotification(message, isForeground, true);
    }
    
    /**
     * 发送通知栏提示
     * This can be override by subclass to provide customer implementation
     * @param message
     */
    protected void sendNotification(ModelChatUserList message, boolean isForeground, boolean numIncrease) {
        String username = message.getTitle();
        if(username == null)
        	username = "未知";
        try {
            String notifyText = username + ":" + message.getContent();
//            String msg_type = message.getType();
//            if(msg_type.equals("text")) {
//            	notifyText += msgs[0];
//            }else if(msg_type.equals("image")) {
//            	notifyText += msgs[1];
//            }else if(msg_type.equals("voice")) {
//            	notifyText += msgs[2];
//            }else if(msg_type.equals("position")) {
//            	notifyText += msgs[3];
//            }
            
            PackageManager packageManager = appContext.getPackageManager();
            String appname = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());
            
            // notification titile
            String contentTitle = appname;
            if (notificationInfoProvider != null) {
                String customNotifyText = notificationInfoProvider.getDisplayedText(message);
                String customCotentTitle = notificationInfoProvider.getTitle(message);
                if (customNotifyText != null){
                    // 设置自定义的状态栏提示内容
                    notifyText = customNotifyText;
                }
                    
                if (customCotentTitle != null){
                    // 设置自定义的通知栏标题
                    contentTitle = customCotentTitle;
                }   
            }

            // create and send notificaiton
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                                                                        .setSmallIcon(appContext.getApplicationInfo().icon)
                                                                        .setWhen(System.currentTimeMillis())
                                                                        .setAutoCancel(true);

            Intent msgIntent = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
            if (notificationInfoProvider != null) {
                // 设置自定义的notification点击跳转intent
                msgIntent = notificationInfoProvider.getLaunchIntent(message);
            }

            PendingIntent pendingIntent = null;
            if(msgIntent != null)
            	pendingIntent = PendingIntent.getActivity(appContext, notifyID, 
            		msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            if(numIncrease){
                // prepare latest event info section
                if(!isForeground){
                    notificationNum++;
                    fromUsers.add(username);
                }
            }

            int fromUsersNum = fromUsers.size();
            String summaryBody = msgs[6].replaceFirst("%1", Integer.toString(fromUsersNum)).replaceFirst("%2",Integer.toString(notificationNum));
            
            if (notificationInfoProvider != null) {
                // lastest text
                String customSummaryBody = notificationInfoProvider.getLatestText(message, fromUsersNum,notificationNum);
                if (customSummaryBody != null){
                    summaryBody = customSummaryBody;
                }
                
                // small icon
                int smallIcon = notificationInfoProvider.getSmallIcon(message);
                if (smallIcon != 0){
                    mBuilder.setSmallIcon(smallIcon);
                }
            }

            mBuilder.setContentTitle(contentTitle);
            mBuilder.setTicker(notifyText);
            mBuilder.setContentText(summaryBody);
            if(pendingIntent != null)
            	mBuilder.setContentIntent(pendingIntent);
            mBuilder.setNumber(notificationNum);
            Notification notification = mBuilder.build();
            notification.defaults = Notification.DEFAULT_SOUND;
//            //设置消息提示音
//            notification.sound = Uri.parse("android.resource://" + 
//            			appContext.getPackageName() + "/" + R.raw.app_notify);
//            if (isForeground) {
//                notificationManager.notify(foregroundNotifyID, notification);
//            } else {
////                notificationManager.notify(notifyID, notification);
//            	notificationManager.cancel(foregroundNotifyID);
//            }
            notificationManager.notify(foregroundNotifyID, notification);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 手机震动和声音提示
     */
    public void viberateAndPlayTone(ModelChatUserList message) {
        if(message != null){
        
        	if (System.currentTimeMillis() - lastNotifiyTime < 1000) {
        		// received new messages within 2 seconds, skip play ringtone
        		return;
        	}
        
        	try {
        		lastNotifiyTime = System.currentTimeMillis();
            
        		// 判断是否处于静音模式
        		if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
        			Log.e(TAG, "in slient mode now");
        			return;
        		}
            
        		long[] pattern = new long[] { 0, 180, 80, 120 };
        		vibrator.vibrate(pattern, -1);
        		if (ringtone == null) {
//                    Uri notificationUri = RingtoneManager.getDefaultUri(
//                    		RingtoneManager.TYPE_NOTIFICATION);
//                    Uri notificationUri = Uri.parse("android.resource://" + 
//                			appContext.getPackageName() + "/" + R.raw.app_notify);
        			Uri notificationUri = RingtoneManager.getActualDefaultRingtoneUri(appContext, 
        					RingtoneManager.TYPE_NOTIFICATION);
                    ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                    if (ringtone == null) {
                        Log.d(TAG, "cant find ringtone at:" + notificationUri.getPath());
                        return;
                    }
             }
                if (!ringtone.isPlaying()) {
                    String vendor = Build.MANUFACTURER;
                    ringtone.play();
                    // for samsung S3, we meet a bug that the phone will
                    // continue ringtone without stop
                    // so add below special handler to stop it after 3s if
                    // needed
                    if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                        Thread ctlThread = new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    if (ringtone.isPlaying()) {
                                        ringtone.stop();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        };
                        ctlThread.run();
                    }
                }
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
    }


    /**
     * 设置NotificationInfoProvider
     * 
     * @param provider
     */
    public void setNotificationInfoProvider(NotificationInfoProvider provider) {
        notificationInfoProvider = provider;
    }

    public interface NotificationInfoProvider {
        /**
         * 设置发送notification时状态栏提示新消息的内容(比如Xxx发来了一条图片消息)
         * 
         * @param message
         *            接收到的消息
         * @return null为使用默认
         */
        String getDisplayedText(ModelChatUserList message);

        /**
         * 设置notification持续显示的新消息提示(比如2个联系人发来了5条消息)
         * 
         * @param message
         *            接收到的消息
         * @param fromUsersNum
         *            发送人的数量
         * @param messageNum
         *            消息数量
         * @return null为使用默认
         */
        String getLatestText(ModelChatUserList message, int fromUsersNum, int messageNum);

        /**
         * 设置notification标题
         * 
         * @param message
         * @return null为使用默认
         */
        String getTitle(ModelChatUserList message);

        /**
         * 设置小图标
         * 
         * @param message
         * @return 0使用默认图标
         */
        int getSmallIcon(ModelChatUserList message);

        /**
         * 设置notification点击时的跳转intent
         * 
         * @param message
         *            显示在notification上最近的一条消息
         * @return null为使用默认
         */
        Intent getLaunchIntent(ModelChatUserList message);
    }
}
