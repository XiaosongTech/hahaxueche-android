package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hahaxueche.R;

/**
 * 寻找教练，排序dialog
 * Created by gibxin on 2016/1/30.
 */
public class FcSortDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private OnBtnClickListener mListener;
    public FcSortDialog(Context context, OnBtnClickListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_find_coach_sort, null);
        setContentView(view);
        initView(view);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.x = 400; // 新位置X坐标
        lp.y = 180; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }
    public interface OnBtnClickListener {
        public void onFindCoachCort(String cityName, String cityId);
    }

    private void initView(View view){

    }

    @Override
    public void onClick(View v) {

    }
}
