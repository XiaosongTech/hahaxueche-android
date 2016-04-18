package com.hahaxueche.ui.activity.appointment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.R;
import com.hahaxueche.model.coach.CoachModel;
import com.hahaxueche.model.student.StudentModel;
import com.hahaxueche.ui.activity.findCoach.FindCoachActivity;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.activity.mySetting.MySettingActivity;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Type;

/**
 * Created by gibxin on 2016/1/27.
 */
public class AppointmentActivity extends Activity {
    private LinearLayout llyTabIndex;
    private LinearLayout llyTabFindCoach;
    private LinearLayout llyTabAppointment;
    private LinearLayout llyTabMySetting;
    private LinearLayout llyApHasCoach;
    private LinearLayout llyApNoCoach;
    private TextView tvHasCoach;
    private TextView tvNoCoach;
    private CircleImageView cirApCoachAvatar;
    private StudentModel mStudent;
    private CoachModel mCurrentCoach;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        initView();
        initEvent();
        loadDatas();
    }

    private void initView() {
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
        llyApHasCoach = Util.instence(this).$(this, R.id.lly_ap_has_coach);
        llyApNoCoach = Util.instence(this).$(this, R.id.lly_ap_no_coach);
        tvHasCoach = Util.instence(this).$(this, R.id.tv_has_coach);
        tvNoCoach = Util.instence(this).$(this, R.id.tv_no_coach);
        cirApCoachAvatar = Util.instence(this).$(this, R.id.cir_ap_coach_avatar);
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
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

            }
        }
    };

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

    private void loadDatas() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        mStudent = spUtil.getStudent();
        Type coachType = new TypeToken<CoachModel>() {
        }.getType();
        mCurrentCoach = spUtil.getCurrentCoach();
        if (mStudent != null && mStudent.getPurchased_services() != null && mStudent.getPurchased_services().size() > 0 && mCurrentCoach != null) {
            llyApHasCoach.setVisibility(View.VISIBLE);
            tvHasCoach.setVisibility(View.VISIBLE);
            llyApNoCoach.setVisibility(View.GONE);
            tvNoCoach.setVisibility(View.GONE);
            //头像
            int iconWidth = Util.instence(this).dip2px(30);
            int iconHeight = iconWidth;
            Picasso.with(this).load(mCurrentCoach.getAvatar()).resize(iconWidth, iconHeight).into(cirApCoachAvatar);
        } else {
            llyApHasCoach.setVisibility(View.GONE);
            tvHasCoach.setVisibility(View.GONE);
            llyApNoCoach.setVisibility(View.VISIBLE);
            tvNoCoach.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
