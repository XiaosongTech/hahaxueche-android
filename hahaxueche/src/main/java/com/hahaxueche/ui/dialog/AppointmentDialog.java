package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.MyApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.TrailResponse;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.presenter.findCoach.FCPresenter;
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
    private EditText tvFirstTime;
    private EditText tvSecondTime;
    private String mCoachId;
    private TextView tvTrail;
    private TextView tvCancel;
    public FCPresenter fcPresenter;
    private ProgressDialog pd;//进度框

    public AppointmentDialog(Context context, String name, String phoneNumber, String coachId) {
        super(context);
        mContext = context;
        mCoachId = coachId;
        fcPresenter = ((MyApplication)mContext.getApplicationContext()).getFCPresenter();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_appointment, null);
        setContentView(view);
        tvFirstTime = (EditText) view.findViewById(R.id.tv_first_time);
        tvSecondTime = (EditText) view.findViewById(R.id.tv_second_time);
        tvFirstTime.setOnTouchListener(new TimeChooseListener());
        tvSecondTime.setOnTouchListener(new TimeChooseListener());
        etRealName = (EditText) view.findViewById(R.id.et_real_name);
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
        if(!TextUtils.isEmpty(name)){
            etRealName.setText(name);
        }
        etContactPhone = (EditText) view.findViewById(R.id.et_contact_phone);
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
        if(!TextUtils.isEmpty(phoneNumber)){
            etContactPhone.setText(phoneNumber);
        }
        tvTrail = (TextView) view.findViewById(R.id.tv_trail);
        tvTrail.setOnClickListener(mClickListener);
        tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(mClickListener);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        lp.x = 100; // 新位置X坐标
        lp.y = 150; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    private final class TimeChooseListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View view = View.inflate(mContext, R.layout.date_dialog, null);
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                builder.setView(view);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

                if (v.getId() == R.id.tv_first_time) {
                    final int inType = tvFirstTime.getInputType();
                    tvFirstTime.setInputType(InputType.TYPE_NULL);
                    tvFirstTime.onTouchEvent(event);
                    tvFirstTime.setInputType(inType);
                    tvFirstTime.setSelection(tvFirstTime.getText().length());
                    builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringBuffer sb = new StringBuffer();
                            sb.append(String.format("%d-%02d-%02d",
                                    datePicker.getYear(),
                                    datePicker.getMonth() + 1,
                                    datePicker.getDayOfMonth()));
                            tvFirstTime.setText(sb);
                            tvFirstTime.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
                            tvFirstTime.setBackgroundResource(R.drawable.edittext_corner_orange);

                            dialog.cancel();
                        }
                    });
                } else if (v.getId() == R.id.tv_second_time) {
                    int inType = tvSecondTime.getInputType();
                    tvSecondTime.setInputType(InputType.TYPE_NULL);
                    tvSecondTime.onTouchEvent(event);
                    tvSecondTime.setInputType(inType);
                    tvSecondTime.setSelection(tvSecondTime.getText().length());
                    builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringBuffer sb = new StringBuffer();
                            sb.append(String.format("%d-%02d-%02d",
                                    datePicker.getYear(),
                                    datePicker.getMonth() + 1,
                                    datePicker.getDayOfMonth()));
                            tvSecondTime.setText(sb);
                            tvSecondTime.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
                            tvSecondTime.setBackgroundResource(R.drawable.edittext_corner_orange);
                            dialog.cancel();
                        }
                    });
                }
                Dialog dialog = builder.create();
                dialog.show();
            }
            return true;
        }
    }
    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_trail:
                    String name = etRealName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    String firstTime = tvFirstTime.getText().toString();
                    String secondTime = tvSecondTime.getText().toString();
                    if (pd != null) {
                        pd.dismiss();
                    }
                    pd = ProgressDialog.show(mContext, null, "预约中，请稍后……");
                    fcPresenter.createTrail(mCoachId, name, contactPhone, firstTime, secondTime, new FCCallbackListener<TrailResponse>() {
                        @Override
                        public void onSuccess(TrailResponse data) {
                            if(pd!=null){
                                pd.dismiss();
                            }
                            Toast.makeText(mContext,"预约成功！",Toast.LENGTH_SHORT).show();
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
                case R.id.tv_cancel:
                    dismiss();
                    break;
            }
        }
    };
}
