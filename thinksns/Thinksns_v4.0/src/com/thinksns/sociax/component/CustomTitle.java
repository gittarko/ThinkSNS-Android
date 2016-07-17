package com.thinksns.sociax.component;

import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class CustomTitle {
	private Activity context;

	public static final int TITLE_HAVE_BOTHIMAGE = 0;// 俩边都是图片
	public static final int TITLE_HAVE_LEFT_IMAGE = 1;// 左边是图片右边为空
	public static final int TITLE_HAVE_RIGHT_IAMGE = 2;// 右边是图片左边为空
	public static final int TITLE_ONLY_CENTER = 3;  // 俩边都是空
	public static final int TITLE_WITH_LAYOUT = 4;
	public static final int TITLE_LEFTIMAGE_RIGHTTEXT = 5;// 左边是图片右边是字
	public static final int TITLE_RIGHTIMAGE_LEFTTEXT = 6;// 右边是图片左边是字
	public static final int TITLE_HAVE_BOTHTEXT = 7;	//两侧都是文字

	private OnClickListener listenerLeft;
	private OnClickListener listenerRight;
	private OnClickListener listenerCenter;

	private static int flag;
	protected View center;	// 中间的view
	protected View left;
	protected View right;
	protected RelativeLayout layout;

	// 通过资源id（通常为drawable资源）生成imagebutton添加到俩边

	protected int leftButtonResource;
	protected int rightButtonResource;
	// 通过字符串生成textview添加到俩边
	protected String str_left,// 左边的字符
			    	str_right;// 右边的字符

	protected TextView tv_center;
	protected TextView tv_left, tv_right;

	public CustomTitle(Activity context, boolean inTab) {
		this.context = context;
		if (!inTab) {
			Window window = context.getWindow();
			window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
			layout = (RelativeLayout) context.findViewById(R.id.custom_title_layout);
		} else {
			layout = (RelativeLayout) context.getParent().findViewById(
					R.id.custom_title_layout);
		}
	}

	/**
	 * 隐藏所有内容
	 */
	public void clear() {
		left.setVisibility(View.GONE);
		right.setVisibility(View.GONE);
		center.setVisibility(View.GONE);
	}

	//获取中间控件
	public View getCenter() {
		return this.center;
	}

	//获取左边控件
	public View getLeft() {
		return this.left;
	}

	//获取右边控件
	public View getRight() {
		return this.right;
	}

	public int getFlag() {
		return flag;
	}

	protected void setView(View view) {
		RelativeLayout.LayoutParams lpCenter = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lpCenter.addRule(RelativeLayout.CENTER_VERTICAL);
		layout.addView(view, lpCenter);
	}

	protected void setView(String text, int flag) {

		CustomTitle.flag = flag;
		switch (flag) {
			case TITLE_HAVE_BOTHIMAGE:
				left = addLeftButton();
				left.setId(R.id.iv_back);
				right = addRightButton();
				right.setId(R.id.iv_more);
				layout.addView(left, getLayoutParams(RelativeLayout.ALIGN_PARENT_LEFT));
				layout.addView(right,getLayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT));
				break;
			case TITLE_HAVE_LEFT_IMAGE:
				left = addLeftButton();
				layout.addView(left, getLayoutParams(RelativeLayout.ALIGN_PARENT_LEFT));
				break;
			case TITLE_HAVE_RIGHT_IAMGE:
				right = addRightButton();
				layout.addView(right,
					getLayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT));
				break;
			case TITLE_ONLY_CENTER:
				break;
			case TITLE_LEFTIMAGE_RIGHTTEXT:
				left = addLeftButton();
				right = addRightText(str_right, getListenerRight());
				layout.addView(left, getLayoutParams(RelativeLayout.ALIGN_PARENT_LEFT));
				layout.addView(right, getTextLayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT));
				break;
			case TITLE_RIGHTIMAGE_LEFTTEXT:
                right = addRightButton();
                left = addLeftText(str_left, getListenerLeft());
                layout.addView(left, getLayoutParams(RelativeLayout.ALIGN_PARENT_LEFT));
                layout.addView(right, getTextLayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT));
				break;
			case TITLE_HAVE_BOTHTEXT:
				left = addLeftText(str_left, getListenerLeft());
				right = addRightText(str_right, getListenerRight());
				layout.addView(left, getTextLayoutParams(RelativeLayout.ALIGN_PARENT_LEFT));
				layout.addView(right, getTextLayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT));
				break;

		}

		//添加导航栏标题
		RelativeLayout.LayoutParams lpCenter = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lpCenter.addRule(RelativeLayout.CENTER_IN_PARENT);
		center = this.addCenterText(text, this.getListenerCenter());
		lpCenter.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.titleBarMargin) * 3;
		lpCenter.rightMargin = lpCenter.leftMargin;
		layout.addView(center, lpCenter);

        //为导航栏添加分割线
        addBarBottomLine();

	}

    private void addBarBottomLine() {
        View view = new View(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                context.getResources().getDimensionPixelSize(R.dimen.titleBarDividerLineHeight));
        view.setBackgroundColor(context.getResources().getColor(R.color.titleBarDivideLineColor));
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.addView(view, lp);
    }

    /**
	 * @param align 左边还是右边
	 * @return
	 */
	private RelativeLayout.LayoutParams getLayoutParams(int align) {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		lp.addRule(align);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		return lp;
	}
	/**
	 * 如果俩边是字符串，则长度和宽度设置成WRAP_CONTENT,
	 * @param align 左边还是右边
	 * @return
	 */
	private RelativeLayout.LayoutParams getTextLayoutParams(int align) {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.addRule(align);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		return lp;
	}
	/**
	 * 添加中间字符串
	 * 
	 * @param title
	 * @param listener
	 * @return
	 */
	protected View addCenterText(String title, OnClickListener listener) {
		tv_center = new TextView(this.context);
		tv_center.setTextSize(18);
		tv_center.setTextColor(context.getResources().getColor(
				R.color.titleBarTextColor));
		tv_center.setText(title);
		tv_center.setSingleLine(true);
		tv_center.setEllipsize(TruncateAt.END);
		if (listener != null) {
			tv_center.setOnClickListener(listener);
		}
		return tv_center;
	}

	/**
	 * 添加左边字符串
	 * @param listener
	 * @return
	 */
	protected View addLeftText(String text, OnClickListener listener) {
		tv_left = new TextView(this.context);
		tv_left.setTextSize(16);
		tv_left.setTextColor(context.getResources().getColor(
				R.color.titleBarLeftTextColor));
		tv_left.setText(text);
		//设置左右间距
		tv_left.setPadding(context.getResources().getDimensionPixelSize(R.dimen.titleBarMargin),
				0, context.getResources().getDimensionPixelSize(R.dimen.titleBarMargin), 0);
		if (listener != null) {
			tv_left.setOnClickListener(listener);
		}
		return tv_left;
	}

	/**
	 * 添加右边字符串
	 *
	 * @param listener
	 * @return
	 */
	protected View addRightText(String text, OnClickListener listener) {
		tv_right = new TextView(this.context);
		tv_right.setTextSize(16);
		tv_right.setTextColor(context.getResources().getColor(
				R.color.titleBarRightTextColor));
		tv_right.setText(text);
		tv_right.setSingleLine(true);
		//设置左右间距
		tv_right.setPadding(context.getResources().getDimensionPixelSize(R.dimen.titleBarMargin),
				0, context.getResources().getDimensionPixelSize(R.dimen.titleBarMargin), 0);
		if (listener != null) {
			tv_right.setOnClickListener(listener);
		}

		return tv_right;
	}

	public void setCenterText(String text) {
		tv_center.setText(text);
	}

	public void resetLeftListener(OnClickListener listener) {
		left.setOnClickListener(listener);
	}

	public void resetRightListener(OnClickListener listener) {
		right.setOnClickListener(listener);
	}

	/**
	 * 左边新建图片按钮
	 * 
	 * @return
	 */
	public View addLeftButton() {
		return this.addButton(this.getLeftResource(), this.getListenerLeft());
	}

	/**
	 * 右边新建图片按钮
	 * 
	 * @return
	 */
	public View addRightButton() {
		return this.addButton(this.getRightResource(), this.getListenerRight());
	}

	/**
	 * 添加按钮具体实现方法
	 * 
	 * @param id
	 * @param listener
	 * @return
	 */
	protected View addButton(int id, OnClickListener listener) {
		ImageView button = new ImageView(this.context);
		button.setImageResource(id);
		button.setScaleType(ScaleType.CENTER_INSIDE);
		//设置左右间距
		button.setPadding(context.getResources().getDimensionPixelSize(R.dimen.titleBarMargin),
				0, context.getResources().getDimensionPixelSize(R.dimen.titleBarMargin), 0);
		if(listener != null)
			button.setOnClickListener(listener);
		return button;
	}

	public int getRightResource() {
		return rightButtonResource;
	}

	public int getLeftResource() {
		return leftButtonResource;
	}

	public OnClickListener getListenerLeft() {
		return listenerLeft;
	}

	public void setListenerLeft(OnClickListener listenerLeft) {
		this.listenerLeft = listenerLeft;
	}

	public OnClickListener getListenerRight() {
		return listenerRight;
	}

	public void setListenerRight(OnClickListener listenerRight) {
		this.listenerRight = listenerRight;
	}

	public OnClickListener getListenerCenter() {
		return listenerCenter;
	}

	public void setListenerCenter(OnClickListener listenerCenter) {
		this.listenerCenter = listenerCenter;
	}

	/**
	 * @return the context
	 */
	public Activity getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Activity context) {
		this.context = context;
	}
}
