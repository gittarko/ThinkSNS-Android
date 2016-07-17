package com.thinksns.sociax.component;

import com.thinksns.sociax.android.R;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MyTextView extends TextView {

	public MyTextView(Context context) {
		super(context);
		setTextSize(18);// 字体大小
		setTextColor(getResources().getColor(R.color.main_fant_color));// 字体颜色
		setBackgroundResource(R.drawable.user_info_text_bg);
		setGravity(Gravity.CENTER_VERTICAL);
		setPadding(8, 8, 8, 8);

		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 3);
		setLayoutParams(params);

	}
}
