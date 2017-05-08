package com.hahaxueche.ui.activity.findCoach;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.user.UserIdentityInfo;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.presenter.findCoach.CoachDetailPresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.activity.myPage.MyVoucherActivity;
import com.hahaxueche.ui.activity.myPage.ReferFriendsActivity;
import com.hahaxueche.ui.activity.myPage.StudentReferActivity;
import com.hahaxueche.ui.activity.myPage.UploadIdCardActivity;
import com.hahaxueche.ui.dialog.BaseAlertSimpleDialog;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.dialog.homepage.GetUserIdentityDialog;
import com.hahaxueche.ui.view.findCoach.CoachDetailView;
import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 16/10/5.
 */

public class CoachDetailActivity extends HHBaseActivity implements CoachDetailView {
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
    @BindView(R.id.rb_average_rating)
    RatingBar mRbCoachScore;
    @BindView(R.id.tv_score)
    TextView mTvScore;
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
    @BindView(R.id.tv_badge_level)
    TextView mTvBadgeLevel;
    @BindView(R.id.tv_badge_pay)
    TextView mTvBadgePay;
    @BindView(R.id.tv_C1_label)
    TextView mTvC1Label;
    @BindView(R.id.tv_C2_label)
    TextView mTvC2Label;
    @BindView(R.id.tv_what_is_C1)
    TextView mTvWhatIsC1;
    @BindView(R.id.tv_what_is_C2)
    TextView mTvWhatIsC2;
    @BindView(R.id.rly_C1)
    RelativeLayout mRlyC1;
    @BindView(R.id.vw_license_divider)
    View mVwLicenseDivider;
    @BindView(R.id.frl_C2)
    FrameLayout mFrlC2;
    @BindView(R.id.lly_class_type)
    LinearLayout mLlyClassType;
    /*****************
     * 分享
     ******************/
    private ShareDialog shareDialog;
    private HHBaseApplication myApplication;
    private String mTitle;
    private String mDescription;
    private String mImageUrl;
    private String mUrl;
    private String mShareSmsUrl;

