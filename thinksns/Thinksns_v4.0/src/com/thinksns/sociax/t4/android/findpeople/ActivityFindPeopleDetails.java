package com.thinksns.sociax.t4.android.findpeople;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.data.StaticInApp;
import com.thinksns.sociax.t4.android.fragment.FragmentAreaList;
import com.thinksns.sociax.t4.android.fragment.FragmentCityList;
import com.thinksns.sociax.t4.android.fragment.FragmentFindPeopleTagList;
import com.thinksns.sociax.t4.android.fragment.FragmentFindPeopleTopList;
import com.thinksns.sociax.t4.android.fragment.FragmentSociax;
import com.thinksns.sociax.t4.android.user.ActivityEditLocationInfo;
import com.thinksns.sociax.t4.model.Region;

/**
 * 找人二级目录 需要传入 intent int type=StaticInApp.FINDPEOPLE_XXX，详情参考StaticInApp.FINDPOPLE_XXX
 *
 * @author wz
 */
public class ActivityFindPeopleDetails extends ThinksnsAbscractActivity {
    private static final String TAG = "T4FindPersonDetails";
    FragmentSociax fragment;
    private int mType;

    public static final String EXTRA_ABBR_NAMES = "extra_abbr_names";
    public static final String EXTRA_ABBR_IDS = "extra_abbr_ids";

    /**
     * 地区名数组
     */
    private String[] abbrName = new String[Region.values().length];
    /**
     * 地区编号数据
     */
    private String[] abbrId = new String[Region.values().length];

    private String currentPid = "0";
    private Region currentType = Region.PROVINCE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mType = getIntent().getIntExtra("type", StaticInApp.FINDPEOPLE_TAG);
        super.onCreate(savedInstanceState);
        initDate();
    }

    private void initDate() {
        Bundle savedInstanceState = new Bundle();
        switch (mType) {
            case StaticInApp.FINDPEOPLE_TAG:
            case StaticInApp.TAG_SELECT:
                //按标签查找用户
                savedInstanceState.putInt("type", mType);
                fragment = new FragmentFindPeopleTagList();
                fragment.setArguments(savedInstanceState);

                fragmentManager.beginTransaction()
                        .replace(R.id.linear_fragment, fragment, "mTagPerson")
                        .commit();
                break;
            case StaticInApp.FINDPEOPLE_VERIFY:
                savedInstanceState.putInt("type", StaticInApp.FINDPEOPLE_VERIFY);
                fragment = new FragmentFindPeopleTagList();
                fragment.setArguments(savedInstanceState);
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.linear_fragment, fragment,
                                "mTagPersonRenzhen").commit();
                break;
            case StaticInApp.FINDPEOPLE_AREA:
                //按地区差早
                savedInstanceState.putInt("type", 4);
//                fragment = new FragmentCityList();
//                fragment.setArguments(savedInstanceState);
//                fragmentManager.beginTransaction()
//                        .replace(R.id.linear_fragment, fragment, "mAreaPerson")
//                        .commit();

                loadNextAbbr(currentType.ordinal(), currentPid);
                break;
            case StaticInApp.FINDPEOPLE_TOPLIST:
                fragment = new FragmentFindPeopleTopList();
                fragmentManager.beginTransaction()
                        .replace(R.id.linear_fragment, fragment, "fragment_toplist")
                        .commit();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initDate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.t4_find_person_details, menu);
        return true;
    }

    /**
     * 返回相关数据
     */
    private void resultAbbrData() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ABBR_NAMES, abbrName);
        intent.putExtra(EXTRA_ABBR_IDS, abbrId);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void loadNextAbbr(int type, String pid, String name) {
        // 储存相关数据
        if (type > 0) {
            abbrName[type - 1] = name;  // 储存当前地区名称
            abbrId[type - 1] = pid;     // 储存当前地区编号
        }

        if (type < Region.values().length-1) {
            // 设置当前地址的pid
            currentPid = pid;
            // 设置当前地址的类型
            currentType = Region.values()[type];

            // 加载下一级地区数据
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.linear_fragment, FragmentAreaList.newInstance(++type, pid));
//            transaction.addToBackStack(String.valueOf(type));
            transaction.commit();
        } else {
            String[] abbrIds = abbrId;
            String[] abbrNames = abbrName;

            String stubCityId="";
            String stubCityName="";

            for (int i = abbrIds.length - 2; i >= 0; --i) {
                if (!TextUtils.isEmpty(abbrIds[i])) {
                    stubCityId = abbrIds[i];
                    break;
                }
            }
            for (int i = abbrNames.length - 2; i >= 0; --i) {
                if (!TextUtils.isEmpty(abbrNames[i])) {
                    stubCityName = abbrNames[i];
                    break;
                }
            }
            if (stubCityId != null&&!stubCityId.equals("null")&&!stubCityId.equals("")) {
                Intent intent = new Intent(this,ActivitySearchUser.class);
                intent.putExtra("type", StaticInApp.FINDPEOPLE_CITY);
                intent.putExtra("city_id", Integer.parseInt(stubCityId));
                intent.putExtra("title", stubCityName);
                startActivity(intent);
            }
        }
    }

    public void loadNextAbbr(int type, String currentPid) {
        loadNextAbbr(type, currentPid, null);
    }

    @Override
    public String getTitleCenter() {
        String title = "";
        switch (mType) {
            case StaticInApp.FINDPEOPLE_TAG:
                title = "标签";
                break;
            case StaticInApp.TAG_SELECT:
                title = "选择标签";
                break;
            case StaticInApp.FINDPEOPLE_VERIFY:
                title = "认证";
                break;
            case StaticInApp.FINDPEOPLE_AREA:
                title = "地区";
                break;
            case StaticInApp.FINDPEOPLE_TOPLIST:
                title = "风云榜";
                break;
        }
        return title;
    }

    @Override
    protected CustomTitle setCustomTitle() {
        return new LeftAndRightTitle(R.drawable.img_back, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_t4_find_person_details;
    }

    @Override
    public void refreshHeader() {
        if (fragment != null) {
            fragment.doRefreshHeader();
        }
    }

    @Override
    public void refreshFooter() {
        if (fragment != null) {
            fragment.doRefreshFooter();
        }
    }
}
