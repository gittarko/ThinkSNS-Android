package com.thinksns.sociax.android;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.RightIsButton;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.component.SmallDialog;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.sociax.unit.SociaxUIUtils;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author povol
 * @date May 16, 2013
 * @version 1.0
 */
public class RegisterActivity extends ThinksnsAbscractActivity {

	private EditText etName;
	private EditText etEmail;
	private EditText etPasswd;
	private RadioGroup rgSex;
	private RadioButton rbMan;
	private RadioButton rbWoman;

	private MyHandler myHandler;

	private String sex;

	private SmallDialog smallDialog;

	private String[] thirdUserData;
	private boolean isThirdReg = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isThirdReg = getIntent().hasExtra("reg_data");
		thirdUserData = getIntent().getStringArrayExtra("reg_data");

		smallDialog = new SmallDialog(this, getString(R.string.please_wait));

		myHandler = new MyHandler();

		initView();
	}

	private void initView() {

		final TextView passTv = (TextView) findViewById(R.id.tv_pass);

		etName = (EditText) findViewById(R.id.et_name);
		etEmail = (EditText) findViewById(R.id.et_email);
		etPasswd = (EditText) findViewById(R.id.et_passwd);

		rgSex = (RadioGroup) findViewById(R.id.rg_sex);
		rbMan = (RadioButton) findViewById(R.id.rb_man);
		rbWoman = (RadioButton) findViewById(R.id.rb_woman);

		sex = ((RadioButton) findViewById(rgSex.getCheckedRadioButtonId()))
				.getTag().toString();

		rgSex.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				sex = ((RadioButton) findViewById(checkedId)).getTag()
						.toString();
			}
		});

		if (isThirdReg) {
			etName.setText(thirdUserData[0]);
			etEmail.setBackgroundResource(R.drawable.reg_buttom_bg);
			if (thirdUserData[1].equals("1"))
				rbMan.setChecked(true);
			else
				rbWoman.setChecked(true);

			etPasswd.setVisibility(View.GONE);
			passTv.setVisibility(View.GONE);
		}
	}

	private void checkData() {

		if (isThirdReg) {
			if (etName.getText().toString().trim().length() == 0) {
				Toast.makeText(this, R.string.reg_re_name_alert,
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (etEmail.getText().toString().trim().length() == 0) {
				Toast.makeText(this, R.string.reg_re_email_alert,
						Toast.LENGTH_SHORT).show();
				return;
			}
			thirdRegister();

		} else {

			if (etName.getText().toString().trim().length() == 0) {
				Toast.makeText(this, R.string.reg_re_name_alert,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (etEmail.getText().toString().trim().length() == 0) {
				Toast.makeText(this, R.string.reg_re_email_alert,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!SociaxUIUtils.checkEmail(etEmail.getText().toString())) {
				Toast.makeText(this, R.string.reg_re_email_check_alert,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (etPasswd.getText().toString().trim().length() == 0) {
				Toast.makeText(this, R.string.reg_re_pass_alert,
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (etPasswd.getText().toString().trim().length() < 6) {
				Toast.makeText(this, R.string.reg_re_pass_lenght_alert,
						Toast.LENGTH_SHORT).show();
				return;
			}

			register();
		}

		smallDialog.show();
	}

	private void thirdRegister() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Thinksns app = (Thinksns) getApplication();
				thirdUserData[0] = etName.getText().toString().trim();
				thirdUserData[1] = sex + "";
				thirdUserData[2] = etEmail.getText().toString().trim();
				try {
					Message msg = myHandler.obtainMessage();
					msg.what = app.getOauth().thirdRegister(thirdUserData); // //成功：1
																			// 失败：0
					// 邮箱不合格：2
					myHandler.sendMessage(msg);
				} catch (ApiException e) {
					Log.d(this.getClass().toString(), e.toString());
				}
			}
		}).start();
	}

	private void register() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Thinksns app = (Thinksns) getApplication();
				String[] data = new String[] {
						etName.getText().toString().trim(),
						etEmail.getText().toString().trim(),
						etPasswd.getText().toString().trim(), sex + "" };
				try {
					Message msg = myHandler.obtainMessage();
					JSONObject jo = new JSONObject(app.getOauth()
							.register(data).toString());
					msg.what = jo.getInt("status"); // //成功：1 失败：0
					msg.obj = jo.getString("msg"); // 邮箱不合格：2
					myHandler.sendMessage(msg);
				} catch (Exception e) {
					Log.d(this.getClass().toString(), e.toString());
				}
			}
		}).start();
	}

	@Override
	public OnClickListener getRightListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkData();
			}
		};
	}

	@Override
	public String getTitleCenter() {
		// TODO Auto-generated method stub
		return getString(R.string.register);
	}

	@Override
	protected CustomTitle setCustomTitle() {
		// TODO Auto-generated method stub
		return new RightIsButton(this, getString(R.string.ok));
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.register;
	}

	@Override
	public int getRightRes() {
		// TODO Auto-generated method stub
		return R.drawable.find_btn_bg;
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Toast.makeText(RegisterActivity.this, getResources().getString(R.string.reg_success),
						Toast.LENGTH_SHORT).show();
				smallDialog.dismiss();
				RegisterActivity.this.finish();
				Anim.exit(RegisterActivity.this);

			} else {
				Toast.makeText(RegisterActivity.this, msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				smallDialog.dismiss();
			}
		}
	}

}
