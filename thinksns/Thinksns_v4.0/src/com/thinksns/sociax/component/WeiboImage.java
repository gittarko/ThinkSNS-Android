package com.thinksns.sociax.component;

import android.view.View;

import com.thinksns.sociax.concurrent.BitmapDownloaderTask;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.unit.WeiboDataSet;

public class WeiboImage extends WeiboDataSet {

	@Override
	protected View appendTranspond(ModelWeibo weibo, View view) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void appendWeiboData(ModelWeibo weibo, View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getContentIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getGravity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected BitmapDownloaderTask.Type getThumbType() {
		return BitmapDownloaderTask.Type.LARGE_THUMB;
	}

	/*
	 * @Override protected Bitmap getThumbCache(Weibo weibo) { // TODO
	 * Auto-generated method stub return weibo.getThumbLarge(); }
	 * 
	 * protected boolean hasThumbCache(Weibo weibo){ return
	 * weibo.isNullForThumbLargeCache(); }
	 * 
	 * protected String getThumbUrl(Weibo weibo){ return weibo.getPicUrl(); }
	 * protected String getThumbUrl(Weibo weibo){ return weibo.getImageUrl()[0];
	 * }
	 */
	@Override
	protected void setCommentCount(ModelWeibo weibo, View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setCountLayout(ModelWeibo weibo, View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setTranspondCount(ModelWeibo weibo, View view) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean hasThumbCache(ModelWeibo weibo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void appendWeiboData(ModelWeibo weibo, View view, boolean isFirst) {
		// TODO Auto-generated method stub

	}

}
