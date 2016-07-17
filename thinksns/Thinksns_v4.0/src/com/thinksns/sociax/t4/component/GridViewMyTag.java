package com.thinksns.sociax.t4.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 类说明：我的标签
 * 
 * @author Zoey
 * @date 2015年10月25日
 * @version 1.0
 */
public class GridViewMyTag extends GridView {

	public GridViewMyTag(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridViewMyTag(Context context) {
		super(context);
	}

	public GridViewMyTag(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	
	//禁止滑动事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
             return true;  //禁止GridView滑动
        }
        return super.dispatchTouchEvent(ev);
	}
}
