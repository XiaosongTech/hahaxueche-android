package com.hahaxueche.ui.dialog.myPage;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.util.Utils;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/12/13.
 */

public class ReferDetailDialog {
    private Context mContext;
    private Dialog mDialog;
    private ImageView mIvClose;
    private TextView mTvReferDetail;//取消
    private boolean mIsAgent;
    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener {
        void callCustomerService();

        void onlineAsk();
    }

    public ReferDetailDialog(Context context, boolean isAgent, OnButtonClickListener onButtonClickListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mIsAgent = isAgent;
        mOnButtonClickListener = onButtonClickListener;
        initView();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.dialog_refer_detail, null);
        mIvClose = ButterKnife.findById(contentView, R.id.iv_close);
        mTvReferDetail = ButterKnife.findById(contentView, R.id.tv_refer_detail);
        if (mIsAgent) {
            mTvReferDetail.setText(R.string.refer_detail_text_agent);
        }
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mIvClose.setOnClickListener(mClickListener);
        String referDetail = mTvReferDetail.getText().toString();
        SpannableString spReferDetail = new SpannableString(referDetail);
        spReferDetail.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mDialog.dismiss();
                mOnButtonClickListener.callCustomerService();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }
        }, referDetail.indexOf("400"), referDetail.indexOf("6006") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spReferDetail.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mDialog.dismiss();
                mOnButtonClickListener.onlineAsk();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }
        }, referDetail.indexOf("在线客服"), referDetail.indexOf("在线客服") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvReferDetail.setText(spReferDetail);
        mTvReferDetail.setHighlightColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
        mTvReferDetail.setMovementMethod(LinkMovementMethod.getInstance());

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
        wl.width = Utils.instence(mContext).getDm().widthPixels * 7 / 10;
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
                default:
                    break;
            }
        }
    };
}
