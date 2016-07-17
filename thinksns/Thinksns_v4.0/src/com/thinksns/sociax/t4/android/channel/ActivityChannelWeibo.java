package com.thinksns.sociax.t4.android.channel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentChannelWeibo;
import com.thinksns.sociax.t4.android.fragment.FragmentWeiboListViewChannel;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateChannelWeibo;
import com.thinksns.sociax.t4.android.weibo.ActivityCreateWeibo;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明： 某个频道里面的微博列表
 * 需要传入 int channel_id,String channel_name
 *
 * @author wz
 * @version 1.0
 * @date 2014-10-15
 */
public class ActivityChannelWeibo extends ThinksnsAbscractActivity {
    Fragment fragment;
    private ImageButton ib_new;
    private int channel_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = new FragmentWeiboListViewChannel();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 2);
        channel_id = getIntent().getIntExtra("channel_id",0);
        bundle.putInt("channel_id", channel_id);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.ll_content, fragment);
        fragmentTransaction.commit();
        //发布新分享图标
        ib_new = (ImageButton) findViewById(R.id.ib_new);
        ib_new.setOnClickListener(getRightListener());
        ib_new.setVisibility(View.GONE);
    }

    @Override
    public String getTitleCenter() {
        return getIntent().getStringExtra("channel_name");
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(this, R.drawable.img_back, null);
    }

    @Override
    public OnClickListener getRightListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
//                Intent intent = getIntent();
                intent.putExtra("type", AppConstant.CREATE_TEXT_WEIBO);
                //传递频道ID
                intent.putExtra("channel_id", channel_id);
                intent.putExtra("channel_name", "");
                intent.putExtra("is_transport", false);
                intent.setClass(ActivityChannelWeibo.this, ActivityCreateChannelWeibo.class);
                startActivity(intent);
                Anim.exit(ActivityChannelWeibo.this);
            }
        };

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_channel_weibo;
    }

    /**
     * 隐藏或显示发布微博按钮
     * @param isShow
     */
    public void toggleCreateBtn(boolean isShow) {
        if(isShow && ib_new != null) {
            ib_new.setVisibility(View.VISIBLE);
        }else {
            ib_new.setVisibility(View.GONE);
        }
    }

}
