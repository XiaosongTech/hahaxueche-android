package com.hahaxueche.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hahaxueche.util.HHLog;


/**
 * Created by wangshirui on 16/9/8.
 */
public class HHBaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HHLog.v(getClass().getName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
