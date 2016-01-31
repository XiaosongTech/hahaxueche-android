package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.hahaxueche.R;
import com.hahaxueche.ui.dialog.FcFilterDialog;
import com.hahaxueche.ui.dialog.FcSortDialog;
import com.hahaxueche.ui.fragment.appointment.AppointmentActivity;
import com.hahaxueche.ui.fragment.index.IndexActivity;
import com.hahaxueche.ui.fragment.mySetting.MySettingActivity;
import com.hahaxueche.utils.Util;

/**
 * Created by gibxin on 2016/1/27.
 */
public class FindCoachActivity extends Activity {
    private LinearLayout llyTabIndex;
    private LinearLayout llyTabFindCoach;
    private LinearLayout llyTabAppointment;
    private LinearLayout llyTabMySetting;
    private LinearLayout llyFcFilter;
    private LinearLayout llyFcSort;
    private FcFilterDialog fcFilterDialog;
    private FcSortDialog fcSortDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_coach);
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


}
