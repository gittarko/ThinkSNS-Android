package com.thinksns.sociax.t4.android.weibo;

import android.widget.Toast;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * Created by hedong on 16/4/17.
 * 创建频道微博/转发频道微博
 */
public class ActivityCreateChannelWeibo extends ActivityCreateBase{

    private int channel_id;
    private String channel_name;
    private boolean isTransport = false;

    @Override
    protected void initIntent() {
        super.initIntent();
        channel_id = getIntent().getIntExtra("channel_id", 0);
        channel_name = getIntent().getStringExtra("channel_name");
        isTransport = getIntent().getBooleanExtra("is_transport", false);
    }

    @Override
    protected String getRightBtnText() {
        return isTransport ? "转发" : "发布";
    }


    @Override
    protected void initData() {
        super.initData();
        ((ModelWeibo)data).setType(channel_id + "");
        ((ModelWeibo)data).setFrom(channel_name);
    }

    @Override
    protected void getDraft() {
        super.getDraft();
        mDraft.setType(AppConstant.CREATE_CHANNEL_WEIBO);
        //保存频道ID和频道名称
        mDraft.setChannel_id(channel_id);
        mDraft.setChannel_name(channel_name);
    }

    @Override
    protected boolean checkDataReady() {
        if(getTextContent().isEmpty()) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.checkDataReady();
    }
}
