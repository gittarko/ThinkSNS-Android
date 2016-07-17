package com.thinksns.sociax.t4.android.gift;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.adapter.AdapterMyScore;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.component.ListScore;
import com.thinksns.sociax.t4.component.ListSociax;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

import org.json.JSONObject;

/**
 * 类说明：我的积分
 * 
 * @author Zoey
 * @date 2015年9月21日
 * @version 1.0
 */
public class ActivityMyScore extends ThinksnsAbscractActivity {

	private ListSociax listView;
	private ListData<SociaxItem> list;
	private AdapterSociaxList adapter;
	private RelativeLayout rl_score_detail;
	private TextView tv_my_score,tv_score_rule;
	private Button btn_exchange_now;
	private ImageButton tv_title_left;
	private LinearLayout ll_top_up,ll_transfer;
	private UIHandler uiHandler=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		
		initIntentData();
		initView();
		initListener();
		initData();
	}
	
	private void initIntentData() {
		
	}

	@Override
	public OnTouchListListener getListView() {
		return listView;
	}
	
	private void initView() {
		
		rl_score_detail=(RelativeLayout)findViewById(R.id.rl_score_detail);
		tv_my_score=(TextView)findViewById(R.id.tv_my_score);
		tv_score_rule=(TextView)findViewById(R.id.tv_score_rule);
		tv_title_left= (ImageButton) findViewById(R.id.tv_title_left);
		btn_exchange_now=(Button)findViewById(R.id.btn_exchange_now);
		ll_top_up=(LinearLayout)findViewById(R.id.ll_top_up);
		ll_transfer=(LinearLayout)findViewById(R.id.ll_transfer);
		
		listView=(ListScore)findViewById(R.id.lv_my_score_detail);
		list = new ListData<SociaxItem>();
		adapter = new AdapterMyScore(this, list,4);
		listView.setAdapter(adapter);
		adapter.loadInitData();
		
		uiHandler=new UIHandler();
		
		IntentFilter filter_del_room = new IntentFilter();
		filter_del_room.addAction(StaticInApp.UPDATE_SCORE_DETAIL);
		this.registerReceiver(broad_update_score, filter_del_room);
		
		getMyScoreTask();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(broad_update_score); 
		super.onDestroy();
	}
	
	protected BroadcastReceiver broad_update_score = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(StaticInApp.UPDATE_SCORE_DETAIL)) {
				adapter.doUpdataList();
				getMyScoreTask();
			}
		}
	};
	
	private void initListener() {
		rl_score_detail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ActivityMyScore.this,ActivityScoreDetail.class);
				startActivity(intent);
			}
		});
		tv_score_rule.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ActivityMyScore.this,ActivityScoreRule.class);
				startActivity(intent);
			}
		});
		btn_exchange_now.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ActivityMyScore.this,ActivityScoreShop.class);
				startActivity(intent);
			}
		});
		ll_top_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ActivityMyScore.this,ActivityScoreTopUp.class);
				startActivity(intent);
			}
		});
		ll_transfer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ActivityMyScore.this,ActivityScoreTransfer.class);
				startActivity(intent);
			}
		});
		tv_title_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}	

	private void initData() {
	
	}
	
	@Override
	public String getTitleCenter() {
		return null;
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_my_score;
	}
	
	class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == StaticInApp.GET_MY_SCORE) {
				try {
					JSONObject result = new JSONObject(msg.obj.toString());
					tv_my_score.setText(result.getString("score"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void getMyScoreTask() {
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
}
