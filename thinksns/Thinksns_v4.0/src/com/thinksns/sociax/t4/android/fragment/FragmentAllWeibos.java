package com.thinksns.sociax.t4.android.fragment;

import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterAllWeiboList;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.t4.android.db.DbHelperManager;
import com.thinksns.sociax.thinksnsbase.bean.ListData;

/**
 * 类说明：所有微博
 *
 * @author Zoey modifiy dong.he
 */
public class FragmentAllWeibos extends FragmentWeibo {

    protected boolean isFirstLoad = true;

    @Override
    public AdapterSociaxList createAdapter() {
        //获取本地数据
        list = DbHelperManager.getInstance(getActivity(), ListData.DataType.ALL_WEIBO).getHeaderData(PAGE_COUNT);
        return new AdapterAllWeiboList(this, list, -1);
    }

    @Override
    public void initIntentData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        loadRemoteData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_all_weibo_list;
    }

    @Override
    protected boolean getFirstLoad() {
        return isFirstLoad;
    }

    @Override
    protected void onFinishLoad(boolean finish) {
        isFirstLoad = !finish;
    }

}
