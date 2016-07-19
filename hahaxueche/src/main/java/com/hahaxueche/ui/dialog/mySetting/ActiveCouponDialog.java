package com.hahaxueche.ui.dialog.mySetting;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.utils.Util;

/**
 * Created by wangshirui on 16/7/19.
 */
public class ActiveCouponDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;

    private TextView mTvFreeTry;//我要免费试学按钮

    private OnFreeTryListener mFreeTryListener;

    public interface OnFreeTryListener {
        boolean freeTry();
    }

    public ActiveCouponDialog(Context context, OnFreeTryListener onFreeTryListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        this.mContext = context;
        this.mFreeTryListener = onFreeTryListener;
        initView();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_activate_coupon, null);
        mTvFreeTry = (TextView) contentView.findViewById(R.id.tv_free_try);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvFreeTry.setOnClickListener(mClickListener);
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
        wl.width = Util.instence(mContext).getDm().widthPixels * 9 / 10;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_free_try:
                    mFreeTryListener.freeTry();
                    break;
                default:
                    break;
            }
        }
    };
}
