package com.thinksns.sociax.t4.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.model.ModelMedals;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author Zoey
 * @date 2015年9月7日
 * @version 1.0
 */
public class AdapterMedal extends BaseAdapter {

	Context context;
	ArrayList<ModelMedals> medalList = new ArrayList<ModelMedals>();

	public AdapterMedal(Context context, ArrayList<ModelMedals> medalList) {
		super();
		this.context = context;
		this.medalList = medalList;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderSociax holder;
		if (convertView == null || convertView.getTag(R.id.tag_viewholder) == null) {
			holder = new HolderSociax();
			convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_medal, null);
			holder.tv_medal_name = (TextView) convertView.findViewById(R.id.tv_medal_name);
			holder.iv_medal = (ImageView) convertView.findViewById(R.id.iv_medal);
			convertView.setTag(R.id.tag_viewholder, holder);
		} else {
			holder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

			convertView.setTag(R.id.tag_medal, getItem(position));
			ModelMedals modelMedals = (ModelMedals) getItem(position);

			// 显示图片的配置
			Glide.with(context).load(modelMedals.getIcon())
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.transform(new GlideCircleTransform(context)).crossFade()
					.into(holder.iv_medal);

			holder.tv_medal_name.setText(modelMedals.getName());

		return convertView;
	}

	@Override
	public int getCount() {
		return medalList.size()==0 ? 1 : medalList.size();
	}

	@Override
	public ModelMedals getItem(int position) {
		return medalList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
