package com.thinksns.sociax.t4.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;

import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.gift.ActivityFindGiftReceiver;
import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.t4.model.ModelUser;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明： 用户列表
 * 
 * @author wz
 * @date 2014-10-28
 * @version 1.0
 */
public class ListGiftUser extends ListSociax {
	private Context context;

	@Override
	protected void initDrag(Context context) {
		
	}
	
	public ListGiftUser(Context context) {
		super(context);
		this.context = context;
	}

	public ListGiftUser(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onClick(View view, int position, long id) {
		if (view.getId() == R.id.footer_content) {
			ImageView iv = (ImageView) view.findViewById(R.id.anim_view);
			iv.setVisibility(View.VISIBLE);
			Anim.refresh(getContext(), iv);
			HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) this.getAdapter();
			AdapterSociaxList adapter = (AdapterSociaxList) headerAdapter.getWrappedAdapter();
			adapter.animView = iv;
			adapter.doRefreshFooter();
		} else if (getActivityObj().getClass() == ActivityFindGiftReceiver.class) {//单选联系人

			ModelSearchUser user = (ModelSearchUser) getItemAtPosition(position);
			
			ModelUser returnuser = new ModelUser();
			returnuser.setUserName(user.getUname());
			returnuser.setUid(user.getUid());
			returnuser.setFace(user.getUface());
			returnuser.setIntro(user.getIntro());
			
			Intent i = new Intent();
			Bundle b = new Bundle();
			b.putString("name", returnuser.getUserName());
			b.putInt("uid", returnuser.getUid());
			b.putSerializable("user", returnuser);
			
			i.putExtras(b);
			ActivityFindGiftReceiver at = (ActivityFindGiftReceiver) getActivityObj();
			at.setResult(StaticInApp.RESULT_CODE_SELET_GIFT_RECEIVER, i);
			at.finish();
		}
	}
}
