package com.thinksns.sociax.t4.android.draft;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.View;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;

/**
 * 类说明：
 * 
 * @author Administrator
 * @date 2014-11-10
 * @version 1.0
 */
public class ActivityMyDraft extends ThinksnsAbscractActivity {
	Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = new FragmentMyDraftList();
		fragmentTransaction.add(R.id.ll_content, fragment);
		fragmentTransaction.commit();

	}

	@Override
	public String getTitleCenter() {
		return "草稿箱";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_draft;
	}
}
