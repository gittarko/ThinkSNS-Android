package com.thinksns.sociax.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;

public class Test extends RotateAnimation {

	public Test(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Test(float fromDegrees, float toDegrees) {
		super(fromDegrees, toDegrees);
	}

	public Test(float fromDegrees, float toDegrees, float pivotX, float pivotY) {
		super(fromDegrees, toDegrees, pivotX, pivotY);
	}

	public Test(float fromDegrees, float toDegrees, int pivotXType,
			float pivotXValue, int pivotYType, float pivotYValue) {
		super(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType,
				pivotYValue);
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {

		super.applyTransformation(interpolatedTime, t);

	}
}
