package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.user.User;
import com.hahaxueche.ui.activity.myPage.ReferFriendsActivity;
import com.hahaxueche.ui.activity.myPage.StudentReferActivity;
import com.hahaxueche.util.SharedPrefUtil;
import com.hahaxueche.util.Utils;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ShareAppDialog {
    private Context mContext;
    private Dialog mDialog;
    private ImageView mIvCancel;
    private TextView mTvShare;
    private TextView mTvShareTxt;
    private String mShareText;
    private boolean mIsCancelable;
    private onShareClickListener mOnShareListener;

    public interface onShareClickListener {
        void share();
    }

    public ShareAppDialog(Context context, String shareText, boolean isCancelable, onShareClickListener listener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mShareText = shareText;
        mIsCancelable = isCancelable;
        mOnShareListener = listener;
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.dialog_share_app, null);
        mIvCancel = (ImageView) contentView.findViewById(R.id.iv_cancel);
        mTvShare = (TextView) contentView.findViewById(R.id.tv_share);
        mTvShareTxt = (TextView) contentView.findViewById(R.id.tv_share_txt);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mIvCancel.setOnClickListener(mClickListener);
        mTvShare.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        mTvShareTxt.setText(mShareText);
        mIvCancel.setVisibility(mIsCancelable ? View.VISIBLE : View.INVISIBLE);
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
                case R.id.iv_cancel:
                    mDialog.dismiss();
                    break;
                case R.id.tv_share:
                    mDialog.dismiss();
                    if (mOnShareListener != null) {
                        mOnShareListener.share();
                    } else {
                        Intent intent;
                        User user = new SharedPrefUtil(mContext).getUser();
                        if (user == null || !user.isLogin() || !user.student.is_sales_agent) {
                            intent = new Intent(mContext, StudentReferActivity.class);
                        } else {
                            intent = new Intent(mContext, ReferFriendsActivity.class);
                        }
                        mContext.startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
