package com.hahaxueche.ui.activity.community;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.community.ExamLibraryPresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.adapter.community.ExamLibraryPageAdapter;
import com.hahaxueche.ui.dialog.BaseAlertSimpleDialog;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.view.community.ExamLibraryView;
import com.hahaxueche.util.ExamLib;
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
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ExamLibraryActivity extends HHBaseActivity implements ExamLibraryView, IWeiboHandler.Response {
    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.iv_pass)
    ImageView mIvPass;
    @BindView(R.id.lly_not_login)
    LinearLayout mLlyNotLogin;
    @BindView(R.id.lly_not_purchase)
    LinearLayout mLlyNotPurchase;
    @BindView(R.id.lly_scores)
    LinearLayout mLlyScores;
    @BindView(R.id.tv_insurance_count)
    TextView mTvInsuranceCount;
    @BindView(R.id.iv_score1)
    ImageView mIvScore1;
    @BindView(R.id.iv_score2)
    ImageView mIvScore2;
    @BindView(R.id.iv_score3)
    ImageView mIvScore3;
    @BindView(R.id.iv_score4)
    ImageView mIvScore4;
    @BindView(R.id.iv_score5)
    ImageView mIvScore5;
    @BindView(R.id.tv_pass_score_text)
    TextView mTvPassScoreText;
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    @BindView(R.id.tv_share_scores)
    TextView mTvShareScores;
    @BindView(R.id.tv_to_exam)
    TextView mTvToExam;
    private ShareAppDialog mShareDialog;
    private ExamLibraryPresenter mPresenter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ExamLibraryPresenter();
        setContentView(R.layout.activity_exam_library);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        ExamLibraryPageAdapter adapter = new ExamLibraryPageAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        HHBaseApplication application = HHBaseApplication.get(getContext());
        HashMap<String, String> map = new HashMap();
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(getContext(), "online_test_page_viewed", map);
        } else {
            MobclickAgent.onEvent(getContext(), "online_test_page_viewed");
        }
        regShareApi();
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        mShareDialog = null;
        super.onDestroy();
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
                ExamLibraryActivity.this.finish();
            }
        });
        mTvTitle.setText("科一保过");
    }

    @OnClick({R.id.tv_share_scores,
            R.id.tv_to_exam,
            R.id.iv_pass,
            R.id.tv_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_share_scores:
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
                break;
            case R.id.tv_to_exam:
                Intent intent = new Intent(this, StartExamActivity.class);
                intent.putExtra("examType", ExamLib.EXAM_TYPE_1);
                startActivityForResult(intent, 2);
                break;
            case R.id.iv_pass:
                BaseAlertSimpleDialog dialog = new BaseAlertSimpleDialog(getContext(), "什么是保过卡？",
                        "在哈哈学车平台上注册登录，即可获得保过卡。\n学员在哈哈学车平台报名后，通过哈哈学车APP模拟科目一考试5次成绩均在90分以上，" +
                                "并分享至第三方平台即可发起理赔，当科目一考试未通过可凭借成绩单获得全额赔付120元。");
                dialog.show();
                break;
            case R.id.tv_login:
                ActivityCollector.finishAll();
                intent = new Intent(getContext(), StartLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void initShareData(String desc, String shareUrl) {
        mTitle = "科一不过包陪";
        mDescription = desc;
        mImageUrl = "http://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = shareUrl;
        HHLog.v("mUrl -> " + mUrl);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data.getBooleanExtra("isShowShare", false)) {
                if (mShareDialog == null) {
                    mShareDialog = new ShareAppDialog(getContext(), mPresenter.getBonus());
                }
                mShareDialog.show();
            }
        } else if (requestCode == 2) {
            mPresenter.fetchScores();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showNotLogin() {
        mIvPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.protectioncard_noget));
        mLlyNotLogin.setVisibility(View.VISIBLE);
        mLlyNotPurchase.setVisibility(View.GONE);
        mLlyScores.setVisibility(View.GONE);
    }

    @Override
    public void showNotPurchase() {
        mIvPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.protectioncard_get));
        mLlyNotLogin.setVisibility(View.GONE);
        mLlyNotPurchase.setVisibility(View.VISIBLE);
        mLlyScores.setVisibility(View.GONE);
    }

    @Override
    public void showScores(int passCount) {
        mIvPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.protectioncard_get));
        mLlyNotLogin.setVisibility(View.GONE);
        mLlyNotPurchase.setVisibility(View.GONE);
        mLlyScores.setVisibility(View.VISIBLE);
        mIvScore1.setImageDrawable(ContextCompat.getDrawable(this, passCount > 0 ? R.drawable.ic_hahapass1 : R.drawable.ic_nopass1));
        mIvScore2.setImageDrawable(ContextCompat.getDrawable(this, passCount > 1 ? R.drawable.ic_hahapass2 : R.drawable.ic_nopass2));
        mIvScore3.setImageDrawable(ContextCompat.getDrawable(this, passCount > 2 ? R.drawable.ic_hahapass3 : R.drawable.ic_nopass3));
        mIvScore4.setImageDrawable(ContextCompat.getDrawable(this, passCount > 3 ? R.drawable.ic_hahapass4 : R.drawable.ic_nopass4));
        mIvScore5.setImageDrawable(ContextCompat.getDrawable(this, passCount > 4 ? R.drawable.ic_hahapass5 : R.drawable.ic_nopass5));
        if (passCount > 0) {
            mTvPassScoreText.setText("您已在" + (passCount > 5 ? 5 : passCount) + "次模拟考试中获得90分以上的成绩。");
            mTvShareScores.setVisibility(View.VISIBLE);
            mTvToExam.setVisibility(View.GONE);
        } else {
            mTvShareScores.setVisibility(View.GONE);
            mTvToExam.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setInsuranceCount(SpannableString ss) {
        mTvInsuranceCount.setText(ss);
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
}
