package com.thinksns.sociax.component;

import com.thinksns.sociax.unit.DragDown;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MessageDetail extends SociaxList {
	public MessageDetail(Context context) {
		super(context);
	}

	public MessageDetail(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onClick(View view, int position, long id) {
		return;
	}

	@Override
	protected void addFooterView() {
		// TODO Auto-generated method stub
	}

	@Override
	public void initSet(Context context) {
		// TODO Auto-generated method stub
		this.setScrollbarFadingEnabled(true);
		// this.setCacheColorHint(Color.argb(0, 255, 255, 255));
		// this.setCacheColorHint(Color.TRANSPARENT);
		this.setCacheColorHint(0);
		// int color = context.getResources().getColor(R.color.line);
		this.setDivider(null);
		// this.setDivider(new ColorDrawable(color));
		// this.setDividerHeight(2);
		dragdown = new DragDown(context, this);
		this.initDrag(context);

	}

}
