package com.thinksns.sociax.t4.unit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.thinksns.sociax.android.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用于初始化Tab栏,并关联Fragment集合
 */
public class TabUtils {

    private List<Fragment> list_fragment;
    private List<RadioButton> list_buttons;

    /**
     * 添加Fragment
     *
     * @param fragmentSociaxes
     */
    public void addFragments(Fragment... fragmentSociaxes) {
        if (list_fragment == null) {
            list_fragment = new ArrayList<Fragment>();
        }
        Collections.addAll(list_fragment, fragmentSociaxes);
    }

    /**
     * 返回Fragment集合
     *
     * @return
     */
    public List<Fragment> getFragments() {
        return list_fragment;
    }

    /**
     * 添加Button
     *
     * @param parent
     */
    public void addButtons(ViewGroup parent) {
        if (parent == null || parent.getChildCount() < 1) {
            return;
        } else {
            if (list_buttons == null) {
                list_buttons = new ArrayList<RadioButton>();
            }
            for (int i = 0; i < parent.getChildCount(); i++) {
                if (parent.getChildAt(i) instanceof RadioButton) {
                    RadioButton btn = (RadioButton) parent.getChildAt(i);
                    btn.setTag(list_buttons.size());
                    list_buttons.add(btn);
                } else if (parent.getChildAt(i) instanceof ViewGroup) {
                    addButtons((ViewGroup) parent.getChildAt(i));
                }
            }
        }
    }

    /**
     * 设置单击事件
     *
     * @param listener
     */
    public void setButtonOnClickListener(View.OnClickListener listener) {
        for (RadioButton btn : list_buttons) {
            btn.setOnClickListener(listener);
        }
    }

    /**
     * 获取按钮集合
     * @return
     */
    public List<RadioButton> getButtons() {
        return list_buttons;
    }

    /**
     * TS默认Tab栏样式
     * @param context
     * @param selected
     */
    @SuppressLint("NewApi") public void setDefaultUI(Context context, int selected) {
        for (int i = 0; i < list_buttons.size(); i++) {
            boolean isShow = i == selected;
            list_buttons.get(i).setTextAppearance(context,
                    isShow ? R.style.ViewPagerButtonSelected : R.style.ViewPagerButtonUnselected);
            list_buttons.get(i).setBackgroundDrawable(isShow ? context.getResources().getDrawable(R.drawable.bottom_border_blue) : null);
            list_buttons.get(i).setPadding(UnitSociax.dip2px(context, 7), 0 , UnitSociax.dip2px(context, 7), 0);

        }
    }

    /**
     * TS默认填充整个Tab栏样式
     * @param context
     * @param selected
     */
    @SuppressLint("NewApi") public void setParentDefaultUI(Context context, int selected) {
        for (int i = 0; i < list_buttons.size(); i++) {
            boolean isShow = i == selected;
            int paddingLeft = isShow ? UnitSociax.dip2px(context, 10) : list_buttons.get(i).getPaddingLeft();
            int paddingRight = list_buttons.get(i).getPaddingRight();
            int paddingTop = list_buttons.get(i).getPaddingTop();
            int paddingBottom = list_buttons.get(i).getPaddingBottom();

            //改变字体颜色
            list_buttons.get(i).setTextAppearance(context,
                    isShow ? R.style.ViewPagerButtonSelected : R.style.ViewPagerButtonUnselected);
            ((View) list_buttons.get(i).getParent()).setBackground(isShow ? context.getResources().getDrawable(R.drawable.bottom_border_blue) : null);
            list_buttons.get(i).setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
    }
}
