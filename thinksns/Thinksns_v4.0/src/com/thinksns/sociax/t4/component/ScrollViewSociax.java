package com.thinksns.sociax.t4.component;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * 类说明： 重写scrollview，防止嵌套viewpager的时候冲突
 * 
 * @author wz
 * @date 2014-11-5
 * @version 1.0
 */
public class ScrollViewSociax extends ScrollView {
	private float xDistance, yDistance, xLast, yLast;

	public ScrollViewSociax(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance >= yDistance) {
				return false;
			}

		}
		return super.onInterceptTouchEvent(ev);
	}

}
