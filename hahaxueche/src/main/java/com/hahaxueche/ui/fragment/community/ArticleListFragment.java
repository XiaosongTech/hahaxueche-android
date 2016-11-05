package com.hahaxueche.ui.fragment.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hahaxueche.R;
import com.hahaxueche.model.community.Article;
import com.hahaxueche.presenter.community.ArticleListPresenter;
import com.hahaxueche.ui.adapter.community.ArticleAdapter;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.community.ArticleListView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wangshirui on 2016/11/3.
 */

public class ArticleListFragment extends HHBaseFragment implements ArticleListView, XListView.IXListViewListener {
    @BindView(R.id.xlv_news)
    XListView mXlvNews;
    private ArticleAdapter mArticleAdapter;
    private ArrayList<Article> mArticleArrayList;
    private ArticleListPresenter mPresenter;


    public static ArticleListFragment newInstance(int isPopular, int category) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("isPopular", isPopular);
        bundle.putInt("category", category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ArticleListPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_list, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        mPresenter.setQuery(getArguments());
        mXlvNews.setPullRefreshEnable(true);
        mXlvNews.setPullLoadEnable(true);
        mXlvNews.setAutoLoadEnable(true);
        mXlvNews.setXListViewListener(this);
        mPresenter.fetchNews();
        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setPullLoadEnable(boolean enable) {
        mXlvNews.setPullLoadEnable(enable);
    }

    @Override
    public void refreshNewsList(ArrayList<Article> articleArrayList) {
        mArticleArrayList = articleArrayList;
        mArticleAdapter = new ArticleAdapter(this, mArticleArrayList, mPresenter.getCategoryLabel());
        mXlvNews.setAdapter(mArticleAdapter);
        mXlvNews.stopRefresh();
        mXlvNews.stopLoadMore();
    }

    @Override
    public void addMoreNewsList(ArrayList<Article> articleArrayList) {
        mArticleArrayList.addAll(articleArrayList);
        mArticleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchNews();
    }

    @Override
    public void onLoadMore() {
        mPresenter.loadMoreNews();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12) {
            if (resultCode == RESULT_OK && null != data) {
                Article article = data.getParcelableExtra("article");
                if (article != null) {
                    for (Article art : mArticleArrayList) {
                        if (art.id.equals(article.id)) {
                            art.like_count = article.like_count;
                            art.liked = article.liked;
                            art.view_count = article.view_count;
                            art.comments = article.comments;
                            mArticleAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
