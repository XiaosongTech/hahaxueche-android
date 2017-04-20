package com.hahaxueche.ui.dialog.homepage;

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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.user.User;
import com.hahaxueche.util.Utils;

/**
 * Created by wangshirui on 2017/4/17.
 */

public class GetUserIdentityDialog {
    private Context mContext;
    private TextView mTvTitle;
    private EditText mEtPhone;
    private TextView mTvSure;
    private String mTitle;
    private String mHints;
    private String mSureText;
    private Dialog mDialog;

    private OnIdentityGetListener mOnIdentityGetListener;

    public interface OnIdentityGetListener {
        void getCellPhone(String cellPhone);
    }


    public GetUserIdentityDialog(Context context, String title, String hints, String sureText,
                                 OnIdentityGetListener listener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mTitle = title;
        mHints = hints;
        mSureText = sureText;
        mOnIdentityGetListener = listener;
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void initView() {
        View contentView = View.inflate(mContext, R.layout.dialog_get_user_identity, null);
        mTvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        mEtPhone = (EditText) contentView.findViewById(R.id.et_phone);
        mTvSure = (TextView) contentView.findViewById(R.id.tv_sure);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvSure.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        mTvTitle.setText(mTitle);
        mTvSure.setText(mSureText);
        mEtPhone.setHint(mHints);
        HHBaseApplication application = HHBaseApplication.get(mContext);
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.student != null && !TextUtils.isEmpty(user.student.cell_phone)) {
            mEtPhone.setText(user.student.cell_phone);
        }
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
                case R.id.tv_sure:
                    String cellPhone = mEtPhone.getText().toString();
                    if (TextUtils.isEmpty(cellPhone)) {
                        Toast.makeText(mContext, "手机号不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    try {
                        Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(cellPhone, "CN");
                        if (!phoneUtil.isValidNumber(chNumberProto)) {
                            Toast.makeText(mContext, "您的手机号码格式有误", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberParseException e) {
                        Toast.makeText(mContext, "您的手机号码格式有误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mOnIdentityGetListener.getCellPhone(cellPhone);
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };
}
