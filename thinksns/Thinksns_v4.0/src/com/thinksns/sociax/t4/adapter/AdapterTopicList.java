package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelTopic;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

/**
 * 类说明：话题列表
 *
 */
public class AdapterTopicList extends ListBaseAdapter<ModelTopic> {

	public AdapterTopicList(Context context) {
		super(context);
	}

	@Override
	public View getRealView(int position, View convertView, ViewGroup parent) {
		HolderSociax holder;
		if (convertView == null ||
				convertView.getTag(R.id.tag_viewholder) == null) {
			holder = new HolderSociax();
			LayoutInflater inflater = getLayoutInflater(mContext);
			convertView = inflater.inflate(R.layout.listitem_topiclist, null);
			holder.tv_topic_type = (TextView) convertView
					.findViewById(R.id.tv_topic_type);
			holder.tv_topic_name = (TextView) convertView
					.findViewById(R.id.tv_topic_name);
			holder.tv_topic_des = (TextView) convertView
					.findViewById(R.id.tv_topic_des);
			convertView.setTag(R.id.tag_viewholder, holder);
		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		convertView.setTag(R.id.tag_topic, getItem(position));
		if (getItem(position).isFirst()) {
			holder.tv_topic_type.setText(getItem(position).getRecommend()
					.equals("1") ? "热门话题" : "最新话题");
			holder.tv_topic_type.setVisibility(View.VISIBLE);
		} else {
			holder.tv_topic_type.setVisibility(View.GONE);
		}

		holder.tv_topic_type.setClickable(false);
		holder.tv_topic_name.setText("#" + getItem(position).getTopic_name()
				+ "#");
		holder.tv_topic_des.setText((getItem(position).getCount().equals("") ? "0" : getItem(
						position).getCount()) + "条讨论");

		return convertView;
	}

	@Override
	public int getMaxId() {
		return getLast() == null ? 0 : ((ModelTopic) getLast()).getTopic_id();
	}

}
