package com.hahaxueche.ui.activity.community;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.community.Article;
import com.hahaxueche.model.community.Comment;
import com.hahaxueche.presenter.community.ArticlePresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.dialog.community.CommentDialog;
import com.hahaxueche.ui.view.community.ArticleView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
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

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/22.
 */

public class ArticleActivity extends HHBaseActivity implements ArticleView, IWeiboHandler.Response {
    ImageView mIvBack;
    ImageView mIvShare;
    @BindView(R.id.tv_publish_comment)
    TextView mTvPublishComment;
    @BindView(R.id.webview_content)
    WebView webView;
    @BindView(R.id.tv_comment_count)
    TextView mTvCommentCount;
    @BindView(R.id.tv_like_count)
    TextView mTvLikeCount;
    @BindView(R.id.tv_read_count)
    TextView mTvViewCount;
    @BindView(R.id.lly_comments)
    LinearLayout mLlyComments;
    @BindView(R.id.frl_webview)
    FrameLayout mFrlWebView;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    private CommentDialog commentDialog;
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
    private ArticlePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ArticlePresenter();
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.clearCache(true);
                dismissProgressDialog();
                mPresenter.loadComments();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgressDialog("页面加载中，请稍后...");
            }
        });
        WebSettings mWebSettings = webView.getSettings();
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
        Intent intent = getIntent();
        if (intent.getParcelableExtra("article") != null) {
            mPresenter.setArticle((Article) intent.getParcelableExtra("article"));
            regShareApi();
        }
    }

    @Override
    public void initShareData(Article article) {
        mTitle = article.title;
        mDescription = article.intro;
        mImageUrl = "http://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = BuildConfig.MOBILE_URL + "/articles/" + article.id + "?view=raw";
        HHLog.v("mUrl -> " + mUrl);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }


    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base_share);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mIvShare = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_share);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArticleActivity.this.finish();
            }
        });
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
        WeiboMessage weiboMessage = new WeiboMessage();
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = mTitle;
        mediaObject.description = mDescription;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
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
        commentDialog = null;
        shareDialog = null;
        super.onDestroy();
    }

    @OnClick({R.id.tv_publish_comment,
            R.id.tv_like_count})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_publish_comment:
                mPresenter.clickComment();
                break;
            case R.id.tv_like_count:
                mPresenter.applaud();
                break;
            default:
                break;
        }
    }

    @Override
    public void showCommentDialog() {
        if (commentDialog == null) {
            commentDialog = new CommentDialog(getContext(), new CommentDialog.OnCommentListener() {
                @Override
                public void send(String comment) {
                    mPresenter.sendComment(comment);
                }

                @Override
                public void saveDraft(String draft) {
                    mPresenter.saveDraft(draft);
                }

                @Override
                public void clearDraft() {
                    mPresenter.clearDraft();
                }
            });
        }
        commentDialog.show();
    }

    @Override
    public void setDraft(String draft) {
        mTvPublishComment.setText(draft);
    }

    @Override
    public void setViewCount(String count) {
        mTvViewCount.setText(count);
    }

    @Override
    public void setCommentCount(String count) {
        mTvCommentCount.setText(count);
    }

    @Override
    public void setWebViewUrl(String url) {
        webView.loadUrl(url);
    }

    @Override
    public void removeCommentViews() {
        mLlyComments.removeAllViews();
    }

    @Override
    public void addCommentTitle() {
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width)));
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
        mLlyComments.addView(divider);

        TextView tvTitle = new TextView(this);
        LinearLayout.LayoutParams tvTitleParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvTitleParam.setMargins(0, Utils.instence(this).dip2px(10), 0, 0);
        tvTitle.setLayoutParams(tvTitleParam);
        tvTitle.setTextSize(20);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
        TextPaint tp = tvTitle.getPaint();
        tp.setFakeBoldText(true);
        tvTitle.setText("评论");
        mLlyComments.addView(tvTitle);
    }

    @Override
    public void addComment(Comment comment, boolean isLast) {
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
        ivAvatar.setImageURI(comment.student_avatar);
        int ivAvatarId = Utils.generateViewId();
        ivAvatar.setId(ivAvatarId);
        rly.addView(ivAvatar);

        TextView tvName = new TextView(this);
        RelativeLayout.LayoutParams tvNameParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvNameParams.addRule(RelativeLayout.RIGHT_OF, ivAvatarId);
        tvNameParams.addRule(RelativeLayout.ALIGN_TOP, ivAvatarId);
        tvNameParams.setMargins(Utils.instence(this).dip2px(10), 0, 0, 0);
        tvName.setLayoutParams(tvNameParams);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_text));
        tvName.setTextSize(12);
        tvName.setText(comment.student_name);
        int tvNameId = Utils.generateViewId();
        tvName.setId(tvNameId);
        rly.addView(tvName);

        TextView tvReviewDate = new TextView(this);
        RelativeLayout.LayoutParams tvReviewDateParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvReviewDateParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvNameId);
        tvReviewDateParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        tvReviewDateParams.setMargins(0, 0, Utils.instence(this).dip2px(20), 0);
        tvReviewDate.setLayoutParams(tvReviewDateParams);
        tvReviewDate.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_text));
        tvReviewDate.setTextSize(12);
        tvReviewDate.setText(Utils.getDateDotFromUTC(comment.created_at));
        rly.addView(tvReviewDate);


        TextView tvComment = new TextView(this);
        RelativeLayout.LayoutParams tvCommentParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvCommentParams.addRule(RelativeLayout.ALIGN_LEFT, tvNameId);
        tvCommentParams.addRule(RelativeLayout.BELOW, tvNameId);
        tvCommentParams.setMargins(0, Utils.instence(this).dip2px(10), Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(20));
        tvComment.setLayoutParams(tvCommentParams);
        tvComment.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        tvComment.setTextSize(14);
        tvComment.setText(comment.content);
        tvComment.setLineSpacing(0, 1.2f);
        int tvCommentId = Utils.generateViewId();
        tvComment.setId(tvCommentId);
        rly.addView(tvComment);

        if (!isLast) {
            View divider = new View(this);
            RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
            dividerParams.setMargins(Utils.instence(this).dip2px(20), 0, 0, 0);
            dividerParams.addRule(RelativeLayout.BELOW, tvCommentId);
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            rly.addView(divider);
        }
        mLlyComments.addView(rly);
    }

    @Override
    public void addMoreCommentButton() {
        View divider = new View(this);
        LinearLayout.LayoutParams dividerParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
        dividerParam.setMargins(0, Utils.instence(this).dip2px(15), 0, 0);
        divider.setLayoutParams(dividerParam);
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
        mLlyComments.addView(divider);
        TextView tvMoreComment = new TextView(this);
        tvMoreComment.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tvMoreComment.setPadding(0, Utils.instence(this).dip2px(15), 0, Utils.instence(this).dip2px(15));
        tvMoreComment.setTextSize(16);
        tvMoreComment.setGravity(Gravity.CENTER);
        tvMoreComment.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        tvMoreComment.setText("点击查看更多");
        tvMoreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ArticleCommentsActivity.class);
                intent.putParcelableArrayListExtra("comments", mPresenter.getArticle().comments);
                startActivity(intent);
            }
        });
        mLlyComments.addView(tvMoreComment);
    }

    @Override
    public void enableApplaud(boolean enable) {
        mTvLikeCount.setClickable(enable);
    }

    @Override
    public void showApplaud(boolean isApplaud) {
        mTvLikeCount.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, isApplaud ? R.drawable.icon_like_click : R.drawable.icon_like), null, null, null);
        mTvLikeCount.setTextColor(ContextCompat.getColor(this, isApplaud ? R.color.app_theme_color : R.color.haha_gray_text));
    }

    @Override
    public void setApplaudCount(String count) {
        mTvLikeCount.setText(count);
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
        mTvLikeCount.startAnimation(animationSet);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, shareQQListener);
        Tencent.onActivityResultData(requestCode, resultCode, data, shareQZoneListener);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
