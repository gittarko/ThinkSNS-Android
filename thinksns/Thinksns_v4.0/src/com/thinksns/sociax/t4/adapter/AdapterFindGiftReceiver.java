package com.thinksns.sociax.t4.adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.GlideCircleTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;
import com.thinksns.sociax.thinksnsbase.exception.DataInvalidException;
import com.thinksns.sociax.thinksnsbase.exception.ListAreEmptyException;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * 类说明：   
 * @author  wz    
 * @date    2015-1-24
 * @version 1.0
 */
public class AdapterFindGiftReceiver extends AdapterUserFollowingList {
	String key;
	boolean isShowAddButton;//是否显示添加关注按钮
	public AdapterFindGiftReceiver(FragmentSociax fragment,
			ListData<SociaxItem> list, int uid, String key) {
		super(fragment, list, uid);
		this.key = key;
	}
	
	public AdapterFindGiftReceiver(ThinksnsAbscractActivity context,
								   ListData<SociaxItem> list, String key) {
		super(context, list);
		this.key = key;
	}

	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return (ListData<SociaxItem>) getApiUser().searchUserByKey(key,getMaxid(), count, httpListener);
	}

	@Override
	public int getMaxid() {
		if (getLast() == null) {
			return 0;
		} else
			return getLast().getFollow_id();
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return (ListData<SociaxItem>) getApiUser().searchUserByKey(key,getMaxid(), PAGE_COUNT, httpListener);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderSociax viewHolder;
		if (convertView == null) {
			viewHolder = new HolderSociax();
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
		
		Glide.with(context).load(getItem(position).getUserface())
		.diskCacheStrategy(DiskCacheStrategy.ALL)
		.transform(new GlideCircleTransform(context))
		.crossFade()
		.into(viewHolder.tv_user_photo);
		
		viewHolder.tv_user_name.setText(getItem(position).getUname());
		viewHolder.tv_user_content.setText(getItem(position).getIntro());
		viewHolder.tv_user_add.setVisibility(View.GONE);
		
		return convertView;
	}
}