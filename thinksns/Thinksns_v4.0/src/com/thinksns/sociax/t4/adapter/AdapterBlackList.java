package com.thinksns.sociax.t4.adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 类说明： 黑名单
 * 
 * @author wz
 * @date 2014-11-12
 * @version 1.0
 */
public class AdapterBlackList extends ListBaseAdapter<ModelSearchUser> {

	protected int uid;

	public AdapterBlackList(Context context) {
		super(context);
	}

	@Override
	public View getRealView(int position, View convertView, ViewGroup parent) {
		HolderSociax viewHolder;
		if (convertView == null) {
			viewHolder = new HolderSociax();
			LayoutInflater inflater = getLayoutInflater(mContext);
			convertView = inflater.inflate(R.layout.listitem_user, null);
			viewHolder.tv_user_photo = (ImageView) convertView
					.findViewById(R.id.image_photo);
			viewHolder.tv_user_name = (TextView) convertView
					.findViewById(R.id.unnames);
			viewHolder.tv_user_content = (TextView) convertView
					.findViewById(R.id.uncontent);
			viewHolder.tv_user_add = (TextView) convertView
					.findViewById(R.id.image_add);
			convertView.setTag(R.id.tag_viewholder, viewHolder);
		} else {
			viewHolder = (HolderSociax) convertView.getTag(R.id.tag_viewholder);
		}

		convertView.setTag(R.id.tag_search_user, getItem(position));
		Glide.with(mContext).load(getItem(position).getUserface())
		.diskCacheStrategy(DiskCacheStrategy.ALL)
		.transform(new GlideCircleTransform(mContext))
		.crossFade()
		.into(viewHolder.tv_user_photo);
		
		viewHolder.tv_user_name.setText(getItem(position).getUname());
		viewHolder.tv_user_content.setText(getItem(position).getIntro());
		viewHolder.tv_user_add.setVisibility(View.VISIBLE);
		viewHolder.tv_user_add.setBackgroundResource(R.drawable.roundbackground_fav_true);
		viewHolder.tv_user_add.setTextColor(mContext.getResources().getColor(R.color.fav_text_true));
		viewHolder.tv_user_add.setText("解除");
		
		viewHolder.tv_user_add.setTag(R.id.tag_position, position);
		viewHolder.tv_user_add.setTag(R.id.tag_follow, getItem(position));
		viewHolder.tv_user_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				FunctionChangeBlackList fcChangeBlackList = new FunctionChangeBlackList(
//						mContext, AdapterBlackList.this, v, mDatas);
//				fcChangeBlackList.deleteFromBlackList();
			}
		});
		
		return convertView;
	}

	@Override
	public int getMaxId() {
		if (getLast() == null)
			return 0;
		else
			return getLast().getFollow_id();
	}

	@Override
	public ModelSearchUser getLast() {
		if (mDatas.size() > 0) {
			return (ModelSearchUser) mDatas.get(mDatas.size() - 1);
		} else
			return null;
	}

}
