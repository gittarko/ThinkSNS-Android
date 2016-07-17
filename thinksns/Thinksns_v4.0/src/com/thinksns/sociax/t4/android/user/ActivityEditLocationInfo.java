package com.thinksns.sociax.t4.android.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.component.CustomTitle;
import com.thinksns.sociax.component.LeftAndRightTitle;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.sociax.t4.android.fragment.FragmentAreaList;
import com.thinksns.sociax.t4.model.Region;

/**
 * 选择地区
 */
public class ActivityEditLocationInfo extends ThinksnsAbscractActivity {
    private static final String TAG = "ActivityEditLocationInfo";
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
        super.onCreate(savedInstanceState);
        loadNextAbbr(currentType.ordinal(), currentPid);
    }

    public void loadNextAbbr(int type, String currentPid) {
        loadNextAbbr(type, currentPid, null);
    }

    /**
     * 加载下一级地区，默认加载所有省份数据。
     * 当fragment点击列表时，调用此方法：判断当前的地区类型，如果点击的是最后一级（即县、区），关闭
     * 当前页面并返回相应数据；如果还存在下一级，加载下一级Fragment，并存储当前Fragment。
     *
     * @param type 当前fragment的地区类型
     * @param pid 默认为0，fragment则返回点击地区的id
     * @param name fragment返回的地区名称
     */
    public void loadNextAbbr(int type, String pid, String name) {
        // 储存相关数据
        if (type > 0) {
            abbrName[type - 1] = name;  // 储存当前地区名称
            abbrId[type - 1] = pid;     // 储存当前地区编号
        }

        if (type < Region.values().length) {
            // 设置当前地址的pid
            currentPid = pid;
            // 设置当前地址的类型
            currentType = Region.values()[type];

            // 加载下一级地区数据
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.linear_fragment, FragmentAreaList.newInstance(++type, pid));
            transaction.addToBackStack(String.valueOf(type));
            transaction.commit();
        } else {
            resultAbbrData();
        }
    }

    /**
     * 返回相关数据
     */
    public void resultAbbrData() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ABBR_NAMES, abbrName);
        intent.putExtra(EXTRA_ABBR_IDS, abbrId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public String getTitleCenter() {
        return "选择地区";
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 不等到清空fragment堆栈的时候才退出
            if (fragmentManager.getBackStackEntryCount() == 1) {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
