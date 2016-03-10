package com.hahaxueche.ui.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.hahaxueche.R;

/**
 * webview
 * Created by gibxin on 2016/3/10.
 */
public class BaseWebViewActivity extends Activity {
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
        baseWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });
        baseWebView.getSettings().setJavaScriptEnabled(true);
        baseWebView.setInitialScale(80);
        baseWebView.loadUrl(url);
    }
}
