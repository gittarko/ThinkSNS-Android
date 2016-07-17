package com.thinksns.sociax.t4.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.adapter.AdapterUserWeiboList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.weibo.ActivityWeiboDetail;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.sociax.unit.DragDown;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 第二种个人主页的List
 * 
 * @author wz
 * @date 2014-11-19
 * @version 1.0
 */
public class ListUserInfo extends ListView implements OnTouchListListener {

	private static final String TAG = "SociaxList";
	public DragDown dragdown;
	public boolean hasFooter = true;// 列表是否有更多

	private static int lastPosition;
	private static Activity activityObj;
	
	public void initSet(Context context) {
		this.setScrollbarFadingEnabled(true);
		this.setCacheColorHint(0);
		this.setVerticalScrollBarEnabled(false); // 设置滑动条垂直不显示
		this.setDivider(new ColorDrawable(context.getResources().getColor(
				R.color.bg_listview_divider)));
		this.setDividerHeight(1);
		dragdown = new DragDown(context, this);
		activityObj = (Activity) context;
		this.initDrag(context);
	}
	public void setDivierNull(){
		this.setDivider(null);
	}
	protected void addHeaderView() {
		// super.addHeaderView(dragdown.getHeaderView());
		super.addHeaderView(dragdown.getHeaderView(), null, false);
	}

	/**
	 * 本方法只是用一次，用于增加更多
	 */
	protected void addFooterView() {
		super.addFooterView(dragdown.getFooterView());
		setHasFooter(true);
	}

	@Override
	public boolean removeFooterView(View v) {
		return super.removeFooterView(dragdown.getFooter());
	}

	public void setAdapter(ListAdapter adapter, long lastTime, Activity obj) {
		setActivityObj(obj);
		this.setLastRefresh(lastTime);
		this.setAdapter(adapter);
	}

	@Override
	public void headerShow() {
		dragdown.headerShow();
	}

	@Override
	public void headerHiden() {
		dragdown.headerHiden();
	}

	@Override
	public void headerRefresh() {
		dragdown.headerRefresh();
	}

	@Override
	public long getLastRefresh() {
		return dragdown.getLastRefresh();
	}

	@Override
	public void setLastRefresh(long lastRefresh) {
		dragdown.setLastRefresh(lastRefresh);
	}

	@Override
	public void footerShow() {
		dragdown.footerShow();
	}

	/**
	 * 此方法有问题
	 */
	@Override
	public void footerHiden() {
		dragdown.footerHiden();

	}

	@Override
	public View hideFooterView() {
		setHasFooter(false);
		removeFooterView(this);
		return null;
		// dragdown.hideFooterView();
	}

	/**
	 * 显示底部的更多，不直接在listview.showFooter，而是在DragDown下修改显示，
	 * 如果当前已经有更多，则直接显示，否则添加一个新的footer
	 */
	@Override
	public View showFooterView() {
		if (hasFooter) {
			dragdown.showFooterView();
		} else {
			this.addFooterView();
		}
		return null;
	}

	public static Activity getActivityObj() {
		return activityObj;
	}

	private static void setActivityObj(Activity activityObj) {
		ListUserInfo.activityObj = activityObj;
	}

	public static int getLastPosition() {
		return lastPosition;
	}

	static void setLastPosition(int lastPosition) {
		ListUserInfo.lastPosition = lastPosition;
	}

	/****** t4 *******/
	/**
	 * 判断列表是否有显示“更多”
	 * 
	 * @return
	 */
	public boolean isHasFooter() {
		return hasFooter;
	}

	public void setHasFooter(boolean hasFooter) {
		this.hasFooter = hasFooter;
	}

	public ListUserInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initSet(context);
	}

	public ListUserInfo(Context context) {
		super(context);
		this.initSet(context);
	}

	protected void initDrag(Context context) {
		this.setOnTouchListener(dragdown);
		this.addHeaderView();
		this.addFooterView();
	}

	/**
	 * 在下拉刷新的下面添加新的headerview
	 * 
	 * @param view
	 */
	public void addViewAfterDragHeader(View view) {
		this.removeHeaderView(dragdown.getHeaderView());
		this.addHeaderView(view);
		this.addHeaderView();
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (dragdown.isUnClickable())
					return;
				ListUserInfo.this.onClick(view, position, id);
			}
		});
		this.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					setLastPosition(ListUserInfo.this.getFirstVisiblePosition());
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	protected void onClick(View view, int position, long id) {

		HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) this
				.getAdapter();
		AdapterSociaxList adapter = (AdapterSociaxList) headerAdapter
				.getWrappedAdapter();
		if (view.getId() == R.id.footer_content) {
			/*ImageView iv = (ImageView) view.findViewById(R.id.anim_view);
			iv.setVisibility(View.VISIBLE);
			Anim.refresh(getContext(), iv);
			adapter.animView = iv;
			adapter.doRefreshFooter();*/
		} else {
			if (adapter instanceof AdapterUserWeiboList) {
				Bundle data = new Bundle();
				data.putSerializable("weibo",
						(ModelWeibo) view.getTag(R.id.tag_weibo));
				ActivityStack.startActivity(getActivityObj(),
						ActivityWeiboDetail.class, data);
			}
		}
	}

}
