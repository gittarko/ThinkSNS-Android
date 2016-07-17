package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.constant.AppConstant;
import com.thinksns.sociax.t4.android.Thinksns;

import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelDraft;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.utils.TimeHelper;

/**
 * 类说明：
 *
 * @author Administrator
 * @version 1.0
 * @date 2014-11-10
 */
public class AdapterDraftWeiboList extends ListBaseAdapter<ModelDraft> {
    int uid = 0;
    public AdapterDraftWeiboList(Context context) {
        super(context);
        if(Thinksns.getMy() != null)
            uid = Thinksns.getMy().getUid();
    }

    @Override
    public View getRealView(int position, View convertView, ViewGroup parent) {
        HolderSociax holder = null;
        if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
            holder = new HolderSociax();
            LayoutInflater inflater = getLayoutInflater(mContext);
            convertView = inflater.inflate(R.layout.listitem_weibo_draft, null);
            holder.tv_ctime = (TextView) convertView.findViewById(R.id.tv_ctime);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_des = (TextView) convertView.findViewById(R.id.tv_des);
            holder.img_send = (ImageView) convertView.findViewById(R.id.img_send);
            convertView.setTag(R.id.tag_viewholder, holder);
        } else {
            holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        }

        ModelDraft draft = getItem(position);
        holder.tv_ctime.setText(TimeHelper.friendlyTime(draft.getCtime()));
        String content = draft.getContent();
        int type = draft.getType();
        switch (type) {
            case AppConstant.CREATE_TEXT_WEIBO:
                holder.tv_title.setText("分享");
                if(TextUtils.isEmpty(content)) {
                    content = "无文字内容";
                }
                break;
            case AppConstant.CREATE_ALBUM_WEIBO:
                holder.tv_title.setText("分享");
                if(TextUtils.isEmpty(content)) {
                    content = "[图片]无文字内容";
                }
                break;
            case AppConstant.CREATE_VIDEO_WEIBO:
                if(TextUtils.isEmpty(content)) {
                    content = "[视频]无文字内容";
                }
                break;
            case AppConstant.CREATE_WEIBA_POST:
                holder.tv_title.setText("帖子");
                if(TextUtils.isEmpty(content)) {
                    content = "[帖子]无文字内容";
                }else {
                    content = draft.getTitle();
                }
                break;
            case AppConstant.CREATE_TRANSPORT_WEIBO:
                holder.tv_title.setText("转发分享");
                if(TextUtils.isEmpty(content)) {
                    content = "无文字内容";
                }
                break;
            case AppConstant.CREATE_TRANSPORT_POST:
                holder.tv_title.setText("转发帖子");
                if(TextUtils.isEmpty(content)) {
                    content = "[帖子]无文字内容";
                }
                break;
            case AppConstant.CREATE_TOPIC_WEIBO:
                holder.tv_title.setText("话题");
                if(TextUtils.isEmpty(content)) {
                    content = "无文字内容";
                }
                break;
            case AppConstant.CREATE_CHANNEL_WEIBO:
                holder.tv_title.setText("来自频道");
                if(TextUtils.isEmpty(content)) {
                    if(draft.isHasImage())
                        content = "[图片]无文字内容";
                    else if(draft.isHasVideo())
                        content = "[视频]无文字内容";
                }
                break;
            default:
                break;
        }

        holder.tv_des.setText(UnitSociax.showContentLintView(mContext, content));

        convertView.setTag(R.id.tag_object, draft);

        return convertView;
    }


    @Override
    public int getMaxId() {
        return mDatas.size() == 0 ? 0 : getItem(mDatas.size() - 1).getId();
    }

}
