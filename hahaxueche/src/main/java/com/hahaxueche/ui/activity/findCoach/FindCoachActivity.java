package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.response.CoachListResponse;
import com.hahaxueche.model.coach.CoachModel;
import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.city.Location;
import com.hahaxueche.model.city.CityModel;
import com.hahaxueche.model.student.StudentModel;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.ui.adapter.findCoach.CoachItemAdapter;
import com.hahaxueche.ui.dialog.FcFilterDialog;
import com.hahaxueche.ui.dialog.FcSortDialog;
import com.hahaxueche.ui.activity.appointment.AppointmentActivity;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.activity.mySetting.MySettingActivity;
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
    private ArrayList<CoachModel> coachList = new ArrayList<CoachModel>();
    private Handler mHandler;
    private int mRefreshIndex = 0;
    private String linkSelf;
    private String linkNext;
    private String linkPrevious;
    private String page;
    private String per_page = "10";
    private String golden_coach_only;
    private String license_type;
    private String price;
    private String city_id;
    private ArrayList<String> training_field_ids;
    private String distance;
    private ArrayList<String> user_location;
    private String sort_by = "0";
    private ImageButton ibtnFcMap;
    private ArrayList<FieldModel> selFieldList;

    private String TAG = "FindCoachActivity";
    private boolean isOnLoadMore = false;
    private SharedPreferencesUtil spUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_coach);
        initView();
        initEvent();
        SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
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
        city_id = spUtil.getStudent().getCity_id();
        StudentModel student = spUtil.getStudent();
        List<CityModel> cityList = spUtil.getConstants().getCities();
        int myCityCount = 0;
        for (int i = 0; i < cityList.size(); i++) {
            if (cityList.get(i).getId().equals(city_id)) {
                myCityCount = i;
                break;
            }
        }
        CityModel city = cityList.get(myCityCount);
        List<String> distanceList = city.getFilters().getRadius();
        distance = distanceList.get(distanceList.size() - 2);
        List<String> priceList = city.getFilters().getPrices();
        price = priceList.get(priceList.size() - 1);
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
                        fcFilterDialog = new FcFilterDialog(FindCoachActivity.this, golden_coach_only, license_type, price, distance,
                                new FcFilterDialog.OnBtnClickListener() {

                                    @Override
                                    public void onFliterCoach(String goldenCoachOnly, String licenseType, String _price, String _distance) {
                                        golden_coach_only = goldenCoachOnly;
                                        license_type = licenseType;
                                        price = _price;
                                        distance = _distance;
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
                    startActivityForResult(intent, 0);
                    break;
            }
        }
    };
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplication(), CoachDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("coach", coachList.get(position - 1));
            intent.putExtras(bundle);
            startActivity(intent);
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
        if (selFieldList != null && selFieldList.size() > 0) {
            training_field_ids = new ArrayList<>();
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
        this.fcPresenter.getCoachList(page, per_page, golden_coach_only, license_type, price, city_id, training_field_ids, distance,
                user_location, sort_by, new FCCallbackListener<CoachListResponse>() {
                    @Override
                    public void onSuccess(CoachListResponse data) {
                        coachList = data.getData();
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
                ArrayList<CoachModel> newCoachList = data.getData();
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
        }
    }

}
