package com.thinksns.sociax.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.thinksns.sociax.api.Api;
import com.thinksns.sociax.concurrent.Worker;
import com.thinksns.sociax.db.UserSqlHelper;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsActivity;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.android.R;

public class ThinksnsStartWeb extends Activity {
	private String url;
	private WebView webView;
	private static Worker thread = null;
	private static ActivityHandler handler;
	private boolean tempBoolean = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.webview);
		webView = (WebView) findViewById(R.id.webview);

		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		Log.e("syste", "+++" + url);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(url);
		webView.setWebViewClient(new HelloWebViewClient());
	}

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			Log.e("url", "url" + url);
			/*
			 * if(url.equals(
			 * "http://dev.thinksns.com/ts/2.0/index.php?app=wap&mod=Public&act=login"
			 * )){ Intent intent =new Intent(ThinksnsStartWeb.this,
			 * ThinksnsActivity.class);
			 * ThinksnsStartWeb.this.startActivity(intent);
			 * ThinksnsStartWeb.this.finish(); }
			 */
			if (url.contains("login")) {
				Intent intent = new Intent(ThinksnsStartWeb.this,
						ThinksnsActivity.class);
				ThinksnsStartWeb.this.startActivity(intent);
				ThinksnsStartWeb.this.finish();
			}
			Pattern pattern = Pattern
					.compile("(oauth_token=(.+?)&)|(uid=(.+?)&)|(oauth_token_secret=(.+?)&)");
			// Pattern pattern =Pattern.compile(
			// "(oauth_token=(.+?)&)|(oauth_token_secret=(.+?)&)");
			Matcher matcher = pattern.matcher(url);
			if (!tempBoolean) {

				if (url.contains("uid")) {
					tempBoolean = true;
					thread = new Worker(
							(Thinksns) ThinksnsStartWeb.this
									.getApplicationContext(),
							"go for user");
					handler = new ActivityHandler(thread.getLooper(),
							ThinksnsStartWeb.this);
					Bundle data = new Bundle();
					// Intent intent = new Intent();
					while (matcher.find()) {
						if (matcher.group().charAt(0) == 'o') {
							if (matcher.group().substring(0, 18)
									.equals("oauth_token_secret")) {
								// intent.putExtra("oauth_token_secret",
								// matcher.group().substring(19,
								// matcher.group().length()-1));
								data.putString(
										"oauth_token_secret",
										matcher.group().substring(19,
												matcher.group().length() - 1));
							} else {
								// intent.putExtra("oauth_token",matcher.group().substring(12,
								// matcher.group().length()-1));
								data.putString(
										"oauth_token",
										matcher.group().substring(12,
												matcher.group().length() - 1));
							}
						} else {
							// intent.putExtra("uid",matcher.group().substring(4,
							// matcher.group().length()-1));
							data.putString(
									"uid",
									matcher.group().substring(4,
											matcher.group().length() - 1));
							// intent.putExtra("uid",226);
						}
					}
					// intent.putExtra("status", true);
					// intent.setClass(ThinksnsStartWeb.this,
					// ThinksnsLoginActivity.class);
					// ThinksnsStartWeb.this.startActivity(intent);
					Message msg = new Message();
					msg.setData(data);
					handler.sendMessage(msg);
				}
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			// TODO Auto-generated method stub
			Log.e("handler", "handler=" + handler.toString());
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}
	}

	private class ActivityHandler extends Handler {

		private static final long SLEEP_TIME = 2000;
		private Context context = null;

		public ActivityHandler(Looper looper, Context context) {
			super(looper);
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Thinksns app = thread.getApp();
			Api.Users users = app.getUsers();
			ModelUser authorizeResult = new ModelUser();
			authorizeResult.setToken(msg.getData().getString("oauth_token"));
			authorizeResult.setSecretToken(msg.getData().getString(
					"oauth_token_secret"));
			authorizeResult.setUid(new Integer(msg.getData().getString("uid")));
			authorizeResult.setUserName("");
			Message errorMessage = new Message();
//			try {
//				ModelUser loginedUser = null;//users.show(authorizeResult);
//				loginedUser.setToken(authorizeResult.getToken());
//				loginedUser.setSecretToken(authorizeResult.getSecretToken());
//				Thinksns.setMy(loginedUser);
//				UserSqlHelper db = UserSqlHelper.getInstance(this.context);
//				db.addUser(loginedUser, true);
//				Intent intent = new Intent();
//				intent.setClass(ThinksnsStartWeb.this, ThinksnsActivity.class);
//				ThinksnsStartWeb.this.startActivity(intent);
//				ThinksnsStartWeb.this.finish();
//			} catch (DataInvalidException e) {
//				errorMessage.obj = e.getMessage();
//				thread.sleep(SLEEP_TIME);
//			} catch (VerifyErrorException e) {
//				errorMessage.obj = e.getMessage();
//				thread.sleep(SLEEP_TIME);
//			} catch (ApiException e) {
//				errorMessage.obj = e.getMessage();
//				thread.sleep(SLEEP_TIME);
//			}

		}

	}

}
