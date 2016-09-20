package com.hahaxueche.ui.activity.myPage;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hahaxueche.R;
import com.hahaxueche.ui.activity.base.HHBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/20.
 */
public class FAQActivity extends HHBaseActivity {
    @BindView(R.id.iv_faq)
    SubsamplingScaleImageView mIvFaq;
    ImageView mIvBack;
    TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);
        initActionBar();
        mIvFaq.setImage(ImageSource.resource(R.drawable.commonfaq));
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("学员常见问题");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQActivity.this.finish();
            }
        });
    }
}
