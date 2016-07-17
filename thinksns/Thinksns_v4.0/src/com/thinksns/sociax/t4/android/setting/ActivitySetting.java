package com.thinksns.sociax.t4.android.setting;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ActivityHome;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.ThinksnsActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.db.SQLHelperChatMessage;
import com.thinksns.sociax.t4.android.interfaces.OnPopupWindowClickListener;
import com.thinksns.sociax.t4.android.popupwindow.PopUpWindowAlertDialog;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowCommon;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowListDialog;
import com.thinksns.sociax.t4.android.user.ActivityUserBlackList;
import com.thinksns.sociax.t4.sharesdk.ShareSDKManager;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.tschat.chat.TSChatManager;

import org.json.JSONObject;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 类说明：设置
 * 
 * @author wz
 * @date 2014-8-29
 * @version 1.0
 */
public class ActivitySetting extends ThinksnsAbscractActivity {
	public static String 	TAG = "ActivitySetting";
	public final int GET_USER_PRIVACY = 102;
	private Thinksns 		app;
	private ResultHandler 	resultHandler;
	private RelativeLayout 	rl_count_manage, rl_bout, rl_feedBack,
							rl_privacy_manage, rl_blacklist,
							rl_auto_play_inwifi,rl_clear_message;
	private CheckBox 		cb_receive_new_count, cb_auto_play_inwifi;
	private TextView 		tv_cache_size;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initListener();
		initData();
	}

	private void initData() {
		UnitSociax unit = new UnitSociax(ActivitySetting.this);
		tv_cache_size.setText(unit.getCacheSize());
	}

	private void initListener() {
//		隐私设置
		rl_privacy_manage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivitySetting.this,ActivityManagePrivacy.class);
				startActivity(intent);
			}
		});
		//黑名单
		rl_blacklist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivitySetting.this,ActivityUserBlackList.class);
				startActivity(intent);
			}
		});

		//退出登录
		findViewById(R.id.btn_exit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopUpWindowAlertDialog.Builder builder = new PopUpWindowAlertDialog.Builder(v.getContext());
				builder.setMessage("确认退出?", 18);
				builder.setTitle(null, 0);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//关闭socket
						TSChatManager.close();
						Bundle data = new Bundle();
						data.putBoolean("login_out", true);
						ActivityStack.startActivity(ActivitySetting.this,ThinksnsActivity.class, data);
						//注销极光推送
						ShareSDKManager.unregister();
						finish();
					}
				});

				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				builder.create();
			}
		});

		findViewById(R.id.ll_clear_cache).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						PopUpWindowAlertDialog.Builder builder = new PopUpWindowAlertDialog.Builder(v.getContext());
						builder.setMessage("是否清理缓存?", 18);
						builder.setTitle(null, 0);
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								clearAppCache();
							}
						});

						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});
						builder.create();
					}
				});

		rl_count_manage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityStack.startActivity(ActivitySetting.this,
						ActivityManageCount.class);
			}
		});
		//关于我们
		rl_bout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityStack.startActivity(ActivitySetting.this, ActivitySettingAboutUs.class);
			}
		});
		rl_feedBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ActivitySetting.this,
						ActivityFeedBack.class);
				i.putExtra("type", "suggest");
				startActivity(i);
				Anim.in(ActivitySetting.this);
			}
		});
		rl_clear_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopupWindowCommon pup = new PopupWindowCommon(ActivitySetting.this, v, "确定要删除所有记录", "确定", "再想想");
				pup.setOnPopupWindowClickListener(new OnPopupWindowClickListener() {

					@Override
					public void secondButtonClick() {
					}

					@Override
					public void firstButtonClick() {
						Thinksns app = (Thinksns) getApplicationContext();
						boolean isSuccess = app.getSQLHelperChatMessage().clearMyChatList();
						if (isSuccess) {
							Toast.makeText(getApplicationContext(), "清除成功",Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
	}

	private void initView() {
		app = (Thinksns) getApplicationContext();
		resultHandler = new ResultHandler(this);
		rl_privacy_manage = (RelativeLayout) findViewById(R.id.rl_privacy_manage);
		rl_feedBack = (RelativeLayout) findViewById(R.id.rl_feedback);
		rl_count_manage = (RelativeLayout) findViewById(R.id.ll_manage_count);
		rl_bout = (RelativeLayout) findViewById(R.id.rl_about_us);
		rl_blacklist = (RelativeLayout) findViewById(R.id.rl_blacklist);
		cb_receive_new_count = (CheckBox) findViewById(R.id.cb_receive_new_count);
		rl_auto_play_inwifi = (RelativeLayout) findViewById(R.id.rl_auto_play_inwifi);
		cb_auto_play_inwifi = (CheckBox) findViewById(R.id.cb_auto_play_inwifi);
		rl_clear_message = (RelativeLayout) findViewById(R.id.rl_clear_message);
		tv_cache_size = (TextView) findViewById(R.id.tv_cache_size);
	}

	/**
	 * 隐私设置，
	 * @param o
	 */
	private void setPrivacyData(Object o) {
		try {
			JSONObject jo = new JSONObject(o.toString());
			if (jo.getString("space").equals("1")) {
				cb_receive_new_count.setChecked(true);
				cb_receive_new_count.setTag(new String[] { "space", "0" });
			} else {
				cb_receive_new_count.setChecked(false);
				cb_receive_new_count.setTag(new String[] { "space", "1" });
			}
		} catch (Exception e) {
		}
	}

	@Override
	public String getTitleCenter() {
		return "设置";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_setting;
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = StaticInApp.GET_PAKAGE_INFO;
				try {
					UnitSociax unit = new UnitSociax(ActivitySetting.this);
					unit.clearAppCache();
					msg.arg1 = 1;
				} catch (Exception e) {
					e.printStackTrace();
					msg.arg1 = 0;
				}
				resultHandler.sendMessage(msg);
			}
		}.start();
	}

	class ResultHandler extends Handler {

		public ResultHandler(Context context) {
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_USER_PRIVACY:
				setPrivacyData(msg.obj);
				break;
			case StaticInApp.GET_PAKAGE_INFO:
				if(msg.arg1==1){
					Toast.makeText(ActivitySetting.this, "清理成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(ActivitySetting.this, "清理失败", Toast.LENGTH_SHORT).show();
				}
				initData();
				break;
			}
		}
	}
}
