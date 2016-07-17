package com.thinksns.sociax.t4.android.function;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.component.SmallDialog;
import com.thinksns.sociax.t4.unit.UnitSociax;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明： 获取验证码,
 * 
 * @author wz
 * @date 2014-9-9
 * @version 1.0
 * 
 */
public class FunctionVerifyCode {
	public static final String TAG = "FunctionVerifyCode";
	private int FAILED = 0;// 失败
	private int WITE = 201402;// 等待
	private int SUCCESS = 1;// 请求中

	private int GET_CODE = 201404;// 标记请求返回类型：请求验证码返回
	private int OAUTH_CODE = 201405;// 请求验证验证码返回
	private int OAUTH_SUCCESS = 201406;// 请求中
	private Activity context;
	private String phoneNumber;
	private String oauthNum;
	private Timer timer;
	private int time = 60;
	private String alter = "秒后重新获取";
	private UIHandler uihandler;
	private TextView tv_get;
	private EditText tv_phone, tv_edoauth;
	private SmallDialog smallDialog;

	private boolean isSuccess = false;

	/**
	 * 进行验证使用这个构造对象
	 * 
	 * @param PhoneNum
	 *            电话号码 oauthNumber 验证码
	 */
	public FunctionVerifyCode(EditText ed_phoneNum, EditText ed_oauthNumber,
			boolean isSuccess, Activity context) {
		this.tv_phone = ed_phoneNum;
		this.tv_edoauth = ed_oauthNumber;
		this.isSuccess = isSuccess;
		this.context = context;
		uihandler = new UIHandler();
	}

	/**
	 * 获取验证码
	 * 
	 * @param context
	 * @param tv_phone
	 *            输入电话框
	 * @param tv_get_button
	 *            点击获取验证码按钮
	 */
	public FunctionVerifyCode(Activity context, EditText tv_phone,
			TextView tv_get_button) {
		this.context = context;
		this.tv_phone = tv_phone;
		this.tv_get = tv_get_button;
		uihandler = new UIHandler();
		smallDialog = new SmallDialog(context, "请稍后...");
		smallDialog.setCanceledOnTouchOutside(false);
	}

	/**
	 * 检测是否正规手机号
	 * 
	 * @param phoneNumber
	 * @return true 合法手机号，false 非法手机号
	 */
	public boolean checkPhoneNumber() {
		phoneNumber = tv_phone.getText().toString();
		if (phoneNumber.trim().length() == 0|| phoneNumber.trim().length() != 11) {
			Toast.makeText(context, R.string.reg_re_phone_unvaild,Toast.LENGTH_SHORT).show();
			return false;
		}
//		// 正则，除了13，15，18开头的其他为非法
//		String regExp = "^(13|15|18)\\d{9}$";
//		Pattern p = Pattern.compile(regExp);
//		Matcher m = p.matcher(phoneNumber);
//		if (!m.find()) {
//			Toast.makeText(context, R.string.reg_re_phone_unvaild,Toast.LENGTH_SHORT).show();
//			return false;
//		}
		return true;
	}

	/**
	 * 根据手机号码获取注册验证码
	 * 
	 * @param phoneNumber
	 */
	public int getRegisterVerify() {
		timer = new Timer();
		if(!smallDialog.isShowing()) {
			smallDialog.setContent("请稍后...");
			smallDialog.show();
		}
		// 启动定时器 执行定时任务， 启动时间是1s后，每隔1s执行一次
		new Thread(new Runnable() {
			@Override
			public void run() {
				Thinksns app = (Thinksns) context.getApplication();
				try {
					Message msg = uihandler.obtainMessage();
					msg.arg1 = GET_CODE;
					JSONObject jo = new JSONObject(app.getOauth().getRegisterVerifyCode(phoneNumber).toString());
					
					msg.what = jo.getInt("status"); // //成功：1 失败：0
					msg.obj = jo.getString("msg"); // 邮箱不合格：2
					uihandler.sendMessage(msg);
				} catch (Exception e) {
					Log.d(this.getClass().toString(), e.toString());

				}
			}
		}).start();

		return FAILED;
	}

