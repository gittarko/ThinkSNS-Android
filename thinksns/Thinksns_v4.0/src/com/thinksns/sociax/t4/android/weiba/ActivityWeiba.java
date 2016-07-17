package com.thinksns.sociax.t4.android.weiba;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentFind;
import com.thinksns.sociax.t4.android.fragment.FragmentRecommentPost;
import com.thinksns.sociax.t4.android.fragment.FragmentWeibaList;
import com.thinksns.sociax.t4.android.weibo.ActivityCommentMeWeibo;
import com.thinksns.sociax.t4.unit.TabUtils;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;

/**
 * 微吧主页,显示推荐的微吧,帖子列表
 */
public class ActivityWeiba extends ThinksnsAbscractActivity implements OnClickListener {
	private ViewPager viewPager;
	private AdapterViewPager adapter;

	private ImageView iv_back;// 返回
	private ImageView iv_weiba_search;
	private ImageView iv_weiba_msg;
	private TextView bg_remind;

	private TabUtils mTabUtils;
	private RadioGroup rg_weiba_title;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreateNoTitle(savedInstanceState);
		initView();
		initFragments();
	}


	/**
	 * 初始化页面
	 */
	private void initView() {
		iv_back = (ImageView)findViewById(R.id.iv_back);
		iv_weiba_msg = (ImageView)findViewById(R.id.iv_weiba_msg);
		bg_remind = (TextView) findViewById(R.id.bg_remind);
		if(getIntent().getIntExtra("unread", 0) > 0) {
			bg_remind.setVisibility(View.VISIBLE);
		}
		iv_weiba_search = (ImageView)findViewById(R.id.iv_weiba_search);
		rg_weiba_title = (RadioGroup) findViewById(R.id.rg_weiba_title);
		// 首页
		viewPager = (ViewPager) findViewById(R.id.vp_home);
        adapter = new AdapterViewPager(getSupportFragmentManager());

		iv_back.setOnClickListener(this);
		iv_weiba_msg.setOnClickListener(this);
		iv_weiba_search.setOnClickListener(this);

	}

    /**
     * 初始化Fragment集合
     */
	private void initFragments() {
		mTabUtils = new TabUtils();
		mTabUtils.addFragments(
				new FragmentWeibaList(),		//我加入的微吧
				new FragmentRecommentPost()
		);
		mTabUtils.addButtons(rg_weiba_title);
        mTabUtils.setButtonOnClickListener(tabOnClickListener);

		adapter.bindData(mTabUtils.getFragments());
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
                viewPager.setCurrentItem(index); // 默认加载第一个Fragment
                mTabUtils.setDefaultUI(ActivityWeiba.this, index);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }
        });
	}

    private final OnClickListener tabOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem((Integer) v.getTag());
        }
    };

	@Override
	public String getTitleCenter() {
		return "微吧";
	}

	@Override
	protected CustomTitle setCustomTitle() {
		return new LeftAndRightTitle(this, R.drawable.img_back, R.drawable.ic_weiba_search);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_weiba;
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.iv_back:
				finish();
				break;
			case R.id.iv_weiba_search:
				//微吧搜索
				ActivityStack.startActivity(ActivityWeiba.this,	ActivitySearchWeiba.class);
				break;
			case R.id.iv_weiba_msg:
				//查看微吧消息
				Bundle bundle = new Bundle();
				bundle.putInt("type", 2);
				ActivityStack.startActivity(ActivityWeiba.this, ActivityCommentMeWeibo.class, bundle);
				bg_remind.setVisibility(View.GONE);
				FragmentFind.newInstance(0).clearUnreadMsg();
				break;
		}
	}
}
