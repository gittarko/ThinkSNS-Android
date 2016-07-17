package com.thinksns.sociax.t4.android.weibo;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.maps.model.Text;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.model.ModelDraft;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.service.ServiceUploadWeibo;

/**
 * Created by hedong on 16/4/17.
 * 创建帖子/转发帖子
 */
public class ActivityCreatePost extends ActivityCreateBase{
    private int weiba_id;
    private int oldType;

    @Override
    protected void initIntent() {
        super.initIntent();
        oldType = type;
        weiba_id = getIntent().getIntExtra("weiba_id", 0);
    }

    @Override
    protected void initData() {
        data = new ModelPost();
        ((ModelPost)data).setWeiba_id(weiba_id);
    }

    @Override
    protected boolean needEditTitle() {
        return true;
    }

    @Override
    protected boolean needLocation() {
        return false;
    }

    //帖子支持图片插入
    @Override
    protected boolean needPicture() {
        return true;
    }

    @Override
    protected boolean needVideo() {
        return false;
    }

    @Override
    protected boolean needSaveDraft() {
        //如果编辑框和标题都有内容则保存
        if (!TextUtils.isEmpty(getTextContent())
                && !TextUtils.isEmpty(getEditTitle()))
            return true;
        return false;
    }

    @Override
    protected void getDraft() {
        super.getDraft();
        //帖子的草稿内容设置成标题
        mDraft.setTitle(getEditTitle());
    }

    @Override
    protected void addCacheDraft() {
        //保存缓存前
        mDraft.setType(oldType);
        mDraft.setFeed_id(weiba_id);
        super.addCacheDraft();
    }

    @Override
    protected boolean checkDataReady() {
        if(TextUtils.isEmpty(getEditTitle())) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(getTextContent())) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }else if(weiba_id == 0) {
            Toast.makeText(this, "未知微吧分类", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void packageData() {
        ((ModelPost)data).setTitle(getEditTitle());
        ((ModelPost)data).setContent(getTextContent());
        //在这个步骤重新设置发布文章的类型
        type = oldType;
        mDraft.setFeed_id(weiba_id);
        mDraft.setType(type);
    }
}
