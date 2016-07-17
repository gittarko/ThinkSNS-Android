package com.thinksns.sociax.t4.component;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** 
 * 类说明：   webview监听工具类
 * @author  wz    
 * @date    2015-1-8
 * @version 1.0
 */
public class SociaxWebViewClient extends WebViewClient {
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		return super.shouldOverrideUrlLoading(view, url);
	}
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onPageFinished(WebView view, String url) {
		view.getSettings().setJavaScriptEnabled(true);
		super.onPageFinished(view, url);
		// html加载完成之后，添加监听图片的点击js函数
		view.loadUrl("javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"img\"); "
				+ "for(var i=0;i<objs.length;i++)  " + "{"
				+ "    objs[i].onclick=function()  " + "    {  "
				+ "        window.sociax_webview_image_listener.openImage(this.src);  "
				+ "    }  " + "}" + "})()");
	}
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		view.getSettings().setJavaScriptEnabled(true);
		super.onPageStarted(view, url, favicon);
	}
	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
	}
}
