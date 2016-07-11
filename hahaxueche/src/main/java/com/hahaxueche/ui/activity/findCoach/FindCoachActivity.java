package com.hahaxueche.ui.activity.findCoach;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.city.Location;
import com.hahaxueche.model.city.City;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.service.LocationService;
import com.hahaxueche.ui.adapter.findCoach.CoachItemAdapter;
import com.hahaxueche.ui.dialog.FcFilterDialog;
import com.hahaxueche.ui.dialog.FcSortDialog;
import com.hahaxueche.ui.activity.appointment.AppointmentActivity;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.activity.mySetting.MySettingActivity;
import com.hahaxueche.ui.dialog.findCoach.SearchCoachDialog;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by gibxin on 2016/1/27.
 */
public class FindCoachActivity extends FCBaseActivity implements XListView.IXListViewListener {
    private LinearLayout llyTabIndex;
    private LinearLayout llyTabFindCoach;
    private LinearLayout llyTabAppointment;
    private LinearLayout llyTabMySetting;
    private LinearLayout llyFcFilter;
    private LinearLayout llyFcSort;
    private FcFilterDialog fcFilterDialog;
    private FcSortDialog fcSortDialog;
    private XListView xlvCoachList;
    private CoachItemAdapter mAdapter;
    private ArrayList<Coach> coachList = new ArrayList<Coach>();
    private Handler mHandler;
    private int mRefreshIndex = 0;
    private String linkSelf;
    private String linkNext;
    private String linkPrevious;
    private String page;
    private String per_page = "10";
    private String golden_coach_only;
    private String vip_only = "0";
    private String license_type;
    private String price;
    private String city_id;
    private ArrayList<String> training_field_ids;
    private String distance;
    private ArrayList<String> user_location;
    private String sort_by = "0";
    private ImageButton ibtnFcMap;
    private ArrayList<FieldModel> selFieldList;
    private ImageView mIvSearch;
    private TextView mTvNoCoachTips;
    private static final int PERMISSIONS_REQUEST = 600;

