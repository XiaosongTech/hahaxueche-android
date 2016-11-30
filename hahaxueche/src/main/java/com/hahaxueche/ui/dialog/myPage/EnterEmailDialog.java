package com.hahaxueche.ui.dialog.myPage;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/30.
 */

public class EnterEmailDialog {
    private Context mContext;
    private Dialog mDialog;
    private EditText mEtEmail;
    private EditText mEtConfirmEmail;
    private TextView mTvCancel;//取消
    private TextView mTvSend;//保存
    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener {
        void send(String email);
    }

    public EnterEmailDialog(Context context, OnButtonClickListener onButtonClickListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mOnButtonClickListener = onButtonClickListener;
        initView();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.dialog_enter_email, null);
        mEtEmail = ButterKnife.findById(contentView, R.id.et_email);
        mEtConfirmEmail = ButterKnife.findById(contentView, R.id.et_confirm_email);
        mTvCancel = ButterKnife.findById(contentView, R.id.tv_cancel);
        mTvSend = ButterKnife.findById(contentView, R.id.tv_send);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvCancel.setOnClickListener(mClickListener);
        mTvSend.setOnClickListener(mClickListener);
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

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_send:
                    String emailAddress = mEtEmail.getText().toString();
                    if (TextUtils.isEmpty(emailAddress)) {
                        Toast.makeText(mContext, "请输入邮箱地址！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String emailAddressConfirm = mEtConfirmEmail.getText().toString();
                    if (TextUtils.isEmpty(emailAddressConfirm)) {
                        Toast.makeText(mContext, "请再次输入邮箱地址！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!emailAddress.equals(emailAddressConfirm)) {
                        Toast.makeText(mContext, "邮箱地址不一致！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Utils.isEmail(emailAddress)) {
                        Toast.makeText(mContext, "邮箱格式错误！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mDialog.dismiss();
                    mOnButtonClickListener.send(emailAddress);
                    break;
                case R.id.tv_cancel:
                    mDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };
}
