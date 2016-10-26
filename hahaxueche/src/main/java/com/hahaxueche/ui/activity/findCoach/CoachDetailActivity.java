package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
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
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.responseList.ReviewResponseList;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.model.user.coach.ProductType;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.presenter.findCoach.CoachDetailPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.myPage.ReferFriendsActivity;
import com.hahaxueche.ui.view.findCoach.CoachDetailView;
import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/10/5.
 */

public class CoachDetailActivity extends HHBaseActivity implements CoachDetailView {
    private CoachDetailPresenter mPresenter;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private ImageView mIvShare;
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
    private ReviewResponseList mReviewResponse;

    private static final int REQUEST_CODE_PURCHASE_COACH = 1;
    private static final int REQUEST_CODE_PAY_SUCCESS = 2;

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

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base_share);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("教练详情");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoachDetailActivity.this.finish();
            }
        });
        mIvShare = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_share);
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
            R.id.rly_training_field
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_follow:
                mPresenter.follow();
                break;
            case R.id.fly_more_comments:
                Intent intent = new Intent(getContext(), ReviewListActivity.class);
                intent.putExtra("coach", mPresenter.getCoach());
                startActivity(intent);
            case R.id.rly_price:
                intent = new Intent(getContext(), PriceActivity.class);
                intent.putExtra("coach", mPresenter.getCoach());
                startActivity(intent);
                break;
            case R.id.tv_applaud_count:
                mPresenter.applaud();
                break;
            case R.id.tv_pay:
                mPresenter.purchaseCoach();
                break;
            case R.id.rly_training_field:
                Field field = mPresenter.getTrainingField();
                if (field != null) {
                    intent = new Intent(getContext(), FieldMapActivity.class);
                    intent.putExtra("field", field);
                    startActivity(intent);
                }
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
    public void addPrices(ArrayList<ProductType> productTypes) {
        if (productTypes == null || productTypes.size() < 1) return;
        for (ProductType productType : productTypes) {
            mLlyPrices.addView(getPriceAdapter(productType), 1 + productTypes.indexOf(productType));
        }

    }

    @Override
    public void navigateToPurchaseCoach(Coach coach) {
        if (coach == null) return;
        Intent intent = new Intent(getContext(), PurchaseCoachActivity.class);
        intent.putExtra("coach", coach);
        startActivityForResult(intent, REQUEST_CODE_PURCHASE_COACH);
    }

    private RelativeLayout getPriceAdapter(ProductType productType) {
        RelativeLayout rly = new RelativeLayout(this);
        LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rly.setLayoutParams(rlyParams);

        View view = new View(this);
        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
        view.setLayoutParams(viewParams);
        view.setBackgroundResource(R.color.haha_gray_divider);
        rly.addView(view);

        TextView tvName = new TextView(this);
        RelativeLayout.LayoutParams tvNameParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvNameParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0, 0);
        tvName.setLayoutParams(tvNameParams);
        tvName.setPadding(Utils.instence(this).dip2px(2), Utils.instence(this).dip2px(1), Utils.instence(this).dip2px(2), Utils.instence(this).dip2px(1));
        tvName.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
        tvName.setTextSize(12);
        tvName.setBackgroundResource(productType.nameBackground);
        tvName.setText(productType.name);
        int tvNameId = Utils.generateViewId();
        tvName.setId(tvNameId);
        rly.addView(tvName);

        TextView tvLabel = new TextView(this);
        RelativeLayout.LayoutParams tvLabelParams = new RelativeLayout.LayoutParams(Utils.instence(this).dip2px(32), RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvLabelParams.setMargins(Utils.instence(this).dip2px(6), 0, 0, 0);
        tvLabelParams.addRule(RelativeLayout.ALIGN_TOP, tvNameId);
        tvLabelParams.addRule(RelativeLayout.RIGHT_OF, tvNameId);
        tvLabel.setLayoutParams(tvLabelParams);
        tvLabel.setPadding(Utils.instence(this).dip2px(1), Utils.instence(this).dip2px(1), Utils.instence(this).dip2px(1), Utils.instence(this).dip2px(1));
        tvLabel.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
        tvLabel.setTextSize(12);
        tvLabel.setBackgroundResource(R.drawable.rect_bg_appcolor_ssm);
        tvLabel.setGravity(Gravity.CENTER);
        tvLabel.setText(productType.label);
        rly.addView(tvLabel);

        TextView tvPrice = new TextView(this);
        RelativeLayout.LayoutParams tvPriceParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvPriceParams.setMargins(0, 0, Utils.instence(this).dip2px(20), 0);
        tvPriceParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        tvPriceParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        tvPrice.setLayoutParams(tvPriceParams);
        tvPrice.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        tvPrice.setTextSize(18);
        tvPrice.setText(Utils.getMoney(productType.price));
        rly.addView(tvPrice);

        TextView tvRemark = new TextView(this);
        RelativeLayout.LayoutParams tvRemarkParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvRemarkParams.setMargins(0, Utils.instence(this).dip2px(10), 0, Utils.instence(this).dip2px(15));
        tvRemarkParams.addRule(RelativeLayout.ALIGN_LEFT, tvNameId);
        tvRemarkParams.addRule(RelativeLayout.BELOW, tvNameId);
        tvRemark.setLayoutParams(tvRemarkParams);
        tvRemark.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        tvRemark.setTextSize(16);
        tvRemark.setText(productType.remark);
        rly.addView(tvRemark);

        return rly;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PURCHASE_COACH) {
            if (resultCode == Activity.RESULT_OK) {
                startActivityForResult(new Intent(getContext(), PaySuccessActivity.class), REQUEST_CODE_PAY_SUCCESS);
            }
        } else if (requestCode == REQUEST_CODE_PAY_SUCCESS) {
            startActivity(new Intent(getContext(), ReferFriendsActivity.class));
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
}
