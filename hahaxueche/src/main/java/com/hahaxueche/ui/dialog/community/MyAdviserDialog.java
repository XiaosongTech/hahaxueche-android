package com.hahaxueche.ui.dialog.community;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.user.employee.Adviser;
import com.hahaxueche.util.Utils;

import butterknife.ButterKnife;
import me.grantland.widget.AutofitTextView;

/**
 * Created by wangshirui on 16/9/29.
 */

public class MyAdviserDialog {
    private Context mContext;
    private Dialog mDialog;
    private ImageView mIvCall;
    private TextView mTvName;
    private SimpleDraweeView mIvAvatar;
    private AutofitTextView mTvDescription;
    private onMyConsultantListener mListener;
    private Adviser mAdviser;

    public interface onMyConsultantListener {
        boolean call();
    }

    public MyAdviserDialog(Context context, Adviser adviser, onMyConsultantListener onMyConsultantListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mAdviser = adviser;
        mListener = onMyConsultantListener;
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.dialog_my_adviser, null);
        mTvName = ButterKnife.findById(contentView, R.id.tv_consultant_name);
        mTvDescription = ButterKnife.findById(contentView, R.id.tv_description);
        mIvAvatar = ButterKnife.findById(contentView, R.id.iv_avatar);
        mIvCall = ButterKnife.findById(contentView, R.id.iv_call);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mIvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                mListener.call();
            }
        });
    }

    private void loadDatas() {
        mTvName.setText("嗨, 我是" + mAdviser.name + ", 您的专属学车顾问!");
        mIvAvatar.setImageURI(mAdviser.avatar);
        mTvDescription.setText("\"" + mAdviser.long_intro + "\"");
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
}
