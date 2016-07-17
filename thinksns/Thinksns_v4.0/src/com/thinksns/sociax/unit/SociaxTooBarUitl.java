package com.thinksns.sociax.unit;

import com.thinksns.sociax.android.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class SociaxTooBarUitl {

	public static void setTextPressed(Context context, View view,
			View[] viewArr, int[] norImgArr, int[] preImgArr) {
		for (View iview : viewArr) {
			TextView view2 = (TextView) iview;
			int index = (Integer) view2.getTag();
			if (view2.equals(view)) {
				view2.setBackgroundResource(R.drawable.weibo_app_bar_p);
				view2.setCompoundDrawablesWithIntrinsicBounds(0,
						preImgArr[index], 0, 0);
				view2.setTextColor(Color.WHITE);
				// view2.getTag();
				// view2.setCompoundDrawablesWithIntrinsicBounds(0,
				// R.drawable.weibo_app_bar_p, 0, 0);
				// (TextView)view2
				// System.out.println(view.getTag()+".png");
				view2.invalidate();
			} else {
				view2.setBackgroundResource(R.drawable.weibo_app_bar_n);
				view2.setCompoundDrawablesWithIntrinsicBounds(0,
						norImgArr[index], 0, 0);
				// view2.setTextColor(Color.red(R.color.weibo_app_bar_text));
				view2.setTextColor(context.getResources().getColor(
						R.color.weibo_app_bar_text));
				view2.invalidate();
			}
		}
	}

}
