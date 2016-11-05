package com.hahaxueche.ui.dialog.myPage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;

/**
 * Created by gibxin on 2016/3/2.
 */
public class PaymentStageInfoDialog extends Dialog {
    private Context mContext;
    private boolean mIsPaid = false;
    private String mPaymentStageName;
    private String mDescription;
    private ImageView ivPsInfo;
    private TextView tvPsInfoTitle;
    private TextView tvPsDescription;
    private TextView tvPsAlreadyKnow;

    public PaymentStageInfoDialog(Context context) {
        super(context);
        mContext = context;
    }

    public PaymentStageInfoDialog(Context context, String paymentStageName, String description, boolean isPaid) {
        super(context);
        mContext = context;
        mPaymentStageName = paymentStageName;
        mDescription = description;
        mIsPaid = isPaid;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_payment_stage_info, null);
        setContentView(view);
        ivPsInfo = (ImageView) view.findViewById(R.id.iv_ps_info);
        tvPsInfoTitle = (TextView) view.findViewById(R.id.tv_ps_info_title);
        tvPsDescription = (TextView) view.findViewById(R.id.tv_ps_description);
        tvPsAlreadyKnow = (TextView) view.findViewById(R.id.tv_ps_already_know);
        loadDatas();
        initEvents();
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.width = (int) (d.getWidth() * 0.8);
        dialogWindow.setAttributes(lp);
    }

    private void loadDatas() {
        tvPsDescription.setText(mDescription);
        if (mIsPaid) {
            tvPsInfoTitle.setText(mPaymentStageName + "已打款");
        } else {
            ivPsInfo.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_havenotpay));
            tvPsInfoTitle.setText(mPaymentStageName + "待打款");
            tvPsInfoTitle.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
        }
    }

    private void initEvents() {
        tvPsAlreadyKnow.setOnClickListener(mClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_ps_already_know:
                    dismiss();
                    break;
            }
        }
    };
}
