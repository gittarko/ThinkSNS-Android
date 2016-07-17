package com.thinksns.sociax.t4.android.weiba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.thinksns.sociax.adapter.AdapterSearchTopic;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterSearchWeiba;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentRecommentPost;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.fragment.FragmentWeibaAll;
import com.thinksns.sociax.t4.android.fragment.FragmentWeibaList;
import com.thinksns.sociax.t4.model.ModelWeiba;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.bean.SociaxItem;
import com.thinksns.sociax.thinksnsbase.utils.UnitSociax;

import java.util.ArrayList;
import java.util.List;

public class ActivitySearchWeiba extends ThinksnsAbscractActivity
        implements View.OnClickListener {

    private static final int SELECTED_WEIBA = 0;// 微吧
    private static final int SELECTED_TZ = 1;// 帖子

    private EditText et_search;
    private Button btn_cancel;
    private ViewPager mViewPager;
    private RadioButton rb_search_weiba, rb_search_tz;
    private AdapterViewPager adapter;

    private List<Fragment> list_fragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreateNoTitle(savedInstanceState);
        initView();
        initListener();

        setSelected(SELECTED_WEIBA);
    }

    /**
     * 设置选择的页面
     *
     * @param selected
     */
    private void setSelected(int selected) {
        setTitleUI(selected);
        setButtonBackGround(selected);
        mViewPager.setCurrentItem(selected);
        currentFragment = list_fragment.get(selected);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        rb_search_weiba.setOnClickListener(this);
        rb_search_tz.setOnClickListener(this);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        // 键盘搜索点击事件
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    searchContext();
                }
                return false;
            }
        });
    }

    /**
     * 搜索内容
     */
    public void searchContext() {
        switch (mViewPager.getCurrentItem()) {
            case SELECTED_WEIBA:
                searchWeiba(et_search.getText().toString().trim());
                break;
            case SELECTED_TZ:
                searchTopic(et_search.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    /**
     * 搜索帖子
     * @param name
     */
    private void searchTopic(String name) {
        FragmentRecommentPost frp = (FragmentRecommentPost) currentFragment;
        AdapterSearchTopic adapterSearchTopic = new AdapterSearchTopic(frp, frp.getList(), name);
        frp.getPullRefreshView().setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        frp.getPullRefreshView().getRefreshableView().setAdapter(adapterSearchTopic);
        try {
            adapterSearchTopic.refreshNew(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 搜索微吧
     *
     * @param name
     */
    public void searchWeiba(String name) {
        FragmentWeibaList fwl = (FragmentWeibaList) currentFragment;
        AdapterSearchWeiba adapterSearchWeiba = new AdapterSearchWeiba(fwl, new ListData<SociaxItem>(), name);
        fwl.getPullRefreshView().getRefreshableView().setAdapter(adapterSearchWeiba);
        try {
            adapterSearchWeiba.loadInitData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理化控件
     */
    private void initView() {
        et_search = (EditText) findViewById(R.id.et_search);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        mViewPager = (ViewPager) findViewById(R.id.vp_search);
        rb_search_weiba = (RadioButton) findViewById(R.id.rb_search_weiba);
        rb_search_tz = (RadioButton) findViewById(R.id.rb_search_tz);

        adapter = new AdapterViewPager(getSupportFragmentManager());
        list_fragment = new ArrayList<Fragment>();
        Bundle args = new Bundle();
        args.putBoolean("down_to_refresh", false);
        list_fragment.add(FragmentWeibaAll.newInstance(args));
        list_fragment.add(FragmentRecommentPost.newInstance(args));

        adapter.bindData(list_fragment);
        mViewPager.setOffscreenPageLimit(list_fragment.size());
        initHomeViewPagerListener();
    }

    /**
     * 初始化ViewPager
     */
    private void initHomeViewPagerListener() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        setButtonBackGround(SELECTED_WEIBA);
                        rb_search_weiba.setTextColor(getResources().getColor(
                                R.color.title_blue));
                        rb_search_tz.setTextColor(getResources().getColor(
                                R.color.title_graybg));
                        currentFragment = list_fragment.get(0);

                        setTitleUI(SELECTED_WEIBA);
                        break;
                    case 1:
                        setButtonBackGround(SELECTED_TZ);
                        rb_search_weiba.setTextColor(getResources().getColor(
                                R.color.title_graybg));
                        rb_search_tz.setTextColor(getResources().getColor(
                                R.color.title_blue));
                        currentFragment = list_fragment.get(1);

                        setTitleUI(SELECTED_TZ);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(adapter);
    }

    /**
     * 设置Button背景
     *
     * @param selected
     */
    private void setButtonBackGround(int selected) {
        switch (selected) {
            case SELECTED_WEIBA:
                rb_search_weiba.setChecked(true);
                rb_search_tz.setChecked(false);
                break;
            case SELECTED_TZ:
                rb_search_weiba.setChecked(false);
                rb_search_weiba.setChecked(true);
                break;
            default:
                break;
        }
    }

    /**
     * 设置标题UI
     *
     * @param selected
     */
    private void setTitleUI(int selected) {
        switch (selected) {
            case SELECTED_WEIBA:
                rb_search_weiba.setBackgroundResource(R.drawable.bottom_border_blue);
                rb_search_weiba.setTextColor(getResources().getColor(R.color.title_blue));
                rb_search_weiba.setPadding(UnitSociax.dip2px(ActivitySearchWeiba.this, 7), 0,
                        UnitSociax.dip2px(ActivitySearchWeiba.this, 7), 0);
                rb_search_tz.setBackgroundResource(0);
                rb_search_tz.setTextColor(getResources().getColor(R.color.black));
                break;
            case SELECTED_TZ:
                rb_search_tz.setBackgroundResource(R.drawable.bottom_border_blue);
                rb_search_tz.setTextColor(getResources().getColor(R.color.title_blue));
                rb_search_tz.setPadding(UnitSociax.dip2px(ActivitySearchWeiba.this, 7), 0,
                        UnitSociax.dip2px(ActivitySearchWeiba.this, 7), 0);
                rb_search_weiba.setBackgroundResource(0);
                rb_search_weiba.setTextColor(getResources().getColor(R.color.black));
                break;
        }
    }

    @Override
    public void refreshFooter() {
        if (currentFragment != null) {
            ((FragmentSociax)currentFragment).getAdapter().doRefreshFooter();
        }
    }

    @Override
    public void refreshHeader() {
        if (currentFragment != null) {
            ((FragmentSociax)currentFragment).getAdapter().doRefreshHeader();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_search_weiba:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rb_search_tz:
                mViewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    @Override
    public String getTitleCenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_weiba;
    }
}
