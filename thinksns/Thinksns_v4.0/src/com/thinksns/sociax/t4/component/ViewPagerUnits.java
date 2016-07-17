package com.thinksns.sociax.t4.component;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-11-19
 * @version 1.0
 */
public class ViewPagerUnits extends ViewPager {

	public ViewPagerUnits(Context context) {
		super(context);
	}

	public ViewPagerUnits(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
