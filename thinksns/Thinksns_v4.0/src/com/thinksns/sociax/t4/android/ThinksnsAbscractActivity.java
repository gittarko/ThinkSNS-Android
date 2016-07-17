package com.thinksns.sociax.t4.android;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.android.ThinksnsImageView;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.listener.OnTouchListListener;
import com.thinksns.sociax.modle.ApproveSite;
import com.thinksns.sociax.t4.android.Listener.ListenerSociax;
import com.thinksns.sociax.t4.android.gift.TestActivity;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowDialog1;
import com.thinksns.sociax.t4.android.user.ActivityRecommendTag;
import com.thinksns.sociax.t4.unit.PrefUtils;
import com.thinksns.sociax.thinksnsbase.activity.widget.LoadingView;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;
import com.thinksns.sociax.thinksnsbase.utils.Anim;

import java.util.List;

public abstract class ThinksnsAbscractActivity extends FragmentActivity
		implements GestureDetector.OnGestureListener, View.OnTouchListener {

	protected static final String TIPS = "tips";
	protected static final String TAG = "ThinksnsAbscractActivity";

	protected CustomTitle title;
	protected Bundle data;
	protected View mBtton;
	public static final int MYWEIBO_DEL = 1212;
	private GestureDetector mGDetector;

	protected FragmentManager fragmentManager = getSupportFragmentManager();
	protected FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	protected String ActivityTag = "";
	
	protected boolean isHasEmailList = false;
	protected List<String> emailList;
	public static boolean sendFlag = false;

	// 获取中间
	public abstract String getTitleCenter();

	// 获取左边资源
	public int getLeftRes() {
		return R.drawable.menu_back_img;
	}

	// 获取右边资源
	public int getRightRes() {
		return R.drawable.menu_home_img;
	}
	protected LoadingView loadingView;

	/**
	 * 获取右边按钮的view
	 * 
	 * @return
	 */
	public View getRightView() {
		if (title != null)
			return title.getRight();
		else
			return null;
	}

	// 是否在底部tab中
	public boolean isInTab() {
		return false;
	}

	/**
	 * 设置头部
	 * 
	 * @return
	 */
	protected CustomTitle setCustomTitle(){
		return null;
	};

	public void executeDataSuccess(ListData<SociaxItem> list) {
    	
    }

	protected void onCreateNoTitle(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		initCreate();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ActivityTag.equals("MainGridActivity")
				|| ActivityTag.equals("MainTaskActivity")) {
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			initCreate();
		} else {
			this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			this.setTheme(R.style.titleTheme);
			// 加载布局
			initCreate();
			initTitle();
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		this.paramDatas();
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	protected void onCreateDefault(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initCreate();
		initTitle();
	}

	// 初始化title
	protected void initTitle() {
		if (!this.isInTab()) {
			title = this.setCustomTitle();
		} else {
			title = this.setCustomTitle();
		}
	}

	/**
	 * 设置布局
	 */
	private void initCreate() {
		//如果是新用户则跳转至补全注册流程
		if (getIntent().hasExtra("new_user")
				&& getIntent().getBooleanExtra("new_user", false)) {
			startActivity(new Intent(this, ActivityRecommendTag.class));
		}

		setContentView(this.getLayoutId());
		//设置页面背景颜色
		ColorDrawable bgDrawable = new ColorDrawable(getResources().getColor(R.color.page_background_color));
		this.getWindow().setBackgroundDrawable(bgDrawable);

		//如果有的话
		loadingView = (LoadingView) findViewById(LoadingView.ID);
		this.paramDatas();
		mGDetector = new GestureDetector(this);
		Thinksns.addActivity(this);

	}

	public Bundle getIntentData() {
		if (data != null)
			return data;
		data = new Bundle();
		return data;
	}

	// 看Intent数据中是否有TIPS，如果有,去除TIPS，用Toast显示TIPS
	protected void paramDatas() {
		data = this.getIntent().getExtras();
		if (data != null && data.containsKey(TIPS)) {
			String tips = data.getString(TIPS);
			data.remove(TIPS);
			Toast.makeText(this, tips, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 获取布局
	 * 
	 * @return
	 */
	protected abstract int getLayoutId();

	// 左部点击，加入栈
	public OnClickListener getLeftListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				Thinksns.finishActivity(ThinksnsAbscractActivity.this);
			}
		};
	}

	// 右边点击，清除栈，进入home
	public OnClickListener getRightListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivityStack.startActivity(ThinksnsAbscractActivity.this,TestActivity.class);
			}
		};
	}

	@Override
	public void finish() {
		Thinksns app = (Thinksns) this.getApplicationContext();
		app.closeDb();
		super.finish();
	}

	/**
	 * 列表头部刷新，一般执行adapter.refreshHeader;
	 */
	public void refreshHeader() {
	}

	/**
	 * 列表加载更多，一般执行adapter.refresher;
	 */
	public void refreshFooter() {
	}

	/**
	 * 列表刷新，一般执行 adapter.updateList;
	 */
	public void refreshList() {

	}

	/**
	 * 数据list view隐藏 显示loading view
	 */
	public OnTouchListListener getListView() {
		return null;
	}

	public PullToRefreshListView getPullRefreshView() {
		return null;
	}

	public View getDefaultView() {
		return null;
	}
	
	/**
	 * 用于loadingview显示的时候隐藏的部分一般是null。某个需要隐藏的view的时候重写，例如ActivityFindPeople
	 * 
	 * @return
	 */
	public View getOtherView() {
		return null;
	}

	/**
	 * 本方法暂时保留，T3时候的方法，后期需要删除
	 * 
	 * @param view
	 * @param state
	 */
	public void updateView(View view, int state) {
	}

	/**
	 * 设置title部分
	 * 
	 * @return
	 */
	public CustomTitle getCustomTitle() {
		return title;
	}

	/**
	 * 全屏查看图片
	 * 
	 * @param url
	 * @return
	 */
	public OnClickListener getImageFullScreen(final String url) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				getIntentData().putString("url", url);
				ActivityStack.startActivity(ThinksnsAbscractActivity.this,
						ThinksnsImageView.class, getIntentData());
			}

		};
	}

	public int getSiteId() {
		Thinksns app = (Thinksns) this.getApplicationContext();
		ApproveSite as = Thinksns.getMySite();
		if (Thinksns.getMySite() == null) {
			return 0;
		} else {
			return Thinksns.getMySite().getSite_id();
		}
	}

	protected void dialog() {
		PopupWindowDialog1 dialog=new PopupWindowDialog1(this, "温馨提示", "确认退出程序？", "取消", "确认");
		dialog.setListenerSociax(new ListenerSociax() {
			@Override
			public void onTaskSuccess() {
				
			}
			
			@Override
			public void onTaskError() {
				
			}
			
			@Override
			public void onTaskCancle() {
				int currentVersion = android.os.Build.VERSION.SDK_INT;
				if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
					Thinksns app = ((Thinksns) getApplicationContext());
					app.exitApp();
				} else {
					// android2.1 支持 restartPackage 结束
					ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					activityManager.restartPackage("com.thinksns.sociax.android");
					System.exit(0);
				}
			}
		});
		dialog.show();
	}

	// //////////////////////************************/////////////////////

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		System.err.println(" on touch  ... qqqq");
		// if ((e1.getX() - e2.getX() > 100.0F) && (Math.abs(velocityX) >
		// 200.0F)) {
		//
		// } else if ((e2.getX() - e1.getX() > 100.0F) && (Math.abs(velocityX) >
		// 200.0F)) {
		// this.finish();
		// }
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGDetector.onTouchEvent(event);
	}

}
