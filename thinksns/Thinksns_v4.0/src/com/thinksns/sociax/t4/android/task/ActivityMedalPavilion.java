package com.thinksns.sociax.t4.android.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentAllMedals;
import com.thinksns.sociax.t4.android.fragment.FragmentMyMedal;
import com.thinksns.sociax.t4.unit.TabUtils;

/** 
 * 类说明：  勋章馆 
 * @author  Zoey    
 * @date    2015年9月7日
 * @version 1.0
 */
public class ActivityMedalPavilion extends ThinksnsAbscractActivity {
	
	// 首页用到的变量
	private ViewPager viewPager;
	private AdapterViewPager adapter;

	private int uid=0;
	private RadioGroup rg_medal_title;
	private TabUtils mTabUtils;
	
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initIntentData();
		initView();
		initFragments();
		initListener();
	}

	/**
	 * 初始化intent信息
	 */
	private void initIntentData() {
		Intent intent=getIntent();
		if (intent!=null) {
			uid=intent.getIntExtra("uid", -1);
			this.setUid(uid);
		}
	}
	/**
	 * 初始化页面
	 */
	private void initView() {
		// 首页
		viewPager = (ViewPager) findViewById(R.id.vp_medal);
		adapter = new AdapterViewPager(getSupportFragmentManager());
		rg_medal_title = (RadioGroup) findViewById(R.id.rg_medal_title);
	}
	
	private void initFragments() {
		// 添加Fragment
		mTabUtils = new TabUtils();
		mTabUtils.addFragments(
				new FragmentMyMedal(),
				new FragmentAllMedals()
		);
		mTabUtils.addButtons(rg_medal_title);
		mTabUtils.setButtonOnClickListener(titleOnClickListener);

		adapter.bindData(mTabUtils.getFragments());
		viewPager.setOffscreenPageLimit(mTabUtils.getFragments().size());
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				viewPager.setCurrentItem(index); // 默认加载第一个Fragment
				mTabUtils.setDefaultUI(ActivityMedalPavilion.this, index);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});
	}

	private final OnClickListener titleOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem((Integer) v.getTag());
		}
	};

	/**
	 * 初始化监事件
	 */
	private void initListener() {

	}
	
	@Override
	public String getTitleCenter() {
		return "勋章馆";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_medal_pavilion;
	}
}
