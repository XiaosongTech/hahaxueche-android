package com.hahaxueche.ui.activity.mySetting;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;
import com.hahaxueche.utils.Util;

/**
 * Created by gibxin on 2016/4/29.
 */
public class MakeMoneyInfoActivity extends MSBaseActivity {
    private ImageButton mIbtnBack;
    private TextView mTvPendingAmount;
    private TextView mTvAvailableAmount;
    private TextView mTvWithdrawnAmount;
    private TextView mTvWithdraw;
    private XListView mXlvReferInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_money);
        initView();
        initEvent();
    }

    private void initView(){
        mIbtnBack =  Util.instence(this).$(this, R.id.ibtn_back);
        mTvPendingAmount =  Util.instence(this).$(this, R.id.tv_pending_amount);
        mTvAvailableAmount = Util.instence(this).$(this, R.id.tv_available_amount);
        mTvWithdrawnAmount = Util.instence(this).$(this, R.id.tv_withdrawn_amount);
        mTvWithdraw = Util.instence(this).$(this, R.id.tv_withdraw);
        mXlvReferInfo = Util.instence(this).$(this, R.id.xlv_refer_info);
    }

    private void initEvent(){

    }

}
