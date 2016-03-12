package com.hahaxueche.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.hahaxueche.R;
import com.hahaxueche.ui.activity.index.IndexActivity;
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
                SharedPreferences spSession = getSharedPreferences("session", Activity.MODE_PRIVATE);
                String session_id = spSession.getString("session_id", "");
                Intent intent;
                if(TextUtils.isEmpty(session_id)){
                    intent=new Intent(WelcomeActivity.this,StartActivity.class);
                }else{
                    intent=new Intent(WelcomeActivity.this,IndexActivity.class);
                }
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 2000);
    }
}
