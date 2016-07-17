package com.thinksns.sociax.t4.android.findpeople;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.*;

/**
 * 类说明： 查找用户 输入type区分类型 type; 0查找用户（需要传入String key），1附件的人，2地区，3认证，4按标签，5通讯录
 * 注；附近的人需要传入latitude longitude
 *
 * @author wz
 * @version 1.0
 * @date 2014-10-28
 */
public class ActivitySearchUser extends ThinksnsAbscractActivity {
    private String title = "";
    private int type;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       initIntent();
        super.onCreate(savedInstanceState);

        fragmentTransaction.replace(R.id.ll_content, fragment);
        fragmentTransaction.commit();

    }

    private void initIntent() {
        title = getIntent().getStringExtra("title");
        type = getIntent().getIntExtra("type", 0);

        switch (type) {
            case StaticInApp.FINDPEOPLE_NEARBY:
                title = "附近的人";
                fragment = new FragmentFindPeopleNearBy();
                break;
            case StaticInApp.FINDPEOPLE_CITY:
                fragment = new FragmentFindPeopleByCity();
                break;
            case StaticInApp.FINDPEOPLE_VERIFY:
                fragment = new FragmentFindPeopleByVerify();
                break;
            case StaticInApp.FINDPEOPLE_TAG:
                title = "标签";
                fragment = new FragmentFindPeopleByTag();
                break;
            case StaticInApp.FINDPEOPLE_CONTACTS:
                title = "通讯录";
                fragment = new FragmentFindPeopleByContract();
                break;
            case StaticInApp.FINDPEOPLE_KEY:
                //按关键词查找
                title = "找人";
                fragment = new FragmentFindPeopleByKey();
                break;
            case StaticInApp.WEIBO_DIGG_LIST:
                //点赞的人
                fragment = new FragmentWeiboDigg();
                break;
            default:
                break;
        }
    }

    @Override
    public String getTitleCenter() {
        return title;
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back,
                ActivitySearchUser.this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common;
    }

    @Override
    public void refreshHeader() {
    }

    @Override
    public void refreshFooter() {
    }
}
