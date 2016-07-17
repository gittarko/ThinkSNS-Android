package com.thinksns.sociax.t4.android.weibo;

import android.text.TextUtils;

import com.thinksns.sociax.t4.model.ModelPost;

/**
 * 帖子草稿箱编辑
 */
public class ActivityEditPostDraft extends ActivityEditWeiboDraft{
    private String postTitle;
    private int weiba_id;

    @Override
    protected void initIntent() {
        super.initIntent();
        postTitle = mDraft.getTitle();
        weiba_id = mDraft.getFeed_id();
    }

    @Override
    protected void initData() {
        //先初始化业务对象
        data = new ModelPost();
        //在调用基类公共方法
        super.initData();

        if(!TextUtils.isEmpty(postTitle))
            setEditTitle(postTitle);
    }

    @Override
    protected boolean needVideo() {
        return false;
    }

    @Override
    protected boolean needEditTitle() {
        return true;
    }

    @Override
    protected boolean needSaveDraft() {
        if(!mDraft.getTitle().equals(getEditTitle())) {
            return true;
        }
        return super.needSaveDraft();
    }

    @Override
    protected void addCacheDraft() {
        //如果帖子修改了，重新设置类型
        mDraft.setType(oldType);
        super.addCacheDraft();
    }
}
