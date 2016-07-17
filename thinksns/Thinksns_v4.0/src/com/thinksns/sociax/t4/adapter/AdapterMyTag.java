package com.thinksns.sociax.t4.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelMyTag;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author Zoey
 * @date 2015年9月9日
 * @version 1.0
 */
public class AdapterMyTag extends BaseAdapter {

	Context context;
	ArrayList<ModelMyTag> tag_list = new ArrayList<ModelMyTag>();
	private Thinksns application;
	
	public AdapterMyTag(Context context,
			ArrayList<ModelMyTag> list_all) {
		super();
		this.context = context;
		this.tag_list = list_all;
		application = (Thinksns) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		return tag_list == null ? 0 : tag_list.size();
	}

	@Override
	public ModelMyTag getItem(int position) {
		return tag_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		HolderSociax holder;
		if (convertView == null) {
			holder = new HolderSociax();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_tag_cloud, null);

			holder.tv_tag_cloud = (TextView) convertView.findViewById(R.id.tv_tag_cloud);
			convertView.setTag(R.id.tag_viewholder, holder);
		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		ModelMyTag modelTag = (ModelMyTag) getItem(position);
		holder.tv_tag_cloud.setText(modelTag.getTag_name());
		
		return convertView;
	}
}
