package com.hahaxueche.ui.activity.signupLogin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.presenter.signupLogin.SLCallbackListener;

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
    private ProgressDialog pd;//进度框

    public SignUpActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
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
        this.slPresenter.getIdentifyCode(phoneNumber, new SLCallbackListener<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (pd != null) {
                    pd.dismiss();
                }
                Toast.makeText(context, "验证码发送成功！！！", Toast.LENGTH_SHORT).show();
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
