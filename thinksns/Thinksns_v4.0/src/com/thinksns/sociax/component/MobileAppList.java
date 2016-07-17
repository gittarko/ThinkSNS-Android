package com.thinksns.sociax.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MobileAppList extends SociaxList {

	private Context context;

	public MobileAppList(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public MobileAppList(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onClick(View view, int position, long id) {
		// TODO Auto-generated method stub
		/*
		 * if (view.getId() == R.id.footer_content) { ImageView iv = (ImageView)
		 * view.findViewById(R.id.anim_view); iv.setVisibility(View.VISIBLE);
		 * Anim.refresh(getContext(), iv);
		 * 
		 * HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter)
		 * this.getAdapter(); ContactAdapter contactAdapter = (ContactAdapter)
		 * headerAdapter.getWrappedAdapter(); contactAdapter.animView = iv;
		 * contactAdapter.doRefreshFooter(); return; }
		 * 
		 * Intent intent = new Intent();
		 * 
		 * ContactCategory category = (ContactCategory)
		 * getItemAtPosition(position); if(category.getcTpye() != null ){
		 * intent.putExtra("documentType", category.getcTpye());
		 * intent.setClass(getActivityObj(), DocumentListActivity.class); }else{
		 * intent.putExtra("contactType", category.getcName());
		 * intent.setClass(getActivityObj(), ContactListActivity.class); }
		 * context.startActivity(intent);
		 */
	}

	@Override
	protected void addHeaderView() {
		// TODO Auto-generated method stub
		// super.addHeaderView();
	}

	@Override
	protected void addFooterView() {
		// TODO Auto-generated method stub
		// super.addFooterView();
	}

}
