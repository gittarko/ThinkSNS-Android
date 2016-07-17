package com.thinksns.sociax.t4.android.fragment;

import com.thinksns.sociax.t4.adapter.AdapterPostHotList;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/** 
 * 类说明：   
 * @author  Administrator    
 * @date    2015-1-4
 * @version 1.0
 */
public class FragmentPostHot extends FragmentPostList{
	@Override
	public void initView() {
		// TODO Auto-generated method stub
//		listView=(ListPost)findViewById(R.id.listView);
		list=new ListData<SociaxItem>();
		adapter=new AdapterPostHotList(this, list);
		listView.setAdapter(adapter);
	}

	@Override
	public void initIntentData() {
		// TODO Auto-generated method stub
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		adapter.loadInitData();
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_common_post_list;
	}

}

