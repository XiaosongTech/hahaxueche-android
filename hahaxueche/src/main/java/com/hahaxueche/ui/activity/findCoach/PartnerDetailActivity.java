package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.Partner;
import com.hahaxueche.model.user.coach.ProductType;
import com.hahaxueche.presenter.findCoach.PartnerDetailPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.findCoach.PartnerDetailView;
import com.hahaxueche.ui.widget.imageSwitcher.ImageSwitcher;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/10/20.
 */

public class PartnerDetailActivity extends HHBaseActivity implements PartnerDetailView {
    private PartnerDetailPresenter mPresenter;
    private ImageView mIvBack;
    private TextView mTvTitle;
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
                PartnerDetailActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showPartnerDetail(Partner partner) {
        mTvName.setText(partner.name);
        mTvDescription.setText(partner.bio);
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

    @Override
    public void addPrices(ArrayList<ProductType> productTypes) {
        if (productTypes == null || productTypes.size() < 1) return;
        for (ProductType productType : productTypes) {
            mLlyPrices.addView(getPriceAdapter(productType), 1 + productTypes.indexOf(productType));
        }

    }

    @OnClick(R.id.tv_applaud_count)
    public void applaud() {
        mPresenter.applaud();
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

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("partner", mPresenter.getPartner());
        setResult(RESULT_OK, intent);
        super.finish();
    }
}
