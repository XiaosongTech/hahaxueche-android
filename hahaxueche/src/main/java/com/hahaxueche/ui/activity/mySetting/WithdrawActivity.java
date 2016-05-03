package com.hahaxueche.ui.activity.mySetting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.student.ReferalBonusSummary;
import com.hahaxueche.model.student.ReferalBonusTransaction;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.dialog.BaseConfirmDialog;
import com.hahaxueche.ui.dialog.WithdrawDialog;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

/**
 * Created by Administrator on 2016/5/3.
 */
public class WithdrawActivity extends MSBaseActivity {
    private ImageButton mIbtnBack;
    private TextView mTvAvailableAmount;
    private EditText mEtWithdrawAmount;
    private TextView mTvConfirmWithdraw;
    private ReferalBonusSummary mReferalBonusSummary;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        initViews();
        initEvents();
        loadDatas();
    }

    private void initViews() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mTvAvailableAmount = Util.instence(this).$(this, R.id.tv_available_amount);
        mEtWithdrawAmount = Util.instence(this).$(this, R.id.et_withdraw_money);
        mTvConfirmWithdraw = Util.instence(this).$(this, R.id.tv_confirm_withdraw);
    }

    private void initEvents() {
        mIbtnBack.setOnClickListener(mClickListener);
        mTvConfirmWithdraw.setOnClickListener(mClickListener);
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
    }

    private void loadDatas() {
        Intent intent = getIntent();
        mReferalBonusSummary = (ReferalBonusSummary) intent.getSerializableExtra("referalBonusSummary");
        mTvAvailableAmount.setText(Util.getMoney(mReferalBonusSummary.getAvailable_to_redeem()));
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        mUser = spUtil.getUser();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ibtn_back:
                    WithdrawActivity.this.finish();
                    break;
                case R.id.tv_confirm_withdraw:
                    String withdrawAmount = mEtWithdrawAmount.getText().toString();
                    if (TextUtils.isEmpty(withdrawAmount)) {
                        Toast.makeText(WithdrawActivity.this, "提现金额不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double withdrawMoney = Double.parseDouble(withdrawAmount) * 100;
                    double availableMoney = Double.parseDouble(mReferalBonusSummary.getAvailable_to_redeem());
                    if (withdrawMoney <= 0d) {
                        Toast.makeText(WithdrawActivity.this, "提现金额必须大于0", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        int interval = Double.compare(withdrawMoney, availableMoney);
                        if (interval > 0) {
                            Toast.makeText(WithdrawActivity.this, "提现金额不能大于可提现金额", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    //确认提款
                    WithdrawDialog dialog = new WithdrawDialog(WithdrawActivity.this, withdrawMoney, new WithdrawDialog.onConfirmListener() {
                        @Override
                        public boolean clickConfirm(final String account, final String accountOwnerName, final double withdrawMoney, double counterMoney, double realMoney) {
                            String content = "提现金额：" + Util.getMoney(withdrawMoney) + "\n支付宝手续费：" + Util.getMoney(counterMoney) + "\n实际提现：" + Util.getMoney(realMoney) + "\n支付宝账号：" + account + "\n账号姓名：" + accountOwnerName;
                            BaseConfirmDialog baseConfirmDialog = new BaseConfirmDialog(WithdrawActivity.this, "确认提现", "提现明细", content, "", "确认提现", "取消返回", new BaseConfirmDialog.onConfirmListener() {
                                @Override
                                public boolean clickConfirm() {
                                    msPresenter.withdrawBonus(mUser.getStudent().getId(), account, accountOwnerName, String.valueOf(withdrawMoney), mUser.getSession().getAccess_token(), new MSCallbackListener<ReferalBonusTransaction>() {
                                        @Override
                                        public void onSuccess(ReferalBonusTransaction data) {
                                            Toast.makeText(WithdrawActivity.this, "提现成功", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(String errorEvent, String message) {
                                            Toast.makeText(WithdrawActivity.this, "提现失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return false;
                                }
                            }, new BaseConfirmDialog.onCancelListener() {
                                @Override
                                public boolean clickCancel() {
                                    Toast.makeText(WithdrawActivity.this, "提现取消", Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                            });
                            baseConfirmDialog.show();
                            return true;
                        }
                    }, new WithdrawDialog.onCancelListener() {
                        @Override
                        public boolean clickCancel() {
                            return false;
                        }
                    });
                    dialog.show();
                    break;
                default:
                    break;
            }
        }
    };
}
