package com.thinksns.sociax.android;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.RightIsButton;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.unit.AsyncImageLoader;
import com.thinksns.sociax.unit.ImageUtil;
import com.thinksns.sociax.unit.ImageZoomView;
import com.thinksns.sociax.unit.SimpleZoomListener;
import com.thinksns.sociax.unit.ZoomState;
import com.thinksns.sociax.android.R;

public class ThinksnsImageView extends ThinksnsAbscractActivity {
	private ImageZoomView imageZoomView;
	private ZoomControls zoomCtrl;
	private ZoomState mZoomState;
	private Button saveButton;
	private static Bitmap bitmap;
	private SimpleZoomListener mZoomListener;
	private String url;
	private static LoadingView loadingView;

	private TextView leftText, rightText;
	private Drawable drawable = null;
	private ResultHandler resultHandler;
	private LinearLayout lyLoading;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		resultHandler = new ResultHandler();
		lyLoading = (LinearLayout) findViewById(R.id.ly_loading);
		// leftText = (TextView) findViewById(R.id.group_flip);
		// rightText = (TextView) findViewById(R.id.group_right_btn);
		imageZoomView = (ImageZoomView) findViewById(R.id.image_data);
		zoomCtrl = (ZoomControls) findViewById(R.id.zoomCtrl);

		url = getIntentData().getString("url");
		getDrawable();
		zoomCtrl.setVisibility(View.GONE);
		// resetZoomState();
	}

	private void resetZoomState() {
		mZoomState.setPanX(0.5f);
		mZoomState.setPanY(0.5f);

		final int mWidth = bitmap.getWidth();
		final int vWidth = imageZoomView.getWidth();
		mZoomState.setZoom(1f);
		mZoomState.notifyObservers();

	}

	private void setFullScreen() {
		if (zoomCtrl != null) {
			if (zoomCtrl.getVisibility() == View.VISIBLE) {
				// zoomCtrl.setVisibility(View.GONE);
				zoomCtrl.hide(); // 有过度效果
			} else if (zoomCtrl.getVisibility() == View.GONE) {
				// zoomCtrl.setVisibility(View.VISIBLE);
				zoomCtrl.show();// 有过渡效果
			}
		}
	}

	private void getDrawable() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				drawable = AsyncImageLoader.loadImageFromUrl(url);
				Message msg = resultHandler.obtainMessage();
				msg.what = 1;
				lyLoading.setVisibility(View.VISIBLE);
				msg.sendToTarget();
			}
		}).start();
	}

	private class ResultHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			lyLoading.setVisibility(View.GONE);
			if (msg.what == 1) {
				if (drawable == null) {
					Toast.makeText(ThinksnsImageView.this,
							R.string.wc_itme_img_error, Toast.LENGTH_LONG)
							.show();
					return;
				}

				imageZoomView.setVisibility(View.VISIBLE);

				bitmap = drawableToBitmap(drawable);
				mZoomState = new ZoomState();
				mZoomListener = new SimpleZoomListener();
				mZoomListener.setZoomState(mZoomState);
				imageZoomView.setImage(bitmap);
				imageZoomView.setZoomState(mZoomState);
				imageZoomView.setOnTouchListener(mZoomListener);
				setZoomCtrls();
				imageZoomView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						setFullScreen();
					}

				});

			}
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.imageshow;
	}

	@Override
	public String getTitleCenter() {
		return this.getString(R.string.imageshow);
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new RightIsButton(this, this.getString(R.string.imagesave));
	}

	@Override
	public int getRightRes() {
		return R.drawable.button_send;
	}

	@Override
	public OnClickListener getRightListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ImageUtil iu = new ImageUtil();
				String[] urlName = null;
				boolean result = false;
				urlName = url.split("/");
				try {
					result = iu.saveImage(urlName[urlName.length - 1], bitmap);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				if (result) {
					if (ImageUtil.getSDPath() == null) {
						Toast.makeText(ThinksnsImageView.this, "保存失败,没有获取到SD卡",
								Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(ThinksnsImageView.this,
							"保存成功, 目录" + ImageUtil.getSDPath() + "/tsimage",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ThinksnsImageView.this, "保存失败",
							Toast.LENGTH_SHORT).show();
				}
			}

		};
	}

	private void setZoomCtrls() {
		zoomCtrl.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				float z = mZoomState.getZoom() + 0.25f;
				mZoomState.setZoom(z);
				mZoomState.notifyObservers();
			}
		});
		zoomCtrl.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float z = mZoomState.getZoom() - 0.25f;
				mZoomState.setZoom(z);
				mZoomState.notifyObservers();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null)
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	@Override
	public boolean isInTab() {
		return false;
	}

}
