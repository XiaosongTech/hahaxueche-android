package com.hahaxueche.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hahaxueche.R;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.view.HHBaseView;

/**
 * Created by wangshirui on 16/9/8.
 */
public class WelcomeActivity extends HHBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeNoTitle);
        setContentView(R.layout.activity_welcome);
        Handler x = new Handler();
        x.postDelayed(new splashHandler(), 2000);
    }

    class splashHandler implements Runnable {
        public void run() {
            navigateToStartActivity();
        }
    }

    private void navigateToStartActivity() {
        startActivity(new Intent(getContext(), StartLoginActivity.class));
        WelcomeActivity.this.finish();
    }
}
