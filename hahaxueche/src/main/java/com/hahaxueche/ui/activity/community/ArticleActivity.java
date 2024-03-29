package com.hahaxueche.ui.activity.community;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
import com.hahaxueche.R;
import com.hahaxueche.model.community.Article;
import com.hahaxueche.model.community.Comment;
import com.hahaxueche.presenter.community.ArticlePresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.dialog.community.CommentDialog;
import com.hahaxueche.ui.view.community.ArticleView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

/**
 * Created by wangshirui on 16/9/22.
 */

public class ArticleActivity extends HHBaseActivity implements ArticleView {
    ImageView mIvBack;
    ImageView mIvShare;
    TextView mTvTitle;
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
    private String mTitle;
    private String mDescription;
    private String mImageUrl;
    private String mUrl;
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
                mTvTitle.setText(view.getTitle());
                super.onPageFinished(view, url);
                view.clearCache(true);
                dismissProgressDialog();
                mPresenter.loadComments();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgressDialog();
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
        } else {
            mPresenter.setArticle(intent.getStringExtra("articleId"));
        }
    }

    @Override
    public void initShareData(Article article, String shortenUrl) {
        mTitle = article.title;
        mDescription = article.intro;
        mImageUrl = "https://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = shortenUrl;
        HHLog.v("mUrl -> " + mUrl);
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
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }


    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base_share);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mIvShare = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_share);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
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
                                case 5:
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, RequestCode.PERMISSIONS_REQUEST_SEND_SMS_FOR_SHARE);
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

    private void shareToQQ() {
        ShareUtil.shareMedia(this, SharePlatform.QQ, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                mPresenter.clickShareSuccessCount("QQ_friend");
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
        ShareUtil.shareMedia(this, SharePlatform.QZONE, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                mPresenter.clickShareSuccessCount("qzone");
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
        ShareUtil.shareMedia(this, SharePlatform.WEIBO, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                mPresenter.clickShareSuccessCount("weibo");
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
        ShareUtil.shareMedia(this, SharePlatform.WX, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                mPresenter.clickShareSuccessCount("wechat_friend");
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
        ShareUtil.shareMedia(this, SharePlatform.WX_TIMELINE, mTitle, mDescription, mUrl, mImageUrl, new ShareListener() {
            @Override
            public void shareSuccess() {
                if (shareDialog != null) {
                    shareDialog.dismiss();
                }
                mPresenter.clickShareSuccessCount("wechat_friend_zone");
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
                mPresenter.clickCommentCount();
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
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("article", mPresenter.getArticle());
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_SEND_SMS_FOR_SHARE) {
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
        intent.putExtra("sms_body", mTitle + mUrl);
        startActivity(intent);
    }
}
