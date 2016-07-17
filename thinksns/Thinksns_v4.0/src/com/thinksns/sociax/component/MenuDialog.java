package com.thinksns.sociax.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class MenuDialog extends Dialog {

	public MenuDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public MenuDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.weibo_app_more);
	}

}
