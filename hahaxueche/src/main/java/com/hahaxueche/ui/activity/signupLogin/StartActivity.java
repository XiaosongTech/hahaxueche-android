package com.hahaxueche.ui.activity.signupLogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hahaxueche.R;

/**
 * Created by gibxin on 2016/1/19.
 */
public class StartActivity extends SLBaseActivity{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_start);
    }

    /**
     * 登录
     * @param view
     */
    public void startLogin(View view){
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 注册
     * @param view
     */
    public void startSignUp(View view){
        Intent intent = new Intent(context, SignUpActivity.class);
        startActivity(intent);
    }
}
