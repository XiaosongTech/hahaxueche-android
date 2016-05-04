package com.hahaxueche.ui.activity.signupLogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.Session;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.presenter.signupLogin.SLCallbackListener;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

/**
 * 登录Activity
 * Created by gibxin on 2016/1/21.
 */
public class LoginActivity extends SLBaseActivity {
    /**
     * 登录类型
     * 1,验证码登录;2,密码登录
     */
    private int loginType;
    /**
     * 登录状态
     * 0,初始化;1,已发送验证码;
     */
    private int loginState;
    private TextView tvLoginTitle;//标题
    private ImageButton ibtnLoginBack;//回退按钮
    private Button btnForgetPwd;//忘记密码按钮
    private EditText etLoginPhoneNumber;//手机号输入框
    private EditText etIdentifyCode;//验证码输入框
    private Button btnReSendIdentifyCode;//重发按钮
    private EditText etLoginPwd;//登录密码输入框
    private Button btnGetIdentifyCode;//获取验证码按钮
    private Button btnLogin;//登录按钮
    private TextView tvChangeLoginType;//切换登录方式
    private LinearLayout llyLoginIdentifyCode;//验证码布局
    private SharedPreferencesUtil spUtil;

    private ProgressDialog pd;//进度框
    private int sendTime = 60;
    private final MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginType = 1;//初始设置，使用验证码登录
        loginState = 0;
        spUtil = new SharedPreferencesUtil(this);
        initView();
        initEvent();
        loadView();
    }

    /**
     * 控件初始化
     */
    private void initView() {
        tvLoginTitle = Util.instence(this).$(this, R.id.tv_login_title);
        ibtnLoginBack = Util.instence(this).$(this, R.id.ibtn_login_back);
        btnForgetPwd = Util.instence(this).$(this, R.id.btn_forget_pwd);
        etLoginPhoneNumber = Util.instence(this).$(this, R.id.et_login_phone_number);
        etIdentifyCode = Util.instence(this).$(this, R.id.et_identify_code);
        btnReSendIdentifyCode = Util.instence(this).$(this, R.id.btn_re_send_identify_code);
        etLoginPwd = Util.instence(this).$(this, R.id.et_login_pwd);
        btnGetIdentifyCode = Util.instence(this).$(this, R.id.btn_get_identify_code);
        btnLogin = Util.instence(this).$(this, R.id.btn_login);
        tvChangeLoginType = Util.instence(this).$(this, R.id.tv_change_login_type);
        llyLoginIdentifyCode = Util.instence(this).$(this, R.id.lly_login_identify_code);
    }

    /**
     * 加载控件显示
     */
    private void loadView() {
        if (loginType == 1) {
            tvLoginTitle.setText(R.string.sLIdentifyCodeLogin);
            tvChangeLoginType.setText(R.string.sLUsePwdLogin);
            btnForgetPwd.setVisibility(View.INVISIBLE);
            switch (loginState) {
                //初始化
                case 0:
                    llyLoginIdentifyCode.setVisibility(View.GONE);
                    etLoginPwd.setVisibility(View.GONE);
                    btnGetIdentifyCode.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.GONE);
                    break;
                //已发送验证码
                case 1:
                    llyLoginIdentifyCode.setVisibility(View.VISIBLE);
                    etLoginPwd.setVisibility(View.GONE);
                    btnGetIdentifyCode.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                    btnReSendIdentifyCode.requestFocus();
                    break;
            }
        } else if (loginType == 2) {
            tvLoginTitle.setText(R.string.sLPwdLogin);
            tvChangeLoginType.setText(R.string.sLUseIdentifyCodeLogin);
            btnForgetPwd.setVisibility(View.VISIBLE);
            llyLoginIdentifyCode.setVisibility(View.GONE);
            etLoginPwd.setVisibility(View.VISIBLE);
            btnGetIdentifyCode.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 事件初始化
     */
    private void initEvent() {
        btnGetIdentifyCode.setOnClickListener(mClickListener);
        tvChangeLoginType.setOnClickListener(mClickListener);
        ibtnLoginBack.setOnClickListener(mClickListener);
        btnForgetPwd.setOnClickListener(mClickListener);
        btnLogin.setOnClickListener(mClickListener);
        btnReSendIdentifyCode.setOnClickListener(mClickListener);
    }

    /**
     * 单击事件
     */
    View.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                //回退
                case R.id.ibtn_login_back:
                    LoginActivity.this.finish();
                    break;
                //获取验证码
                case R.id.btn_get_identify_code:
                    getIdentifyCode();
                    break;
                //切换登录方式
                case R.id.tv_change_login_type:
                    loginType = loginType == 1 ? 2 : 1;
                    loadView();
                    break;
                //忘记密码
                case R.id.btn_forget_pwd:
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    intent.putExtra("isResetPwd", true);//重置密码
                    LoginActivity.this.startActivity(intent);
                    break;
                //登录
                case R.id.btn_login:
                    login();
                    break;
                case R.id.btn_re_send_identify_code:
                    loginState = 1;
                    loadView();
                    getIdentifyCode();
                    break;
            }
        }
    };

    /**
     * 获取验证码
     */
    private void getIdentifyCode() {
        String phoneNumber = etLoginPhoneNumber.getText().toString();
        if (pd != null) {
            pd.dismiss();
        }
        pd = ProgressDialog.show(LoginActivity.this, null, "验证码发送中，请稍后……");
        this.slPresenter.getIdentifyCode(phoneNumber, "login", new SLCallbackListener<BaseApiResponse>() {
            @Override
            public void onSuccess(BaseApiResponse data) {
                if (pd != null) {
                    pd.dismiss();
                }
                loginState = 1;
                loadView();
                startCountDown();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                if (pd != null) {
                    pd.dismiss();
                }
                if (errorEvent != null && errorEvent.equals("40044")) {
                    Toast.makeText(context, "当前用户不存在！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 登录
     */
    private void login() {
        String cell_phone = etLoginPhoneNumber.getText().toString();
        String pwd = etIdentifyCode.getText().toString();
        if (loginType == 2) {
            pwd = etLoginPwd.getText().toString();
        }
        if (pd != null) {
            pd.dismiss();
        }
        pd = ProgressDialog.show(LoginActivity.this, null, "登录中，请稍后……");
        this.slPresenter.login(cell_phone, pwd, loginType, new SLCallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (pd != null) {
                    pd.dismiss();
                }
                spUtil.setUser(user);
                MobclickAgent.onProfileSignIn(user.getStudent().getId());
                Intent intent;
                if (TextUtils.isEmpty(user.getStudent().getCity_id()) || TextUtils.isEmpty(user.getStudent().getAvatar()) || TextUtils.isEmpty(user.getStudent().getName())) {
                    //补全资料
                    intent = new Intent(LoginActivity.this, SignUpInfoActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    return;
                }
                if (TextUtils.isEmpty(user.getStudent().getCurrent_coach_id())) {
                    Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show();
                    intent = new Intent(context, IndexActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    fcPresenter.getCoach(user.getStudent().getCurrent_coach_id(), new FCCallbackListener<Coach>() {
                        @Override
                        public void onSuccess(Coach coach) {
                            spUtil.setCurrentCoach(coach);
                            Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, IndexActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }

                        @Override
                        public void onFailure(String errorEvent, String message) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                if (pd != null) {
                    pd.dismiss();
                }
                if (errorEvent != null && errorEvent.equals("40044")) {
                    Toast.makeText(context, "当前用户不存在！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startCountDown() {
        sendTime = 60;
        btnReSendIdentifyCode.setClickable(false);
        btnReSendIdentifyCode.setText(sendTime-- + "");
        mHandler.sendEmptyMessage(1);
    }

    static class MyHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;

        public MyHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final LoginActivity activity = mActivity.get();
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
                    activity.btnReSendIdentifyCode.setText(R.string.sLReSend);
                }
            }
        }
    }
}
