package com.thinksns.sociax.t4.android.fragment;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterRecommendWeiboList;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.db.DbHelperManager;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

/**
 * 类说明：推荐的微博内容
 * 
 * @author wz
 * @date 2014-10-16
 * @version 1.0
 */
public class FragmentRecommendWeibo extends FragmentWeibo {
	
	protected boolean isFirstLoad = true;
	
	@Override
	public void initView() {
		super.initView();
	}
	
	@Override
	public AdapterSociaxList createAdapter() {
		list = DbHelperManager.getInstance(getActivity(), ListData.DataType.RECOMMEND_WEIBO).getHeaderData(10);
		return new AdapterRecommendWeiboList(this, list, -1);
	}
	
	@Override
	public void initReceiver() {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
