package com.thinksns.sociax.listener;

import android.view.View;

public interface OnTouchListListener {
	public void headerShow();

	public void headerHiden();

	public void headerRefresh();

	public long getLastRefresh();

	public void setLastRefresh(long lastRefresh);

	public void footerShow();

	public void footerHiden();

	public View hideFooterView();

	public View showFooterView();
}
