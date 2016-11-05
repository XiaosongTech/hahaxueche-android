package com.hahaxueche.ui.dialog.findCoach;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.ui.widget.comboSeekBar.ComboSeekBar;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class PartnerFilterDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;
    private ComboSeekBar mCbsPrice;
    private TextView mTvCancel;
    private TextView mTvSure;
    private CheckBox mCbLicenseTypeC1;
    private CheckBox mCbLicenseTypeC2;
    private String mSelectPrice;
    private OnFilterListener mOnFilterListener;

    public interface OnFilterListener {
        void filter(String price, boolean C1Checked, boolean C2Checked);
    }

    public PartnerFilterDialog(Context context, OnFilterListener onFilterListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mOnFilterListener = onFilterListener;
        initView();
        initFilter();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_partner_filter, null);
        mCbsPrice = ButterKnife.findById(contentView, R.id.cbs_price);
        mTvCancel = ButterKnife.findById(contentView, R.id.tv_cancel);
        mTvSure = ButterKnife.findById(contentView, R.id.tv_sure);
        mCbLicenseTypeC1 = ButterKnife.findById(contentView, R.id.cb_license_type_c1);
        mCbLicenseTypeC2 = ButterKnife.findById(contentView, R.id.cb_license_type_c2);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mOnFilterListener.filter(mSelectPrice, mCbLicenseTypeC1.isChecked(), mCbLicenseTypeC2.isChecked());
            }
        });
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        if (mDialog.isShowing())
            mDialog.dismiss();
    }

    private void setDialogParams() {
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
        //lp.x = 100; // 新位置X坐标
        lp.y = 100; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = Utils.instence(mContext).getDm().widthPixels * 9 / 10;
        dialogWindow.setAttributes(lp);
    }

    private void initFilter() {
        HHBaseApplication application = HHBaseApplication.get(mContext);
        /**********价格筛选**********/
        ArrayList<String> priceTextList = new ArrayList<>();
        priceTextList.add("¥500");
        priceTextList.add("¥1000");
        priceTextList.add("¥1500");
        priceTextList.add("¥2000");
        final ArrayList<String> priceList = new ArrayList<>();
        priceList.add("50000");
        priceList.add("100000");
        priceList.add("150000");
        priceList.add("200000");
        mCbsPrice.setAdapter(priceTextList);
        int progress = (priceTextList.size() - 1) * 100 / (priceTextList.size() - 1);
        mCbsPrice.setProgress(progress >= 95 ? 95 : progress);
        mCbsPrice.setSelection(priceTextList.size() - 1);
        mSelectPrice = String.valueOf(priceList.get(priceTextList.size() - 1));
        mCbsPrice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == priceList.size() - 1) {
                    mSelectPrice = "";//最大刻度时,反馈-1,不给后台传值
                } else {
                    mSelectPrice = String.valueOf(priceList.get(position));
                }
            }
        });
        /**********end**********/
    }
}
