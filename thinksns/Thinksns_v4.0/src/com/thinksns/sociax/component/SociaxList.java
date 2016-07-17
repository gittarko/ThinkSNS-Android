package com.thinksns.sociax.component;

import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.unit.DragDown;
import com.thinksns.sociax.android.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class SociaxList extends ListView implements
		OnTouchListListener {

	private static final String TAG = "SociaxList";
	public DragDown dragdown;

	private static int lastPosition;
	private static Activity activityObj;

	public SociaxList(Context context) {
		super(context);
		this.initSet(context);
	}

	public SociaxList(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initSet(context);
	}

	public void initSet(Context context) {
		this.setScrollbarFadingEnabled(true);
		this.setCacheColorHint(0);
		this.setDivider(new ColorDrawable(context.getResources().getColor(
				R.color.bg_listview_divider)));
		this.setDividerHeight(1);
		dragdown = new DragDown(context, this);
		activityObj = (Activity) context;
		this.initDrag(context);
	}

	protected void initDrag(Context context) {
		this.setOnTouchListener(dragdown);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		this.addHeaderView();
		this.addFooterView();
		super.setAdapter(adapter);
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (dragdown.isUnClickable())
					return;
				SociaxList.this.onClick(view, position, id);
			}
		});
		this.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					setLastPosition(SociaxList.this.getFirstVisiblePosition());
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	protected void addHeaderView() {
		// super.addHeaderView(dragdown.getHeaderView());
		super.addHeaderView(dragdown.getHeaderView(), null, false);
	}

	protected void addFooterView() {
		super.addFooterView(dragdown.getFooterView());
	}

	@Override
	public boolean removeFooterView(View v) {
		return super.removeFooterView(dragdown.getFooter());
	}

	protected abstract void onClick(View view, int position, long id);

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

	@Override
	public void footerHiden() {
		dragdown.footerHiden();
	}

	@Override
	public View hideFooterView() {
		removeFooterView(this);
		return null;
		// dragdown.hideFooterView();
	}

	/**
	 * 显示底部的更多，不直接在listview.showFooter，而是在DragDown下修改显示
	 */
	@Override
	public View showFooterView() {
		dragdown.showFooterView();
		return null;
	}

	public static Activity getActivityObj() {
		return activityObj;
	}

	private static void setActivityObj(Activity activityObj) {
		SociaxList.activityObj = activityObj;
	}

	public static int getLastPosition() {
		return lastPosition;
	}

	private static void setLastPosition(int lastPosition) {
		SociaxList.lastPosition = lastPosition;
	}
}
