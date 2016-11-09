package com.hahaxueche.ui.dialog.myPage;

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
import com.hahaxueche.util.Utils;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/9.
 */

public class EditUsernameDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;
    private EditText mEtUsername;//用户名
    private ImageView mIvClear;//清除按钮
    private TextView mTvCancel;//取消
    private TextView mTvSave;//保存
    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener {
        void save(String username);
    }

    public EditUsernameDialog(Context context, OnButtonClickListener onButtonClickListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mOnButtonClickListener = onButtonClickListener;
        initView();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_edit_username, null);
        mEtUsername = ButterKnife.findById(contentView, R.id.et_username);
        mIvClear = ButterKnife.findById(contentView, R.id.iv_clear);
        mTvCancel = ButterKnife.findById(contentView, R.id.tv_cancel);
        mTvSave = ButterKnife.findById(contentView, R.id.tv_save);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvCancel.setOnClickListener(mClickListener);
        mTvSave.setOnClickListener(mClickListener);
        mIvClear.setOnClickListener(mClickListener);
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
                case R.id.tv_save:
                    String username = mEtUsername.getText().toString();
                    if (TextUtils.isEmpty(username)) {
                        Toast.makeText(mContext, "用户名不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mDialog.dismiss();
                    mOnButtonClickListener.save(username);
                    break;
                case R.id.tv_cancel:
                    mDialog.dismiss();
                    break;
                case R.id.iv_clear:
                    mEtUsername.setText("");
                    break;
                default:
                    break;
            }
        }
    };
}
