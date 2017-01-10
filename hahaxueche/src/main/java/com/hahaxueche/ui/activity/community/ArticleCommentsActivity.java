package com.hahaxueche.ui.activity.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.community.Comment;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.community.ArticleCommentAdapter;
import com.hahaxueche.ui.widget.recyclerView.DividerItemDecoration;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/4.
 */

public class ArticleCommentsActivity extends HHBaseActivity {
    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.lv_article_comments)
    RecyclerView mLvArticleComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_comments);
        ButterKnife.bind(this);
        initActionBar();
        Intent intent = getIntent();
        if (intent.getParcelableArrayListExtra("comments") != null) {
            ArrayList<Comment> comments = intent.getParcelableArrayListExtra("comments");
            mLvArticleComments.setLayoutManager(new LinearLayoutManager(this));
            mLvArticleComments.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST,
                    Utils.instence(this).dip2px(20)));
            ArticleCommentAdapter adapter = new ArticleCommentAdapter(this, comments);
            mLvArticleComments.setAdapter(adapter);
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("评论");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArticleCommentsActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
