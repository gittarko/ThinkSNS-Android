package com.thinksns.sociax.t4.android.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.function.FunctionThirdPlatForm;
import com.thinksns.sociax.t4.model.ModelBindItem;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

import org.json.JSONObject;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 类说明：账号管理类
 * 
 * @author wz
 * @date 2014-9-4
 * @version 1.0
 */
public class ActivityManageCount extends ThinksnsAbscractActivity {
	private static Handler handler;
	private ActivityHandler handlerActivity;// 处理耗时操作
	private ImageView tv_title_left;
	private RelativeLayout rl_change_pwd, rl_bind_phone, rl_bind_sina,
			rl_bind_qq, rl_bind_weichat;
	TextView tv_bind_phone, tv_bind_sina, tv_bind_qq, tv_bind_weichat;
	private ScrollView sv_content;
	private boolean isBindQQ = false, isBindSina = false,
			isBindWeichat = false, isBindPhone = false;
	private static Worker thread = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		initIntentData();
		initView();
		initListener();
		initData();
	}

	/**
	 * 载入数据
	 */
	private void initData() {
		getBindInfoTask();
	}

	private void getBindInfoTask() {
		loadingView.show(sv_content);
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = StaticInApp.GET_USER_BIND;
				try {
					msg.obj = ((Thinksns) getApplicationContext()).getUsers()
							.getUserBindInfo();
				} catch (ApiException e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}).start();

	}

	/**
	 * 初始化监事件
	 */
	private void initListener() {
		rl_bind_phone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isBindPhone){
					unbindTask("phone");
				}else{
					Intent intent = new Intent(ActivityManageCount.this,ActivityBindPhone.class);
					startActivityForResult(intent, StaticInApp.BIND_OTHER_PHONE);
				}
			}
		});
		
		
		tv_title_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		rl_change_pwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ActivityManageCount.this,ActivityChangePassword.class);
				startActivity(i);
			}
		});

		tv_bind_qq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isBindQQ) {// 解绑
					unbindTask("qzone");
				} else {// 绑定
					FunctionThirdPlatForm fc_qq = new FunctionThirdPlatForm(
							ActivityManageCount.this, ShareSDK.getPlatform(
									ActivityManageCount.this, QQ.NAME));
					// 设置好监听事件
					fc_qq.setListenerSociax(new ListenerSociax() {
						@Override
						public void onTaskSuccess() {
							Platform weibo = ShareSDK.getPlatform(
									ActivityManageCount.this, QQ.NAME);
							String accessToken = weibo.getDb().getToken(); // 获取授权token
							String openId = weibo.getDb().getUserId(); // 获取用户在此平台的ID
							String nickname = weibo.getDb().get("nickname"); // 获取用户昵称
							Log.v("ActivityManageCount", "wztest "
									+ accessToken + " " + openId + "  "
									+ nickname);
							// 接下来执行您要的操作
							bindTask("qzone", openId, accessToken);
						}

						@Override
						public void onTaskError() {
						}

						@Override
						public void onTaskCancle() {
						}
					});
					fc_qq.doBindOauth();
				}
			}
		});
		tv_bind_weichat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isBindWeichat) {
					unbindTask("weixin");
				} else {
					FunctionThirdPlatForm fc_wechat = new FunctionThirdPlatForm(
							ActivityManageCount.this, ShareSDK.getPlatform(
									ActivityManageCount.this, Wechat.NAME));
					// 设置好监听事件
					fc_wechat.setListenerSociax(new ListenerSociax() {
						@Override
						public void onTaskSuccess() {
							Platform weibo = ShareSDK.getPlatform(
									ActivityManageCount.this, Wechat.NAME);
							String accessToken = weibo.getDb().getToken(); // 获取授权token
							String openId = weibo.getDb().getUserId(); // 获取用户在此平台的ID
							String nickname = weibo.getDb().get("nickname"); // 获取用户昵称
							Log.v("ActivityManageCount", "wztest "
									+ accessToken + " " + openId + "  "
									+ nickname);
							// 接下来执行您要的操作
							bindTask("weixin", openId, accessToken);
						}

						@Override
						public void onTaskError() {
						}

						@Override
						public void onTaskCancle() {
						}
					});
					fc_wechat.doBindOauth();
				}
			}
		});
		tv_bind_sina.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isBindSina) {
					unbindTask("sina");
				} else {
					FunctionThirdPlatForm fc_wechat = new FunctionThirdPlatForm(
							ActivityManageCount.this, ShareSDK.getPlatform(
									ActivityManageCount.this, SinaWeibo.NAME));
					// 设置好监听事件
					fc_wechat.setListenerSociax(new ListenerSociax() {
						@Override
						public void onTaskSuccess() {
							Platform weibo = ShareSDK.getPlatform(
									ActivityManageCount.this, SinaWeibo.NAME);
							String accessToken = weibo.getDb().getToken(); // 获取授权token
							String openId = weibo.getDb().getUserId(); // 获取用户在此平台的ID
							String nickname = weibo.getDb().get("nickname"); // 获取用户昵称
							Log.v("ActivityManageCount", "wztest "
									+ accessToken + " " + openId + "  "
									+ nickname);
							// 接下来执行您要的操作
							bindTask("sina", openId, accessToken);
						}
						@Override
						public void onTaskError() {
						}
						@Override
						public void onTaskCancle() {
						}
					});
					fc_wechat.doBindOauth();
				}
			}
		});

	}

	/**
	 * 解绑操作
	 * @param type
	 */
	private void unbindTask(String type) {
		Message msg = new Message();
		msg.what = type.equals("qzone") ? StaticInApp.UNBIND_OTHER_QQ : type
				.equals("sina") ? StaticInApp.UNBIND_OTHER_SINA
				: type.equals("phone")?StaticInApp.UNBIND_OTHER_PHONE:StaticInApp.UNBIND_OTHER_WEICHAT;
		handlerActivity.sendMessage(msg);
	}

	/**
	 * 
	 * @param type
	 *            qzone/sina/weixin
	 * @param openId
	 */
	private void bindTask( String type,  String openId,
			String token) {
		Message msg = new Message();
		msg.what = type.equals("qzone") ? StaticInApp.BIND_OTHER_QQ
				: type.equals("sina") ? StaticInApp.BIND_OTHER_SINA
						: StaticInApp.BIND_OTHER_WEICHAT;
		String []obj=new String [3];
		obj[0]=type;
		obj[1]=openId;
		obj[2]=token;
		msg.obj=obj;
		handlerActivity.sendMessage(msg);
	}

	/**
	 * 初始化intent信息
	 */
	private void initIntentData() {
		ShareSDK.initSDK(this);// 初始化ShareSdk必须有这个操作
	}

	/**
	 * 初始化页面
	 */
	private void initView() {
		tv_title_left = (ImageView) findViewById(R.id.tv_title_left);
		rl_change_pwd = (RelativeLayout) findViewById(R.id.rl_change_pwd);

		rl_bind_phone = (RelativeLayout) findViewById(R.id.rl_bind_phone);
		rl_bind_qq = (RelativeLayout) findViewById(R.id.rl_bind_qq);
		rl_bind_sina = (RelativeLayout) findViewById(R.id.rl_bind_sina);
		rl_bind_weichat = (RelativeLayout) findViewById(R.id.rl_bind_weichat);

		tv_bind_phone = (TextView) findViewById(R.id.tv_bind_phone);
		tv_bind_qq = (TextView) findViewById(R.id.tv_bind_qq);
		tv_bind_sina = (TextView) findViewById(R.id.tv_bind_sina);
		tv_bind_weichat = (TextView) findViewById(R.id.tv_bind_weichat);

		sv_content = (ScrollView) findViewById(R.id.sv_content);
		thread = new Worker((Thinksns) this.getApplicationContext(),
				"ManageCount");
		handlerActivity = new ActivityHandler(thread.getLooper(), this);
		handler = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case StaticInApp.GET_USER_BIND://获取用户绑定信息列表
					if (msg.obj != null) {
						ListData<SociaxItem> listBindInfo = (ListData<SociaxItem>) msg.obj;
						for (int i = 0; i < listBindInfo.size(); i++) {
							ModelBindItem mdi = (ModelBindItem) listBindInfo
									.get(i);
							if (mdi.getType().equals("phone")) {
								setPhoneBindUI(mdi);
							} else if (mdi.getType().equals("sina")) {
								setSinaBindUI(mdi);
							} else if (mdi.getType().equals("qzone")) {
								setQQBindUI(mdi);
							} else if (mdi.getType().equals("weixin")) {
								setWeichatBindUI(mdi);
							}
						}
					} else {
					}
					break;
				case StaticInApp.BIND_OTHER_QQ://绑定QQ
					if (msg.obj != null) {
						try {
							JSONObject data = new JSONObject(msg.obj.toString());
							
							if (data.getInt("status")==1) {
								changeBindUI(tv_bind_qq, true);
								isBindQQ = true;
							}
							Toast.makeText(getApplicationContext(),
									data.getString("msg"), Toast.LENGTH_SHORT)
									.show();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
					}
					break;
				case StaticInApp.BIND_OTHER_PHONE://绑定手机
					if (msg.obj != null) {
						try {
							JSONObject data = new JSONObject(msg.obj.toString());
							if (data.getInt("status") == 1) {
								changeBindUI(tv_bind_phone, true);
								isBindPhone = true;
							}
							Toast.makeText(getApplicationContext(),
									data.getString("msg"), Toast.LENGTH_SHORT)
									.show();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
					}
					break;
				case StaticInApp.BIND_OTHER_SINA://绑定新浪微博
					if (msg.obj != null) {
						try {
							JSONObject data = new JSONObject(msg.obj.toString());
							if (data.getInt("status") == 1) {
								changeBindUI(tv_bind_sina, true);
								isBindSina = true;
							}
							Toast.makeText(getApplicationContext(),
									data.getString("msg"), Toast.LENGTH_SHORT)
									.show();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
					}
					break;
				case StaticInApp.BIND_OTHER_WEICHAT://绑定微信
					if (msg.obj != null) {
						try {
							JSONObject data = new JSONObject(msg.obj.toString());
							if (data.getInt("status") == 1) {
								changeBindUI(tv_bind_weichat, true);
								isBindWeichat = true;
							}
							Toast.makeText(getApplicationContext(),
									data.getString("msg"), Toast.LENGTH_SHORT)
									.show();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
					}
					break;

				case StaticInApp.UNBIND_OTHER_QQ://解除QQ绑定
					if (msg.obj != null) {
						try {
							JSONObject data = new JSONObject(msg.obj.toString());
							if (data.getInt("status") == 1) {
								changeBindUI(tv_bind_qq, false);
								isBindQQ = false;
							}
							Toast.makeText(getApplicationContext(),
									data.getString("msg"), Toast.LENGTH_SHORT)
									.show();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
					}
					break;
				case StaticInApp.UNBIND_OTHER_PHONE:
					if (msg.obj != null) {
						try {
							JSONObject data = new JSONObject(msg.obj.toString());
							if (data.getInt("status") == 1) {
								changeBindUI(tv_bind_phone, false);
								isBindPhone = false;
							}
							Toast.makeText(getApplicationContext(),
									data.getString("msg"), Toast.LENGTH_SHORT)
									.show();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
					}
					break;
				case StaticInApp.UNBIND_OTHER_SINA:
					if (msg.obj != null) {
						try {
							JSONObject data = new JSONObject(msg.obj.toString());
							if (data.getInt("status") == 1) {
								changeBindUI(tv_bind_sina, false);
								isBindSina = false;
							}
							Toast.makeText(getApplicationContext(),data.getString("msg"), Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
					}
					break;
				case StaticInApp.UNBIND_OTHER_WEICHAT:
					if (msg.obj != null) {
						try {
							JSONObject data = new JSONObject(msg.obj.toString());
							if (data.getInt("status")==1) {
								changeBindUI(tv_bind_weichat, false);
								isBindWeichat = false;
							}
							Toast.makeText(getApplicationContext(),
									data.getString("msg"), Toast.LENGTH_SHORT)
									.show();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
					}
					break;
					
				}
				loadingView.hide(sv_content);
			}
		};
	}

	/**
	 * 微信绑定情况
	 * 
	 * @param mdi
	 */
	protected void setWeichatBindUI(ModelBindItem mdi) {
		isBindWeichat = mdi.isBind();
		changeBindUI(tv_bind_weichat, mdi.isBind());
	}

	/**
	 * QQ绑定情况
	 * 
	 * @param mdi
	 */
	protected void setQQBindUI(ModelBindItem mdi) {
		isBindQQ = mdi.isBind();
		changeBindUI(tv_bind_qq, mdi.isBind());
	}

	/**
	 * 新浪绑定情况
	 * 
	 * @param mdi
	 */
	protected void setSinaBindUI(ModelBindItem mdi) {
		isBindSina = mdi.isBind();
		changeBindUI(tv_bind_sina, mdi.isBind());
	}

	/**
	 * 手机绑定情况
	 * 
	 * @param mdi
	 */
	protected void setPhoneBindUI(ModelBindItem mdi) {
		isBindPhone = mdi.isBind();
		changeBindUI(tv_bind_phone, mdi.isBind());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public String getTitleCenter() {
		return null;
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return null;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_count;
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
			Thinksns app = (Thinksns) context.getApplicationContext();
			
			Message msg1 = new Message();
			switch (msg.what) {
			case StaticInApp.UNBIND_OTHER_QQ:
				msg1.what = msg.what;
				msg1.obj = app.getUsers().unbindOther("qzone");
				break;
			case StaticInApp.UNBIND_OTHER_PHONE:
				msg1.what = msg.what;
				msg1.obj = app.getUsers().unbindOther("phone");
				break;
			case StaticInApp.UNBIND_OTHER_SINA:
				msg1.what = msg.what;
				msg1.obj = app.getUsers().unbindOther("sina");
				break;
			case StaticInApp.UNBIND_OTHER_WEICHAT:
				msg1.what = msg.what;
				msg1.obj = app.getUsers().unbindOther("weixin");
				break;
				
			case StaticInApp.BIND_OTHER_QQ:
				msg1.what = msg.what;
				String[]info1=(String[]) msg.obj;
				msg1.obj = app.getUsers().bindOther(info1[0],info1[1],info1[2]);
				break;
			case StaticInApp.BIND_OTHER_SINA:
				msg1.what = msg.what;
				String[]info2=(String[]) msg.obj;
				msg1.obj = app.getUsers().bindOther(info2[0],info2[1],info2[2]);
				break;
			case StaticInApp.BIND_OTHER_WEICHAT:
				msg1.what = msg.what;
				String[]info3=(String[]) msg.obj;
				msg1.obj = app.getUsers().bindOther(info3[0],info3[1],info3[2]);
				break;
			}
			handler.sendMessage(msg1);
		}
	}
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg0==StaticInApp.BIND_OTHER_PHONE){
			if(arg1==RESULT_OK&&arg2.hasExtra("input")){
				isBindPhone=true;
				changeBindUI(tv_bind_phone, true);
			}
		}
	}

	private void changeBindUI(TextView view, boolean isBind) {
		if (!isBind) {
			view.setText("绑定");
		} else {
			view.setText("解绑");
		}
	}
}
