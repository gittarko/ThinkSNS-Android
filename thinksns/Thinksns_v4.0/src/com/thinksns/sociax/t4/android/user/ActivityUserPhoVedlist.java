package com.thinksns.sociax.t4.android.user;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.fragment.FragmentUserPhotoList;
import com.thinksns.sociax.t4.android.fragment.FragmentUserVedioList;
import com.thinksns.sociax.android.R;

/**
 * 类说明： 用户的图片列表/视频列表 需要传入int uid，String type ：photo,vedio；
 * 
 * @author wz
 * @date 2014-11-24
 * @version 1.0
 */
public class ActivityUserPhoVedlist extends ThinksnsAbscractActivity {
	String type = "photo";
	LinearLayout ll_content;
	FragmentSociax fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initIntentData();
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initData() {
		if (type.equals("photo")) {
			fragment = new FragmentUserPhotoList();
		} else {
			fragment = new FragmentUserVedioList();
		}

		fragmentTransaction.add(R.id.ll_content, fragment);
		fragmentTransaction.commit();
	}

	private void initView() {
		ll_content = (LinearLayout) findViewById(R.id.ll_content);
	}

	private void initIntentData() {
		type = getIntent().getStringExtra("type");
	}

	@Override
	public String getTitleCenter() {
		return type.equals("photo") ? "相册" : "视频";
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
	public void refreshFooter() {
		fragment.getAdapter().doRefreshFooter();
	}

	@Override
	public void refreshHeader() {
		fragment.getAdapter().doRefreshHeader();
	}
}
