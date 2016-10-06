package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.findCoach.CoachDetailPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.findCoach.CoachDetailView;
import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/10/5.
 */

public class CoachDetailActivity extends HHBaseActivity implements CoachDetailView {
    private CoachDetailPresenter mPresenter;
    private ImageView mIvBack;
    private TextView mTvTitle;
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
    @BindView(R.id.tv_normal_price)
    TextView mTvNormalPrice;
    @BindView(R.id.tv_old_normal_price)
    TextView mTvOldNormalPrice;
    @BindView(R.id.tv_vip_price)
    TextView mTvVipPrice;
    @BindView(R.id.tv_old_vip_price)
    TextView mTvOldVipPrice;
    @BindView(R.id.rly_vip_price)
    RelativeLayout mRlyVipPrice;
    @BindView(R.id.tv_train_location)
    TextView mTvTrainLocation;
    @BindView(R.id.tv_license_type)
    TextView mTvLicenseType;
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
        actionBar.setCustomView(R.layout.actionbar_base);
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
        //价格
        mTvNormalPrice.setText(Utils.getMoney(coach.coach_group.training_cost));
        mTvOldNormalPrice.setText("门市价：" + Utils.getMoney(coach.coach_group.market_price));
        mTvOldNormalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        if (coach.vip == 0) {
            mRlyVipPrice.setVisibility(View.GONE);
        } else {
            mRlyVipPrice.setVisibility(View.VISIBLE);
            mTvVipPrice.setText(Utils.getMoney(coach.coach_group.vip_price));
            mTvOldVipPrice.setText("门市价：" + Utils.getMoney(coach.coach_group.vip_market_price));
            mTvOldVipPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        mTvTrainLocation.setText(mPresenter.getTrainingFieldName());
        ArrayList<Coach> peerCoaches = coach.peer_coaches;
        if (peerCoaches != null && peerCoaches.size() > 0) {
            for (Coach peerCoach : peerCoaches) {
                mLlyPeerCoaches.addView(getPeerCoachAdapter(peerCoach));
            }
        } else {
            mLlyPeerCoaches.setVisibility(View.GONE);
        }

        if (coach.license_type.equals("1")) {
            mTvLicenseType.setText("C1手动档");
        } else if (coach.license_type.equals("2")) {
            mTvLicenseType.setText("C2自动档");
        } else {
            mTvLicenseType.setText("C1手动档，C2自动挡");
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
}
