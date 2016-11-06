package com.hahaxueche.ui.activity.findCoach;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.FixedCostItem;
import com.hahaxueche.model.payment.OtherFee;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.findCoach.PricePresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.findCoach.PriceView;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/25.
 */

public class PriceActivity extends HHBaseActivity implements PriceView {
    private PricePresenter mPresenter;
    private ImageView mIvBack;
    private TextView mTvTitle;
    @BindView(R.id.table_fee)
    TableLayout mTableFee;
    @BindView(R.id.lly_other_fees)
    LinearLayout mLlyOtherFees;
    @BindView(R.id.tv_train_fee_c1_normal)
    TextView mTvTrainFeeC1Normal;
    @BindView(R.id.tv_train_fee_c1_vip)
    TextView mTvTrainFeeC1VIP;
    @BindView(R.id.tv_train_fee_c2_normal)
    TextView mTvTrainFeeC2Normal;
    @BindView(R.id.tv_train_fee_c2_vip)
    TextView mTvTrainFeeC2VIP;
    @BindView(R.id.tv_total_fee_c1_normal)
    TextView mTvTotalFeeC1Normal;
    @BindView(R.id.tv_total_fee_c1_vip)
    TextView mTvTotalFeeC1VIP;
    @BindView(R.id.tv_total_fee_c2_normal)
    TextView mTvTotalFeeC2Normal;
    @BindView(R.id.tv_total_fee_c2_vip)
    TextView mTvTotalFeeC2VIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PricePresenter();
        setContentView(R.layout.activity_price);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        Intent intent = getIntent();
        if (intent.getParcelableExtra("coach") != null) {
            mPresenter.showPrice((Coach) intent.getParcelableExtra("coach"));
        }
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("拿证价格");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PriceActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setFixedFees(ArrayList<FixedCostItem> fixedFees) {
        if (fixedFees == null || fixedFees.size() < 1) return;
        for (FixedCostItem fixedFee : fixedFees) {
            TableRow tr = new TableRow(this);
            TextView tvName = new TextView(this);
            TableRow.LayoutParams tvNameParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            int margin = Utils.instence(this).dip2px(0.5f);
            tvNameParam.setMargins(margin, margin, margin, margin);
            tvName.setLayoutParams(tvNameParam);
            tvName.setBackgroundResource(R.color.haha_white);
            tvName.setGravity(Gravity.CENTER);
            int padding = Utils.instence(this).dip2px(3);
            tvName.setPadding(padding, padding, padding, padding);
            tvName.setText(fixedFee.name);
            tvName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_14sp));
            tr.addView(tvName);

            TextView tvCost = new TextView(this);
            TableRow.LayoutParams tvCostParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            tvCostParam.setMargins(margin, margin, margin, margin);
            tvCostParam.span = 4;
            tvCost.setLayoutParams(tvCostParam);
            tvCost.setBackgroundResource(R.color.haha_white);
            tvCost.setGravity(Gravity.CENTER);
            tvCost.setPadding(padding, padding, padding, padding);
            tvCost.setText(Utils.getMoney(fixedFee.cost));
            tvCost.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
            tvCost.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_14sp));
            tr.addView(tvCost);

            mTableFee.addView(tr, fixedFees.indexOf(fixedFee) + 1);
        }
    }

    @Override
    public void setOtherFees(ArrayList<OtherFee> otherFees) {
        if (otherFees == null || otherFees.size() < 1) return;
        int insertLine = 0;
        for (OtherFee otherFee : otherFees) {
            FrameLayout frTitle = new FrameLayout(this);
            LinearLayout.LayoutParams frTitleParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            frTitleParam.setMargins(0, Utils.instence(this).dip2px(10), 0, 0);
            frTitle.setLayoutParams(frTitleParam);

            FrameLayout frIcon = new FrameLayout(this);
            FrameLayout.LayoutParams frIconParam = new FrameLayout.LayoutParams(Utils.instence(this).dip2px(5), Utils.instence(this).dip2px(18));
            frIconParam.gravity = Gravity.CENTER_VERTICAL;
            frIcon.setLayoutParams(frIconParam);
            frIcon.setBackgroundResource(R.drawable.rect_bg_appcolor);
            frTitle.addView(frIcon);

            TextView tvFeeName = new TextView(this);
            FrameLayout.LayoutParams tvFeeNameParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            tvFeeNameParam.gravity = Gravity.CENTER_VERTICAL;
            tvFeeNameParam.setMargins(Utils.instence(this).dip2px(10), 0, 0, 0);
            tvFeeName.setLayoutParams(tvFeeNameParam);
            tvFeeName.setText(otherFee.name);
            tvFeeName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
            frTitle.addView(tvFeeName);

            mLlyOtherFees.addView(frTitle, insertLine++);

            TextView tvDescription = new TextView(this);
            LinearLayout.LayoutParams tvDescriptionParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tvDescriptionParam.setMargins(0, Utils.instence(this).dip2px(5), 0, 0);
            tvDescription.setLayoutParams(tvDescriptionParam);
            tvDescription.setText(otherFee.description.replace("\\n", "\n"));
            HHLog.v(otherFee.description);
            tvDescription.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
            tvDescription.setLineSpacing(0, 1.2f);
            mLlyOtherFees.addView(tvDescription, insertLine++);
        }
    }

    @Override
    public void setTrainFeeC1Normal(String fee) {
        mTvTrainFeeC1Normal.setText(fee);
    }

    @Override
    public void setTrainFeeC1VIP(String fee) {
        mTvTrainFeeC1VIP.setText(fee);
    }

    @Override
    public void setTrainFeeC2Normal(String fee) {
        mTvTrainFeeC2Normal.setText(fee);
    }

    @Override
    public void setTrainFeeC2VIP(String fee) {
        mTvTrainFeeC2VIP.setText(fee);
    }

    @Override
    public void setTotalFeeC1Normal(String fee) {
        mTvTotalFeeC1Normal.setText(fee);
    }

    @Override
    public void setTotalFeeC1VIP(String fee) {
        mTvTotalFeeC1VIP.setText(fee);
    }

    @Override
    public void setTotalFeeC2Normal(String fee) {
        mTvTotalFeeC2Normal.setText(fee);
    }

    @Override
    public void setTotalFeeC2VIP(String fee) {
        mTvTotalFeeC2VIP.setText(fee);
    }
}
