package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

import com.hahaxueche.R;
import com.hahaxueche.ui.util.comboSeekBar.ComboSeekBar;

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
    private int selDistance = 1;
    private LinearLayout llyFcDistanceTvs;
    private List<String> distanceList = Arrays.asList("0KM", "3KM", "5KM", "10KM", "15KM");
    private List<String> priceList = Arrays.asList("￥1000", "￥2000", "￥3000", "￥4000");
    private List<TextView> distanceTvList = new ArrayList<TextView>();

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
                System.out.println("-----position="+position);
                for(int i= 0;i<distanceTvList.size();i++){
                    if(i<=position){
                        distanceTvList.get(i).setTextColor(mContext.getResources().getColor(R.color.tab_bottom_gray));
                    }else{
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
        btnFcFilterSure = (Button) view.findViewById(R.id.btn_fc_filter_sure);
        btnFcFilterSure.setOnClickListener(this);
        llyFcDistanceTvs = (LinearLayout) view.findViewById(R.id.lly_fc_distance_tvs);
        for(String distanceStr : distanceList){
            TextView tvDistance = new TextView(mContext);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
            tvDistance.setLayoutParams(p);
            tvDistance.setGravity(Gravity.CENTER);
            tvDistance.setTextColor(mContext.getResources().getColor(R.color.filter_txt_white_heavy));
            tvDistance.setTextSize(12);
            tvDistance.setText(distanceStr);
            llyFcDistanceTvs.addView(tvDistance);
            distanceTvList.add(tvDistance);
        }

    }

    private void initFilter() {
        cbsDistanceFilter.setSelection(selDistance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fc_filter_sure:
                System.out.println("111111");
                cbsDistanceFilter.setSelection(2);
                break;
        }
    }
}
