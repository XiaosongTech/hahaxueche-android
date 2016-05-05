package com.hahaxueche.ui.activity.mySetting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.MyApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.net.HttpEngine;
import com.hahaxueche.ui.dialog.ReferFriendsDialog;
import com.hahaxueche.utils.SharedPreferencesUtil;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import jp.wasabeef.blurry.Blurry;

/**
 * Created by gibxin on 2016/4/26.
 */
public class ReferFriendsActivity extends MSBaseActivity implements IWeiboHandler.Response {
    private TextView mTvReferFriends;
    private ImageButton mIbtnBack;
    private ReferFriendsDialog refererFriendsDialog;
    private LinearLayout mLlyReferFriends;
    private ImageView mIvDash;
    private IWXAPI wxApi; //微信api
    private Tencent mTencent;//QQ
    private IWeiboShareAPI mWeiboShareAPI;//新浪微博
    private MyApplication myApplication;
    private SharedPreferencesUtil spUtil;
    private String mTitle = "墙裂推荐：哈哈学车";
    private String mDescription = "注册立享50元优惠";
    private String mUrl = "";
    private String mImageUrl = "http://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
    private ProgressDialog pd;//进度框

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_friends);
        initView();
        initEvent();
        regShareApi();
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
    }

    private void initView() {
        mTvReferFriends = Util.instence(this).$(this, R.id.tv_refer_friends);
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_ps_back);
        mLlyReferFriends = Util.instence(this).$(this, R.id.lly_refer_friends);
        mIvDash = Util.instence(this).$(this, R.id.iv_dash);
        mIvDash.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        spUtil = new SharedPreferencesUtil(this);
    }

    private void initEvent() {
        mTvReferFriends.setOnClickListener(mClickListener);
        mIbtnBack.setOnClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_refer_friends:
                    if (null == refererFriendsDialog) {
                        refererFriendsDialog = new ReferFriendsDialog(ReferFriendsActivity.this, mOnDismissListener, mOnShareListener);
                    }
                    applyBlur();
                    refererFriendsDialog.show();
                    //推荐好友
                    break;
                case R.id.ibtn_ps_back:
                    ReferFriendsActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 模糊效果
     */
    private void applyBlur() {
        Blurry.with(ReferFriendsActivity.this).radius(25).sampling(2).onto(mLlyReferFriends);
    }

    private ReferFriendsDialog.OnDismissListener mOnDismissListener = new ReferFriendsDialog.OnDismissListener() {
        @Override
        public void onDismiss() {
            Blurry.delete(mLlyReferFriends);
        }
    };

    private ReferFriendsDialog.OnShareListener mOnShareListener = new ReferFriendsDialog.OnShareListener() {
        @Override
        public void onShare(int shareType) {
            Blurry.delete(mLlyReferFriends);
            generateShareUrl(shareType);
        }
    };

    /**
     * 获取分享API
     */
    private void regShareApi() {
        myApplication = (MyApplication) this.getApplication();
        wxApi = myApplication.getIWXAPI();
        mTencent = myApplication.getTencentAPI();
        mWeiboShareAPI = myApplication.getWeiboAPI();
    }

    private void shareToQQ() {
        ShareListener myListener = new ShareListener();
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mTitle);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "哈哈学车");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mDescription);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mImageUrl);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mUrl);
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
        mediaObject.actionUrl = mUrl;
        mediaObject.defaultText = mTitle + mDescription;
        weiboMessage.mediaObject = mediaObject;
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(ReferFriendsActivity.this, request);
    }

    private void shareToWeixin() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mDescription;
        Bitmap thumb = BitmapFactory.decodeResource(ReferFriendsActivity.this.getResources(), R.drawable.ic_launcher);
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
        webpage.webpageUrl = mUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mDescription;
        Bitmap thumb = BitmapFactory.decodeResource(ReferFriendsActivity.this.getResources(), R.drawable.ic_launcher);
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
                    Toast.makeText(ReferFriendsActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    Toast.makeText(ReferFriendsActivity.this, "取消分享", Toast.LENGTH_LONG).show();
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    Toast.makeText(ReferFriendsActivity.this, "分享失败，原因：" + baseResp.errMsg, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void generateShareUrl(final int shareType) {
        if (pd != null) {
            pd.dismiss();
        }
        pd = ProgressDialog.show(ReferFriendsActivity.this, null, "数据加载中，请稍后……");
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier(spUtil.getUser().getId())
                .setTitle("Share Refer Link")
                .setContentDescription("Share Refer Link")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("refererId", spUtil.getUser().getId());
        LinkProperties linkProperties = new LinkProperties().setChannel("android").setFeature("sharing");
        branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                pd.dismiss();
                if (error == null) {
                    try {
                        mUrl = HttpEngine.BASE_SERVER_IP + "/share/invitations?target=" + URLEncoder.encode(url, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e("gibxin", "create branchUrl error :" + e.getMessage());
                        e.printStackTrace();
                    }
                    Log.v("gibxin", "got my Branch link to share: " + url);
                    Log.v("gibxin", "mUrl: " + mUrl);
                    share(shareType);
                } else {
                    Log.e("gibxin", "create branchUrl error :" + error.getMessage());
                    Toast.makeText(ReferFriendsActivity.this, "分享链接生成失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void share(int shareType) {
        switch (shareType) {
            case 0:
                //短信分享
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
                intent.putExtra("sms_body", mTitle + mDescription + mUrl);
                startActivity(intent);
                break;
            case 1:
                //复制链接
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(mUrl);
                Toast.makeText(ReferFriendsActivity.this, "链接已复制到粘贴板！", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                //分享QQ
                shareToQQ();
                break;
            case 3:
                //分享微博
                shareToWeibo();
                break;
            case 4:
                //分享微信
                shareToWeixin();
                break;
            case 5:
                //分享朋友圈
                shareToFriendCircle();
                break;
            default:
                break;
        }
    }
}
