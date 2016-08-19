package com.hahaxueche.ui.activity.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.MyApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.utils.Util;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

/**
 * webview
 * Created by gibxin on 2016/3/10.
 */
public class BaseWebViewActivity extends BaseActivity implements IWeiboHandler.Response {
    private ImageButton ibtn_base_webview_back;
    private WebView baseWebView;
    private ImageView mIvShare;
    private TextView mTvTitle;
    private ShareAppDialog shareAppDialog;

    private String url;
    private boolean isShowShare;

    /*****************
     * 分享
     ******************/
    private IWXAPI wxApi; //微信api
    private Tencent mTencent;//QQ
    private IWeiboShareAPI mWeiboShareAPI;//新浪微博
    private MyApplication myApplication;
    private String mTitle;
    private String mDescription;
    private String mImageUrl;

    /*****************
     * end
     ******************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_webview);
        ibtn_base_webview_back = (ImageButton) findViewById(R.id.ibtn_base_webview_back);
        baseWebView = (WebView) findViewById(R.id.base_webview);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        loadDatas();
        loadShare();
        ibtn_base_webview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseWebViewActivity.this.finish();
            }
        });
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

    @Override
    protected void onResume() {
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
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
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
        MobclickAgent.onPause(this);
    }

    private void loadDatas() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        isShowShare = intent.getBooleanExtra("isShowShare", false);
        mTitle = intent.getStringExtra("title");
    }

    private void loadShare() {
        if (isShowShare) {
            mTvTitle.setText(mTitle);
            mDescription = "限时活动，疯抢中";
            mImageUrl = "http://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
            regShareApi();
            mIvShare.setVisibility(View.VISIBLE);
            mIvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showShareAppDialog();
                }
            });

        } else {
            mIvShare.setVisibility(View.GONE);
        }
    }

    private void share(int shareType) {
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
            default:
                break;
        }
    }

    private void shareToQQ() {
        ShareListener myListener = new ShareListener();
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mTitle);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mDescription);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mImageUrl);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        mTencent.shareToQQ(this, params, myListener);
    }

    private void shareToWeibo() {
        // 1. 初始化微博的分享消息
        WeiboMessage weiboMessage = new WeiboMessage();
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = mTitle;
        mediaObject.description = mDescription;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = url;
        mediaObject.defaultText = mTitle + mDescription;
        weiboMessage.mediaObject = mediaObject;
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(BaseWebViewActivity.this, request);
    }

    private void shareToWeixin() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mDescription;
        Bitmap thumb = BitmapFactory.decodeResource(BaseWebViewActivity.this.getResources(), R.drawable.ic_launcher);
        msg.thumbData = Util.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        //SendMessageToWX.Req.WXSceneTimeline
        req.scene = SendMessageToWX.Req.WXSceneSession;
        wxApi.sendReq(req);
    }

    private void shareToFriendCircle() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mDescription;
        Bitmap thumb = BitmapFactory.decodeResource(BaseWebViewActivity.this.getResources(), R.drawable.ic_launcher);
        msg.thumbData = Util.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private class ShareListener implements IUiListener {

        @Override
        public void onCancel() {
            Log.v("gibxin", "onCancel");
        }

        @Override
        public void onComplete(Object arg0) {
            Log.v("gibxin", "onComplete");
        }

        @Override
        public void onError(UiError arg0) {
            Log.v("gibxin", "onError");
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResp) {
        if (baseResp != null) {
            Log.v("gibxin", "baseResp.errCode" + baseResp.errCode);
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    Toast.makeText(BaseWebViewActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    Toast.makeText(BaseWebViewActivity.this, "取消分享", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    Toast.makeText(BaseWebViewActivity.this, "分享失败，原因：" + baseResp.errMsg, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**
     * 获取分享API
     */
    private void regShareApi() {
        myApplication = (MyApplication) this.getApplication();
        wxApi = myApplication.getIWXAPI();
        mTencent = myApplication.getTencentAPI();
        mWeiboShareAPI = myApplication.getWeiboAPI();
    }

    private void showShareAppDialog() {
        if (shareAppDialog == null) {
            shareAppDialog = new ShareAppDialog(BaseWebViewActivity.this, new ShareAppDialog.OnShareListener() {
                @Override
                public void onShare(int shareType) {
                    share(shareType);
                }
            });
        }
        shareAppDialog.show();
    }
}
