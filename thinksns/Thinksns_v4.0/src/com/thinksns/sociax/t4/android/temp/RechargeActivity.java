package com.thinksns.sociax.t4.android.temp;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 充值
 * 
 * @author wz
 * @date 2014-9-4
 * @version 1.0
 */
public class RechargeActivity extends ThinksnsAbscractActivity {
	private Handler handler;
	private TextView tv_title_left;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		initIntentData();
		initView();
		initListener();
		initData();
	}

	/**
	 * 载入数据
	 */
	private void initData() {
		// TODO Auto-generated method stub
	}

	/**
	 * 初始化监事件
	 */
	private void initListener() {
		// TODO Auto-generated method stub
		tv_title_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	/**
	 * 初始化intent信息
	 */
	private void initIntentData() {
		// TODO Auto-generated method stub
	}

	/**
	 * 初始化页面
	 */
	private void initView() {
		tv_title_left = (TextView) findViewById(R.id.tv_title_left);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public String getTitleCenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CustomTitle setCustomTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_recharge;
	}
}
