package com.thinksns.sociax.component;

import android.content.Context;
import android.view.Gravity;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class QuesCateTextView extends TextView {

	public QuesCateTextView(Context context) {
		super(context);
		setTextSize(16);// 字体大小
		setGravity(Gravity.CENTER);
		setPadding(8, 8, 8, 8);

		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		params.setMargins(0, 0, 8, 8);
		setLayoutParams(params);

		/*
		 * LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
		 * LayoutParams.WRAP_CONTENT); params.setMargins(0, 0, 0, 3);
		 * setLayoutParams(params);
		 */

	}
}
