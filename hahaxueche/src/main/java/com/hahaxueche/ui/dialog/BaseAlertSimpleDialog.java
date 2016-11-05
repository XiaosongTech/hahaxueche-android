package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.util.Utils;

/**
 * Created by wangshirui on 2016/10/21.
 */

public class BaseAlertSimpleDialog {
    private Context mContext;
    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mTvKnow;
    private String mTitle;
    private String mContent;
    private Dialog mDialog;
    private View contentView;


    public BaseAlertSimpleDialog(Context context, String title, String content) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mTitle = title;
        mContent = content;
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_base_alert_simple, null);
        mTvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        mTvContent = (TextView) contentView.findViewById(R.id.tv_content);
        mTvKnow = (TextView) contentView.findViewById(R.id.tv_know);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvKnow.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        mTvTitle.setText(mTitle);
        mTvContent.setText(mContent);
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

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_know:
                    mDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };
}
