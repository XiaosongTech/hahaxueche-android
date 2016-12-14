package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.presenter.findCoach.CoachDetailPresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.activity.myPage.ReferFriendsActivity;
import com.hahaxueche.ui.activity.myPage.UploadIdCardActivity;
import com.hahaxueche.ui.dialog.BaseAlertSimpleDialog;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.view.findCoach.CoachDetailView;
import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
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
 * Created by wangshirui on 16/10/5.
 */

public class CoachDetailActivity extends HHBaseActivity implements CoachDetailView, IWeiboHandler.Response {
    private CoachDetailPresenter mPresenter;
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    @BindView(R.id.tv_coach_name)
    TextView mTvName;
    @BindView(R.id.tv_description)
    TextView mTvDescription;
    @BindView(R.id.iv_coach_avatar)
    SimpleDraweeView mIvAvatar;
    @BindView(R.id.is_coach_images)
    ImageSwitcher mIsImages;
    @BindView(R.id.rly_info_line)
    RelativeLayout mRlyInfoLine;
    @BindView(R.id.iv_is_golden_coach)
    ImageView mIvGoldenCoach;
    @BindView(R.id.iv_is_cash_pledge)
    ImageView mIvCashPledge;
    @BindView(R.id.tv_satisfaction_rate)
    TextView mTvSatisfactionRate;
    @BindView(R.id.tv_coach_level)
    TextView mTvCoachLevel;
    @BindView(R.id.tv_pass_days)
    TextView mTvPassDays;
    @BindView(R.id.tv_pass_rate)
    TextView mTvPassRate;
    @BindView(R.id.tv_train_location)
    TextView mTvTrainLocation;
    @BindView(R.id.tv_comments_count)
    TextView mTvCommentsCount;
    @BindView(R.id.sv_average_rating)
    ScoreView mSvCoachScore;
    @BindView(R.id.lly_train_school)
    LinearLayout mLlyTrainSchool;
    @BindView(R.id.tv_train_school)
    TextView mTvTrainSchoolName;
    @BindView(R.id.lly_peer_coaches)
    LinearLayout mLlyPeerCoaches;
    @BindView(R.id.lly_reviews)
    LinearLayout mLlyReviews;
    @BindView(R.id.fly_more_comments)
    FrameLayout mFlyMoreComments;
    @BindView(R.id.tv_more_reviews)
    TextView mTvMoreReviews;
    @BindView(R.id.iv_follow)
    ImageView mIvFollow;
    @BindView(R.id.tv_applaud_count)
    TextView mTvApplaud;
    @BindView(R.id.lly_prices)
    LinearLayout mLlyPrices;
    @BindView(R.id.tv_badge_level)
    TextView mTvBadgeLevel;
    @BindView(R.id.tv_badge_pay)
    TextView mTvBadgePay;
    private ReviewResponseList mReviewResponse;
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

