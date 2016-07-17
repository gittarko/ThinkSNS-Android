package com.thinksns.sociax.t4.android.fragment;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterTabsPage;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.interfaces.OnTabListener;
import com.thinksns.sociax.t4.unit.TabUtils;

import java.util.ArrayList;

/**
 * 主页,用户显示用户相关的微博
 */
public class FragmentHome extends FragmentSociax {
    private ViewPager viewPager_Home;            // 首页viewpager
    private PagerSlidingTabStrip tabs;
    private LinearLayout tabsContainer;
    private RelativeLayout ll_tabs;

    private AdapterTabsPage tabsAdapter;

    private RelativeLayout rl_title;
    private LinearLayout ll_top;

    private boolean isHideTitle = false;

    AnimatorSet hideAnimatorSet;
    AnimatorSet backAnimatorSet;

    private static FragmentHome instance;

    public static FragmentHome getInstance() {
        return instance;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView() {
        instance = this;

        ll_top = (LinearLayout) findViewById(R.id.ll_top);
        viewPager_Home = (ViewPager) findViewById(R.id.vp_home);
        tabs = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabs.setTypeface(null, Typeface.NORMAL);
        tabs.setTabBackground(0);

        ll_tabs = (RelativeLayout)findViewById(R.id.ll_tabs);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        rl_title.post(new Runnable() {
            @Override
            public void run() {
                moveViewPagerDown();
            }
        });
    }

    @Override
    public void initIntentData() {

    }

    @Override
    public void initListener() {
    }

    @Override
    public void initData() {
        initFragments();
    }

    /**
     * 初始化Fragments
     */
    private void initFragments() {
        // 初始化适配器
        tabsAdapter = new AdapterTabsPage(getChildFragmentManager());
        tabsAdapter.addTab("全部", new FragmentWeiboListViewAll())
                .addTab("关注", new FragmentWeiboListViewFriends())
                .addTab("频道", new FragmentWeiboListViewChannel())
                .addTab("推荐", new FragmentWeiboListViewRecommend());
        viewPager_Home.setAdapter(tabsAdapter);
        tabs.setViewPager(viewPager_Home);
        tabsContainer = (LinearLayout)tabs.getChildAt(0);
        tabs.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((OnTabListener)tabsAdapter.getItem(position)).onTabClickListener();
                //设置选中样式
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


    //隐藏标题栏
    public void animatorHide() {
        if (!isHideTitle && rl_title.getHeight() > 0) {
            isHideTitle = true;
            if (backAnimatorSet != null)
                backAnimatorSet.cancel();
            hideAnimatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<Animator>();
            if (rl_title != null) {
                ObjectAnimator headerAnimator = ObjectAnimator.ofFloat(ll_top, "translationY",
                        ll_top.getTranslationY(), -rl_title.getHeight());

                headerAnimator.addListener(new AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator arg0) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator arg0) {

                    }

                    @Override
                    public void onAnimationEnd(Animator arg0) {
                        isHideTitle = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator arg0) {

                    }
                });

                animators.add(headerAnimator);
            }
            ObjectAnimator viewPagerAnimator = ObjectAnimator.ofFloat(viewPager_Home, "y", viewPager_Home.getY(),
                    ll_tabs.getHeight());
            viewPagerAnimator.setDuration(100).start();
            hideAnimatorSet.setDuration(300);
            hideAnimatorSet.playTogether(animators);
            hideAnimatorSet.start();
        }
    }

    public void moveViewPagerDown() {
        ObjectAnimator.ofFloat(viewPager_Home, "y", viewPager_Home.getY(), ll_tabs.getY() + ll_tabs.getHeight())
                .setDuration(300).start();
    }


    /**
     * @param forced 强制显示标题栏
     */
    public void animatorShow(boolean forced) {
        if (!isHideTitle || forced) {
            isHideTitle = true;
            if (hideAnimatorSet != null) {
                hideAnimatorSet.cancel();
            }
            backAnimatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<Animator>();
            ObjectAnimator headerAnimator = ObjectAnimator.ofFloat(ll_top, "translationY",
                    ll_top.getTranslationY(), 0);
            headerAnimator.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator arg0) {
                }

                @Override
                public void onAnimationRepeat(Animator arg0) {

                }

                @Override
                public void onAnimationEnd(Animator arg0) {
                    isHideTitle = false;
                }

                @Override
                public void onAnimationCancel(Animator arg0) {
                }
            });
            ObjectAnimator viewPagerAnimator = ObjectAnimator.ofFloat(viewPager_Home, "y", viewPager_Home.getY(),
                    ll_tabs.getY() + ll_tabs.getHeight());
            animators.add(headerAnimator);
            viewPagerAnimator.setDuration(300).start();
            backAnimatorSet.setDuration(300);
            backAnimatorSet.playTogether(animators);
            backAnimatorSet.start();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
