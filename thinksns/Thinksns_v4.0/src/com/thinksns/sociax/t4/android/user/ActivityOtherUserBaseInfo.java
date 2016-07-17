package com.thinksns.sociax.t4.android.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.temp.SelectImageListener;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.SmallDialog;
import com.thinksns.sociax.t4.model.ModelUser;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 其他用户的基本信息
 *
 * @author wz
 * @version 1.0
 * @date 2015-1-28
 */
public class ActivityOtherUserBaseInfo extends ThinksnsAbscractActivity {
    private LinearLayout ll_uploadFace, ll_change_name, ll_change_city;
    private LinearLayout ll_sex, ll_tag;

    private TextView tv_intro, tv_uname, tv_sex, tv_tag, tv_city;
    private TextView tv_score;
    private ImageView img_level;
    private ImageView tv_face;
    private TextView tv_uploadFace;

    private SelectImageListener changeListener;
    public int UPLOAD_FACE = 4;
    private Bitmap newHeader;
    private SmallDialog smallDialog;
    private String input;

    ModelUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntentData();
        initView();
        initOnClickListener();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();

        tv_uname.setText(intent.getStringExtra("uname"));
        tv_city.setText(intent.getStringExtra("city"));
        //设置简介
        String intro = intent.getStringExtra("intro");
        if (intro == null || intro.isEmpty() || intro.equals("null") || intro.equals("暂无简介")) {
            tv_intro.setText("这家伙很懒，什么也没留下");
        } else {
            tv_intro.setText(intro);
        }

        Glide.with(ActivityOtherUserBaseInfo.this).load(intent.getStringExtra("uface"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new GlideCircleTransform(ActivityOtherUserBaseInfo.this))
                .crossFade()
                .into(tv_face);

        tv_score.setText(intent.getStringExtra("score"));

        if (intent.getStringExtra("level") != null) {

            img_level.setVisibility(View.VISIBLE);
            try {
                img_level.setImageResource(UnitSociax.getResId(this, "icon_level" + intent.getStringExtra("level"), "drawable"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            img_level.setVisibility(View.GONE);
        }

        if(user != null) {
            tv_sex.setText(user.getSex());
            if(TextUtils.isEmpty(user.getUserTag()))
                tv_tag.setText("暂无标签");
            else
                tv_tag.setText(user.getUserTag());
        }
    }

    private void initOnClickListener() {
    }

    private void initIntentData() {
        user = (ModelUser)getIntent().getSerializableExtra("user");
    }

    private void initView() {
        ll_change_city = (LinearLayout) findViewById(R.id.ll_change_city);
        ll_change_name = (LinearLayout) findViewById(R.id.ll_change_name);
        ll_uploadFace = (LinearLayout) findViewById(R.id.ll_uploadFace);
        tv_uploadFace = (TextView) findViewById(R.id.tv_uploadFace);

        tv_face = (ImageView) findViewById(R.id.tv_face);
        tv_score = (TextView) findViewById(R.id.tv_score);
        img_level = (ImageView) findViewById(R.id.img_level);

        changeListener = new SelectImageListener(
                ActivityOtherUserBaseInfo.this, tv_uploadFace);

        smallDialog = new SmallDialog(this, getString(R.string.please_wait));
        tv_sex = (TextView)findViewById(R.id.tv_sex);
        tv_tag = (TextView)findViewById(R.id.tv_tag);

        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_intro = (TextView) findViewById(R.id.tv_intro);
        tv_uname = (TextView) findViewById(R.id.tv_uname);

    }

    @Override
    public String getTitleCenter() {
        return "基本信息";
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_other_user_base_info;
    }
}
