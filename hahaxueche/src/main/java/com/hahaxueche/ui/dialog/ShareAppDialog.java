package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    private TextView mTvRefuse;
    private TextView mTvShare;
    private TextView mTvShareTxt;
    private int mBonus;

    public ShareAppDialog(Context context, int bonus) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mBonus = bonus;
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.dialog_share_app, null);
        mTvRefuse = (TextView) contentView.findViewById(R.id.tv_refuse);
        mTvShare = (TextView) contentView.findViewById(R.id.tv_share);
        mTvShareTxt = (TextView) contentView.findViewById(R.id.tv_share_txt);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvRefuse.setOnClickListener(mClickListener);
        mTvShare.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        String shareTxt = mContext.getResources().getString(R.string.referDialogStr);
        mTvShareTxt.setText(String.format(shareTxt, Utils.getMoney(mBonus)));
    }

    public void show() {
        mDialog.show();
//        if (contentView != null)
//            contentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_downup));
    }

    public void dismiss() {
//        if (contentView != null)
//            contentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_updown));
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
                case R.id.tv_refuse:
                    mDialog.dismiss();
                    break;
                case R.id.tv_share:
                    mDialog.dismiss();
                    Intent intent;
                    User user = new SharedPrefUtil(mContext).getUser();
                    if (user == null || !user.isLogin() || !user.student.is_sales_agent) {
                        intent = new Intent(mContext, StudentReferActivity.class);
                    } else {
                        intent = new Intent(mContext, ReferFriendsActivity.class);
                    }
                    mContext.startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
}
