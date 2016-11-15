package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.util.Utils;

/**
 * Created by gibxin on 2016/4/19.
 */
public class BaseConfirmDialog {
    private Context mContext;
    private TextView mTvTitle;
    private TextView mTvSubtile;
    private TextView mTvContent;
    private TextView mTvTip;
    private View mVwTipLine;
    private TextView mTvConfirm;
    private TextView mTvCancel;
    private String mTitle;
    private String mSubtitle;
    private String mContent;
    private String mTip;
    private String mConfirmString;
    private String mCancelString;
    private Dialog mDialog;
    private View contentView;

    private onClickListener mOnClickListener;

    public interface onClickListener {
        void clickConfirm();

        void clickCancel();
    }

    public BaseConfirmDialog(Context context, String title, String subtitle, String content, String tip, String confirmString, String cancelString, onClickListener onClickListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mTitle = title;
        mSubtitle = subtitle;
        mTip = tip;
        mConfirmString = confirmString;
        mCancelString = cancelString;
        mContent = content;
        mOnClickListener = onClickListener;
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_base_confirm, null);
        mTvTitle = (TextView) contentView.findViewById(R.id.tv_base_confirm_title);
        mTvSubtile = (TextView) contentView.findViewById(R.id.tv_base_confirm_subtitle);
        mTvContent = (TextView) contentView.findViewById(R.id.tv_base_confirm_content);
        mTvTip = (TextView) contentView.findViewById(R.id.vw_base_confirm_tips);
        mVwTipLine = contentView.findViewById(R.id.vw_base_confirm_tip_line);
        mTvConfirm = (TextView) contentView.findViewById(R.id.tv_base_confirm_sure);
        mTvCancel = (TextView) contentView.findViewById(R.id.tv_base_confirm_cancel);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvConfirm.setOnClickListener(mClickListener);
        mTvCancel.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        if (TextUtils.isEmpty(mTip)) {
            mTvTip.setVisibility(View.GONE);
            mVwTipLine.setVisibility(View.GONE);
        } else {
            mTvTip.setVisibility(View.VISIBLE);
            mVwTipLine.setVisibility(View.VISIBLE);
            mTvTip.setText(mTip);
        }
        mTvTitle.setText(mTitle);
        mTvSubtile.setText(mSubtitle);
        mTvContent.setText(mContent);
        mTvConfirm.setText(mConfirmString);
        mTvCancel.setText(mCancelString);
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
                case R.id.tv_base_confirm_sure:
                    mDialog.dismiss();
                    mOnClickListener.clickConfirm();
                    break;
                case R.id.tv_base_confirm_cancel:
                    mDialog.dismiss();
                    mOnClickListener.clickCancel();
                    break;
                default:
                    break;
            }
        }
    };
}
