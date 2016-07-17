package com.thinksns.sociax.t4.android.weibo;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.model.ModelDraft;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.model.ModelWeibo;

/**
 * 创建转发微博,转发帖子
 */
public class ActivityCreateTransportWeibo extends ActivityCreateBase{
    protected int sourceId;
    private String feedName;

    @Override
    protected void initIntent() {
        super.initIntent();
        sourceId = getIntent().getIntExtra("feed_id", 0);
        feedName = getIntent().getStringExtra("feed_name");
        //是否携带转发内容
        if(getIntent().hasExtra("content")) {
            content = getIntent().getStringExtra("content");
        }
    }

    @Override
    protected String getRightBtnText() {
        return "转发";
    }

    @Override
    protected void initData() {
        if(type == AppConstant.CREATE_TRANSPORT_WEIBO) {
            data = new ModelWeibo();
            ((ModelWeibo)data).setWeiboId(sourceId);
        }else if(type == AppConstant.CREATE_TRANSPORT_POST) {
            data = new ModelPost();
            ((ModelPost)data).setPost_id(sourceId);
        }

    }

    @Override
    protected void initView() {
        super.initView();
//        if(!TextUtils.isEmpty(feedName)) {
//            //设置标题样式
//            TextView titleCenter = (TextView)getCustomTitle().getCenter();
//            //文字大小
//            titleCenter.setTextSize(getResources().getDimensionPixelSize(R.dimen.dimen_size_12));
//            //文字颜色
//        }
    }

    @Override
    protected boolean needSaveDraft() {
        if(TextUtils.isEmpty(getTextContent())) {
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    protected void initDraft() {
        super.initDraft();
    }

    @Override
    protected boolean needPicture() {
        return false;
    }

    @Override
    protected boolean needVideo() {
        return false;
    }

    @Override
    protected boolean needLocation() {
        return false;
    }

    @Override
    protected String getContent() {
        String content = getTextContent();
        if(TextUtils.isEmpty(content)){
            if(type == AppConstant.CREATE_TRANSPORT_POST) {
                content = "转发帖子";
            }else if(type == AppConstant.CREATE_TRANSPORT_WEIBO) {
                content = "转发分享";
            }
        }
        return content;
    }

    @Override
    protected void getDraft() {
        if(mDraft == null) {
            mDraft = new ModelDraft();
        }

        mDraft.setFeed_id(sourceId);
    }

    @Override
    protected boolean checkDataReady() {
        if(sourceId == 0) {
            Toast.makeText(this, "源内容缺失", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.checkDataReady();
    }

    @Override
    protected void packageData() {
        if(type == AppConstant.CREATE_TRANSPORT_WEIBO) {
            super.packageData();
        }else if(type == AppConstant.CREATE_TRANSPORT_POST){
            ((ModelPost)data).setContent(getContent());
        }
    }
}
