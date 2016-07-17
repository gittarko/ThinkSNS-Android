package com.thinksns.sociax.t4.android.weiba;

import android.os.Bundle;
import android.text.TextUtils;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentWeibaAll;

/**
 * Created by hedong on 16/4/6.
 * 关于微吧的公用Activity,主要用于加载不同类型的Fragment
 */
public class ActivityWeibaCommon extends ThinksnsAbscractActivity{
    public static final int FRAGMENT_WEIBA_WALK = 0x00;       //逛一逛
    public static final int FRAGMENT_WEIBA_ALL = 0x01;        //全部微吧列表

    private String name = "微吧";
    private int type = FRAGMENT_WEIBA_WALK;   //需要加载的Fragment类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initIntent();
        super.onCreate(savedInstanceState);
        initFragment();
    }

    private void initFragment() {
        switch (type) {
            case FRAGMENT_WEIBA_WALK:
                Bundle args = new Bundle();
                args.putInt("weiba_id", getIntent().getIntExtra("weiba_id", 0));
                FragmentWeibaWalk fragment = FragmentWeibaWalk.newInstance(args);
                fragmentTransaction.add(R.id.ll_content, fragment)
                        .commit();
                break;
            case FRAGMENT_WEIBA_ALL:
                FragmentWeibaAll fragmentWeibaAll = new FragmentWeibaAll();
                fragmentTransaction.add(R.id.ll_content, fragmentWeibaAll)
                        .commit();
                break;
        }
    }

    private void initIntent() {
        name = getIntent().getStringExtra("name");
        if(TextUtils.isEmpty(name))
            name = "微吧";
        type = getIntent().getIntExtra("type", FRAGMENT_WEIBA_WALK);
    }

    @Override
    public String getTitleCenter() {
        return name;
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common;
    }
}
