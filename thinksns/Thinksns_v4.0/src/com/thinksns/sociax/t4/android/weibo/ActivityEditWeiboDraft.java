package com.thinksns.sociax.t4.android.weibo;

import android.content.Intent;

import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.img.Bimp;
import com.thinksns.sociax.t4.model.ModelDraft;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.t4.service.ServiceUploadWeibo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 编辑微博草稿
 * 包括文本，图片，视频微博,频道微博，话题微博
 */
public class ActivityEditWeiboDraft extends ActivityCreateBase{
    private ArrayList<String> old;      //修改前的图片集合
    private int channel_id = 0;

    protected int oldType = AppConstant.CREATE_TEXT_WEIBO;

    @Override
    protected void initIntent() {
        mDraft = (ModelDraft)getIntent().getSerializableExtra("draft");
        oldType = type = mDraft.getType();

        content = mDraft.getContent();
        channel_id = mDraft.getChannel_id();

        if(mDraft.isHasVideo()) {
            staticVideoPath = mDraft.getVideoPath();
            type = AppConstant.CREATE_VIDEO_WEIBO;
        }else if(mDraft.isHasImage()) {
            //读取已选图片
            Bimp.address = mDraft.getImageList();
            //复制
            old = new ArrayList<String>(Bimp.address);
            type = AppConstant.CREATE_ALBUM_WEIBO;
        }

        if(mDraft.getAddress() != null) {
            //设置地理位置信息
            latitude = Double.parseDouble(mDraft.getLatitude());
            longitude = Double.parseDouble(mDraft.getLongitude());
            address = mDraft.getAddress();
        }

    }

    @Override
    protected void initData() {
        super.initData();
        if(channel_id != 0) {
            ((ModelWeibo)data).setType(channel_id + "");
        }
    }

    @Override
    protected void initDraft() {
        super.initDraft();
    }

    @Override
    protected boolean needSaveDraft() {
        if(mDraft.getType() != type
                && type != AppConstant.CREATE_TEXT_WEIBO) {
            //修改了草稿
            return true;
        }

        content = getTextContent();
        if(type == AppConstant.CREATE_TEXT_WEIBO) {
            if(content.length() == 0) {
                //放弃编辑
                return false;
            }
            //比较文本内容
            if(!mDraft.getContent().equals(content))
                return true;
        }else if(type == AppConstant.CREATE_ALBUM_WEIBO) {
            //先比较内容是否改变
            if(!mDraft.getContent().equals(content)) {
                return true;
            }
            //比较选择的照片是否改变
            if(old != null
                    && old.size() == Bimp.address.size()) {
                for(int i=0; i<old.size(); i++) {
                    if(Bimp.address.contains(old.get(i)))
                        continue;
                    return true;
                }
            }else {
                return true;
            }
        }else if(type == AppConstant.CREATE_VIDEO_WEIBO) {
            if(!staticVideoPath.equals(mDraft.getVideoPath())) {
                return true;
            }
            if(!mDraft.getContent().equals(content))
                return true;
        }else if(type == AppConstant.CREATE_TRANSPORT_WEIBO) {
            //转发微博比较文本内容
            if(content.length() == 0)
                return false;
            if(!mDraft.getContent().equals(content)) {
                return true;
            }
        }

        //判断是否修改了地理位置信息
        if(latitude > 0 && longitude > 0
                    && !String.valueOf(latitude).equals(mDraft.getLatitude())
                && !String.valueOf(longitude).equals(mDraft.getLongitude())) {
            return true;
        }

        return false;
    }

    @Override
    protected void startUploadService(Intent intent) {
        super.startUploadService(intent);
        //从列表中删除对应的微博,只是从内存中删除
        EventBus.getDefault().post(mDraft);
    }
}
