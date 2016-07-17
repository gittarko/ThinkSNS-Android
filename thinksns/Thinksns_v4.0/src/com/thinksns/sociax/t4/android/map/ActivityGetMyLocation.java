package com.thinksns.sociax.t4.android.map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.thinksns.sociax.android.R;
import com.thinksns.sociax.t4.adapter.PoiAdapter;
import com.thinksns.sociax.t4.android.ThinksnsAbscractActivity;
import com.thinksns.tschat.widget.SmallDialog;

import java.util.List;

public class ActivityGetMyLocation extends ThinksnsAbscractActivity
        implements LocationSource, AMapLocationListener, PoiSearch.OnPoiSearchListener, AdapterView.OnItemClickListener {

    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private ImageButton tv_title_left;
    private SmallDialog dialog;

    // about map data
    private String currentAddress;
    private double currentLatitude, currentLongitude;

    private ListView listPoi;
    private View headerView;
    private TextView headerStreet;

    // Poi
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private PoiResult poiResult; // poi返回的结果
    private List<PoiItem> poiItems;// poi数据
    private PoiAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreateNoTitle(savedInstanceState);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写

        initView();
        initMap();
    }

    /**
     * 初始化地图相关
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();

            aMap.setLocationSource(this);// 设置定位监听
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv_title_left = (ImageButton) findViewById(R.id.tv_title_left);

        listPoi = (ListView) findViewById(R.id.list_poi);
        listPoi.setOnItemClickListener(this);

        headerView = getLayoutInflater().inflate(R.layout.header_my_location, null, false);
        headerStreet = (TextView) headerView.findViewById(R.id.tv_location_street);

        initListener();
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        tv_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityGetMyLocation.this.finish();
            }
        });

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                returnLocation(currentAddress, currentLatitude, currentLatitude);
            }
        });
    }

    /**
     * 显示提示框
     */
    private void showDialog() {
        showDialog("请稍后...");
    }

    /**
     * 显示提示框
     *
     * @param msg 消息
     */
    private void showDialog(String msg) {
        if (dialog == null) {
            dialog = new SmallDialog(this, msg);
        } else {
            dialog.setContent(msg);
        }
        dialog.show();
    }

    /**
     * 隐藏提示框
     */
    private void hideDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public String getTitleCenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_get_my_location;
    }

    /**
     * 当定位发生变化
     *
     * @param amapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点

                // 获取地理位置信息
                currentAddress = /*amapLocation.getProvince() + */amapLocation.getCity() + amapLocation.getDistrict() + amapLocation.getRoad();
                currentLatitude = amapLocation.getLatitude();
                currentLongitude = amapLocation.getLongitude();
                initPoiAround(amapLocation);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void returnLocation(String address, double latitude, double longitude) {
        showDialog();
        Intent intent = getIntent();
        intent.putExtra("address", address);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        setResult(RESULT_OK, intent);
        ActivityGetMyLocation.this.finish();
    }

    /**
     * 初始化POI搜索
     *
     * @param amapLocation
     */
    private void initPoiAround(AMapLocation amapLocation) {
        String location = amapLocation.getProvince() + amapLocation.getCity() + amapLocation.getDistrict() + amapLocation.getRoad();
        headerStreet.setText(location);
//        listPoi.addHeaderView(headerView);
        doSearchQuery(amapLocation.getCity(), new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude()));
    }

    /**
     * 进行poi查询
     *
     * @param location
     */
    private void doSearchQuery(String city, LatLonPoint lp) {
        query = new PoiSearch.Query("", "公司企业|道路附属设施|地名地址信息|公共设施", city);
        query.setPageSize(10);
        query.setPageNum(0);

        poiSearch = new PoiSearch(this, query);
        // 设置搜索区域为以lp点为圆心，其周围2000米范围
        poiSearch.setBound(new PoiSearch.SearchBound(lp, 2000, true));
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();  // 异步搜索
    }

    /**
     * 激活定位
     *
     * @param listener
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置需要地理位置信息
            mLocationOption.isNeedAddress();
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mLocationOption.setOnceLocation(true);
            mlocationClient.startLocation();
        }
    }

    /**
     * 取消定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得第一页的poiitem数据，页数从数字0开始
                    poiItems = poiResult.getPois();
                    if (poiItems != null && poiItems.size() > 0) {
                        Log.e("sn_", poiItems.toString());
                        adapter = new PoiAdapter(this, poiItems);
                        listPoi.setAdapter(adapter);
                        listPoi.addHeaderView(headerView);
                    }
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            PoiItem poiItem = adapter.getItem((int)id);
            returnLocation(poiItem.getSnippet() + poiItem.getTitle(), poiItem.getLatLonPoint().getLatitude(),
                    poiItem.getLatLonPoint().getLongitude());
        }
    }
}

