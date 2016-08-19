package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.utils.Util;

/**
 * Created by wangshirui on 16/8/18.
 */
public class ExamSubmitAlertDialog {
    private Context mContext;
    private TextView mTvAlertInfo;
    private TextView mTvSure;
    private Dialog mDialog;
    private View contentView;
    private String mAlertInfo;
    private onConfirmListener mConfirmListener;

    public interface onConfirmListener {
        boolean clickConfirm();
    }

    public ExamSubmitAlertDialog(Context context, String alertInfo, onConfirmListener confirmListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mAlertInfo = alertInfo;
        mConfirmListener = confirmListener;
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_exam_submit_alert, null);
        mTvAlertInfo = (TextView) contentView.findViewById(R.id.tv_alert_info);
        mTvSure = (TextView) contentView.findViewById(R.id.tv_sure);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvSure.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        mTvAlertInfo.setText(mAlertInfo);
    }

    public void show() {
        mDialog.show();
        if (contentView != null)
            contentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_downup));
    }

    public void dismiss() {
        if (contentView != null)
            contentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_updown));
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
                case R.id.tv_sure:
                    mDialog.dismiss();
                    mConfirmListener.clickConfirm();
                    break;
                default:
                    break;
            }
        }
    };
}
