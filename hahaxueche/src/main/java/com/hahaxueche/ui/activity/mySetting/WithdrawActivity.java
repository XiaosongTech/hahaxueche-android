package com.hahaxueche.ui.activity.mySetting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.model.student.BankCard;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.dialog.BaseConfirmDialog;
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
    private RelativeLayout mRlyBankCard;
    private TextView mTvBankName;
    private TextView mTvBankRemarks;
    private FrameLayout mFlyAddBank;//添加银行卡
    private SwipeRefreshLayout mSrlRefresh;//下拉刷新
    private TextView mTvWithdrawRecord;//提现记录
    private BankCard mBankCard;
    private ImageView mIvDash;
    private String mAvailableAmount = "0";

    private boolean isRefresh = false;//是否刷新中
    private SharedPreferencesUtil spUtil;
    public static int REQUEST_CODE_ADD_BANK_CARD = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        spUtil = new SharedPreferencesUtil(WithdrawActivity.this);
        initViews();
        initEvents();
        refreshStudent();
    }

    private void initViews() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mTvAvailableAmount = Util.instence(this).$(this, R.id.tv_available_amount);
        mEtWithdrawAmount = Util.instence(this).$(this, R.id.et_withdraw_money);
        mTvConfirmWithdraw = Util.instence(this).$(this, R.id.tv_confirm_withdraw);
        mSrlRefresh = Util.instence(this).$(this, R.id.srl_refresh);
        mFlyAddBank = Util.instence(this).$(this, R.id.fly_add_bank);
        mTvWithdrawRecord = Util.instence(this).$(this, R.id.tv_withdraw_record);
        mRlyBankCard = Util.instence(this).$(this, R.id.rly_bank_card);
        mTvBankName = Util.instence(this).$(this, R.id.tv_bank_name);
        mTvBankRemarks = Util.instence(this).$(this, R.id.tv_bank_remarks);
        mIvDash = Util.instence(this).$(this, R.id.iv_dash);
        mIvDash.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
        mFlyAddBank.setOnClickListener(mClickListener);
        mTvWithdrawRecord.setOnClickListener(mClickListener);
        mSrlRefresh.setOnRefreshListener(mRefreshListener);
        mSrlRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mRlyBankCard.setOnClickListener(mClickListener);
    }

    SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!isRefresh) {
                refreshStudent();
            }
        }
    };

    private void refreshUI() {
        Student student = spUtil.getUser().getStudent();
        mAvailableAmount = student.getBonus_balance();
        mTvAvailableAmount.setText(Util.getMoney(mAvailableAmount));
        //目前只会有一张银行卡
        mBankCard = student.getBank_card();
        if (mBankCard != null) {
            mRlyBankCard.setVisibility(View.VISIBLE);
            mFlyAddBank.setVisibility(View.GONE);
            mTvBankName.setText(mBankCard.getBank_name());
            mTvBankRemarks.setText(mBankCard.getName() + " , 尾号" + mBankCard.getCard_number().substring(mBankCard.getCard_number().length() - 4, mBankCard.getCard_number().length()));
        } else {
            mRlyBankCard.setVisibility(View.GONE);
            mFlyAddBank.setVisibility(View.VISIBLE);
        }
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
                    final double withdrawMoney = Double.parseDouble(withdrawAmount) * 100;
                    //double availableMoney = Double.parseDouble(mReferalBonusSummary.getAvailable_to_redeem());
                    if (Double.compare(withdrawMoney, 10000d) < 0) {
                        Toast.makeText(WithdrawActivity.this, "提现金额不能低于100元", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        int interval = Double.compare(withdrawMoney, Double.parseDouble(mAvailableAmount) * 100);
                        if (interval > 0) {
                            Toast.makeText(WithdrawActivity.this, "提现金额不能大于可提现金额", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    //确认提款
                    String content = "提现金额：" + Util.getMoney(withdrawMoney) + "\n银行卡号：" + mBankCard.getCard_number() + "\n持卡人：" + mBankCard.getName();
                    BaseConfirmDialog baseConfirmDialog = new BaseConfirmDialog(WithdrawActivity.this, "确认提现", "提现明细", content, "", "确认提现", "取消返回", new BaseConfirmDialog.onConfirmListener() {
                        @Override
                        public boolean clickConfirm() {
                            msPresenter.withdrawBonus(spUtil.getUser().getStudent().getId(), String.valueOf(withdrawMoney), spUtil.getUser().getSession().getAccess_token(), new MSCallbackListener<BaseApiResponse>() {
                                @Override
                                public void onSuccess(BaseApiResponse data) {
                                    Intent intent = new Intent();
                                    intent.putExtra("isUpdate", true);
                                    setResult(RESULT_OK, intent);
                                    WithdrawActivity.this.finish();
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
                    break;
                case R.id.fly_add_bank:
                    Intent intent = new Intent(WithdrawActivity.this, AddBankActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ADD_BANK_CARD);
                    break;
                case R.id.tv_withdraw_record:
                    //提现记录
                    intent = new Intent(WithdrawActivity.this, WithdrawRecordListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rly_bank_card:
                    if (mBankCard != null) {
                        intent = new Intent(WithdrawActivity.this, AddBankActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bankCard", mBankCard);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, REQUEST_CODE_ADD_BANK_CARD);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void refreshStudent() {
        isRefresh = true;
        mSrlRefresh.setRefreshing(true);
        this.msPresenter.getStudent(spUtil.getUser().getStudent().getId(), spUtil.getUser().getSession().getAccess_token(),
                new MSCallbackListener<Student>() {
                    @Override
                    public void onSuccess(Student student) {
                        User user = spUtil.getUser();
                        user.setStudent(student);
                        spUtil.setUser(user);
                        refreshUI();
                        mSrlRefresh.setRefreshing(false);
                        isRefresh = false;
                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        mSrlRefresh.setRefreshing(false);
                        isRefresh = false;
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_BANK_CARD) {
            if (resultCode == RESULT_OK && null != data && data.getBooleanExtra("isUpdate", false)) {
                refreshStudent();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
