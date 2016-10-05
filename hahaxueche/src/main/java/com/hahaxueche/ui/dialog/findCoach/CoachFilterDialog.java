package com.hahaxueche.ui.dialog.findCoach;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.user.User;
import com.hahaxueche.ui.widget.comboSeekBar.ComboSeekBar;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/10/2.
 */

public class CoachFilterDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;
    private ComboSeekBar mCbsDistance;
    private ComboSeekBar mCbsPrice;
    private TextView mTvCancel;
    private TextView mTvSure;
    private Switch mSwGoldenCoachOnly;
    private Switch mSwVipCoachOnly;
    private CheckBox mCbLicenseTypeC1;
    private CheckBox mCbLicenseTypeC2;
    private String mSelectDistance;
    private String mSelectPrice;
    private OnFilterListener mOnFilterListener;

    public interface OnFilterListener {
        void filter(String distance, String price, boolean isGoldenCoachOnly, boolean isVipOnly, boolean C1Checked, boolean C2Checked);
    }

    public CoachFilterDialog(Context context, OnFilterListener onFilterListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mOnFilterListener = onFilterListener;
        initView();
        initFilter();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_coach_filter, null);
        mCbsDistance = ButterKnife.findById(contentView, R.id.cbs_distinct);
        mCbsPrice = ButterKnife.findById(contentView, R.id.cbs_price);
        mTvCancel = ButterKnife.findById(contentView, R.id.tv_cancel);
        mTvSure = ButterKnife.findById(contentView, R.id.tv_sure);
        mSwGoldenCoachOnly = ButterKnife.findById(contentView, R.id.sw_golden_coach_only);
        mSwVipCoachOnly = ButterKnife.findById(contentView, R.id.sw_vip_only);
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
                mOnFilterListener.filter(mSelectDistance, mSelectPrice, mSwGoldenCoachOnly.isChecked(),
                        mSwVipCoachOnly.isChecked(), mCbLicenseTypeC1.isChecked(), mCbLicenseTypeC2.isChecked());
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
        User user = application.getSharedPrefUtil().getUser();
        int cityId = 0;//默认武汉
        if (user != null) {
            cityId = user.student.city_id;
        }
        final City myCity = application.getConstants().getCity(cityId);
        /**********距离筛选**********/
        ArrayList<String> distanceTextList = new ArrayList<>();
        for (int i = 0; i < myCity.filters.radius.length; i++) {
            distanceTextList.add(i == myCity.filters.radius.length - 1 ? (myCity.filters.radius[i] + "KM+") : (myCity.filters.radius[i] + "KM"));
        }
        mCbsDistance.setAdapter(distanceTextList);
        int progress = (distanceTextList.size() - 2) * 100 / (distanceTextList.size() - 1);
        mCbsDistance.setProgress(progress >= 95 ? 95 : progress);
        mCbsDistance.setSelection(distanceTextList.size() - 2);
        mSelectDistance = String.valueOf(myCity.filters.radius[distanceTextList.size() - 2]);
        mCbsDistance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == myCity.filters.radius.length - 1) {
                    mSelectDistance = "";//最大刻度时,反馈-1,不给后台传值
                } else {
                    mSelectDistance = String.valueOf(myCity.filters.radius[position]);
                }
            }
        });
        /**********end**********/
        /**********价格筛选**********/
        ArrayList<String> priceTextList = new ArrayList<>();
        for (int i = 0; i < myCity.filters.prices.length; i++) {
            priceTextList.add(i == myCity.filters.prices.length - 1 ? ("¥" + myCity.filters.prices[i] / 100 + "+") : ("¥" + myCity.filters.prices[i] / 100));
        }
        mCbsPrice.setAdapter(priceTextList);
        progress = (priceTextList.size() - 1) * 100 / (priceTextList.size() - 1);
        mCbsPrice.setProgress(progress >= 95 ? 95 : progress);
        mCbsPrice.setSelection(priceTextList.size() - 1);
        mSelectPrice = String.valueOf(myCity.filters.prices[priceTextList.size() - 1]);
        mCbsPrice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == myCity.filters.prices.length - 1) {
                    mSelectPrice = "";//最大刻度时,反馈-1,不给后台传值
                } else {
                    mSelectPrice = String.valueOf(myCity.filters.prices[position]);
                }
            }
        });
        /**********end**********/
    }
}
