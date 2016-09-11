package com.hahaxueche.ui.dialog.login;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.City;
import com.hahaxueche.ui.adapter.login.CityChoseAdapter;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/11.
 */
public class CityChoseDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;
    private TextView mTvSure;
    private GridView mGvCities;
    private onConfirmListener mConfirmListener;

    public interface onConfirmListener {
        boolean clickConfirm();
    }

    public CityChoseDialog(Context context, onConfirmListener confirmListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mConfirmListener = confirmListener;
        initView();
        initEvent();
        loadDatas();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_city_chose, null);
        mTvSure = ButterKnife.findById(contentView, R.id.tv_sure);
        mGvCities = ButterKnife.findById(contentView, R.id.gv_cities);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                mConfirmListener.clickConfirm();
            }
        });
    }

    private void loadDatas() {
        HHBaseApplication application = HHBaseApplication.get(mContext);
        ArrayList<City> cities = application.getConstants().cities;
        if (cities == null) return;
        final CityChoseAdapter cityChoseAdapter = new CityChoseAdapter(mContext, cities);
        mGvCities.setAdapter(cityChoseAdapter);
        mGvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityChoseAdapter.clearSelection(position);
                cityChoseAdapter.notifyDataSetChanged();
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
        Window window = mDialog.getWindow(); //得到对话框
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = Utils.instence(mContext).getDm().widthPixels * 9 / 10;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
    }
}
