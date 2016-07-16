package com.thinksns.sociax.thinksnsbase.activity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.thinksns.sociax.thinksnsbase.R;

public class LoadingView extends RelativeLayout {
	public static final int ID = 3306;

	public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.initOtherComponent(context);
	}

	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initOtherComponent(context);
	}

	public LoadingView(Context context) {
		super(context);
		this.initOtherComponent(context);
	}

	/**
	 * 显示loadingview，隐藏view,view为null时不隐藏，但显示loadingview
	 * 
	 * @param view
	 */
	public void show(View view) {
		final View v = view;
		this.post(new Runnable() {
			@Override
			public void run() {
				if (v != null) {
					v.setVisibility(View.GONE);
					LoadingView.this.setVisibility(View.VISIBLE);
				} else {
					LoadingView.this.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/**
	 * 显示loadingview
	 */
	public void show() {
		this.post(new Runnable() {
			@Override
			public void run() {
				LoadingView.this.setVisibility(View.VISIBLE);
			}
		});
	}

	public void showInfo(String text, View view) {
		final String info = text;
		final View v = view;
		this.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show(); // TODO
																				// Auto-generated
																				// method
																				// stub
				v.setVisibility(View.GONE);
				LoadingView.this.setVisibility(View.VISIBLE);
			}

		});
	}

	public void error(String text) {
		final String info = text;
		// final View v = view;
		this.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show(); // TODO
																				// Auto-generated
																				// method
																				// stub
				// v.setVisibility(View.GONE);
				// LoadingView.this.setVisibility(View.VISIBLE);
			}

		});
	}

	/**
	 * 隐藏LoadingView，显示view
	 * 
	 * @param view
	 *            显示的View
	 */
	public void hide(View view) {
		final View v = view;
		this.post(new Runnable() {
			@Override
			public void run() {
				v.setVisibility(View.VISIBLE);
				LoadingView.this.setVisibility(View.GONE);
			}
		});

	}

	private void initOtherComponent(Context context) {
		this.setId(ID);
		View.inflate(context, R.layout.loading, this);
		this.setVisibility(View.GONE);
	}

}
