package com.hahaxueche.ui.activity.mySetting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hahaxueche.R;
import com.hahaxueche.utils.Util;

/**
 * Created by Administrator on 2016/6/18.
 */
public class FAQActivity extends MSBaseActivity {
    private ImageButton mIbtnBack;
    private SubsamplingScaleImageView mIvFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        initViews();
        initEvents();
    }

    private void initViews() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mIvFaq = Util.instence(this).$(this, R.id.iv_faq);
        mIvFaq.setImage(ImageSource.resource(R.drawable.commonfaq));
    }

    private void initEvents() {
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FAQActivity.this.finish();
            }
        });
    }
}
