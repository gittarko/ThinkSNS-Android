package com.thinksns.sociax.component;

import com.thinksns.sociax.adapter.ContactAdapter;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;

public class TaskCategoryList extends SociaxList {

	private Context context;

	public TaskCategoryList(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.setDivider(null);
		this.setDividerHeight(4);
		// TODO Auto-generated constructor stub
	}

	public TaskCategoryList(Context context) {
		super(context);
		this.setDivider(null);
		this.setDividerHeight(4);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onClick(View view, int position, long id) {
		// TODO Auto-generated method stub
		if (view.getId() == R.id.footer_content) {
			ImageView iv = (ImageView) view.findViewById(R.id.anim_view);
			iv.setVisibility(View.VISIBLE);
			Anim.refresh(getContext(), iv);

			HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) this
					.getAdapter();
			ContactAdapter contactAdapter = (ContactAdapter) headerAdapter
					.getWrappedAdapter();
			contactAdapter.animView = iv;
			contactAdapter.doRefreshFooter();
			return;
		}

		/*
		 * Intent intent = new Intent();
		 * 
		 * ContactCategory category = (ContactCategory)
		 * getItemAtPosition(position); if(category.getcId()== -2){
		 * intent.setClass(getActivityObj(), MyContactListActivity.class); }
		 * else if(category.getcTpye() != null ){
		 * intent.putExtra("documentType", category.getcTpye());
		 * intent.setClass(getActivityObj(), DocumentListActivity.class); }else{
		 * intent.putExtra("departId", category.getcId());
		 * intent.putExtra("contactType", category.getcName());
		 * intent.setClass(getActivityObj(), ContactListActivity.class); }
		 * context.startActivity(intent);
		 */
	}

	@Override
	public void setOnItemLongClickListener(
			android.widget.AdapterView.OnItemLongClickListener listener) {
		// TODO Auto-generated method stub
		super.setOnItemLongClickListener(listener);
	}

	@Override
	protected void addHeaderView() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void addFooterView() {
		// TODO Auto-generated method stub
	}
}
