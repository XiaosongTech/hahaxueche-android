package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.model.findCoach.FieldModel;
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
    private String mlat;
    private String mlng;

    private String TAG = "FindCoachActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_coach);
        initView();
        initEvent();
        SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
        mlat = sharedPreferences.getString("lat", "");
        mlng = sharedPreferences.getString("lng", "");
        if (xlvCoachList != null)
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
        if (TextUtils.isEmpty(linkNext)) {
            xlvCoachList.setPullLoadEnable(false);
        } else {
            xlvCoachList.setPullLoadEnable(true);
        }
        mAdapter = new CoachItemAdapter(this, coachList, R.layout.view_coach_list_item);
        //mAdapter = new ArrayAdapter<String>(this, R.layout.view_coach_list_item, items);
        xlvCoachList.setAdapter(mAdapter);
        ibtnFcMap = Util.instence(this).$(this, R.id.ibtn_fc_map);
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
                    fcFilterDialog.show();
                    break;
                //排序
                case R.id.lly_fc_sort:
                    fcSortDialog = new FcSortDialog(FindCoachActivity.this, sort_by, new FcSortDialog.OnBtnClickListener() {
                        @Override
                        public void onFindCoachCort(String sortby) {
                            sort_by = sortby;
                            xlvCoachList.autoRefresh();
                        }
                    });
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
        if (!TextUtils.isEmpty(linkPrevious)) {
            getCoachList(linkPrevious);
        } else {
            getCoachList();
        }
        if (TextUtils.isEmpty(linkNext)) {
            xlvCoachList.setPullLoadEnable(false);
        } else {
            xlvCoachList.setPullLoadEnable(true);
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
        if (selFieldList != null && selFieldList.size() > 0) {
            training_field_ids = new ArrayList<>();
            for (FieldModel selField : selFieldList) {
                training_field_ids.add(selField.getId());
            }
        }
        if(!TextUtils.isEmpty(mlat)&&!TextUtils.isEmpty(mlng)){
            user_location = new ArrayList<>();
            user_location.add(mlat);
            user_location.add(mlng);
        }
        this.fcPresenter.getCoachList(page, per_page, golden_coach_only, license_type, price, city_id, training_field_ids, distance,
                user_location, sort_by, new FCCallbackListener<CoachListResponse>() {
                    @Override
                    public void onSuccess(CoachListResponse data) {
                        coachList.clear();
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
                coachList.clear();
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

    private void onLoad() {
        xlvCoachList.stopRefresh();
        xlvCoachList.stopLoadMore();
        xlvCoachList.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
                case RESULT_OK:
                    Bundle bundle = data.getExtras(); //data为B中回传的Intent
                    if (bundle.getSerializable("selFieldList") != null) {
                        selFieldList = (ArrayList<FieldModel>) bundle.getSerializable("selFieldList");
                        getCoachList();
                    } else {
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
