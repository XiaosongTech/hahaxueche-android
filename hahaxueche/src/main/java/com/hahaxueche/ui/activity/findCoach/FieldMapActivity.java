package com.hahaxueche.ui.activity.findCoach;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.presenter.findCoach.FieldMapPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.findCoach.FieldMapView;
import com.hahaxueche.util.Utils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/26.
 */

public class FieldMapActivity extends HHBaseActivity implements FieldMapView,  AMap.OnMarkerClickListener {
    private FieldMapPresenter mPresenter;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvGuide;
    private MarkerOptions markerOption;
    private AMap aMap;
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.frl_main)
    FrameLayout mFrlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new FieldMapPresenter();
        setContentView(R.layout.activity_field_map);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        mPresenter.attachView(this);
        Intent intent = getIntent();
        if (intent.getParcelableExtra("field") != null) {
            mPresenter.setField((Field) intent.getParcelableExtra("field"));
            if (aMap == null) {
                aMap = mapView.getMap();
                addMarkersToMap();
            }
        }
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_field_map);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        mTvGuide = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_guide);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("训练场地图");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FieldMapActivity.this.finish();
            }
        });
        mTvGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FieldMapActivity.this);
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

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        markerOption = new MarkerOptions();
        Field field = mPresenter.getField();
        LatLng x = new LatLng(field.lat, field.lng);
        markerOption.position(x);
        markerOption.title(field.name).snippet(field.street);

        markerOption.draggable(false);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),
                        R.drawable.ic_map_local_choseon)));
        markerOptionlst.add(markerOption);
        List<Marker> markerList = aMap.addMarkers(markerOptionlst, true);
        //设定初始可视区域
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(field.lat, field.lng), 14));
    }

    /**
     * 启动高德地图导航
     */
    private void startAMap() {
        Field field = mPresenter.getField();
        if (Utils.instence(this).isApkInstall(this, "com.autonavi.minimap")) {
            try {
                Intent intent = Intent.getIntent("androidamap://viewMap?sourceApplication=哈哈学车&poiname=" +
                        field.name + "&lat=" +
                        field.lat + "&lon=" +
                        field.lng + "&dev=0");
                startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            showMessage("请安装高德地图");
        }
    }

    /**
     * 启动百度地图导航
     */
    private void startBaiduMap() {
        Field field = mPresenter.getField();
        if (Utils.instence(this).isApkInstall(this, "com.baidu.BaiduMap")) {
            try {
                Intent intent = Intent.getIntent("intent://map/marker?coord_type=gcj02&location="
                        + field.lat + "," + field.lng + "&title=" +
                        field.name + "&content=" +
                        field.description + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            showMessage("请安装百度地图");
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mFrlMain, message, Snackbar.LENGTH_SHORT).show();
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
        mPresenter.detachView();
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
