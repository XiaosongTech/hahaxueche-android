package com.hahaxueche.ui.activity.findCoach;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.Partner;
import com.hahaxueche.presenter.findCoach.PartnerDetailPresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.dialog.BaseAlertSimpleDialog;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.view.findCoach.PartnerDetailView;
import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
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
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/10/20.
 */

public class PartnerDetailActivity extends HHBaseActivity implements PartnerDetailView, IWeiboHandler.Response {
    private PartnerDetailPresenter mPresenter;
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    @BindView(R.id.tv_partner_name)
    TextView mTvName;
    @BindView(R.id.tv_description)
    TextView mTvDescription;
    @BindView(R.id.iv_partner_avatar)
    SimpleDraweeView mIvAvatar;
    @BindView(R.id.is_partner_images)
    ImageSwitcher mIsImages;
    @BindView(R.id.rly_info_line)
    RelativeLayout mRlyInfoLine;
    @BindView(R.id.tv_applaud_count)
    TextView mTvApplaud;
    @BindView(R.id.lly_prices)
    LinearLayout mLlyPrices;
    /*****************
     * 分享
     ******************/
    private ShareDialog shareDialog;
    private IWXAPI wxApi; //微信api
    private Tencent mTencent;//QQ
    private IWeiboShareAPI mWeiboShareAPI;//新浪微博
    private HHBaseApplication myApplication;
    private String mTitle;
    private String mDescription;
    private String mImageUrl;
    private String mUrl;
    private ShareQQListener shareQQListener;
    private ShareQZoneListener shareQZoneListener;
    /*****************
     * end
     ******************/

