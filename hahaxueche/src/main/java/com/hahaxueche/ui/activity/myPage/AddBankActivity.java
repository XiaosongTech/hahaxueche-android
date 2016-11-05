package com.hahaxueche.ui.activity.myPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.Bank;
import com.hahaxueche.model.payment.BankCard;
import com.hahaxueche.presenter.myPage.AddBankPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.myPage.AddBankView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/11/2.
 */

public class AddBankActivity extends HHBaseActivity implements AddBankView {
    private AddBankPresenter mPresenter;
    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    @BindView(R.id.et_account_name)
    EditText mEtAccountName;
    @BindView(R.id.et_account)
    EditText mEtAccount;
    @BindView(R.id.tv_open_bank)
    TextView mTvOpenBank;
    private static final int REQUEST_CODE_SELECT_OPEN_BANK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new AddBankPresenter();
        setContentView(R.layout.activity_add_bank);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("添加银行卡");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBankActivity.this.finish();
            }
        });
    }

    @OnClick({R.id.tv_confirm,
            R.id.tv_open_bank})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm:
                mPresenter.addBankCard(mEtAccountName.getText().toString(), mEtAccount.getText().toString());
                break;
            case R.id.tv_open_bank:
                startActivityForResult(new Intent(getContext(), SelectBankActivity.class), REQUEST_CODE_SELECT_OPEN_BANK);
                break;
            default:
                break;
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void back(boolean isUpdate) {
        Intent intent = new Intent();
        intent.putExtra("isUpdate", isUpdate);
        setResult(RESULT_OK, intent);
        AddBankActivity.this.finish();
    }

    @Override
    public void loadOpenBank(Bank openAccountBank) {
        mTvOpenBank.setText(openAccountBank.name);
        mTvOpenBank.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
    }

    @Override
    public void loadAccount(BankCard bankCard) {
        mEtAccountName.setText(bankCard.name);
        mEtAccount.setText(bankCard.card_number);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_OPEN_BANK) {
            if (resultCode == RESULT_OK && data != null) {
                try {
                    Bank bank = data.getParcelableExtra("bank");
                    if (bank != null) {
                        mPresenter.setOpenAccountBank(bank);
                    }
                } catch (Exception e) {

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
