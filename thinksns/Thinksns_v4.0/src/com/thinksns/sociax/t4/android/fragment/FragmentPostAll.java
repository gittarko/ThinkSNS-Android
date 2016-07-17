package com.thinksns.sociax.t4.android.fragment;

import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.adapter.AdapterPostAllList;

import android.util.Log;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;

/** 
 * 类说明：   逛一逛，需要传入int weiba_id;
 * 
 * @author  wz    
 * @date    2015-2-5
 * @version 1.0
 */
public class FragmentPostAll extends FragmentPostList{
	private int weiba_id=-1;

	@Override
	public void initView() {
//		listView=(ListPost)findViewById(R.id.listView);
		list=new ListData<SociaxItem>();
		adapter=new AdapterPostAllList(this, list, weiba_id);
		listView.setAdapter(adapter);
	}

	@Override
	public void initIntentData() {
		weiba_id=getActivity().getIntent().getIntExtra("weiba_id", -1);
		if(weiba_id==-1){
			Log.e("FragmentPostAll", "FragmentWeibaDigest needs intent weiba_id");
		}
	}

	@Override
	public void initListener() {
		
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
