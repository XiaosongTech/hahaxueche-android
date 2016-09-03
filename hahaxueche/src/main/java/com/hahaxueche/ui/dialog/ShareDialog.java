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
import com.hahaxueche.model.city.City;
import com.hahaxueche.ui.activity.mySetting.ReferFriendsActivity;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

/**
 * Created by wangshirui on 16/9/3.
 */
public class ShareDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;
    private TextView mTvRefuse;
    private TextView mTvShare;
    private TextView mTvShareTxt;

    public ShareDialog(Context context) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_share, null);
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
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(mContext);
        City city = spUtil.getMyCity();
        String shareTxt = mContext.getResources().getString(R.string.referDialogStr);
        if (city != null) {
            mTvShareTxt.setText(String.format(shareTxt, Util.getMoney(String.valueOf(city.getReferer_bonus()))));
        }
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
                case R.id.tv_refuse:
                    mDialog.dismiss();
                    break;
                case R.id.tv_share:
                    mDialog.dismiss();
                    Intent intent = new Intent(mContext, ReferFriendsActivity.class);
                    mContext.startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
}
