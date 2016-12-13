package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.util.Utils;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/12/13.
 */

public class MainShareDialog {
    private Context mContext;
    private Dialog mDialog;
    private ImageView mIvClose;
    private TextView mTvVoucherAmount;
    private TextView mTvVoucherName;
    private TextView mTvVoucherExpiredAt;
    private TextView mTvShare;
    private Voucher mVoucher;
    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener {
        void shareToFriends();
    }

    public MainShareDialog(Context context, Voucher voucher, OnButtonClickListener onButtonClickListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mVoucher = voucher;
        mOnButtonClickListener = onButtonClickListener;
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void loadDatas() {
        mTvVoucherAmount.setText(Utils.getMoney(mVoucher.amount));
        mTvVoucherName.setText(mVoucher.title);
        mTvVoucherExpiredAt.setText("有效期 " + mVoucher.expired_at);
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.dialog_main_share, null);
        mIvClose = ButterKnife.findById(contentView, R.id.iv_close);
        mTvVoucherAmount = ButterKnife.findById(contentView, R.id.tv_voucher_amount);
        mTvVoucherName = ButterKnife.findById(contentView, R.id.tv_voucher_name);
        mTvVoucherExpiredAt = ButterKnife.findById(contentView, R.id.tv_voucher_expired_at);
        mTvShare = ButterKnife.findById(contentView, R.id.tv_share);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mIvClose.setOnClickListener(mClickListener);
        mTvShare.setOnClickListener(mClickListener);
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
        wl.width = Utils.instence(mContext).getDm().widthPixels * 9 / 10;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_close:
                    mDialog.dismiss();
                    break;
                case R.id.tv_share:
                    mDialog.dismiss();
                    mOnButtonClickListener.shareToFriends();
                    break;
                default:
                    break;
            }
        }
    };
}
