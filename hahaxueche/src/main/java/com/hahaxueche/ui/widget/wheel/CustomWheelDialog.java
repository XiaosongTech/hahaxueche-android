package com.hahaxueche.ui.widget.wheel;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.ui.widget.wheel.adapters.ArrayWheelAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yellowlgx on 2015/7/24.
 */
public class CustomWheelDialog implements View.OnClickListener, OnWheelChangedListener {

    private Context mContext;
    private Button mBtnConfirm;

    private OnConfirmListener mListener;

    private Dialog mDialog;

    private View contentView;

    private String[][] datas = null;
    private List<WheelView> wheelViews = new ArrayList<WheelView>();
    private OnWheelChangeListener wheelChangedListener = null;
    private String mMinMonthDay;

    public interface OnConfirmListener {
        public boolean onConfirm(String[] data);
    }

    public interface OnWheelChangeListener {
        public void onChange(int changePos, String[] current);
    }

    public void setOnWheelChangedListener(OnWheelChangeListener listener) {
        this.wheelChangedListener = listener;
    }


    public CustomWheelDialog(Context context, OnConfirmListener listener, String[][] datas, String minMonthDay) {
        mDialog = new Dialog(context, R.style.my_dialog);
        this.datas = datas;
        mContext = context;
        mListener = listener;
        mMinMonthDay = minMonthDay;
        initView();
        initEvent();

        setUpData();

        setDialogParams();
    }


    public void show() {
        mDialog.show();
        if (contentView != null)
            contentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_downup));
    }

    public void dismiss() {
        if (contentView != null)
            contentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_updown));
        if (mDialog.isShowing())
            mDialog.dismiss();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.custom_wheel_layout, null);
        mBtnConfirm = (Button) contentView.findViewById(R.id.id_wheel_ok_btn);
        LinearLayout layout = (LinearLayout) contentView.findViewById(R.id.wheel_layout);
        int len = datas.length;
        for (int i = 0; i < len; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            WheelView view = new WheelView(mContext);
            layout.addView(view, params);
            view.setId(i);
            wheelViews.add(view);
        }
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        // 添加change事件
        for (WheelView view : wheelViews) {
            view.addChangingListener(this);
            view.addChangingListener(this);
        }
        mBtnConfirm.setOnClickListener(this);
    }

    private void setUpData() {
        int i = 0;
        for (WheelView view : wheelViews) {
            view.setViewAdapter(new ArrayWheelAdapter<String>(mContext, datas[i]));
            // 设置可见条目数量
            view.setVisibleItems(7);
            i++;
        }
    }

    public void refreshUI(int[] pos, String[][] datas) {
        int len = pos.length;
        for (int i = 0; i < len; i++) {
            this.datas[pos[i]] = datas[i];
            int current = wheelViews.get(pos[i]).getCurrentItem();
            wheelViews.get(pos[i]).setViewAdapter(new ArrayWheelAdapter<String>(mContext, datas[i]));
            if (current < datas[i].length)
                wheelViews.get(pos[i]).setCurrentItem(current);
            else
                wheelViews.get(pos[i]).setCurrentItem(datas[i].length - 1);
        }
    }

    public void setSelect(int[] pos) {
        for (int i = 0; i < pos.length; i++) {
            wheelViews.get(i).setCurrentItem(pos[i]);
        }
    }

    private void setDialogParams() {
        Window window = mDialog.getWindow(); //得到对话框
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL; //设置重力
        window.setAttributes(wl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_wheel_ok_btn:
                String[] data = new String[wheelViews.size()];
                int i = 0;
                for (WheelView view : wheelViews) {
                    data[i] = datas[i][view.getCurrentItem()];
                    i++;
                }
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date minDay = null;
                Date selDay = null;
                try {
                    minDay = df.parse(this.datas[0][0] + "-" + mMinMonthDay);
                    selDay = df.parse(data[0] + "-" + data[1] + "-" + data[2]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (minDay.compareTo(selDay) > 0) {
                    Toast.makeText(mContext, "选择日期必须大于当天！", Toast.LENGTH_SHORT).show();
                    return;
                }
                mListener.onConfirm(data);
                dismiss();
                break;

            default:
                break;
        }
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheelChangedListener != null) {
            String[] data = new String[wheelViews.size()];
            int i = 0;
            for (WheelView view : wheelViews) {
                data[i] = datas[i][view.getCurrentItem()];
                i++;
            }
            wheelChangedListener.onChange(wheel.getId(), data);
        }
    }
}
