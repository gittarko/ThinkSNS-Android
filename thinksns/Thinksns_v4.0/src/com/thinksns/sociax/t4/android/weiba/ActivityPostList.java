package com.thinksns.sociax.t4.android.weiba;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.FragmentPostAll;
import com.thinksns.sociax.t4.android.fragment.FragmentPostDigest;
import com.thinksns.sociax.t4.android.fragment.FragmentPostHot;
import com.thinksns.sociax.t4.android.fragment.FragmentPostList;
import com.thinksns.sociax.android.R;

import android.os.Bundle;
import android.util.Log;

/** 
 * 类说明：  各种微吧的帖子列表，
 * 需要传入参数 int type：用于标fragment的类型，例如精华帖、逛一逛，详细查看StaticInApp.POST_xxx
 * int weiba_id;(微吧内的各类帖子需要传，例如精华帖)
 * @author  wz    
 * @date    2014-12-26
 * @version 1.0
 */
public class ActivityPostList extends ThinksnsAbscractActivity{
	String title="详情";
	int weiba_id;
	int type;//用于标记fragment的类型
	FragmentPostList fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		weiba_id=getIntent().getIntExtra("weiba_id", -1);
		type = getIntent().getIntExtra("type", -1);
		 if(type ==-1){
			Log.e("ActivityPostListFinish-->err", "need intent type_id");
			finish();
		}else{
			switch(type){
			case StaticInApp.POST_DIGEST:
				title = "精华帖";
				fragment = new FragmentPostDigest();
				break;
			case StaticInApp.POST_HOT:
				title="热门帖子";
				fragment=new FragmentPostHot();
				break;
			case StaticInApp.POST_ALL:
				title="逛一逛";
				fragment=new FragmentPostAll();
				break;
			}
			
		}
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		fragmentTransaction.add(R.id.ll_content, fragment);
		fragmentTransaction.commit();
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
