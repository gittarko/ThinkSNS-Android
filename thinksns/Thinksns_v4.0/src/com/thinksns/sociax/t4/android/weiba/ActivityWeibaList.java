package com.thinksns.sociax.t4.android.weiba;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.fragment.FragmentWeibaAll;
import com.thinksns.sociax.android.R;

/** 
 * 类说明：   微吧列表，需要传入intent int type=StaticInApp.WEIBA_XXX;默认全部微吧列表
 * 纤细参看staticinapp
 * @author  Administrator    
 * @date    2015-1-4
 * @version 1.0
 */
public class ActivityWeibaList extends ThinksnsAbscractActivity{
	String title="全部微吧";
	int type=StaticInApp.WEIBA_ALL;
	FragmentSociax fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initIntentData();
		super.onCreate(savedInstanceState);
		initView();
	}
	
	private void initView() {

		fragmentTransaction.add(R.id.ll_content, fragment);
		fragmentTransaction.commit();
	}


	private void initIntentData() {
		type=getIntent().getIntExtra("type", -1);
		if(type==-1){
			Log.e("ActivityPostListFinish-->err", "need intent type_id");
			finish();
		}else{
			switch(type){
			case StaticInApp.WEIBA_ALL:
				title="全部微吧";
				fragment=new FragmentWeibaAll();
				break;
			}
		}
	}
	@Override
	public String getTitleCenter() {
		return title;
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_common;
	}
	@Override
	public void refreshHeader() {
		fragment.getAdapter().doRefreshHeader();
	}
	@Override
	public void refreshFooter() {
		fragment.getAdapter().doRefreshFooter();
	}


}
