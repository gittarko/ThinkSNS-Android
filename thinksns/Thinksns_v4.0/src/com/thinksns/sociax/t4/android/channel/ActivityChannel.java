package com.thinksns.sociax.t4.android.channel;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.adapter.AdapterTabsPage;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentChannelList;
import com.thinksns.sociax.t4.android.fragment.FragmentFollowChannelList;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.unit.TabUtils;

/**
 * 频道界面,包含探索频道和已关注频道
 */
public class ActivityChannel extends ThinksnsAbscractActivity {
    private ViewPager viewPager_Home;
    private RelativeLayout ll_tabs;
    private LinearLayout tabsContainer;

    private PagerSlidingTabStrip tabs;
    private AdapterTabsPage adapterTabsPage;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initFragments();
    }



    /**
     * 初始化页面
     */
    private void initView() {
        ll_tabs = (RelativeLayout)findViewById(R.id.ll_tabs);
        tabs = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabs.setTypeface(null, Typeface.NORMAL);
        tabs.setTabBackground(0);

        // 首页
        viewPager_Home = (ViewPager) findViewById(R.id.vp_home);
    }

    private void initFragments() {
        adapterTabsPage = new AdapterTabsPage(getSupportFragmentManager());
        adapterTabsPage.addTab("探索频道",new FragmentChannelList())
                .addTab("已关注频道", new FragmentFollowChannelList());
        viewPager_Home.setAdapter(adapterTabsPage);
        tabs.setViewPager(viewPager_Home);
        tabsContainer = (LinearLayout)tabs.getChildAt(0);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchTabColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void switchTabColor(int position) {
        int count = tabsContainer.getChildCount();
        for(int i=0; i<count; i++) {
            TextView selectView = (TextView)tabsContainer.getChildAt(i);
            if(position == i) {
                //设置选中颜色，背景
                selectView.setTextColor(getResources().getColor(R.color.title_blue));
            }
            else {
                selectView.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_channel;
    }

    @Override
    public String getTitleCenter() {
        return "频道";
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }
}

