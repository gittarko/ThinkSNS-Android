package com.thinksns.sociax.component;

import com.thinksns.sociax.android.R;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 类说明：
 * 
 * @author povol
 * @date May 10, 2013
 * @version 1.0
 */
public class TagCategoryTextView extends TextView {

	public TagCategoryTextView(Context context) {
		super(context);
		this.setBackgroundResource(R.drawable.find_tag_bg);
		setTextColor(context.getResources().getColor(R.color.black));

		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 8);

		setLayoutParams(params);

		setGravity(Gravity.CENTER_VERTICAL);
		setPadding(16, 0, 0, 0);

	}

}
