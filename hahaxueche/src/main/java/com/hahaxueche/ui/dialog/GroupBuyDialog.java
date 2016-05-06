package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hahaxueche.R;
import com.hahaxueche.utils.Util;

/**
 * Created by Administrator on 2016/5/6.
 */
public class GroupBuyDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;
    private ImageView mIvClose;
    private TextView mTvGroupBuy;
    private EditText mEtName;
    private EditText mEtPhoneNumber;
    private String mName;
    private String mPhoneNumber;
    private OnConfirmListener mOnConfirmListener;

    public interface OnConfirmListener {
        void onGroupBuy(String name, String cellPhone);
    }

    public GroupBuyDialog(Context context, String name, String phoneNumber, OnConfirmListener onConfirmListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        this.mContext = context;
        this.mOnConfirmListener = onConfirmListener;
        if (!TextUtils.isEmpty(name)) {
            mName = name;
        }
        if (!TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumber = phoneNumber;
        }
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_group_buy_2380, null);
        mIvClose = (ImageView) contentView.findViewById(R.id.iv_close);
        mTvGroupBuy = (TextView) contentView.findViewById(R.id.tv_group_buy);
        mEtName = (EditText) contentView.findViewById(R.id.et_real_name);
        mEtPhoneNumber = (EditText) contentView.findViewById(R.id.et_contact_phone);
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mEtName.setBackgroundResource(R.drawable.edittext_corner_orange);
            }
        });
        mEtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mEtPhoneNumber.setBackgroundResource(R.drawable.edittext_corner_orange);
            }
        });
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mIvClose.setOnClickListener(mClickListener);
        mTvGroupBuy.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        if (!TextUtils.isEmpty(mName)) {
            mEtName.setText(mName);
        }
        if (!TextUtils.isEmpty(mPhoneNumber)) {
            mEtPhoneNumber.setText(mPhoneNumber);
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
                case R.id.iv_close:
                    mDialog.dismiss();
                    break;
                case R.id.tv_group_buy:
                    mName = mEtName.getText().toString();
                    mPhoneNumber = mEtPhoneNumber.getText().toString();
                    if (TextUtils.isEmpty(mName)) {
                        Toast.makeText(mContext, "您的姓名不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(mPhoneNumber)) {
                        Toast.makeText(mContext, "您的手机号不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                        try {
                            Phonenumber.PhoneNumber chNumberProto = phoneUtil.parse(mPhoneNumber, "CN");
                            if (!phoneUtil.isValidNumber(chNumberProto)) {
                                Toast.makeText(mContext, "您的手机号码格式有误！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberParseException e) {
                            Toast.makeText(mContext, "您的手机号码格式有误！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    mDialog.dismiss();
                    mOnConfirmListener.onGroupBuy(mName, mPhoneNumber);
                    break;
                default:
                    break;
            }
        }
    };
}
