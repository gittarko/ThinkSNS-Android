package com.thinksns.sociax.t4.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.model.ModelAllTag;
import com.thinksns.sociax.t4.model.ModelUserTagandVerify.Child;

import java.util.List;

/**
 * 类说明：标签用户
 * 
 * @author xhs
 * @date 2014-9-26
 * @version 1.0
 */
public class AdapterTagList extends BaseAdapter {
	private static final String TAG = "TagPersonAdapter";
	private List<Child> list;
	private LayoutInflater inflater;
	private onItemTagClickListener listener;

	public AdapterTagList(LayoutInflater inflater) {
		super();
		this.inflater = inflater;
	}

	public void bindData(List<Child> list) {
		this.list = list;
		Log.d(TAG, list.toString());
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		MyHolder holder;
		if (convertView == null) {
			holder = new MyHolder();
			convertView = inflater.inflate(R.layout.list_item_tag, null);
			holder.title = (TextView) convertView
					.findViewById(R.id.tv_tag_cloud);
			convertView.setTag(holder);
		} else {
			holder = (MyHolder) convertView.getTag();
		}
		if (list.get(position) != null && list.get(position).getTitle() != null) {
			final String title = list.get(position).getTitle();
			holder.title.setText(list.get(position).getTitle());
			holder.title.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onTitleClick(title);
						listener.onTitleClick(list.get(position));
					}
				}
			});
		} else {
			holder.title.setText(null);
		}

		return convertView;
	}

	class MyHolder {
		TextView title;
	}

	public interface onItemTagClickListener {
		void onTitleClick(String title);
		void onTitleClick(Child child);
	}

	public void setListener(onItemTagClickListener listener) {
		this.listener = listener;
	}
}
