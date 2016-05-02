package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;


/**
 * Created by gibxin on 2016/4/26.
 */
public class ReferFriendsDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;
    private TextView mTvSmsShare;
    private TextView mTvCopyLink;
    private ImageView mIvShareQQ;
    private ImageView mIvShareWeibo;
    private ImageView mTvShareWeixin;
    private ImageView mIvShareFriendCircle;
    private ImageView mIvCancel;

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnShareListener {
        void onShare(int shareType);
    }

    private OnDismissListener mOnDismissListener;
    private OnShareListener mOnShareListener;


    public ReferFriendsDialog(Context context, OnDismissListener onDismissListener, OnShareListener onShareListener) {
        mDialog = new Dialog(context, R.style.FullScreen_Dialog);//全屏半透明风格
        mContext = context;
        mOnDismissListener = onDismissListener;
        mOnShareListener = onShareListener;
        initView();
        initEvent();
        setDialogParams();
        mDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mDialog.dismiss();
                    mOnDismissListener.onDismiss();
                }
                return true;
            }
        });
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_refer_friends, null);
        mTvSmsShare = (TextView) contentView.findViewById(R.id.tv_sms_share);
        mTvCopyLink = (TextView) contentView.findViewById(R.id.tv_copy_link);
        mIvShareQQ = (ImageView) contentView.findViewById(R.id.iv_share_qq);
        mIvShareWeibo = (ImageView) contentView.findViewById(R.id.iv_share_weibo);
        mTvShareWeixin = (ImageView) contentView.findViewById(R.id.iv_share_weixin);
        mIvShareFriendCircle = (ImageView) contentView.findViewById(R.id.iv_share_friend_circle);
        mIvCancel = (ImageView) contentView.findViewById(R.id.iv_cancel);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvSmsShare.setOnClickListener(mClickListener);
        mTvCopyLink.setOnClickListener(mClickListener);
        mIvShareQQ.setOnClickListener(mClickListener);
        mIvShareWeibo.setOnClickListener(mClickListener);
        mTvShareWeixin.setOnClickListener(mClickListener);
        mIvShareFriendCircle.setOnClickListener(mClickListener);
        mIvCancel.setOnClickListener(mClickListener);
    }

    public void show() {
        mDialog.show();
        if (contentView != null)
            contentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_downup));
    }

    public void dismiss() {
        if (contentView != null)
            contentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_updown));
        if (mDialog.isShowing()) {
            mDialog.dismiss();
            mOnDismissListener.onDismiss();
        }
    }

    private void setDialogParams() {
        Window window = mDialog.getWindow(); //得到对话框
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wl);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_sms_share:
                    mDialog.dismiss();
                    mOnShareListener.onShare(0);
                    break;
                case R.id.tv_copy_link:
                    mDialog.dismiss();
                    mOnShareListener.onShare(1);
                    break;
                case R.id.iv_share_qq:
                    mDialog.dismiss();
                    mOnShareListener.onShare(2);
                    break;
                case R.id.iv_share_weibo:
                    mDialog.dismiss();
                    mOnShareListener.onShare(3);
                    break;
                case R.id.iv_share_weixin:
                    mDialog.dismiss();
                    mOnShareListener.onShare(4);
                    break;
                case R.id.iv_share_friend_circle:
                    mDialog.dismiss();
                    mOnShareListener.onShare(5);
                    break;
                case R.id.iv_cancel:
                    mDialog.dismiss();
                    mOnDismissListener.onDismiss();
                default:
                    break;
            }
        }
    };
}
