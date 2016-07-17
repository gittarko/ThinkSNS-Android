package com.thinksns.sociax.t4.android.weibo;

import android.os.Bundle;
import android.util.Log;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentDiggMeWeibo;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 赞我的
 * 
 * @author wz
 * @date 2014-10-17
 * @version 1.0
 */
public class ActivityDiggMeWeibo extends ThinksnsAbscractActivity {
	FragmentDiggMeWeibo fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = new FragmentDiggMeWeibo();
		fragmentTransaction.add(R.id.ll_content, fragment);
		fragmentTransaction.commit();
	}

	@Override
	public String getTitleCenter() {
		return "赞我的";
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
	public OnTouchListListener getListView() {
		return null;//fragment.getListView();
	}

	@Override
	public void refreshHeader() {
//		fragment.doRefreshHeader();
	}

	@Override
	public void refreshFooter() {
//		fragment.doRefreshFooter();
	}
}
