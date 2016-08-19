package com.hahaxueche.ui.activity.mySetting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.student.Bank;
import com.hahaxueche.model.student.BankCard;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

/**
 * Created by wangshirui on 16/8/1.
 */
public class AddBankActivity extends MSBaseActivity {
    private EditText mEtAccountName;
    private EditText mEtAccount;
    private TextView mTvConfirm;
    private ImageButton mIbtnBack;
    private RelativeLayout mRlyOpenBank;//选择开户行布局
    private TextView mTvOpenBank;

    private String mOpenBankName;//开户行名称
    private String mOpenBankCode;//开户行编码
    private User mUser;
    private SharedPreferencesUtil spUtil;
    private ProgressDialog pd;
    private BankCard mBankCard;//编辑的银行卡

    private static final int REQUEST_CODE_SELECT_OPEN_BANK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        spUtil = new SharedPreferencesUtil(AddBankActivity.this);
        initViews();
        initEvents();
        loadDatas();
    }

    private void initViews() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mEtAccount = Util.instence(this).$(this, R.id.et_account);
        mEtAccountName = Util.instence(this).$(this, R.id.et_account_name);
        mTvConfirm = Util.instence(this).$(this, R.id.tv_confirm);
        mRlyOpenBank = Util.instence(this).$(this, R.id.rly_open_bank);
        mTvOpenBank = Util.instence(this).$(this, R.id.tv_open_bank);
    }

    private void initEvents() {
        mEtAccountName.clearFocus();
        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBankCard != null) {//修改银行卡
                    pd = ProgressDialog.show(AddBankActivity.this, null, "银行卡信息添加中，请稍后……");
                    msPresenter.editBankCard(mEtAccountName.getText().toString(), mEtAccount.getText().toString(), mOpenBankCode, mUser.getStudent().getId(),
                            mUser.getSession().getAccess_token(), new MSCallbackListener<BankCard>() {
                                @Override
                                public void onSuccess(BankCard data) {
                                    pd.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("isUpdate", true);
                                    setResult(RESULT_OK, intent);
                                    AddBankActivity.this.finish();
                                }

                                @Override
                                public void onFailure(String errorEvent, String message) {
                                    pd.dismiss();
                                    Toast.makeText(AddBankActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {//添加银行卡
                    pd = ProgressDialog.show(AddBankActivity.this, null, "银行卡信息修改中，请稍后……");
                    msPresenter.addBankCard(mEtAccountName.getText().toString(), mEtAccount.getText().toString(), mOpenBankCode, mUser.getStudent().getId(),
                            mUser.getSession().getAccess_token(), new MSCallbackListener<BankCard>() {
                                @Override
                                public void onSuccess(BankCard data) {
                                    pd.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("isUpdate", true);
                                    setResult(RESULT_OK, intent);
                                    AddBankActivity.this.finish();
                                }

                                @Override
                                public void onFailure(String errorEvent, String message) {
                                    pd.dismiss();
                                    Toast.makeText(AddBankActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBankActivity.this.finish();
            }
        });
        mRlyOpenBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), SelectOpenBankActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_OPEN_BANK);
            }
        });
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }


    private void loadDatas() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            try {
                mBankCard = (BankCard) bundle.get("bankCard");
                if (mBankCard != null) {
                    mOpenBankCode = mBankCard.getOpen_bank_code();
                    mOpenBankName = mBankCard.getBank_name();
                    mEtAccountName.setText(mBankCard.getName());
                    mEtAccount.setText(mBankCard.getCard_number());
                    loadOpenBank();
                }
            } catch (Exception e) {

            }
        }
        mUser = spUtil.getUser();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_OPEN_BANK) {
            if (resultCode == RESULT_OK && data != null) {
                try {
                    Bank bank = (Bank) data.getExtras().getSerializable("bank");
                    if (bank != null) {
                        mOpenBankCode = bank.getCode();
                        mOpenBankName = bank.getName();
                        loadOpenBank();
                    }
                } catch (Exception e) {

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadOpenBank() {
        if (!TextUtils.isEmpty(mOpenBankCode)) {
            mTvOpenBank.setText(mOpenBankName);
            mTvOpenBank.setTextColor(ContextCompat.getColor(context, R.color.haha_black_light));
        }

    }
}
