package com.thinksns.sociax.t4.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.android.img.RoundImageView;
import com.thinksns.sociax.t4.android.login.ActivityLogin;
import com.thinksns.sociax.t4.android.login.ActivityRegister;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;

public class ThinksnsActivity extends InstrumentedActivity {
	private static final String TAG = "Init Activity";
	protected static final int SHOW_GUIDE = 4;
	private static final int LOGIN = 5;
	protected static final int REGISTER = 6;
	protected static final int GET_KEY = 7;

	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private ViewGroup guide;
	private LayoutInflater inflater;
	private TextView tv_register, tv_login;
	public static SharedPreferences preferences;

	private ImageView smalldot;		// 广告位小圆点
	private ImageView[] smalldots;	// 广告位所有小圆点
	private LinearLayout ll_find_ads_dots;	// 广告位红点

	public Handler handlerUI;

	private Thinksns app;
	private static ThinksnsActivity instance = null;

	public static ThinksnsActivity getInstance() {
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		boolean loginOut = getIntent().getBooleanExtra("login_out", false);
		if(loginOut) {
			Thinksns.clearAllActivity();
			//清除当前用户信息
			Thinksns.getUserSql().clear();
		}

		Thinksns.addActivity(this);
		instance = this;
		preferences = getSharedPreferences("count", MODE_WORLD_READABLE);
		inflater = getLayoutInflater();
		setContentView(R.layout.main);

		initHandler();
		this.initApp();
	}

	private void initHandler() {
		handlerUI = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.arg1 == SHOW_GUIDE) {
					showGuide();
				}else if(msg.arg1==GET_KEY){
					Class<? extends Activity> clz;
					if(msg.what==LOGIN){
						clz = ActivityLogin.class;
					}else{
						clz = ActivityRegister.class;
					}
					//进入登录主页
					ActivityStack.startActivity(ThinksnsActivity.this, clz);
//					finish();
				}
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	protected void initApp() {
		app = (Thinksns) this.getApplicationContext();
		app.initApi();
		if (app.HasLoginUser()) {
			// 已经有登录的用户，直接进入主页
			Intent intent = new Intent(ThinksnsActivity.this, ActivityHome.class);
			if(getIntent().hasExtra("type")) {
				intent.putExtra("type", getIntent().getStringExtra("type"));
			}
			startActivity(intent);
			finish();
		} else {
			Message msg2 = new Message();
			msg2.arg1 = SHOW_GUIDE;
			handlerUI.sendMessage(msg2);
		}
	}

	boolean initGuide = false;

	private void showGuide() {
		if (!initGuide) {
			pageViews = new ArrayList<View>();
			if (inflater!=null) {
				pageViews.add(inflater.inflate(R.layout.guideitem1, null));
				pageViews.add(inflater.inflate(R.layout.guideitem2, null));
				pageViews.add(inflater.inflate(R.layout.guideitem3, null));
				pageViews.add(inflater.inflate(R.layout.guideitem4, null));
//				pageViews.add(inflater.inflate(R.layout.guideitem5, null));
				guide = (ViewGroup) inflater.inflate(R.layout.guide, null);
				
				viewPager = (ViewPager) guide.findViewById(R.id.guidePages);
				viewPager.setAdapter(new GuidePageAdapter());
				tv_login = (TextView) guide.findViewById(R.id.tv_login);
				tv_register = (TextView) guide.findViewById(R.id.tv_register);

				ll_find_ads_dots = (LinearLayout) guide.findViewById(R.id.ll_find_ads_dot);
				smalldots = new ImageView[pageViews.size()];
				for (int i = 0; i < smalldots.length; i++) {
					smalldot = new RoundImageView(this);

					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.setMargins(30, 0, 0, 0);
					smalldot.setLayoutParams(lp);
					smalldots[i] = smalldot;
					if (i == 0) {
						smalldots[i].setBackgroundResource(R.drawable.dot_ring_checked);
					} else {
						smalldots[i].setBackgroundResource(R.drawable.dot_ring_unchecked);
					}
					ll_find_ads_dots.addView(smalldots[i]);
				}

				viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageScrolled(int i, float v, int i1) {

					}

					@Override
					public void onPageSelected(int i) {
						if (smalldots == null)
							return;
						for (int j = 0; j < smalldots.length; j++) {
							if (j == i) {
								smalldots[j].setBackgroundResource(R.drawable.dot_ring_checked);
							} else {
								smalldots[j].setBackgroundResource(R.drawable.dot_ring_unchecked);
							}
						}
					}

					@Override
					public void onPageScrollStateChanged(int i) {

					}
				});

				tv_login.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Message msg=new Message();
						msg.arg1 = GET_KEY;
						msg.what = LOGIN;
						handlerUI.sendMessage(msg);
					}
				});
				tv_register.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Message msg=new Message();
						msg.arg1 = GET_KEY;
						msg.what = REGISTER;
						handlerUI.sendMessage(msg);
					}
				});
				initGuide = true;
			};
			setContentView(guide);
			}
	}

	class GuidePageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}

		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(pageViews.get(arg1));
			return pageViews.get(arg1);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {

		}

		public void finishUpdate(View arg0) {

		}

	}
}