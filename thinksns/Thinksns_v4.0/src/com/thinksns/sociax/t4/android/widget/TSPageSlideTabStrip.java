package com.thinksns.sociax.t4.android.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.thinksns.sociax.android.R;

/**
 * Created by hedong on 16/3/7.
 * 自定义PagerSlidingTabStrip
 */
public class TSPageSlideTabStrip extends PagerSlidingTabStrip{
    protected int selectTabcolor, selectTabBackgroundResId;      //选中tab字体颜色和背景样式
    protected int tabDefaultTextSize, tabDefaultTextColor;

    protected LinearLayout tabsContainer;

    public TSPageSlideTabStrip(Context context) {
        super(context);
    }

    public TSPageSlideTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TSPageSlideTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //设置字体
        setTypeface(null, Typeface.NORMAL);
        //获取初始化tab字体颜色和大小
        updateTabTextStyle();
        //设置tab默认背景是透明的
        setTabBackground(0);
    }

    protected void updateTabTextStyle() {
        tabDefaultTextColor = getTextColor();
        tabDefaultTextColor = getTextSize();
    }

    @Override
    public void setTextColor(int textColor) {
        super.setTextColor(textColor);
        tabDefaultTextColor = textColor;
    }

    @Override
    public void setTextColorResource(int resId) {
        super.setTextColorResource(resId);
        tabDefaultTextColor = getTextColor();
    }

    @Override
    public void setTextSize(int textSizePx) {
        super.setTextSize(textSizePx);
        this.tabDefaultTextSize = textSizePx;
    }

    //默认字体样式为粗体，这里更改为普通
    @Override
    public void setTypeface(Typeface typeface, int style) {
        super.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
    }

    //设置选中tab的样式
    public void setSelectTabStyle(int colorResId, int backroundResId) {
        this.selectTabcolor = getResources().getColor(colorResId);
        this.selectTabBackgroundResId = backroundResId;
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        super.setOnPageChangeListener(listener);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        tabsContainer = (LinearLayout)getChildAt(0);
    }

    private void switchTabColor(int position) {
        if(selectTabcolor == tabDefaultTextColor
                && selectTabBackgroundResId == getTabBackground()) {
            return;
        }

        int count = tabsContainer.getChildCount();
        for(int i=0; i<count; i++) {
            TextView selectView = (TextView)tabsContainer.getChildAt(i);
            if(position == i) {
                //设置选中颜色，背景
                selectView.setTextColor(selectTabcolor);
                selectView.setBackgroundResource(selectTabBackgroundResId);
            }
            else {
                selectView.setTextColor(getResources().getColor(R.color.black));
                selectView.setTextSize(tabDefaultTextSize);
                selectView.setBackgroundResource(0);
            }
        }

    }

    public class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //改变选中页卡样式
            switchTabColor(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
