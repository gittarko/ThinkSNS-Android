package com.thinksns.sociax.t4.android.temp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.android.R;

/**
 * 类说明：群组列表页面
 * 
 * @author wz
 * @date 2014-9-3
 * @version 1.0
 */
public class T4GroupActivity extends ThinksnsAbscractActivity {
	private Handler handler;

	// 首页用到的变量
	private ViewPager viewPager_Home;
	private List<Fragment> fragList_home;
	private AdapterViewPager adapter_Home;

	// 声明被选择的值
	private final int SELECTED_HOT = 0;// 热门
	private final int SELECTED_ALL = 1;// 全部
	private final int SELECTED_MYGROUP = 2;// 我的群组
	private RelativeLayout rl_hot, rl_all, rl_mygroup;
	private RadioButton rb_hot, rb_all, rb_mygroup;
	private RadioGroup rg_title;
	private TextView tv_title_left;

	private final int SELECT = 101;// 标记handler执行的是载入页面
	private final int INITDATA = 102;// 标记handler执行加载数据

	private boolean isInitData = false;

	private int selected = SELECTED_HOT;// 标记当前选择的，默认为热门

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		initIntentData();
		initView();
		initListener();
		initData();
		setSelecte(SELECTED_HOT);
	}

	private void setSelecte(int selected) {
		// TODO Auto-generated method stub
		this.selected = selected;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				msg.what = getSelected();
				msg.arg1 = SELECT;
				msg.sendToTarget();
			}
		}).start();
	}

	/**
	 * 载入数据
	 */
	private void initData() {
		// TODO Auto-generated method stub

		// 测试
		// loadData(AutoListView.REFRESH);
	}

	/**
	 * 初始化监事件
	 */
	private void initListener() {
		// TODO Auto-generated method stub

		tv_title_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		rl_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setSelecte(SELECTED_ALL);
			}
		});
		rl_mygroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setSelecte(SELECTED_MYGROUP);
			}
		});
		rl_hot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setSelecte(SELECTED_HOT);
			}
		});

	}

	/**
	 * 初始化intent信息
	 */
	private void initIntentData() {
		// TODO Auto-generated method stub
	}

	/**
	 * 初始化页面
	 */
	private void initView() {
		// TODO Auto-generated method stub
		rb_all = (RadioButton) findViewById(R.id.rb_all);
		rb_mygroup = (RadioButton) findViewById(R.id.rb_guanzhu);
		rb_hot = (RadioButton) findViewById(R.id.rb_tuijian);
		rg_title = (RadioGroup) findViewById(R.id.rg_weiba_title);
		tv_title_left = (TextView) findViewById(R.id.tv_title_left);

		rl_hot = (RelativeLayout) findViewById(R.id.rl_tuijian);
		rl_all = (RelativeLayout) findViewById(R.id.rl_all);
		rl_mygroup = (RelativeLayout) findViewById(R.id.rl_guanzhu);
		// 首页
		viewPager_Home = (ViewPager) findViewById(R.id.vp_home);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.arg1 == SELECT) {
					setButtonBackGround(selected);
					if (!isInitData) {
						adapter_Home = new AdapterViewPager(
								getSupportFragmentManager());
						fragList_home = new ArrayList<Fragment>();
						adapter_Home.bindData(fragList_home);
						viewPager_Home.setOffscreenPageLimit(fragList_home
								.size());
						initHomeViewPagerListener();
						isInitData = true;
					}
					viewPager_Home.setCurrentItem(selected);
				}
			};
		};
	}

	private void setButtonBackGround(int selected) {
		switch (selected) {

		case SELECTED_ALL:
			rb_all.setChecked(true);
			rb_hot.setChecked(false);
			rb_mygroup.setChecked(false);
			break;// 热门
		case SELECTED_HOT:
			rb_all.setChecked(false);
			rb_hot.setChecked(true);
			rb_mygroup.setChecked(false);

			break;// 全部
		case SELECTED_MYGROUP:
			rb_all.setChecked(false);
			rb_hot.setChecked(false);
			rb_mygroup.setChecked(true);
			break;// 我的群组
		}
	}

	// TODO Auto-generated method stub
	/**
	 * 注册ViewPager修改的时候的监听事件，在这里修改滑动之后顶部按钮的字体颜色，下划线颜色以及数据
	 */
	protected void initHomeViewPagerListener() {
		// TODO Auto-generated method stub
		viewPager_Home.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				switch (index) {
				case 0:// 展示推荐
					setButtonBackGround(SELECTED_HOT);
					rb_hot.setTextColor(getResources().getColor(
							R.color.title_blue));
					rb_all.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_mygroup.setTextColor(getResources().getColor(
							R.color.title_graybg));
					break;
				case 1:// 展示全部
					setButtonBackGround(SELECTED_ALL);
					rb_hot.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_all.setTextColor(getResources().getColor(
							R.color.title_blue));
					rb_mygroup.setTextColor(getResources().getColor(
							R.color.title_graybg));
					break;
				case 2:// 展示关注
					setButtonBackGround(SELECTED_MYGROUP);
					rb_hot.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_all.setTextColor(getResources().getColor(
							R.color.title_graybg));
					rb_mygroup.setTextColor(getResources().getColor(
							R.color.title_blue));
					break;

				}

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}
		});

		viewPager_Home.setAdapter(adapter_Home);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public String getTitleCenter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CustomTitle setCustomTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_group;
	}

	

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}
}
