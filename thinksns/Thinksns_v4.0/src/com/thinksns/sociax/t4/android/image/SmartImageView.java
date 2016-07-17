package com.thinksns.sociax.t4.android.image;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.thinksns.sociax.android.R;

public class SmartImageView extends ImageView {
	// public class SmartImageView extends MaskedImage {
	private static final int LOADING_THREADS = 4;
	private String imgUrl;
	private static ExecutorService threadPool = Executors
			.newFixedThreadPool(LOADING_THREADS);

	private SmartImageTask currentTask;

	public SmartImageView(Context context) {
		super(context);

	}

	public SmartImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setImageUrl(String url) {
		setImage(new WebImage(url));
		this.imgUrl = url;
	}

	public void setImageUrl(String url,
			SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url), completeListener);
	}

	public void setImageUrl(String url, final Integer fallbackResource) {
		setImage(new WebImage(url), fallbackResource);
	}

	public void setImageUrl(String url, final Integer fallbackResource,
			SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url), fallbackResource, completeListener);
	}

	public void setImageUrl(String url, final Integer fallbackResource,
			final Integer loadingResource) {
		setImage(new WebImage(url), fallbackResource, loadingResource);
	}

	public void setImageUrl(String url, final Integer fallbackResource,
			final Integer loadingResource,
			SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url), fallbackResource, loadingResource,
				completeListener);
	}

	// Helpers to set image by contact address book id
	public void setImageContact(long contactId) {
		setImage(new ContactImage(contactId));
	}

	public void setImageContact(long contactId, final Integer fallbackResource) {
		setImage(new ContactImage(contactId), fallbackResource);
	}

	public void setImageContact(long contactId, final Integer fallbackResource,
			final Integer loadingResource) {
		setImage(new ContactImage(contactId), fallbackResource,
				fallbackResource);
	}

	// Set image using SmartImage object
	public void setImage(final SmartImage image) {
		setImage(image, null, R.drawable.default_image_small, null);
	}

	public void setImage(final SmartImage image,
			final SmartImageTask.OnCompleteListener completeListener) {
		setImage(image, null, null, completeListener);
	}

	public void setImage(final SmartImage image, final Integer fallbackResource) {
		setImage(image, fallbackResource, fallbackResource, null);
	}

	public void setImage(final SmartImage image,
			final Integer fallbackResource,
			SmartImageTask.OnCompleteListener completeListener) {
		setImage(image, fallbackResource, fallbackResource, completeListener);
	}

	public void setImage(final SmartImage image,
			final Integer fallbackResource, final Integer loadingResource) {
		setImage(image, fallbackResource, loadingResource, null);
	}

	public void setImage(final SmartImage image,
			final Integer fallbackResource, final Integer loadingResource,
			final SmartImageTask.OnCompleteListener completeListener) {
		// Set a loading resource
		if (loadingResource != null) {
			setImageResource(loadingResource);
		}

		// Cancel any existing tasks for this image view
		if (currentTask != null) {
			currentTask.cancel();
			currentTask = null;
		}

		// Set up the new task
		currentTask = new SmartImageTask(getContext(), image);
		currentTask
				.setOnCompleteHandler(new SmartImageTask.OnCompleteHandler() {
					@Override
					public void onComplete(Bitmap bitmap) {
						if (bitmap != null) {
							// 这里应该是没有压缩而导致不清晰的
							setImageBitmap(fixTheNotClearBitmap(bitmap));
						} else {
							// Set fallback resource
							if (fallbackResource != null) {
								setImageResource(fallbackResource);
							}
						}

						if (completeListener != null) {
							completeListener.onComplete(bitmap);
						}
					}

					// 修复不清晰的bitmap ，悄悄做一下压缩
					private Bitmap fixTheNotClearBitmap(Bitmap bitmap) {
						Log.i("bitmap",
								"bitmap.getWidth()=" + bitmap.getWidth()
										+ "bitmap.getHeight()="
										+ bitmap.getHeight());
						LayoutParams params = getLayoutParams();
						Log.i("bitmap", "params.width=" + params.width);
						float option = (float) bitmap.getWidth()
								/ (float) params.width;
						if (option < 0) {
							option = 1;
						}
						int bitmapWidth = (int) (bitmap.getWidth() / option);
						int bitmapHeight = (int) (bitmap.getHeight() / option);
						bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth,
								bitmapHeight, true);
						Log.i("bitmap",
								"after_bitmap.getWidth()=" + bitmap.getWidth()
										+ "bitmap.getHeight()="
										+ bitmap.getHeight());
						return bitmap;
					}

				});

		// Run the task in a threadpool
		threadPool.execute(currentTask);
	}

	public static void cancelAllTasks() {
		threadPool.shutdownNow();
		threadPool = Executors.newFixedThreadPool(LOADING_THREADS);
	}

	/*********** gif *******************/
	/**
	 * 播放GIF动画的关键类
	 */
	private Movie mMovie;

	/**
	 * 开始播放按钮图片
	 */
	private Bitmap mStartButton;

	/**
	 * 记录动画开始的时间
	 */
	private long mMovieStart;

	/**
	 * GIF图片的宽度
	 */
	private int mImageWidth;

	/**
	 * GIF图片的高度
	 */
	private int mImageHeight;

	/**
	 * 图片是否正在播放
	 */
	private boolean isPlaying;

	/**
	 * 是否允许自动播放
	 */
	private boolean isAutoPlay;

	/**
	 * PowerImageView构造函数，在这里完成所有必要的初始化操作。
	 * 
	 * @param context
	 */
	public SmartImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PowerImageView);
		int resourceId = getResourceId(a, context, attrs);
		if (resourceId != 0) {
			// 当资源id不等于0时，就去获取该资源的流
			InputStream is = getResources().openRawResource(resourceId);
			// 使用Movie类对流进行解码
			mMovie = Movie.decodeStream(is);
			if (mMovie != null) {
				// 如果返回值不等于null，就说明这是一个GIF图片，下面获取是否自动播放的属性
				// Log.v("SmartImageView--onDraw","SmartImageView gif");
				isAutoPlay = true;
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				mImageWidth = bitmap.getWidth();
				mImageHeight = bitmap.getHeight();
				bitmap.recycle();
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mMovie == null) {
			// mMovie等于null，说明是张普通的图片，则直接调用父类的onDraw()方法
			super.onDraw(canvas);
			// Log.v("SmartImageView--onDraw","wztest 普通图片");
		} else {
			// mMovie不等于null，说明是张GIF图片

			// Log.v("SmartImageView--onDraw","wztest GIF图片");
			if (isAutoPlay) {
				// 如果允许自动播放，就调用playMovie()方法播放GIF动画
				playMovie(canvas);
				invalidate();
			} else {
				// 不允许自动播放时，判断当前图片是否正在播放
				if (isPlaying) {
					// 正在播放就继续调用playMovie()方法，一直到动画播放结束为止
					if (playMovie(canvas)) {
						isPlaying = false;
					}
					invalidate();
				} else {
					// 还没开始播放就只绘制GIF图片的第一帧，并绘制一个开始按钮
					mMovie.setTime(0);
					mMovie.draw(canvas, 0, 0);
					int offsetW = (mImageWidth - mStartButton.getWidth()) / 2;
					int offsetH = (mImageHeight - mStartButton.getHeight()) / 2;
					canvas.drawBitmap(mStartButton, offsetW, offsetH, null);
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mMovie != null) {
			// 如果是GIF图片则重写设定PowerImageView的大小
			setMeasuredDimension(mImageWidth, mImageHeight);
		}
	}

	/**
	 * 开始播放GIF动画，播放完成返回true，未完成返回false。
	 * 
	 * @param canvas
	 * @return 播放完成返回true，未完成返回false。
	 */
	private boolean playMovie(Canvas canvas) {
		long now = SystemClock.uptimeMillis();
		if (mMovieStart == 0) {
			mMovieStart = now;
		}
		int duration = mMovie.duration();
		if (duration == 0) {
			duration = 1000;
		}
		int relTime = (int) ((now - mMovieStart) % duration);
		mMovie.setTime(relTime);
		mMovie.draw(canvas, 0, 0);
		if ((now - mMovieStart) >= duration) {
			mMovieStart = 0;
			return true;
		}
		return false;
	}

	/**
	 * 通过Java反射，获取到src指定图片资源所对应的id。
	 * 
	 * @param a
	 * @param context
	 * @param attrs
	 * @return 返回布局文件中指定图片资源所对应的id，没有指定任何图片资源就返回0。
	 */
	private int getResourceId(TypedArray a, Context context, AttributeSet attrs) {
		try {
			Field field = TypedArray.class.getDeclaredField("mValue");
			field.setAccessible(true);
			TypedValue typedValueObject = (TypedValue) field.get(a);
			return typedValueObject.resourceId;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (a != null) {
				a.recycle();
			}
		}
		return 0;
	}
}