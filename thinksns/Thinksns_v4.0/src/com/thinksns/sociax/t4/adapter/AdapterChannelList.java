package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.data.AppendChannelList;
import com.thinksns.sociax.t4.android.interfaces.ChannelViewInterface;
import com.thinksns.sociax.t4.android.widget.roundimageview.RoundedImageView;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelChannel;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

/**
 * 类说明： 频道列表 默认获取所有频道
 * @version 1.0
 */
public class AdapterChannelList extends ListBaseAdapter<ModelChannel> {
	protected AppendChannelList append;
	protected ChannelViewInterface listener;

	public int getMaxId() {
		if (getLast() == null)
			return 0;
		else
			return getLast().getId();
	}

	/**
	 * 从Activity生成的频道adapter
	 * 
	 * @param context
	 */
	public AdapterChannelList(Context context, ChannelViewInterface listener) {
		super(context);
		append = new AppendChannelList(context);
		this.listener = listener;
	}


	@Override
	public View getRealView(int position, View convertView, ViewGroup parent) {
		HolderSociax holder = null;
		if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
			holder = new HolderSociax();
			LayoutInflater inflater = getLayoutInflater(mContext);
			convertView = inflater.inflate(R.layout.listitem_channel, null);
			//频道图标
			holder.img_channel_icon = (RoundedImageView) convertView
						.findViewById(R.id.img_channel_icon);
			//频道标题
			holder.tv_channel_name = (TextView) convertView
						.findViewById(R.id.tv_channel_name);
			//频道简介
			holder.tv_channel_des = (TextView) convertView
						.findViewById(R.id.tv_channel_des);
			//频道关注按钮
			holder.tv_channel_follow = (TextView) convertView
						.findViewById(R.id.channel_follow);
			holder.tv_channel_follow.setVisibility(View.VISIBLE);
			convertView.setTag(R.id.tag_viewholder, holder);
		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		convertView.setTag(R.id.tag_channel, getItem(position));

		final ModelChannel channel = getItem(position);
		//显示图片的配置
		Glide.with(mContext).load(channel.getUserChannelImageUrl())
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.crossFade()
				.into(holder.img_channel_icon);

		holder.tv_channel_name.setText(channel.getcName());
		if (channel.getIs_follow() == 1) {
			holder.tv_channel_follow.setBackgroundResource(R.drawable.roundbackground_fav_true);
			holder.tv_channel_follow.setText(R.string.fav_followed);
			holder.tv_channel_follow.setTextColor(mContext.getResources().getColor(R.color.fav_text_true));
		} else {
			holder.tv_channel_follow.setBackgroundResource(R.drawable.roundbackground_green_digg);
			holder.tv_channel_follow.setText(R.string.fav_add_follow);
			holder.tv_channel_follow.setTextColor(mContext.getResources().getColor(R.color.fav_border));
		}

		String content_count = mContext.getResources().getString(R.string.channel_content_count);
		content_count = String.format(content_count, channel.getCount());
		holder.tv_channel_des.setText(content_count);
		holder.tv_channel_follow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.postAddFollow(v, channel);
				}
			}
		});

		return convertView;
	}

}
