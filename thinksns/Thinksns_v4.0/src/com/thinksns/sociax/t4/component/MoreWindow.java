package com.thinksns.sociax.t4.component;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.unit.FastBlur;

public class MoreWindow extends PopupWindow implements OnClickListener{

    private static final float DEFAULT_SCALE_FACTOR = 8;
    private static final float DEFAULT_RADIUS = 20;

    private Activity mContext;
    private RelativeLayout mBtnGroup;
    private Bitmap overlay;

    private IMoreWindowListener listener;
    
    public interface IMoreWindowListener {
    	void OnItemClick(View v);
    }

    private Handler mHandler = new Handler();

    public MoreWindow(Activity context) {
        mContext = context;

        // 设置width和height
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        setWidth(metrics.widthPixels);
        setHeight(metrics.heightPixels);
    }

    /**
     * 获取系统状态栏和软件标题栏
     */
    private int getOtherHeight() {
        Rect frame = new Rect();
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int contentTop = mContext.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTop - statusBarHeight;
        return statusBarHeight + titleBarHeight;
    }

    private Bitmap blur() {
        if (null != overlay) {
            return overlay;
        }

        // 截屏
        View view = mContext.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap tempBitmap = view.getDrawingCache();

        int width = tempBitmap.getWidth();
        int height = tempBitmap.getHeight();

        // 截取一半屏幕
//        Bitmap half = Bitmap.createBitmap(tempBitmap, 0, height, width, height / 2);
//        tempBitmap.recycle();

        overlay = Bitmap.createBitmap((int) (width / DEFAULT_SCALE_FACTOR), (int) (height / DEFAULT_SCALE_FACTOR), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / DEFAULT_SCALE_FACTOR, 1 / DEFAULT_SCALE_FACTOR);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(tempBitmap, 0, 0, paint);
        tempBitmap.recycle();

        overlay = FastBlur.doBlur(overlay, (int) DEFAULT_RADIUS, true);
        return overlay;
    }

    private Animation showAnimation1(final View view, int fromY, int toY) {
        AnimationSet set = new AnimationSet(true);
        TranslateAnimation go = new TranslateAnimation(0, 0, fromY, toY);
        go.setDuration(300);
        TranslateAnimation go1 = new TranslateAnimation(0, 0, -10, 2);
        go1.setDuration(100);
        go1.setStartOffset(250);
        set.addAnimation(go1);
        set.addAnimation(go);

        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
        return set;
    }

    /**
     * 显示窗口
     *
     * @param anchor
     */
    public void showMoreWindow(View anchor) {
        final LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.more_window_create_weibo, null);
        setContentView(layout);

        ImageView close = (ImageView) layout.findViewById(R.id.close);
        mBtnGroup = (RelativeLayout) layout.findViewById(R.id.btnGroup);

        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    closeAnimation(mBtnGroup);
                }
            }
        });

        showAnimation(mBtnGroup);
        setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
        setOutsideTouchable(true);
        setFocusable(true);
        // 暂时不考虑statusBar
        showAtLocation(anchor, Gravity.BOTTOM, 0, 0);
    }

    private void showAnimation(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View child = layout.getChildAt(i);
            child.setOnClickListener(this);
            child.setVisibility(View.INVISIBLE);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
                    fadeAnim.setDuration(500);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(200);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                }
            }, i * 100);
        }
    }

    private void closeAnimation(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View child = layout.getChildAt(i);
            child.setOnClickListener(this);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 0, 600);
                    fadeAnim.setDuration(500);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(200);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                    fadeAnim.addListener(new AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                        	
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        	
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            child.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        	
                        }
                    });
                }
            }, (layout.getChildCount() - i - 1) * 80);

            if (child.getId() == R.id.tv_create_weibo_camera) {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        dismiss();
                    }
                }, (layout.getChildCount() - i) * 80 + 80);
            }
        }

    }
    
    public void setOnItemClick(IMoreWindowListener listener) {
    	this.listener = listener;
    }

    @Override
    public void onClick(View v) {
    	if (listener != null) {
    		listener.OnItemClick(v);
    	}
    	if (isShowing() && mBtnGroup!=null) {
            closeAnimation(mBtnGroup);
        }
    }
}
