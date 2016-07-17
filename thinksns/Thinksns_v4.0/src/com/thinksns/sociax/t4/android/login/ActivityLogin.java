package com.thinksns.sociax.t4.android.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.db.UserSqlHelper;
import com.thinksns.sociax.t4.android.ActivityHome;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.ThinksnsActivity;

import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.t4.android.function.FunctionThirdPlatForm;
import com.thinksns.sociax.t4.android.temp.T4ForgetPasswordActivity;
import com.thinksns.sociax.t4.component.SmallDialog;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.sociax.unit.SociaxUIUtils;

import org.w3c.dom.Text;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-9-5
 * @version 1.0
 */
public class ActivityLogin extends ThinksnsAbscractActivity implements View.OnClickListener {

	private static final String TAG = "LoginActivity";
	private static Worker thread = null;
	private static DialogHandler dialogHandler = null;
	protected static ActivityHandler handler = null;
	
	private TextView 	img_login_sina, 
						img_login_qq, 
						img_login_weichat;				// 第三方登录的3个图片按钮
	private Button bt_login, bt_forget, bt_register;	// 登录/注册/忘记密码按钮
	private AutoCompleteTextView tv_username;			// 用户账号输入
	private EditText tv_password;						// 用户密码输入
	private ImageView tv_title_left;

	private FunctionThirdPlatForm fc_third;				// 第三方登录功能点
	private SmallDialog dialog;							// 提示信息
	private static ActivityLogin instance;

	public static ActivityLogin getInstance() {
		return instance;
	}

	@Override
	public String getTitleCenter() {
		return "";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.ic_login_x, this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		initIntentData();
		initView();
		initListener();
	}

	/**
	 * 初始化intent
	 */
	private void initIntentData() {
		this.initWorker();
		ShareSDK.initSDK(ActivityLogin.this);
	}

