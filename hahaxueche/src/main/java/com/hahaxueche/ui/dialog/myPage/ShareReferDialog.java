package com.hahaxueche.ui.dialog.myPage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.ui.dialog.ShareDialog;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/12/13.
 */

public class ShareReferDialog extends Dialog {
    private Context mContext;
    private TextView tvShareQQ;
    private TextView tvShareWeibo;
    private TextView tvShareWeixin;
    private TextView tvShareFriendCircle;
    private TextView tvShareQzone;
    private TextView tvShareSms;
    private ImageView ivClose;

    public interface OnShareListener {
        void onShare(int shareType);
    }

    private ShareDialog.OnShareListener mOnShareListener;

    public ShareReferDialog(Context context, ShareDialog.OnShareListener onShareListener) {
        super(context);
        mContext = context;
        mOnShareListener = onShareListener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_share_refer, null);
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
        ivClose = ButterKnife.findById(view, R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }
}