    private static final int PERMISSIONS_REQUEST_CELL_PHONE = 601;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 603;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PartnerDetailPresenter();
        setContentView(R.layout.activity_partner_detail);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        Intent intent = getIntent();
        if (intent.getParcelableExtra("partner") != null) {
            mPresenter.setPartner((Partner) intent.getParcelableExtra("partner"));
        } else if (intent.getStringExtra("partnerId") != null) {
            mPresenter.setPartner(intent.getStringExtra("partnerId"));
        }
        regShareApi();
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base_share);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("教练详情");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PartnerDetailActivity.this.finish();
            }
        });
        ImageView mIvShare = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_share);
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clickShareCount();
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(getContext(), new ShareDialog.OnShareListener() {
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
    }

    @Override
    public void initShareData(Partner partner) {
        mTitle = "哈哈学车-选驾校，挑教练，上哈哈学车";
        mDescription = "好友力荐:\n哈哈学车优秀教练" + partner.name;
        mImageUrl = "https://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = BuildConfig.SERVER_URL + "/share/training_partners/" + partner.id;
        HHLog.v("mUrl -> " + mUrl);
    }

    @Override
    public void addC1Label(int pos) {
        RelativeLayout rly = new RelativeLayout(this);
        rly.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View dividerView = new View(this);
        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
        dividerView.setLayoutParams(viewParams);
        dividerView.setBackgroundResource(R.color.haha_gray_divider);
        rly.addView(dividerView);

        TextView tvC1Label = new TextView(this);
        RelativeLayout.LayoutParams labelParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0, 0);
        tvC1Label.setLayoutParams(labelParams);
        tvC1Label.setText("C1手动档");
        tvC1Label.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        int tvLabelId = Utils.generateViewId();
        tvC1Label.setId(tvLabelId);
        rly.addView(tvC1Label);

        TextView tvMore = new TextView(this);
        RelativeLayout.LayoutParams tvMoreParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvMoreParams.setMargins(Utils.instence(this).dip2px(5), Utils.instence(this).dip2px(15), 0, 0);
        tvMoreParams.addRule(RelativeLayout.RIGHT_OF, tvLabelId);
        tvMore.setLayoutParams(tvMoreParams);
        tvMore.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        tvMore.setBackgroundResource(R.drawable.rect_bg_gray_bd_gray_ssm);
        tvMore.setText("?");
        int padding = Utils.instence(this).dip2px(4);
        tvMore.setPadding(padding, 0, padding, 0);
        tvMore.setGravity(Gravity.CENTER);
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAlertSimpleDialog dialog = new BaseAlertSimpleDialog(getContext(), "什么是C1手动档？",
                        "C1为手动挡小型车驾照，取得了C1类驾驶证的人可以驾驶C2类车");
                dialog.show();
            }
        });
        rly.addView(tvMore);

        mLlyPrices.addView(rly, pos);
    }

    @Override
    public void addC2Label(int pos) {
        RelativeLayout rly = new RelativeLayout(this);
        rly.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View dividerView = new View(this);
        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
        dividerView.setLayoutParams(viewParams);
        dividerView.setBackgroundResource(R.color.haha_gray_divider);
        rly.addView(dividerView);

        TextView tvC2Label = new TextView(this);
        RelativeLayout.LayoutParams labelParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0, 0);
        tvC2Label.setLayoutParams(labelParams);
        tvC2Label.setText("C2自动档");
        tvC2Label.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        int tvLabelId = Utils.generateViewId();
        tvC2Label.setId(tvLabelId);
        rly.addView(tvC2Label);

        TextView tvMore = new TextView(this);
        RelativeLayout.LayoutParams tvMoreParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvMoreParams.setMargins(Utils.instence(this).dip2px(5), Utils.instence(this).dip2px(15), 0, 0);
        tvMoreParams.addRule(RelativeLayout.RIGHT_OF, tvLabelId);
        tvMore.setLayoutParams(tvMoreParams);
        tvMore.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        tvMore.setBackgroundResource(R.drawable.rect_bg_gray_bd_gray_ssm);
        tvMore.setText("?");
        int padding = Utils.instence(this).dip2px(4);
        tvMore.setPadding(padding, 0, padding, 0);
        tvMore.setGravity(Gravity.CENTER);
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAlertSimpleDialog dialog = new BaseAlertSimpleDialog(getContext(), "什么是C2自动档？",
                        "C2为自动挡小型车驾照，取得了C2类驾驶证的人不可以驾驶C1类车。" +
                                "C2驾照培训费要稍贵于C1照。费用的差别主要是由于C2自动挡教练车数量比较少，使用过程中维修费用比较高所致。");
                dialog.show();
            }
        });
        rly.addView(tvMore);

        mLlyPrices.addView(rly, pos);
    }

    @Override
    public void addPrice(int pos, int price, int duration, String description) {
        RelativeLayout rly = new RelativeLayout(this);
        rly.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tvPriceLabel = new TextView(this);
        RelativeLayout.LayoutParams tvLabelParams = new RelativeLayout.LayoutParams(Utils.instence(this).dip2px(60), ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLabelParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0, Utils.instence(this).dip2px(15));
        tvPriceLabel.setLayoutParams(tvLabelParams);
        tvPriceLabel.setText(duration + "h");
        tvPriceLabel.setGravity(Gravity.CENTER);
        int padding = Utils.instence(this).dip2px(2);
        tvPriceLabel.setPadding(0, padding, 0, padding);
        tvPriceLabel.setBackgroundResource(R.drawable.rect_bg_trans_bd_appcolor_ssm);
        tvPriceLabel.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        int tvLabelId = Utils.generateViewId();
        tvPriceLabel.setId(tvLabelId);
        rly.addView(tvPriceLabel);

        TextView tvRemarks = new TextView(this);
        RelativeLayout.LayoutParams tvRemarksParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvRemarksParams.setMargins(Utils.instence(this).dip2px(10), 0, 0, 0);
        tvRemarksParams.addRule(RelativeLayout.RIGHT_OF, tvLabelId);
        tvRemarksParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        tvRemarks.setLayoutParams(tvRemarksParams);
        tvRemarks.setText(description);
        tvRemarks.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        tvRemarks.setTextSize(16);
        rly.addView(tvRemarks);

        TextView tvPrice = new TextView(this);
        RelativeLayout.LayoutParams tvPriceParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvPriceParams.setMargins(0, 0, Utils.instence(this).dip2px(20), 0);
        tvPriceParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        tvPriceParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        tvPrice.setLayoutParams(tvPriceParams);
        tvPrice.setText(Utils.getMoney(price));
        tvPrice.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        tvPrice.setTextSize(16);
        rly.addView(tvPrice);

        View dividerView = new View(this);
        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
        viewParams.addRule(RelativeLayout.BELOW, tvLabelId);
        viewParams.setMargins(Utils.instence(this).dip2px(20), 0, 0, 0);
        dividerView.setLayoutParams(viewParams);
        dividerView.setBackgroundResource(R.color.haha_gray_divider);
        rly.addView(dividerView);

        mLlyPrices.addView(rly, pos);
    }

    @Override
    public void alertToLogin(String alertMessage) {
        BaseConfirmSimpleDialog dialog = new BaseConfirmSimpleDialog(getContext(), "请登录", alertMessage, "去登录", "知道了",
                new BaseConfirmSimpleDialog.onClickListener() {
                    @Override
                    public void clickConfirm() {
                        ActivityCollector.finishAll();
                        Intent intent = new Intent(getContext(), StartLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void clickCancel() {
                    }
                });
        dialog.show();
    }

    /**
     * 获取分享API
     */
    private void regShareApi() {
        myApplication = HHBaseApplication.get(getContext());
        wxApi = myApplication.getIWXAPI();
        mTencent = myApplication.getTencentAPI();
        mWeiboShareAPI = myApplication.getWeiboAPI();
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
        textObject.text = mTitle + mDescription;
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
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
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
                        mPresenter.clickShareSuccessCount("wechat_friend");
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
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
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
                        mPresenter.clickShareSuccessCount("wechat_friend_zone");
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
            mPresenter.clickShareSuccessCount("QQ_friend");
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
            mPresenter.clickShareSuccessCount("qzone");
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
                    mPresenter.clickShareSuccessCount("weibo");
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
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showPartnerDetail(Partner partner) {
        mTvName.setText(partner.name);
        if (partner.description != null && partner.description.size() > 0) {
            String description = "";
            for (String des : partner.description) {
                description += des;
            }
            mTvDescription.setText(description);
        }
        mIvAvatar.setImageURI(partner.avatar);
        mIsImages.updateImages(partner.images);
        int width = Utils.instence(this).getDm().widthPixels;
        int height = Math.round(width * 4 / 5);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        mIsImages.setLayoutParams(p);
        RelativeLayout.LayoutParams paramAvatar = new RelativeLayout.LayoutParams(Utils.instence(this).dip2px(70), Utils.instence(this).dip2px(70));
        paramAvatar.setMargins(Utils.instence(this).dip2px(30), height - Utils.instence(this).dip2px(35), 0, 0);
        mIvAvatar.setLayoutParams(paramAvatar);
        RelativeLayout.LayoutParams paramLlyFlCd = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramLlyFlCd.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.iv_partner_avatar);
        paramLlyFlCd.addRule(RelativeLayout.RIGHT_OF, R.id.iv_partner_avatar);
        mRlyInfoLine.setLayoutParams(paramLlyFlCd);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void enableApplaud(boolean enable) {
        mTvApplaud.setClickable(enable);
    }

    @Override
    public void showApplaud(boolean isApplaud) {
        mTvApplaud.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, isApplaud ? R.drawable.ic_list_best_click : R.drawable.ic_list_best_unclick), null, null, null);
    }

    @Override
    public void setApplaudCount(int count) {
        mTvApplaud.setText(String.valueOf(count));
    }

    @Override
    public void startApplaudAnimation() {
        //点赞
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(200);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setFillAfter(true); //让其保持动画结束时的状态。
        mTvApplaud.startAnimation(animationSet);
    }

    @OnClick({R.id.tv_applaud_count,
            R.id.tv_contact})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_applaud_count:
                mPresenter.applaud();
                break;
            case R.id.tv_contact:
                mPresenter.clickContactCount();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CELL_PHONE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    callMyCoach(mPresenter.getPartner().phone);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("partner", mPresenter.getPartner());
        setResult(RESULT_OK, intent);
        super.finish();
    }

    /**
     * 联系教练
     */
    private void callMyCoach(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                callMyCoach(mPresenter.getPartner().phone);
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系教练");
            }
        } else if (requestCode == PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToSms();
            } else {
                showMessage("请允许发送短信权限，不然无法分享到短信");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, shareQQListener);
        Tencent.onActivityResultData(requestCode, resultCode, data, shareQZoneListener);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void shareToSms() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", mTitle + mDescription + mUrl);
        startActivity(intent);
    }
}
