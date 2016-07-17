package com.thinksns.sociax.t4.android.task;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentCopyTask;
import com.thinksns.sociax.t4.android.fragment.FragmentDailyTask;
import com.thinksns.sociax.t4.android.fragment.FragmentMainTask;
import com.thinksns.sociax.t4.unit.TabUtils;

/**
 * 类说明：任务中心
 *
 * @author Administrator
 * @version 1.0
 * @date 2014-11-10
 */
public class ActivityTaskCenter extends ThinksnsAbscractActivity {

	// 首页用到的变量
	private ViewPager viewPager_task;
	private AdapterViewPager adapter;

	private RadioGroup rg_task_title;
	private TabUtils mTabUtils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
        initFragments();
		initListener();
	}

	/**
	 * 初始化监事件
	 */
	private void initListener() {
	}

	/**
	 * 初始化页面
	 */
	private void initView() {
		rg_task_title = (RadioGroup) findViewById(R.id.rg_task_title);

		// 首页
		viewPager_task = (ViewPager) findViewById(R.id.vp_task);
        adapter = new AdapterViewPager(getSupportFragmentManager());
	}

	private void initFragments() {
		// 添加Fragment
		mTabUtils = new TabUtils();
		mTabUtils.addFragments(
				new FragmentMainTask(),
				new FragmentDailyTask(),
				new FragmentCopyTask()
		);
		mTabUtils.addButtons(rg_task_title);
		mTabUtils.setButtonOnClickListener(titleOnClickListener);

		adapter.bindData(mTabUtils.getFragments());
        viewPager_task.setOffscreenPageLimit(mTabUtils.getFragments().size());
        viewPager_task.setAdapter(adapter);
        viewPager_task.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
                viewPager_task.setCurrentItem(index); // 默认加载第一个Fragment
                mTabUtils.setDefaultUI(ActivityTaskCenter.this, index);
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
			viewPager_task.setCurrentItem((Integer) v.getTag());
		}
	};
	@Override
	public String getTitleCenter() {
		return "任务中心";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(R.drawable.img_back, this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_task;
	}

}
