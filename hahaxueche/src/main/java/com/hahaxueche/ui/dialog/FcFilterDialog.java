package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.signupLogin.CityModel;
import com.hahaxueche.model.signupLogin.StudentModel;
import com.hahaxueche.model.util.ConstantsModel;
import com.hahaxueche.ui.widget.comboSeekBar.ComboSeekBar;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
    private Button btnFcFilterCancel;
    private Switch swGoldenCoachOly;
    private CheckBox cbLicenseTypeC1;
    private CheckBox cbLicenseTypeC2;
    private LinearLayout llyFcDistanceTvs;//距离筛选文字
    private LinearLayout llyFcPriceTvs;//价格筛选文字
    private List<String> distanceList;
    private List<String> priceList;
    private List<TextView> distanceTvList = new ArrayList<TextView>();
    private List<TextView> priceTvList = new ArrayList<TextView>();
    private String goldenCoachOnly;//只显示金牌教练
    private String licenseType;//C1 还是 C2 后者都行
    private String price;//薪水
    private String distance;//距离

    public interface OnBtnClickListener {
        public void onFliterCoach(String goldenCoachOnly, String licenseType, String price, String distance);
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

    public FcFilterDialog(Context context, String _goldenCoachOnly, String _licenseType, String _price, String _distance,
                          OnBtnClickListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_find_coach_filter, null);
        setContentView(view);
        initFilterArray();
        initView(view);
        goldenCoachOnly = _goldenCoachOnly;
        licenseType = _licenseType;
        price = _price;
        distance = _distance;
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
        swGoldenCoachOly = (Switch) view.findViewById(R.id.sw_golden_coach_only);
        cbLicenseTypeC1 = (CheckBox) view.findViewById(R.id.cb_license_type_c1);
        cbLicenseTypeC2 = (CheckBox) view.findViewById(R.id.cb_license_type_c2);
        cbsDistanceFilter = (ComboSeekBar) view.findViewById(R.id.cbs_distinct_filter);
        if (distanceList != null) {
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
                    if (position == distanceTvList.size() - 1) {
                        distance = "";
                    } else {
                        distance = distanceList.get(position);
                    }

                }
            });
        }
        cbsPriceFilter = (ComboSeekBar) view.findViewById(R.id.cbs_price_filter);
        if (priceList != null) {
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
                    for (int i = 0; i < priceTvList.size(); i++) {
                        if (i <= position) {
                            priceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.tab_bottom_gray));
                        } else {
                            priceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.filter_txt_white_heavy));
                        }
                    }
                    if (position == priceTvList.size() - 1) {
                        price = "";
                    } else {
                        price = priceList.get(position);
                    }
                }
            });
        }
        btnFcFilterSure = (Button) view.findViewById(R.id.btn_fc_filter_sure);
        btnFcFilterSure.setOnClickListener(this);
        btnFcFilterCancel = (Button) view.findViewById(R.id.btn_fc_filter_cancel);
        btnFcFilterCancel.setOnClickListener(this);
        llyFcDistanceTvs = (LinearLayout) view.findViewById(R.id.lly_fc_distance_tvs);
        if (distanceList != null) {
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
        }
        llyFcPriceTvs = (LinearLayout) view.findViewById(R.id.lly_fc_price_tvs);
        DecimalFormat dfInt = new DecimalFormat("#####");
        if (priceList != null) {
            for (String priceStr : priceList) {
                TextView tvDistance = new TextView(mContext);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                tvDistance.setLayoutParams(p);
                tvDistance.setGravity(Gravity.CENTER);
                tvDistance.setTextColor(mContext.getResources().getColor(R.color.filter_txt_white_heavy));
                tvDistance.setTextSize(12);
                tvDistance.setText(Util.getMoney(priceStr));
                llyFcPriceTvs.addView(tvDistance);
                priceTvList.add(tvDistance);
            }
        }
    }

    /**
     * 筛选条件初始化
     */
    public void initFilter() {
        //价格、距离默认最大
        if (distanceList != null) {
            int initDistancePosition = distanceList.size() - 1;
            if (!TextUtils.isEmpty(distance)) {
                for (int i = 0; i < distanceList.size(); i++) {
                    if (distanceList.get(i).equals(distance)) {
                        initDistancePosition = i;
                        break;
                    }
                }
            }
            cbsDistanceFilter.setSelection(initDistancePosition);
            for (int i = 0; i < distanceTvList.size(); i++) {
                if (i <= initDistancePosition) {
                    distanceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.tab_bottom_gray));
                } else {
                    distanceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.filter_txt_white_heavy));
                }
            }
        }
        if (priceList != null) {
            int initPricePosition = priceList.size() - 1;
            if (!TextUtils.isEmpty(price)) {
                for (int i = 0; i < priceList.size(); i++) {
                    if (priceList.get(i).equals(price)) {
                        initPricePosition = i;
                        break;
                    }
                }
            }
            cbsPriceFilter.setSelection(initPricePosition);
            for (int i = 0; i < priceTvList.size(); i++) {
                if (i <= initPricePosition) {
                    priceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.tab_bottom_gray));
                } else {
                    priceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.filter_txt_white_heavy));
                }
            }
        }
        if (!TextUtils.isEmpty(goldenCoachOnly)) {
            swGoldenCoachOly.setChecked(true);
        }
        System.out.println("debug licenseType= " + licenseType);
        if (!TextUtils.isEmpty(licenseType)) {
            if (licenseType.equals("1")) {
                cbLicenseTypeC1.setChecked(true);
            } else if (licenseType.equals("2")) {
                cbLicenseTypeC2.setChecked(true);
            }
        }
    }

    /**
     * 价格和距离array初始化
     */
    private void initFilterArray() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(mContext);
        ConstantsModel constants = spUtil.getConstants();
        if (constants != null) {
            List<CityModel> cityList = constants.getCities();
            int myCityCount = 0;
            //根据城市id加载价格、距离筛选列表
            StudentModel student = spUtil.getStudent();
            String city_id = student.getCity_id();
            for (int i = 0; i < cityList.size(); i++) {
                if (cityList.get(i).getId().equals(city_id)) {
                    myCityCount = i;
                    break;
                }
            }
            CityModel city = cityList.get(myCityCount);
            distanceList = city.getFilters().getRadius();
            priceList = city.getFilters().getPrices();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fc_filter_sure:
                if (mListener != null) {
                    if (swGoldenCoachOly.isChecked()) {
                        goldenCoachOnly = "true";
                    } else {
                        goldenCoachOnly = "";
                    }
                    //license_type 1 = C1, 2 = C2, 3 = c1+c2
                    if (cbLicenseTypeC1.isChecked() && cbLicenseTypeC2.isChecked()) {
                        licenseType = "";
                    } else if (cbLicenseTypeC1.isChecked()) {
                        licenseType = "1";
                    } else if (cbLicenseTypeC2.isChecked()) {
                        licenseType = "2";
                    }
                    mListener.onFliterCoach(goldenCoachOnly, licenseType, price, distance);
                }
                this.dismiss();
                break;
            case R.id.btn_fc_filter_cancel:
                cancel();
                break;
        }
    }
}
