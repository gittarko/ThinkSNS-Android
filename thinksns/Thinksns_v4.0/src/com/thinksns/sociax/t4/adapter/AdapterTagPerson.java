package com.thinksns.sociax.t4.adapter;

import java.util.List;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.findpeople.ActivitySearchUser;
import com.thinksns.sociax.t4.model.ModelUserTagandVerify.Child;
import com.thinksns.sociax.android.R;

/**
 * 类说明：标签用户
 * 
 * @author xhs
 * @date 2014-9-26
 * @version 1.0
 */
public class AdapterTagPerson extends BaseAdapter {
	private static final String TAG = "TagPersonAdapter";
	private List<Child> list;
	private LayoutInflater inflater;
	private int type;

	public AdapterTagPerson(LayoutInflater inflater, int type) {
		super();
		this.inflater = inflater;
		this.type = type;
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
	public View getView(int position, View convertView, ViewGroup parent) {
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
			if (type == StaticInApp.FINDPEOPLE_TAG) {
				final int tag_id = Integer.parseInt(list.get(position)
						.getId());
				convertView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(),
								ActivitySearchUser.class);
						intent.putExtra("type", StaticInApp.FINDPEOPLE_TAG);
						intent.putExtra("tag_id", tag_id);
						intent.putExtra("title", title);
						v.getContext().startActivity(intent);
					}
				});
			} else {
				final String tag_id = list.get(position).getId();
				convertView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(),
								ActivitySearchUser.class);
						intent.putExtra("type", StaticInApp.FINDPEOPLE_VERIFY);
						intent.putExtra("verify_id", tag_id);
						intent.putExtra("title", title);
						inflater.getContext().startActivity(intent);
					}
				});

			}
		} else {
			holder.title.setText(null);
		}

		return convertView;
	}

	class MyHolder {
		TextView title;
	}
}
