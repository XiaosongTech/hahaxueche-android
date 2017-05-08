package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.presenter.findCoach.DrivingSchoolDetailPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.findCoach.DrivingSchoolDetailView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2017/5/8.
 */

public class DrivingSchoolDetailDetailActivity extends HHBaseActivity implements DrivingSchoolDetailView {
    private DrivingSchoolDetailPresenter mPresenter;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    @BindView(R.id.vw_click_more_comments)
    View mVwClickMoreComments;
    @BindView(R.id.tv_click_more_comments)
    TextView mTvClickMoreComments;
    @BindView(R.id.lly_comments)
    LinearLayout mLlyReviews;
    @BindView(R.id.iv_image)
    ImageView mIvImage;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_consultant_count)
    TextView mTvConsultantCount;
    @BindView(R.id.tv_lowest_price)
    TextView mTvLowestPrice;
    @BindView(R.id.tv_field_count)
    TextView mTvFieldCount;
    @BindView(R.id.tv_pass_rate)
    TextView mTvPassRate;
    @BindView(R.id.tv_satisfaction_rate)
    TextView mTvSatisfactionRate;
    @BindView(R.id.tv_coach_count)
    TextView mTvCoachCount;
    @BindView(R.id.tv_bio)
    TextView mTvBio;
    @BindView(R.id.lly_classes)
    LinearLayout mLlyClasses;
    @BindView(R.id.tv_group_buy_count)
    TextView mTvGroupBuyCount;
    @BindView(R.id.lly_fields)
    LinearLayout mLlyFields;
    @BindView(R.id.iv_bell)
    SimpleDraweeView mIvBell;
    @BindView(R.id.lly_details)
    LinearLayout mLlyDetails;
    @BindView(R.id.tv_more_bio)
    TextView mTvMoreBio;

    private int mReviewStartLine = 2;
    private int mClassStartLine = 2;
    private int mFieldStartLine = 2;

    private boolean mIsBioExpand = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new DrivingSchoolDetailPresenter();
        setContentView(R.layout.activity_driving_school_detail);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        Intent intent = getIntent();
        if (intent.getIntExtra("drivingSchoolId", -1) > 0) {
            mPresenter.getDrivingSchoolDetail(intent.getIntExtra("drivingSchoolId", -1));
        }
        Uri uri = Uri.parse("res://com.hahaxueche)/" + R.drawable.ic_bell);
        DraweeController draweeController =
                Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                        .build();
        mIvBell.setController(draweeController);
        GenericDraweeHierarchy hierarchy = mIvBell.getHierarchy();
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        mLlyDetails.addView(getHotDrivingSchoolView(), mLlyDetails.getChildCount());
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base_share);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("驾校详情");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrivingSchoolDetailDetailActivity.this.finish();
            }
        });
        ImageView mIvShare = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_share);
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.tv_near_fields,
            R.id.tv_more_fields,
            R.id.tv_click_more_fields,
            R.id.tv_more_bio})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_near_fields:
                mPresenter.clickToFields(null);
                break;
            case R.id.tv_more_fields:
                mPresenter.clickToFields(null);
                break;
            case R.id.tv_click_more_fields:
                mPresenter.clickToFields(null);
                break;
            case R.id.tv_more_bio:
                clickMoreBio();
                break;
            default:
                break;
        }
    }

    private void clickMoreBio() {
        if (mIsBioExpand) {
            mTvBio.setMaxLines(Integer.MAX_VALUE);
            mTvMoreBio.setText("收起");
        } else {
            mTvBio.setMaxLines(2);
            mTvMoreBio.setText("更多");
        }
        mIsBioExpand = !mIsBioExpand;
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showNoReview() {
        mVwClickMoreComments.setVisibility(View.GONE);
        mTvClickMoreComments.setText("该驾校目前还没有评价");
        mTvClickMoreComments.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        mTvClickMoreComments.setClickable(false);

    }

    @Override
    public void addReview(Review review) {
        mLlyReviews.addView(getReviewAdapter(review, mReviewStartLine == 2), mReviewStartLine++);
    }

    @Override
    public void setImage(Uri uri) {
        mIvImage.setImageURI(uri);
    }

    @Override
    public void setName(String drivingSchoolName) {
        mTvName.setText(drivingSchoolName);
    }

    @Override
    public void setConsultantCount(SpannableString text) {
        mTvConsultantCount.setText(text);
    }

    @Override
    public void setLowestPrice(SpannableString text) {
        mTvLowestPrice.setText(text);
    }

    @Override
    public void setFieldCount(SpannableString text) {
        mTvFieldCount.setText(text);
    }

    @Override
    public void setPassRate(SpannableString text) {
        mTvPassRate.setText(text);
    }

    @Override
    public void setSatisfactionRate(SpannableString text) {
        mTvSatisfactionRate.setText(text);
    }

    @Override
    public void setCoachCount(SpannableString text) {
        mTvCoachCount.setText(text);
    }

    @Override
    public void setBio(String bio) {
        mTvBio.setText(bio);
    }

    @Override
    public void addClassType(ClassType classType) {
        mLlyClasses.addView(getClassTypeAdapter(classType, mClassStartLine == 2), mClassStartLine++);
    }

    @Override
    public void setGroupBuyCount(SpannableString text) {
        mTvGroupBuyCount.setText(text);
    }

    @Override
    public void addFieldView(Field field) {
        mLlyFields.addView(getFieldAdapter(field, mFieldStartLine == 2), mFieldStartLine++);
    }

    @Override
    public void navigateToFieldFilter(List<Field> highlightFields, Field selectField) {
        Intent intent = new Intent(getContext(), FieldFilterActivity.class);
        intent.putParcelableArrayListExtra("hightlightFields", (ArrayList<? extends Parcelable>) highlightFields);
        intent.putExtra("field", selectField);
        startActivity(intent);
    }

    private RelativeLayout getReviewAdapter(Review review, boolean isFirstLine) {
        RelativeLayout rly = new RelativeLayout(this);
        LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rly.setLayoutParams(rlyParams);

        if (!isFirstLine) {
            View divider = new View(this);
            RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
            dividerParams.setMargins(Utils.instence(this).dip2px(20), 0, 0, 0);
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            rly.addView(divider);
        }

        SimpleDraweeView ivAvatar = new SimpleDraweeView(this);
        GenericDraweeHierarchy hierarchy = ivAvatar.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.ic_mypage_ava);
        hierarchy.setRoundingParams(new RoundingParams().setRoundAsCircle(true));
        RelativeLayout.LayoutParams ivAvatarParams = new RelativeLayout.LayoutParams(Utils.instence(this).dip2px(40), Utils.instence(this).dip2px(40));
        ivAvatarParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0, Utils.instence(this).dip2px(15));
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
        tvCommentParams.setMargins(0, Utils.instence(this).dip2px(10), Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15));
        tvComment.setLayoutParams(tvCommentParams);
        tvComment.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        tvComment.setTextSize(12);
        tvComment.setText(review.comment);
        tvComment.setLineSpacing(0, 1.2f);
        tvComment.setMaxLines(2);
        tvComment.setEllipsize(TextUtils.TruncateAt.END);
        int tvCommentId = Utils.generateViewId();
        tvComment.setId(tvCommentId);
        rly.addView(tvComment);

        return rly;
    }

    private View getClassTypeAdapter(final ClassType classType, boolean isFirstLine) {
        int length20 = Utils.instence(this).dip2px(20);
        int length10 = Utils.instence(this).dip2px(10);
        int length2 = Utils.instence(this).dip2px(2);
        int length4 = Utils.instence(this).dip2px(4);
        RelativeLayout rly = new RelativeLayout(this);
        LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rly.setLayoutParams(rlyParams);

        if (!isFirstLine) {
            View divider = new View(this);
            RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
            dividerParams.setMargins(length20, 0, 0, 0);
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            rly.addView(divider);
        }

        TextView tvClassTypeName = new TextView(this);
        RelativeLayout.LayoutParams tvClassTypeNameParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvClassTypeNameParam.setMargins(length20, length20, 0, length20);
        tvClassTypeName.setLayoutParams(tvClassTypeNameParam);
        tvClassTypeName.setBackgroundResource(R.drawable.rect_bg_trans_bd_appcolor_ssm);
        tvClassTypeName.setPadding(length4, length2, length4, length2);
        tvClassTypeName.setText(classType.name);
        tvClassTypeName.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
        tvClassTypeName.setTextSize(12);
        int tvClassTypeNameId = Utils.generateViewId();
        tvClassTypeName.setId(tvClassTypeNameId);
        rly.addView(tvClassTypeName);

        TextView tvClassTypeDesc = new TextView(this);
        RelativeLayout.LayoutParams tvClassTypeDescParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvClassTypeDescParams.addRule(RelativeLayout.RIGHT_OF, tvClassTypeNameId);
        tvClassTypeDescParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        tvClassTypeDescParams.setMargins(length10, 0, 0, 0);
        tvClassTypeDesc.setLayoutParams(tvClassTypeDescParams);
        tvClassTypeDesc.setText(classType.desc);
        tvClassTypeDesc.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        int tvClassTypeDescId = Utils.generateViewId();
        tvClassTypeDesc.setId(tvClassTypeDescId);
        rly.addView(tvClassTypeDesc);

        ImageView ivArrow = new ImageView(this);
        RelativeLayout.LayoutParams ivArrowParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ivArrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        ivArrowParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        ivArrowParams.setMargins(0, 0, length20, 0);
        ivArrow.setLayoutParams(ivArrowParams);
        ivArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_coachmsg_more_arrow));
        int ivArrowId = Utils.generateViewId();
        ivArrow.setId(ivArrowId);
        rly.addView(ivArrow);

        TextView tvPrice = new TextView(this);
        RelativeLayout.LayoutParams tvPriceParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvPriceParams.addRule(RelativeLayout.LEFT_OF, ivArrowId);
        tvPriceParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        tvPriceParams.setMargins(0, length10, length10, 0);
        tvPrice.setLayoutParams(tvPriceParams);
        tvPrice.setText(Utils.getMoney(classType.price));
        tvPrice.setTextColor(ContextCompat.getColor(this, R.color.haha_orange));
        tvPrice.setTextSize(16);
        rly.addView(tvPrice);

        rly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ClassTypeIntroActivity.class);
                intent.putExtra("totalAmount", classType.price);
                intent.putExtra("isWuyouClass", false);
                intent.putExtra("classType", classType);
                intent.putExtra("isShowPurchase", false);
                startActivity(intent);
            }
        });

        return rly;
    }

    private View getFieldAdapter(final Field field, boolean isFirstLine) {
        int length3 = Utils.instence(this).dip2px(3);
        int length5 = Utils.instence(this).dip2px(5);
        int length10 = Utils.instence(this).dip2px(10);
        int length15 = Utils.instence(this).dip2px(15);
        int avatarLength = Utils.instence(this).dip2px(60);

        RelativeLayout rly = new RelativeLayout(this);
        rly.setPadding(length10, 0, length10, 0);
        LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rly.setLayoutParams(rlyParams);

        if (!isFirstLine) {
            View divider = new View(this);
            RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
            divider.setLayoutParams(dividerParams);
            divider.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            rly.addView(divider);
        }

        SimpleDraweeView ivAvatar = new SimpleDraweeView(this);
        RelativeLayout.LayoutParams ivAvatarParams = new RelativeLayout.LayoutParams(avatarLength, avatarLength);
        ivAvatarParams.setMargins(0, length15, 0, length15);
        ivAvatar.setLayoutParams(ivAvatarParams);
        ivAvatar.setImageURI(field.image);
        int ivAvatarId = Utils.generateViewId();
        ivAvatar.setId(ivAvatarId);
        rly.addView(ivAvatar);

        TextView tvName = new TextView(this);
        RelativeLayout.LayoutParams tvNameParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvNameParam.addRule(RelativeLayout.RIGHT_OF, ivAvatarId);
        tvNameParam.addRule(RelativeLayout.ALIGN_TOP, ivAvatarId);
        tvNameParam.setMargins(length5, length5, 0, 0);
        tvName.setLayoutParams(tvNameParam);
        tvName.setText(field.name);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
        int tvNameId = Utils.generateViewId();
        tvName.setId(tvNameId);
        rly.addView(tvName);

        TextView tvToField = new TextView(this);
        RelativeLayout.LayoutParams tvToFieldParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvToFieldParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        tvToFieldParam.addRule(RelativeLayout.ALIGN_TOP, ivAvatarId);
        tvToField.setLayoutParams(tvToFieldParam);
        tvToField.setText("去现场看看 >");
        tvToField.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
        tvToField.setBackgroundResource(R.drawable.rect_bg_yellow_gradient);
        tvToField.setPadding(length3, length5, length3, length5);
        tvToField.setTextSize(12);
        int tvToFieldId = Utils.generateViewId();
        tvToField.setId(tvToFieldId);
        rly.addView(tvToField);

        TextView tvLocation = new TextView(this);
        RelativeLayout.LayoutParams tvLocationParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLocationParam.addRule(RelativeLayout.BELOW, tvToFieldId);
        tvLocationParam.addRule(RelativeLayout.ALIGN_LEFT, tvNameId);
        tvLocationParam.setMargins(0, length5, 0, 0);
        tvLocation.setLayoutParams(tvLocationParam);
        tvLocation.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_list_local_btn),
                null, null, null);
        tvLocation.setCompoundDrawablePadding(length3);
        tvLocation.setMaxLines(1);
        tvLocation.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        tvLocation.setTextSize(12);
        tvLocation.setText(field.zone + " | " + field.display_address);
        rly.addView(tvLocation);

        rly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.clickToFields(field);
            }
        });

        return rly;
    }

    private LinearLayout getHotDrivingSchoolView() {
        int margin5dp = Utils.instence(this).dip2px(5);
        int margin8dp = Utils.instence(this).dip2px(8);
        int margin10dp = Utils.instence(this).dip2px(10);
        int margin15dp = Utils.instence(this).dip2px(15);
        int margin20dp = Utils.instence(this).dip2px(20);
        int padding3dp = Utils.instence(this).dip2px(3);

        LinearLayout llyHotDrivingSchool = new LinearLayout(this);
        LinearLayout.LayoutParams llyHotDrivingSchoolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llyHotDrivingSchoolParam.setMargins(0, margin10dp, 0, margin10dp);
        llyHotDrivingSchool.setLayoutParams(llyHotDrivingSchoolParam);
        llyHotDrivingSchool.setBackgroundResource(R.color.haha_white);
        llyHotDrivingSchool.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout rlyHotSearch = new RelativeLayout(this);
        rlyHotSearch.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tvHotSearch = new TextView(this);
        RelativeLayout.LayoutParams tvHotSearchParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvHotSearchParam.setMargins(margin20dp, margin15dp, 0, margin15dp);
        tvHotSearch.setLayoutParams(tvHotSearchParam);
        tvHotSearch.setText("大家都在搜");
        tvHotSearch.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
        tvHotSearch.setTextSize(16);
        int tvHotSearchId = Utils.generateViewId();
        tvHotSearch.setId(tvHotSearchId);
        rlyHotSearch.addView(tvHotSearch);
        TextView tvHot = new TextView(this);
        RelativeLayout.LayoutParams tvHotParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvHotParam.addRule(RelativeLayout.RIGHT_OF, tvHotSearchId);
        tvHotParam.setMargins(margin5dp, margin10dp, 0, 0);
        tvHot.setLayoutParams(tvHotParam);
        tvHot.setText("hot!");
        tvHot.setTextColor(ContextCompat.getColor(this, R.color.haha_red));
        rlyHotSearch.addView(tvHot);
        llyHotDrivingSchool.addView(rlyHotSearch);

        View vwDivider = new View(this);
        vwDivider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                this.getResources().getDimensionPixelSize(R.dimen.divider_width)));
        vwDivider.setBackgroundResource(R.color.haha_gray_divider);
        llyHotDrivingSchool.addView(vwDivider);

        TableLayout tbDrivingSchool = new TableLayout(this);
        LinearLayout.LayoutParams tbDrivingSchoolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tbDrivingSchoolParam.setMargins(0, 0, 0, margin15dp);
        tbDrivingSchool.setLayoutParams(tbDrivingSchoolParam);
        tbDrivingSchool.setStretchAllColumns(true);

        int maxColCount = 4;
        List<DrivingSchool> hotDrivingSchoolList = mPresenter.getHotDrivingSchools(this);
        for (int row = 0; row < hotDrivingSchoolList.size() / maxColCount; row++) {
            TableRow tr = new TableRow(this);
            TableLayout.LayoutParams trParam = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            trParam.setMargins(0, margin10dp, 0, 0);
            tr.setLayoutParams(trParam);
            for (int col = 0; col < maxColCount; col++) {
                if (row * maxColCount + col > hotDrivingSchoolList.size() - 1) {
                    break;
                }
                final DrivingSchool drivingSchool = hotDrivingSchoolList.get(row * maxColCount + col);
                TextView tvDrivingSchool = new TextView(this);
                TableRow.LayoutParams tvDrivingSchoolParam = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                tvDrivingSchoolParam.setMargins(margin8dp, 0, margin8dp, 0);
                tvDrivingSchool.setLayoutParams(tvDrivingSchoolParam);
                tvDrivingSchool.setBackgroundResource(R.drawable.rect_bg_gray_bd_gray_corner);
                tvDrivingSchool.setGravity(Gravity.CENTER);
                tvDrivingSchool.setPadding(0, padding3dp, 0, padding3dp);
                tvDrivingSchool.setText(drivingSchool.name);
                tvDrivingSchool.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
                tvDrivingSchool.setTextSize(12);
                tvDrivingSchool.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HHLog.v("click " + drivingSchool.name);
                    }
                });
                tr.addView(tvDrivingSchool);
            }
            tbDrivingSchool.addView(tr);
        }
        llyHotDrivingSchool.addView(tbDrivingSchool);
        return llyHotDrivingSchool;
    }
}
