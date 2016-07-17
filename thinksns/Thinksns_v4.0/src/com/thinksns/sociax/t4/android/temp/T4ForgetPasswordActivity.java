package com.thinksns.sociax.t4.android.temp;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.RightIsButton;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.function.FunctionVerifyCode;
import com.thinksns.sociax.t4.component.SmallDialog;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.utils.Anim;
import com.thinksns.sociax.unit.SociaxUIUtils;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-9-5
 * @version 1.0
 */
public class T4ForgetPasswordActivity extends ThinksnsAbscractActivity {

	private EditText etName;
	private EditText etEmail;
	private EditText etPasswd;
	private RadioGroup rgSex;
	private RadioButton rbMan;
	private RadioButton rbWoman;
	private Button bt_next_step;
	private MyHandler myHandler;
	private LinearLayout ll_step_one, ll_step_two;
	private int STEP_ONE = 1, STEP_TWO = 2;
	protected static final int OAUTH_CODE = 5;
	protected static final int SAVEPWD = 6;

	private int doWhat = STEP_ONE;

	private String sex;

	private SmallDialog smallDialog;
	private ImageView tv_title_left;
	private EditText ed_phone, ed_verifycode, ed_newpwd, ed_newpwd_comfirm;
	private TextView tv_getVerify;

	private String[] thirdUserData;
	private boolean isThirdReg = false;
	private int FAILED = 0;// 失败
	private int SUCCESS = 1;// 请求中
	boolean isOauthVerifycodeSuccess = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		isThirdReg = getIntent().hasExtra("reg_data");
		thirdUserData = getIntent().getStringArrayExtra("reg_data");

		smallDialog = new SmallDialog(this, getString(R.string.please_wait));
		smallDialog.setCanceledOnTouchOutside(false);
		myHandler = new MyHandler();

		initView();
		initListener();
	}

	private void initListener() {
		tv_title_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(tv_getVerify.getWindowToken(), 0);
				finish();
			}
		});

		bt_next_step.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(tv_getVerify.getWindowToken(), 0);
				if (doWhat == STEP_ONE) {
					doStepOne();
				} else {
					doStepTwo();
				}
			}
		});
		tv_getVerify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(tv_getVerify.getWindowToken(), 0);
				FunctionVerifyCode verify = new FunctionVerifyCode(
						T4ForgetPasswordActivity.this, ed_phone, tv_getVerify);
				if (verify.checkPhoneNumber()) {
					verify.getFindBackVerify();
				}
			}
		});
	}

	/**
	 * 第二步，确认修改密码
	 */
	protected void doStepTwo() {
		if (ed_newpwd.getText().toString().trim().length() == 0
				|| ed_newpwd_comfirm.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), "密码不能为空",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (ed_newpwd.getText().toString().trim()
				.equals(ed_newpwd_comfirm.getText().toString().trim())) {
			doResetPwd();
		} else {// 两次密码不一致
			Toast.makeText(getApplicationContext(), "两次输入密码不一致",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 异步线程重置密码
	 */
	private void doResetPwd() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Thinksns app = (Thinksns) getApplication();
				try {
					Message msg = myHandler.obtainMessage();
					JSONObject jo = new JSONObject(app
							.getOauth()
							.saveNewPwd(ed_phone.getText().toString(),
									ed_newpwd.getText().toString(),
									ed_verifycode.getText().toString())
							.toString());
					msg.arg1 = SAVEPWD;
					msg.what = jo.getInt("status"); // //成功：1 失败：0
					if (jo.has("message"))
						msg.obj = jo.getString("message"); // 邮箱不合格：2
					myHandler.sendMessage(msg);
				} catch (Exception e) {
					Log.d(this.getClass().toString(), e.toString());
				}
			}
		}).start();
		smallDialog.show();
	}

	/**
	 * 第一步，检测验证码
	 */
	protected void doStepOne() {
		FunctionVerifyCode verify = new FunctionVerifyCode(ed_phone,
				ed_verifycode, isOauthVerifycodeSuccess,
				T4ForgetPasswordActivity.this);
		if (verify.checkPhoneNumber()) {
			checkVerifyCode();
		}

	}

	/**
	 * 检验验证码线程
	 */
	protected void checkVerifyCode() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Thinksns app = (Thinksns) getApplication();
				try {
					Message msg = myHandler.obtainMessage();
					msg.arg1 = OAUTH_CODE;
					JSONObject jo = new JSONObject(app
							.getOauth()
							.oauthFindbackVerifyCode(
									ed_phone.getText().toString(),
									ed_verifycode.getText().toString())
							.toString());
					msg.what = jo.getInt("status"); // //成功：1 失败：0
					msg.obj = jo.getString("message"); // 邮箱不合格：2
					myHandler.sendMessage(msg);
				} catch (Exception e) {
					Log.d(this.getClass().toString(), e.toString());
				}
			}
		}).start();
		smallDialog.show();
	}

	private void initView() {

		final TextView passTv = (TextView) findViewById(R.id.tv_pass);
		bt_next_step = (Button) findViewById(R.id.bt_next_step);
		ll_step_one = (LinearLayout) findViewById(R.id.ll_step_one);
		ll_step_two = (LinearLayout) findViewById(R.id.ll_step_two);
		tv_title_left = (ImageView) findViewById(R.id.tv_title_left);
		ed_phone = (EditText) findViewById(R.id.ed_phone);
		ed_verifycode = (EditText) findViewById(R.id.ed_verifyCode);
		tv_getVerify = (TextView) findViewById(R.id.tv_getVerify);
		ed_newpwd = (EditText) findViewById(R.id.ed_newpwd);
		ed_newpwd_comfirm = (EditText) findViewById(R.id.ed_newpwd_comfirm);

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
		return getString(R.string.register);
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new RightIsButton(this, getString(R.string.ok));
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_forget;
	}

	@Override
	public int getRightRes() {
		return R.drawable.find_btn_bg;
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == OAUTH_CODE) {
				if (msg.what == SUCCESS) {// 验证成功
					Log.v("T4RegisterActivity",
							"wztest checkVerifyCode success");
					ll_step_one.setVisibility(View.GONE);
					ll_step_two.setVisibility(View.VISIBLE);
					bt_next_step.setText("完成");
					doWhat = STEP_TWO;
				} else {
					Toast.makeText(T4ForgetPasswordActivity.this,
							msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
				}

			} else if (msg.arg1 == SAVEPWD) {
				if (msg.what == 1) {
					Toast.makeText(
							T4ForgetPasswordActivity.this,
							getResources().getString(R.string.re_p_success) + ",请妥善保管好您的新密码",Toast.LENGTH_SHORT).show();
					smallDialog.dismiss();
					T4ForgetPasswordActivity.this.finish();
					Anim.exit(T4ForgetPasswordActivity.this);
				} else {
					Toast.makeText(T4ForgetPasswordActivity.this,
							msg.obj.toString(), Toast.LENGTH_SHORT).show();
					smallDialog.dismiss();
				}
			} else {
				if (msg.what == 1) {
					Toast.makeText(T4ForgetPasswordActivity.this,
							getResources().getString(R.string.reg_success), Toast.LENGTH_SHORT).show();
					T4ForgetPasswordActivity.this.finish();
					Anim.exit(T4ForgetPasswordActivity.this);

				} else {
					Toast.makeText(T4ForgetPasswordActivity.this,
							msg.obj.toString(), Toast.LENGTH_SHORT).show();
				}
			}

			smallDialog.dismiss();
		}
	}
}
