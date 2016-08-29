package com.hahaxueche.ui.activity.findCoach;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.hahaxueche.R;
import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.city.Location;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.net.URISyntaxException;
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
    private TextView mTvGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_map);
        Intent intent = getIntent();
        mFieldModel = (FieldModel) intent.getExtras().getSerializable("fieldModel");
        mapView = (MapView) findViewById(R.id.field_map);
        mTvGuide = (TextView) findViewById(R.id.tv_guide);
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
        mTvGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FieldMapActivity.this);
                builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("导航");
                //    指定下拉列表的显示数据
                final String[] guides = {"高德", "百度"};
                //    设置一个下拉的列表选择项
                builder.setItems(guides, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (guides[which].equals("高德")) {
                            startAMap();
                        } else if (guides[which].equals("百度")) {
                            startBaiduMap();
                        }
                    }
                });
                builder.show();
            }
        });
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
        //设定初始可视区域
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mFieldModel.getLat()), Double.parseDouble(mFieldModel.getLng())), 14));
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

    /**
     * 启动高德地图导航
     */
    private void startAMap() {
        if (Util.instence(this).isApkInstall(this, "com.autonavi.minimap")) {
            try {
                Intent intent = Intent.getIntent("androidamap://viewMap?sourceApplication=哈哈学车&poiname=" +
                        mFieldModel.getName() + "&lat=" +
                        mFieldModel.getLat() + "&lon=" +
                        mFieldModel.getLng() + "&dev=0");
                startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "请安装高德地图", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 启动百度地图导航
     */
    private void startBaiduMap() {
        if (Util.instence(this).isApkInstall(this, "com.baidu.BaiduMap")) {
            try {
                Intent intent = Intent.getIntent("intent://map/marker?coord_type=gcj02&location="
                        + mFieldModel.getLat() + "," + mFieldModel.getLng() + "&title=" +
                        mFieldModel.getName() + "&content=" +
                        mFieldModel.getDescription() + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "请安装百度地图", Toast.LENGTH_SHORT).show();
        }
    }
}
