package com.thinksns.sociax.t4.android.weibo;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentMyWeiboList;

/**
 * 类说明：我的微博列表
 * 
 * @author wz
 * @date 2014-10-17
 * @version 1.0
 */
public class ActivityMyWeibo extends ThinksnsAbscractActivity {
	Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = new FragmentMyWeiboList();
		fragmentTransaction.add(R.id.ll_content, fragment);
		fragmentTransaction.commit();
	}

	@Override
	public String getTitleCenter() {
		return "我的分享";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_my_weibo;
	}

	@Override
	public OnTouchListListener getListView() {
		return null;
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
