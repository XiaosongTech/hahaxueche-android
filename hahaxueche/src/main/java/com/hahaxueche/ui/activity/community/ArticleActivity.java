package com.hahaxueche.ui.activity.community;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;

import com.hahaxueche.R;
import com.hahaxueche.ui.activity.base.HHBaseActivity;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/22.
 */

public class ArticleActivity extends HHBaseActivity {
    ImageView mIvBack;
    ImageView mIvShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_article);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mIvShare = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_share);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArticleActivity.this.finish();
            }
        });
    }
}
