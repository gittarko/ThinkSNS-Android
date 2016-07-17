//package com.thinksns.sociax.t4.android.video;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnPreparedListener;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.widget.Toast;
//
//import com.thinksns.sociax.t4.android.video.VideoView.OnPlayStateListener;
//import com.yixia.camera.FFMpegUtils;
//import com.yixia.camera.model.MediaObject;
//import com.yixia.camera.model.MediaObject.MediaPart;
//import com.yixia.camera.util.DeviceUtils;
//import com.yixia.camera.util.Log;
//import com.yixia.videoeditor.adapter.UtilityAdapter;
//import com.thinksns.sociax.android.R;
//
//public class ImportVideoActivity extends BaseActivity implements
//		OnClickListener, OnPreparedListener, OnPlayStateListener {
//
//	/** 视频预览 */
//	private VideoView mVideoView;
//	/** 暂停图标 */
//	private View mRecordPlay;
//	/** 视频总进度条 */
//	private ProgressView mProgressView;
//
//	/** 视频信息 */
//	private MediaObject mMediaObject;
//	private MediaPart mMediaPart;
//	/** 窗体宽度 */
//	private int mWindowWidth;
//	private String mVideoPath;
//
//	@SuppressLint("NewApi")
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 防止锁屏
//
//		String obj = getIntent().getStringExtra("obj");
//		mVideoPath = getIntent().getStringExtra("path");
//		mMediaObject = restoneMediaObject(obj);
//		if (mMediaObject == null) {
//			Toast.makeText(this, R.string.record_read_object_faild,
//					Toast.LENGTH_SHORT).show();
//			finish();
//			return;
//		}
//
//		mWindowWidth = DeviceUtils.getScreenWidth(this);
//		setContentView(R.layout.activity_import_video);
//
//		// ~~~ 绑定控件
//		mVideoView = (VideoView) findViewById(R.id.record_preview);
//		mRecordPlay = findViewById(R.id.record_play);
//		mProgressView = (ProgressView) findViewById(R.id.record_progress);
//
//		// ~~~ 绑定事件
//		mVideoView.setOnClickListener(this);
//		mVideoView.setOnPreparedListener(this);
//		mVideoView.setOnPlayStateListener(this);
//		findViewById(R.id.title_left).setOnClickListener(this);
//		findViewById(R.id.title_right).setOnClickListener(this);
//
//		findViewById(R.id.record_layout).getLayoutParams().height = mWindowWidth;
//		mVideoView.setVideoPath(mVideoPath);
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.title_left:
//			finish();
//			break;
//		case R.id.title_right:
//			startEncoding();
//			break;
//		}
//	}
//
//	/** 开始转码 */
//	private void startEncoding() {
//		// 检测磁盘空间
//		// if (FileUtils.showFileAvailable() < 200) {
//		// Toast.makeText(this, R.string.record_camera_check_available_faild,
//		// Toast.LENGTH_SHORT).show();
//		// return;
//		// }
//
//		if (!isFinishing() && mMediaObject != null && mMediaPart != null) {
//			new AsyncTask<Void, Void, Boolean>() {
//
//				@Override
//				protected void onPreExecute() {
//					super.onPreExecute();
//					showProgress("",
//							getString(R.string.record_camera_progress_message));
//				}
//
//				@Override
//				protected Boolean doInBackground(Void... params) {
//					// ffmpeg -i a.mov -vf scale=480:480 -ss 00:00:00 -t
//					// 00:00:10 -acodec aac -vcodec h264 -strict -2 out.mp4
//					long start = System.currentTimeMillis();
//					// $ ffmpeg -i a.mov -ss 00:00:21 -t 00:00:10 -acodec aac
//					// -vcodec h264 -strict -2 out.mp4
//					// String cmd =
//					// String.format("ffmpeg %s -i \"%s\"  -ss 00:00:00 -t 00:00:08 -acodec aac -vcodec h264 -strict -2 \"%s\"",
//					// FFMpegUtils.getLogCommand(), mVideoPath,
//					// mMediaObject.getOutputTempVideoPath());
//					// String cmd =
//					// String.format("ffmpeg %s -i \"%s\"  -ss 00:00:00 -t 00:00:08 -vcodec copy -acodec copy \"%s\"",
//					// FFMpegUtils.getLogCommand(), mVideoPath,
//					// mMediaObject.getOutputTempVideoPath());
//					// ffmpeg -ss 00:00:00 -t 00:00:03 -y -i test.mp4 -vcodec
//					// copy -acodec copy test1.mp4
//					// String cmd =
//					// String.format("ffmpeg %s -i \"%s\" -vf scale=480:480 -ss 00:00:00 -t 00:00:08 -acodec aac -vcodec h264 -strict -2 \"%s\"",
//					// FFMpegUtils.getLogCommand(), mVideoPath,
//					// mMediaObject.getOutputTempVideoPath());
//					// UtilityAdapter.FFmpegRun("", cmd);// == 0;
//					// mHandler.sendEmptyMessage(HANDLER_ENCODING_START);
//					// mMediaPart.tempPath =
//					// mMediaObject.getOutputTempVideoPath();
//					start = System.currentTimeMillis();
//					boolean importVideo = FFMpegUtils.importVideo(mMediaPart,
//							mWindowWidth, mVideoView.getVideoWidth(),
//							mVideoView.getVideoHeight(), 0, 0, true);
//					Logger.d("裁剪耗时:", (System.currentTimeMillis() - start) + "");
//					return importVideo;
//				}
//
//				@Override
//				protected void onPostExecute(Boolean result) {
//					super.onPostExecute(result);
//					hideProgress();
//					if (result) {
//						saveMediaObject(mMediaObject);
//						// setResult(Activity.RESULT_OK);
//						Intent intent = new Intent(ImportVideoActivity.this,
//								MediaPreviewActivity.class);
//						Bundle bundle = getIntent().getExtras();
//						if (bundle == null)
//							bundle = new Bundle();
//						bundle.putSerializable(
//								CommonIntentExtra.EXTRA_MEDIA_OBJECT,
//								mMediaObject);
//						bundle.putString("output",
//								mMediaObject.getOutputTempVideoPath());
//						intent.putExtras(bundle);
//						startActivity(intent);
//						finish();
//					} else {
//						Toast.makeText(ImportVideoActivity.this,
//								R.string.record_video_transcoding_faild,
//								Toast.LENGTH_SHORT).show();
//					}
//				}
//			}.execute();
//		}
//	}
//
//	/** 开始转码 */
//	private static final int HANDLER_ENCODING_START = 100;
//	/** 转码进度 */
//	private static final int HANDLER_ENCODING_PROGRESS = 101;
//	/** 转码结束 */
//	private static final int HANDLER_ENCODING_END = 102;
//
//	private Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case HANDLER_ENCODING_START:
//				if (!isFinishing()) {
//					showProgress("",
//							getString(R.string.record_preview_encoding));
//					sendEmptyMessage(HANDLER_ENCODING_PROGRESS);
//				}
//				break;
//			case HANDLER_ENCODING_PROGRESS:// 读取进度
//				int progress = UtilityAdapter
//						.FilterParserInfo(UtilityAdapter.FILTERINFO_PROGRESS);
//				if (mProgressDialog != null) {
//					mProgressDialog.setMessage(getString(
//							R.string.record_preview_encoding_format, progress));
//				}
//				if (progress < 100)
//					sendEmptyMessageDelayed(HANDLER_ENCODING_PROGRESS, 200);
//				else {
//					sendEmptyMessage(HANDLER_ENCODING_END);
//				}
//				break;
//			case HANDLER_ENCODING_END:
//				hideProgress();
//				break;
//			}
//			super.handleMessage(msg);
//		}
//	};
//
//	@Override
//	public void onStateChanged(boolean isPlaying) {
//		if (isPlaying)
//			mRecordPlay.setVisibility(View.GONE);
//		else
//			mRecordPlay.setVisibility(View.VISIBLE);
//	}
//
//	@Override
//	public void onPrepared(MediaPlayer mp) {
//		if (!isFinishing()) {
//			if (mVideoView.getVideoWidth() == 0
//					|| mVideoView.getVideoHeight() == 0) {
//				Toast.makeText(ImportVideoActivity.this,
//						R.string.record_camera_import_video_faild,
//						Toast.LENGTH_SHORT).show();
//				finish();
//				return;
//			}
//
//			mVideoView.start();
//			mVideoView.setLooping(true);
//
//			int duration = mMediaObject.getMaxDuration()
//					- mMediaObject.getDuration();
//			if (duration > mVideoView.getDuration())
//				duration = mVideoView.getDuration();
//
//			mMediaPart = mMediaObject.buildMediaPart(mVideoPath, duration,
//					MediaObject.MEDIA_PART_TYPE_IMPORT_VIDEO);
//			mProgressView.setData(mMediaObject);
//		}
//	}
//}
