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

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/2/21.
 */

public class ManualUploadDialog {
    private Context mContext;
    private Dialog mDialog;
    private EditText mEtName;
    private EditText mEtIdCardNumber;
    private TextView mTvCancel;//取消
    private TextView mTvUpload;//保存
    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener {
        void upload(String name, String idCardNumber);
    }

    public ManualUploadDialog(Context context, OnButtonClickListener onButtonClickListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mOnButtonClickListener = onButtonClickListener;
        initView();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.dialog_maunal_upload, null);
        mEtName = ButterKnife.findById(contentView, R.id.et_name);
        mEtIdCardNumber = ButterKnife.findById(contentView, R.id.et_id_card_number);
        mTvCancel = ButterKnife.findById(contentView, R.id.tv_cancel);
        mTvUpload = ButterKnife.findById(contentView, R.id.tv_upload);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvCancel.setOnClickListener(mClickListener);
        mTvUpload.setOnClickListener(mClickListener);
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
                case R.id.tv_upload:
                    String name = mEtName.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(mContext, "请输入真实姓名！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String idCardNumber = mEtIdCardNumber.getText().toString();
                    if (TextUtils.isEmpty(idCardNumber)) {
                        Toast.makeText(mContext, "请输入身份证号码！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mDialog.dismiss();
                    mOnButtonClickListener.upload(name, idCardNumber);
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
