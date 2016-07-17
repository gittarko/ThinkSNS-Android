package com.thinksns.sociax.t4.android.setting;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.android.R;

/**
 * 类说明：隐私设置类
 * 
 * @author wz
 * @date 2014-9-4
 * @version 1.0
 * 
 * @date 20125-7-2
 * caoligai 修改 将按下 back 键退出并保存数据修改为每修改一个数据就保存，按下 back 时直接 finish()
 */
public class ActivityManagePrivacy extends ThinksnsAbscractActivity {
	protected static final int GET_PRIVACY = 5;
	protected static final int SAVE_PRIVACY = 4;
	private Handler handler;
	private CheckBox tv_comment_all, tv_comment_follow, tv_message_all,
			tv_message_follow, tv_space_all, tv_space_follow;
	private String[] oldprivacy = new String[3], newprivacy = new String[3];
	private  String space = "", comment = "", message = "";

	// caoligai 修改
	private RelativeLayout rl_comment_all, rl_comment_follow, rl_message_all,
			rl_message_follow, rl_space_all, rl_space_follow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initIntentData();
		initView();
		initListener();
		initData();
	}

	/**
	 * 载入数据
	 */
	private void initData() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				Message msg = handler.obtainMessage();
				msg.what = GET_PRIVACY;
				msg.obj = new Api.Oauth().getPrivacy();
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 初始化监事件
	 */
	private void initListener() {
		tv_comment_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_comment_follow.setChecked(false);
				tv_comment_all.setChecked(true);
				
				comment = "0";
				
				newprivacy[0] = space;
				newprivacy[1] = comment;
				newprivacy[2] = message;
				
				savePrivaceTask(newprivacy);
			}
		});
		tv_comment_follow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_comment_follow.setChecked(true);
				tv_comment_all.setChecked(false);
				
				comment = "1";
				
				newprivacy[0] = space;
				newprivacy[1] = comment;
				newprivacy[2] = message;
				
				savePrivaceTask(newprivacy);
			}
		});
		tv_message_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_message_all.setChecked(true);
				tv_message_follow.setChecked(false);
				
				message = "0";
				
				newprivacy[0] = space;
				newprivacy[1] = comment;
				newprivacy[2] = message;
				
				savePrivaceTask(newprivacy);
			}
		});
		tv_message_follow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_message_all.setChecked(false);
				tv_message_follow.setChecked(true);
				message = "1";
				newprivacy[0] = space;
				newprivacy[1] = comment;
				newprivacy[2] = message;
				savePrivaceTask(newprivacy);
			}
		});
		tv_space_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_space_all.setChecked(true);
				tv_space_follow.setChecked(false);
				space = "0";
				newprivacy[0] = space;
				newprivacy[1] = comment;
				newprivacy[2] = message;
				savePrivaceTask(newprivacy);
			}
		});
		tv_space_follow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_space_all.setChecked(false);
				tv_space_follow.setChecked(true);
				space = "1";
				newprivacy[0] = space;
				newprivacy[1] = comment;
				newprivacy[2] = message;
				savePrivaceTask(newprivacy);
			}
		});

	}

	protected void savePrivaceTask(final String[] newpriv) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				msg.what = SAVE_PRIVACY;
				msg.obj = new Api.Oauth().savePrivacy(newpriv);
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 初始化intent信息
	 */
	private void initIntentData() {
	}

	/**
	 * 初始化页面
	 */
	private void initView() {
		tv_comment_all = (CheckBox) findViewById(R.id.tv_comment_all);
		tv_comment_follow = (CheckBox) findViewById(R.id.tv_comment_follow);

		tv_space_all = (CheckBox) findViewById(R.id.tv_space_all);
		tv_space_follow = (CheckBox) findViewById(R.id.tv_space_follow);

		tv_message_all = (CheckBox) findViewById(R.id.tv_sixin_all);
		tv_message_follow = (CheckBox) findViewById(R.id.tv_sixin_follow);

		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case GET_PRIVACY:
					try {
						
						JSONObject result2json = new JSONObject(msg.obj.toString());
						
						space = result2json.getString("space");
						comment = result2json.getString("comment_weibo");
						message = result2json.getString("message");
						
						oldprivacy[0] = space;
						oldprivacy[1] = comment;
						oldprivacy[2] = message;
						
						newprivacy[0] = space;
						newprivacy[1] = comment;
						newprivacy[2] = message;

						if ((space+"").equals("1")) {
							tv_space_all.setChecked(false);
							tv_space_follow.setChecked(true);
						} else {
							tv_space_all.setChecked(true);
							tv_space_follow.setChecked(false);
						}
						
						if ((comment+"").equals("1")) {
							tv_comment_all.setChecked(false);
							tv_comment_follow.setChecked(true);

						} else {
							tv_comment_all.setChecked(true);
							tv_comment_follow.setChecked(false);
						}

						if ((message+"").equals("1")) {
							tv_message_all.setChecked(false);
							tv_message_follow.setChecked(true);
						} else {
							tv_message_all.setChecked(true);
							tv_message_follow.setChecked(false);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case SAVE_PRIVACY:
					try {
						JSONObject result2json = new JSONObject(msg.obj.toString());
						
						String result = result2json.getString("status");
						if (result.equals("1")) {
							Toast.makeText(getApplicationContext(),result2json.getString("msg"),Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getApplicationContext(),result2json.getString("msg"),1000).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
//					finish();
					break;
				}
			};
		};
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
		return "隐私设置";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_privacy;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			/*newprivacy[0] = space;
			newprivacy[1] = comment;
			newprivacy[2] = message;
			if (space.equals(oldprivacy[0]) && comment.equals(oldprivacy[1])
					&& message.equals(oldprivacy[2])) {
				finish();
			} else {
				savePrivaceTask();
			}*/
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
