package com.thinksns.sociax.component;

import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;

import android.app.Activity;
import android.view.View;

public class LeftAndRightTitle extends CustomTitle {
	/**
	 * 默认activity里面的title
	 * @param context
	 */
	public LeftAndRightTitle(Activity context) {
		super(context, ((ThinksnsAbscractActivity) context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity) context;
		leftButtonResource = activity.getLeftRes();
		rightButtonResource = activity.getRightRes();
		this.setListenerLeft(activity.getLeftListener());
		this.setListenerRight(activity.getRightListener());
		this.setView(activity.getTitleCenter(), TITLE_HAVE_BOTHIMAGE);
	}

	/**
	 * 生成 title
	 * 
	 * @param context
	 * @param left_resource
	 *            左边图片，默认为0
	 * @param right_resource
	 *            右边图片，默认为0
	 */
	public LeftAndRightTitle(Activity context, int left_resource,
			int right_resource) {
		super(context, ((ThinksnsAbscractActivity) context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity) context;
		leftButtonResource = left_resource == 0 ? activity.getLeftRes()
				: left_resource;
		rightButtonResource = right_resource == 0 ? activity.getRightRes()
				: right_resource;
		this.setListenerLeft(activity.getLeftListener());
		this.setListenerRight(activity.getRightListener());
		this.setView(activity.getTitleCenter(), TITLE_HAVE_BOTHIMAGE);
	}
	
	/**
	 * 生成 title
	 * 
	 * @param context
	 * @param left_resource
	 *            左边图片，默认为0
	 * @param right_text
	 *            右边字符串，默认为空
	 */
	public LeftAndRightTitle(Activity context, int left_resource,
			String right_text) {
		super(context, ((ThinksnsAbscractActivity) context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity) context;
		leftButtonResource = left_resource == 0 ? activity.getLeftRes()
				: left_resource;
		this.str_right = right_text;
		this.setListenerLeft(activity.getLeftListener());
		this.setListenerRight(activity.getRightListener());
		this.setView(activity.getTitleCenter(), TITLE_LEFTIMAGE_RIGHTTEXT);
	}


	/**
	 * 生成 title
	 *
	 * @param context
	 * @param right_resource
	 *            右边图片，默认为0
	 * @param left_text
	 *            左边字符串，默认为空
	 */
	public LeftAndRightTitle(Activity context, String left_text,
							 int right_resource) {
		super(context, ((ThinksnsAbscractActivity) context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity) context;
		rightButtonResource = right_resource == 0 ? activity.getRightRes()
				: right_resource;
		this.str_left = left_text;
		this.setListenerLeft(activity.getLeftListener());
		this.setListenerRight(activity.getRightListener());
		this.setView(activity.getTitleCenter(), TITLE_LEFTIMAGE_RIGHTTEXT);
	}

	/**
	 * 生成 title 只有左边的图片
	 * @param context
	 * @param left_resource
	 *  左边图片，默认为0
	 */
	public LeftAndRightTitle(int left_resource, Activity context) {
		super(context, ((ThinksnsAbscractActivity) context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity) context;
		leftButtonResource = left_resource == 0 ? activity.getLeftRes()
				: left_resource;
		this.setListenerLeft(activity.getLeftListener());
		this.setView(activity.getTitleCenter(), TITLE_HAVE_LEFT_IMAGE);
	}

	/**
	 * 生成 title 只有右边图片
	 * 
	 * @param context
	 * @param right_resource
	 *        右边图片，默认为0
	 */
	public LeftAndRightTitle(Activity context, int right_resource) {
		super(context, ((ThinksnsAbscractActivity) context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity) context;
		rightButtonResource = right_resource == 0 ? activity.getLeftRes()
				: right_resource;
		this.setListenerLeft(activity.getLeftListener());
		this.setView(activity.getTitleCenter(), TITLE_HAVE_RIGHT_IAMGE);
	}
	/**
	 * 生成 title 只有中间
	 * @param context
	 */
	public LeftAndRightTitle(Activity context, String noused) {
		super(context, ((ThinksnsAbscractActivity) context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity) context;
		this.setView(activity.getTitleCenter(), TITLE_ONLY_CENTER);
	}
	

	public LeftAndRightTitle(Activity context, View layout) {
		super(context, ((ThinksnsAbscractActivity) context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity) context;
		leftButtonResource = activity.getLeftRes();
		rightButtonResource = activity.getRightRes();
		this.setListenerLeft(activity.getLeftListener());
		this.setListenerRight(activity.getRightListener());
		this.setView(layout);
	}

	/**
	 * 左右两边都是文字
	 * @param context
	 * @param str_left
	 * @param str_right
     */
	public LeftAndRightTitle(Activity context, String str_left, String str_right) {
		super(context, ((ThinksnsAbscractActivity)context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity) context;
		this.str_left = str_left;
		this.str_right = str_right;
		this.setListenerLeft(activity.getLeftListener());
		this.setListenerRight(activity.getRightListener());
		this.setView(activity.getTitleCenter(), TITLE_HAVE_BOTHTEXT);
	}
}
