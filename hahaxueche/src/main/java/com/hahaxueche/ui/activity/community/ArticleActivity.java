package com.hahaxueche.ui.activity.community;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.presenter.community.ArticlePresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.ui.dialog.community.CommentDialog;
import com.hahaxueche.ui.view.community.ArticleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/22.
 */

public class ArticleActivity extends HHBaseActivity implements ArticleView {
    ImageView mIvBack;
    ImageView mIvShare;
    @BindView(R.id.tv_publish_comment)
    TextView mTvPublishComment;
    private CommentDialog commentDialog;
    private ShareDialog shareDialog;
    private ArticlePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ArticlePresenter();
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base_share);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mIvShare = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_share);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArticleActivity.this.finish();
            }
        });
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareDialog == null) {
                    shareDialog = new ShareDialog(getContext(), new ShareDialog.OnShareListener() {
                        @Override
                        public void onShare(int shareType) {

                        }
                    });
                }
                shareDialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        commentDialog = null;
        shareDialog = null;
        super.onDestroy();
    }

    @OnClick(R.id.tv_publish_comment)
    @Override
    public void showCommentDialog() {
        if (commentDialog == null) {
            commentDialog = new CommentDialog(getContext(), new CommentDialog.OnCommentListener() {
                @Override
                public void send(String comment) {
                    mPresenter.sendComment(comment);
                }

                @Override
                public void saveDraft(String draft) {
                    mPresenter.saveDraft(draft);
                }

                @Override
                public void clearDraft() {
                    mPresenter.clearDraft();
                }
            });
        }
        commentDialog.show();
    }

    @Override
    public void setDraft(String draft) {
        mTvPublishComment.setText(draft);
    }
}
