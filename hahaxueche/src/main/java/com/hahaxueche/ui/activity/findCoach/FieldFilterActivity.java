package com.hahaxueche.ui.activity.findCoach;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.findCoach.FieldFilterPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.homepage.MapCoachAdapter;
import com.hahaxueche.ui.dialog.homepage.GetUserIdentityDialog;
import com.hahaxueche.ui.view.findCoach.FieldFilterView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.WebViewUrl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/17.
 */

public class FieldFilterActivity extends HHBaseActivity implements FieldFilterView, LocationSource,
        AMapLocationListener, AMap.InfoWindowAdapter {
    private FieldFilterPresenter mPresenter;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMap aMap;
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.rcy_map_coach)
    RecyclerView mRcyMapCoach;
    @BindView(R.id.rly_main)
    RelativeLayout mRlyMain;

    private MapCoachAdapter mapCoachAdapter;
    private Field mSelectField;
    private ArrayList<Marker> markerList;
    private String cellPhone;

    private ArrayList<Field> mHighlightFields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new FieldFilterPresenter();
        setContentView(R.layout.activity_field_filter);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        mPresenter.attachView(this);
        initActionBar();
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        mRcyMapCoach.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        if (intent.getParcelableExtra("field") != null) {
            mHighlightFields.add((Field) intent.getParcelableExtra("field"));
            mSelectField = intent.getParcelableExtra("field");
        }
        mPresenter.getFields();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("训练场/驾校教练");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FieldFilterActivity.this.finish();
            }
        });
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
                if (mHighlightFields.size() == 0) {
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                }
                mlocationClient.stopLocation();
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
    public void initMap(List<Field> fields) {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.setOnMarkerClickListener(mMarkerClickListener);// 设置点击marker事件监听器
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                if (mSelectField != null) {
                    //设定初始可视区域
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mSelectField.lat, mSelectField.lng), 14));
                    mPresenter.selectField(mSelectField);
                }
            }
        });
        initFields(fields);
        if (mHighlightFields.size() == 0) {
            aMap.setLocationSource(this);// 设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        }
    }

    @Override
    public void loadCoaches(ArrayList<Coach> coaches) {
        mapCoachAdapter = new MapCoachAdapter(this, coaches, new MapCoachAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onDrivingSchoolClick(String drivingSchoolId) {
                mPresenter.addDataTrack("map_view_page_check_school_tapped", getContext());
                openWebView(WebViewUrl.WEB_URL_JIAXIAO + "/" + drivingSchoolId);
            }

            @Override
            public void onCoachDetailClick(Coach coach) {
                mPresenter.addDataTrack("map_view_page_check_coach_tapped", getContext());
                Intent intent = new Intent(getContext(), CoachDetailActivity.class);
                intent.putExtra("coach", coach);
                startActivity(intent);
            }

            @Override
            public void onCheckFieldClick() {
                mPresenter.addDataTrack("map_view_page_check_site_tapped", getContext());
                GetUserIdentityDialog dialog = new GetUserIdentityDialog(getContext(), "看过训练场才放心！",
                        "输入手机号，教练立即带你看场地", "预约看场地", new GetUserIdentityDialog.OnIdentityGetListener() {
                    @Override
                    public void getCellPhone(String cellPhone) {
                        mPresenter.addDataTrack("map_view_page_locate_confirmed", getContext());
                        mPresenter.getUserIdentity(cellPhone);
                    }
                });
                dialog.show();
            }

            @Override
            public void onCustomerServiceClick() {
                mPresenter.addDataTrack("map_view_page_online_support_tapped", getContext());
                mPresenter.onlineAsk();
            }

            @Override
            public void onContactCoachClick(Coach coach) {
                mPresenter.addDataTrack("map_view_page_contact_coach_tapped", getContext());
                cellPhone = coach.consult_phone;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    callMyCoach();
                }
            }
        });
        mRcyMapCoach.setAdapter(mapCoachAdapter);
    }

    private AMap.OnMarkerClickListener mMarkerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            for (Marker existMarker : markerList) {
                Field existField = (Field) existMarker.getObject();
                if (isHighlightField(existField)) {
                    existMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.drawable.ic_map_local_choseonly)));
                } else {
                    existMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.drawable.ic_map_local_choseoff)));
                }
            }
            Field field = (Field) marker.getObject();
            if (mSelectField != null && mSelectField.id.equals(field.id)) {
                mSelectField = null;
                marker.hideInfoWindow();
                hideCoachesView();
            } else {
                mSelectField = field;
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),
                                R.drawable.ic_map_local_choseon)));
                marker.showInfoWindow();
                mPresenter.selectField(mSelectField);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(field.lat, field.lng), 14));
            }
            return true;
        }
    };

    private void initFields(List<Field> fields) {
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<>();
        markerList = new ArrayList<>();
        for (Field field : fields) {
            MarkerOptions markerOption = new MarkerOptions();
            LatLng x = new LatLng(field.lat, field.lng);
            markerOption.position(x);
            markerOption.title(field.name).snippet(field.display_address);
            //markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),
                            R.drawable.ic_map_local_choseoff)));
            markerOptionlst.add(markerOption);
        }
        if (aMap != null) {
            markerList = aMap.addMarkers(markerOptionlst, true);
            for (Marker marker : markerList) {
                Field field = fields.get(markerList.indexOf(marker));
                marker.setObject(field);
                if (isHighlightField(field)) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.drawable.ic_map_local_choseonly)));
                }
                if (mSelectField != null && mSelectField.id.equals(field.id)) {
                    marker.showInfoWindow();
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.drawable.ic_map_local_choseon)));
                }
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow = getLayoutInflater().inflate(
                R.layout.info_window_field, null);
        render(marker, infoWindow);
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * 自定义infowinfow窗口
     */
    private void render(Marker marker, View view) {
        Field field = (Field) marker.getObject();
        SimpleDraweeView mIvFieldAvatar = ButterKnife.findById(view, R.id.iv_field_avatar);
        mIvFieldAvatar.setImageURI(field.image);
        TextView tvFieldName = ButterKnife.findById(view, R.id.tv_field_name);
        tvFieldName.setText(field.name);
        TextView tvDisplayAddress = ButterKnife.findById(view, R.id.tv_display_address);
        String text = (TextUtils.isEmpty(field.display_address) ? "" : field.display_address) +
                " (" + field.coach_count + "名教练)";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.haha_red_text)),
                text.indexOf("(") + 1, text.indexOf("名教练"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvDisplayAddress.setText(ss);
        TextView tvSendLocation = ButterKnife.findById(view, R.id.tv_send_location);
        tvSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addDataTrack("map_view_page_locate_tapped", getContext());
                GetUserIdentityDialog dialog = new GetUserIdentityDialog(getContext(), "轻松定位训练场",
                        "输入手机号，立即接收详细地址", "发我定位", new GetUserIdentityDialog.OnIdentityGetListener() {
                    @Override
                    public void getCellPhone(String cellPhone) {
                        mPresenter.addDataTrack("map_view_page_check_site_confirmed", getContext());
                        mPresenter.getUserIdentity(cellPhone);
                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * 联系教练
     */
    private void callMyCoach() {
        if (TextUtils.isEmpty(cellPhone))
            return;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + cellPhone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                callMyCoach();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系教练");
            }
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mRlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showCoachesView() {
        mRcyMapCoach.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCoachesView() {
        mRcyMapCoach.setVisibility(View.GONE);
    }


    private boolean isHighlightField(Field field) {
        if (mHighlightFields.size() == 0) return false;
        boolean isExist = false;
        for (Field highlightField : mHighlightFields) {
            if (highlightField.id.equals(field.id)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }
}
