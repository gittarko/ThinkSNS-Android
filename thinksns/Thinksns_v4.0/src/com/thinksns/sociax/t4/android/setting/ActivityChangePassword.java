package com.thinksns.sociax.t4.android.setting;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.login.ActivityLogin;
import com.thinksns.sociax.t4.model.ModelBackMessage;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明： 修改密码
 * 
 * @author wz
 * @date 2014-8-29
 * @version 1.0
 */
public class ActivityChangePassword extends ThinksnsAbscractActivity {

	private EditText etOldPass, etNewPass, etNewCommitPass;
	private Button btnOkPass;

	private Thinksns app;
	private ResultHandler resultHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);

		app = (Thinksns) getApplicationContext();
		resultHandler = new ResultHandler(this);

		findViewById(R.id.chat_left_img).setOnClickListener(getLeftListener());

		etOldPass = (EditText) findViewById(R.id.et_old_pass);
		etNewPass = (EditText) findViewById(R.id.et_new_pass);
		etNewCommitPass = (EditText) findViewById(R.id.et_new_pass_commit);
		btnOkPass = (Button) findViewById(R.id.btn_pass_ok);
		btnOkPass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				savePass();
			}
		});
	}

	/**
	 * 修改比吗
	 */
	private void savePass() {
		String newPass = etNewPass.getText().toString().trim();
		if (etOldPass.getText().length() == 0) {
			Toast.makeText(ActivityChangePassword.this, "请输入原始密码", 0).show();
		} else if (newPass.length() == 0) {
			Toast.makeText(ActivityChangePassword.this, "请输入新密码", 0).show();
		} else if (!newPass.equals(etNewCommitPass.getText().toString().trim())) {
			Toast.makeText(ActivityChangePassword.this, "两次输入密码不一致，请重新输入", 0)
					.show();
			etNewPass.setText("");
			etNewCommitPass.setText("");
		} else if (newPass.length() < 6 || newPass.length() > 15) {
			Toast.makeText(ActivityChangePassword.this, "密码长度应为6-15位，请重新输入密码",
					0).show();
			etNewPass.setText("");
			etNewCommitPass.setText("");
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Message msg = resultHandler.obtainMessage();
					try {
						msg.what = 1;
						msg.obj = app.getUsers().saveUserInfo(
								StaticInApp.CHANGE_USER_PWD,
								etNewPass.getText().toString(),
								etOldPass.getText().toString());
						System.err.println(msg.obj.toString());
					} catch (Exception e) {
						e.printStackTrace();
						msg.what = 2;
					}
					msg.sendToTarget();
				}
			}).start();
			btnOkPass.setEnabled(false);
		}

	}

	class ResultHandler extends Handler {

		public ResultHandler(Context context) {
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				try {
					ModelBackMessage backmsg = new ModelBackMessage(
							msg.obj.toString());
					if (backmsg != null) {
						Toast.makeText(ActivityChangePassword.this,
								backmsg.getMsg(), Toast.LENGTH_SHORT).show();
						if (backmsg.getStatus() == 1) {
							Thinksns app = (Thinksns) ActivityChangePassword.this
									.getApplicationContext();
							app.stopService();
							app.getUserSql().clear();
							Bundle data = new Bundle();
							data.putBoolean("status", true);
							ActivityStack.startActivity(ActivityChangePassword.this,
									ActivityLogin.class, data,
									Intent.FLAG_ACTIVITY_CLEAR_TASK);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (msg.what == 2) {
				Toast.makeText(ActivityChangePassword.this, "修改失败", 0).show();
			}
			btnOkPass.setEnabled(true);
		}
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
		return R.layout.gb_edit_pass;
	}
}
