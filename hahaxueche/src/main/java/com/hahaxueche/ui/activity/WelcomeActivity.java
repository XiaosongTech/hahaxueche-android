package com.hahaxueche.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.hahaxueche.R;
import com.hahaxueche.ui.activity.signupLogin.StartActivity;

/**
 * Created by gibxin on 2016/3/6.
 */
public class WelcomeActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 声明使用自定义标题
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent=new Intent(WelcomeActivity.this,StartActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 2000);
    }
}