	/**
	 * 根据手机号码获取找回密码验证码
	 * 
	 * @param phoneNumber
	 */
	public int getFindBackVerify() {
		timer = new Timer();
		// 启动定时器 执行定时任务， 启动时间是1s后，每隔1s执行一次
		new Thread(new Runnable() {
			@Override
			public void run() {
				Thinksns app = (Thinksns) context.getApplication();
				try {
					Message msg = uihandler.obtainMessage();
					msg.arg1 = GET_CODE;
					
					JSONObject jo = new JSONObject(app.getOauth().getFindVerifyCode(phoneNumber).toString());
					msg.what = jo.getInt("status");  //成功：1 失败：0   邮箱不合格：2
					msg.obj = jo.getString("message"); 
					uihandler.sendMessage(msg);
				} catch (Exception e) {
					Log.d(this.getClass().toString(), e.toString());
				}
			}
		}).start();

		return FAILED;
	}
	/**
	 * 根据手机号码获取注册验证码
	 * 
	 * @param phoneNumber
	 */
	public int getBindVerify() {
		timer = new Timer();
		// 启动定时器 执行定时任务， 启动时间是1s后，每隔1s执行一次

		new Thread(new Runnable() {
			@Override
			public void run() {
				Thinksns app = (Thinksns) context.getApplication();
				try {
					Message msg = uihandler.obtainMessage();
					msg.arg1 = GET_CODE;
					JSONObject jo = new JSONObject(app.getUsers()
							.getBindVerifyCode(phoneNumber).toString());
					msg.what = jo.getInt("status"); // //成功：1 失败：0
					msg.obj = jo.getString("msg"); // 邮箱不合格：2
					uihandler.sendMessage(msg);
				} catch (Exception e) {
					Log.d(this.getClass().toString(), e.toString());
				}
			}
		}).start();

		return FAILED;
	}
	/**
	 * 验证 输入的验证码
	 * 
	 * @return
	 */
	public boolean checkVerifyCode() {
		phoneNumber = tv_phone.getText().toString();
		oauthNum = tv_edoauth.getText().toString();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Thinksns app = (Thinksns) context.getApplication();
				try {
					Message msg = uihandler.obtainMessage();
					msg.arg1 = OAUTH_CODE;
					JSONObject jo = new JSONObject(app.getOauth()
							.oauthRegisterVerifyCode(phoneNumber, oauthNum)
							.toString());
					msg.what = jo.getInt("status"); // //成功：1 失败：0
					msg.obj = jo.getString("msg"); // 邮箱不合格：2
					uihandler.sendMessage(msg);
				} catch (Exception e) {
					Log.d(this.getClass().toString(), e.toString());
				}
			}
		}).start();
		return isSuccess;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * 重复提醒
	 * 
	 * @author wz
	 * 
	 */
	class RemindTask extends TimerTask {
		@Override
		public void run() {
			--time;
			Message msg = uihandler.obtainMessage();
			msg.arg1 = WITE;
			msg.sendToTarget();
		}
	}

	/**
	 * 电话注册
	 * 
	 * @author wz
	 * 
	 */
	class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			int tenDip=UnitSociax.dip2px(context, 10);
			int sixDip=UnitSociax.dip2px(context, 6);
			
			if (msg.arg1 == WITE) {
				tv_get.setText(time + alter);
				if (time == 0) {
					timer.cancel();
					time = 60;
					tv_get.setPadding(tenDip, sixDip, tenDip, sixDip);
					tv_get.setBackgroundResource(R.drawable.tv_getverify_on);
					tv_get.setText(context.getResources().getString(R.string.getverifycode_name));
					tv_get.setClickable(true);
				}
			} else if (msg.arg1 == GET_CODE) {
				if (msg.what == SUCCESS) {// 发送成功
					Toast.makeText(context, R.string.reg_sendvarify_success,
							Toast.LENGTH_SHORT).show();
					Anim.exit(context);
					timer.schedule(new RemindTask(), 100, 1000);
					tv_get.setText(time + alter);
					tv_get.setPadding(tenDip, sixDip, tenDip, sixDip);
					tv_get.setBackgroundResource(R.drawable.tv_getverify_off);
					tv_get.setClickable(false);
					setSuccess(true);
				} else {
					// 发送失败则显示失败原因
					if (timer != null)
						timer.cancel();
						tv_get.setClickable(true);
						tv_get.setPadding(tenDip, sixDip, tenDip, sixDip);
//						tv_get.setBackgroundResource(R.drawable.tv_getverify_on);
						tv_get.setText( context.getResources().getString(R.string.getverifycode_name));
					Toast.makeText(context, msg.obj.toString(),Toast.LENGTH_SHORT).show();
					setSuccess(false);
				}

				smallDialog.dismiss();
			} else if (msg.arg1 == OAUTH_CODE) {
					setSuccess(true);
					tv_get.setPadding(tenDip, sixDip, tenDip, sixDip);
				} else {
					Toast.makeText(context, msg.obj.toString(),Toast.LENGTH_SHORT).show();
					setSuccess(false);
					tv_get.setPadding(tenDip, sixDip, tenDip, sixDip);
				}
			}
		}
}
