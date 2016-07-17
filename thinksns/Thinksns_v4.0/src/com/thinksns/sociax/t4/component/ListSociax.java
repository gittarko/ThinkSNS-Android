package com.thinksns.sociax.t4.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.unit.DragDown;
import com.thinksns.sociax.android.R;

/**
 * 类说明：所有列表的基类 子类必须实现public XXX(Context context, AttributeSet attrs) {
 * super(context, attrs); 否则报错 Error inflating Class
 * 
 * @author wz
 * @date 2014-10-15
 * @version 1.0
 */
public abstract class ListSociax extends ListView implements
		OnTouchListListener {
	private static final String TAG = "SociaxList";
	public DragDown dragdown;
	public boolean hasFooter = true;// 列表是否有更多
	private static int lastPosition;
	protected static Activity activityObj;
	protected ListData<SociaxItem> list;

	public ListSociax(Context context) {
		super(context);
		this.initSet(context);
	}

	public ListSociax(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initSet(context);
	}

	/**
	 * 初始化listview的divier边框
	 * 
	 * @param context
	 */
	public void initSet(Context context) {
		this.setScrollbarFadingEnabled(true);
		this.setCacheColorHint(0);
		this.setDivider(new ColorDrawable(context.getResources().getColor(
				R.color.bg_listview_divider)));
		this.setDividerHeight(1);
		this.setVerticalScrollBarEnabled(false); // 设置滑动条垂直不显示
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
		this.list = ((AdapterSociaxList) adapter).getList();
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (dragdown.isUnClickable())
					return;
				ListSociax.this.onClick(view, position, id);
			}
		});
		this.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					setLastPosition(ListSociax.this.getFirstVisiblePosition());
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

	protected abstract void onClick(View view, int position, long id);
	public void onItemLongClick(AdapterView<?> parent, View view, int position, long id){
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
		ListSociax.activityObj = activityObj;
	}

	public static int getLastPosition() {
		return lastPosition;
	}

	static void setLastPosition(int lastPosition) {
		ListSociax.lastPosition = lastPosition;
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
}
