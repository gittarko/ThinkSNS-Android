package com.thinksns.sociax.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class SystemNotifyList extends SociaxList {

	private Context context;

	public SystemNotifyList(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public SystemNotifyList(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	@Override
	protected void onClick(View view, int position, long id) {

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
