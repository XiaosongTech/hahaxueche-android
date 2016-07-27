package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.city.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.util.List;

/**
 * 城市选择dialog
 * Created by gibxin on 2016/1/23.
 */
public class CityChoseDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private OnBtnClickListener mListener;
    private String mCurrentCityId;
    private String mCurrentCityName;
    private int[] viewIds;
    private Button btnCityChoseSure;
    TableLayout tableLayout;

    private OnDismissListener mOnDismissListener;

    private String TAG = "CityChoseDialog";

    public interface OnBtnClickListener {
        public void onCitySelected(String cityName, String cityId);
    }

    public interface OnDismissListener {
        public void onDismiss(String cityId);
    }

    public CityChoseDialog(Context context, OnDismissListener dismissListener, OnBtnClickListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        mOnDismissListener = dismissListener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_city_chose, null);
        setContentView(view);
        tableLayout = (TableLayout) view.findViewById(R.id.tly_city_table);
        btnCityChoseSure = (Button) view.findViewById(R.id.btn_city_chose_sure);
        btnCityChoseSure.setOnClickListener(this);
        initCityData();
        setDialogParams();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mOnDismissListener.onDismiss(mCurrentCityId);
    }

    private void setDialogParams() {
        Window window = CityChoseDialog.this.getWindow(); //得到对话框
        WindowManager.LayoutParams wl = window.getAttributes();
        //wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.width = Util.instence(mContext).getDm().widthPixels * 9 / 10;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //wl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL; //设置重力
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_city_chose_sure:
                if (mListener != null) {
                    mListener.onCitySelected(mCurrentCityName, mCurrentCityId);
                }
                break;
            default:
                break;
        }
    }

    private void initCityData() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(mContext);
        Constants constants = spUtil.getConstants();
        if (constants != null) {
            final List<City> cityList = constants.getCities();
            int rowCount = cityList.size() / 4 + 1;
            viewIds = new int[cityList.size()];
            //int colCount = 3;
            for (int i = 0; i < rowCount; i++) {
                TableRow tr = new TableRow(mContext);
                TableLayout.LayoutParams trLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trLayoutParams.setMargins(0, Util.instence(mContext).dip2px(10), 0, 0);
                tr.setLayoutParams(trLayoutParams);
                tr.setGravity(Gravity.CENTER);
                for (int j = 0; j < 3; j++) {
                    final int index = i * 3 + j;
                    if (index > cityList.size() - 1)
                        break;
                    TextView tv = new TextView(mContext);
                    int viewId = Util.generateViewId();
                    tv.setId(viewId);
                    viewIds[index] = viewId;
                    tv.setText(cityList.get(index).getName());
                    tv.setTextSize(16);
                    TableRow.LayoutParams tvLayoutParams = new TableRow.LayoutParams(Util.instence(mContext).dip2px(80),
                            TableRow.LayoutParams.WRAP_CONTENT);
                    //tvLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    //tvLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    //tv.setWidth(80);
                    //tv.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    tvLayoutParams.setMargins(0, 0, Util.instence(mContext).dip2px(10), 0);
                    tv.setLayoutParams(tvLayoutParams);
                    tv.setPadding(0, Util.instence(mContext).dip2px(10), 0, Util.instence(mContext).dip2px(10));
                    tv.setGravity(Gravity.CENTER);
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(mContext.getResources().getColor(R.color.sLFadeBlack));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView choseTv = (TextView) v;
                            if (mCurrentCityId != null && mCurrentCityId.equals(cityList.get(index).getId())) {
                                choseTv.setBackgroundColor(Color.WHITE);
                                choseTv.setTextColor(mContext.getResources().getColor(R.color.sLFadeBlack));
                            } else {
                                setAllTvUnselected();
                                mCurrentCityId = "";
                                mCurrentCityName = "";
                                choseTv.setBackgroundColor(mContext.getResources().getColor(R.color.app_theme_color));
                                choseTv.setTextColor(Color.WHITE);
                            }
                            mCurrentCityId = cityList.get(index).getId();
                            mCurrentCityName = cityList.get(index).getName();
                        }
                    });
                    tr.addView(tv);
                }
                tableLayout.addView(tr);
            }
        }
    }

    /**
     * 设置所有TextView未选中状态
     */
    private void setAllTvUnselected() {
        for (int viewId : viewIds) {
            TextView tv = (TextView) this.findViewById(viewId);
            tv.setBackgroundColor(Color.WHITE);
            tv.setTextColor(mContext.getResources().getColor(R.color.sLFadeBlack));
        }
    }
}
