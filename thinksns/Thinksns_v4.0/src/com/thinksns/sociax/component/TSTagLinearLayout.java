package com.thinksns.sociax.component;

import com.thinksns.sociax.android.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 类说明：
 * 
 * @author povol
 * @date May 8, 2013
 * @version 1.0
 */
public class TSTagLinearLayout extends LinearLayout {

	private View v;
	private TextView one;
	private TextView two;
	private TextView three;
	private TextView four;

	public TSTagLinearLayout(Context context) {
		super(context);
		initOtherComponent(context);
	}

	public TSTagLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initOtherComponent(context);
	}

	@SuppressLint("NewApi")
	public TSTagLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initOtherComponent(context);
	}

	private void initOtherComponent(Context context) {
		v = View.inflate(context, R.layout.find_tag_view, this);
	}

	public void setText(int i, String o, int id) {
		switch (i) {
		case 1:
			one = (TextView) v.findViewById(R.id.tag_one);
			one.setText(o);
			one.setTag(id);
			one.setVisibility(View.VISIBLE);
			break;
		case 2:
			two = (TextView) v.findViewById(R.id.tag_two);
			two.setText(o);
			two.setTag(id);
			two.setVisibility(View.VISIBLE);
			break;
		case 3:
			three = (TextView) v.findViewById(R.id.tag_three);
			three.setText(o);
			three.setTag(id);
			three.setVisibility(View.VISIBLE);
			break;
		case 4:
			four = (TextView) v.findViewById(R.id.tag_four);
			four.setText(o);
			four.setTag(id);
			four.setVisibility(View.VISIBLE);
			break;
		}
	}
}
