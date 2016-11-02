package com.hahaxueche.ui.activity.myPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.payment.BankCard;
import com.hahaxueche.presenter.myPage.WithdrawPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.myPage.WithdrawView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/11/1.
 */

public class WithdrawActivity extends HHBaseActivity implements WithdrawView, SwipeRefreshLayout.OnRefreshListener {
    private WithdrawPresenter mPresenter;
    ImageView mIvBack;
    TextView mTvTitle;
    TextView mTvWithdrawRecords;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;
    @BindView(R.id.et_withdraw_money)
    EditText mEtWithdrawAmount;
    @BindView(R.id.iv_dash)
    ImageView mIvDash;
    @BindView(R.id.tv_available_amount)
    TextView mTvAvailableAmount;
    @BindView(R.id.rly_bank_card)
    RelativeLayout mRlyBankCard;
    @BindView(R.id.tv_bank_name)
    TextView mTvBankName;
    @BindView(R.id.tv_bank_remarks)
    TextView mTvBankRemarks;
    @BindView(R.id.fly_add_bank)
    FrameLayout mFlyAddBank;
    public static int REQUEST_CODE_ADD_BANK_CARD = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new WithdrawPresenter();
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        mSrlRefresh.setOnRefreshListener(this);
        mSrlRefresh.setColorSchemeResources(R.color.app_theme_color);
        mEtWithdrawAmount.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2) {
                    edt.delete(posDot + 3, posDot + 4);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
        });
        mIvDash.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_withdraw);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        mTvWithdrawRecords = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_withdraw_records);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("提现");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WithdrawActivity.this.finish();
            }
        });
        mTvWithdrawRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), WithdrawRecordsActivity.class));
            }
        });
    }

    @OnClick({R.id.tv_confirm_withdraw,
            R.id.fly_add_bank,
            R.id.rly_bank_card})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm_withdraw:
                mPresenter.withdraw(mEtWithdrawAmount.getText().toString());
                break;
            case R.id.fly_add_bank:
                startActivityForResult(new Intent(getContext(), AddBankActivity.class), REQUEST_CODE_ADD_BANK_CARD);
                break;
            case R.id.rly_bank_card:
                startActivityForResult(new Intent(getContext(), AddBankActivity.class), REQUEST_CODE_ADD_BANK_CARD);
                break;
            default:
                break;
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSrlRefresh, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void back(boolean isUpdate) {
        Intent intent = new Intent();
        intent.putExtra("isUpdate", isUpdate);
        setResult(RESULT_OK, intent);
        WithdrawActivity.this.finish();
    }


    @Override
    public void startRefresh() {
        mSrlRefresh.setRefreshing(true);
    }

    @Override
    public void stopRefresh() {
        mSrlRefresh.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchStudent();
    }

    @Override
    public void setAvailableAmount(String amount) {
        mTvAvailableAmount.setText(amount);
    }

    @Override
    public void setBankInfo(BankCard bankCard) {
        if (bankCard != null) {
            mRlyBankCard.setVisibility(View.VISIBLE);
            mFlyAddBank.setVisibility(View.GONE);
            mTvBankName.setText(bankCard.bank_name);
            mTvBankRemarks.setText(bankCard.bank_name + " , 尾号" + bankCard.card_number.substring(bankCard.card_number.length() - 4, bankCard.card_number.length()));
        } else {
            mRlyBankCard.setVisibility(View.GONE);
            mFlyAddBank.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_BANK_CARD) {
            if (resultCode == RESULT_OK && null != data && data.getBooleanExtra("isUpdate", false)) {
                mPresenter.fetchStudent();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
