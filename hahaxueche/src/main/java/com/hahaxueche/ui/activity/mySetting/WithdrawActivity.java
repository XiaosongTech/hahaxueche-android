package com.hahaxueche.ui.activity.mySetting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.student.Bank;
import com.hahaxueche.model.student.ReferalBonusSummary;
import com.hahaxueche.model.student.ReferalBonusTransaction;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.adapter.mySetting.BankAccountAdapter;
import com.hahaxueche.ui.dialog.BaseConfirmDialog;
import com.hahaxueche.ui.dialog.WithdrawDialog;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/3.
 */
public class WithdrawActivity extends MSBaseActivity {
    private ImageButton mIbtnBack;
    private TextView mTvAvailableAmount;
    private EditText mEtWithdrawAmount;
    private TextView mTvConfirmWithdraw;
    private ListView mLvBanks;//提现银行列表
    private FrameLayout mFlyAddBank;//添加银行卡
    private SwipeRefreshLayout mSrlRefresh;//下拉刷新
    private User mUser;
    private ArrayList<Bank> mBanks;
    private int mSelectBankPos = -1;
    private BankAccountAdapter mBankAdapter;

    private boolean isRefresh = false;//是否刷新中
    private SharedPreferencesUtil spUtil;
    public static int REQUEST_CODE_WITHDRAW = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        spUtil = new SharedPreferencesUtil(WithdrawActivity.this);
        initViews();
        initEvents();
        refreshUI();
    }

    private void initViews() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mTvAvailableAmount = Util.instence(this).$(this, R.id.tv_available_amount);
        mEtWithdrawAmount = Util.instence(this).$(this, R.id.et_withdraw_money);
        mTvConfirmWithdraw = Util.instence(this).$(this, R.id.tv_confirm_withdraw);
        mLvBanks = Util.instence(this).$(this, R.id.lv_bank);
        mSrlRefresh = Util.instence(this).$(this, R.id.srl_refresh);
        mFlyAddBank = Util.instence(this).$(this, R.id.fly_add_bank);
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
        mSrlRefresh.setOnRefreshListener(mRefreshListener);
        mSrlRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mLvBanks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mBanks != null && mBanks.size() > 0 && position > -1 && position < mBanks.size()) {
                    for (Bank bank : mBanks) {
                        bank.setSelect(false);
                    }
                    mBanks.get(position).setSelect(true);
                    mBankAdapter.notifyDataSetChanged();
                    mSelectBankPos = position;
                }
            }
        });

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
        mTvAvailableAmount.setText(Util.getMoney(450));
        /********添加测试数据******/
        Bank bank = new Bank();
        bank.setAccount("6217234301000097323");
        bank.setAccount_name("王时睿");
        bank.setBank_name("工商银行");
        if (mBanks == null || mBanks.size() < 1) {
            mBanks = new ArrayList<>();
        }
        mBanks.add(bank);
        /********end************/
        //没有选择银行的时候,默认第一个
        if (mSelectBankPos < 0) {
            mSelectBankPos = 0;
            mBanks.get(0).setSelect(true);
        }
        mBankAdapter = new BankAccountAdapter(WithdrawActivity.this, mBanks, R.layout.adapter_bank_account);
        mLvBanks.setAdapter(mBankAdapter);
        setListViewHeightBasedOnChildren(mLvBanks);
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
                    if (Double.compare(withdrawMoney, 100d) <= 0) {
                        Toast.makeText(WithdrawActivity.this, "提现金额必须大于1元", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        /*int interval = Double.compare(withdrawMoney, availableMoney);
                        if (interval > 0) {
                            Toast.makeText(WithdrawActivity.this, "提现金额不能大于可提现金额", Toast.LENGTH_SHORT).show();
                            return;
                        }*/
                    }
                    if (mSelectBankPos < 0) {
                        Toast.makeText(WithdrawActivity.this, "请选择提现银行", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //确认提款
                    String content = "提现金额：" + Util.getMoney(withdrawMoney) + "\n银行手续费：" + Util.getMoney(200) + "\n实际提现：" + Util.getMoney(withdrawMoney - 200) + "\n银行卡号：" + mBanks.get(mSelectBankPos).getAccount() + "\n持卡人：" + mBanks.get(mSelectBankPos).getAccount_name();
                    BaseConfirmDialog baseConfirmDialog = new BaseConfirmDialog(WithdrawActivity.this, "确认提现", "提现明细", content, "", "确认提现", "取消返回", new BaseConfirmDialog.onConfirmListener() {
                        @Override
                        public boolean clickConfirm() {
                            msPresenter.withdrawBonus(mUser.getStudent().getId(), mBanks.get(mSelectBankPos).getAccount(), mBanks.get(mSelectBankPos).getAccount(), String.valueOf(withdrawMoney), mUser.getSession().getAccess_token(), new MSCallbackListener<ReferalBonusTransaction>() {
                                @Override
                                public void onSuccess(ReferalBonusTransaction data) {
                                    Intent intent = new Intent();
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
                    startActivityForResult(intent, REQUEST_CODE_WITHDRAW);
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //params.height = Util.instence(this).dip2px(height) * listAdapter.getCount() + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_WITHDRAW) {
            if (resultCode == RESULT_OK && null != data && data.getBooleanExtra("isUpdate", false)) {
                refreshUI();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
