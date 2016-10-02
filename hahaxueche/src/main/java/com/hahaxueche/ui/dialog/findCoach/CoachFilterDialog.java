package com.hahaxueche.ui.dialog.findCoach;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.user.User;
import com.hahaxueche.ui.widget.comboSeekBar.ComboSeekBar;
import com.hahaxueche.util.HHLog;
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

    public CoachFilterDialog(Context context) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        initView();
        initEvent();
        initSeekBar();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_coach_filter, null);
        mCbsDistance = ButterKnife.findById(contentView, R.id.cbs_distinct);
        mCbsPrice = ButterKnife.findById(contentView, R.id.cbs_price);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {

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

    private void initSeekBar() {
        HHBaseApplication application = HHBaseApplication.get(mContext);
        User user = application.getSharedPrefUtil().getUser();
        int cityId = 0;//默认武汉
        if (user != null) {
            cityId = user.student.city_id;
        }
        City myCity = application.getConstants().getMyCity(cityId);
        ArrayList<String> distanceTextList = new ArrayList<>();
        for (int i = 0; i < myCity.filters.radius.length; i++) {
            distanceTextList.add(i == myCity.filters.radius.length - 1 ? (myCity.filters.radius[i] + "KM+") : (myCity.filters.radius[i] + "KM"));
        }
        mCbsDistance.setAdapter(distanceTextList);
        int progress = (distanceTextList.size() - 2) * 100 / (distanceTextList.size() - 1);
        mCbsDistance.setProgress(progress);
        mCbsDistance.setSelection(distanceTextList.size() - 2);
        ArrayList<String> priceTextList = new ArrayList<>();
        for (int i = 0; i < myCity.filters.prices.length; i++) {
            priceTextList.add(i == myCity.filters.prices.length - 1 ? ("¥" + myCity.filters.prices[i] / 100 + "+") : ("¥" + myCity.filters.prices[i] / 100));
        }
        mCbsPrice.setAdapter(priceTextList);
        progress = (priceTextList.size() - 1) * 100 / (priceTextList.size() - 1);
        HHLog.v("progress" + progress);
        if (progress == 100) {
            progress = 95;
        }
        mCbsPrice.setProgress(progress);
        mCbsPrice.setSelection(priceTextList.size() - 1);
    }
}
