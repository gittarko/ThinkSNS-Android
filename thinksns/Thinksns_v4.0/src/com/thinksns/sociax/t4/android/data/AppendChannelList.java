package com.thinksns.sociax.t4.android.data;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterChannelList;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.function.FunctionChangeSociaxItemStatus;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.GlideRoundTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelChannel;

/**
 * 类说明： 将频道数据映射到列表
 *
 * @author wz
 * @version 1.0
 * @date 2014-10-16
 */
public class AppendChannelList {
    public AdapterChannelList adapter;
    public ModelChannel currentChannel;
    public Context context;
    public HolderSociax holder;
    public int position;

    public AppendChannelList(Context context) {
        this.context = context;
    }

    public void appendData(int position, View convertView) {
        this.position = position;
        currentChannel = adapter.getItem(position);
        holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
        //显示图片的配置
        Glide.with(context).load(currentChannel.getUserChannelImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(holder.img_channel_icon);

        holder.tv_channel_name.setText(currentChannel.getcName());
        if (currentChannel.getIs_follow() == 1) {
            holder.tv_channel_follow.setBackgroundResource(R.drawable.roundbackground_fav_true);
            holder.tv_channel_follow.setText(R.string.fav_followed);
            holder.tv_channel_follow.setTextColor(context.getResources().getColor(R.color.fav_text_true));
        } else {
            holder.tv_channel_follow.setBackgroundResource(R.drawable.roundbackground_green_digg);
            holder.tv_channel_follow.setText(R.string.fav_add_follow);
            holder.tv_channel_follow.setTextColor(context.getResources().getColor(R.color.fav_border));
        }

        holder.tv_channel_des.setText("已产生"
                + (currentChannel.getCount().equals("") ? "0" : currentChannel
                .getCount() + "条内容"));
        holder.tv_channel_follow.setTag(R.id.tag_position, position);

    }
}
