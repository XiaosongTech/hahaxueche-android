package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.FieldModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gibxin on 2016/3/6.
 */
public class FieldMapActivity extends FCBaseActivity implements AMap.OnMarkerClickListener {
    private AMap aMap;
    private MapView mapView;
    private MarkerOptions markerOption;
    private ImageButton ibtnFieldMapBack;
    private FieldModel mFieldModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_map);
        Intent intent = getIntent();
        mFieldModel = (FieldModel) intent.getExtras().getSerializable("fieldModel");
        mapView = (MapView) findViewById(R.id.field_map);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        init();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        ibtnFieldMapBack = (ImageButton) findViewById(R.id.ibtn_field_map_back);
        ibtnFieldMapBack.setOnClickListener(mClickListener);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        addMarkersToMap();// 往地图上添加marker
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
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        markerOption = new MarkerOptions();
        LatLng x = new LatLng(Double.parseDouble(mFieldModel.getLat()), Double.parseDouble(mFieldModel.getLng()));
        markerOption.position(x);
        markerOption.title(mFieldModel.getName()).snippet(mFieldModel.getStreet());

        markerOption.draggable(false);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),
                        R.drawable.ic_map_local_choseon)));
        markerOptionlst.add(markerOption);
        List<Marker> markerList = aMap.addMarkers(markerOptionlst, true);
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        return false;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ibtn_field_map_back:
                    FieldMapActivity.this.finish();
                    break;
            }
        }
    };
}
