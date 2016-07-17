package com.thinksns.sociax.t4.android.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.AdapterFindPeopleNearByList;
import com.thinksns.sociax.t4.adapter.AdapterSociaxList;
import com.thinksns.sociax.t4.adapter.AdapterUserFollowingListNew;

import com.thinksns.sociax.t4.android.presenter.NearByUserPresenter;
import com.thinksns.sociax.t4.android.user.ActivityUserInfo_2;

import com.thinksns.sociax.t4.model.ModelSearchUser;
import com.thinksns.sociax.thinksnsbase.base.BaseListFragment;
import com.thinksns.sociax.thinksnsbase.base.ListBaseAdapter;
import com.thinksns.sociax.thinksnsbase.bean.ListData;
import com.thinksns.sociax.thinksnsbase.utils.ActivityStack;

/**
 * 类说明： 附近的人
 * @author hedong
 * @version 1.0
 * @date 2016.3.1
 */
public class FragmentFindPeopleNearBy extends BaseListFragment<ModelSearchUser>
        implements AMapLocationListener{
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initOptions();
    }


    /**
     * 初始化定位参数
     */
    public void initOptions() {
        locationClient = new AMapLocationClient(getActivity().getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationOption.setOnceLocation(true);
        // 设置定位监听
        locationClient.setLocationListener(this);

        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    @Override
    protected void initPresenter() {
        //自定义实现附近的人Presenter
        mPresenter = new NearByUserPresenter(getActivity(), this);
        mPresenter.setCacheKey("nearby_user");
    }

    @Override
    public void onDestroy() {
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        super.onDestroy();
    }

    @Override
    protected boolean requestDataIfViewCreated() {
        return false;
    }

    @Override
    protected ListBaseAdapter<ModelSearchUser> getListAdapter() {
        return new AdapterUserFollowingListNew(getActivity()) {
            @Override
            public int getMaxId() {
                if(mDatas == null || mDatas.size() == 0)
                    return 0;
                return ((ModelSearchUser)mDatas.get(mDatas.size() -1)).getUid();
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ModelSearchUser modelSearchUser = (ModelSearchUser) mAdapter.getItem((int) id);
        if(modelSearchUser != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("uid", modelSearchUser.getUid());
            ActivityStack.startActivity(getActivity(), ActivityUserInfo_2.class, bundle);
        }
    }

    @Override
    public void onLoadDataSuccess(ListData<ModelSearchUser> data) {
        mEmptyLayout.setNoDataContent(getResources().getString(R.string.empty_user));
        super.onLoadDataSuccess(data);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                ((NearByUserPresenter)mPresenter).setLatitudeLngitude(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mPresenter.loadInitData(true);
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Toast.makeText(getActivity(), "定位失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
