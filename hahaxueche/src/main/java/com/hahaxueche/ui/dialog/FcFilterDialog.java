package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hahaxueche.R;
import com.hahaxueche.model.signupLogin.CitiesModel;
import com.hahaxueche.model.signupLogin.CityModel;
import com.hahaxueche.ui.widget.comboSeekBar.ComboSeekBar;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 寻找教练筛选dialog
 * Created by gibxin on 2016/1/26.
 */
public class FcFilterDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private OnBtnClickListener mListener;
    private ComboSeekBar cbsDistanceFilter;
    private ComboSeekBar cbsPriceFilter;
    private Button btnFcFilterSure;
    private LinearLayout llyFcDistanceTvs;//距离筛选文字
    private LinearLayout llyFcPriceTvs;//价格筛选文字
    private List<String> distanceList;
    private List<String> priceList;
    private List<TextView> distanceTvList = new ArrayList<TextView>();
    private List<TextView> priceTvList = new ArrayList<TextView>();

    public interface OnBtnClickListener {
        public void onFliterCoach(String cityName, String cityId);
    }


    public FcFilterDialog(Context context, OnBtnClickListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_find_coach_filter, null);
        setContentView(view);
        initFilterArray();
        initView(view);
        initFilter();
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.x = 100; // 新位置X坐标
        lp.y = 100; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }


    /**
     * 控件初始化
     *
     * @param view
     */
    private void initView(View view) {
        cbsDistanceFilter = (ComboSeekBar) view.findViewById(R.id.cbs_distinct_filter);
        cbsDistanceFilter.setAdapter(distanceList);
        cbsDistanceFilter.setOnSeekBarChangeListener(new ComboSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        cbsDistanceFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("-----distance position=" + position);
                for (int i = 0; i < distanceTvList.size(); i++) {
                    if (i <= position) {
                        distanceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.tab_bottom_gray));
                    } else {
                        distanceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.filter_txt_white_heavy));
                    }
                }
            }
        });
        cbsPriceFilter = (ComboSeekBar) view.findViewById(R.id.cbs_price_filter);
        cbsPriceFilter.setAdapter(priceList);
        cbsPriceFilter.setOnSeekBarChangeListener(new ComboSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        cbsPriceFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("-----price position=" + position);
                for (int i = 0; i < priceTvList.size(); i++) {
                    if (i <= position) {
                        priceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.tab_bottom_gray));
                    } else {
                        priceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.filter_txt_white_heavy));
                    }
                }
            }
        });
        btnFcFilterSure = (Button) view.findViewById(R.id.btn_fc_filter_sure);
        btnFcFilterSure.setOnClickListener(this);
        llyFcDistanceTvs = (LinearLayout) view.findViewById(R.id.lly_fc_distance_tvs);
        for (String distanceStr : distanceList) {
            TextView tvDistance = new TextView(mContext);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            tvDistance.setLayoutParams(p);
            tvDistance.setGravity(Gravity.CENTER);
            tvDistance.setTextColor(mContext.getResources().getColor(R.color.filter_txt_white_heavy));
            tvDistance.setTextSize(12);
            tvDistance.setText(distanceStr + "KM");
            llyFcDistanceTvs.addView(tvDistance);
            distanceTvList.add(tvDistance);
        }
        llyFcPriceTvs = (LinearLayout) view.findViewById(R.id.lly_fc_price_tvs);
        for (String priceStr : priceList) {
            TextView tvDistance = new TextView(mContext);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            tvDistance.setLayoutParams(p);
            tvDistance.setGravity(Gravity.CENTER);
            tvDistance.setTextColor(mContext.getResources().getColor(R.color.filter_txt_white_heavy));
            tvDistance.setTextSize(12);
            tvDistance.setText("￥" + priceStr);
            llyFcPriceTvs.addView(tvDistance);
            priceTvList.add(tvDistance);
        }
    }

    /**
     * 筛选条件初始化
     */
    public void initFilter() {
        //价格、距离默认最大
        int initDistancePosition = distanceList.size() - 1;
        int initPricePosition = priceList.size() - 1;
        cbsDistanceFilter.setSelection(initDistancePosition);
        cbsPriceFilter.setSelection(initPricePosition);
        for (int i = 0; i < distanceTvList.size(); i++) {
            if (i <= initDistancePosition) {
                distanceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.tab_bottom_gray));
            } else {
                distanceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.filter_txt_white_heavy));
            }
        }
        for (int i = 0; i < priceTvList.size(); i++) {
            if (i <= initPricePosition) {
                priceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.tab_bottom_gray));
            } else {
                priceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.filter_txt_white_heavy));
            }
        }
    }

    /**
     * 价格和距离array初始化
     */
    private void initFilterArray() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("constants", Activity.MODE_PRIVATE);
        String constants = sharedPreferences.getString("constants", "");
        Gson gson = new Gson();
        Type type = new TypeToken<CitiesModel>() {
        }.getType();
        CitiesModel cities = gson.fromJson(constants, type);
        List<CityModel> cityList = cities.getCities();
        CityModel city = cityList.get(0);
        distanceList = city.getFilters().getRadius();
        priceList = city.getFilters().getPrices();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fc_filter_sure:
                break;
        }
    }
}
