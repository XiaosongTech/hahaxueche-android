package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.MyApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.response.TrailResponse;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.presenter.findCoach.FCPresenter;
import com.hahaxueche.ui.widget.wheel.CustomWheelDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;

/**
 * 预约dialog
 * Created by gibxin on 2016/2/26.
 */
public class AppointmentDialog extends Dialog {
    private Context mContext;
    private EditText etRealName;
    private EditText etContactPhone;
    private EditText etFirstTime;
    private EditText etSecondTime;
    private TextView mTvAppointmentTime;
    private String mCoachId;
    private TextView tvTrail;
    private ImageView ivClose;
    public FCPresenter fcPresenter;
    private ProgressDialog pd;//进度框
    private CustomWheelDialog firstTimeDialog;
    private CustomWheelDialog secondTimeDialog;
    private boolean mIsFreeTry;//是否免费试学

    public AppointmentDialog(Context context, String name, String phoneNumber, String coachId, boolean isFreeTry) {
        super(context);
        mContext = context;
        mCoachId = coachId;
        mIsFreeTry = isFreeTry;
        fcPresenter = ((MyApplication) mContext.getApplicationContext()).getFCPresenter();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_appointment, null);
        setContentView(view);
        etFirstTime = (EditText) view.findViewById(R.id.tv_first_time);
        etSecondTime = (EditText) view.findViewById(R.id.tv_second_time);
        mTvAppointmentTime = (TextView) view.findViewById(R.id.tv_appointment_time);
        etRealName = (EditText) view.findViewById(R.id.et_real_name);
        etContactPhone = (EditText) view.findViewById(R.id.et_contact_phone);
        tvTrail = (TextView) view.findViewById(R.id.tv_trail);
        ivClose = (ImageView) view.findViewById(R.id.iv_close);
        if (mIsFreeTry) {
            mTvAppointmentTime.setVisibility(View.GONE);
            etFirstTime.setVisibility(View.GONE);
            etSecondTime.setVisibility(View.GONE);
        }
        initEvents();
        if (!TextUtils.isEmpty(name)) {
            etRealName.setText(name);
        }

        if (!TextUtils.isEmpty(phoneNumber)) {
            etContactPhone.setText(phoneNumber);
        }

        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.x = 100; // 新位置X坐标
        lp.y = 150; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    private void initEvents() {
        if (!mIsFreeTry) {
            etFirstTime.setOnTouchListener(new TimeChooseListener());
            etSecondTime.setOnTouchListener(new TimeChooseListener());
        }
        etRealName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etRealName.setBackgroundResource(R.drawable.edittext_corner_orange);
            }
        });
        etContactPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etContactPhone.setBackgroundResource(R.drawable.edittext_corner_orange);
            }
        });
        tvTrail.setOnClickListener(mClickListener);
        ivClose.setOnClickListener(mClickListener);
    }

    private final class TimeChooseListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int[] position = {0, month, day};
                String[][] datas = getDate();
                if (v.getId() == R.id.tv_first_time) {
                    if (null == firstTimeDialog) {
                        firstTimeDialog = new CustomWheelDialog(mContext, firstConfirm, datas, (month + 1) + "-" + (day + 1));
                        firstTimeDialog.setSelect(position);
                    }
                    firstTimeDialog.show();
                } else if (v.getId() == R.id.tv_second_time) {
                    if (null == secondTimeDialog) {
                        secondTimeDialog = new CustomWheelDialog(mContext, secondConfirm, datas, (month + 1) + "-" + (day + 1));
                        secondTimeDialog.setSelect(position);
                    }
                    secondTimeDialog.show();
                }
            }
            return true;
        }
    }

    private String[][] getDate() {
        String[][] datas = new String[3][];
        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        String[] year = new String[10];
        for (int i = y, j = 0; i < y + 10; i++, j++) {
            year[j] = String.valueOf(i);
        }
        String[] month = new String[12];
        for (int i = 0; i < 12; i++) {
            month[i] = String.format("%02d", i + 1);
        }
        String[] day = new String[31];
        for (int i = 0; i < 31; i++) {
            day[i] = String.format("%02d", i + 1);
        }
        datas[0] = year;
        datas[1] = month;
        datas[2] = day;
        return datas;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_trail:
                    String name = etRealName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    String firstTime = etFirstTime.getText().toString();
                    String secondTime = etSecondTime.getText().toString();
                    if (pd != null) {
                        pd.dismiss();
                    }
                    pd = ProgressDialog.show(mContext, null, "预约中，请稍后……");
                    fcPresenter.createTrail(mCoachId, name, contactPhone, firstTime, secondTime, new FCCallbackListener<TrailResponse>() {
                        @Override
                        public void onSuccess(TrailResponse data) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                            Toast.makeText(mContext, "预约成功！", Toast.LENGTH_SHORT).show();
                            MobclickAgent.onEvent(mContext, "did_try_coach");
                            dismiss();
                        }

                        @Override
                        public void onFailure(String errorEvent, String message) {
                            if (pd != null) {
                                pd.dismiss();
                            }
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.iv_close:
                    dismiss();
                    break;
            }
        }
    };

    CustomWheelDialog.OnConfirmListener firstConfirm = new CustomWheelDialog.OnConfirmListener() {
        @Override
        public boolean onConfirm(String[] data) {
            etFirstTime.setText(data[0] + "-" + data[1] + "-" + data[2]);
            etFirstTime.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
            etFirstTime.setBackgroundResource(R.drawable.edittext_corner_orange);
            return true;
        }
    };

    CustomWheelDialog.OnConfirmListener secondConfirm = new CustomWheelDialog.OnConfirmListener() {
        @Override
        public boolean onConfirm(String[] data) {
            etSecondTime.setText(data[0] + "-" + data[1] + "-" + data[2]);
            etSecondTime.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
            etSecondTime.setBackgroundResource(R.drawable.edittext_corner_orange);
            return true;
        }
    };
}
