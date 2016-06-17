package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gibxin on 2016/3/5.
 */
public class FindCoachMapFilterActivity extends FCBaseActivity implements LocationSource, AMapLocationListener {
    private AMap aMap;
    private MapView mapView;
    private TextView tvFcmHint1;
    private TextView tvFcmHint2;
    private TextView tvSelFieldMap;
    private MarkerOptions markerOption;
    private ImageButton ibtnFcmBack;
    private ArrayList<FieldModel> selFieldList = new ArrayList<>();
    private ArrayList<FieldModel> fieldList = new ArrayList<>();
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_coach_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        init();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        tvFcmHint1 = (TextView) findViewById(R.id.tv_fcm_hint1);
        tvFcmHint1.setText(Html.fromHtml(getResources().getString(R.string.fCMapSelectHint1)));
        tvFcmHint2 = (TextView) findViewById(R.id.tv_fcm_hint2);
        tvFcmHint2.setText(Html.fromHtml(getResources().getString(R.string.fCMapSelectHint2)));
        ibtnFcmBack = (ImageButton) findViewById(R.id.ibtn_fcm_back);
        ibtnFcmBack.setOnClickListener(mClickListener);
        tvSelFieldMap = (TextView) findViewById(R.id.tv_sel_field_map);
        initFieldList();
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        tvSelFieldMap.setOnClickListener(mClickListener);
    }

    private void setUpMap() {
        aMap.setOnMarkerClickListener(mMarkerClickListener);// 设置点击marker事件监听器
        addMarkersToMap();// 往地图上添加marker
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    private AMap.OnMarkerClickListener mMarkerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (aMap != null) {
                FieldModel fieldModel = (FieldModel) marker.getObject();
                if (selFieldList == null) {
                    selFieldList = new ArrayList<>();
                }
                int selIndex = -1;
                for (FieldModel selField : selFieldList) {
                    if (selField.getId().equals(fieldModel.getId())) {
                        selIndex = selFieldList.indexOf(selField);
                        break;
                    }
                }
                if (selIndex > -1) {
                    selFieldList.remove(selIndex);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.drawable.ic_map_local_choseoff)));
                } else {
                    selFieldList.add(fieldModel);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.drawable.ic_map_local_choseon)));
                }

            }
            tvSelFieldMap.setText("查看训练场（已选" + selFieldList.size() + "）个");
            return false;
        }
    };


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

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        for (FieldModel fieldModel : fieldList) {
            markerOption = new MarkerOptions();
            LatLng x = new LatLng(Double.parseDouble(fieldModel.getLat()), Double.parseDouble(fieldModel.getLng()));
            markerOption.position(x);
            markerOption.title(fieldModel.getName()).snippet(fieldModel.getStreet());

            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),
                            R.drawable.ic_map_local_choseoff)));
            markerOptionlst.add(markerOption);
        }
        if (aMap != null) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            selFieldList = (ArrayList<FieldModel>) bundle.getSerializable("selFieldList");
            List<Marker> markerList = aMap.addMarkers(markerOptionlst, true);
            for (Marker marker : markerList) {
                FieldModel field = fieldList.get(markerList.indexOf(marker));
                marker.setObject(field);
                if (selFieldList != null && selFieldList.size() > 0) {
                    for (FieldModel selField : selFieldList) {
                        if (selField.getId().equals(field.getId())) {
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources(),
                                            R.drawable.ic_map_local_choseon)));
                        }
                    }
                }
            }
        }
        if (selFieldList != null && selFieldList.size() > 0) {
            tvSelFieldMap.setText("查看训练场（已选" + selFieldList.size() + "）个");
        }
    }


    private void initFieldList() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        String city_id = spUtil.getMyCity().getId();
        Constants constants = spUtil.getConstants();
        if (constants != null && constants.getFields() != null && constants.getFields().size() > 0) {
            for (FieldModel fieldsModel : constants.getFields()) {
                if (fieldsModel.getCity_id().equals(city_id)) {
                    fieldList.add(fieldsModel);
                    continue;
                }
            }
        }
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ibtn_fcm_back:
                    FindCoachMapFilterActivity.this.finish();
                    break;
                case R.id.tv_sel_field_map:
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("selFieldList", selFieldList);
                    intent.putExtras(bundle);
                    Log.v("gibxin", "setResult -> " + RESULT_OK);
                    setResult(RESULT_OK, intent);
                    FindCoachMapFilterActivity.this.finish();
                    break;
            }
        }
    };

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
                Log.e("AmapErr", errText);
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


}
