package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hahaxueche.R;


/**
 * 确认打款dialog
 * Created by gibxin on 2016/3/3.
 */
public class TransferConfirmDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private TextView tvPsDescriptionDialog;
    private TextView tvSureTransferDialog;
    private TextView tvTransferNotNow;
    private OnBtnClickListener mListener;
    private String mDescription;

    public interface OnBtnClickListener {
        public void onTransfer();
    }

    public TransferConfirmDialog(Context context, String description, OnBtnClickListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        mDescription = description;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_transfer_confirm, null);
        setContentView(view);
        tvPsDescriptionDialog = (TextView) view.findViewById(R.id.tv_ps_description_dialog);
        tvSureTransferDialog = (TextView) view.findViewById(R.id.tv_sure_transfer_dialog);
        tvTransferNotNow = (TextView) view.findViewById(R.id.tv_transfer_not_now);
        initViews();
        initEvents();
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        //lp.x = 100; // 新位置X坐标
        //lp.y = 150; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.width = (int) (d.getWidth() * 0.8);
        //lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    private void initViews() {
        tvPsDescriptionDialog.setText(mDescription);
    }

    private void initEvents() {
        tvSureTransferDialog.setOnClickListener(this);
        tvTransferNotNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure_transfer_dialog:
                if (mListener != null) {
                    mListener.onTransfer();
                }
                break;
            case R.id.tv_transfer_not_now:
                dismiss();
        }
    }
}
