package com.hahaxueche.ui.fragment.mySetting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.hahaxueche.R;
import com.hahaxueche.ui.fragment.appointment.AppointmentActivity;
import com.hahaxueche.ui.activity.findCoach.FindCoachActivity;
import com.hahaxueche.ui.fragment.index.IndexActivity;
import com.hahaxueche.utils.Util;

/**
 * Created by gibxin on 2016/1/27.
 */
public class MySettingActivity extends Activity{
    private LinearLayout llyTabIndex;
    private LinearLayout llyTabFindCoach;
    private LinearLayout llyTabAppointment;
    private LinearLayout llyTabMySetting;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        initView();
        initEvent();
    }
    private void initView(){
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
    }

    private void initEvent(){
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
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
                case R.id.lly_tab_find_coach:
                    intent = new Intent(getApplication(), FindCoachActivity.class);
                    startActivity(intent);
                    finish();
                    break;

            }
        }
    };
}
