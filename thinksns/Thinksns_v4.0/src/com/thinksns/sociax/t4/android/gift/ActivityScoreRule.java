package com.thinksns.sociax.t4.android.gift;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.adapter.AdapterScoreRule;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;

import com.thinksns.sociax.t4.component.ListScoreRule;
import com.thinksns.sociax.t4.component.ListSociax;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/** 
 * 类说明：积分规则
 *    
 * @author  Zoey   
 * @date    2015年9月21日
 * @version 1.0
 */
public class ActivityScoreRule extends ThinksnsAbscractActivity {

	private ListSociax listView;
	private ListData<SociaxItem> list;
	private AdapterSociaxList adapter;
	private TextView tv_title_left;
	protected boolean isRefreshing = false;
	
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

	private void initView() {
		tv_title_left=(TextView)findViewById(R.id.tv_title_left);
		listView = (ListScoreRule) findViewById(R.id.lv_score_rule);
		list = new ListData<SociaxItem>();
		adapter = new AdapterScoreRule(this, list);
		listView.setAdapter(adapter);
		adapter.loadInitData();
	}
	
	private void initListener() {
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
	public OnTouchListListener getListView() {
		return listView;
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_score_rule;
	}
}
