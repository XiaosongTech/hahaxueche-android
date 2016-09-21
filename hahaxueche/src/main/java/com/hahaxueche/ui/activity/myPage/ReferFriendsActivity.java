package com.hahaxueche.ui.activity.myPage;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.ReferFriendsPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.myPage.ReferFriendsView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/21.
 */

public class ReferFriendsActivity extends HHBaseActivity implements ReferFriendsView {
    @BindView(R.id.tv_refer_rules)
    TextView mTvReferRules;
    @BindView(R.id.tv_withdraw_money)
    TextView mTvWithdrawMoney;
    @BindView(R.id.iv_refer)
    SimpleDraweeView mIvRefer;
    @BindView(R.id.iv_qr_code)
    SimpleDraweeView mIvQrCode;
    @BindView(R.id.iv_dash)
    ImageView mIvDash;
    ImageView mIvBack;
    TextView mTvTitle;
    private ReferFriendsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ReferFriendsPresenter();
        setContentView(R.layout.activity_refer_friends);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        mIvDash.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initActionBar();
    }

    @Override
    public void setReferRules(String rules) {
        mTvReferRules.setText(rules);
    }

    @Override
    public void setMyCityReferImage(String url) {
        mIvRefer.setImageURI(url);
    }

    @Override
    public void setQrCodeImage(String url) {
        mIvQrCode.setImageURI(url);
    }

    @Override
    public void setWithdrawMoney(String money) {
        mTvWithdrawMoney.setText(money);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("我为哈哈代言");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReferFriendsActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}
