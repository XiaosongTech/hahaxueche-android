package com.hahaxueche.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hahaxueche.model.city.Location;
import com.hahaxueche.utils.SharedPreferencesUtil;

/**
 * Created by wangshirui on 16/7/11.
 */
public class LocationService extends Service {
    //定位client
    public AMapLocationClient mLocationClient;
    //定位回调监听器
    public AMapLocationListener mLocationListener;
    //定位参数
    private AMapLocationClientOption mLocationOption;
    private SharedPreferencesUtil spUtil;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("gibxin","startLocationService");
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        spUtil = new SharedPreferencesUtil(getApplicationContext());
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        double lat = amapLocation.getLatitude();//获取纬度
                        double lng = amapLocation.getLongitude();//获取经度
                        Log.v("gibxin", "location : lat -> " + lat + " ; lng -> " + lng);
                        Location location = new Location();
                        location.setLat(lat + "");
                        location.setLng(lng + "");
                        spUtil.setLocation(location);
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("gibxin", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }
            }
        };
        mLocationClient.setLocationListener(mLocationListener);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000 * 10);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        Log.v("gibxin", "create location service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("gibxin", "start location service");
        //启动定位
        mLocationClient.startLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        Log.v("gibxin", "stop location service");
        mLocationClient.stopLocation();//停止定位
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端。
        Log.v("gibxin", "destroy location service");
    }
}