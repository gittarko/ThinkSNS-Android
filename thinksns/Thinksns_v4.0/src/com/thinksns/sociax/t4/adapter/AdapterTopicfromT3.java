package com.thinksns.sociax.t4.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.modle.RecentTopic;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;


public class AdapterTopicfromT3 extends ListBaseAdapter<RecentTopic> {
	public AdapterTopicfromT3(Context context) {
		super(context);
	}

	@Override
	public View getRealView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHodler = null;
		if (convertView == null
				|| convertView.getTag(R.id.tag_viewholder) == null) {
			viewHodler = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.topic_list_item, null);
			viewHodler.textView = (TextView) convertView
					.findViewById(R.id.tv_name);
			convertView.setTag(R.id.tag_viewholder, viewHodler);
		} else {
			viewHodler = (ViewHolder) convertView.getTag(R.id.tag_viewholder);
		}

		RecentTopic reTopic = (RecentTopic) getItem(position);
		if(reTopic != null) {
			if(!TextUtils.isEmpty(reTopic.getName()))
				viewHodler.textView.setText("#" + reTopic.getName() + "#");
			else
				viewHodler.textView.setText("");
		}
		return convertView;
	}

	private class ViewHolder {
		TextView textView;
	}

	@Override
	public int getMaxId() {
		if(getLast() == null)
			return 0;
		return getLast().getTopic_id();
	}

}
