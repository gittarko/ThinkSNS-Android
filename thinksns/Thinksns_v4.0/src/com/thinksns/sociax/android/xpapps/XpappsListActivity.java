package com.thinksns.sociax.android.xpapps;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
//import com.umeng.newxp.common.ExchangeConstants;
//import com.umeng.newxp.controller.ExchangeDataService;
//import com.umeng.newxp.view.ExchangeViewManager;
import com.thinksns.sociax.android.R;

/**
 * 类说明：
 * 
 * @author povol
 * @date Apr 11, 2013
 * @version 1.0
 */
public class XpappsListActivity extends ThinksnsAbscractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

//		ExchangeConstants.CONTAINER_AUTOEXPANDED = true;

		ViewGroup fatherLayout = (ViewGroup) findViewById(R.id.rootId);
		final ListView listView = (ListView) findViewById(R.id.list);

//		ExchangeViewManager exchangeViewManager = new ExchangeViewManager(this,
//				new ExchangeDataService());
//		exchangeViewManager.addView(fatherLayout, listView);

	}

	@Override
	public int getRightRes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public OnClickListener getRightListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitleCenter() {
		// TODO Auto-generated method stub
		return getString(R.string.xp_apps_title);
	}

	@Override
	protected CustomTitle setCustomTitle() {
		// TODO Auto-generated method stub
		return new LeftAndRightTitle(this);
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.xpapps_main;
	}

}
