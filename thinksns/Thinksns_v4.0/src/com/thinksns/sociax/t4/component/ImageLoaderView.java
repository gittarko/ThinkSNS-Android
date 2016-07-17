package com.thinksns.sociax.t4.component;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.unit.UnitSociax;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageLoaderView extends FrameLayout {

    private static final float DEFAULT_AlPHA = 0.3f;  // 默认透明度
    private static final int ASPECT_RATIO = 3;    // 压缩比例,当前为1:3
    private static final int ERROR_RESOURCE_ID = -1;

    /**
     * 控件显示状态
     */
    public enum Status {
        TAG_NO_WORKING,
        TAG_IS_LOADING,
        TAG_LOADING_FINISHED,
        TAG_LOADING_ERROR
    }

    // 默认no working
    private Status mStatus = Status.TAG_NO_WORKING;
    protected ImageView mImageView, mBackground;
    protected ProgressBar mProgressBar;

    private float defaultMaxWidth;
    private float defaultMaxHeight;
    private float modifyWidth;
    private float modifyHeight;
    private boolean isChanged;

    public ImageLoaderView(Context context) {
        this(context, null);
    }

    public ImageLoaderView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.ImageLoaderView_src);
    }

    /**
     * 初始化必要的数据
     */
    public ImageLoaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        // 初始化进度条
        mProgressBar = new ProgressBar(context);
//        mProgressBar = new ProgressBar(context, null, R.style.CustomProgressStyle);
        mProgressBar.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
//        mProgressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress));

        // 计算屏幕比
        defaultMaxWidth = (UnitSociax.getWindowWidth(context) /5)*2;
        defaultMaxHeight = defaultMaxWidth;
        
        // 获取自定义属性
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageLoaderView);
        // 获取图片
        int id = a.getResourceId(R.styleable.ImageLoaderView_src, ERROR_RESOURCE_ID);
        a.recycle();
        if (id != ERROR_RESOURCE_ID) {
            mImageView = new ImageView(getContext());
            setImage(id);
        }
    }

    /**
     * 设置image
     *
     * @param id image resource id
     */
    public void setImage(int id) {
    	if (mImageView == null) {
    		mImageView = new ImageView(getContext());
    	} else {
    		removeView(mImageView);
    	}
        // 计算图片长宽
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), id, opts);
        // 处理图片长宽
        countImageSize(opts.outWidth, opts.outHeight);
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
        // 压缩并添加图片
        processImage(bitmap);
    }

    /**
     * 设置image
     *
     * @param path image file path
     */
    public void setImage(String path) {
    	if (mImageView == null) {
    		mImageView = new ImageView(getContext());
    	}  else {
    		removeView(mImageView);
    	}
        // 计算图片长宽
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        // 处理图片长宽
        countImageSize(opts.outWidth, opts.outHeight);
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        // 压缩并添加图片
        processImage(bitmap);

    }

    /**
     * 压缩并添加图片
     *
     * @param bitmap
     */
    private void processImage(Bitmap bitmap) {
        if (isChanged) {
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) modifyWidth, (int) modifyHeight, false);
        }
        mImageView.setImageBitmap(bitmap);
        mImageView.setLayoutParams(new LayoutParams((int) modifyWidth, (int) modifyHeight, Gravity.CENTER));
//        mImageView.setPadding(0, 0, 20, 0);
        addView(mImageView);
        if (mBackground != null) {
        	removeView(mBackground);
        }
        mBackground = new ImageView(getContext());
        mBackground.setImageDrawable(getResources().getDrawable(R.drawable.bg_chat_pic_send));
        mBackground.setLayoutParams(new ViewGroup.LayoutParams((int) modifyWidth, (int) modifyHeight));
        mBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(mBackground);
    }

    /**
     * 计算图片
     *
     * @param width  原始宽度
     * @param height 原始高度
     */
    private void countImageSize(int width, int height) {
        // 判断图片长宽
        modifyWidth = defaultMaxWidth;
        modifyHeight = defaultMaxHeight;
        isChanged = false;
        if (width > defaultMaxWidth || height > defaultMaxHeight) {
            // 大于规定长宽
            float scale = 0.0f;
            // 判断横图或竖图
            if (width > height) {
                // 为横图
                scale = width / defaultMaxWidth;
                modifyHeight = height / scale;
            } else {
                // 为竖图
                scale = height / defaultMaxHeight;
                modifyWidth = width / scale;
            }
            isChanged = true;
        } else {
        	modifyWidth = width;
        	modifyHeight = height;
        }
    }

    /**
     * 处理正在加载状态
     */
    public void isLoading() {
        if (mImageView != null && mStatus != Status.TAG_IS_LOADING) {
            mStatus = Status.TAG_IS_LOADING;
            mImageView.setAlpha(DEFAULT_AlPHA);
            addView(mProgressBar);
        }
    }

    /**
     * 处于加载完毕状态
     */
    public void onLoadFinish() {
        if (mImageView != null && mStatus != Status.TAG_LOADING_FINISHED) {
            mStatus = Status.TAG_LOADING_FINISHED;
            mImageView.setAlpha(1f);
            removeView(mProgressBar);
        }
    }

    /**
     * 获取当前状态
     *
     * @return 状态码
     */
    public Status getmStatus() {
        return mStatus;
    }
}
