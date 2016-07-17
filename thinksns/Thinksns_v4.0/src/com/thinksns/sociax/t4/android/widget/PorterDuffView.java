package com.thinksns.sociax.t4.android.widget;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class PorterDuffView extends ImageView {
	private static final String TAG = "PorterDuffView";
	
	 /** 前景Bitmap高度为1像素。采用循环多次填充进度区域。 */
	 public static final int FG_HEIGHT = 1;
	 /** 前景色*/
	 public static final int FOREGROUND_COLOR = 0xa0000000;
	 /** 进度值文本颜色*/
	 public static final int TEXT_COLOR = 0xffffffff;
	 /**进度之文本大小 */
	 public static final int FONT_SIZE = 40;
	 
	 private Bitmap bitmapBg, bitmapFg;
	 private Paint paint;
	 /** 进度值 */
	 private float progress;
	 /** 加载图片大小 */
	 private int width, height;
	 /** 格式化输出百分比。 */
	 private DecimalFormat decFormat;
	 /** 进度百分比文本的锚定Y中心坐标值。 */
	 private float txtBaseY = 0f;
	 /** 标识是否使用PorterDuff模式重组界面。 */
	 private boolean porterduffMode = true;
	 /** 标识是否正在下载图片。 */
	 private boolean loading = false;
	 
	public PorterDuffView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public PorterDuffView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		Drawable drawable = getDrawable();
		if (porterduffMode && drawable != null
			   && drawable instanceof BitmapDrawable) {
			//设置背景图
			bitmapBg = ((BitmapDrawable) drawable).getBitmap();
			width = bitmapBg.getWidth();
			height = bitmapBg.getHeight();
			bitmapFg = createForegroundBitmap(width);
		} else {
			porterduffMode = false;
		}
		
		paint = new Paint();
		paint.setFilterBitmap(false);
		paint.setAntiAlias(true);		//消除锯齿
		paint.setTextSize(FONT_SIZE);	//设置文本大小
		Paint.FontMetrics fontMetrics = paint.getFontMetrics();
		// 在此处直接计算出来，避免了在onDraw()处的重复计算
		txtBaseY = (height - fontMetrics.bottom - fontMetrics.top) / 2;
			  
		decFormat = new DecimalFormat("0.0%");
	}

		/** 生成一宽与背景图片等同高为1像素的Bitmap，。 */
		 private static Bitmap createForegroundBitmap(int w) {
		  Bitmap bm = Bitmap.createBitmap(w, FG_HEIGHT, Bitmap.Config.ARGB_8888);
		  Canvas c = new Canvas(bm);
		  Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		  p.setColor(FOREGROUND_COLOR);
		  c.drawRect(0, 0, w, FG_HEIGHT, p);
		  return bm;
		 }
		 
		 @Override
		protected void onDraw(Canvas canvas) {
			 if (porterduffMode) {
				 
				   int tmpW = ((getWidth() - width) / 2);
				   tmpW = tmpW < 0 ? 0 : tmpW;
					int tmpH = (getHeight() - height) / 2;
					tmpH = tmpH < 0 ? 0 : tmpH;
					 // 画出背景图
				   canvas.drawBitmap(bitmapBg, tmpW, tmpH, paint);
				   // 设置PorterDuff模式
				   paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
				   int tH = height - (int) (progress * height);
				   for (int i = 0; i < tH; i++) {
					   canvas.drawBitmap(bitmapFg, tmpW, tmpH + i, paint);
				   }
				   
				   //重置porterfermode
				   paint.setXfermode(null);
				   int oriColor = paint.getColor();
				   paint.setColor(TEXT_COLOR);
				   paint.setTextSize(FONT_SIZE);
				   String tmp = decFormat.format(progress);
				   float tmpWidth = paint.measureText(tmp);
				   canvas.drawText(decFormat.format(progress), tmpW
				     + (width - tmpWidth) / 2, tmpH + txtBaseY, paint);
				   // 恢复为初始值时的颜色
				   paint.setColor(oriColor);
				   
			 } else {
				 Log.i(TAG, "onDraw super");
				 super.onDraw(canvas);
			 }
		}
		 
		 public void setProgress(float progress) {  
			 if (porterduffMode) {  
				 if (this.progress != progress) {  
					 this.progress = progress;  
					 Log.e("PorterDuffView", "progress:" + progress);
					 // 刷新自身。  
		             invalidate();  
				 }  
		     }  
		 }
		 
		 public void setBitmap(Bitmap bg) {  
		        if (porterduffMode) {  
		            bitmapBg = bg;  
		            width = bitmapBg.getWidth();  
		            height = bitmapBg.getHeight();  
		  
		            bitmapFg = createForegroundBitmap(width);  
		  
		            Paint.FontMetrics fontMetrics = paint.getFontMetrics();  
		            txtBaseY = (height - fontMetrics.bottom - fontMetrics.top) / 2;  
		  
		            setImageBitmap(bg);
		        }  
		    }  
		  
		    public boolean isLoading() {  
		        return loading;  
		    }  
		  
		    public void setLoading(boolean loading) {  
		        this.loading = loading;  
		    }  
		  
		    public void setPorterDuffMode(boolean bool) {  
		        porterduffMode = bool;  
		    }  
}