    private String TAG = "FindCoachActivity";
    private boolean isOnLoadMore = false;
    private SharedPreferencesUtil spUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_coach);
        initView();
        initEvent();
        if (xlvCoachList != null)
            xlvCoachList.autoRefresh();
    }

    private void initView() {
        spUtil = new SharedPreferencesUtil(FindCoachActivity.this);
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
        llyFcFilter = Util.instence(this).$(this, R.id.lly_fc_filter);
        llyFcSort = Util.instence(this).$(this, R.id.lly_fc_sort);
        mIvSearch = Util.instence(this).$(this, R.id.iv_search);
        mTvNoCoachTips = Util.instence(this).$(this, R.id.tv_no_coach_tips);
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            startLocationService();
        }
        mHandler = new Handler();

        xlvCoachList = (XListView) findViewById(R.id.xlv_coach_list);
        xlvCoachList.setPullRefreshEnable(true);
        xlvCoachList.setPullLoadEnable(true);
        xlvCoachList.setAutoLoadEnable(true);
        xlvCoachList.setXListViewListener(this);
        xlvCoachList.setRefreshTime(getTime());
        if (TextUtils.isEmpty(linkNext)) {
            xlvCoachList.setPullLoadEnable(false);
        } else {
            xlvCoachList.setPullLoadEnable(true);
        }
        //mAdapter = new CoachItemAdapter(this, coachList, R.layout.view_coach_list_item);
        //mAdapter = new ArrayAdapter<String>(this, R.layout.view_coach_list_item, items);
        //xlvCoachList.setAdapter(mAdapter);
        ibtnFcMap = Util.instence(this).$(this, R.id.ibtn_fc_map);
        City city = spUtil.getMyCity();
        List<String> distanceList = city.getFilters().getRadius();
        distance = distanceList.get(distanceList.size() - 2);
        city_id = city.getId();
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
        llyFcFilter.setOnClickListener(mClickListener);
        llyFcSort.setOnClickListener(mClickListener);
        xlvCoachList.setOnItemClickListener(mItemClickListener);
        ibtnFcMap.setOnClickListener(mClickListener);
        mIvSearch.setOnClickListener(mClickListener);
    }


    View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lly_tab_appointment:
                    Intent intent = new Intent(getApplication(), AppointmentActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.lly_tab_index:
                    intent = new Intent(getApplication(), IndexActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                //我的页面
                case R.id.lly_tab_my_setting:
                    intent = new Intent(getApplication(), MySettingActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                //筛选
                case R.id.lly_fc_filter:
                    if (fcFilterDialog == null) {
                        fcFilterDialog = new FcFilterDialog(FindCoachActivity.this, golden_coach_only, license_type, price, distance, vip_only,
                                new FcFilterDialog.OnBtnClickListener() {

                                    @Override
                                    public void onFliterCoach(String goldenCoachOnly, String licenseType, String _price, String _distance, String _vipOnly) {
                                        golden_coach_only = goldenCoachOnly;
                                        license_type = licenseType;
                                        price = _price;
                                        distance = _distance;
                                        vip_only = _vipOnly;
                                        xlvCoachList.autoRefresh();
                                    }
                                });
                        fcFilterDialog.initFilter();
                    }
                    fcFilterDialog.show();
                    break;
                //排序
                case R.id.lly_fc_sort:
                    if (null == fcSortDialog) {
                        fcSortDialog = new FcSortDialog(FindCoachActivity.this, sort_by, new FcSortDialog.OnBtnClickListener() {
                            @Override
                            public void onFindCoachCort(String sortby) {
                                sort_by = sortby;
                                xlvCoachList.autoRefresh();
                            }
                        });
                    }
                    fcSortDialog.show();
                    break;
                //地图
                case R.id.ibtn_fc_map:
                    intent = new Intent(getApplication(), FindCoachMapFilterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("selFieldList", selFieldList);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.iv_search:
                    //搜索教练
                    SearchCoachDialog searchCoachDialog = new SearchCoachDialog(FindCoachActivity.this, mCoachItemClickListener);
                    searchCoachDialog.show();
                    break;
                default:
                    break;
            }
        }
    };

    private SearchCoachDialog.OnCoachItemClicktListener mCoachItemClickListener = new SearchCoachDialog.OnCoachItemClicktListener() {
        @Override
        public boolean selectCoach(Coach coach) {
            Intent intent = new Intent(getApplication(), CoachDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("coach", coach);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
            return true;
        }
    };
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (coachList != null && coachList.size() > 0 && position > 0 && position - 1 < coachList.size()) {
                Intent intent = new Intent(getApplication(), CoachDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("coach", coachList.get(position - 1));
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        }
    };


//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//
//        if (hasFocus) {
//            xlvCoachList.autoRefresh();
//        }
//    }

    @Override
    public void onRefresh() {
        isOnLoadMore = false;
        getCoachList();
        if (TextUtils.isEmpty(linkNext)) {
            xlvCoachList.setPullLoadEnable(false);
        } else {
            xlvCoachList.setPullLoadEnable(true);
        }
    }

    @Override
    public void onLoadMore() {
        if (!TextUtils.isEmpty(linkNext) && !isOnLoadMore) {
            isOnLoadMore = true;
            getCoachList(linkNext);
        } else {
            onLoad();
        }
    }

    private void getCoachList() {
        training_field_ids = new ArrayList<>();
        if (selFieldList != null && selFieldList.size() > 0) {
            for (FieldModel selField : selFieldList) {
                training_field_ids.add(selField.getId());
            }
        }
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        Location location = spUtil.getLocation();
        if (location != null && !TextUtils.isEmpty(location.getLat()) && !TextUtils.isEmpty(location.getLng())) {
            user_location = new ArrayList<>();
            user_location.add(location.getLat());
            user_location.add(location.getLng());
        }
        String studentId = "";
        if (spUtil.getUser() != null && spUtil.getUser().getSession() != null && spUtil.getUser().getStudent() != null) {
            studentId = spUtil.getUser().getStudent().getId();
        }
        this.fcPresenter.getCoachList(page, per_page, golden_coach_only, license_type, price, city_id, training_field_ids, distance,
                user_location, sort_by, vip_only, studentId, new FCCallbackListener<CoachListResponse>() {
                    @Override
                    public void onSuccess(CoachListResponse data) {
                        coachList = data.getData();
                        if (coachList != null && coachList.size() > 0) {
                            mTvNoCoachTips.setVisibility(View.GONE);
                            xlvCoachList.setVisibility(View.VISIBLE);
                            linkSelf = data.getLinks().getSelf();
                            linkNext = data.getLinks().getNext();
                            linkPrevious = data.getLinks().getPrevious();
                            if (TextUtils.isEmpty(linkNext)) {
                                xlvCoachList.setPullLoadEnable(false);
                            } else {
                                xlvCoachList.setPullLoadEnable(true);
                            }
                            mAdapter = new CoachItemAdapter(FindCoachActivity.this, coachList, R.layout.view_coach_list_item);
                            xlvCoachList.setAdapter(mAdapter);
                            onLoad();
                        } else {
                            mTvNoCoachTips.setVisibility(View.VISIBLE);
                            xlvCoachList.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getCoachList(String url) {
        this.fcPresenter.getCoachList(url, new FCCallbackListener<CoachListResponse>() {
            @Override
            public void onSuccess(CoachListResponse data) {
                ArrayList<Coach> newCoachList = data.getData();
                if (newCoachList != null && newCoachList.size() > 0) {
                    coachList.addAll(newCoachList);
                }
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
                if (TextUtils.isEmpty(linkNext)) {
                    xlvCoachList.setPullLoadEnable(false);
                } else {
                    xlvCoachList.setPullLoadEnable(true);
                }
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter = new CoachItemAdapter(FindCoachActivity.this, coachList, R.layout.view_coach_list_item);
                    xlvCoachList.setAdapter(mAdapter);
                }
                isOnLoadMore = false;
                onLoad();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onLoad() {
        xlvCoachList.stopRefresh();
        xlvCoachList.stopLoadMore();
        xlvCoachList.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("gibxin", "requestCode -> " + requestCode);
        Log.v("gibxin", "resultCode -> " + resultCode);
        if (requestCode == 0) {
            switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
                case RESULT_OK:
                    Bundle bundle = data.getExtras(); //data为B中回传的Intent
                    if (bundle.getSerializable("selFieldList") != null) {
                        selFieldList = (ArrayList<FieldModel>) bundle.getSerializable("selFieldList");
                        Log.v("gibxin", "selFieldList.size() -> " + selFieldList.size());
                        getCoachList();
                    } else {
                        Log.v("gibxin", "new ArrayList");
                        selFieldList = new ArrayList<>();
                        getCoachList();
                    }
                    break;
                default:
                    break;
            }
        } else if (requestCode == 1) {
            switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
                case RESULT_OK:
                    Bundle bundle = data.getExtras(); //data为B中回传的Intent
                    if (bundle.getSerializable("coach") != null) {
                        Coach retCoach = (Coach) bundle.getSerializable("coach");
                        Log.v("gibxin", "retCoach ->" + retCoach.getId());
                        for (Coach coach : coachList) {
                            if (coach.getId().equals(retCoach.getId())) {
                                coach.setLike_count(retCoach.getLike_count());
                                coach.setLiked(retCoach.getLiked());
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                startLocationService();
            } else {
                Toast.makeText(this, "请允许使用定位权限，不然我们无法精确的为您推荐教练", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 开启定位服务
     */
    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    /**
     * 停止定位服务
     */
    private void stopLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationService();
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
