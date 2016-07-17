package com.thinksns.sociax.t4.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Pair;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.thinksns.sociax.t4.android.Thinksns;
import com.thinksns.sociax.thinksnsbase.exception.ApiException;

/**
 * 类说明：
 *
 * @author Zoey
 * @version 1.0
 * @date 2015年10月21日
 */
public class UpdateLocationService extends Service
        implements AMapLocationListener {

    private static final int UPDATE_TIME = 30 * 60 * 1000;    // 半个小时定位一次并上传服务器

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLocation();
    }

    // 初始化位置
    public void initLocation() {
        locationClient = new AMapLocationClient(getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationOption.setInterval(UPDATE_TIME);
        // 设置定位监听
        locationClient.setLocationListener(this);

        // 设置定位参数
        locationClient.setLocationOption(locationOption);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 启动定位
        locationClient.startLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    // 发送当前位置
    public void sendNowLocation(final Pair<String, String> location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thinksns app = (Thinksns) getApplicationContext();
                try {
                    app.getFindPeopleApi().updateLocation(location.first, location.second);
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                Pair<String, String> location = Pair.create(
                        String.valueOf(aMapLocation.getLatitude()),
                        String.valueOf(aMapLocation.getLongitude()));
                sendNowLocation(location);
            }
        }
    }
}
