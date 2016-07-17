package com.thinksns.sociax.t4.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;

import com.thinksns.sociax.t4.adapter.AdapterSociaxList;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明： 礼物列表，实际上是listview，每个item显示多个礼物
 * 
 * @author wz
 * @date 2014-11-13
 * @version 1.0
 */
public class ListScoreDetail extends ListSociax {
	private static final String TAG = "WeiboList";

	public ListScoreDetail(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListScoreDetail(Context context) {
		super(context);
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
		}
	}
}
