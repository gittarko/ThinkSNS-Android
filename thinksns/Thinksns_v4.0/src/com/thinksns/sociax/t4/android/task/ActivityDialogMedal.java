package com.thinksns.sociax.t4.android.task;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.img.UIImageLoader;
import com.thinksns.sociax.t4.android.video.ToastUtils;
import com.thinksns.sociax.t4.unit.UnitSociax;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author Zoey
 * @date 2015年9月7日
 * @version 1.0
 */
public class ActivityDialogMedal extends Activity {
	
	private ImageButton x;
	private ImageView iv_show;
	private ProgressBar progressBar;

	private Thinksns application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_medal);
		
		application = (Thinksns) this.getApplicationContext();
		
		iv_show = (ImageView)this.findViewById(R.id.iv_show);
		x = (ImageButton)this.findViewById(R.id.x);
		progressBar = (ProgressBar)findViewById(R.id.progress);
		x.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Intent intent = getIntent();
		if (intent!=null) {
			String show = intent.getStringExtra("show");
			UIImageLoader.getImageLoader().displayImage(show, iv_show, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String s, View view) {
					progressBar.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String s, View view, FailReason failReason) {
					Toast.makeText(ActivityDialogMedal.this, "获取失败", 500).show();
					finish();
				}

				@Override
				public void onLoadingComplete(String s, View view, Bitmap bitmap) {
					iv_show.setImageBitmap(bitmap);
					iv_show.setVisibility(View.VISIBLE);
					x.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingCancelled(String s, View view) {
					finish();
				}
			});
		}
	}
}
