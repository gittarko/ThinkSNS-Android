package com.thinksns.sociax.pla;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.thinksns.sociax.pla.PLA_AbsListView.OnScrollListener;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author povol
 * @date Aug 19, 2013
 * @version 1.0
 */
public class TSPhotoListView extends MultiColumnListView implements
		OnScrollListener {

	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	private TSPhotoListViewListener mListViewListener;

	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	private View mFooterView;
	private boolean mPullLoading;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull

	private boolean mIsCanLoadMore = false;

	// feature.

	public TSPhotoListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public TSPhotoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public TSPhotoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// add footer view
		mFooterView = View.inflate(context, R.layout.loading_bottom, null);
		this.addFooterView(mFooterView);

	}

	public void setTSPhotoListViewListener(TSPhotoListViewListener l) {
		mListViewListener = l;
	}

	public void setIsCanLoadMore(boolean isCanLoadMore) {
		mIsCanLoadMore = isCanLoadMore;
		if (!mIsCanLoadMore) {
			this.removeFooterView(mFooterView);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getLastVisiblePosition() == mTotalItemCount - 1
					&& mIsCanLoadMore) {
				// if (getLastVisiblePosition() == mTotalItemCount - 1 &&
				// (footerView.getBottomMargin() > 0 || deltaY < 0)) {
				// last item, already pulled up or want to pull up.
				// updateFooterHeight(-deltaY / OFFSET_RADIO);
				// }
				invokeOnScrolling();
			}
			break;
		default:
			mLastY = -1; // reset
			if (getLastVisiblePosition() == mTotalItemCount - 1
					&& mIsCanLoadMore) {
				// invoke load more.
				// if (mEnablePullLoad && footerView.getBottomMargin() >
				// PULL_LOAD_MORE_DELTA) {
				startLoadMore();
				// }
				// resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		// mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	public interface TSPhotoListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}

	@Override
	public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(PLA_AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

}
