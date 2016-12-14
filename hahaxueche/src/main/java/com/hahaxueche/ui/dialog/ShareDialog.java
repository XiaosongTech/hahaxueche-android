package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hahaxueche.R;

import butterknife.ButterKnife;

/**
 * 分享dialog页面
 * Created by gibxin on 2016/2/21.
 */
public class ShareDialog extends Dialog {
    private Context mContext;
    private TextView tvShareQQ;
    private TextView tvShareWeibo;
    private TextView tvShareWeixin;
    private TextView tvShareFriendCircle;
    private TextView tvShareCancel;
    private TextView tvShareQzone;
    private TextView tvShareSms;

    public interface OnShareListener {
        void onShare(int shareType);
    }

    private OnShareListener mOnShareListener;

    public ShareDialog(Context context, OnShareListener onShareListener) {
        super(context);
        mContext = context;
        mOnShareListener = onShareListener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_share, null);
        setContentView(view);
        initView(view);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }


    /**
     * 控件初始化
     *
     * @param view
     */
    private void initView(View view) {
        tvShareWeixin = ButterKnife.findById(view, R.id.tv_share_weixin);
        tvShareWeixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(0);
                dismiss();
            }
        });
        tvShareFriendCircle = ButterKnife.findById(view, R.id.tv_share_friend_circle);
        tvShareFriendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(1);
                dismiss();
            }
        });
        tvShareQQ = ButterKnife.findById(view, R.id.tv_share_qq);
        tvShareQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(2);
            }
        });
        tvShareWeibo = ButterKnife.findById(view, R.id.tv_share_weibo);
        tvShareWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(3);
                dismiss();
            }
        });
        tvShareQzone = ButterKnife.findById(view, R.id.tv_share_qzone);
        tvShareQzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(4);
                dismiss();
            }
        });
        tvShareSms = ButterKnife.findById(view, R.id.tv_share_sms);
        tvShareSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnShareListener.onShare(5);
                dismiss();
            }
        });
        tvShareCancel = ButterKnife.findById(view, R.id.tv_share_cancel);
        tvShareCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }


}
