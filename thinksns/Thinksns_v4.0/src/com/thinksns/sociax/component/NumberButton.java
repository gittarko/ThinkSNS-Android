package com.thinksns.sociax.component;

import com.thinksns.sociax.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NumberButton extends RelativeLayout {
	private TextView count;
	private TextView text;

	public NumberButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initLayout(context);
	}

	public NumberButton(Context context) {
		super(context);
		this.initLayout(context);
	}

	public void setText(int res) {
		text.setText(res);
	}

	public void setCount(int counts) {
		count.setText(counts + "");
	}

	public void setCount(int counts, String type) {
		count.setText(counts);
	}

	private void initLayout(Context context) {
		inflate(context, R.layout.numberbutton, this);
		count = (TextView) findViewById(R.id.count);
		text = (TextView) findViewById(R.id.text);
	}

}
