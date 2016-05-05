package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.utils.Util;

/**
 * Created by Administrator on 2016/5/3.
 */
public class WithdrawDialog {
    private Context mContext;
    private TextView mTvConfirm;
    private TextView mTvCancel;
    private EditText mEtAccount;
    private EditText mEtAccountOwnerName;
    private Dialog mDialog;
    private View contentView;
    private double mWithdrawMoney;//提现金额
    private double mCounterMoney;//手续费
    private double mRealMoney;//实际金额

    private onConfirmListener mConfirmListener;
    private onCancelListener mCancelListener;

    public interface onConfirmListener {
        public boolean clickConfirm(String account, String accountOwnerName, double withdrawMoney, double counterMoney, double realMoney);
    }

    public interface onCancelListener {
        public boolean clickCancel();
    }

    public WithdrawDialog(Context context, Double withdrawMoney, onConfirmListener confirmListener, onCancelListener cancelListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mConfirmListener = confirmListener;
        mCancelListener = cancelListener;
        mWithdrawMoney = withdrawMoney;
        initView();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_withdraw, null);
        mEtAccount = (EditText) contentView.findViewById(R.id.et_account);
        mEtAccountOwnerName = (EditText) contentView.findViewById(R.id.et_account_owner_name);
        mTvConfirm = (TextView) contentView.findViewById(R.id.tv_confirm);
        mTvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvConfirm.setOnClickListener(mClickListener);
        mTvCancel.setOnClickListener(mClickListener);
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        if (mDialog.isShowing())
            mDialog.dismiss();
    }

    private void setDialogParams() {
        Window window = mDialog.getWindow(); //得到对话框
        WindowManager.LayoutParams wl = window.getAttributes();
        //wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.width = Util.instence(mContext).getDm().widthPixels * 9 / 10;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //wl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL; //设置重力
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_confirm:
                    String account = mEtAccount.getText().toString();
                    if (TextUtils.isEmpty(account)) {
                        Toast.makeText(mContext, "支付宝账户不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String accountOwnerName = mEtAccountOwnerName.getText().toString();
                    if (TextUtils.isEmpty(accountOwnerName)) {
                        Toast.makeText(mContext, "支付宝真实姓名不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    calculateMoney();
                    mDialog.dismiss();
                    mConfirmListener.clickConfirm(account, accountOwnerName, mWithdrawMoney, mCounterMoney, mRealMoney);
                    break;
                case R.id.tv_cancel:
                    mDialog.dismiss();
                    mCancelListener.clickCancel();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 手续费计算
     * 支付宝手续费0.5%，上限和下限分别1和25
     */
    private void calculateMoney() {
        mCounterMoney = mWithdrawMoney * 0.005;
        if (Double.compare(100, mCounterMoney) > 0) {
            mCounterMoney = 100d;
        } else if (Double.compare(mCounterMoney, 2500) > 0) {
            mCounterMoney = 2500d;
        }
        mRealMoney = mWithdrawMoney - mCounterMoney;
    }

}
