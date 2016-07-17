package com.thinksns.sociax.t4.android.gift;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;

import com.thinksns.sociax.t4.model.ModelShopGift;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;

/**
 * 类说明：礼物详情
 *
 * @author Zoey
 * @version 1.0
 * @date 2015年9月21日
 */
public class ActivityGiftDetail extends ThinksnsAbscractActivity {

    private TextView tv_detail_name, tv_detail_brief,
            tv_detail_count, tv_detail_content,
            tv_detail_buttom_score, tv_exchange_now;
    private ImageView tv_title_back;

    private static ModelShopGift modelGift = null;
    private ImageView iv_detail;
    private GiftHandler mHandler = null;
    protected UnitSociax uint;//工具类
    private WebView wv_content;
    private String wv_head = "<html><body>";
    private String wv_tail = "\r\n<body><html>";
    private LoadingView loadingView;
    private ScrollView sv_find;
    private ProgressBar pb_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreateNoTitle(savedInstanceState);

        initIntentData();
        initView();
        initListener();
        initData();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            modelGift = (ModelShopGift) intent.getSerializableExtra("modelGift");
        }
    }

    private void initView() {
        iv_detail = (ImageView) findViewById(R.id.iv_detail);
        tv_title_back = (ImageView) findViewById(R.id.tv_title_left);
        tv_detail_name = (TextView) findViewById(R.id.tv_gift_detail_name);
        tv_detail_brief = (TextView) findViewById(R.id.tv_gift_detail_brief);
        tv_detail_count = (TextView) findViewById(R.id.tv_gift_detail_get_num);
        tv_detail_buttom_score = (TextView) findViewById(R.id.tv_dialog_score);
        tv_detail_content = (TextView) findViewById(R.id.tv_gift_detail_content);
        tv_exchange_now = (TextView) findViewById(R.id.tv_dialog_i_wanna_exchange);
        wv_content = (WebView) findViewById(R.id.wv_content);
        loadingView = (LoadingView) findViewById(LoadingView.ID);
        sv_find = (ScrollView) findViewById(R.id.sv_find);
        pb_bar = (ProgressBar) this.findViewById(R.id.pb_bar);

        mHandler = new GiftHandler();
        this.uint = new UnitSociax(this);
    }

    private void initListener() {
        tv_title_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_exchange_now.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (modelGift != null) {
                    Intent intent = new Intent(ActivityGiftDetail.this, ActivityGiftExchange.class);
                    intent.putExtra("FLAG", "exchange");
                    intent.putExtra("modelGift", modelGift);
                    startActivity(intent);
                }
            }
        });
    }

    private void initData() {
        if (modelGift != null) {
            getGiftDetail(modelGift.getId());
        }
    }

    public void getGiftDetail(final String id) {

//        loadingView.show(sv_find);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = StaticInApp.GET_GIFT_DETAIL;
                try {
                    msg.obj = ((Thinksns) (ActivityGiftDetail.this.getApplicationContext())).getApiGift().getGiftDetail(id);
                } catch (Exception e) {
                    e.printStackTrace();
//                    loadingView.hide(sv_find);
                }
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    public class GiftHandler extends Handler {
        public GiftHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case StaticInApp.GET_GIFT_DETAIL:

                    try {
                        if (msg.obj != null) {
                            JSONObject result = new JSONObject(msg.obj.toString());
                            if (result.has("name"))
                                tv_detail_name.setText(result.getString("name"));
                            if (result.has("brief"))
                                tv_detail_brief.setText(result.getString("brief"));
                            if (result.has("image"))

                                Glide.with(Thinksns.getContext()).load(result.getString("image"))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .crossFade()
                                        .into(iv_detail);

                            if (result.has("score"))
                                tv_detail_buttom_score.setText(result.getString("score"));
                            if (result.has("count"))
                                tv_detail_count.setText("已有" + result.getString("count") + "人兑换");
                            if (result.has("info"))
                                uint.appendWebViewContentGift(wv_content, wv_head + result.getString("info") + wv_tail);

                                wv_content.setWebChromeClient(new WebChromeClient() {

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
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    loadingView.hide(sv_find);
                    break;
            }
        }
    }

    @Override
    public String getTitleCenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gift_detail;
    }
}
