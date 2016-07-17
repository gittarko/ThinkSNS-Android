package com.thinksns.sociax.component;

import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.android.R;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class RightIsButton extends CustomTitle {

	private int leftButtonResource;
	private int rightButtonResource;
	private static String rightText;

	public RightIsButton(Activity context, String text2) {
		super(context, ((ThinksnsAbscractActivity) context).isInTab());
		ThinksnsAbscractActivity activity = (ThinksnsAbscractActivity) context;

		leftButtonResource = activity.getLeftRes();
		rightButtonResource = activity.getRightRes();

		this.setListenerLeft(activity.getLeftListener());
		this.setListenerRight(activity.getRightListener());

		rightText = text2;
		this.setView(activity.getTitleCenter(), TITLE_HAVE_BOTHIMAGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thinksns.components.CustomTitle#addRightButton()
	 */
	@Override
	public View addRightButton() {
		TextView button = new TextView(this.getContext());
		button.setBackgroundResource(this.getRightResource());
		button.setOnClickListener(this.getListenerRight());
		button.setTextColor(this.getContext().getResources()
				.getColor(R.color.white));
		// button.setPadding(10, 20, 10, 2);
		button.setText(rightText);
		button.setGravity(Gravity.CENTER);
		button.setTextSize(14);
		return button;
	}

	@Override
	public int getRightResource() {
		return rightButtonResource;
	}

	@Override
	public int getLeftResource() {
		return leftButtonResource;
	}

}
