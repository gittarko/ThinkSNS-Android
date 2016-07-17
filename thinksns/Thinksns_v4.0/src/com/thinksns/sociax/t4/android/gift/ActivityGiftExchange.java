package com.thinksns.sociax.t4.android.gift;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.model.ModelMyGifts;
import com.thinksns.sociax.t4.model.ModelShopGift;
import com.thinksns.sociax.t4.model.ModelUser;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类说明：兑换礼物
 *
 * @author Zoey
 * @version 1.0
 * @date 2015年9月21日
 */
public class ActivityGiftExchange extends ThinksnsAbscractActivity {

    private TextView tv_exchange_name, tv_score, tv_exchange_now, tv_exchange_detail, tv_title_center, tv_txt;
    private ImageView iv_exchange, tv_title_back, iv_integral;
    private Button btn_num_sub, btn_num, btn_num_plus;
    private ModelShopGift modelGift = null;
    private static ModelMyGifts modelMyGifts = null;
    private static int count = 1;
    private TextView tv_receiver_nickname;
    private EditText et_receiver_wish;
    private EditText et_true_name, et_way_of_contact, et_address;
    private LinearLayout ll_exchange_normal_info, ll_exchange_entity_info, ll_info_detail;
    private RelativeLayout rl_pick_num, rl_way;
    private ExchangeHandler mHandler = null;
    private static int uid = 0;
    private RadioGroup rl_way_of_present;
    private RadioButton rb_way_anonymous, rb_way_open, rb_way_private;
    private final static String SELECTED_ANONYMOUS = "1";// 匿名赠送
    private final static String SELECTED_OPEN = "2";// 公开赠送
    private final static String SELECTED_PRIVATE = "3";// 私下赠送
    private static String type = SELECTED_ANONYMOUS;//默认为匿名赠送
    private static String logId = null;

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
            String FLAG = intent.getStringExtra("FLAG");
            if (FLAG == null) {
                return;
            }
            //兑换
            if (FLAG.equals("exchange")) {
                modelGift = (ModelShopGift) intent.getSerializableExtra("modelGift");
            }
            //转赠
            else if (FLAG.equals("transfer")) {
                modelMyGifts = (ModelMyGifts) intent.getSerializableExtra("modelMyGift");
                modelGift = new ModelShopGift();
                modelGift.setId(modelMyGifts.getId());
                modelGift.setName(modelMyGifts.getName());
                modelGift.setBrief(modelMyGifts.getBrief());
                modelGift.setInfo(modelMyGifts.getInfo());
                modelGift.setImage(modelMyGifts.getImage());
                modelGift.setScore(modelMyGifts.getScore());
                modelGift.setStock(modelMyGifts.getStock());
                modelGift.setMax(modelMyGifts.getMax());
                modelGift.setTime(modelMyGifts.getDate());
                modelGift.setCate(modelMyGifts.getCate());
                logId = modelMyGifts.getLogId();
                Log.v("transfermyGift", "------set----logId---" + logId);
            }
        }
    }

    private void initView() {
        iv_exchange = (ImageView) findViewById(R.id.iv_exchange);

        tv_title_back = (ImageView) findViewById(R.id.tv_title_left);
        iv_integral = (ImageView) findViewById(R.id.iv_integral);
        tv_exchange_name = (TextView) findViewById(R.id.tv_exchange_name);
        tv_exchange_detail = (TextView) findViewById(R.id.tv_exchange_detail);
        tv_score = (TextView) findViewById(R.id.tv_score);
        tv_exchange_now = (TextView) findViewById(R.id.tv_exchange_now);

        tv_title_center = (TextView) findViewById(R.id.tv_title_center);
        tv_txt = (TextView) findViewById(R.id.tv_txt);

        tv_receiver_nickname = (TextView) findViewById(R.id.tv_receiver_nickname);
        et_receiver_wish = (EditText) findViewById(R.id.et_receiver_wish);
        et_true_name = (EditText) findViewById(R.id.et_true_name);
        et_way_of_contact = (EditText) findViewById(R.id.et_way_of_contact);
        et_address = (EditText) findViewById(R.id.et_address);

        ll_exchange_normal_info = (LinearLayout) findViewById(R.id.ll_exchange_normal_info);
        ll_exchange_entity_info = (LinearLayout) findViewById(R.id.ll_exchange_entity_info);

        rl_pick_num = (RelativeLayout) findViewById(R.id.rl_pick_num);
        rl_way = (RelativeLayout) findViewById(R.id.rl_way);

        btn_num_sub = (Button) findViewById(R.id.btn_num_sub);
        btn_num = (Button) findViewById(R.id.btn_num);
        btn_num_plus = (Button) findViewById(R.id.btn_num_plus);

        rl_way_of_present = (RadioGroup) findViewById(R.id.rg_way_of_present);
        rb_way_anonymous = (RadioButton) findViewById(R.id.rb_way_anonymous);
        rb_way_open = (RadioButton) findViewById(R.id.rb_way_open);
        rb_way_private = (RadioButton) findViewById(R.id.rb_way_private);

        mHandler = new ExchangeHandler();
    }

    private void initListener() {
        tv_title_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_num_sub.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (count > 1) {
                    btn_num_plus.setClickable(true);
                    count--;
                    btn_num.setText(count + "");
                } else {
                    btn_num_sub.setClickable(false);
                    btn_num.setText(1 + "");
                }
            }
        });
        btn_num_plus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                btn_num_sub.setClickable(true);
