package com.thinksns.sociax.t4.android.video;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.utils.TimeHelper;

/**
 * 视频播放详情页面
 * 需要传入intent String  url;视频地址
 * @author wz 
 *
 */
public class ActivityVideoDetail extends ThinksnsAbscractActivity implements
		OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
		MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener,
		SurfaceHolder.Callback {

	protected static final int ADD_DIGG = 3;
	protected static final int DEL_DIGG = 4;
	protected static final int HIDE_PLAY = 5;
	private MediaPlayer mediaPlayer;
	private SurfaceView surface;
	private SurfaceHolder surfaceHolder;
	private SeekBar skbProgress;

	private TextView tvPostion, tvDuraction;
	private LinearLayout llSeekBar, lyStartPause;
	/***预览图片控件***/
	private ImageView preview;

	private Timer mTimer = new Timer();
	// private ImageView ivRight, ivImgFlag;
	private ImageView ivImgFlag;

	/**视频播放地址，视频预览图地址***/
	private String url , previewUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		url= getIntentData().getString("url");
		if(url==null){
			Toast.makeText(getApplicationContext(), "视频读取错误", Toast.LENGTH_SHORT).show();
		}
		previewUrl = getIntent().getStringExtra("preview_url");

		findViewById(R.id.grid_left_img).setOnClickListener(getLeftListener());
		
		surface = (SurfaceView) findViewById(R.id.surface);
		skbProgress = (SeekBar) findViewById(R.id.skbProgress);
		tvPostion = (TextView) findViewById(R.id.tv_position);
		tvDuraction = (TextView) findViewById(R.id.tv_duration);
		llSeekBar = (LinearLayout) findViewById(R.id.ll_seekbar);
		ivImgFlag = (ImageView) findViewById(R.id.iv_imgflag);
		preview = (ImageView)findViewById(R.id.preview);

		/***显示视频预览图***/
		Glide.with(this).load(previewUrl).into(preview);

		llSeekBar.setVisibility(View.GONE);
		ivImgFlag.setVisibility(View.GONE);

		mediaPlayer = new MediaPlayer();
		surfaceHolder = surface.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		Button btn = (Button) findViewById(R.id.btn_max);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse(getIntentData().getString("url"));
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(uri, "video/mp4");
				startActivity(intent);
			}
		});
		surface.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.pause();
						ivImgFlag.setVisibility(View.VISIBLE);
						ivImgFlag.setImageResource(R.drawable.bofang);
					} else {
						mediaPlayer.start();
						ivImgFlag.setVisibility(View.VISIBLE);
						ivImgFlag.setImageResource(R.drawable.bofang);
						handleDoDigg.sendEmptyMessage(HIDE_PLAY);
					}
				}
			}
		});

		skbProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			int progress;

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mediaPlayer.seekTo(progress);
				if (!mediaPlayer.isPlaying()) {
					ivImgFlag.setVisibility(View.GONE);
					mediaPlayer.start();
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				this.progress = progress * mediaPlayer.getDuration()
						/ seekBar.getMax();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		mTimerTask.cancel();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		mediaPlayer.release();
	}

	@Override
	public String getTitleCenter() {
		return null;
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return null;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.video;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		mediaPlayer.setDisplay(surfaceHolder);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnCompletionListener(this);
		try {
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
			System.err.println("surface created error" + e.toString());
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		// skbProgress.setSecondaryProgress(bufferingProgress);
		int currentProgress = skbProgress.getMax()
				* mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		Log.e(currentProgress + "% play", bufferingProgress + "% buffer");
	}

	private boolean isPlaying;

	@Override
	public void onPrepared(MediaPlayer mp) {
		try {
			View v = findViewById(R.id.ly_loading);
			v.setVisibility(View.GONE);
			preview.setVisibility(View.GONE);
			llSeekBar.setVisibility(View.VISIBLE);

			int videoWidth = mediaPlayer.getVideoWidth();
			int videoHeight = mediaPlayer.getVideoHeight();

			if (surface.getLayoutParams().height < 10) {
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				// dm.heightPixels;
				double bs = ((double) dm.widthPixels / (double) videoWidth);

				int heigth = (int) (bs * videoHeight);
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, heigth);
				lp.setMargins(8, 0, 8, 0);
				surface.setLayoutParams(lp);
			}

			if (videoHeight != 0 && videoWidth != 0) {
				mp.start();
			}

			mTimer.schedule(mTimerTask, 0, 5);
			isPlaying = mediaPlayer.isPlaying();
		} catch (Exception e) {
			System.err.println("onPrepared" + e.toString());
		}
	}

	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
				handleProgress.sendEmptyMessage(0);
			}
		}
	};

	int position;
	int duration;

	Handler handleProgress = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			position = mediaPlayer.getCurrentPosition();
			duration = mediaPlayer.getDuration();

			if (duration > 0) {

				long pos = skbProgress.getMax() * position / duration;
				skbProgress.setProgress((int) pos);

				tvPostion.setText(TimeHelper
						.getStandardTimeWithSen(position / 1000));
				tvDuraction.setText(TimeHelper
						.getStandardTimeWithSen(duration / 1000));
			}
		};
	};

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (isPlaying) {
			skbProgress.setProgress(skbProgress.getMax());
			tvPostion.setText(TimeHelper
					.getStandardTimeWithSen(duration / 1000));
			ivImgFlag.setImageResource(R.drawable.bofang);
			ivImgFlag.setVisibility(View.VISIBLE);
		}
	}

	Handler handleDoDigg = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HIDE_PLAY:
				ivImgFlag.setVisibility(View.GONE);
				break;
			}
		}

	};

}
