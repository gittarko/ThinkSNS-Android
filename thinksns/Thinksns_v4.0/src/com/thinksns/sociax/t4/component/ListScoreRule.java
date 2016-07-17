package com.thinksns.sociax.t4.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 类说明： 礼物列表，实际上是listview，每个item显示多个礼物
 * 
 * @author wz
 * @date 2014-11-13
 * @version 1.0
 */
public class ListScoreRule extends ListSociax {
	private static final String TAG = "WeiboList";

	@Override
	protected void initDrag(Context context) {
	}
	
	public ListScoreRule(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListScoreRule(Context context) {
		super(context);
	}

	@Override
	protected void onClick(View view, int position, long id) {
//		if (view.getId() == R.id.footer_content) {
//			ImageView iv = (ImageView) view.findViewById(R.id.anim_view);
//			iv.setVisibility(View.VISIBLE);
//			Anim.refresh(getContext(), iv);
//			HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) this.getAdapter();
//			AdapterSociaxList adapter = (AdapterSociaxList) headerAdapter.getWrappedAdapter();
//			adapter.animView = iv;
//			adapter.doRefreshFooter();
//		}
	}
}