//                if (count < Integer.parseInt(modelGift.getMax())) {
                    count++;
                    btn_num.setText(count + "");
//                } else {
//                    btn_num_plus.setClickable(false);
//                    btn_num.setText(modelGift.getMax());
//                    Toast.makeText(ActivityGiftExchange.this, "您已达上限", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        tv_exchange_now.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String say = et_receiver_wish.getText().toString();
                String addres = et_address.getText().toString();
                String num = btn_num.getText().toString();
                String name = et_true_name.getText().toString();
                String phone = et_way_of_contact.getText().toString();

                if (modelGift == null) {
                    return;
                }

                if (tv_exchange_name.getText().toString() != null && !tv_exchange_name.equals("")) {
                    if (getIntent() != null && getIntent().getStringExtra("FLAG").equals("transfer") && logId != null) {
                        transferNow(logId, uid, say, Integer.parseInt(num), type);
                    } else if (getIntent() != null && getIntent().getStringExtra("FLAG").equals("exchange")) {
                        exchangeNow(modelGift.getId(), uid, Integer.parseInt(num), addres, say, type, phone, name);
                    }
                } else {
                    Toast.makeText(ActivityGiftExchange.this, "请选择你要送出的对象", 1).show();
                }
            }
        });

        rl_way_of_present.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rb_way_anonymous.getId() == checkedId) {
                    type = SELECTED_ANONYMOUS;
                    rb_way_anonymous.setBackground(ActivityGiftExchange.this.getResources().getDrawable(R.drawable.rec_bg_1_blue));
                    rb_way_open.setBackground(ActivityGiftExchange.this.getResources().getDrawable(R.drawable.rec_bg_1_white));
                    rb_way_private.setBackground(ActivityGiftExchange.this.getResources().getDrawable(R.drawable.rec_bg_1_white));

                    rb_way_anonymous.setTextColor(ActivityGiftExchange.this.getResources().getColor(R.color.white));
                    rb_way_open.setTextColor(ActivityGiftExchange.this.getResources().getColor(R.color.bar));
                    rb_way_private.setTextColor(ActivityGiftExchange.this.getResources().getColor(R.color.bar));
                } else if (rb_way_open.getId() == checkedId) {
                    type = SELECTED_OPEN;

                    rb_way_anonymous.setBackground(ActivityGiftExchange.this.getResources().getDrawable(R.drawable.rec_bg_1_white));
                    rb_way_open.setBackground(ActivityGiftExchange.this.getResources().getDrawable(R.drawable.rec_bg_1_blue));
                    rb_way_private.setBackground(ActivityGiftExchange.this.getResources().getDrawable(R.drawable.rec_bg_1_white));

                    rb_way_anonymous.setTextColor(ActivityGiftExchange.this.getResources().getColor(R.color.bar));
                    rb_way_open.setTextColor(ActivityGiftExchange.this.getResources().getColor(R.color.white));
                    rb_way_private.setTextColor(ActivityGiftExchange.this.getResources().getColor(R.color.bar));
                } else if (rb_way_private.getId() == checkedId) {
                    type = SELECTED_PRIVATE;

                    rb_way_anonymous.setBackground(ActivityGiftExchange.this.getResources().getDrawable(R.drawable.rec_bg_1_white));
                    rb_way_open.setBackground(ActivityGiftExchange.this.getResources().getDrawable(R.drawable.rec_bg_1_white));
                    rb_way_private.setBackground(ActivityGiftExchange.this.getResources().getDrawable(R.drawable.rec_bg_1_blue));

                    rb_way_anonymous.setTextColor(ActivityGiftExchange.this.getResources().getColor(R.color.bar));
                    rb_way_open.setTextColor(ActivityGiftExchange.this.getResources().getColor(R.color.bar));
                    rb_way_private.setTextColor(ActivityGiftExchange.this.getResources().getColor(R.color.white));
                }
            }
        });

        tv_receiver_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ActivityGiftExchange.this, ActivityFindGiftReceiver2.class);