	/**
	 * 初始化监听事件
	 */
	private void initListener() {
		
		// 忘记密码
		bt_forget.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityStack.startActivity(ActivityLogin.this, T4ForgetPasswordActivity.class);
			}
		});
		
		// 执行登录
		bt_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String uname = tv_username.getText().toString().trim();
				String password = tv_password.getText().toString().trim();

				if (TextUtils.isEmpty(uname)
						|| TextUtils.isEmpty(password)) {
					Toast.makeText(ActivityLogin.this, "账户或密码不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}

				if(dialogHandler == null)
					dialogHandler = new DialogHandler();
				if (!dialog.isShowing()) {
					dialog.setContent("请稍后...");
					dialog.show();
				}
				//执行验证请求
				new Api.Oauth().authorize(uname, password, mListener);

			}

		});
		// 注册账号
		bt_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SociaxUIUtils.startActivity(ActivityLogin.this,ActivityRegister.class);
			}
		});

		img_login_sina.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fc_third = new FunctionThirdPlatForm(
						ActivityLogin.this,
						ShareSDK.getPlatform(ActivityLogin.this, SinaWeibo.NAME));
				dialog.show();
				fc_third.doLogin();
				fc_third.setListenerSociax(new ListenerSociax() {

					@Override
					public void onTaskSuccess() {
						// 授权成功之后会显示dialog
						dialog.dismiss();
					}

					@Override
					public void onTaskError() {
						dialog.dismiss();
					}

					@Override
					public void onTaskCancle() {
						// 请求用户资料之取消dialog
						dialog.dismiss();
					}
				});
			}
		});
		img_login_qq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fc_third = new FunctionThirdPlatForm(ActivityLogin.this,ShareSDK.getPlatform(ActivityLogin.this, QZone.NAME));
				dialog.show();
				fc_third.doLogin();
				fc_third.setListenerSociax(new ListenerSociax() {

					@Override
					public void onTaskSuccess() {
						// 授权成功之后会显示dialog
						dialog.show();
					}

					@Override
					public void onTaskError() {
						dialog.dismiss();
					}

					@Override
					public void onTaskCancle() {
						// 请求用户资料之取消dialog
						dialog.dismiss();
					}
				});
			}
		});
		img_login_weichat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fc_third = new FunctionThirdPlatForm(ActivityLogin.this,
						ShareSDK.getPlatform(ActivityLogin.this, Wechat.NAME));
				dialog.show();
				fc_third.doLogin();
				fc_third.setListenerSociax(new ListenerSociax() {

					@Override
					public void onTaskSuccess() {
						// 授权成功之后会显示dialog
						dialog.show();
					}

					@Override
					public void onTaskError() {
						dialog.dismiss();
					}

					@Override
					public void onTaskCancle() {
						// 请求用户资料之取消dialog
						dialog.dismiss();
					}
				});
			}
		});

	}
	
	final HttpResponseListener mListener = new HttpResponseListener() {
		
		@Override
		public void onSuccess(final Object result) {
			if(result instanceof ModelUser) {
				ModelUser authorizeResult = (ModelUser)result;
				//获取个人数据
				new Api.Users().show(authorizeResult, this);
			}else if(result instanceof ListData<?>) {
				ListData<SociaxItem> list = (ListData<SociaxItem>) result;
				if(list != null && list.size() == 1) {
					ModelUser loginedUser = (ModelUser)list.get(0);
					//保存用户信息
					Thinksns.setMy(loginedUser);
					//添加用户信息至数据库
					UserSqlHelper db = UserSqlHelper.getInstance(ActivityHandler.context);
					db.addUser(loginedUser, true);
					String username = loginedUser.getUserName();
					if (!db.hasUname(username))
						db.addSiteUser(username);
					Message errorMessage = new Message();
					errorMessage.arg1 = DialogHandler.AUTH_DOWN;
					dialogHandler.sendMessage(errorMessage);

				}
			}
			
		}
		
		@Override
		public void onError(Object result) {
			dialog.setContent(result.toString());
			Message errorMessage = new Message();
			errorMessage.arg1 = DialogHandler.CLOSE_DIALOG;
			dialogHandler.sendMessageDelayed(errorMessage, 1000);
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_login;
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		tv_password = (EditText) this.findViewById(R.id.password);
		tv_username = (AutoCompleteTextView) this.findViewById(R.id.email);
		bt_forget = (Button) findViewById(R.id.bt_login_forget);
		bt_register = (Button) this.findViewById(R.id.register);
		bt_login = (Button) this.findViewById(R.id.login);
		
		img_login_sina = (TextView) findViewById(R.id.img_login_sina);
		img_login_qq = (TextView) findViewById(R.id.img_login_qq);
		img_login_weichat = (TextView) findViewById(R.id.img_login_weichat);
		tv_title_left = (ImageView) findViewById(R.id.tv_title_left);
		//填充历史登录账号
		UserSqlHelper db = UserSqlHelper.getInstance(ActivityHandler.context);
		tv_username.setAdapter(new ArrayAdapter<String>(this,R.layout.account_item, db.getUnameList()));
		
		//初始化提示框，默认点击外部可以消失
		dialog = new SmallDialog(this, "请稍后...");
		dialog.setCanceledOnTouchOutside(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		Util.getSharePersistentBoolean(getApplicationContext(), "AUTH_STATE");
	}

	private void initWorker() {
		thread = new Worker((Thinksns) this.getApplicationContext(),
				"Auth User");
		handler = new ActivityHandler(thread.getLooper(), this);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private final class DialogHandler extends Handler {
		public static final int AUTH_ERROR = 0;
		public static final int CLOSE_DIALOG = 1;
		public static final int AUTH_DOWN = 2;
		public static final int SINA_LOGIN = 3;
		public static final int FOR_REG = 4;
		
		public DialogHandler() {
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case AUTH_ERROR:
//				sendEmptyMessageDelayed(CLOSE_DIALOG, 500);
				break;
			case CLOSE_DIALOG:
				dialog.dismiss();
				break;
			case AUTH_DOWN:
				dialog.dismiss();
				Intent intent = new Intent(ActivityLogin.this,
						ActivityHome.class);
				startActivity(intent);
				Anim.in(ActivityLogin.this);
				//关闭首页
				ThinksnsActivity.getInstance().finish();
				ActivityLogin.this.finish();
				break;
			}
		}
	}

	private static final class ActivityHandler extends Handler {
		private static final long SLEEP_TIME = 2000;
		private static Context context = null;

		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			ActivityHandler.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Thinksns app = thread.getApp();
			Api.Sites siteLog = app.getSites();
			Message errorMessage = new Message();
			Message errorStatus = new Message();
			boolean isSuport = false;
			try {
				switch (msg.what) {
				case 2:
					try {
						isSuport = siteLog.isSupport();
					} catch (ListAreEmptyException e) {
						e.printStackTrace();
					}
					errorMessage.arg1 = DialogHandler.SINA_LOGIN;
					errorMessage.arg2 = isSuport ? 1 : 0;
					dialogHandler.sendMessage(errorMessage);
					break;
				case 3:
					try {
						isSuport = siteLog.isSupportReg();
					} catch (ListAreEmptyException e) {
						e.printStackTrace();
					}
					errorMessage.arg1 = DialogHandler.FOR_REG;
					errorMessage.arg2 = isSuport ? 1 : 0;
					dialogHandler.sendMessage(errorMessage);
					break;
				}
			} catch (DataInvalidException e) {
				errorMessage.obj = e.getMessage();
				errorMessage.arg1 = DialogHandler.AUTH_ERROR;
				dialogHandler.sendMessage(errorMessage);
				thread.sleep(SLEEP_TIME);
				errorStatus.arg1 = DialogHandler.CLOSE_DIALOG;
				dialogHandler.sendMessage(errorStatus);
			} catch (VerifyErrorException e) {
				errorMessage.obj = e.getMessage();
				errorMessage.arg1 = DialogHandler.AUTH_ERROR;
				dialogHandler.sendMessage(errorMessage);
				thread.sleep(SLEEP_TIME);
				errorStatus.arg1 = DialogHandler.CLOSE_DIALOG;
				dialogHandler.sendMessage(errorStatus);
			} catch (ApiException e) {
				errorMessage.obj = e.getMessage();
				errorMessage.arg1 = DialogHandler.AUTH_ERROR;
				dialogHandler.sendMessage(errorMessage);
				thread.sleep(SLEEP_TIME);
				errorStatus.arg1 = DialogHandler.CLOSE_DIALOG;
				dialogHandler.sendMessage(errorStatus);
			}
		}

	}


    
	@Override
	public void onClick(View v) {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}