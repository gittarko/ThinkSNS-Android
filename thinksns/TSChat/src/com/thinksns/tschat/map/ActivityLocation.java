package com.thinksns.tschat.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.thinksns.tschat.R;
import com.thinksns.tschat.api.MessageApi;
import com.thinksns.tschat.api.RequestResponseHandler;
import com.thinksns.tschat.constant.TSConfig;
import com.thinksns.tschat.widget.SmallDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hedong on 16/1/19.
 */
public class ActivityLocation extends Activity implements LocationSource, AMapLocationListener,
        AMap.OnMapLoadedListener, AMap.OnMapScreenShotListener, View.OnClickListener{
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private ImageView iv_back;
    private TextView tv_send, tv_title;
    private SmallDialog dialog;
    private LinearLayout ll_location_info;
    private TextView tv_address;

    //地图首次坐标定位
    private boolean isFirst = true;
    private double latitude, longitude; //定位详细经纬度
    private String address = "";    //定位详细地址
    private int room_id;
    private boolean shareLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.locationsource_activity);

        mapView = (MapView)findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);   //此方法必须重写

        dialog = new SmallDialog(this, "请稍后...");
        dialog.setCanceledOnTouchOutside(false);


        initIntent();
        initMap();
        initView();
    }

    private void initIntent() {
        room_id = getIntent().getIntExtra("room_id", -1);
        latitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
        if (latitude == 0) {
            shareLocation = true;
        } else {
            longitude = getIntent().getDoubleExtra("longitude",0.1);
            address = getIntent().getStringExtra("address");
            shareLocation = false;
        }

    }

    private void initMap() {
        if(aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void initView() {
        tv_send = (TextView)findViewById(R.id.tv_send);
        tv_send.setEnabled(false);
        tv_send.setAlpha(0.5f);
        tv_send.setOnClickListener(this);

        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        ll_location_info = (LinearLayout)findViewById(R.id.ll_location_info);
        tv_address = (TextView)findViewById(R.id.tv_address);

        tv_title = (TextView)findViewById(R.id.tv_title_center);
        if(!shareLocation) {
            tv_title.setText("查看位置");
            ll_location_info.setVisibility(View.VISIBLE);
            tv_address.setText(address);
            tv_send.setVisibility(View.GONE);
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        if(shareLocation) {
            setMyLocation();
        }else {
            addMarkersToMap();
        }
        aMap.setOnMapLoadedListener(this);
    }

    private void setMyLocation() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.redpin));// 设置小蓝点的图标
//        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }

    private void addMarkersToMap() {
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(
                new LatLng(latitude, longitude)));
        CameraUpdateFactory.zoomTo(18);

        //设置marker
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(latitude, longitude));
        markerOption.title(address).snippet(address + ": " + latitude + ", " + longitude);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.redpin));
        markerOption.draggable(true);
        aMap.addMarker(markerOption);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if(shareLocation)
            deactivate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 开启定位
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
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
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                if(isFirst) {
                    isFirst = false;
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(
                            new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    CameraUpdateFactory.zoomTo(18);

                    latitude = amapLocation.getLatitude();
                    longitude = amapLocation.getLongitude();
                    address = amapLocation.getAddress();
                    Log.v("ActivityLocation", "当前定位结果:" + latitude + ", " + longitude + ", " + address);

                }
                tv_send.setEnabled(true);
                tv_send.setAlpha(1.0f);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
                Toast.makeText(this, "定位失败,请检查是否打开手机定位功能", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onMapLoaded() {
        //设置缩放级别
        CameraUpdateFactory.zoomTo(18);
    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        if(null == bitmap){
            dialog.setContent("获取位置截图数据失败");
            tv_send.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    tv_send.setEnabled(false);
                }
            }, 1000);
            return;
        }

        String error = "";
        try {
            String path = Environment.getExternalStorageDirectory() + "/" + TSConfig.CACHE_PATH ;
            File file = new File(path);
            if(!file.exists()) {
                //创建目录
                file.mkdirs();
            }

            path = path + "/location_" + sdf.format(new Date()) + ".png";
            FileOutputStream fos = new FileOutputStream(path);
            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(b) {
                //发送位置
                if(!address.isEmpty()) {
                    Intent intent = new Intent();
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("location", address);
                    intent.putExtra("path", path);
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }else {
                    error = "获取位置信息失败,请重新定位";
                }
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            error = "保存位置文件路径错误";
        }

        dialog.setContent(error);
        tv_send.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                tv_send.setEnabled(true);
            }
        }, 1000);
    }

    /**
     * 上传地图
     *
     * @param path
     *   地图保存路径
     */
    protected void upLoadMapThread(final String path) {
        MessageApi.uploadImageMessage(room_id, path, new RequestResponseHandler() {
            @Override
            public void onSuccess(Object result) {
                String attach_id = result.toString();
                Intent intent = new Intent();
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("location", address);
                intent.putExtra("attach_id", attach_id);
                setResult(RESULT_OK, intent);
                dialog.dismiss();
                finish();
            }

            @Override
            public void onFailure(Object errorResult) {
                if (errorResult != null)
                    Log.e("ActivityLocation", "上传位置至服务器失败：" + errorResult.toString());
                dialog.setContent("");
                tv_send.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        tv_send.setEnabled(true);
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.tv_send) {
            //获取地图截图并发送
            aMap.getMapScreenShot(this);
            //刷新地图
            mapView.invalidate();
            v.setEnabled(false);
        }else if(id == R.id.iv_back) {
            finish();
        }
    }
}
