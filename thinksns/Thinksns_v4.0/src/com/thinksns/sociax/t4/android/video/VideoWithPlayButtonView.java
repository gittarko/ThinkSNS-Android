package com.thinksns.sociax.t4.android.video;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.video.HttpDownloader.HttpDownloaderCallback;
import com.thinksns.sociax.t4.android.weibo.NetActivity;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;

public class VideoWithPlayButtonView extends FrameLayout implements
		TextureView.SurfaceTextureListener, OnCompletionListener,
		OnErrorListener, OnInfoListener, OnPreparedListener,
		OnSeekCompleteListener, OnVideoSizeChangedListener, MediaPlayerControl {
	private static final String TAG = "TSTAG_VideoWithPlayButtonView";
	private FrameLayout mBackgroundLayout = null;
	private Button mPlayButton = null;
	private Button mRetryButton = null;
	private ImageView mPlayIconImageView = null;
	private ImageView mVideoPreview = null;
	// private VideoView mVideoView = null;
	private int mStopPosition = 0;
	private SurfaceHolder mSurfaceHolder;
	private MediaController mMediaController;
	private boolean mInitialPlay = false;
	private RoundProgressBar mRoundProgressBar;
	private MediaPlayer mMediaPlayer;
	private String mPlayFile = null;
	private SurfaceTexture mSurfaceTexture;
	private TextureView mTextureView;
	private boolean mIsStarted = false;// 是否启动过
	private HttpDownloader mVideoDownloadTask = null;
	private HttpDownloader mImageDownloadTask = null;
	private boolean mIsStopVideo = false;
	private boolean mIsStopByUser = false;// 是否手动暂停
	private boolean mIsStopByOthers = false;// 是否是其他方面的暂停
	private MyBroadCast myBroadCast;
	private DisplayMetrics mDisplayMetrics;

	private Uri mRetryVideoUri = null;
	private Uri mRetryImageUri = null;

	private File cacheFile = null;
	private Context mContext;
	private Surface mSurface = null;
	private Bitmap myBitmap = null;
	private Thinksns application;
	
	private onBeginPlayListener BeginPlayListener;
	
	/**
	 * 是否来自第三方网站
	 */
	private boolean isThirdHost = false;

	private boolean isSetComplectionListener = false;
	private MediaPlayer mediaPlayer = null;
	private VideoStopCallBack videoStopCallBack = null;
	private boolean isAutoPlay = false;
	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	};

	private class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "intent.getAction()=" + intent.getAction());
			if (intent.getAction().equals(StaticInApp.STOPVIDEOINTENT)) {
				if (mIsStopVideo) {
					mIsStopVideo = false;
				} else {
					VideoWithPlayButtonView.this.pause();
				}
			} else if (intent.getAction().equals(StaticInApp.ACTION_UP_INTENT)) {
				int[] location = new int[2];
				mTextureView.getLocationOnScreen(location);
				if (mTextureView != null) {
					Log.d(TAG + "xhs",
							location[0] + "," + location[1]
									+ ",mTextureView.getHeight()"
									+ mTextureView.getHeight());
					if (isAutoPlay) {
						if (location[1] < 0
								&& Math.abs(location[1]) > mTextureView
										.getHeight() / 3) {
							pause();
							mIsStopByUser = false;
						} else if (location[1] > 0
								&& (mDisplayMetrics.heightPixels - location[1]) > mTextureView
										.getHeight()) {
							if (mIsStarted && !mIsStopByUser)
								start();
						} else if ((mDisplayMetrics.heightPixels - location[1]) < mTextureView
								.getHeight() / (1.2)) {
							pause();
						}

					} else {
						if (location[1] < 0
								&& Math.abs(location[1]) > mTextureView
										.getHeight() / 3) {
							pause();

						} else if ((mDisplayMetrics.heightPixels - location[1]) < mTextureView
								.getHeight() / 2) {
							pause();
						}
					}
					Log.d(TAG, "Math.abs=" + Math.abs(location[1]));
				}
			} else if (intent.getAction().equals(
					StaticInApp.STOPVIDEOBYOTHERSINTENT)) {
				mIsStopByOthers = true;
				Log.d(TAG, "mIsStopByOthers=" + mIsStopByOthers);
				if (mVideoPreview != null) {
					mVideoPreview.setVisibility(View.VISIBLE);
					mTextureView.setVisibility(View.GONE);
				}
				pause();
			} else if (intent.getAction().equals(
					StaticInApp.RESUMEVIDEOBYOTHERSINTENT)) {
				mIsStopByOthers = false;
			}
		}
	}

	private void setView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_vidoe_with_button,
				this, true);
		mContext = context;
		mDisplayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		mBackgroundLayout = (FrameLayout) findViewById(R.id.video_with_play_button_view_bakcground_layout);
		mPlayIconImageView = (ImageView) findViewById(R.id.play_video_image);
		mPlayIconImageView.setLayoutParams(new FrameLayout.LayoutParams(UnitSociax.getWindowWidth(mContext)/7, 
				UnitSociax.getWindowWidth(mContext)/7, Gravity.CENTER));
		mPlayButton = (Button) findViewById(R.id.play_button);
		isAutoPlay = ThinksnsActivity.preferences
				.getBoolean("auto_play", false);
		if (isAutoPlay) {
			mPlayIconImageView.setVisibility(View.GONE);
		}
		mVideoPreview = (ImageView) findViewById(R.id.video_preview);
		mTextureView = (TextureView) findViewById(R.id.textureView_videos);
		mRetryButton = (Button) findViewById(R.id.retry_button);
		mTextureView.setEnabled(true);
		mRoundProgressBar = (RoundProgressBar) findViewById(R.id.mRoundProgressBar);
		// setSurface();

		setSurfaceTexture();
		mTextureView.setSurfaceTextureListener(this);
		setupButtonCliked();

		cacheFile = context.getCacheDir();
		myBroadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter(StaticInApp.STOPVIDEOINTENT);
		filter.addAction(StaticInApp.ACTION_UP_INTENT);
		filter.addAction(StaticInApp.STOPVIDEOBYOTHERSINTENT);
		filter.addAction(StaticInApp.RESUMEVIDEOBYOTHERSINTENT);
		mContext.registerReceiver(myBroadCast, filter);
	}

	public void setSurfaceTexture() {
		mSurfaceTexture = mTextureView.getSurfaceTexture();
		mMediaController = new MediaController(mContext);
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnSeekCompleteListener(this);
		mMediaPlayer.setOnVideoSizeChangedListener(this);
	}

	public VideoWithPlayButtonView(Context context) {
		super(context);
		setView(context);
		application = (Thinksns) context.getApplicationContext();
	}

	public VideoWithPlayButtonView(Context context, String localPath) {
		super(context);
		application = (Thinksns) context.getApplicationContext();
		setView(context);
		setupVideoPreviewWithLocalFile(localPath);
		setupVideoViewWithLocaFile(localPath);
	}

	public Button getPlayButton() {
		return mPlayButton;
	}

	/**
	 * 生成图片播放并且携带按钮
	 * 
	 * @param context
	 * @param videoUrl
	 *            视频所在地址，保存到本地的时候
	 * @param previewUrl
	 *            视频预览图片地址
	 * @param isThirdHost
	 *            是否来自第三方
	 */
	public VideoWithPlayButtonView(Context context, Uri videoUrl,
			Uri previewUrl, boolean isThirdHost) {
		super(context);
		this.isThirdHost = isThirdHost;
		application = (Thinksns) context.getApplicationContext();
		mRetryImageUri = previewUrl;
		mRetryVideoUri = videoUrl;
		setView(context);
		checkAndDownloadToCache(previewUrl, videoUrl);

	}

	public void refeshView(Context context, Uri videoUrl, Uri previewUrl) {
		if (mMediaPlayer != null && mSurface != null) {
			return;
		}
		mRetryImageUri = previewUrl;
		mRetryVideoUri = videoUrl;

		setView(context);
		checkAndDownloadToCache(previewUrl, videoUrl);
	}

	public VideoWithPlayButtonView(Context context, int resource) {
		super(context);
		application = (Thinksns) context.getApplicationContext();
		setView(context);
		mPlayIconImageView.setVisibility(View.GONE);
		mVideoPreview.setImageResource(resource);
	}

	public void setImageViewScaleType(ScaleType scaleType) {
		mVideoPreview.setScaleType(scaleType);
	}

	public void setVideoPauseAndShowPreview() {
		if (mVideoPreview.getVisibility() == View.GONE)
			mVideoPreview.setVisibility(View.VISIBLE);

		// if (mSurfaceView.getVisibility() == View.VISIBLE)
		// // mSurfaceView.setVisibility(View.GONE);
		pause();
	}

	public void setVideo(Uri videoUrl, Uri previewUrl) {
		mRetryImageUri = previewUrl;
		mRetryVideoUri = videoUrl;
		Log.i("vedio", "mRetryImageUri=" + mRetryImageUri.toString());
		Log.i("vedio", "mRetryVideoUri=" + mRetryVideoUri.toString());
		checkAndDownloadToCache(previewUrl, videoUrl);
	}

	private void handleOnlyImageDownload(Uri previewUrl) {
		String outputPath = cacheFile.getAbsolutePath().toString() + "/"
				+ convertUrlToFileName(previewUrl.toString());
		File file = new File(outputPath);
		if (file.exists()
				&& checkAndSetImageViewWithLocalPath(file.getAbsolutePath()) == true) {
			mPlayIconImageView.setVisibility(View.GONE);
			return;
		}

		downloadImageFile(previewUrl.toString(), outputPath);
	}

	// private void handeVideoExist(String vidoePath, String previewview) {
	// setupVideoPreviewWithLocalFile(previewview);
	// setupVideoViewWithLocaFile(vidoePath);
	// }

	private void handeVideoExist(String vidoePath, String priviewPath) {

		// BitmapFactory.Options bmpFactoryOptions = new
		// BitmapFactory.Options();
		// bmpFactoryOptions.inSampleSize = 8;
		//
		// Bitmap btm = BitmapFactory.decodeFile(priviewPath,bmpFactoryOptions);
		// mVideoPreview.setImageBitmap(btm);

//		ImageLoader.getInstance().displayImage("file://" + priviewPath,
//				mVideoPreview, Thinksns.getOptions());
		
		application.displayImage("file://" + priviewPath,mVideoPreview);
		
		// setupVideoPreviewWithLocalFile(vidoePath);
		setupVideoViewWithLocaFile(vidoePath);
	}

	private void handleVideoDownload(Uri previewUrl, Uri videoUrl) {
		String preivewOutputPath = cacheFile.getAbsolutePath().toString() + "/"
				+ convertUrlToFileName(previewUrl.toString());
		File previewFile = new File(preivewOutputPath);

		String videoOutputPathString = cacheFile.getAbsolutePath().toString()
				+ "/" + convertUrlToFileName(videoUrl.toString());
		File videoFile = new File(videoOutputPathString);
		mPlayFile = videoFile.getAbsolutePath();
		Log.d(TAG,
				"videoFile.exists()="
						+ videoFile.exists()
						+ ",previewFile.exists"
						+ (previewFile.exists() && checkAndSetImageViewWithLocalPath(previewFile
								.getAbsolutePath())));
		if (videoFile.exists()) {
			handeVideoExist(videoFile.getAbsolutePath(),
					previewFile.getAbsolutePath());
		} else if (previewFile.exists()
				&& checkAndSetImageViewWithLocalPath(previewFile
						.getAbsolutePath())) {
			downloadVideoFile(videoUrl.toString(), videoOutputPathString);
		} else {
			downloadImageAndVideoFile(previewUrl.toString(), preivewOutputPath,
					videoUrl.toString(), videoOutputPathString);
		}
	}

	private void checkAndDownloadToCache(Uri previewUrl, Uri videoUrl) {
		if (videoUrl == null && previewUrl != null) {
			// Log.i("nat", "only image");
			handleOnlyImageDownload(previewUrl);
		} else if (videoUrl != null && previewUrl != null) {
			// Log.i("nat", "videor("+videoUrl+") and image("+previewUrl+")");
			handleVideoDownload(previewUrl, videoUrl);
		} else {
			throw new IllegalArgumentException(
					"VideoWithPlayButtonView preivewurl cant NOT be null");
		}
	}

	private String mDownloadImageAndVideoFile_VideoUrl;
	private String mDownloadImageAndVideoFile_VideoSavedPath;
	private HttpDownloaderCallback downloadImageAndVideoFileCallback = new HttpDownloaderCallback() {

		@Override
		public void onProgressUpdate(float progress) {
		}

		@Override
		public void onDownloadSuccessed(String savedPath) {
			mImageDownloadTask = null;
			checkAndSetImageViewWithLocalPath(savedPath);

			downloadVideoFile(mDownloadImageAndVideoFile_VideoUrl,
					mDownloadImageAndVideoFile_VideoSavedPath);
		}

		@Override
		public void onDownloadFailed(String errorReason) {
			mImageDownloadTask = null;
			Log.i("nat", "download image and video fail -> " + errorReason);

		}
	};

	private void downloadImageAndVideoFile(String previewUrl,
			final String preivewSavedPath, final String videoUrl,
			final String videoSavedPath) {
		if (isAutoPlay)
			mPlayIconImageView.setVisibility(View.GONE);
		mImageDownloadTask = new HttpDownloader(getContext(), previewUrl,
				preivewSavedPath, downloadImageAndVideoFileCallback);
		mImageDownloadTask.start();
		mDownloadImageAndVideoFile_VideoUrl = videoUrl;
		mDownloadImageAndVideoFile_VideoSavedPath = videoSavedPath;
		/*
		 * mImageDownloadTask = new DownloadFileAsyncTask(getContext(),
		 * preivewSavedPath, downloadImageAndVideoFileCallback);
		 * mImageDownloadTask.execute(previewUrl);
		 */

	}

	private HttpDownloaderCallback downloadImageFileCallback = new HttpDownloaderCallback() {
		@Override
		public void onProgressUpdate(float progress) {
		}

		@Override
		public void onDownloadSuccessed(String savedPath) {
			mImageDownloadTask = null;

			if (checkAndSetImageViewWithLocalPath(savedPath) == false) {
				mRetryButton.setVisibility(View.VISIBLE);
			}
			start();
		}

		@Override
		public void onDownloadFailed(String errorReason) {
			mImageDownloadTask = null;
			Log.i("nat", "download image fail -> " + errorReason);
		}
	};

	private void downloadImageFile(String fromUrl, final String toFilePath) {
		if (isAutoPlay)
			mPlayIconImageView.setVisibility(View.GONE);
		mImageDownloadTask = new HttpDownloader(getContext(), fromUrl,
				toFilePath, downloadImageFileCallback);
		mImageDownloadTask.start();

		/*
		 * mImageDownloadTask = new DownloadFileAsyncTask(getContext(),
		 * toFilePath, downloadImageFileCallback);
		 * mImageDownloadTask.execute(fromUrl);
		 */
	}

	private HttpDownloaderCallback downloadVideoFileCallback = new HttpDownloaderCallback() {

		@Override
		public void onProgressUpdate(final float progress) {
			mhandler.post(new Runnable() {

				@Override
				public void run() {
					mRoundProgressBar.setProgress((int) (progress * 100));
				}
			});
		}

		@Override
		public void onDownloadSuccessed(final String savedPath) {
			mVideoDownloadTask = null;
			mRoundProgressBar.setVisibility(View.GONE);
			setupVideoViewWithLocaFile(savedPath);
			// if (!isAutoPlay) {
			// mIsStarted = true;
			// play();
			// }
			// if (mInitialPlay) {
			// // play();
			// }
		}

		@Override
		public void onDownloadFailed(String errorReason) {
			mVideoDownloadTask = null;
			Log.i("nat", "download video fail -> " + errorReason);
		}
	};

	private void downloadVideoFile(String fromUrl, String toFilePath) {

		mVideoDownloadTask = new HttpDownloader(getContext(), fromUrl,
				toFilePath, downloadVideoFileCallback);
		if (isAutoPlay) {
			if (mRoundProgressBar.getVisibility() == View.GONE)
				mRoundProgressBar.setVisibility(View.VISIBLE);
			mPlayIconImageView.setVisibility(View.GONE);
			if (!taskStarted)
				mVideoDownloadTask.start();
		}

		/*
		 * mVideoDownloadTask = new DownloadFileAsyncTask(getContext(),
		 * toFilePath, downloadVideoFileCallback);
		 * mVideoDownloadTask.execute(fromUrl);
		 */

	}

	/**
	 * 把视频所在服务器地址映射成本地图片地址，需要把特殊字符处理掉
	 * 
	 * @param url
	 * @return
	 */
	private String convertUrlToFileName(String url) {
		String filePath = url.replace(".", "");
		filePath = filePath.replace(":", "");
		filePath = filePath.replace("/", "");
		filePath = filePath.replace("-", "");
		filePath = filePath.replace("=", "");
		filePath = filePath.replace("?", "");
		filePath = filePath.replace("&", "");
		filePath = filePath.replace("_", "");
		return filePath;
	}

	private boolean checkAndSetImageViewWithLocalPath(String localPath) {
//		myBitmap = BitmapFactory.decodeFile(localPath,Thinksns.getOpts());
//		if (myBitmap == null) {
//			File file = new File(localPath);
//			if (file.exists())
//				file.delete();
		
//		ImageLoader.getInstance().displayImage("file://" + localPath,
//				mVideoPreview, Thinksns.getOptions());
		
		application.displayImage("file://" + localPath,mVideoPreview);
		
		// mVideoPreview.setImageBitmap(myBitmap);
		mVideoPreview.setVisibility(View.VISIBLE);
		return true;
	}

	private void setupVideoPreviewWithLocalFile(String localPath) {
		// Bitmap thubmnail = ThumbnailUtils.createVideoThumbnail(localPath,
		// // MediaStore.Images.Thumbnails.MINI_KIND);
		// mVideoPreview.setImageBitmap(thubmnail);
		checkAndSetImageViewWithLocalPath(localPath);
	}

	private void setupVideoViewWithLocaFile(final String localPath) {
		if (isAutoPlay)
			mPlayIconImageView.setVisibility(View.GONE);
		try {
			// 设置MediaPlayer将要播放的视频
			mMediaPlayer.setDataSource(localPath);
			mPlayFile = localPath;
			if (mSurface != null) {
				mMediaPlayer.setSurface(mSurface);
				try {
					// mMediaPlayer.setDataSource(mPlayFile);
					mMediaPlayer.setSurface(mSurface);
					mMediaPlayer.prepareAsync();
					mIsStarted = true;
					isAutoPlay = true;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
				// catch (IOException e) {
				// e.printStackTrace();
				// mMediaPlayer.prepareAsync();
				// mIsStarted = true;
				//
				// }
				catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean taskStarted = false;

	private void setupButtonCliked() {
		if (!isThirdHost) {// 判断是否来自第三方，如果不是，则需要添加点击播放，否则添加点击跳转到第三方网站
//			mVideoPreview.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					if (mMediaPlayer.isPlaying()) {
//						mIsStopByUser = true;
//						pause();
//						muteAudioFocus(mContext, false);
//					} else {
//						mIsStopByOthers = false;
//						Intent intent = new Intent(StaticInApp.STOPVIDEOINTENT);
//						mIsStopVideo = true;
//						mContext.sendBroadcast(intent);
//						start();
//						muteAudioFocus(mContext, true);
//					}
//				}
//			});
//			
//			
//			mTextureView.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					if (mMediaPlayer.isPlaying()) {
//						mIsStopByUser = true;
//						pause();
//						muteAudioFocus(mContext, false);
//					} else {
//						mIsStopByOthers = false;
//						if (mVideoDownloadTask != null && !taskStarted) {
//							// mPlayIconImageView.setVisibility(View.GONE);
//							mVideoDownloadTask.start();
//							taskStarted = true;
//							if (mRoundProgressBar.getVisibility() == View.GONE)
//								mRoundProgressBar.setVisibility(View.VISIBLE);
//							mPlayIconImageView.setVisibility(View.GONE);
//						} else {
//							start();
//							muteAudioFocus(mContext, true);
//						}
//					}
//				}
//			});
//			mPlayIconImageView.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					if (mMediaPlayer.isPlaying()) {
//						mIsStopByUser = true;
//						pause();
//					} else {
//						mIsStopByOthers = false;
//						Intent intent = new Intent(StaticInApp.STOPVIDEOINTENT);
//						mIsStopVideo = true;
//						mContext.sendBroadcast(intent);
//						if (mVideoDownloadTask != null
//								&& mPlayIconImageView.getVisibility() != View.GONE
//								&& !taskStarted) {
//							mVideoDownloadTask.start();
//							taskStarted = true;
//							if (mRoundProgressBar.getVisibility() == View.GONE)
//								mRoundProgressBar.setVisibility(View.VISIBLE);
//							mPlayIconImageView.setVisibility(View.GONE);
//						} else if (mVideoDownloadTask == null) {
//							start();
//						}
//					}
//				}
//			});
			mPlayButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					if (mMediaPlayer.isPlaying()) {
//						mIsStopByUser = true;
//						pause();
//					} else {
//						mIsStopByOthers = false;
//						Intent intent = new Intent(StaticInApp.STOPVIDEOINTENT);
//						mIsStopVideo = true;
//						mContext.sendBroadcast(intent);
//						start();
//					}
					// onPauseOrPalyClicked();
					
					Intent intent=new Intent(getContext(),NetActivity.class);
					intent.putExtra("url",mRetryVideoUri.toString());
					getContext().startActivity(intent);
				}
			});

//			mRetryButton.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					v.setVisibility(View.GONE);
//					checkAndDownloadToCache(mRetryImageUri, mRetryVideoUri);
//				}
//			});
		} else {

			OnClickListener clickToWeb = new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 跳转到相应的浏览器页面
//					Intent it = new Intent(Intent.ACTION_VIEW, mRetryVideoUri);
//					getContext().startActivity(it);
					
					Intent intent=new Intent(getContext(),NetActivity.class);
					intent.putExtra("url",mRetryVideoUri.toString());
					getContext().startActivity(intent);
				}
			};

			mVideoPreview.setOnClickListener(clickToWeb);
			mTextureView.setOnClickListener(clickToWeb);
			mPlayButton.setOnClickListener(clickToWeb);
			mRetryButton.setOnClickListener(clickToWeb);
			mPlayIconImageView.setOnClickListener(clickToWeb);
		}
	}

	public void pause() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			mStopPosition = mMediaPlayer.getCurrentPosition();
			// if (isAutoPlay) {
			// mPlayIconImageView.setVisibility(View.GONE);
			// } else {
			// mPlayIconImageView.setVisibility(View.VISIBLE);
			// }
			mPlayIconImageView.setVisibility(View.VISIBLE);
			mPlayButton.setVisibility(View.VISIBLE);
			mTextureView.setVisibility(View.GONE);
			// synchronized (WeiboAppActivity.LOCK_PLAYVIDEO) {

			// }
		}
		// caoligai 增加，点击播放视频后关闭背景音乐
		muteAudioFocus(mContext, false);
	}

	public void play() {
		// caoligai 增加，开始播放监听器，用于控制同一页面同一时刻只有一个视频正在播放
		if (null != BeginPlayListener) {
			BeginPlayListener.onBeginPlay();
		}
		if (mIsStopByOthers || mMediaPlayer == null || mTextureView == null) {
			pause();
			return;
		}
		mIsStarted = true;
		if (mTextureView.getVisibility() == View.GONE)
			mTextureView.setVisibility(View.VISIBLE);
		// mSurfaceView.bringToFront();
		if (mStopPosition != 0) {
			mMediaPlayer.seekTo(mStopPosition);
			mStopPosition = 0;
		}
		mMediaPlayer.start();
		mPlayButton.setVisibility(View.GONE);
		// caoligai 增加，点击播放视频后关闭背景音乐
		muteAudioFocus(mContext, true);
	}

	private void playVideoError(String localPath) {
		Log.i("nat", "PlayVideo Error");

		mRetryButton.setVisibility(View.VISIBLE);
		mVideoPreview.setVisibility(View.VISIBLE);

		File file = new File(localPath);
		if (file.exists())
			file.delete();
	}

	public View getTouchableView() {
		return mVideoPreview;
	}

	public void shouldPlay() {
		if (mTextureView != null && mInitialPlay == false) {
			// play();
		}
		mInitialPlay = true;
	}

	public void stop() {
		if (mTextureView != null) {
			mMediaPlayer.pause();
			mTextureView = null;
			mMediaPlayer.release();
		}

		if (mVideoDownloadTask != null) {
			mVideoDownloadTask.cancel(true);
			mVideoDownloadTask = null;
		}

		if (mImageDownloadTask != null) {
			mImageDownloadTask.cancel(true);
			mImageDownloadTask = null;
		}
	}

	public void setTitle(String titString) {
	}

	public void reset() {
		mVideoPreview.setVisibility(View.VISIBLE);
		if (mTextureView != null) {
			mTextureView.setVisibility(View.GONE);
			pause();
		} else {
			Log.e("nat", "mVideoView not initial yet");
		}
	}

	public interface VideoStopCallBack {
		public void startCallBack();

		public void stopCallBack();
	}

	// 来自于MediaPlayer.OnVideoSizeChangedListener接口
	// 当视频的宽度或高度发生变化时调用该方法
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		Log.d(TAG, "height=" + height);
	}

	// 来自于MediaPlayer.OnSeekCompleteListener接口
	public void onSeekComplete(MediaPlayer mp) {
	}

	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		if (isAutoPlay)
			mMediaPlayer.start();

	}

	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING) {
			// 音频和视频数据不正确地交错时将出现该提示信息.在一个
			// 正确交错的媒体文件中,音频和视频样本将依序排列,从而
			// 使得播放可以有效和平稳地进行
		}
		if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
			// 当媒体不能正确定位时将出现该提示信息.
			// 此时意味着它可能是一个在线流
		}
		if (what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
			// 当设备无法播放视频时将出现该提示信息
			// 比如视频太复杂或者码率过高
		}
		if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
			// 当新的元数据可用时将出现该提示信息
		}
		if (what == MediaPlayer.MEDIA_INFO_UNKNOWN) {
			// 其余不可知提示信息
		}
		return false;
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
			System.err.println("第一种错误");
		}
		if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
			System.err.println("第二种错误");
		}
		if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
			System.err.println("第三种错误");
		}
		return false;
	}

	public void onCompletion(MediaPlayer mp) {
		// finish();
		int[] location = new int[2];
		mTextureView.getLocationInWindow(location);
		// synchronized (WeiboAppActivity.LOCK_PLAYVIDEO) {
		// }
		play();
	}

	// 以下方法均来自MediaPlayerControl接口
	public void start() {
		play();
	}

	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	public int getCurrentPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	public void seekTo(int pos) {
		mMediaPlayer.seekTo(pos);
	}

	public boolean isPlaying() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.isPlaying();
		}
		return false;
	}

	public int getBufferPercentage() {
		return 0;
	}

	public boolean canPause() {
		return true;
	}

	public boolean canSeekBackward() {
		return true;
	}

	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		mSurface = new Surface(surface);
		if (mPlayFile != null) {
			mMediaPlayer.setSurface(mSurface);
			try {
				mMediaPlayer.prepareAsync();
				if (isAutoPlay) {
					// play();
					mIsStarted = true;
				}

			} catch (Exception e) {
			}
		}
		Log.d("xhs", "onSurfaceTextureAvailable");

	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		Log.d("xhs", "onSurfaceTextureDestroyed");
		if (mPlayFile != null)
			mPlayFile = null;
		if (mSurface != null)
			mSurface = null;
		if (myBitmap != null) {
			// myBitmap.recycle();
			myBitmap = null;
		}
		if (myBroadCast != null) {
			mContext.unregisterReceiver(myBroadCast);
			myBroadCast = null;
		}
		mIsStarted = false;
		// synchronized (WeiboAppActivity.LOCK_PLAYVIDEO) {

		// }
		if (mMediaPlayer != null) {
			pause();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		if (surface != null) {
			surface.release();
		}
		return false;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
			int height) {
		Log.d("xhs", "onSurfaceTextureSizeChanged");
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d(TAG, "onSizeChanged" + h);
	}

	// @Override
	// public void onClick(View v) {
	// switch (v.getId())
	// {
	// case R.id.textureView_videos:
	// if (mMediaPlayer.isPlaying()) {
	// pause();
	// } else {
	// Intent intent = new Intent(StaticInApp.StopVideoIntent);
	// mIsStopVideo = true;
	// mContext.sendBroadcast(intent);
	// start();
	// }
	// break;
	// case R.id.play_video_image:
	// if (mMediaPlayer.isPlaying()) {
	// pause();
	// } else {
	// Intent intent = new Intent(StaticInApp.StopVideoIntent);
	// mIsStopVideo = true;
	// mContext.sendBroadcast(intent);
	// start();
	// }
	// break;
	// }
	//
	// }
	
	// caoligai 增加，点击播放视频后关闭背景音乐
	/**@param bMute 值为true时为关闭背景音乐。*/  
	public static boolean muteAudioFocus(Context context, boolean bMute) {  
	    if(context == null){  
	        Log.d("ANDROID_LAB", "context is null.");  
	        return false;  
	    }  
//	    if(!VersionUtils.isrFroyo()){  
//	        // 2.1以下的版本不支持下面的API：requestAudioFocus和abandonAudioFocus  
//	        Log.d("ANDROID_LAB", "Android 2.1 and below can not stop music");  
//	        return false;  
//	    }  
	    boolean bool = false;  
	    AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);  
	    if(bMute){  
	        int result = am.requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);  
	        bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;  
	    }else{  
	        int result = am.abandonAudioFocus(null);  
	        bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;  
	    }  
	    Log.d("ANDROID_LAB", "pauseMusic bMute="+bMute +" result="+bool);  
	    return bool;  
	}  
	
	// caoligai 增加，开始播放监听器，用于控制同一页面同一时刻只有一个视频正在播放
	public void setOnBeginPlayListener(onBeginPlayListener listener){
		this.BeginPlayListener = listener;
	}
	
	// caoligai 增加，开始播放监听器，用于控制同一页面同一时刻只有一个视频正在播放
	public interface onBeginPlayListener{
		void onBeginPlay();
	}
	
}
