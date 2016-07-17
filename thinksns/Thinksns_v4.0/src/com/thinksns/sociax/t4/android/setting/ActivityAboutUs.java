package com.thinksns.sociax.t4.android.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.weibo.NetActivity;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author povol
 * @date Mar 22, 2013
 * @version 1.0
 */
public class ActivityAboutUs extends ThinksnsAbscractActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		findViewById(R.id.ll_concern).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse("http://www.thinksns.com");
				intent.setData(content_url);
				startActivity(intent);
			}
		});

		findViewById(R.id.ll_tel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("tel:01082431402");
				Intent it = new Intent(Intent.ACTION_DIAL, uri);
				startActivity(it);
			}
		});

		findViewById(R.id.newsfeed_flip).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
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
		return R.layout.about_us;
	}
}
