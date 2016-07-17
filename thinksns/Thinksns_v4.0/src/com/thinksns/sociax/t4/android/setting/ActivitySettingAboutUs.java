package com.thinksns.sociax.t4.android.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.Listener.onWebViewLoadListener;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.model.ModelPhoto;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.network.ApiHttpClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * 类说明：
 *
 * @author Zoey
 * @version 1.0
 * @date 2015年9月8日
 */
public class ActivitySettingAboutUs extends ThinksnsAbscractActivity {

    private WebView wv_about_us;
    protected UnitSociax uint;
    private ImageView iv_about_us_back;
    private ProgressBar pb_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
    }

    private void initListener() {
    }

    private void initData() {
        uint = new UnitSociax(ActivitySettingAboutUs.this);
        wv_about_us.setHorizontalScrollBarEnabled(false);    //设置滑动条水平不显示
        wv_about_us.setVerticalScrollBarEnabled(false);    //设置滑动条垂直不显示
        pb_bar = (ProgressBar) this.findViewById(R.id.pb_bar);
    }

    private void initView() {
        loadingView = (LoadingView) findViewById(LoadingView.ID);
        wv_about_us = (WebView) findViewById(R.id.wv_about_us);
        getAboutUs();
    }

    @Override
    public String getTitleCenter() {
        return "关于我们";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    public void getAboutUs() {
//		loadingView.show(wv_about_us);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Message msg = new Message();
                    msg.what = StaticInApp.SHOW_ABOUT_US;
                    try {
                        Thinksns app = (Thinksns) getApplicationContext();
                        msg.obj = app.getPublicApi().showAboutUs();
                    } catch (ApiException e) {
                        e.printStackTrace();
//						loadingView.hide(wv_about_us);
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case StaticInApp.SHOW_ABOUT_US:
                    try {
                        uint.appendWebViewContent(wv_about_us, (String) msg.obj, null);
                        wv_about_us.setWebChromeClient(new WebChromeClient() {

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
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//				loadingView.hide(wv_about_us);
                    break;
            }
        }
    };
}
