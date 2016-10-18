package com.hahaxueche.ui.fragment.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hahaxueche.R;
import com.hahaxueche.model.community.News;
import com.hahaxueche.presenter.community.CommunityPresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.community.ArticleActivity;
import com.hahaxueche.ui.activity.community.ExamLibraryActivity;
import com.hahaxueche.ui.adapter.community.NewsAdapter;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.community.CommunityView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/13.
 */
public class CommunityFragment extends HHBaseFragment implements CommunityView, XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private CommunityPresenter mPresenter;
    private MainActivity mActivity;
    @BindView(R.id.xlv_news)
    XListView mXlvNews;
    private NewsAdapter mNewsAdapter;
    private ArrayList<News> mNewsArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mPresenter = new CommunityPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        mXlvNews.setPullRefreshEnable(true);
        mXlvNews.setPullLoadEnable(true);
        mXlvNews.setAutoLoadEnable(true);
        mXlvNews.setXListViewListener(this);
        mXlvNews.setOnItemClickListener(this);
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
    public void refreshNewsList(ArrayList<News> newsArrayList) {
        mNewsArrayList = newsArrayList;
        mNewsAdapter = new NewsAdapter(getContext(), mNewsArrayList);
        mXlvNews.setAdapter(mNewsAdapter);
        mXlvNews.stopRefresh();
        mXlvNews.stopLoadMore();
    }

    @Override
    public void addMoreNewsList(ArrayList<News> newsArrayList) {
        mNewsArrayList.addAll(newsArrayList);
        mNewsAdapter.notifyDataSetChanged();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), ArticleActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rly_test_lib)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_test_lib:
                mActivity.startActivity(new Intent(getContext(), ExamLibraryActivity.class));
            default:
                break;
        }
    }
}
