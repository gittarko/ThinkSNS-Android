package com.thinksns.sociax.android;

import com.thinksns.sociax.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class SociaxWebView extends Activity {

	private WebView webView;

	private Button buttonBack;
	private Button buttonForward;
	private ImageButton imBtnHome;

	Handler handler = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sociax_web);

		webView = (WebView) findViewById(R.id.web_view);

		buttonBack = (Button) findViewById(R.id.btn_back);
		buttonForward = (Button) findViewById(R.id.btn_forward);
		imBtnHome = (ImageButton) findViewById(R.id.im_btn_home);

		webView.getSettings().setJavaScriptEnabled(true);
		// 設置是否支持放大缩小
		// webView.getSettings().setBuiltInZoomControls(true);
		String url = getIntent().getStringExtra("link");
		webView.loadUrl(url);
		// webView.loadUrl("http://dev.zhishisoft.com/ts/index.php?app=w3g");

		// webView.loadUrl("http://www.baidu.com/");
		// webView.loadUrl("file:///android_asset/js.html");

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}

		});

		class MyWebChromeClient extends WebChromeClient {

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_LONG).show();
				return true;
			}

		}

		webView.setWebChromeClient(new MyWebChromeClient());

		webView.addJavascriptInterface(new Object() {

			public void show() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.i("通知", "调用了该方法哦");
						/*
						 * 通过webView.loadUrl("javascript:xxx")方式就可以调用当前网页中的名称
						 * 为xxx的javascript方法
						 */
						webView.loadUrl("javascript:show()");
					}
				});

			}

		}, "chenzheng_java");

		buttonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (webView.canGoBack()) {
					webView.goBack();
				}
			}
		});

		buttonForward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (webView.canGoForward()) {
					webView.goForward();
				}
			}
		});

		imBtnHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(SociaxWebView.this,
//						MainGridActivity.class);
//				startActivity(intent);
//				SociaxWebView.this.finish();
			}
		});
	}
	/*
	 * @Override protected void onResume() { // TODO Auto-generated method stub
	 * System.out.println("onResume");
	 * //webView.loadUrl("file:///android_asset/js.html"); super.onResume(); }
	 */

	/*
	 * 当WebView内容影响UI时调用WebChromeClient的方法
	 */
	// mWebView.setWebChromeClient(new WebChromeClient()

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { // TODO
	 * Auto-generated method stub if (keyCode == KeyEvent.KEYCODE_BACK &&
	 * webView.canGoBack()) { webView.goBack(); return true; //
	 * webView.goBackOrForward(-1); } else if(keyCode == KeyEvent.KEYCODE_BACK){
	 * AlertDialog.Builder alertDialog = new Builder(this);
	 * 
	 * alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
	 * {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub finish(); } });
	 * alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
	 * {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub
	 * 
	 * } }); alertDialog.create().show(); } return super.onKeyDown(keyCode,
	 * event);
	 * 
	 * }
	 */

}
