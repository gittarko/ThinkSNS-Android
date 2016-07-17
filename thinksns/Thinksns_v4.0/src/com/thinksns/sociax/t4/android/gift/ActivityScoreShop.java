package com.thinksns.sociax.t4.android.gift;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentMyGift;
import com.thinksns.sociax.t4.android.fragment.FragmentShopGift;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowGift;
import com.thinksns.sociax.t4.android.popupwindow.PopupWindowMyGIft;

/**
 * 礼物商城，使用viewpager显示{@link FragmentShopGift}商城礼物以及
 * {@link FragmentMyGift}与我相关的礼物
 */
public class ActivityScoreShop extends ThinksnsAbscractActivity
        implements PopupWindowGift.OnGiftItemClickListener, PopupWindowMyGIft.OnMyGiftItemClickListener {

    // 声明被选择的值
    private RadioGroup rg_gift_title;

    //    private TabUtils mTabUtils;
    private PopupWindowGift pwGift;
    private PopupWindowMyGIft pwMyGift;
    private RadioButton rb_gift, rb_my_gift;
    private Drawable tabMoreBlue, tabMoreDark, tabMoreBlueUp;

    private RelativeLayout rl_gift, rl_my_gift;
    private FragmentSociax currentFragment;
    private String currentType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initFragments();
        initListener();
    }

    /**
     * 初始化监事件
     */
    private void initListener() {

    }

    /**
     * 初始化页面
     */
    private void initView() {
        rg_gift_title = (RadioGroup) findViewById(R.id.rg_gift_title);

        rb_gift = (RadioButton) findViewById(R.id.rb_gift);
        rb_my_gift = (RadioButton) findViewById(R.id.rb_my_gift);

        rl_gift = (RelativeLayout) findViewById(R.id.rl_gift);
        rl_my_gift = (RelativeLayout) findViewById(R.id.rl_my_gift);

        pwGift = new PopupWindowGift(this);
        pwGift.setListener(this);
        pwGift.setOnDismissListener(onGiftDismiss);
        pwMyGift = new PopupWindowMyGIft(this);
        pwMyGift.setListener(this);
        pwMyGift.setOnDismissListener(onMyGiftDismiss);

        rl_gift.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pwGift.showAsDropDown(rg_gift_title);
                changUIForGift();
//                rb_gift.setCompoundDrawables(null, null, getTabBlueUpDrawable(), null);
            }
        });

        rl_my_gift.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pwMyGift.showAsDropDown(rg_gift_title);
                changUIForMyGift();
//                rb_my_gift.setCompoundDrawables(null, null, getTabBlueUpDrawable(), null);
            }
        });
    }

    private PopupWindow.OnDismissListener onGiftDismiss = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            if (currentFragment instanceof FragmentShopGift) {
                changUIForGift();
            } else {
                changUIForMyGift();
            }
        }
    };

    private PopupWindow.OnDismissListener onMyGiftDismiss = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            if (currentFragment instanceof FragmentMyGift) {
                changUIForMyGift();
            } else {
                changUIForGift();
            }
        }
    };

    private void changUIForGift() {
        rl_gift.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_border_blue));
        rl_my_gift.setBackgroundDrawable(null);
        rb_gift.setTextAppearance(ActivityScoreShop.this, R.style.ViewPagerButtonSelected);
        rb_my_gift.setTextAppearance(ActivityScoreShop.this, R.style.ViewPagerButtonUnselected);
//        rb_gift.setCompoundDrawables(null, null, getTabBlueDrawable(), null);
//        rb_my_gift.setCompoundDrawables(null, null, getTabDarkDrawable(), null);
    }

    private void changUIForMyGift() {
        rl_my_gift.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_border_blue));
        rl_gift.setBackgroundDrawable(null);
        rb_my_gift.setTextAppearance(ActivityScoreShop.this, R.style.ViewPagerButtonSelected);
        rb_gift.setTextAppearance(ActivityScoreShop.this, R.style.ViewPagerButtonUnselected);
