package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.ui.activity.signupLogin.StartActivity;
import com.hahaxueche.ui.adapter.findCoach.CoachItemAdapter;
import com.hahaxueche.ui.dialog.FcFilterDialog;
import com.hahaxueche.ui.dialog.FcSortDialog;
import com.hahaxueche.ui.activity.appointment.AppointmentActivity;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.activity.mySetting.MySettingActivity;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;
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
    private List<CoachModel> coachList = new ArrayList<CoachModel>();
    private Handler mHandler;
    private int mRefreshIndex = 0;
    private String linkSelf;
    private String linkNext;
    private String linkPrevious;
    private String page;
    private String per_page;
    private String golden_coach_only;
    private String license_type;
    private String price;
    private String city_id;
    private String training_field_ids;
    private String distance;
    private String user_location;
    private String sort_by;

    private String TAG = "FindCoachActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_coach);
        initView();
        initEvent();
        if(xlvCoachList!=null)
            xlvCoachList.autoRefresh();
    }

    private void initView() {
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
        llyFcFilter = Util.instence(this).$(this, R.id.lly_fc_filter);
        llyFcSort = Util.instence(this).$(this, R.id.lly_fc_sort);
        fcFilterDialog = new FcFilterDialog(this,
                new FcFilterDialog.OnBtnClickListener() {

                    @Override
                    public void onFliterCoach(String goldenCoachOnly, String licenseType, String _price, String _distance) {
                        golden_coach_only = goldenCoachOnly;
                        license_type = licenseType;
                        price = _price;
                        distance = _distance;
                        Log.v(TAG, "filter -> golden_coach_only=" + golden_coach_only + " license_type=" + license_type
                                + " price=" + price + " distance=" + distance);
                    }
                });
        fcSortDialog = new FcSortDialog(this,
                new FcSortDialog.OnBtnClickListener() {
                    @Override
                    public void onFindCoachCort(String sortby) {
                        sort_by = sortby;
                    }
                });
        mHandler = new Handler();

        xlvCoachList = (XListView) findViewById(R.id.xlv_coach_list);
        xlvCoachList.setPullRefreshEnable(true);
        xlvCoachList.setPullLoadEnable(true);
        xlvCoachList.setAutoLoadEnable(true);
        xlvCoachList.setXListViewListener(this);
        xlvCoachList.setRefreshTime(getTime());

        mAdapter = new CoachItemAdapter(this, coachList, R.layout.view_coach_list_item);
        //mAdapter = new ArrayAdapter<String>(this, R.layout.view_coach_list_item, items);
        xlvCoachList.setAdapter(mAdapter);
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
        llyFcFilter.setOnClickListener(mClickListener);
        llyFcSort.setOnClickListener(mClickListener);
        xlvCoachList.setOnItemClickListener(mItemClickListener);
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
                    fcFilterDialog = new FcFilterDialog(FindCoachActivity.this,golden_coach_only, license_type, price, distance,
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
                    fcFilterDialog.show();
                    break;
                //排序
                case R.id.lly_fc_sort:
                    fcSortDialog = new FcSortDialog(FindCoachActivity.this,sort_by, new FcSortDialog.OnBtnClickListener() {
                        @Override
                        public void onFindCoachCort(String sortby) {
                            sort_by = sortby;
                            xlvCoachList.autoRefresh();
                        }
                    });
                    fcSortDialog.show();
            }
        }
    };
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplication(), CoachDetailActivity.class);
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
        Log.v("gibxin", "onRefresh");
        if (!TextUtils.isEmpty(linkPrevious)) {
            getCoachList(linkPrevious);
        } else {
            getCoachList();
        }
        mAdapter = new CoachItemAdapter(FindCoachActivity.this, coachList, R.layout.view_coach_list_item);
        xlvCoachList.setAdapter(mAdapter);
        /*mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(linkPrevious)) {
                    getCoachList(linkPrevious);
                } else {
                    getCoachList();
                }
                mAdapter = new CoachItemAdapter(FindCoachActivity.this, coachList, R.layout.view_coach_list_item);
                xlvCoachList.setAdapter(mAdapter);
                onLoad();
            }
        }, 1500);*/
    }

    @Override
    public void onLoadMore() {
        Log.v("gibxin", "onLoadMore");
        if (!TextUtils.isEmpty(linkNext)) {
            getCoachList(linkNext);
        } else {
            onLoad();
            //getCoachList();
        }
        //mAdapter.notifyDataSetChanged();
        /*mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(linkNext)){
                    getCoachList(linkNext);
                }else {
                    getCoachList();
                }
                mAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 1500);*/
    }

    private void getCoachList() {
        this.fcPresenter.getCoachList(page, per_page, golden_coach_only, license_type, price, city_id, training_field_ids, distance,
                user_location, sort_by, new FCCallbackListener<CoachListResponse>() {
                    @Override
                    public void onSuccess(CoachListResponse data) {
                        coachList.clear();
                        Log.v("gibxin", "success");
                        coachList = data.getData();
                        linkSelf = data.getLinks().getSelf();
                        linkNext = data.getLinks().getNext();
                        linkPrevious = data.getLinks().getPrevious();
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
                coachList.clear();
                coachList = data.getData();
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
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

    private void onLoad() {
        xlvCoachList.stopRefresh();
        xlvCoachList.stopLoadMore();
        xlvCoachList.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }

}
