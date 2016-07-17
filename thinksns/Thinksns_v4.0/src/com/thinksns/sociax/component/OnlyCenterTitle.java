package com.thinksns.sociax.component;

import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.android.R;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class OnlyCenterTitle extends CustomTitle {
	private int leftButtonResource;
	private int rightButtonResource;
	private Activity context;
	public final static int AT_ME = 444;
	public final static int AT_COMMENT = 555;
	public final static int AT_MESSAGE = 666;

	private Button atButton;
	private Button messageButton;
	private Button commentButton;

	public OnlyCenterTitle(Activity context) {
		super(context, ((ThinksnsAbscractActivity) context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity) context;
		this.context = context;
		this.setView(activity.getTitleCenter(), TITLE_ONLY_CENTER);
	}

	@Override
	public int getRightResource() {
		return rightButtonResource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thinksns.components.CustomTitle#addCenterText(java.lang.String,
	 * android.view.View.OnClickListener)
	 */
	@Override
	protected View addCenterText(String title, OnClickListener listener) {
		LinearLayout.LayoutParams lpCenter = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		LinearLayout layout = new LinearLayout(this.context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(lpCenter);
		String[] buttonText = title.split(",");
		atButton = new Button(this.context);
		commentButton = new Button(this.context);
		messageButton = new Button(this.context);
		atButton.setText(buttonText[0]);
		commentButton.setPadding(0, 2, 0, 0);
		atButton.setPadding(0, 2, 0, 0);
		messageButton.setPadding(0, 2, 0, 0);
		commentButton.setText(buttonText[1]);
		messageButton.setText(buttonText[2]);
		atButton.setTextColor(this.context.getResources().getColor(
				R.color.white));
		commentButton.setTextColor(this.context.getResources().getColor(
				R.color.white));
		messageButton.setTextColor(this.context.getResources().getColor(
				R.color.white));
		atButton.setTextSize(14);
		commentButton.setTextSize(14);
		messageButton.setTextSize(14);
		atButton.setBackgroundResource(R.drawable.qie_nav_bg);
		commentButton.setBackgroundResource(R.drawable.qie_bg_02);
		messageButton.setBackgroundResource(R.drawable.qie_bg_03);
		atButton.setId(AT_ME);
		commentButton.setId(AT_COMMENT);
		messageButton.setId(AT_MESSAGE);

		layout.addView(atButton);
		layout.addView(commentButton);
		layout.addView(messageButton);

		return layout;
	}

	@Override
	public int getLeftResource() {
		return leftButtonResource;
	}

}
