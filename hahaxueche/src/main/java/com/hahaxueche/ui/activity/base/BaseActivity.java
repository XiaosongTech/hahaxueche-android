package com.hahaxueche.ui.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.hahaxueche.ui.activity.collector.ActivityCollector;

/**
 * Created by Administrator on 2016/6/16.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        Log.v("gibxin", getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 打开webview
     * @param url
     */
    public void openWebView(String url) {
        Intent intent = new Intent(getApplication(), BaseWebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
