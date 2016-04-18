package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.base.ConstantsModel;
import com.hahaxueche.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gibxin on 2016/3/5.
 */
public class FindCoachMapFilterActivity extends FCBaseActivity implements AMap.OnMarkerClickListener {
    private AMap aMap;
    private MapView mapView;
    private TextView tvFcmHint1;
    private TextView tvFcmHint2;
    private TextView tvSelFieldMap;
    private MarkerOptions markerOption;
    private ImageButton ibtnFcmBack;
    private ArrayList<FieldModel> selFieldList = new ArrayList<>();
    private ArrayList<FieldModel> fieldList = new ArrayList<>();

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
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
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
        List<Marker> markerList = aMap.addMarkers(markerOptionlst, true);
        for (Marker marker : markerList) {
            marker.setObject(fieldList.get(markerList.indexOf(marker)));
        }
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (aMap != null) {
            FieldModel fieldModel = (FieldModel) marker.getObject();
            if (selFieldList.indexOf(fieldModel) > -1) {
                selFieldList.remove(fieldModel);
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

    private void initFieldList() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        String city_id = spUtil.getStudent().getCity_id();
        ConstantsModel constants = spUtil.getConstants();
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

}
