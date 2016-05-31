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
 * Created by Administrator on 2016/5/31.
 */
public class EditUsernameDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;

    private EditText mEtUsername;//用户名
    private ImageView mIvClear;//清除按钮
    private TextView mTvCancel;//取消
    private TextView mTvSave;//保存

    private OnEditUsernameSaveListener mOnSaveListener;

    public interface OnEditUsernameSaveListener {
        boolean saveUserName(String username);
    }

    public EditUsernameDialog(Context context, OnEditUsernameSaveListener onSaveListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        this.mContext = context;
        this.mOnSaveListener = onSaveListener;
        initView();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_edit_username, null);
        mEtUsername = (EditText) contentView.findViewById(R.id.et_username);
        mIvClear = (ImageView) contentView.findViewById(R.id.iv_clear);
        mTvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        mTvSave = (TextView) contentView.findViewById(R.id.tv_save);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvSave.setOnClickListener(mClickListener);
        mTvCancel.setOnClickListener(mClickListener);
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
        //wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.width = Util.instence(mContext).getDm().widthPixels * 9 / 10;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //wl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL; //设置重力
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_clear:
                    mEtUsername.setText("");
                    break;
                case R.id.tv_cancel:
                    mDialog.dismiss();
                    break;
                case R.id.tv_save:
                    String username = mEtUsername.getText().toString();
                    if (TextUtils.isEmpty(username)) {
                        Toast.makeText(mContext, "用户名不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mDialog.dismiss();
                    mOnSaveListener.saveUserName(username);
                    break;
            }
        }
    };
}