//                Intent intent = new Intent(ActivityGiftExchange.this, ActivityFindGiftReceiver.class);
                startActivityForResult(intent, StaticInApp.REQUEST_CODE_SELET_GIFT_RECEIVER);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == StaticInApp.RESULT_CODE_SELET_GIFT_RECEIVER
                && requestCode == StaticInApp.REQUEST_CODE_SELET_GIFT_RECEIVER) {
            if (data != null) {
                ModelUser model = (ModelUser) data.getSerializableExtra("user");
                tv_receiver_nickname.setText(model.getUserName());
                uid = model.getUid();
                Log.v("transfermyGift", "------set----uid---" + uid);
            }
        }
    }

    private void initData() {
        if (modelGift != null) {
            tv_exchange_name.setText(modelGift.getName());
            tv_exchange_detail.setText(modelGift.getBrief());
            tv_score.setText(modelGift.getScore());
            //虚拟礼物
            if (modelGift.getCate().equals("1")) {
                ll_exchange_entity_info.setVisibility(View.GONE);
            }
            //实体礼物
            else if (modelGift.getCate().equals("2")) {
                ll_exchange_entity_info.setVisibility(View.VISIBLE);
            }

            Glide.with(this).load(modelGift.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(iv_exchange);

        }
        if (getIntent() != null && getIntent().getStringExtra("FLAG").equals("transfer")) {
            tv_exchange_now.setText("确定");
            tv_title_center.setText("礼物转赠");
            tv_txt.setVisibility(View.GONE);
            tv_score.setVisibility(View.GONE);
            iv_integral.setVisibility(View.GONE);
        }
    }

    @Override
    public String getTitleCenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gift_exchange;
    }

    public void exchangeNow(final String id, final int uid, final int num, final String addres, final String say, final String type, final String phone, final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message msg = new Message();
                msg.what = StaticInApp.EXCHANGE_NOW;
                try {
                    msg.obj = ((Thinksns) (ActivityGiftExchange.this.getApplicationContext())).getApiGift().exchangeGift(id, uid, num, addres, say, type, phone, name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendMessage(msg);

            }
        }).start();
    }

    public void transferNow(final String id, final int uid, final String say, final int num, final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message msg = new Message();
                msg.what = StaticInApp.TRANSFER_GIFT;
                try {
                    msg.obj = ((Thinksns) (ActivityGiftExchange.this.getApplicationContext())).getApiGift().transferMyGift(id, uid, say, num, type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendMessage(msg);

            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    public class ExchangeHandler extends Handler {
        public ExchangeHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case StaticInApp.EXCHANGE_NOW:
                    try {
                        if (msg.obj != null) {
                            JSONObject result = new JSONObject(msg.obj.toString());
                            if (result != null) {
                                String status = result.getString("status");
                                String message = result.getString("mesage");
                                Toast.makeText(ActivityGiftExchange.this, message, 1).show();
                                if (status.equals("1")) {

                                    Intent intent = new Intent(ActivityGiftExchange.this, ActivityMyGift.class);
                                    intent.putExtra("FLAG", 1);
                                    startActivity(intent);

                                    finish();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case StaticInApp.TRANSFER_GIFT:

                    try {
                        if (msg.obj != null) {
                            JSONObject result = new JSONObject(msg.obj.toString());
                            if (result != null) {
                                int status = result.getInt("status");
                                String message = result.getString("message");
                                Toast.makeText(ActivityGiftExchange.this, message, 1).show();
                                if (status == 1) {

                                    Intent intent = new Intent(ActivityGiftExchange.this, ActivityMyGift.class);
                                    intent.putExtra("FLAG", 2);
                                    startActivity(intent);

                                    finish();
                                    if (ActivityMyGift.activity != null) {
                                        ActivityMyGift.activity.finish();
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }
}
