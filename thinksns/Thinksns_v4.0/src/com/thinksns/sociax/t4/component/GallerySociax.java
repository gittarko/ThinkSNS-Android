package com.thinksns.sociax.t4.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * 类说明： 广告播放组建
 * 
 * @author wz
 * @date 2014-12-11
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public class GallerySociax extends Gallery {

	public GallerySociax(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public GallerySociax(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

}
