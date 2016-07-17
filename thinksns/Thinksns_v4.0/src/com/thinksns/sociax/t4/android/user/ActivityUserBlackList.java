package com.thinksns.sociax.t4.android.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentBlackList;

/**
 * 类说明： 黑名单
 * 
 * @author wz
 * @date 2014-11-12
 * @version 1.0
 */
public class ActivityUserBlackList extends ThinksnsAbscractActivity {
	private static final String TAG = "ActivityUserBlackList";
	private FragmentBlackList fg_blackList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initFragment();
		initDate(getIntent());
	}

	private void initFragment() {
		Log.d(TAG, "initFragment()");
		fg_blackList = new FragmentBlackList();
	}

	private void initDate(Intent intent) {
		fragmentManager.beginTransaction()
				.replace(R.id.linear_fragment, fg_blackList, "mTagPerson")
				.commit();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		initDate(intent);
		Log.d(TAG, "onNewIntent");
	}

	@Override
	public String getTitleCenter() {
		return "黑名单";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_t4_find_person_details;
	}
}
