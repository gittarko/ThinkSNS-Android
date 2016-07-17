package com.thinksns.sociax.unit;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.thinksns.sociax.component.ImageBroder;
import com.thinksns.sociax.concurrent.BitmapDownloaderTask;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.model.ModelImageAttach;
import com.thinksns.sociax.t4.model.ModelWeibo;
import com.thinksns.sociax.android.R;

public abstract class WeiboDataSet {
	protected static final int TRANSPOND_LAYOUT = 111;
	protected static final int IMAGE_VIEW = 222;
	protected static final int IMAGE_LAYOUT = 333;
	protected static final int WEIBA_VIEW = 4444;

	protected static final int CONTENT_INDEX = 2;

	private static enum PATTERN {
		AT, TOPIC, URL
	}

	private ImageBroder imageBorder;
	private Object weibo;
	private Bitmap bitmap;

	// public abstract void appendWeiboData(Weibo weibo,View view,WeiboDataItem
	// weiboDataItem);
	public abstract void appendWeiboData(ModelWeibo weibo, View view);

	public abstract void appendWeiboData(ModelWeibo weibo, View view, boolean isFirst);

	protected static ThinksnsAbscractActivity activityObj;

	protected abstract int getContentIndex();

	protected abstract void setCountLayout(ModelWeibo weibo, View view);

	protected abstract void setTranspondCount(ModelWeibo weibo, View view);

	protected abstract void setCommentCount(ModelWeibo weibo, View view);

	protected abstract int getGravity();

	protected abstract BitmapDownloaderTask.Type getThumbType();

	protected abstract boolean hasThumbCache(ModelWeibo weibo);

	/*
	 * protected abstract String getThumbUrl(Weibo weibo);
	 * 
	 * protected abstract Bitmap getThumbCache(Weibo weibo);
	 */

	protected int getThumbWidth() {
		// TODO Auto-generated method stub
		return LayoutParams.WRAP_CONTENT;
	}

	protected int getThumbHeight() {
		// TODO Auto-generated method stub
		return LayoutParams.WRAP_CONTENT;
	}

	// 头像
	protected void addHeader(ModelWeibo weibo, View view, ImageView header) {
		Thinksns app = (Thinksns) view.getContext().getApplicationContext();
		// header = (ImageView)view.findViewById(R.id.user_header);
		header.setTag(weibo);
		header.setImageDrawable(view.getContext().getResources()
				.getDrawable(R.drawable.default_user));
	}

	final protected void removeViews(LinearLayout layout) {
		// ImageBroder image = (ImageBroder) layout.findViewById(IMAGE_VIEW);
		ImageView image = (ImageView) layout.findViewById(IMAGE_VIEW);
		LinearLayout transpond = (LinearLayout) layout
				.findViewById(TRANSPOND_LAYOUT);
		LinearLayout weibapost = (LinearLayout) layout.findViewById(WEIBA_VIEW);

		if (image != null) {
			layout.removeViewInLayout(image);
		}

		if (transpond != null) {
			layout.removeViewInLayout(transpond);
		}
		if (weibapost != null) {
			layout.removeViewInLayout(weibapost);
		}
	}

	protected abstract View appendTranspond(ModelWeibo weibo, View view);

	final protected View appendImage(ModelWeibo weibo, View view) {
		ImageBroder image = new ImageBroder(view.getContext());
		image.setTag(weibo);
		image.setId(IMAGE_VIEW);
		if (weibo.getAttachImage() != null) {
			LinearLayout ly = creatImageLayout(view);
			dowloaderTask(((ModelImageAttach)weibo.getAttachImage().get(0)).getSmall(), image,
					getThumbType());

		}

		return image;
	}

	final protected void dowloaderTask(String url, ImageView image,
			BitmapDownloaderTask.Type type) {
		BitmapDownloaderTask task = new BitmapDownloaderTask(image, type);
		task.execute(url);
	}

	protected LinearLayout creatImageLayout(View view) {
		LinearLayout imageLayout = new LinearLayout(view.getContext());
		imageLayout.setId(IMAGE_LAYOUT);
		return imageLayout;

	}
}
