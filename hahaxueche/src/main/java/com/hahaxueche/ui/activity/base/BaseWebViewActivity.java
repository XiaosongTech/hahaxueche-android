package com.hahaxueche.ui.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.hahaxueche.R;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.InvocationTargetException;

/**
 * webview
 * Created by gibxin on 2016/3/10.
 */
public class BaseWebViewActivity extends BaseActivity {
    private ImageButton ibtn_base_webview_back;
    private WebView baseWebView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_webview);
        ibtn_base_webview_back = (ImageButton) findViewById(R.id.ibtn_base_webview_back);
        baseWebView = (WebView) findViewById(R.id.base_webview);
        ibtn_base_webview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseWebViewActivity.this.finish();
            }
        });
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        baseWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.clearCache(true);
            }
        });
        WebSettings mWebSettings = baseWebView.getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDefaultTextEncodingName("GBK");
        mWebSettings.setSupportMultipleWindows(true);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(false);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        baseWebView.loadUrl(url);
    }

    @Override
    protected void onResume() {
        try {
            baseWebView.getClass().getMethod("onResume").invoke(baseWebView,(Object[])null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        try {
            baseWebView.getClass().getMethod("onPause").invoke(baseWebView,(Object[])null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
