package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.util.Utils;

/**
 * Created by wangshirui on 2016/11/26.
 */

public class BaseAlertDialog extends Dialog {
    private Context mContext;
    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mTvSure;
    private String mTitle;
    private String mContent;
    private String mSureText;
    private onButtonClickListener mOnButtonClickListener;

    public interface onButtonClickListener {
        void sure();
    }


    public BaseAlertDialog(Context context, String title, String content, String sureText, onButtonClickListener listener) {
        super(context);
        mContext = context;
        mTitle = title;
        mContent = content;
        mSureText = sureText;
        mOnButtonClickListener = listener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_base_alert, null);
        setContentView(view);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mTvContent = (TextView) view.findViewById(R.id.tv_content);
        mTvSure = (TextView) view.findViewById(R.id.tv_sure);
        initEvent();
        loadDatas();
        setDialogParams();
    }


    private void initEvent() {
        mTvSure.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        mTvTitle.setText(mTitle);
        mTvContent.setText(mContent);
        mTvSure.setText(mSureText);
    }

    private void setDialogParams() {
        Window window = this.getWindow(); //得到对话框
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = Utils.instence(mContext).getDm().widthPixels;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_sure:
                    dismiss();
                    mOnButtonClickListener.sure();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }
}
