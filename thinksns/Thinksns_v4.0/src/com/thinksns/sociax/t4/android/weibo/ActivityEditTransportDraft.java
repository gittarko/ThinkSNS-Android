package com.thinksns.sociax.t4.android.weibo;

import android.content.Intent;

import com.thinksns.sociax.t4.model.ModelDraft;

import org.greenrobot.eventbus.EventBus;

/**
 * 转发草稿编辑页
 * 包括转发微博、转发帖子
 */
public class ActivityEditTransportDraft extends ActivityCreateTransportWeibo{
    @Override
    protected void initIntent() {
        mDraft = (ModelDraft)getIntent().getSerializableExtra("draft");
        type = mDraft.getType();
        content = mDraft.getContent();
        sourceId = mDraft.getFeed_id();
    }

    @Override
    protected void initData() {
        super.initData();
        setTextContent(content);
    }

    @Override
    protected boolean needSaveDraft() {
        if(mDraft.getContent().equals(getTextContent()))
            return false;
        return true;
    }

    @Override
    protected void startUploadService(Intent intent) {
        super.startUploadService(intent);
        EventBus.getDefault().post(mDraft);
    }
}
