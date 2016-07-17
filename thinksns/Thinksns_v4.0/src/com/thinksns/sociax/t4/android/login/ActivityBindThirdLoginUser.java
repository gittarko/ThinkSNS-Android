package com.thinksns.sociax.t4.android.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.api.Api.Oauth;
import com.thinksns.sociax.api.Api.Users;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.component.RightIsButton;
import com.thinksns.sociax.db.TopicListSqlHelper;
import com.thinksns.sociax.db.UserSqlHelper;
import com.thinksns.sociax.t4.android.ActivityHome;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.ThinksnsActivity;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient.HttpResponseListener;
import com.thinksns.sociax.t4.component.SmallDialog;

import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明:第三方登录之后完善资料进行注册
 * 需要传入intent ：
 * String type  =   qzone/sina/weixin   type_uid/access_token第三方平台获取
 */
public class ActivityBindThirdLoginUser extends ThinksnsAbscractActivity implements OnClickListener{
	private EditText etName;
	private EditText etPasswd;
	private ImageView iv_photo;

//	private ImageView iv_back;
	private SmallDialog smallDialog;
//	private Button mOk;
//	private Button mCancel;

	private Intent data;
	private String name, sex, icon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		smallDialog = new SmallDialog(this, getString(R.string.please_wait));
		smallDialog.setCanceledOnTouchOutside(false);

		data = getIntent();
		name = data.getStringExtra("name");
		sex = data.getStringExtra("gender");
		icon = data.getStringExtra("icon");

		initView();
		initLinter();
	}

	private void initLinter() {
		//确定完成
//		mOk.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				String uname = etName.getText().toString().trim();
//				String psw = etPasswd.getText().toString().trim();
//				if (uname == null || uname.length() == 0) {
//					Toast.makeText(ActivityBindThirdLoginUser.this,"用户名不能为空", Toast.LENGTH_SHORT).show();
//					return;
//				}else if(TextUtils.isEmpty(psw)) {
//					Toast.makeText(ActivityBindThirdLoginUser.this,"密码不能为空", Toast.LENGTH_SHORT).show();
//					return;
//				}
//
//				smallDialog.show();
//				bindUser(uname, psw);
//			}
//		});
		//重置
//		mCancel.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				etName.getText().clear();
//				etPasswd.getText().clear();
//			}
//		});

	}

	private void bindUser(String uname, String psw) {
		new AsyncTask<String, Void, Object>() {

			@Override
			protected Object doInBackground(String... params) {
				try {
					return new Api.Oauth().setThirdRegInfo(params[0],params[1], params[2],
							params[3], params[4], params[5], params[6]);
				} catch (ApiException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Object o) {
				doThirdLogin(o);
			}
		}.execute(data.getStringExtra("type"), data.getStringExtra("type_uid"),
				data.getStringExtra("access_token"), icon,
				sex,uname, psw);
	}

	private void initView() {
		iv_photo = (ImageView)findViewById(R.id.iv_face);
		etName = (EditText) findViewById(R.id.bind_et_name);
		etPasswd = (EditText) findViewById(R.id.bind_et_passwd);

		if(!TextUtils.isEmpty(name)) {
			etName.setText(name);
			etName.setSelection(name.length());
		}

		if(!TextUtils.isEmpty(icon)) {
			Glide.with(this)
					.load(icon)
					.transform(new GlideCircleTransform(this))
					.crossFade()
					.into(iv_photo);
		}

//		mOk = (Button) findViewById(R.id.bind_ok);
//		mCancel = (Button) findViewById(R.id.bind_cancal);
	}

	//处理三方账号注册信息
	private void doThirdLogin(Object o) {
		if(o == null) {
			Toast.makeText(ActivityBindThirdLoginUser.this, "操作异常，请重试", Toast.LENGTH_SHORT).show();
			smallDialog.dismiss();
			return;
		}

		try {
			JSONObject data = new JSONObject(o.toString());

			if(!data.has("status")) {
				String oauth_token = data.getString("oauth_token");
				String oauth_token_secret = data.getString("oauth_token_secret");
				int uid = data.getInt("uid");
				if (!TextUtils.isEmpty(oauth_token) && !TextUtils.isEmpty(oauth_token_secret)) {
					ModelUser authorizeResult = new ModelUser(uid, "", "", oauth_token,
							oauth_token_secret);
					ApiHttpClient.TOKEN = oauth_token;
					ApiHttpClient.TOKEN_SECRET = oauth_token_secret;
					//进行到这里可以保存用户信息，进入主页后再获取更详细资料
					getUserInfo(authorizeResult);
				} else {
					//拉取用户信息失败
				}
			}else {
				Toast.makeText(this, data.getString("msg"), Toast.LENGTH_SHORT).show();
				smallDialog.dismiss();
			}

		} catch (JSONException e1) {
			e1.printStackTrace();
			smallDialog.dismiss();

		} catch (Exception e) {
			e.printStackTrace();
			smallDialog.dismiss();
		}


	}

	//获取用户信息
	private void getUserInfo(ModelUser authorizeResult) {
		new Api.Users().show(authorizeResult, new HttpResponseListener() {

			@Override
			public void onSuccess(Object result) {
				ListData<SociaxItem> list = (ListData<SociaxItem>) result;
				if(list != null && list.size() == 1) {
					ModelUser user = (ModelUser) list.get(0);
					Thinksns.setMy(user);
					UserSqlHelper db = UserSqlHelper.getInstance(ActivityBindThirdLoginUser.this);
					db.addUser(user, true);

					Intent intent = new Intent(ActivityBindThirdLoginUser.this,
							ActivityHome.class);
					intent.putExtra("new_user", true);	//标示为新用户
					startActivity(intent);
					Anim.in(ActivityBindThirdLoginUser.this);
					//关闭首页
					ThinksnsActivity.getInstance().finish();
					//关闭登录页
					ActivityLogin.getInstance().finish();
					finish();
					smallDialog.dismiss();
				}
			}

			@Override
			public void onError(Object result) {
				Toast.makeText(ActivityBindThirdLoginUser.this, result.toString(), 0).show();
				smallDialog.dismiss();
			}
		});
	}

	@Override
	public String getTitleCenter() {
		return "完善资料";
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_bind__user__register;
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(this, R.drawable.img_back, "下一步");
	}

	@Override
	public OnClickListener getRightListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				String uname = etName.getText().toString().trim();
				String psw = etPasswd.getText().toString().trim();
				if (TextUtils.isEmpty(uname)) {
					Toast.makeText(ActivityBindThirdLoginUser.this,"用户名不能为空", Toast.LENGTH_SHORT).show();
					return;
				}else if(TextUtils.isEmpty(psw)) {
					Toast.makeText(ActivityBindThirdLoginUser.this,"密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}

				smallDialog.show();
				bindUser(uname, psw);
			}
		};
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
	}
}
