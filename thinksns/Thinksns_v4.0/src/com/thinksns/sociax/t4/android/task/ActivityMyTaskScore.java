package com.thinksns.sociax.t4.android.task;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.gift.ActivityScoreShop;
import com.thinksns.sociax.t4.android.temp.RechargeActivity;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;

/**
 * 类说明：
 * 
 * @author Administrator
 * @date 2014-11-10
 * @version 1.0
 */
public class ActivityMyTaskScore extends ThinksnsAbscractActivity {
	LinearLayout ll_content, ll_task_center, ll_recharge;
	TextView tv_myscore, tv_exchange;
	LoadingView loadingview;
	private UIHandler uiHandler;// 处理ui线程

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initIntentData();
		initView();
		initListener();
		initData();
	}

	private void initData() {
		getMyScoreTask();
	}

	private void getMyScoreTask() {
		loadingview.show(ll_content);
		new Thread(new Runnable() {

			@Override
			public void run() {
				Thinksns app = (Thinksns) getApplication();
				Message msg = new Message();
				msg.what = StaticInApp.GET_MY_SCORE;
				msg.obj = app.getUsers().getMyCredit();
				uiHandler.sendMessage(msg);
			}
		}).start();
	}

	private void initListener() {
		ll_task_center.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				  Intent intent = new Intent(ActivityMyTaskScore.this,ActivityTaskCenter.class); 
				  startActivity(intent);
			}
		});
		ll_recharge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityMyTaskScore.this,RechargeActivity.class);
				ActivityMyTaskScore.this.startActivity(intent);
			};
		});
		tv_exchange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityMyTaskScore.this,ActivityScoreShop.class);
				ActivityMyTaskScore.this.startActivity(intent);
			}
		});
	}

	private void initView() {

		ll_recharge = (LinearLayout) findViewById(R.id.ll_recharge);
		ll_task_center = (LinearLayout) findViewById(R.id.ll_task_center);
		ll_content = (LinearLayout) findViewById(R.id.ll_content);

		tv_myscore = (TextView) findViewById(R.id.tv_myscore);
		tv_exchange = (TextView) findViewById(R.id.tv_exchange);

		uiHandler = new UIHandler();
		loadingview = (LoadingView) findViewById(LoadingView.ID);
	}

	private void initIntentData() {

	}

	@Override
	public String getTitleCenter() {

		return "我的积分";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_myscore;
	}

	class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == StaticInApp.GET_MY_SCORE) {
				try {
					JSONObject result = new JSONObject(msg.obj.toString());
					tv_myscore.setText("我的积分：" + result.getString("score"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				loadingview.hide(ll_content);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getMyScoreTask();
	}
}
