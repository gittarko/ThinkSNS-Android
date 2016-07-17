package com.thinksns.sociax.t4.android.gift;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentMyGift;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;

import java.util.ArrayList;
import java.util.List;

/**
 * 类说明： 我的礼物
 * 
 * @author Zoey
 * @date 2015年9月21日
 * @version 1.0
 */
public class ActivityMyGift extends ThinksnsAbscractActivity {
	private Handler handler;

	// 首页用到的变量
	private ViewPager vp_gift;
	private List<Fragment> fragList_home;
	private AdapterViewPager adapter_Home;

	private FragmentSociax currentFragment;
	// 声明被选择的值
	private final int SELECTED_MY_GET = 0;// 我收到的礼物
	private final int SELECTED_MY_SEND = 1;// 我送出的礼物
	private RelativeLayout rl_my_get_gift, rl_my_send_gift;
	private RadioButton  rb_my_get_gift,rb_my_send_gift;
	private RadioGroup rg_gift_title;
	private ImageView tv_title_left;

	private final int SELECT = 101;// 标记handler执行的是载入页面
	private final int INITDATA = 102;// 标记handler执行加载数据

	private boolean isInitData = false;

	private int selected = SELECTED_MY_GET;// 标记当前选择的，默认全部礼物
	
	private View view_my_get_gift,view_my_send_gift;
	private static int FLAG=-1;
	public static Activity activity=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		
		initIntentData();
		initView();
		initListener();
		initData();
	}

	private void setSelecte(int selected) {
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
		activity=this;
	}

	/**
	 * 初始化监事件
	 */
	private void initListener() {

		tv_title_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		rl_my_get_gift.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelecte(SELECTED_MY_GET);
			}
		});
		rl_my_send_gift.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelecte(SELECTED_MY_SEND);
			}
		});
	}

	
	
	/**
	 * 初始化intent信息
	 */
	private void initIntentData() {
		
	}

	/**
	 * 初始化页面
	 */
	private void initView() {
		rb_my_get_gift = (RadioButton) findViewById(R.id.rb_my_get_gift);
		rb_my_send_gift = (RadioButton) findViewById(R.id.rb_my_send_gift);

		rg_gift_title = (RadioGroup) findViewById(R.id.rg_gift_title);
		tv_title_left = (ImageView) findViewById(R.id.tv_title_left);

		rl_my_get_gift = (RelativeLayout) findViewById(R.id.rl_my_get_gift);
		rl_my_send_gift= (RelativeLayout) findViewById(R.id.rl_my_send_gift);
		
		view_my_get_gift=(View)findViewById(R.id.view_my_get_gift);
		view_my_send_gift=(View)findViewById(R.id.view_my_send_gift);
		
		// 首页
		vp_gift = (ViewPager) findViewById(R.id.vp_gift);
		
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.arg1 == SELECT) {
					if (!isInitData) {
						adapter_Home = new AdapterViewPager(getSupportFragmentManager());
						fragList_home = new ArrayList<Fragment>();
						fragList_home.add(FragmentMyGift.newInstance(FragmentMyGift.TYPE_GET));
						fragList_home.add(FragmentMyGift.newInstance(FragmentMyGift.TYPE_SEND));
						adapter_Home.bindData(fragList_home);
						vp_gift.setOffscreenPageLimit(fragList_home.size());
						initHomeViewPagerListener();
						isInitData = true;
					}
					setTitleUIBackground(selected);
					vp_gift.setCurrentItem(selected);
					if (selected == SELECTED_MY_GET) {
						currentFragment = (FragmentSociax) fragList_home.get(0);
					} 
					else if (selected == SELECTED_MY_SEND) {
						currentFragment = (FragmentSociax) fragList_home.get(1);
					}
				}
			};
		};
	}

	/**
	 * 修改顶部按钮颜色
	 * 
	 * @param selected
	 */
	private void setTitleUIBackground(int selected) {
		if (selected == SELECTED_MY_GET) {
			rb_my_get_gift.setChecked(true);
			rb_my_send_gift.setChecked(false);
			
			view_my_get_gift.setVisibility(View.VISIBLE);
			view_my_send_gift.setVisibility(View.GONE);
		} 
		else if (selected == SELECTED_MY_SEND) {
			rb_my_get_gift.setChecked(false);
			rb_my_send_gift.setChecked(true);
			
			view_my_get_gift.setVisibility(View.GONE);
			view_my_send_gift.setVisibility(View.VISIBLE);
		}
	};

	/**
	 * 注册ViewPager修改的时候的监听事件，在这里修改滑动之后顶部按钮的字体颜色，下划线颜色以及数据
	 */
	protected void initHomeViewPagerListener() {
		vp_gift.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				currentFragment = (FragmentSociax) fragList_home.get(index);
				switch (index) {
				case SELECTED_MY_GET:// 展示全部礼物
					setTitleUIBackground(SELECTED_MY_GET);
					rb_my_get_gift.setTextColor(getResources().getColor(R.color.title_blue));
					rb_my_send_gift.setTextColor(getResources().getColor(R.color.title_graybg));
					break;
				case SELECTED_MY_SEND:// 展示我的礼物
					setTitleUIBackground(SELECTED_MY_SEND);
					rb_my_get_gift.setTextColor(getResources().getColor(R.color.title_graybg));
					rb_my_send_gift.setTextColor(getResources().getColor(R.color.title_blue));
					break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}
		});

		vp_gift.setAdapter(adapter_Home);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		Intent intent=getIntent();
		if (intent!=null) {
			FLAG=intent.getIntExtra("FLAG",-1);
		}
		if (FLAG!=-1) {
			setSelecte(SELECTED_MY_SEND);
		}else {
			setSelecte(SELECTED_MY_GET);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public String getTitleCenter() {
		return null;
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return null;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_my_gift;
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

	@Override
	public void refreshHeader() {
		if (currentFragment != null && currentFragment.getAdapter() != null) {
			currentFragment.getAdapter().doRefreshHeader();
		}
	}

	@Override
	public void refreshFooter() {
		if (currentFragment != null && currentFragment.getAdapter() != null) {
			currentFragment.getAdapter().doRefreshFooter();
		}
	}

	@Override
	public OnTouchListListener getListView() {
		return currentFragment.getListView();
	}

	@Override
	public void refreshList() {
		if (currentFragment != null && currentFragment.getAdapter() != null) {
			currentFragment.getAdapter().doUpdataList();
		}
	}
}
