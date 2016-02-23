package com.hahaxueche.ui.activity.mySetting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hahaxueche.R;
import com.hahaxueche.share.ShareConstants;
import com.hahaxueche.ui.activity.appointment.AppointmentActivity;
import com.hahaxueche.ui.activity.findCoach.FindCoachActivity;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;
import com.tencent.tauth.Tencent;

/**
 * Created by gibxin on 2016/1/27.
 */
public class MySettingActivity extends Activity {
    private LinearLayout llyTabIndex;
    private LinearLayout llyTabFindCoach;
    private LinearLayout llyTabAppointment;
    private LinearLayout llyTabMySetting;
    private CircleImageView cirMyAvatar;
    private RelativeLayout rllCustomerServiceQQ;
    private Tencent mTencent;//QQ

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        mTencent = Tencent.createInstance(ShareConstants.APP_ID_QQ, MySettingActivity.this);
        initView();
        initEvent();
    }

    private void initView() {
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
        cirMyAvatar = Util.instence(this).$(this, R.id.cir_my_avatar);
        rllCustomerServiceQQ = Util.instence(this).$(this, R.id.rll_customer_service_qq);
        int iconWidth = Util.instence(this).dip2px(90);
        int iconHeight = iconWidth;
        Picasso.with(this).load("http://shanxi.sinaimg.cn/2013/1120/U10195P1196DT20131120151205.png").resize(iconWidth, iconHeight).into(cirMyAvatar);
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
        rllCustomerServiceQQ.setOnClickListener(mClickListener);
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
                case R.id.rll_customer_service_qq:
                    int ret = mTencent.startWPAConversation(MySettingActivity.this,ShareConstants.CUSTOMER_SERVICE_QQ , "");
                    Log.v("gibxin","打开QQ -> " + ret);
                    break;
            }
        }
    };
}
