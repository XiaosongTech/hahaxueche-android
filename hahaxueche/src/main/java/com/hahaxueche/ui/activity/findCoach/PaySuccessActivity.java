package com.hahaxueche.ui.activity.findCoach;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.student.PurchasedService;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

/**
 * Created by wangshirui on 16/9/3.
 */
public class PaySuccessActivity extends FCBaseActivity {
    private ImageButton mIbtnBack;
    private TextView mTvCoachName;
    private TextView mTvPayTime;
    private TextView mTvPayAmount;
    private TextView mTvOrderCode;
    private TextView mTvShare;
    private User mUser;
    private Coach mCoach;//教练

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        initViews();
        loadDatas();
        initEvents();
    }

    private void initViews() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mTvCoachName = Util.instence(this).$(this, R.id.tv_coach_name);
        mTvPayTime = Util.instence(this).$(this, R.id.tv_pay_time);
        mTvPayAmount = Util.instence(this).$(this, R.id.tv_pay_amount);
        mTvOrderCode = Util.instence(this).$(this, R.id.tv_order_code);
        mTvShare = Util.instence(this).$(this, R.id.tv_share);
    }

    private void loadDatas() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(PaySuccessActivity.this);
        mUser = spUtil.getUser();
        if (mUser == null || mUser.getStudent() == null || TextUtils.isEmpty(mUser.getStudent().getCurrent_coach_id())
                || !mUser.getStudent().hasPurchasedService())
            return;
        String coach_id = mUser.getStudent().getCurrent_coach_id();
        this.fcPresenter.getCoach(coach_id, mUser.getStudent().getId(), new FCCallbackListener<Coach>() {
            @Override
            public void onSuccess(Coach coach) {
                mCoach = coach;
                mTvCoachName.setText(mCoach.getName());
                PurchasedService ps = mUser.getStudent().getPurchased_services().get(0);
                mTvPayTime.setText(Util.getDateFromUTC(ps.getPaid_at()));
                mTvPayAmount.setText(Util.getMoney(ps.getTotal_amount()));
                mTvOrderCode.setText(ps.getOrder_no());
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initEvents() {
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaySuccessActivity.this.finish();
            }
        });
        mTvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, null);
                finish();
            }
        });
    }


}
