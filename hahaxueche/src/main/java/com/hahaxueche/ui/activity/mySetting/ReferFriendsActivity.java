package com.hahaxueche.ui.activity.mySetting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.city.City;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

/**
 * Created by gibxin on 2016/4/26.
 */
public class ReferFriendsActivity extends MSBaseActivity {
    private ImageButton mIbtnBack;
    private TextView mTvWithdrawMoney;//提现金额
    private TextView mTvWithdraw;//提现
    private ImageView mIvDash;
    private SharedPreferencesUtil spUtil;

    private ProgressDialog pd;//进度框
    private TextView mTvReferRules;
    private City myCity;
    private ImageView mIvRefer;
    private static final int PERMISSIONS_REQUEST = 600;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_friends);
        initView();
        initEvent();
        loadDatas();
    }

    private void initView() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mIvDash = Util.instence(this).$(this, R.id.iv_dash);
        mTvReferRules = Util.instence(this).$(this, R.id.tv_refer_rules);
        mTvWithdrawMoney = Util.instence(this).$(this, R.id.tv_withdraw_money);
        mTvWithdraw = Util.instence(this).$(this, R.id.tv_withdraw);
        mIvDash.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        spUtil = new SharedPreferencesUtil(this);
        mIvRefer = Util.instence(this).$(this, R.id.iv_refer);
    }

    private void initEvent() {
        mTvWithdraw.setOnClickListener(mClickListener);
        mIbtnBack.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        String eventDetailTips = getResources().getString(R.string.eventDetailsTips);
        myCity = spUtil.getMyCity();
        if (myCity != null && !TextUtils.isEmpty(myCity.getReferral_banner())) {
            mTvReferRules.setText(String.format(eventDetailTips, Util.getMoney(String.valueOf(myCity.getReferer_bonus()))));
            int width = Util.instence(context).getDm().widthPixels;
            int height = Math.round(((float) 8 / 9) * width);
            Picasso.with(context).load(myCity.getReferral_banner()).resize(width, height).centerCrop().into(mIvRefer);
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ibtn_back:
                    ReferFriendsActivity.this.finish();
                    break;
                case R.id.tv_withdraw:
                    Intent intent = new Intent(ReferFriendsActivity.this, WithdrawActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
}