    private static final int REQUEST_CODE_PURCHASE_COACH = 1;
    private static final int REQUEST_CODE_PAY_SUCCESS = 2;
    private static final int REQUEST_CODE_UPLOAD_ID_CARD = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CoachDetailPresenter();
        setContentView(R.layout.activity_coach_detail);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        Intent intent = getIntent();
        if (intent.getParcelableExtra("coach") != null) {
            mPresenter.setCoach((Coach) intent.getParcelableExtra("coach"));
        } else {
            String coach_id;
            if (intent.getSerializableExtra("coachId") != null) {
                coach_id = (String) intent.getSerializableExtra("coachId");
            } else {
                coach_id = getIntent().getStringExtra("coach_id");
            }
            mPresenter.setCoach(coach_id);
        }
        regShareApi();
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
    }

    @Override
    public void initShareData(Coach coach) {
        mTitle = "哈哈学车-选驾校，挑教练，上哈哈学车";
        mDescription = "好友力荐:\n哈哈学车优秀教练" + coach.name;
        mImageUrl = "http://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = BuildConfig.SERVER_URL + "/share/coaches/" + coach.id;
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
    public void addPrice(int pos, boolean isVIP, int price) {
        RelativeLayout rly = new RelativeLayout(this);
        rly.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView tvPriceLabel = new TextView(this);
        RelativeLayout.LayoutParams tvLabelParams = new RelativeLayout.LayoutParams(Utils.instence(this).dip2px(60), ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLabelParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0, Utils.instence(this).dip2px(15));
        tvPriceLabel.setLayoutParams(tvLabelParams);
        tvPriceLabel.setText(isVIP ? "VIP班" : "普通班");
        tvPriceLabel.setGravity(Gravity.CENTER);
        int padding = Utils.instence(this).dip2px(2);
        tvPriceLabel.setPadding(0, padding, 0, padding);
        tvPriceLabel.setBackgroundResource(isVIP ? R.drawable.rect_bg_trans_bd_yellow_ssm : R.drawable.rect_bg_trans_bd_appcolor_ssm);
        tvPriceLabel.setTextColor(ContextCompat.getColor(this, isVIP ? R.color.haha_yellow : R.color.app_theme_color));
        int tvLabelId = Utils.generateViewId();
        tvPriceLabel.setId(tvLabelId);
        rly.addView(tvPriceLabel);

        TextView tvRemarks = new TextView(this);
        RelativeLayout.LayoutParams tvRemarksParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvRemarksParams.setMargins(Utils.instence(this).dip2px(10), 0, 0, 0);
        tvRemarksParams.addRule(RelativeLayout.RIGHT_OF, tvLabelId);
        tvRemarksParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        tvRemarks.setLayoutParams(tvRemarksParams);
        tvRemarks.setText(isVIP ? "一人一车，极速拿证" : "四人一车，性价比高");
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
        tvPrice.setTextColor(ContextCompat.getColor(this, isVIP ? R.color.haha_yellow : R.color.app_theme_color));
        tvPrice.setTextSize(16);
        rly.addView(tvPrice);

        if (!isVIP) {
            View dividerView = new View(this);
            RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
            viewParams.addRule(RelativeLayout.BELOW, tvLabelId);
            viewParams.setMargins(Utils.instence(this).dip2px(20), 0, 0, 0);
            dividerView.setLayoutParams(viewParams);
            dividerView.setBackgroundResource(R.color.haha_gray_divider);
            rly.addView(dividerView);
        }

        mLlyPrices.addView(rly, pos);
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
                CoachDetailActivity.this.finish();
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
        mWeiboShareAPI.sendRequest(CoachDetailActivity.this, request);
    }

    private void shareToWeixin() {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mTitle;
        msg.description = mDescription;
        Bitmap thumb = BitmapFactory.decodeResource(CoachDetailActivity.this.getResources(), R.mipmap.ic_launcher);
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
        Bitmap thumb = BitmapFactory.decodeResource(CoachDetailActivity.this.getResources(), R.mipmap.ic_launcher);
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
    public void showCoachDetail(Coach coach) {
        mTvName.setText(coach.name);
        mTvDescription.setText(coach.bio);
        mIvAvatar.setImageURI(coach.avatar);
        mIsImages.updateImages(coach.images);
        int width = Utils.instence(this).getDm().widthPixels;
        int height = Math.round(width * 4 / 5);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        mIsImages.setLayoutParams(p);
        RelativeLayout.LayoutParams paramAvatar = new RelativeLayout.LayoutParams(Utils.instence(this).dip2px(70), Utils.instence(this).dip2px(70));
        paramAvatar.setMargins(Utils.instence(this).dip2px(30), height - Utils.instence(this).dip2px(35), 0, 0);
        mIvAvatar.setLayoutParams(paramAvatar);
        RelativeLayout.LayoutParams paramLlyFlCd = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramLlyFlCd.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.iv_coach_avatar);
        paramLlyFlCd.addRule(RelativeLayout.RIGHT_OF, R.id.iv_coach_avatar);
        mRlyInfoLine.setLayoutParams(paramLlyFlCd);
        //金牌教练显示
        if (coach.skill_level.equals("1")) {
            mIvGoldenCoach.setVisibility(View.VISIBLE);
            mTvCoachLevel.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_auth_golden), null);
        } else {
            mIvGoldenCoach.setVisibility(View.GONE);
            mTvCoachLevel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        //保证金
        if (coach.has_cash_pledge == 1) {
            mIvCashPledge.setVisibility(View.VISIBLE);
        } else {
            mIvCashPledge.setVisibility(View.GONE);
        }
        mTvSatisfactionRate.setText(Utils.getRate(coach.satisfaction_rate));
        mTvCoachLevel.setText(coach.skill_level_label);
        mTvPassDays.setText(coach.average_pass_days + "天");
        mTvPassRate.setText(Utils.getRate(coach.stage_three_pass_rate));
        mTvTrainLocation.setText(mPresenter.getTrainingFieldName());
        ArrayList<Coach> peerCoaches = coach.peer_coaches;
        if (peerCoaches != null && peerCoaches.size() > 0) {
            for (Coach peerCoach : peerCoaches) {
                mLlyPeerCoaches.addView(getPeerCoachAdapter(peerCoach));
            }
        } else {
            mLlyPeerCoaches.setVisibility(View.GONE);
        }
        //学员评价数量
        mTvCommentsCount.setText("学员评价（" + coach.review_count + "）");
        //综合得分
        float averageRating = 0;
        if (!TextUtils.isEmpty(coach.average_rating)) {
            averageRating = Float.parseFloat(coach.average_rating);
        }
        if (averageRating > 5) {
            averageRating = 5;
        }
        mSvCoachScore.setScore(averageRating, true);
        if (!TextUtils.isEmpty(coach.driving_school)) {
            mLlyTrainSchool.setVisibility(View.VISIBLE);
            mTvTrainSchoolName.setText(coach.driving_school);
        } else {
            mLlyTrainSchool.setVisibility(View.GONE);
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showNoReview(String coachName) {
        mFlyMoreComments.setClickable(false);
        mTvMoreReviews.setText(coachName + "教练目前还没有评价");
        mTvMoreReviews.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
    }

    @OnClick({R.id.fly_more_comments,
            R.id.rly_price,
            R.id.iv_follow,
            R.id.tv_applaud_count,
            R.id.tv_pay,
            R.id.rly_training_field,
            R.id.tv_free_try,
            R.id.lly_platform_assurance
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_follow:
                mPresenter.follow();
                break;
            case R.id.fly_more_comments:
                mPresenter.clickCommentsCount();
                Intent intent = new Intent(getContext(), ReviewListActivity.class);
                intent.putExtra("coach", mPresenter.getCoach());
                startActivity(intent);
                break;
            case R.id.rly_price:
                mPresenter.clickPrice();
                intent = new Intent(getContext(), PriceActivity.class);
                intent.putExtra("coach", mPresenter.getCoach());
                startActivity(intent);
                break;
            case R.id.tv_applaud_count:
                mPresenter.applaud();
                break;
            case R.id.tv_pay:
                mPresenter.clickPurchaseCount();
                mPresenter.purchaseCoach();
                break;
            case R.id.rly_training_field:
                mPresenter.clickTrainFieldCount();
                Field field = mPresenter.getTrainingField();
                if (field != null) {
                    intent = new Intent(getContext(), FieldMapActivity.class);
                    intent.putExtra("field", field);
                    startActivity(intent);
                }
                break;
            case R.id.tv_free_try:
                mPresenter.freeTry();
                break;
            case R.id.lly_platform_assurance:
                mPresenter.clickPlatformAssurance();
                break;
            default:
                break;
        }
    }

    @Override
    public void showReviews(ReviewResponseList responseList) {
        mReviewResponse = responseList;
        int showReviewCount = responseList.data.size() > 3 ? 3 : responseList.data.size();
        for (int i = 0; i < showReviewCount; i++) {
            mLlyReviews.addView(getReviewAdapter(responseList.data.get(i), i == showReviewCount - 1), i + 2);
        }
    }

    @Override
    public void enableFollow(boolean enable) {
        mIvFollow.setClickable(enable);
    }

    @Override
    public void showFollow(boolean isFollow) {
        if (isFollow) {
            mIvFollow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coachmsg_attention_on));
        } else {
            mIvFollow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coachmsg_attention_hold));
        }
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


    @Override
    public void navigateToPurchaseCoach(Coach coach) {
        if (coach == null) return;
        Intent intent = new Intent(getContext(), PurchaseCoachActivity.class);
        intent.putExtra("coach", coach);
        startActivityForResult(intent, REQUEST_CODE_PURCHASE_COACH);
    }

    private RelativeLayout getPeerCoachAdapter(final Coach coach) {
        RelativeLayout rly = new RelativeLayout(this);
        LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rly.setLayoutParams(rlyParams);

        SimpleDraweeView ivAvatar = new SimpleDraweeView(this);
        GenericDraweeHierarchy hierarchy = ivAvatar.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.ic_coach_ava);
        hierarchy.setRoundingParams(new RoundingParams().setRoundAsCircle(true));
        RelativeLayout.LayoutParams ivAvatarParams = new RelativeLayout.LayoutParams(Utils.instence(this).dip2px(40), Utils.instence(this).dip2px(40));
        ivAvatarParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), Utils.instence(this).dip2px(10), Utils.instence(this).dip2px(15));
        ivAvatar.setLayoutParams(ivAvatarParams);
        ivAvatar.setImageURI(coach.avatar);
        int ivAvatarId = Utils.generateViewId();
        ivAvatar.setId(ivAvatarId);
        rly.addView(ivAvatar);

        TextView tvName = new TextView(this);
        RelativeLayout.LayoutParams tvNameParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvNameParams.addRule(RelativeLayout.RIGHT_OF, ivAvatarId);
        tvNameParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        tvName.setLayoutParams(tvNameParams);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        tvName.setTextSize(16);
        tvName.setText(coach.name);
        rly.addView(tvName);

        ImageView ivArrow = new ImageView(this);
        RelativeLayout.LayoutParams ivArrowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ivArrowParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        ivArrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        ivArrowParams.setMargins(0, 0, Utils.instence(this).dip2px(20), 0);
        ivArrow.setLayoutParams(ivArrowParams);
        ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coachmsg_more_arrow));
        rly.addView(ivArrow);

        View divider = new View(this);
        RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
        dividerParams.setMargins(Utils.instence(this).dip2px(20), 0, 0, 0);
        dividerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
        rly.addView(divider);

        //点击合作教练,查看详情
        rly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CoachDetailActivity.class);
                intent.putExtra("coach_id", coach.id);
                startActivity(intent);
            }
        });

        return rly;
    }

    private RelativeLayout getReviewAdapter(Review review, boolean isLastLine) {
        RelativeLayout rly = new RelativeLayout(this);
        LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rly.setLayoutParams(rlyParams);

        SimpleDraweeView ivAvatar = new SimpleDraweeView(this);
        GenericDraweeHierarchy hierarchy = ivAvatar.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.ic_mypage_ava);
        hierarchy.setRoundingParams(new RoundingParams().setRoundAsCircle(true));
        RelativeLayout.LayoutParams ivAvatarParams = new RelativeLayout.LayoutParams(Utils.instence(this).dip2px(40), Utils.instence(this).dip2px(40));
        ivAvatarParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0, 0);
        ivAvatar.setLayoutParams(ivAvatarParams);
        ivAvatar.setImageURI(review.reviewer.avatar);
        int ivAvatarId = Utils.generateViewId();
        ivAvatar.setId(ivAvatarId);
        rly.addView(ivAvatar);

        TextView tvName = new TextView(this);
        RelativeLayout.LayoutParams tvNameParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvNameParams.addRule(RelativeLayout.RIGHT_OF, ivAvatarId);
        tvNameParams.addRule(RelativeLayout.ALIGN_TOP, ivAvatarId);
        tvNameParams.setMargins(Utils.instence(this).dip2px(10), 0, 0, 0);
        tvName.setLayoutParams(tvNameParams);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        tvName.setTextSize(18);
        tvName.setText(review.reviewer.name);
        int tvNameId = Utils.generateViewId();
        tvName.setId(tvNameId);
        rly.addView(tvName);

        TextView tvReviewDate = new TextView(this);
        RelativeLayout.LayoutParams tvReviewDateParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvReviewDateParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvNameId);
        tvReviewDateParams.addRule(RelativeLayout.RIGHT_OF, tvNameId);
        tvReviewDateParams.setMargins(Utils.instence(this).dip2px(6), 0, 0, 0);
        tvReviewDate.setLayoutParams(tvReviewDateParams);
        tvReviewDate.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_text));
        tvReviewDate.setTextSize(12);
        tvReviewDate.setText(review.updated_at.substring(0, 10));
        rly.addView(tvReviewDate);

        ScoreView svScore = new ScoreView(this);
        RelativeLayout.LayoutParams svScoreParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        svScoreParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvNameId);
        svScoreParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        svScoreParams.setMargins(0, 0, Utils.instence(this).dip2px(20), 0);
        svScore.setLayoutParams(svScoreParams);
        float reviewerRating = 0;
        if (!TextUtils.isEmpty(review.rating)) {
            reviewerRating = Float.parseFloat(review.rating);
        }
        if (reviewerRating > 5) {
            reviewerRating = 5;
        }
        svScore.setScore(reviewerRating, false);
        rly.addView(svScore);

        TextView tvComment = new TextView(this);
        RelativeLayout.LayoutParams tvCommentParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvCommentParams.addRule(RelativeLayout.ALIGN_LEFT, tvNameId);
        tvCommentParams.addRule(RelativeLayout.BELOW, tvNameId);
        tvCommentParams.setMargins(0, Utils.instence(this).dip2px(10), Utils.instence(this).dip2px(20), 0);
        tvComment.setLayoutParams(tvCommentParams);
        tvComment.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        tvComment.setTextSize(12);
        tvComment.setText(review.comment);
        tvComment.setLineSpacing(0, 1.2f);
        int tvCommentId = Utils.generateViewId();
        tvComment.setId(tvCommentId);
        rly.addView(tvComment);

        View divider = new View(this);
        RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
        if (!isLastLine) {
            dividerParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0, 0);
        } else {
            dividerParams.setMargins(0, Utils.instence(this).dip2px(15), 0, 0);
        }
        dividerParams.addRule(RelativeLayout.BELOW, tvCommentId);
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
        rly.addView(divider);

        return rly;
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

    @Override
    public void setCoachBadge(boolean isGolden) {
        mTvBadgeLevel.setCompoundDrawablesWithIntrinsicBounds(null,
                ContextCompat.getDrawable(this, isGolden ? R.drawable.ic_jinpaijiaolian : R.drawable.ic_jiaolianrenzheng), null, null);
        mTvBadgeLevel.setText(isGolden ? "金牌教练" : "教练认证");
    }

    @Override
    public void setPayBadge(boolean isCashPledge) {
        mTvBadgePay.setCompoundDrawablesWithIntrinsicBounds(null,
                ContextCompat.getDrawable(this, isCashPledge ? R.drawable.ic_xianxiangpeifu : R.drawable.ic_mianfeishixue), null, null);
        mTvBadgePay.setText(isCashPledge ? "先行赔付" : "免费试学");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PURCHASE_COACH) {
            if (resultCode == Activity.RESULT_OK) {
                startActivityForResult(new Intent(getContext(), PaySuccessActivity.class), REQUEST_CODE_PAY_SUCCESS);
            }
        } else if (requestCode == REQUEST_CODE_PAY_SUCCESS) {
            Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
            intent.putExtra("isFromPaySuccess", true);
            startActivityForResult(intent, REQUEST_CODE_UPLOAD_ID_CARD);
        } else if (requestCode == REQUEST_CODE_UPLOAD_ID_CARD) {
            startActivity(new Intent(getContext(), ReferFriendsActivity.class));
        }
        Tencent.onActivityResultData(requestCode, resultCode, data, shareQQListener);
        Tencent.onActivityResultData(requestCode, resultCode, data, shareQZoneListener);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("coach", mPresenter.getCoach());
        setResult(RESULT_OK, intent);
        super.finish();
    }
}