    /*****************
     * end
     ******************/

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
    }

    @Override
    public void initShareData(Coach coach) {
        mTitle = "哈哈学车-选驾校，挑教练，上哈哈学车";
        mDescription = "好友力荐:" + coach.name + "教练-" + coach.driving_school;
        mImageUrl = "https://haha-test.oss-cn-shanghai.aliyuncs.com/tmp%2Fhaha_240_240.jpg";
        mUrl = BuildConfig.MOBILE_URL + "/jiaolian/" + coach.id;
        HHLog.v("mUrl -> " + mUrl);
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
                            mPresenter.shortenUrl(mUrl, shareType);
                        }
                    });
                }
                shareDialog.show();
            }
        });
    }

    @Override
    public void startToShare(int shareType, String shareUrl) {
        switch (shareType) {
            case 0:
                shareToWeixin(shareUrl);
                break;
            case 1:
                shareToFriendCircle(shareUrl);
                break;
            case 2:
                shareToQQ(shareUrl);
                break;
            case 3:
                shareToWeibo(shareUrl);
                break;
            case 4:
                shareToQZone(shareUrl);
                break;
            case 5:
                mShareSmsUrl = shareUrl;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, RequestCode.PERMISSIONS_REQUEST_SEND_SMS_FOR_SHARE);
                } else {
                    shareToSms(shareUrl);
                }
            default:
                break;
        }
    }

    private void shareToQQ(String shareUrl) {
        ShareUtil.shareMedia(this, SharePlatform.QQ, mTitle, mDescription, shareUrl, mImageUrl, new ShareListener() {
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

    private void shareToQZone(String shareUrl) {
        ShareUtil.shareMedia(this, SharePlatform.QZONE, mTitle, mDescription, shareUrl, mImageUrl, new ShareListener() {
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

    private void shareToWeibo(String shareUrl) {
        ShareUtil.shareMedia(this, SharePlatform.WEIBO, mTitle, mDescription, shareUrl, mImageUrl, new ShareListener() {
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

    private void shareToWeixin(String shareUrl) {
        ShareUtil.shareMedia(this, SharePlatform.WX, mTitle, mDescription, shareUrl, mImageUrl, new ShareListener() {
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

    private void shareToFriendCircle(String shareUrl) {
        ShareUtil.shareMedia(this, SharePlatform.WX_TIMELINE, mTitle, mDescription, shareUrl, mImageUrl, new ShareListener() {
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
        super.onDestroy();
    }

    @Override
    public void setCoachName(String name) {
        mTvName.setText(name);
    }

    @Override
    public void setCoachBio(String bio) {
        mTvDescription.setText(bio);
    }

    @Override
    public void setCoachAvatar(String avatarUrl) {
        mIvAvatar.setImageURI(avatarUrl);
    }

    @Override
    public void setCoachImages(List<String> images) {
        mIsImages.updateImages(images);
        int width = Utils.instence(this).getDm().widthPixels;
        int height = Math.round(width * 4 / 5);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        mIsImages.setLayoutParams(p);
        RelativeLayout.LayoutParams paramAvatar = new RelativeLayout.LayoutParams(
                Utils.instence(this).dip2px(70), Utils.instence(this).dip2px(70));
        paramAvatar.setMargins(Utils.instence(this).dip2px(30), height - Utils.instence(this).dip2px(35), 0, 0);
        mIvAvatar.setLayoutParams(paramAvatar);
        RelativeLayout.LayoutParams paramLlyFlCd = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramLlyFlCd.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.iv_coach_avatar);
        paramLlyFlCd.addRule(RelativeLayout.RIGHT_OF, R.id.iv_coach_avatar);
        mRlyInfoLine.setLayoutParams(paramLlyFlCd);
    }

    @Override
    public void setCoachGolden(boolean isGolden) {
        if (isGolden) {
            mIvGoldenCoach.setVisibility(View.VISIBLE);
            mTvCoachLevel.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(this, R.drawable.ic_auth_golden), null);
        } else {
            mIvGoldenCoach.setVisibility(View.GONE);
            mTvCoachLevel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    @Override
    public void setCoachPledge(boolean hasPledge) {
        mIvCashPledge.setVisibility(hasPledge ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setCoachSatisfaction(String satisfactionRate) {
        mTvSatisfactionRate.setText(satisfactionRate);
    }

    @Override
    public void setCoachSkillLevel(String skillLevel) {
        mTvCoachLevel.setText(skillLevel);
    }

    @Override
    public void setCoachAveragePassDays(String passDays) {
        mTvPassDays.setText(passDays);
    }

    @Override
    public void setCoachPassRate(String passRate) {
        mTvPassRate.setText(passRate);
    }

    @Override
    public void setTrainingLocation(String trainingLocation) {
        mTvTrainLocation.setText(trainingLocation);
    }

    @Override
    public void setPeerCoaches(List<Coach> peerCoaches) {
        if (peerCoaches != null && peerCoaches.size() > 0) {
            for (Coach peerCoach : peerCoaches) {
                mLlyPeerCoaches.addView(getPeerCoachAdapter(peerCoach));
            }
        } else {
            mLlyPeerCoaches.setVisibility(View.GONE);
        }
    }

    @Override
    public void setCommentCount(String commentCount) {
        mTvCommentsCount.setText(commentCount);
    }

    @Override
    public void setCoachAverageRating(String averageRating) {
        //综合得分
        float rating = 0;
        try {
            rating = Float.parseFloat(averageRating);
            if (rating > 5) {
                rating = 5;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRbCoachScore.setRating(rating);
        mTvScore.setText(averageRating + "分");
    }

    @Override
    public void setDrivingSchool(String drivingSchool) {
        if (!TextUtils.isEmpty(drivingSchool)) {
            mLlyTrainSchool.setVisibility(View.VISIBLE);
            mTvTrainSchoolName.setText(drivingSchool);
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
            R.id.iv_follow,
            R.id.tv_applaud_count,
            R.id.rly_training_field,
            R.id.tv_free_try,
            R.id.lly_platform_assurance,
            R.id.fly_sms_coach,
            R.id.fly_online_ask,
            R.id.fly_call_coach,
            R.id.lly_train_school
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
            case R.id.tv_applaud_count:
                mPresenter.applaud();
                break;
            case R.id.rly_training_field:
                mPresenter.clickTrainFieldCount();
                Field field = mPresenter.getTrainingField();
                if (field != null) {
                    intent = new Intent(getContext(), FieldFilterActivity.class);
                    ArrayList<Field> highlightFields = new ArrayList<>();
                    highlightFields.add(field);
                    intent.putParcelableArrayListExtra("hightlightFields", highlightFields);
                    intent.putExtra("field", field);
                    startActivity(intent);
                }
                break;
            case R.id.tv_free_try:
                mPresenter.freeTry();
                GetUserIdentityDialog dialog = new GetUserIdentityDialog(getContext(), "看过训练场才放心！",
                        "输入手机号，教练立即带你看场地", "预约看场地", new GetUserIdentityDialog.OnIdentityGetListener() {
                    @Override
                    public void getCellPhone(String cellPhone) {
                        mPresenter.getUserIdentity(cellPhone);
                    }
                });
                dialog.show();
                break;
            case R.id.lly_platform_assurance:
                mPresenter.clickPlatformAssurance();
                break;
            case R.id.fly_sms_coach:
                mPresenter.addDataTrack("coach_detail_page_text_tapped", getContext());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, RequestCode.PERMISSIONS_REQUEST_SEND_SMS_TO_COACH);
                } else {
                    sendSmsToCoach();
                }
                break;
            case R.id.fly_online_ask:
                mPresenter.addDataTrack("coach_detail_page_online_support_tapped", getContext());
                mPresenter.onlineAsk();
                break;
            case R.id.fly_call_coach:
                mPresenter.addDataTrack("coach_detail_page_phone_support_tapped", getContext());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH);
                } else {
                    callMyCoach();
                }
                break;
            case R.id.lly_train_school:
                Coach coach = mPresenter.getCoach();
                openWebView(WebViewUrl.WEB_URL_JIAXIAO + "/" + coach.driving_school_id);
                break;
            default:
                break;
        }
    }

    @Override
    public void showReviews(ReviewResponseList responseList) {
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
    public void navigateToPurchaseCoach(Coach coach, ClassType classType) {
        if (coach == null) return;
        Intent intent = new Intent(getContext(), PurchaseCoachActivity.class);
        intent.putExtra("coach", coach);
        intent.putExtra("classType", classType);
        startActivityForResult(intent, RequestCode.REQUEST_CODE_PURCHASE_COACH);
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
                mPresenter.addDataTrack("coach_detail_page_co-coach_tapped", getContext());
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
    public void navigationToPlatformAssurance(boolean isGolden, boolean isCashPledge) {
        Intent intent = new Intent(getContext(), PlatformAssuranceActivity.class);
        intent.putExtra("isGolden", isGolden);
        intent.putExtra("isCashPledge", isCashPledge);
        startActivity(intent);
    }

    @Override
    public void navigateToStudentRefer() {
        startActivity(new Intent(getContext(), StudentReferActivity.class));
    }

    @Override
    public void navigateToReferFriends() {
        startActivity(new Intent(getContext(), ReferFriendsActivity.class));
    }

    @Override
    public void setLicenseTab(boolean isShowTitleC1, boolean isShowTitleC2) {
        if (isShowTitleC1 && isShowTitleC2) {
            mTvC1Label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.selectLicenseType(Common.LICENSE_TYPE_C1);
                }
            });
            mTvC2Label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.selectLicenseType(Common.LICENSE_TYPE_C2);
                }
            });
        } else {
            mVwLicenseDivider.setVisibility(View.GONE);
            mFrlC2.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            mRlyC1.setLayoutParams(layoutParams);
        }
        mTvWhatIsC1.setText("?");
        mTvWhatIsC2.setText("?");
        mTvWhatIsC1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAlertSimpleDialog dialog = new BaseAlertSimpleDialog(getContext(), "什么是C1手动档？",
                        "C1为手动挡小型车驾照，取得了C1类驾驶证的人可以驾驶C2类车");
                dialog.show();
            }
        });
        mTvWhatIsC2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAlertSimpleDialog dialog = new BaseAlertSimpleDialog(getContext(), "什么是C2自动档？",
                        "C2为自动挡小型车驾照，取得了C2类驾驶证的人不可以驾驶C1类车。" +
                                "C2驾照培训费要稍贵于C1照。费用的差别主要是由于C2自动挡教练车数量比较少，使用过程中维修费用比较高所致。");
                dialog.show();
            }
        });
    }

    @Override
    public void addClassType(final ClassType classType) {
        int length20 = Utils.instence(this).dip2px(20);
        int length10 = Utils.instence(this).dip2px(10);
        int length2 = Utils.instence(this).dip2px(2);
        int length4 = Utils.instence(this).dip2px(4);

        View vwDivider = new View(this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.divider_width));
        viewParams.setMargins(length20, 0, 0, 0);
        vwDivider.setLayoutParams(viewParams);
        vwDivider.setBackgroundResource(R.color.haha_gray_divider);
        mLlyClassType.addView(vwDivider);

        RelativeLayout rlyClassType = new RelativeLayout(this);
        rlyClassType.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        rlyClassType.setPadding(length20, length20, length20, length20);

        TextView tvClassTypeName = new TextView(this);
        tvClassTypeName.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tvClassTypeName.setBackgroundResource(R.drawable.rect_bg_trans_bd_appcolor_ssm);
        tvClassTypeName.setPadding(length4, length2, length4, length2);
        tvClassTypeName.setText(classType.name);
        tvClassTypeName.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        tvClassTypeName.setTextSize(12);
        int tvClassTypeNameId = Utils.generateViewId();
        tvClassTypeName.setId(tvClassTypeNameId);
        rlyClassType.addView(tvClassTypeName);

        TextView tvPrice = new TextView(this);
        RelativeLayout.LayoutParams tvPriceParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvPriceParams.addRule(RelativeLayout.RIGHT_OF, tvClassTypeNameId);
        tvPriceParams.addRule(RelativeLayout.ALIGN_TOP, tvClassTypeNameId);
        tvPriceParams.setMargins(length10, length4, 0, 0);
        tvPrice.setLayoutParams(tvPriceParams);
        tvPrice.setText(Utils.getMoney(classType.price));
        tvPrice.setTextColor(ContextCompat.getColor(this, R.color.haha_orange));
        tvPrice.setTextSize(16);
        rlyClassType.addView(tvPrice);

        ImageView ivArrow = new ImageView(this);
        RelativeLayout.LayoutParams ivArrowParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ivArrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        ivArrowParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvClassTypeNameId);
        ivArrow.setLayoutParams(ivArrowParams);
        ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coachmsg_more_arrow));
        rlyClassType.addView(ivArrow);

        TextView tvClassTypeDesc = new TextView(this);
        RelativeLayout.LayoutParams tvClassTypeDescParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvClassTypeDescParams.addRule(RelativeLayout.BELOW, tvClassTypeNameId);
        tvClassTypeDescParams.setMargins(0, length10, 0, 0);
        tvClassTypeDesc.setLayoutParams(tvClassTypeDescParams);
        tvClassTypeDesc.setText(classType.desc);
        tvClassTypeDesc.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        int tvClassTypeDescId = Utils.generateViewId();
        tvClassTypeDesc.setId(tvClassTypeDescId);
        rlyClassType.addView(tvClassTypeDesc);

        TextView tvPurchase = new TextView(this);
        RelativeLayout.LayoutParams tvPurchaseParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvPurchaseParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvClassTypeDescId);
        tvPurchaseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        tvPurchase.setLayoutParams(tvPurchaseParams);
        tvPurchase.setBackgroundResource(R.drawable.rect_bg_orange_ssm);
        tvPurchase.setPadding(length10, length2, length10, length2);
        tvPurchase.setText("报名");
        tvPurchase.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
        int tvPurchaseId = Utils.generateViewId();
        tvPurchase.setId(tvPurchaseId);
        rlyClassType.addView(tvPurchase);

        TextView tvPrePay = new TextView(this);
        RelativeLayout.LayoutParams tvPrePayParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvPrePayParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvPurchaseId);
        tvPrePayParams.addRule(RelativeLayout.LEFT_OF, tvPurchaseId);
        tvPrePayParams.setMargins(0, 0, length4, 0);
        tvPrePay.setLayoutParams(tvPrePayParams);
        tvPrePay.setBackgroundResource(R.drawable.rect_bg_appcolor_ssm);
        tvPrePay.setPadding(length10, length2, length10, length2);
        tvPrePay.setText("预付100");
        tvPrePay.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
        rlyClassType.addView(tvPrePay);


        //点击整行查看班别介绍
        rlyClassType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addDataTrack("coach_detail_page_price_detail_tapped", getContext());
                Intent intent = new Intent(getContext(), ClassTypeIntroActivity.class);
                intent.putExtra("totalAmount", classType.price);
                intent.putExtra("isWuyouClass", mPresenter.getCoach().coach_group.group_type == Common.GROUP_TYPE_CHEYOU_WUYOU);
                intent.putExtra("classType", classType);
                if (mPresenter.isPurchasedService()) {
                    intent.putExtra("isShowPurchase", false);
                }
                startActivityForResult(intent, RequestCode.REQUEST_CODE_CLASS_TYPE_INTRO);
            }
        });
        //点击购买
        tvPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addDataTrack("coach_detail_page_purchase_tapped", getContext());
                mPresenter.purchaseCoach(classType);
            }
        });

        tvPrePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addDataTrack("coach_detail_page_deposit_tapped", getContext());
                startActivityForResult(new Intent(getContext(), PurchasePrepaidActivity.class),
                        RequestCode.REQUEST_CODE_PURCHASE_PREPAID);
            }
        });

        mLlyClassType.addView(rlyClassType);
    }

    @Override
    public void clearClassType() {
        mLlyClassType.removeAllViews();
    }

    @Override
    public void showC1Tab(boolean isLight) {
        mTvC1Label.setTextColor(ContextCompat.getColor(this, isLight ? R.color.app_theme_color : R.color.haha_gray_text));
    }

    @Override
    public void showC2Tab(boolean isLight) {
        mTvC2Label.setTextColor(ContextCompat.getColor(this, isLight ? R.color.app_theme_color : R.color.haha_gray_text));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_PURCHASE_COACH) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getContext(), PaySuccessActivity.class);
                if (data != null && data.getBooleanExtra("isOnlyPurchaseCoach", false)) {
                    intent.putExtra("isPurchasedInsurance", false);
                } else {
                    intent.putExtra("isPurchasedInsurance", true);
                }
                intent.putExtra("isFromPurchaseInsurance", false);
                startActivityForResult(intent, RequestCode.REQUEST_CODE_PAY_SUCCESS);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PAY_SUCCESS) {
            boolean isPurchasedInsurance = data != null && data.getBooleanExtra("isPurchasedInsurance", false);
            Intent intent = new Intent(getContext(), UploadIdCardActivity.class);
            intent.putExtra("isFromPaySuccess", true);
            intent.putExtra("isInsurance", isPurchasedInsurance);
            startActivityForResult(intent, RequestCode.REQUEST_CODE_UPLOAD_ID_CARD);
        } else if (requestCode == RequestCode.REQUEST_CODE_UPLOAD_ID_CARD) {
            mPresenter.toReferFriends();
        } else if (requestCode == RequestCode.REQUEST_CODE_CLASS_TYPE_INTRO) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (data.getBooleanExtra("prepay", false)) {
                        startActivityForResult(new Intent(getContext(), PurchasePrepaidActivity.class),
                                RequestCode.REQUEST_CODE_PURCHASE_PREPAID);
                    } else {
                        mPresenter.purchaseCoach((ClassType) data.getParcelableExtra("classType"));
                    }
                }
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PURCHASE_PREPAID) {
            if (resultCode == Activity.RESULT_OK) {
                startActivityForResult(new Intent(getContext(), PrepaySuccessActivity.class),
                        RequestCode.REQUEST_CODE_PREPAY_SUCCESS);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_PREPAY_SUCCESS) {
            startActivity(new Intent(getContext(), MyVoucherActivity.class));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("coach", mPresenter.getCoach());
        setResult(RESULT_OK, intent);
        super.finish();
    }

    private void contactService() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    /**
     * 联系教练
     */
    private void callMyCoach() {
        Coach coach = mPresenter.getCoach();
        if (TextUtils.isEmpty(coach.consult_phone))
            return;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + coach.consult_phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_SEND_SMS_FOR_SHARE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                shareToSms(mShareSmsUrl);
            } else {
                showMessage("请允许发送短信权限，不然无法分享到短信");
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                callMyCoach();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系教练");
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CUSTOMER_SERVICE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactService();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        } else if (requestCode == RequestCode.PERMISSIONS_REQUEST_SEND_SMS_TO_COACH) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                sendSmsToCoach();
            } else {
                showMessage("请允许发送短信权限，不然给教练发短信");
            }
        }
    }

    private void shareToSms(String shareUrl) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", mTitle + mDescription + shareUrl);
        startActivity(intent);
    }

    private void sendSmsToCoach() {
        Coach coach = mPresenter.getCoach();
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + coach.consult_phone));
        intent.putExtra("sms_body", coach.name + "教练，我在哈哈学车看到您的招生信息，我想详细了解一下。");
        startActivity(intent);
    }
}
