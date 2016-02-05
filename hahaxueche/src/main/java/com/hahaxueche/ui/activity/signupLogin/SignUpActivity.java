package com.hahaxueche.ui.activity.signupLogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.signupLogin.CreateUserResponse;
import com.hahaxueche.model.signupLogin.SessionModel;
import com.hahaxueche.model.signupLogin.StudentModel;
import com.hahaxueche.model.util.BaseApiResponse;
import com.hahaxueche.presenter.signupLogin.SLCallbackListener;
import com.hahaxueche.ui.fragment.index.IndexActivity;

import java.lang.ref.WeakReference;

/**
 * 注册Activity
 * Created by gibxin on 2016/1/21.
 */
public class SignUpActivity extends SLBaseActivity {
    private TextView tvRegisterTitle;//标题
    private ImageButton ibtnRegisterBack;//回退按钮
    private EditText etLoginPhoneNumber;//手机号输入框
    private LinearLayout llyLoginIdentifyCode;//验证码布局
    private EditText etIdentifyCode;//验证码输入框
    private Button btnReSendIdentifyCode;//重发按钮
    private EditText etRegisterSetPwd;//设置密码输入框
    private Button btnGetIdentifyCode;//获取验证码按钮
    private Button btnFinish;//完成按钮
    private boolean isResetPwd;//是否重置密码
    private ProgressDialog pd;//进度框
    private int sendTime = 60;
    private final MyHandler mHandler = new MyHandler(this);

    public SignUpActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Intent intent = getIntent();
        isResetPwd = intent.getBooleanExtra("isResetPwd", false);
        initView();
        loadInitState();
    }

    /**
     * 控件初始化
     */
    private void initView() {
        tvRegisterTitle = (TextView) findViewById(R.id.tv_register_title);
        ibtnRegisterBack = (ImageButton) findViewById(R.id.ibtn_register_back);
        etLoginPhoneNumber = (EditText) findViewById(R.id.et_register_phone_number);
        llyLoginIdentifyCode = (LinearLayout) findViewById(R.id.lly_register_identify_code);
        etIdentifyCode = (EditText) findViewById(R.id.et_identify_code);
        btnReSendIdentifyCode = (Button) findViewById(R.id.btn_re_send_identify_code);
        etRegisterSetPwd = (EditText) findViewById(R.id.et_register_set_pwd);
        btnGetIdentifyCode = (Button) findViewById(R.id.btn_get_identify_code);
        btnFinish = (Button) findViewById(R.id.btn_finish);
        if (isResetPwd) {
            tvRegisterTitle.setText(getResources().getText(R.string.sLResetPwd));//密码重置
        }
    }

    /**
     * 加载初始化状态
     */
    private void loadInitState() {
        llyLoginIdentifyCode.setVisibility(View.GONE);
        btnGetIdentifyCode.setVisibility(View.VISIBLE);
        etRegisterSetPwd.setVisibility(View.GONE);
        btnFinish.setVisibility(View.GONE);
    }

    /**
     * 加载已发送验证码状态
     */
    private void loadSendCodeState() {
        llyLoginIdentifyCode.setVisibility(View.VISIBLE);
        btnGetIdentifyCode.setVisibility(View.GONE);
        etRegisterSetPwd.setVisibility(View.VISIBLE);
        btnFinish.setVisibility(View.VISIBLE);
        if(isResetPwd){
            btnFinish.setText(getResources().getText(R.string.sLSure));
        }
        sendTime = 60;
        btnReSendIdentifyCode.setClickable(false);
        btnReSendIdentifyCode.setText(sendTime-- + "");
        mHandler.sendEmptyMessage(1);
    }

    /**
     * 获取验证码
     *
     * @param view
     */
    public void getIdentifyCode(View view) {
        String phoneNumber = etLoginPhoneNumber.getText().toString();
        if (pd != null) {
            pd.dismiss();
        }
        pd = ProgressDialog.show(SignUpActivity.this, null, "验证码发送中，请稍后……");
        this.slPresenter.getIdentifyCode(phoneNumber, isResetPwd ? "reset" : "register", new SLCallbackListener<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (pd != null) {
                    pd.dismiss();
                }
                loadSendCodeState();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                if (pd != null) {
                    pd.dismiss();
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void back(View view) {
        this.finish();
    }

    /**
     * 注册完成
     *
     * @param view
     */
    public void finish(View view) {
        String phoneNumber = etLoginPhoneNumber.getText().toString();
        String identifyCode = etIdentifyCode.getText().toString();
        String pwd = etRegisterSetPwd.getText().toString();
        if (pd != null) {
            pd.dismiss();
        }
        pd = ProgressDialog.show(SignUpActivity.this, null, "数据提交中，请稍后……");
        if(isResetPwd){
            this.slPresenter.resetPassword(phoneNumber, pwd, identifyCode, new SLCallbackListener<BaseApiResponse>() {
                @Override
                public void onSuccess(BaseApiResponse baseApiResponse) {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    Toast.makeText(context,getResources().getText(R.string.sLResetPwdSuccess),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, IndexActivity.class);
                    startActivity(intent);
                    SignUpActivity.this.finish();
                }

                @Override
                public void onFailure(String errorEvent, String message) {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            this.slPresenter.createUser(phoneNumber, identifyCode, pwd, "student", new SLCallbackListener<CreateUserResponse>() {
                @Override
                public void onSuccess(CreateUserResponse createUserResponse) {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    SessionModel userSession = createUserResponse.getSession();
                    StudentModel userStudent = createUserResponse.getStudent();
                    SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("access_token", userSession.getAccess_token());
                    editor.putString("student_id", userStudent.getId());
                    editor.commit();
                    Intent intent = new Intent(context, SignUpInfoActivity.class);
                    startActivity(intent);
                    SignUpActivity.this.finish();
                }

                @Override
                public void onFailure(String errorEvent, String message) {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    static class MyHandler extends Handler {
        private final WeakReference<SignUpActivity> mActivity;

        public MyHandler(SignUpActivity activity) {
            mActivity = new WeakReference<SignUpActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final SignUpActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 1) {
                    activity.btnReSendIdentifyCode.setClickable(false);
                    activity.btnReSendIdentifyCode.setText(activity.sendTime-- + "");
                    if (activity.sendTime > 0) {
                        activity.mHandler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        activity.mHandler.sendEmptyMessage(2);
                    }
                } else if (msg.what == 2) {
                    activity.btnReSendIdentifyCode.setClickable(true);
                    activity.btnReSendIdentifyCode.setText(R.string.sLSendIdentifyCode);
                }
            }
        }
    }
}
