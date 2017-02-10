package com.hahaxueche.ui.activity.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.PushObject;
import com.hahaxueche.presenter.base.BaseWebViewPresenter;
import com.hahaxueche.ui.dialog.BaseAlertSimpleDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.view.base.BaseWebViewView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;

import java.lang.reflect.InvocationTargetException;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

/**
 * webview
 * Created by gibxin on 2016/3/10.
 */
public class BaseWebViewActivity extends HHBaseActivity implements BaseWebViewView {
    private BaseWebViewPresenter mPresenter;
    @BindView(R.id.base_webview)
    WebView baseWebView;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    ImageView mIvBack;
    TextView mTvTitle;
    ImageView mIvShare;
    private String url;
    private String mShareUrl;

    /*****************
     * 分享
     ******************/
    private ShareDialog shareDialog;
    private HHBaseApplication myApplication;
    private String mTitle;
    private String mDescription;
    private String mImageUrl;

    /*****************
     * end
     ******************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new BaseWebViewPresenter();
        setContentView(R.layout.activity_base_webview);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        loadDatas();
        baseWebView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.equals("hhxc://findcoach")) {
                    returnToFindCoach();
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loadTitle(view.getTitle());
                super.onPageFinished(view, url);
                view.clearCache(true);
            }

        });
        baseWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                BaseAlertSimpleDialog dialog = new BaseAlertSimpleDialog(getContext(), "哈哈学车", message);
                dialog.show();
                return true;
                //return super.onJsAlert(view, url, message, result);
            }
        });
        WebSettings mWebSettings = baseWebView.getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDefaultTextEncodingName("GBK");
        mWebSettings.setSupportMultipleWindows(false);
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

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base_share);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseWebViewActivity.this.finish();
            }
        });
        mIvShare = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_share);
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(getContext(), new ShareDialog.OnShareListener() {
                        @Override
                        public void onShare(int shareType) {
                            mPresenter.convertUrlForShare(mShareUrl, shareType);
                        }
                    });
                }
                shareDialog.show();
            }
        });
    }

    @Override
    public void onResume() {
        try {
            baseWebView.getClass().getMethod("onResume").invoke(baseWebView, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        try {
            baseWebView.getClass().getMethod("onPause").invoke(baseWebView, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    private void loadDatas() {
        Intent intent = getIntent();
        if (!TextUtils.isEmpty(intent.getStringExtra("url"))) {
            url = intent.getStringExtra("url");//加载的URL
            mShareUrl = TextUtils.isEmpty(intent.getStringExtra("shareUrl")) ?
                    url : intent.getStringExtra("shareUrl");//分享的URL，可能涉及到一些参数不用分享出去的情况
        } else if (!TextUtils.isEmpty(intent.getStringExtra("extraMap"))) {
            try {
                PushObject pushObject = new Gson().fromJson(intent.getStringExtra("extraMap"), PushObject.class);
                if (!TextUtils.isEmpty(pushObject.url)) {
                    url = pushObject.url;
                    mShareUrl = url;
                }
            } catch (Exception e) {
                HHLog.e(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void returnToFindCoach() {
        Intent intent = new Intent();
        intent.putExtra("showTab", 1);
        setResult(RESULT_OK, intent);
        BaseWebViewActivity.this.finish();
    }

    private void shareToQQ() {
        ShareUtil.shareMedia(this, SharePlatform.QQ, mTitle, mDescription, mShareUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToQZone() {
        ShareUtil.shareMedia(this, SharePlatform.QZONE, mTitle, mDescription, mShareUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToWeibo() {
        ShareUtil.shareMedia(this, SharePlatform.WEIBO, mTitle, mDescription, mShareUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToWeixin() {
        ShareUtil.shareMedia(this, SharePlatform.WX, mTitle, mDescription, mShareUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    private void shareToFriendCircle() {
        ShareUtil.shareMedia(this, SharePlatform.WX_TIMELINE, mTitle, mDescription, mShareUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                showMessage("分享成功");
            }

            @Override
            public void shareFailure(Exception e) {
                showMessage("分享失败");
                e.printStackTrace();
            }

            @Override
            public void shareCancel() {
                showMessage("取消分享");
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setShareUrl(String shareUrl) {
        mShareUrl = shareUrl;
        mImageUrl = "https://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void startToShare(int shareType) {
        switch (shareType) {
            case 0:
                shareToWeixin();
                break;
            case 1:
                shareToFriendCircle();
                break;
            case 2:
                shareToQQ();
                break;
            case 3:
                shareToWeibo();
                break;
            case 4:
                shareToQZone();
                break;
            case 5:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, RequestCode.PERMISSIONS_REQUEST_SEND_SMS);
                } else {
                    shareToSms();
                }
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToSms();
            } else {
                showMessage("请允许发送短信权限，不然无法分享到短信");
            }
        }
    }

    private void shareToSms() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", "［哈哈学车］" + mDescription + mShareUrl);
        startActivity(intent);
    }

    private void loadTitle(String title) {
        mTitle = title;
        mDescription = mTitle;
        mTvTitle.setText(mTitle);
    }
}
