package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hahaxueche.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Administrator on 2016/5/9.
 */
public class FAQDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;
    private SubsamplingScaleImageView mIvFaq;

    public FAQDialog(Context context) {
        mDialog = new Dialog(context, R.style.zoom_dialog);
        mContext = context;
        initView();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_faq, null);
        mIvFaq = (SubsamplingScaleImageView) contentView.findViewById(R.id.iv_faq);
        mIvFaq.setImage(ImageSource.resource(R.drawable.commonfaq));
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mIvFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQDialog.this.dismiss();
            }
        });
    }

    private void setDialogParams() {
        Window window = mDialog.getWindow(); //得到对话框
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wl);
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
