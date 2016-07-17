package com.thinksns.sociax.component;

import com.thinksns.sociax.android.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageBroder extends ImageView {
	public ImageBroder(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setColor(context);
	}

	public ImageBroder(Context context) {
		super(context);
		this.setColor(context);
	}

	private int innerColor;
	private int broderColor;

	public ImageBroder(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setColor(context);
	}

	private void setColor(Context context) {
		innerColor = context.getResources().getColor(R.color.white);
		broderColor = context.getResources().getColor(R.color.imageBroder);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Rect rec = canvas.getClipBounds();
		rec.bottom--;
		rec.right--;
		Paint paint = new Paint();
		paint.setColor(broderColor);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(rec, paint);

		rec.left++;
		rec.top++;
		rec.bottom--;
		rec.right--;
		paint.setColor(innerColor);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(rec, paint);
	}

}
