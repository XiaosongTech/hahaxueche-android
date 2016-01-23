package com.hahaxueche.ui.dialog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hahaxueche.R;
import com.hahaxueche.model.signupLogin.CitiesModel;
import com.hahaxueche.model.signupLogin.CityModel;
import com.hahaxueche.utils.Util;

import java.lang.reflect.Type;
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

    private String TAG = "CityChoseDialog";

    public interface OnBtnClickListener {
        public void onCitySelected(String cityName, String cityId);
    }

    public CityChoseDialog(Context context, OnBtnClickListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_city_chose, null);
        setContentView(view);
        tableLayout = (TableLayout) view.findViewById(R.id.tly_city_table);
        btnCityChoseSure = (Button) view.findViewById(R.id.btn_city_chose_sure);
        btnCityChoseSure.setOnClickListener(this);
        initCityData();
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.x = 100; // 新位置X坐标
        lp.y = 300; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
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
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("constants", Activity.MODE_PRIVATE);
        String citiesStr = sharedPreferences.getString("constants", "");
        Gson gson = new Gson();
        Type type = new TypeToken<CitiesModel>() {
        }.getType();
        CitiesModel cities = gson.fromJson(citiesStr, type);
        final List<CityModel> cityList = cities.getCities();
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
                Log.v(TAG, "index->" + index);
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
