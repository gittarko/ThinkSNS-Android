package com.thinksns.sociax.t4.android.weibo;

import java.io.InputStream;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.api.Api;

/**
 * 类说明： 网页站内展示
 *
 * @author Zoey
 * @date 2015-10-9
 * @version 1.0
 */
public class NetActivity extends ThinksnsAbscractActivity {

	private WebView wv_net = null;
	private Intent intent = null;
	private String url = null;
	private ImageView iv_back = null;
	private TextView tv_title = null;
	private ProgressBar pb_bar;
	private int flag=0;
	private final Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);

		initIntentData();
		initView();
		initLisener();
		initData();
	}

	public void initIntentData() {
		intent = getIntent();
		if (intent != null) {
			url = intent.getStringExtra("url");
			flag = intent.getIntExtra("flag",0);
		}
	}

	public void initView() {
		wv_net = (WebView) this.findViewById(R.id.wv_net);
		iv_back = (ImageView) this.findViewById(R.id.iv_back);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		pb_bar = (ProgressBar) this.findViewById(R.id.pb_bar);
	}

	public void initLisener() {
		iv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void initData() {
		if (url != null) {

			final Activity activity = this;
			WebChromeClient wvcc = new WebChromeClient() {
				@Override
				public void onReceivedTitle(WebView view, String title) {
					super.onReceivedTitle(view, title);
					if (flag!=0){
						tv_title.setText(title);
					}
				}
				@Override
				public void onProgressChanged(WebView view, int progress) {
					super.onProgressChanged(view, progress);
					activity.setProgress(progress * 1000);
				}
			};
			// 设置setWebChromeClient对象
			wv_net.setWebChromeClient(wvcc);

			WebSettings webSettings = wv_net.getSettings();
			// 设置WebView属性，能够执行Javascript脚本
			webSettings.setJavaScriptEnabled(true);
			// 设置可以访问文件
			webSettings.setAllowFileAccess(true);
			// 设置支持缩放
			webSettings.setBuiltInZoomControls(true);
			webSettings.setPluginState(PluginState.ON);

			// 设置Web视图
			wv_net.setWebViewClient(new WebViewClient(){
				@Override
				public void onReceivedError(WebView view, int errorCode,
											String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description, failingUrl);
					Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
				}
			});

			wv_net.setWebChromeClient(new WebChromeClient() {

				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					if (newProgress == 100) {
						pb_bar.setVisibility(View.INVISIBLE);
					} else {
						if (View.INVISIBLE == pb_bar.getVisibility()) {
							pb_bar.setVisibility(View.VISIBLE);
						}
						pb_bar.setProgress(newProgress);
					}
					super.onProgressChanged(view, newProgress);
				}

				@Override
				public void onReceivedTitle(WebView view, String title) {
					super.onReceivedTitle(view, title);
					if (flag!=0){
						tv_title.setText(title);
					}
				}
			});

//			//如果是mp4格式的视频
//			if (url.endsWith(".mp4")) {
//				String newContent=reCreateHtml(getContentFromRaw(),url);
//				wv_net.loadDataWithBaseURL(Api.getHost(), newContent, "text/html","utf-8", null);
//			}
//			
//			else {
			wv_net.loadUrl(url);
//			}
		}
	}

	/**
	 * 获取资源文件下的字符串文件数据
	 */
	public String getContentFromRaw(){
		//将文件读取到buffer数组中
		InputStream is = this.getResources().openRawResource(R.raw.foobars);
		String content =null;
		try {
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			//将字节数组转换为以utf-8编码的字符串
			content= new String(buffer, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	//重新包装视频地址
	public String reCreateHtml(String content,String url){
		//页面适配
		Document doc_Dis = Jsoup.parse(content);
		Elements ele_video = doc_Dis.getElementsByTag("video");
		if (ele_video!=null){
			for (Element e_videl : ele_video) {
				String videourl=e_videl.text();
				Log.v("videoUrl", videourl);
			}
		}
		return doc_Dis.toString();
	}


//	private class AndroidBridge {
//		public void goMarket() {
//			handler.post(new Runnable() {
//				public void run() {
//					Intent installIntent = new Intent("android.intent.action.VIEW");
//					installIntent.setData(Uri.parse("market://details?id=com.adobe.flashplayer"));
//					startActivity(installIntent);
//				}
//			});
//		}
//	}
//	@SuppressLint("JavascriptInterface")
//	private void install() {
//		wv_net.addJavascriptInterface(new AndroidBridge(), "android");
//		wv_net.loadUrl("file:///android_asset/go_market.html");
//	}
//	
//	//检测用户设备有没有安装flashplayer
//	private boolean check() {  
//        PackageManager pm = getPackageManager();  
//        List<PackageInfo> infoList = pm.getInstalledPackages(PackageManager.GET_SERVICES);  
//        for (PackageInfo info : infoList) {  
//            if ("com.adobe.flashplayer".equals(info.packageName)) {  
//                return true;  
//            }  
//        }  
//        return false;  
//}

	@Override
	protected void onResume() {
		super.onResume();
		wv_net.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		wv_net.onPause();
	}

	@Override
	protected void onDestroy() {
		wv_net.stopLoading();
		wv_net.destroy();
		super.onDestroy();
	}

	@Override
	public void finish() {
		ViewGroup view = (ViewGroup) getWindow().getDecorView();
		view.removeAllViews();
		super.finish();
	}

	@Override
	public String getTitleCenter() {
		return null;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.net_activity;
	}
}
