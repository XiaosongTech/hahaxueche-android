package com.hahaxueche.ui.activity.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.presenter.base.MainPresenter;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.activity.findCoach.PartnerDetailActivity;
import com.hahaxueche.ui.activity.myPage.MyContractActivity;
import com.hahaxueche.ui.activity.myPage.ReferFriendsActivity;
import com.hahaxueche.ui.activity.myPage.UploadIdCardActivity;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.MainShareDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.dialog.myPage.ShareReferDialog;
import com.hahaxueche.ui.fragment.community.CommunityFragment;
import com.hahaxueche.ui.fragment.findCoach.FindCoachFragment;
import com.hahaxueche.ui.fragment.homepage.HomepageFragment;
import com.hahaxueche.ui.fragment.myPage.MypageFragment;
import com.hahaxueche.ui.view.base.MainView;
import com.hahaxueche.ui.widget.FragmentTabHost;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by wangshirui on 16/9/15.
 */
public class MainActivity extends HHBaseActivity implements MainView, IWeiboHandler.Response {
    private MainPresenter mPresenter;
    private FragmentTabHost mTabHost = null;
    private View indicator = null;
    private View mViewBadgeMyPage;
    private static final int REQUEST_CODE_UPLOAD_ID_CARD = 3;
    private static final int REQUEST_CODE_MY_CONTRACT = 4;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    /*****************
     * 分享
     ******************/
    private ShareReferDialog shareDialog;
    private IWXAPI wxApi; //微信api
    private Tencent mTencent;//QQ
    private IWeiboShareAPI mWeiboShareAPI;//新浪微博
    private String mTitle;
    private String mDescription;
    private String mImageUrl;
    private String mUrl;
    private ShareQQListener shareQQListener;
    private ShareQZoneListener shareQZoneListener;
    /*****************
     * end
     ******************/
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 603;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MainPresenter();
        mPresenter.attachView(this);
        setTheme(R.style.AppThemeNoTitle);
        setContentView(R.layout.activity_main);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("homepage").setIndicator(getLayoutInflater().inflate(R.layout.indicator_homepage, null)),
                HomepageFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("findCoach").setIndicator(getLayoutInflater().inflate(R.layout.indicator_find_coach, null)),
                FindCoachFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("community").setIndicator(getLayoutInflater().inflate(R.layout.indicator_community, null)),
                CommunityFragment.class, null);

        View myPageIndicator = getLayoutInflater().inflate(R.layout.indicator_my_page, null);
        mViewBadgeMyPage = myPageIndicator.findViewById(R.id.view_badge_my_page);
        mTabHost.addTab(mTabHost.newTabSpec("myPage").setIndicator(myPageIndicator), MypageFragment.class, null);

        mPresenter.viewHomepageCount();

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mPresenter.setMyPageBadge();
                switch (tabId) {
                    case "homepage":
                        mPresenter.viewHomepageCount();
                        break;
                    case "findCoach":
                        mPresenter.viewFindCoachCount();
                        break;
                    case "community":
                        mPresenter.viewCommunityCount();
                        break;
                    case "myPage":
                        mPresenter.viewMyPageCount();
                        break;
                    default:
                        break;
                }
            }
        });
        mTabHost.getTabWidget().setDividerDrawable(null);
        Intent intent = getIntent();
        Bundle shareObject = intent.getBundleExtra("shareObject");
        if (shareObject != null && !TextUtils.isEmpty(shareObject.getString("objectId", ""))) {
            if (shareObject.getString("type", "").equals("coach_detail")) {
                Intent startIntent = new Intent(getContext(), CoachDetailActivity.class);
                startIntent.putExtra("coach_id", shareObject.getString("objectId", ""));
                startActivity(startIntent);
            } else if (shareObject.getString("type", "").equals("training_partner_detail")) {
                Intent startIntent = new Intent(getContext(), PartnerDetailActivity.class);
                startIntent.putExtra("partnerId", shareObject.getString("objectId", ""));
                startActivity(startIntent);
            }
        }
        controlMyPageBadge();
        mPresenter.controlSignDialog();
        regShareApi();
    }

    /**
     * 获取分享API
     */
    private void regShareApi() {
        HHBaseApplication myApplication = HHBaseApplication.get(getContext());
        wxApi = myApplication.getIWXAPI();
        mTencent = myApplication.getTencentAPI();
        mWeiboShareAPI = myApplication.getWeiboAPI();
    }

    @Override
    public void initShareData() {
        mTitle = "新人大礼包";
        mDescription = "HI，知道你想学车，再送你200元代金券一张，怕你考不过，再送你一张保过卡，比心❤️️";
        mImageUrl = "https://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = WebViewUrl.WEB_URL_DALIBAO;
        HHLog.v("mUrl -> " + mUrl);
    }

    public void controlMyPageBadge() {
        mPresenter.setMyPageBadge();
    }

    public void selectTab(int tab) {
        if (mTabHost != null) {
            mTabHost.setCurrentTab(tab);
        }
    }

    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setMyPageBadge(boolean hasBadge) {
        if (hasBadge) {
            mViewBadgeMyPage.setVisibility(View.VISIBLE);
        } else {
            mViewBadgeMyPage.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSignDialog() {
        BaseConfirmSimpleDialog dialog = new BaseConfirmSimpleDialog(getContext(), "友情提醒", "快去上传资料签署专属学员协议吧！",
                "去上传", "取消", new BaseConfirmSimpleDialog.onClickListener() {
            @Override
            public void clickConfirm() {
                mPresenter.clickMyContract();
            }

            @Override
            public void clickCancel() {

            }
        });
        dialog.show();
    }

    @Override
    public void navigateToUploadIdCard() {
        startActivityForResult(new Intent(getContext(), UploadIdCardActivity.class), REQUEST_CODE_UPLOAD_ID_CARD);
    }

    @Override
    public void navigateToSignContract() {
        startActivityForResult(new Intent(getContext(), MyContractActivity.class), REQUEST_CODE_MY_CONTRACT);
    }

    @Override
    public void navigateToMyContract() {
        startActivityForResult(new Intent(getContext(), MyContractActivity.class), REQUEST_CODE_MY_CONTRACT);
    }

    @Override
    public void showVoucherDialog(final String studentId, Voucher voucher) {
        MainShareDialog dialog = new MainShareDialog(getContext(), voucher,
                new MainShareDialog.OnButtonClickListener() {
                    @Override
                    public void shareToFriends() {
                        if (TextUtils.isEmpty(mUrl)) return;
                        if (shareDialog == null) {
                            shareDialog = new ShareReferDialog(getContext(), mUrl, new ShareDialog.OnShareListener() {
                                @Override
                                public void onShare(int shareType) {
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
                                                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
                                            } else {
                                                shareToSms();
                                            }
                                        default:
                                            break;
                                    }
                                }
                            });
                        }
                        shareDialog.show();
                    }
                });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
        mTabHost = null;
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_UPLOAD_ID_CARD) {
            if (resultCode == RESULT_OK) {
                controlMyPageBadge();
                startActivity(new Intent(getContext(), ReferFriendsActivity.class));
            }
        } else if (requestCode == REQUEST_CODE_MY_CONTRACT) {
            if (resultCode == RESULT_OK) {//已签订协议
                controlMyPageBadge();
                startActivity(new Intent(getContext(), ReferFriendsActivity.class));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void shareToQQ() {
        shareQQListener = new ShareQQListener();
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mTitle);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mDescription);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mImageUrl);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mUrl);
        params.putString("type", "qq");
        mTencent.shareToQQ(this, params, shareQQListener);
    }

    private void shareToQZone() {
        shareQZoneListener = new ShareQZoneListener();
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_APP);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mTitle);
        params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mDescription);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mUrl);
        ArrayList<String> imgUrlList = new ArrayList<>();
        imgUrlList.add(mImageUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrlList);
        mTencent.shareToQzone(this, params, shareQZoneListener);
    }

    private void shareToWeibo() {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        ImageObject imageObject = new ImageObject();
        Bitmap thumbBmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share_qrcode), 400, 150, true);
        // 设置 Bitmap 类型的图片到视频对象里
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        imageObject.setImageObject(thumbBmp);
        TextObject textObject = new TextObject();
        textObject.text = mTitle;
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = "链接";
        mediaObject.description = mDescription;
        mediaObject.setThumbImage(thumbBmp);
        mediaObject.actionUrl = mUrl;

        weiboMessage.imageObject = imageObject;
        weiboMessage.textObject = textObject;
        weiboMessage.mediaObject = mediaObject;

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求*/
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(this, request);
    }

    private void shareToWeixin() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mDescription;
        Bitmap thumb = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher);
        msg.thumbData = Utils.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        //SendMessageToWX.Req.WXSceneTimeline
        req.scene = SendMessageToWX.Req.WXSceneSession;
        wxApi.handleIntent(getIntent(), new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {

            }

            @Override
            public void onResp(BaseResp baseResp) {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        showMessage("分享成功");
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        showMessage("取消分享");
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        showMessage("分享失败");
                        break;
                    default:
                        break;
                }
            }
        });
        wxApi.sendReq(req);
    }

    private void shareToFriendCircle() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mDescription;
        Bitmap thumb = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher);
        msg.thumbData = Utils.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        wxApi.handleIntent(getIntent(), new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {

            }

            @Override
            public void onResp(BaseResp baseResp) {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        showMessage("分享成功");
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        showMessage("取消分享");
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        showMessage("分享失败");
                        break;
                    default:
                        break;
                }
            }
        });
        wxApi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private class ShareQQListener implements IUiListener {

        @Override
        public void onCancel() {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("取消分享");
        }

        @Override
        public void onComplete(Object arg0) {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("分享成功");
        }

        @Override
        public void onError(UiError arg0) {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("分享失败");
            HHLog.e("分享失败，原因：" + arg0.errorMessage);
        }

    }

    private class ShareQZoneListener implements IUiListener {

        @Override
        public void onCancel() {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("取消分享");
        }

        @Override
        public void onComplete(Object arg0) {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("分享成功");
        }

        @Override
        public void onError(UiError arg0) {
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            showMessage("分享失败");
            HHLog.e("分享失败，原因：" + arg0.errorMessage);
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
            if (shareDialog != null) {
                shareDialog.dismiss();
            }
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    showMessage("分享成功");
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    showMessage("取消分享");
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    showMessage("分享失败");
                    HHLog.e("分享失败，原因：" + baseResp.errMsg);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_SEND_SMS) {
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
        intent.putExtra("sms_body", mTitle + mDescription + mUrl);
        startActivity(intent);
    }
}
