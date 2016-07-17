package com.thinksns.sociax.t4.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thinksns.sociax.api.Api.GiftApi;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.component.GlideRoundTransform;
import com.thinksns.sociax.t4.component.HolderSociax;
import com.thinksns.sociax.t4.exception.VerifyErrorException;

import com.thinksns.sociax.t4.model.ModelMyGifts;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.exception.*;

/**
 * 类说明： 我的礼物
 * 
 * @author Zoey
 * @date 2015年9月21日
 * @version 1.0
 */
public class AdapterMySendGift extends AdapterSociaxList {

	private int page = 1;
	private String mType;

	@Override
	public ModelMyGifts getItem(int position) {
		return (ModelMyGifts) this.list.get(position);
	}

	public AdapterMySendGift(ThinksnsAbscractActivity context,
							 ListData<SociaxItem> list, String type) {
		super(context, list);
		this.mType = type;
	}

	public AdapterMySendGift(FragmentSociax fragment, ListData<SociaxItem> list,
			String type) {
		super(fragment, list);
		this.mType = type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderSociax holder;
		ModelMyGifts modelGift = getItem(position);

			if (convertView == null) {
				holder = new HolderSociax();
				convertView = inflater.inflate(R.layout.list_item_my_gift, null);
	
				holder.iv_my_gift = (ImageView) convertView.findViewById(R.id.iv_my_gift);
				holder.tv_my_gift_name = (TextView) convertView.findViewById(R.id.tv_my_gift_name);
				holder.tv_my_gift_username = (TextView) convertView.findViewById(R.id.tv_my_gift_username);
				holder.tv_id = (TextView) convertView.findViewById(R.id.tv_id);
				
				convertView.setTag(holder);
			} else {
				holder = (HolderSociax) convertView.getTag();
			}
			convertView.setTag(R.id.my_send_gift, modelGift);
	
			holder.tv_num.setText(modelGift.getNum());
			Glide.with(context).load(modelGift.getImage())
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.transform(new GlideRoundTransform(context)).crossFade()
					.into(holder.iv_my_gift);
	
			holder.tv_my_gift_name.setText(modelGift.getName());
			holder.tv_id.setText("被赠送人：");
			holder.tv_my_gift_username.setText(modelGift.getInUserName());
		return convertView;
	}

	@Override
	public int getMaxid() {
		return 0;
	}

	@Override
	public ListData<SociaxItem> refreshHeader(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		return super.refreshHeader(obj);
	}
	
	@Override
	public ListData<SociaxItem> refreshNew(int count)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		page = 1;
		return (ListData<SociaxItem>) getApiGift().getMyGifts(page, mType, httpListener);
	}

	@Override
	public ListData<SociaxItem> refreshFooter(SociaxItem obj)
			throws VerifyErrorException, ApiException, ListAreEmptyException,
			DataInvalidException {
		page++;
		return (ListData<SociaxItem>) getApiGift().getMyGifts(page, mType, httpListener);
	}

	GiftApi getApiGift() {
		return thread.getApp().getApiGift();
	}

	
	@Override
	public void addFooter(ListData<SociaxItem> list) {
		super.addFooter(list);

	}
}
