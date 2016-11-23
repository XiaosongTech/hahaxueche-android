package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.presenter.findCoach.FieldFilterPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.findCoach.FieldFilterView;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/10/17.
 */

public class FieldFilterActivity extends HHBaseActivity implements FieldFilterView, LocationSource, AMapLocationListener {
    private FieldFilterPresenter mPresenter;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private MarkerOptions markerOption;
    private AMap aMap;
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.tv_hints)
    TextView mTvHints;
    @BindView(R.id.tv_select_fields)
    TextView mTvSelectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new FieldFilterPresenter();
        setContentView(R.layout.activity_field_filter);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        mPresenter.attachView(this);
        Intent intent = getIntent();
        if (intent.getParcelableArrayListExtra("selectFields") != null) {
            ArrayList<Field> fields = intent.getParcelableArrayListExtra("selectFields");
            mPresenter.setSelectFields(fields);
        }
        mPresenter.initMap();
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("训练场地图");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FieldFilterActivity.this.finish();
            }
        });
    }

    @OnClick(R.id.tv_select_fields)
    public void click() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("selectFields", mPresenter.getSelectFields());
        setResult(RESULT_OK, intent);
        FieldFilterActivity.this.finish();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                HHLog.e(errText);
            }
        }
    }

    /**
     * 激活定位
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
    public void initMap(ArrayList<Field> fields) {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.setOnMarkerClickListener(mMarkerClickListener);// 设置点击marker事件监听器
        setFields(fields);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    private AMap.OnMarkerClickListener mMarkerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Field field = (Field) marker.getObject();
            if (mPresenter.selectField(field)) {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),
                                R.drawable.ic_map_local_choseon)));
            } else {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),
                                R.drawable.ic_map_local_choseoff)));
            }
            return false;
        }
    };

    @Override
    public void setHints(Spanned hints) {
        mTvHints.setText(hints);
    }

    @Override
    public void setSelectFieldText(String text) {
        mTvSelectText.setText(text);
    }

    public void setFields(ArrayList<Field> fields) {
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<>();
        for (Field field : fields) {
            markerOption = new MarkerOptions();
            LatLng x = new LatLng(field.lat, field.lng);
            markerOption.position(x);
            markerOption.title(field.name).snippet(field.street);

            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),
                            R.drawable.ic_map_local_choseoff)));
            markerOptionlst.add(markerOption);
        }
        if (aMap != null) {
            List<Marker> markerList = aMap.addMarkers(markerOptionlst, true);
            for (Marker marker : markerList) {
                Field field = fields.get(markerList.indexOf(marker));
                marker.setObject(field);
                if (mPresenter.containsSelectField(field)) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.drawable.ic_map_local_choseon)));
                }
            }
        }
    }
}
