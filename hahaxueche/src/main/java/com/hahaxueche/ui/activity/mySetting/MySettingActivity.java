package com.hahaxueche.ui.activity.mySetting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.R;
import com.hahaxueche.model.mySetting.PaymentStage;
import com.hahaxueche.model.mySetting.PurchasedService;
import com.hahaxueche.model.signupLogin.StudentModel;
import com.hahaxueche.model.util.BaseApiResponse;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.share.ShareConstants;
import com.hahaxueche.ui.activity.appointment.AppointmentActivity;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.activity.findCoach.FindCoachActivity;
import com.hahaxueche.ui.activity.findCoach.MyCoachActivity;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.activity.signupLogin.StartActivity;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.monitorScrollView.MonitorScrollView;
import com.hahaxueche.utils.JsonUtils;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;
import com.tencent.tauth.Tencent;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by gibxin on 2016/1/27.
 */
public class MySettingActivity extends MSBaseActivity {
    private LinearLayout llyTabIndex;
    private LinearLayout llyTabFindCoach;
    private LinearLayout llyTabAppointment;
    private LinearLayout llyTabMySetting;
    private CircleImageView cirMyAvatar;
    private RelativeLayout rllCustomerServiceQQ;
    private Tencent mTencent;//QQ
    private TextView tvBackLogin;//跳转登录
    private LinearLayout llyNotLogin;//未登录页面
    private MonitorScrollView msvMain;
    private boolean isLogin = false;
    private StudentModel mStudent;
    private PurchasedService mPurchasedService;
    private String accessToken;
    private String session_id;
    private TextView tvStuName;
    private TextView tvUnpaidAmount;
    private RelativeLayout rlyMyFollowCoach;
    private RelativeLayout rlyMyCoach;
    private RelativeLayout rlyPaymentStage;
    private TextView tvPaymentStage;
    private ImageView ivPaymentStage;
    private LinearLayout llyLoginOff;
    private RelativeLayout rlyCustomerPhone;
    private RelativeLayout rlyAboutHaha;
    private View vwMyCoach;
    private ProgressDialog pd;//进度框

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_setting);
        mTencent = Tencent.createInstance(ShareConstants.APP_ID_QQ, MySettingActivity.this);
        initView();
        initEvent();
        loadDatas();
    }

    private void initView() {
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
        cirMyAvatar = Util.instence(this).$(this, R.id.cir_my_avatar);
        rllCustomerServiceQQ = Util.instence(this).$(this, R.id.rll_customer_service_qq);
        tvBackLogin = Util.instence(this).$(this, R.id.tv_back_login);
        llyNotLogin = Util.instence(this).$(this, R.id.lly_not_login);
        msvMain = Util.instence(this).$(this, R.id.msv_main);
        tvStuName = Util.instence(this).$(this, R.id.tv_stu_name);
        tvUnpaidAmount = Util.instence(this).$(this, R.id.tv_unpaid_amount);
        rlyMyFollowCoach = Util.instence(this).$(this, R.id.rly_my_follow_coach);
        rlyMyCoach = Util.instence(this).$(this, R.id.rly_my_coach);
        rlyPaymentStage = Util.instence(this).$(this, R.id.rly_payment_stage);
        tvPaymentStage = Util.instence(this).$(this, R.id.tv_payment_stage);
        ivPaymentStage = Util.instence(this).$(this, R.id.iv_payment_stage);
        llyLoginOff = Util.instence(this).$(this, R.id.lly_login_off);
        rlyCustomerPhone = Util.instence(this).$(this, R.id.rly_customer_phone);
        rlyAboutHaha = Util.instence(this).$(this, R.id.rly_about_haha);
        vwMyCoach = Util.instence(this).$(this, R.id.vw_my_coach);
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
        rllCustomerServiceQQ.setOnClickListener(mClickListener);
        tvBackLogin.setOnClickListener(mClickListener);
        rlyMyFollowCoach.setOnClickListener(mClickListener);
        rlyMyCoach.setOnClickListener(mClickListener);
        rlyPaymentStage.setOnClickListener(mClickListener);
        llyLoginOff.setOnClickListener(mClickListener);
        rlyCustomerPhone.setOnClickListener(mClickListener);
        rlyAboutHaha.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token", "");
        session_id = sharedPreferences.getString("session_id", "");
        if (!TextUtils.isEmpty(accessToken)) {
            Type stuType = new TypeToken<StudentModel>() {
            }.getType();
            mStudent = JsonUtils.deserialize(sharedPreferences.getString("student", ""), stuType);
            if (mStudent != null && !TextUtils.isEmpty(mStudent.getId())) {
                isLogin = true;
            }
        }
        if (isLogin) {
            llyNotLogin.setVisibility(View.GONE);
            msvMain.setVisibility(View.VISIBLE);
            tvStuName.setText(mStudent.getName());
            //头像
            int iconWidth = Util.instence(this).dip2px(90);
            int iconHeight = iconWidth;
            Picasso.with(this).load(mStudent.getAvatar()).resize(iconWidth, iconHeight).into(cirMyAvatar);
            if (mStudent.getPurchased_services() != null && mStudent.getPurchased_services().size() > 0) {
                //有pruchased service，目前默认取第一个
                mPurchasedService = mStudent.getPurchased_services().get(0);
                //账户余额
                tvUnpaidAmount.setText(Util.getMoney(mPurchasedService.getUnpaid_amount()));
                List<PaymentStage> paymentStageList = mPurchasedService.getPayment_stages();
                String tempPaymentStageStr = "";
                for (PaymentStage paymentStage : paymentStageList) {
                    if (paymentStage.getStage_number().equals(mPurchasedService.getCurrent_payment_stage())) {
                        tempPaymentStageStr = paymentStage.getStage_name();
                        break;
                    }
                }
                if (TextUtils.isEmpty(tempPaymentStageStr)) {
                    if (paymentStageList.size() + 1 == Integer.parseInt(mPurchasedService.getCurrent_payment_stage())) {
                        tempPaymentStageStr = "已拿证";
                    }
                }
                tvPaymentStage.setText(tempPaymentStageStr);
                rlyMyCoach.setVisibility(View.VISIBLE);
                ivPaymentStage.setVisibility(View.VISIBLE);
                vwMyCoach.setVisibility(View.VISIBLE);
                rlyMyCoach.setClickable(true);
                rlyPaymentStage.setClickable(true);
            } else {
                tvPaymentStage.setText("未选择教练");
                rlyMyCoach.setVisibility(View.GONE);
                ivPaymentStage.setVisibility(View.GONE);
                vwMyCoach.setVisibility(View.GONE);
                rlyPaymentStage.setClickable(false);
                rlyMyCoach.setClickable(false);
            }
        } else {
            llyNotLogin.setVisibility(View.VISIBLE);
            msvMain.setVisibility(View.GONE);
        }


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
                //QQ客服
                case R.id.rll_customer_service_qq:
                    int ret = mTencent.startWPAConversation(MySettingActivity.this, ShareConstants.CUSTOMER_SERVICE_QQ, "");
                    break;
                case R.id.tv_back_login:
                    intent = new Intent(getApplication(), StartActivity.class);
                    intent.putExtra("isBack", "1");
                    startActivity(intent);
                    break;
                //我的教练
                case R.id.rly_my_coach:
                    if (mStudent != null && !TextUtils.isEmpty(mStudent.getCurrent_coach_id())) {
                        intent = new Intent(getApplication(), MyCoachActivity.class);
                        intent.putExtra("coach_id", mStudent.getCurrent_coach_id());
                        startActivity(intent);
                    }
                    break;
                //我关注的教练
                case R.id.rly_my_follow_coach:
                    intent = new Intent(getApplication(), FollowCoachListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rly_payment_stage:
                    intent = new Intent(getApplication(), PaymentStageActivity.class);
                    startActivity(intent);
                    break;
                //退出登录
                case R.id.lly_login_off:
                    if (pd != null) {
                        pd.dismiss();
                    }
                    pd = ProgressDialog.show(MySettingActivity.this, null, "退出中，请稍后……");
                    msPresenter.loginOff(session_id, accessToken, new MSCallbackListener<BaseApiResponse>() {
                        @Override
                        public void onSuccess(BaseApiResponse data) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                            SharedPreferences spSession = getSharedPreferences("session", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor spEditor = spSession.edit();
                            spEditor.clear();
                            spEditor.commit();
                            Intent intent = new Intent(getApplication(), StartActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(String errorEvent, String message) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                        }
                    });
                    break;
                //客服热线
                case R.id.rly_customer_phone:
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
                    startActivity(intent);
                    break;
                //关于哈哈
                case R.id.rly_about_haha:
                    Uri uri = Uri.parse("http://staging.hahaxueche.net/#/student");
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
