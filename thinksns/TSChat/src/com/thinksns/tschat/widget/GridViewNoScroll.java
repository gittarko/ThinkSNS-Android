package com.thinksns.tschat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 类说明：
 * 
 * @author wz
 * @date 2014-11-28
 * @version 1.0
 */
public class GridViewNoScroll extends GridView {
	public GridViewNoScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, mExpandSpec);
	}
}
