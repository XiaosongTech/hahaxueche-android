package com.hahaxueche.ui.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;

import java.lang.reflect.InvocationTargetException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * webview
 * Created by gibxin on 2016/3/10.
 */
public class BaseWebViewActivity extends HHBaseActivity {
    @BindView(R.id.base_webview)
    WebView baseWebView;
    ImageView mIvBack;
    TextView mTvTitle;
    private String url;
    private boolean isShowShare;

    /*****************
     * 分享
     ******************/
    private IWXAPI wxApi; //微信api
    private Tencent mTencent;//QQ
    private IWeiboShareAPI mWeiboShareAPI;//新浪微博
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
        ButterKnife.bind(this);
        initActionBar();
        loadDatas();
        //loadShare();
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

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseWebViewActivity.this.finish();
            }
        });
        mTvTitle.setText("哈哈学车");
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
    }

    private void loadDatas() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        isShowShare = intent.getBooleanExtra("isShowShare", false);
        mTitle = intent.getStringExtra("title");
    }

//    private void loadShare() {
//        if (isShowShare) {
//            mTvTitle.setText(mTitle);
//            mDescription = "限时活动，疯抢中";
//            mImageUrl = "http://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
//            regShareApi();
//            mIvShare.setVisibility(View.VISIBLE);
//            mIvShare.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showShareAppDialog();
//                }
//            });
//
//        } else {
//            mIvShare.setVisibility(View.GONE);
//        }
//    }
//
//    private void share(int shareType) {
//        switch (shareType) {
//            case 0:
//                shareToWeixin();
//                break;
//            case 1:
//                shareToFriendCircle();
//                break;
//            case 2:
//                shareToQQ();
//                break;
//            case 3:
//                shareToWeibo();
//                break;
//            case 4:
//                shareToQZone();
//            default:
//                break;
//        }
//    }
//
//    private void shareToQQ() {
//        ShareListener myListener = new ShareListener();
//        final Bundle params = new Bundle();
//        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//        params.putString(QQShare.SHARE_TO_QQ_TITLE, mTitle);
//        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mDescription);
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mImageUrl);
//        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
//        mTencent.shareToQQ(this, params, myListener);
//    }
//
//    private void shareToQZone() {
//        ShareListener myListener = new ShareListener();
//        final Bundle params = new Bundle();
//        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_APP);
//        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mTitle);
//        params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
//        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mDescription);
//        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);
//        ArrayList<String> imgUrlList = new ArrayList<>();
//        imgUrlList.add(mImageUrl);
//        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrlList);
//        mTencent.shareToQzone(this, params, myListener);
//    }
//
//    private void shareToWeibo() {
//        // 1. 初始化微博的分享消息
//        WeiboMessage weiboMessage = new WeiboMessage();
//        WebpageObject mediaObject = new WebpageObject();
//        mediaObject.identify = Utility.generateGUID();
//        mediaObject.title = mTitle;
//        mediaObject.description = mDescription;
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
//        mediaObject.setThumbImage(bitmap);
//        mediaObject.actionUrl = url;
//        mediaObject.defaultText = mTitle + mDescription;
//        weiboMessage.mediaObject = mediaObject;
//        // 2. 初始化从第三方到微博的消息请求
//        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
//        // 用transaction唯一标识一个请求
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.message = weiboMessage;
//        // 3. 发送请求消息到微博，唤起微博分享界面
//        mWeiboShareAPI.sendRequest(BaseWebViewActivity.this, request);
//    }
//
//    private void shareToWeixin() {
//        WXWebpageObject webpage = new WXWebpageObject();
//        webpage.webpageUrl = url;
//        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.title = mTitle;
//        msg.description = mDescription;
//        Bitmap thumb = BitmapFactory.decodeResource(BaseWebViewActivity.this.getResources(), R.drawable.ic_launcher);
//        msg.thumbData = Util.bmpToByteArray(thumb, true);
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("webpage");
//        req.message = msg;
//        //SendMessageToWX.Req.WXSceneTimeline
//        req.scene = SendMessageToWX.Req.WXSceneSession;
//        wxApi.sendReq(req);
//    }
//
//    private void shareToFriendCircle() {
//        WXWebpageObject webpage = new WXWebpageObject();
//        webpage.webpageUrl = url;
//        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.title = mTitle;
//        msg.description = mDescription;
//        Bitmap thumb = BitmapFactory.decodeResource(BaseWebViewActivity.this.getResources(), R.drawable.ic_launcher);
//        msg.thumbData = Util.bmpToByteArray(thumb, true);
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("webpage");
//        req.message = msg;
//        req.scene = SendMessageToWX.Req.WXSceneTimeline;
//        wxApi.sendReq(req);
//    }
//
//    private String buildTransaction(final String type) {
//        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
//    }
//
//    private class ShareListener implements IUiListener {
//
//        @Override
//        public void onCancel() {
//            Log.v("gibxin", "onCancel");
//        }
//
//        @Override
//        public void onComplete(Object arg0) {
//            Log.v("gibxin", "onComplete");
//        }
//
//        @Override
//        public void onError(UiError arg0) {
//            Log.v("gibxin", "onError");
//        }
//
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
//        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
//        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
//        mWeiboShareAPI.handleWeiboResponse(intent, this);
//    }
//
//    @Override
//    public void onResponse(BaseResponse baseResp) {
//        if (baseResp != null) {
//            Log.v("gibxin", "baseResp.errCode" + baseResp.errCode);
//            switch (baseResp.errCode) {
//                case WBConstants.ErrorCode.ERR_OK:
//                    Toast.makeText(BaseWebViewActivity.this, "分享成功", Toast.LENGTH_LONG).show();
//                    break;
//                case WBConstants.ErrorCode.ERR_CANCEL:
//                    Toast.makeText(BaseWebViewActivity.this, "取消分享", Toast.LENGTH_LONG).show();
//                    break;
//                case WBConstants.ErrorCode.ERR_FAIL:
//                    Toast.makeText(BaseWebViewActivity.this, "分享失败，原因：" + baseResp.errMsg, Toast.LENGTH_LONG).show();
//                    break;
//            }
//        }
//    }
//
//    /**
//     * 获取分享API
//     */
//    private void regShareApi() {
//        myApplication = (MyApplication) this.getApplication();
//        wxApi = myApplication.getIWXAPI();
//        mTencent = myApplication.getTencentAPI();
//        mWeiboShareAPI = myApplication.getWeiboAPI();
//    }
//
//    private void showShareAppDialog() {
//        if (shareAppDialog == null) {
//            shareAppDialog = new ShareAppDialog(BaseWebViewActivity.this, new ShareAppDialog.OnShareListener() {
//                @Override
//                public void onShare(int shareType) {
//                    share(shareType);
//                }
//            });
//        }
//        shareAppDialog.show();
//    }
}
