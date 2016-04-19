package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.utils.Util;

/**
 * Created by gibxin on 2016/4/18.
 */
public class BaseAlertDialog {
    private Context mContext;
    private TextView mTvTitle;
    private TextView mTvSubtile;
    private TextView mTvContent;
    private TextView mTvSure;
    private View mVwTitleLine;
    private Dialog mDialog;
    private View contentView;
    private String title;
    private String subtitle;
    private String content;
    private String mTitle;
    private String mSubtitle;
    private String mContent;

    public BaseAlertDialog(Context context, String title, String subtitle, String content) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mTitle = title;
        mSubtitle = subtitle;
        mContent = content;
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_base_alert, null);
        mTvTitle = (TextView) contentView.findViewById(R.id.tv_base_alert_dialog_title);
        mVwTitleLine = contentView.findViewById(R.id.vw_base_alert_dialog_line);
        mTvSubtile = (TextView) contentView.findViewById(R.id.tv_base_alert_dialog_subtitle);
        mTvContent = (TextView) contentView.findViewById(R.id.tv_base_alert_dialog_content);
        mTvSure = (TextView) contentView.findViewById(R.id.tv_base_alert_dialog_sure);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvSure.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        if (TextUtils.isEmpty(mTitle)) {
            mTvTitle.setVisibility(View.GONE);
            mVwTitleLine.setVisibility(View.GONE);
        } else {
            mTvTitle.setText(mTitle);
        }
        mTvSubtile.setText(mSubtitle);
        mTvContent.setText(mContent);
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
                case R.id.tv_base_alert_dialog_sure:
                    mDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };


}
