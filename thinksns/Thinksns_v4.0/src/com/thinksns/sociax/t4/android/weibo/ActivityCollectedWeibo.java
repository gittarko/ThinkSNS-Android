package com.thinksns.sociax.t4.android.weibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterViewPager;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.*;
import com.thinksns.sociax.t4.android.presenter.PostListPresenter;
import com.thinksns.sociax.t4.model.ModelPost;
import com.thinksns.sociax.t4.unit.TabUtils;

/**
 * 我的收藏,展示收藏的微博和帖子.
 */
public class ActivityCollectedWeibo extends ThinksnsAbscractActivity {

    private RadioGroup rg_weiba_title;
    private ViewPager viewPager;
    private ImageView iv_back;
    private AdapterViewPager adapter;

    private TabUtils mTabUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreateNoTitle(savedInstanceState);
        initView();
        initFragments();
        initListener();
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化View
     */
    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        rg_weiba_title = (RadioGroup) findViewById(R.id.rg_weiba_title);

        // 首页
        viewPager = (ViewPager) findViewById(R.id.vp_home);
        adapter = new AdapterViewPager(getSupportFragmentManager());
    }

    /**
     * 标题RadioButton点击事件
     */
    private final View.OnClickListener titleOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem((Integer) v.getTag());
        }
    };

    /**
     * 初始化Fragment集合以及适配器,其中集合包涵:
     * 微博收藏{@link BaseFragmentPostList}
     * 帖子收藏{@link FragmentCollectPost}
     */
    private void initFragments() {
        // 添加Fragment
        mTabUtils = new TabUtils();
        Fragment collectFragment = new FragmentWeiboCollectList();
        BaseFragmentPostList postFragment = new BaseFragmentPostList();
        postFragment.setRequestPostType(PostListPresenter.MY_COLLECT_POST);

        mTabUtils.addFragments(collectFragment, postFragment);
        mTabUtils.addButtons(rg_weiba_title);
        mTabUtils.setButtonOnClickListener(titleOnClickListener);

        adapter.bindData(mTabUtils.getFragments());
        viewPager.setOffscreenPageLimit(mTabUtils.getFragments().size());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
                viewPager.setCurrentItem(index); // 默认加载第一个Fragment
                mTabUtils.setDefaultUI(ActivityCollectedWeibo.this, index);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
        });
    }

    @Override
    public String getTitleCenter() {
        return "我的收藏";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collected;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 刷新首页数据
        if (resultCode == RESULT_OK) {
            Fragment fragment = mTabUtils.getFragments().get(viewPager.getCurrentItem());
            if (fragment instanceof FragmentWeibo) {
                ((FragmentWeibo) fragment).getAdapter().refreshNewSociaxList();
            }
        }
    }
}
