package com.thinksns.sociax.t4.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.thinksns.sociax.db.UserSqlHelper;
import com.thinksns.sociax.net.Request;
import com.thinksns.sociax.t4.android.ActivityHome;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.exception.UserDataInvalidException;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 类说明：即时聊天极光推送
 * 
 * @author Zoey
 * @date 2015-12-29
 * @version 1.0
 */
public class JpushBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "JPUSHRECEIVER";
	private static final String PACKAGE_NAME = "com.thinksns.sociax.android";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {// 自定义消息不会展示在通知栏，完全要开发者写代码去处理

			Log.e("Thinksns", "jpush 收到推送消息title：" +bundle.getString(JPushInterface.EXTRA_TITLE)+"/message/"
					+ bundle.getString(JPushInterface.EXTRA_MESSAGE)+"/extras /"+bundle.getString(JPushInterface.EXTRA_EXTRA)
					+"/content_type /"+bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE)
					+"/msgID/"+bundle.getString(JPushInterface.EXTRA_MSG_ID));

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {// 在这里可以做些统计，或者做些其他工作

			Log.e("Thinksns", "jpush 收到通知title：" +bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE)+"/message/"
					+ bundle.getString(JPushInterface.EXTRA_MESSAGE)+"/extras /"+bundle.getString(JPushInterface.EXTRA_EXTRA)
					+"/content_type /"+bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE)
					+"/ID/"+bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)+"/msgID/"+bundle.getString(JPushInterface.EXTRA_MSG_ID));

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

			String result = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Log.e("Thinksns", "jpush 用户打开了通知result:"+result);
			boolean isChat = false;
			try{
				JSONObject jsonObject = new JSONObject(result);
				if(jsonObject.has("push_type") && jsonObject.getString("push_type").equals("message")) {
					isChat = true;
				}
			}catch (JSONException e) {
				e.printStackTrace();
			}
			//判断app进程是否存活
			if(UnitSociax.isAppAlive(context, PACKAGE_NAME)){
				Intent mainIntent = new Intent(context, ActivityHome.class);
				//将MainAtivity的launchMode设置成SingleTask, 或者在下面flag中加上Intent.FLAG_CLEAR_TOP,
				//如果Task栈中有MainActivity的实例，就会把它移到栈顶，把在它之上的Activity都清理出栈，
				//如果Task栈不存在MainActivity实例，则在栈顶创建
				mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if(isChat) {
					mainIntent.putExtra("type", "message");
				}
				context.startActivity(mainIntent);
			}else {
				//如果app进程已经被杀死，先重新启动app
				Log.i(TAG, "the app process is dead");
				Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
				launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				if(isChat) {
					launchIntent.putExtra("type", "message");
				}

				context.startActivity(launchIntent);
			}
		} else {
			Log.d(TAG,"jpush =============Unhandled intent - "+ intent.getAction());
		}
	}

	public boolean HasLoginUser(Context context) {
		UserSqlHelper db = ((Thinksns)context.getApplicationContext()).getUserSql();
		try {
			if (db!=null){
				ModelUser user = db.getLoginedUser();
				Request.setToken(user.getToken());
				Request.setSecretToken(user.getSecretToken());
				Thinksns.setMy(user);

				Log.v("ActivityHome","/uid/"+user.getUid()+"/getUid/"+Thinksns.getMy().getUid());
				return true;
			}
			return false;
		} catch (UserDataInvalidException e) {
			return false;
		}
	}
}
