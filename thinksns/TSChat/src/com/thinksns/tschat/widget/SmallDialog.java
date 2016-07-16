package com.thinksns.tschat.widget;

import android.content.Context;

import com.thinksns.tschat.R;


public class SmallDialog extends CustomerDialogNoTitle {
	public SmallDialog(Context context, String title) {
		super(context, R.style.toastDialog, R.layout.toast_dialog, title);
	}

	public SmallDialog(Context context, String title, float margin) {
		super(context, R.style.toastDialog, R.layout.toast_dialog, margin, title);
	}
}
