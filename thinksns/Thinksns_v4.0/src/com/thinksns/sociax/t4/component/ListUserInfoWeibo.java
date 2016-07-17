package com.thinksns.sociax.t4.component;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.thinksns.sociax.t4.adapter.AdapterWeiboList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

/**
 * 类说明： 个人主页的微博，需要去除header
 * 
 * @author wz
 * @date 2014-11-5
 * @version 1.0
 */
public class ListUserInfoWeibo extends ListSociax {
	private static final String TAG = "WeiboList";

	public ListUserInfoWeibo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListUserInfoWeibo(Context context) {
		super(context);
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
			AdapterWeiboList weiboLisAdapter = (AdapterWeiboList) headerAdapter
					.getWrappedAdapter();
			weiboLisAdapter.animView = iv;
			weiboLisAdapter.doRefreshFooter();
		} else {
			Bundle data = new Bundle();
			ModelWeibo md=(ModelWeibo)view.getTag(R.id.tag_weibo);
			data.putSerializable("weibo", md);
			ActivityStack.startActivity(getActivityObj(), ActivityWeiboDetail.class,
					data);
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		super.setAdapter(adapter);
		this.removeHeaderView(dragdown.getHeaderView());
		
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (dragdown.isUnClickable())
					return;
				ListUserInfoWeibo.this.onClick(view, position, id);
			}
		});
		this.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					setLastPosition(ListUserInfoWeibo.this
							.getFirstVisiblePosition());
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

	}
}
