package com.thinksns.sociax.t4.android.weiba;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentPostDigg;

/**
 * Created by hedong on 16/3/31.
 */
public class ActivityPostCommon extends ThinksnsAbscractActivity{

    private FragmentPostDigg postDigg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postDigg = FragmentPostDigg.newInstance(getIntent().getIntExtra("post_id", 0));
        fragmentTransaction.add(R.id.ll_content, postDigg)
                .commit();
    }

    @Override
    public String getTitleCenter() {
        return "点赞人列表";
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
