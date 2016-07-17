package com.thinksns.sociax.t4.android.fragment;

import com.thinksns.sociax.t4.adapter.AdapterFeedBackType;
import com.thinksns.sociax.t4.component.ListFeedBack;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/** 
 * 类说明：   反馈类型
 * @author  wz    
 * @date    2015-1-26
 * @version 1.0
 */
public class FrgamnetFeedBackType extends FragmentSociax{

	@Override
	public int getLayoutId() {
		return R.layout.fragment_feedback_type;
	}

	@Override
	public void initView() {
		listView = (ListFeedBack) findViewById(R.id.listView);
		list=new ListData<SociaxItem>();
		adapter=new AdapterFeedBackType(this,list);
		listView.setAdapter(adapter);
		
	}

	@Override
	public void initIntentData() {
	}

	@Override
	public void initListener() {
		
	}

	@Override
	public void initData() {
		adapter.loadInitData();
	}
}
