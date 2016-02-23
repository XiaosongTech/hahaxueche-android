package com.hahaxueche.ui.activity.index;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hahaxueche.R;
import com.hahaxueche.ui.activity.appointment.AppointmentActivity;
import com.hahaxueche.ui.activity.findCoach.FindCoachActivity;
import com.hahaxueche.ui.activity.mySetting.MySettingActivity;
import com.hahaxueche.ui.activity.signupLogin.StartActivity;
import com.hahaxueche.utils.Util;

/**
 * Created by gibxin on 2016/1/27.
 */
public class IndexActivity extends Activity {
    private LinearLayout llyTabIndex;
    private LinearLayout llyTabFindCoach;
    private LinearLayout llyTabAppointment;
    private LinearLayout llyTabMySetting;
    private boolean isLogin = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();
        initEvent();
        loadDatas();
    }

    private void initView() {
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("access_token", "");
        if (!TextUtils.isEmpty(accessToken)) {
            isLogin = true;
        }
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lly_tab_find_coach:
                    Intent intent = new Intent(getApplication(), FindCoachActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.lly_tab_appointment:
                    intent = new Intent(getApplication(), AppointmentActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.lly_tab_my_setting:
                    if (isLogin) {
                        intent = new Intent(getApplication(), MySettingActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        intent = new Intent(getApplication(), StartActivity.class);
                        intent.putExtra("isBack", "1");
                        startActivity(intent);
                    }
                    break;

            }
        }
    };
}
