package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.hahaxueche.R;
import com.hahaxueche.ui.util.comboSeekBar.ComboSeekBar;

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

    public interface OnBtnClickListener {
        public void onFliterCoach(String cityName, String cityId);
    }

    @Override
    public void onClick(View v) {

    }
    public FcFilterDialog(Context context,OnBtnClickListener listener){
        super(context);
        mContext = context;
        mListener = listener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_find_coach_filter, null);
        setContentView(view);
        initView(view);

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
     * @param view
     */
    private void initView(View view){
        cbsDistanceFilter = (ComboSeekBar)view.findViewById(R.id.cbs_distinct_filter);
        List<String> distanceList = Arrays.asList("0KM", "3KM", "5KM", "10KM", "15KM");
        cbsDistanceFilter.setAdapter(distanceList);
        cbsDistanceFilter.setOnSeekBarChangeListener(new ComboSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println("----------------------");
                //System.out.println(((ComboSeekBar) seekBar).getValue());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        cbsPriceFilter = (ComboSeekBar)view.findViewById(R.id.cbs_price_filter);
        List<String> priceList = Arrays.asList("￥1000", "￥2000", "￥3000","￥4000");
        cbsPriceFilter.setAdapter(priceList);
        cbsPriceFilter.setOnSeekBarChangeListener(new ComboSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println("----------------------");
                //System.out.println(((ComboSeekBar) seekBar).getValue());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
