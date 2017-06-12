package com.hahaxueche.ui.activity.homepage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Marker;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.cluster.Cluster;
import com.hahaxueche.model.cluster.ClusterListener;
import com.hahaxueche.model.cluster.ClusterOverlay;
import com.hahaxueche.model.cluster.ClusterRender;
import com.hahaxueche.model.cluster.FieldItem;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.homepage.MapSearchPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.activity.findCoach.DrivingSchoolDetailActivity;
import com.hahaxueche.ui.activity.findCoach.SearchCoachActivity;
import com.hahaxueche.ui.adapter.homepage.MapCoachAdapter;
import com.hahaxueche.ui.dialog.homepage.GetUserIdentityDialog;
import com.hahaxueche.ui.popupWindow.findCoach.ZonePopupWindow;
import com.hahaxueche.ui.popupWindow.homepage.DrivingSchoolPopupWindow;
import com.hahaxueche.ui.view.homepage.MapSearchView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2017/5/16.
 */

public class MapSearchActivity extends HHBaseActivity implements MapSearchView, LocationSource,
        AMapLocationListener, AMap.InfoWindowAdapter, ClusterRender, ClusterListener {
    private MapSearchPresenter mPresenter;
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
    @BindView(R.id.tv_zone)
    TextView mTvZone;
    @BindView(R.id.tv_driving_school)
    TextView mTvDrivingSchool;
    @BindView(R.id.fly_bg_half_trans)
    FrameLayout mFlyBgHalfTrans;

    private MapCoachAdapter mapCoachAdapter;
    private ZonePopupWindow mZonePopWindow;
    private DrivingSchoolPopupWindow mDrivingSchoolPopWindow;
    private Map<Integer, Drawable> mBackDrawAbles = new HashMap<>();
    private ClusterOverlay mClusterOverlay;

    private String cellPhone;
    private final int POP_ZONE = 0;
    private final int POP_DRIVING_SCHOOL = 1;
    private DrivingSchool mInfoWindowDrivingSchool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MapSearchPresenter();
        setContentView(R.layout.activity_map_search);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        mPresenter.attachView(this);
        initActionBar();
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        mRcyMapCoach.setLayoutManager(layoutManager);
        //地图初始化
        initMap();
        int drivingSchoolId = getIntent().getIntExtra("drivingSchoolId", -1);
        if (drivingSchoolId >= 0) {
            mPresenter.setDrivingSchoolId(drivingSchoolId);
        } else {
            mPresenter.getFields();
        }
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.setInfoWindowAdapter(this);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_map_search);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("地图找驾校");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapSearchActivity.this.finish();
            }
        });
        ImageView mIvSearch = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_search);
        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchCoachActivity.class));
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
        mClusterOverlay.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @OnClick({R.id.fly_zone,
            R.id.fly_driving_school})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fly_zone:
                if (mZonePopWindow == null) {
                    mZonePopWindow = new ZonePopupWindow(this, new ZonePopupWindow.OnZoneClickListener() {
                        @Override
                        public void selectNoLimit() {
                            mPresenter.setDistance(Common.NO_LIMIT);
                        }

                        @Override
                        public void selectBusinessArea(String businessArea) {
                            mPresenter.setBusinessArea(businessArea);
                        }

                        @Override
                        public void selectDistance(int distance) {
                            mPresenter.setDistance(distance);
                        }

                        @Override
                        public void selectZone(String zone) {
                            mPresenter.setZone(zone);
                        }

                        @Override
                        public void dismiss() {
                            hidePopWindow();
                        }
                    }, mPresenter.getZoneDetails(getContext()), mPresenter.getRadius(getContext()));
                }
                mZonePopWindow.showAsDropDown(view);
                showPopWindow(POP_ZONE);
                break;
            case R.id.fly_driving_school:
                if (mDrivingSchoolPopWindow == null) {
                    mDrivingSchoolPopWindow = new DrivingSchoolPopupWindow(this, new DrivingSchoolPopupWindow.OnDrivingSchoolClickListener() {
                        @Override
                        public void selectNoLimit() {
                            mPresenter.setDrivingSchoolId(Common.NO_LIMIT);
                        }

                        @Override
                        public void selectDrivingSchool(int drivingSchoolId) {
                            mPresenter.setDrivingSchoolId(drivingSchoolId);
                        }

                        @Override
                        public void dismiss() {
                            hidePopWindow();
                        }
                    }, mPresenter.getDrivingSchools(getContext()));
                }
                mDrivingSchoolPopWindow.showAsDropDown(view);
                showPopWindow(POP_DRIVING_SCHOOL);
                break;
            default:
                break;
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
                mPresenter.setLocation(amapLocation.getLatitude(), amapLocation.getLongitude());
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

    @Override
    public void loadFields(List<FieldItem> fieldItems) {
        boolean isInitLoadPoint = !TextUtils.isEmpty(mPresenter.getSelectZone());
        mClusterOverlay = new ClusterOverlay(aMap, fieldItems, getContext(), isInitLoadPoint);
        mClusterOverlay.setClusterRenderer(this);
        mClusterOverlay.setClusterListener(this);
        mClusterOverlay.calculateClusters();
        mClusterOverlay.moveCamera();
    }

    @Override
    public void loadCoaches(List<Coach> coaches, int[] drivingSchoolIds) {
        mapCoachAdapter = new MapCoachAdapter(this, coaches, drivingSchoolIds, new MapCoachAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onDrivingSchoolClick(int drivingSchoolId) {
                mPresenter.addDataTrack("map_view_page_check_school_tapped", getContext());
                Intent intent = new Intent(getContext(), DrivingSchoolDetailActivity.class);
                intent.putExtra("drivingSchoolId", drivingSchoolId);
                startActivity(intent);
            }

            @Override
            public void onCoachDetailClick(Coach coach) {
                mPresenter.addDataTrack("map_view_page_check_coach_tapped", getContext());
                Intent intent = new Intent(getContext(), CoachDetailActivity.class);
                intent.putExtra("coach", coach);
                startActivity(intent);
            }

            @Override
            public void onCheckFieldClick(final Coach coach) {
                mPresenter.addDataTrack("map_view_page_check_site_tapped", getContext());
                GetUserIdentityDialog dialog = new GetUserIdentityDialog(getContext(), "看过训练场才放心！",
                        "输入手机号，教练立即带你看场地", "预约看场地", new GetUserIdentityDialog.OnIdentityGetListener() {
                    @Override
                    public void getCellPhone(String cellPhone) {
                        mPresenter.addDataTrack("map_view_page_locate_confirmed", getContext());
                        mPresenter.checkField(cellPhone, coach);
                    }
                });
                dialog.show();
            }

            @Override
            public void onSendLocationClick(final Coach coach) {
                GetUserIdentityDialog dialog = new GetUserIdentityDialog(getContext(), "轻松定位训练场",
                        "输入手机号，立即接收详细地址", "发我定位", new GetUserIdentityDialog.OnIdentityGetListener() {
                    @Override
                    public void getCellPhone(String cellPhone) {
                        mPresenter.addDataTrack("map_view_page_check_site_confirmed", getContext());
                        mPresenter.checkField(cellPhone, coach);
                    }
                });
                dialog.show();
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

    @Override
    public void setInfoWindowDrivingSchool(DrivingSchool drivingSchool, Marker marker) {
        mInfoWindowDrivingSchool = drivingSchool;
        marker.showInfoWindow();
    }

    /**
     * 自定义infowinfow窗口
     */
    private void render(Marker marker, View view) {
        Cluster cluster = (Cluster) marker.getObject();
        if (cluster.isFieldPoint()) {
            final Field field = cluster.getFieldItems().get(0).getField();
            SimpleDraweeView mIvFieldAvatar = ButterKnife.findById(view, R.id.iv_field_avatar);
            if (mInfoWindowDrivingSchool != null) {
                mIvFieldAvatar.setImageURI(mInfoWindowDrivingSchool.avatar);
            } else {
                mIvFieldAvatar.setImageURI(field.image);
            }
            TextView tvFieldName = ButterKnife.findById(view, R.id.tv_field_name);
            if (mInfoWindowDrivingSchool != null) {
                tvFieldName.setText(mInfoWindowDrivingSchool.name);
            } else {
                tvFieldName.setText(field.name);
            }
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
                            mPresenter.sendLocation(cellPhone, field);
                        }
                    });
                    dialog.show();
                }
            });
        }
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

    /**
     * 显示下拉窗口
     *
     * @param order
     */
    private void showPopWindow(int order) {
        if (order == POP_ZONE) {
            mTvZone.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_orange), null);
        } else if (order == POP_DRIVING_SCHOOL) {
            mTvDrivingSchool.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_orange), null);
        }
        mFlyBgHalfTrans.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏下拉窗口
     */
    private void hidePopWindow() {
        mTvZone.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_gray), null);
        mTvDrivingSchool.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_gray), null);
        mFlyBgHalfTrans.setVisibility(View.GONE);
    }

    @Override
    public Drawable getDrawAble(int clusterNum, String clusterName, boolean isFieldPoint) {
        int radius = getResources().getDimensionPixelSize(R.dimen.cluster_radius);
        if (isFieldPoint) {
            Drawable bitmapDrawable = mBackDrawAbles.get(1);
            if (bitmapDrawable == null) {
                bitmapDrawable =
                        getApplication().getResources().getDrawable(
                                R.drawable.ic_map_local_choseon);
                mBackDrawAbles.put(1, bitmapDrawable);
            }
            return bitmapDrawable;
        } else {
            Drawable bitmapDrawable = mBackDrawAbles.get(2);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(255, 249, 111, 109)));
                mBackDrawAbles.put(2, bitmapDrawable);
            }
            return bitmapDrawable;
        }
    }

    @Override
    public void onClickCluster(Marker marker, Cluster cluster) {
        String zone = cluster.getZoneName();
        mPresenter.setZone(zone);
    }

    @Override
    public void onClickField(Field field, boolean isSelect) {
        if (isSelect) {
            mPresenter.selectField(field);
        } else {
            hideCoachesView();
        }
    }

    @Override
    public void onZoomToCity() {
        mPresenter.zoomToCity();
    }

    @Override
    public void showInfoWindow(Marker marker) {
        Cluster cluster = (Cluster) marker.getObject();
        if (cluster.isFieldPoint()) {
            Field field = cluster.getFieldItems().get(0).getField();
            if (field.driving_school_ids.length == 1) {
                //训练场的驾校数==1，info window显示驾校
                mInfoWindowDrivingSchool = null;
                mPresenter.getDrivingSchool(field.driving_school_ids[0], marker);
                return;
            }
        }
        marker.showInfoWindow();
    }

    @Override
    public void setInfoDrivingSchoolNull() {
        mInfoWindowDrivingSchool = null;
    }

    private Bitmap drawCircle(int radius, int color) {
        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        RectF rectF = new RectF(0, 0, radius * 2, radius * 2);
        paint.setColor(color);
        canvas.drawArc(rectF, 0, 360, true, paint);

        Paint paintWhite = new Paint();
        paintWhite.setColor(ContextCompat.getColor(this, R.color.haha_white));
        paintWhite.setStyle(Paint.Style.STROKE);
        paintWhite.setStrokeWidth(Utils.instence(this).dip2px(2));
        canvas.drawArc(rectF, 0, 360, false, paintWhite);

        return bitmap;
    }
}
