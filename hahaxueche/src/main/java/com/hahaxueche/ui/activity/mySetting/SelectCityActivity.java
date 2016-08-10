package com.hahaxueche.ui.activity.mySetting;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.city.City;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.util.List;

/**
 * Created by wangshirui on 16/8/7.
 */
public class SelectCityActivity extends MSBaseActivity {

    private EditText mEtSearchCityName;//搜索框
    private LinearLayout mLlyHotCity;

    private SharedPreferencesUtil spUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        spUtil = new SharedPreferencesUtil(context);
        initViews();
        initEvents();
        loadHotCities();
    }

    private void initViews() {
        mEtSearchCityName = Util.instence(this).$(this, R.id.et_search_city_name);
        mLlyHotCity = Util.instence(this).$(this, R.id.lly_hot_city);
    }

    private void initEvents() {

    }

    /**
     * 加载热门城市
     */
    private void loadHotCities() {
        if (spUtil.getConstants() == null) return;
        List<City> cities = spUtil.getConstants().getCities();
        if (cities == null || cities.size() < 1) return;
        int rowCount = cities.size() / 4 + 1;
        for (int i = 0; i < rowCount; i++) {
            LinearLayout honLayout = new LinearLayout(context);
            LinearLayout.LayoutParams honLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin10dp = Util.instence(context).dip2px(10);
            honLayoutParams.setMargins(0, margin10dp, 0, 0);
            honLayout.setLayoutParams(honLayoutParams);
            honLayout.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 3; j++) {
                final int index = i * 3 + j;
                TextView tv = new TextView(context);
                LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                if (j != 0) {
                    tvLayoutParams.setMargins(margin10dp, 0, 0, 0);
                } else {
                    tvLayoutParams.setMargins(0, 0, 0, 0);
                }
                tv.setLayoutParams(tvLayoutParams);
                int topPadding = Util.instence(context).dip2px(5);
                tv.setPadding(0, topPadding, 0, topPadding);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(16);
                if (index < cities.size()) {
                    tv.setText(cities.get(index).getName());
                    tv.setBackgroundColor(ContextCompat.getColor(context, R.color.haha_white));
                    tv.setTextColor(ContextCompat.getColor(context, R.color.haha_black_light));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
                honLayout.addView(tv);
            }
            mLlyHotCity.addView(honLayout);
        }
    }
}
