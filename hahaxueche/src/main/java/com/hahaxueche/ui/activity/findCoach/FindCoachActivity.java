package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.ui.adapter.findCoach.CoachItemAdapter;
import com.hahaxueche.ui.dialog.FcFilterDialog;
import com.hahaxueche.ui.dialog.FcSortDialog;
import com.hahaxueche.ui.fragment.appointment.AppointmentActivity;
import com.hahaxueche.ui.fragment.index.IndexActivity;
import com.hahaxueche.ui.fragment.mySetting.MySettingActivity;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;
import com.hahaxueche.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gibxin on 2016/1/27.
 */
public class FindCoachActivity extends Activity implements XListView.IXListViewListener {
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
    private int mIndex = 0;
    private int mRefreshIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_coach);
        geneItems();
        initView();
        initEvent();
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
                    public void onFliterCoach(String cityName, String cityId) {

                    }
                });
        fcSortDialog = new FcSortDialog(this,
                new FcSortDialog.OnBtnClickListener() {
                    @Override
                    public void onFindCoachCort(String cityName, String cityId) {

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
                case R.id.lly_tab_my_setting:
                    intent = new Intent(getApplication(), MySettingActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.lly_fc_filter:
                    fcFilterDialog = new FcFilterDialog(FindCoachActivity.this,
                            new FcFilterDialog.OnBtnClickListener() {

                                @Override
                                public void onFliterCoach(String cityName, String cityId) {

                                }
                            });
                    fcFilterDialog.show();
                    break;
                case R.id.lly_fc_sort:
                    fcSortDialog = new FcSortDialog(FindCoachActivity.this,
                            new FcSortDialog.OnBtnClickListener() {
                                @Override
                                public void onFindCoachCort(String cityName, String cityId) {

                                }
                            });
                    fcSortDialog.show();
            }
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            xlvCoachList.autoRefresh();
        }
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIndex = ++mRefreshIndex;
                coachList.clear();
                geneItems();
                mAdapter = new CoachItemAdapter(FindCoachActivity.this, coachList, R.layout.view_coach_list_item);
                xlvCoachList.setAdapter(mAdapter);
                onLoad();
            }
        }, 2500);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                geneItems();
                mAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2500);
    }

    private void geneItems() {
        for (int i = 0; i < 10; i++) {
            CoachModel c = new CoachModel();
            c.setCoachName("张三");
            c.setCoachTeachTime("9年教龄");
            c.setCoachPoints("4.8分");
            c.setCoachActualPrice("￥2850");
            c.setCoachOldPrice("￥3200");
            coachList.add(c);
            mIndex++;
        }
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
