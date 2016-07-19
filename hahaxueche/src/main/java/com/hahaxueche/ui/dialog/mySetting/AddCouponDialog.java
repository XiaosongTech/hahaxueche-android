package com.hahaxueche.ui.dialog.mySetting;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.utils.Util;

/**
 * Created by wangshirui on 16/7/19.
 */
public class AddCouponDialog {

    private Context mContext;
    private Dialog mDialog;
    private View contentView;

    private EditText mEtCoupon;//用户名
    private TextView mTvCancel;//取消
    private TextView mTvSave;//保存

    private OnAddCouponSaveListener mOnSaveListener;

    public interface OnAddCouponSaveListener {
        boolean saveCoupon(String coupon);
    }

    public AddCouponDialog(Context context, OnAddCouponSaveListener onSaveListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        this.mContext = context;
        this.mOnSaveListener = onSaveListener;
        initView();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_add_coupon, null);
        mEtCoupon = (EditText) contentView.findViewById(R.id.et_coupon);
        mTvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        mTvSave = (TextView) contentView.findViewById(R.id.tv_sure);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvSave.setOnClickListener(mClickListener);
        mTvCancel.setOnClickListener(mClickListener);
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
        wl.width = Util.instence(mContext).getDm().widthPixels * 9 / 10;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_cancel:
                    mDialog.dismiss();
                    break;
                case R.id.tv_save:
                    String coupon = mEtCoupon.getText().toString();
                    if (TextUtils.isEmpty(coupon)) {
                        Toast.makeText(mContext, "优惠码不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mOnSaveListener.saveCoupon(coupon);
                    break;
                default:
                    break;
            }
        }
    };
}
