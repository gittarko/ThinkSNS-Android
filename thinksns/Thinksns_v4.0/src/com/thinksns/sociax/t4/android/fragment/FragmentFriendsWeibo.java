package com.thinksns.sociax.t4.android.fragment;

import com.thinksns.sociax.android.R;

/**
 * 类说明：好友的微博
 * 
 * @author wz
 * @date 2014-10-16
 * @version 1.0
 */
public class FragmentFriendsWeibo extends FragmentWeibo {

	protected boolean isFirstLoad = true;

	@Override
	public void initReceiver() {

	}

	@Override
	public void initIntentData() {
	}
	
	@Override
	public int getLayoutId() {
		return R.layout.fragment_common_weibo_list_hasloadingview;
	}


	@Override
	protected boolean getFirstLoad() {
		return isFirstLoad;
	}

	@Override
	protected void onFinishLoad(boolean finish) {
		isFirstLoad = !finish;
	}
}