//        rb_my_gift.setCompoundDrawables(null, null, getTabBlueDrawable(), null);
//        rb_gift.setCompoundDrawables(null, null, getTabDarkDrawable(), null);
    }

    private void initFragments() {
        currentType = FragmentShopGift.TYPE_ALL;
        currentFragment = FragmentShopGift.newInstance(currentType);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fl_content, currentFragment);
        ft.commit();
    }

    /**
     * 获取未展开的箭头
     *
     * @return
     */
    private Drawable getTabBlueDrawable() {
        if (tabMoreBlue == null) {
            tabMoreBlue = getResources().getDrawable(R.drawable.tab_more_blue);
            tabMoreBlue.setBounds(0, 0, tabMoreBlue.getMinimumWidth(), tabMoreBlue.getMinimumHeight());
        }
        return tabMoreBlue;
    }

    /**
     * 获取未选中的箭头
     *
     * @return
     */
    private Drawable getTabDarkDrawable() {
        if (tabMoreDark == null) {
            tabMoreDark = getResources().getDrawable(R.drawable.tab_more_dark);
            tabMoreDark.setBounds(0, 0, tabMoreDark.getMinimumWidth(), tabMoreDark.getMinimumHeight());
        }
        return tabMoreDark;
    }

    /**
     * 返回展开的箭头
     *
     * @return
     */
    private Drawable getTabBlueUpDrawable() {
        if (tabMoreBlueUp == null) {
            tabMoreBlueUp = getResources().getDrawable(R.drawable.tab_more_blue_up);
            tabMoreBlueUp.setBounds(0, 0, tabMoreBlueUp.getMinimumWidth(), tabMoreBlueUp.getMinimumHeight());
        }
        return tabMoreBlueUp;
    }

    @Override
    public String getTitleCenter() {
        return "礼物商城";
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_score_shop;
    }

    @Override
    public OnClickListener onAllGiftClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_gift.setText("全部礼物 ");
                switchGiftFragment(FragmentShopGift.TYPE_ALL);
                pwGift.dismiss();
            }
        };
    }

    @Override
    public OnClickListener onEntityGiftClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_gift.setText("实体礼物 ");
                switchGiftFragment(FragmentShopGift.TYPE_ENTITY);
                pwGift.dismiss();
            }
        };
    }

    @Override
    public OnClickListener onVirtualGiftClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_gift.setText("虚拟礼物 ");
                switchGiftFragment(FragmentShopGift.TYPE_VIRTUAL);
                pwGift.dismiss();
            }
        };
    }

    @Override
    public OnClickListener onSendGiftClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_my_gift.setText("收到的礼物 ");
                switchMyGIftFragment(FragmentMyGift.TYPE_GET);
                pwMyGift.dismiss();
            }
        };
    }

    @Override
    public OnClickListener onReceivedGiftClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_my_gift.setText("送出的礼物 ");
                switchMyGIftFragment(FragmentMyGift.TYPE_SEND);
                pwMyGift.dismiss();
            }
        };
    }

    /**
     * 切换商城礼物
     *
     * @param type
     */
    private void switchGiftFragment(String type) {
        if (!(currentFragment instanceof FragmentShopGift)) {
            currentFragment = FragmentShopGift.newInstance(type);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_content, currentFragment);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            if (!type.equals(currentType)) {
                currentType = type;
                ((FragmentShopGift) currentFragment).setType(type);
            }
        }
    }

    /**
     * 切换我的礼物
     *
     * @param type
     */
    private void switchMyGIftFragment(String type) {
        if (!(currentFragment instanceof FragmentMyGift)) {
            currentFragment = FragmentMyGift.newInstance(type);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_content, currentFragment);
            ft.commit();
        } else {
            if (!type.equals(currentType)) {
                currentType = type;
                ((FragmentMyGift) currentFragment).setType(type);
            }
        }
    }
}
