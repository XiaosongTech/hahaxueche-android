package com.hahaxueche.ui.fragment.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.community.Article;
import com.hahaxueche.presenter.community.CommunityPresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.community.ArticleActivity;
import com.hahaxueche.ui.activity.community.ExamLibraryActivity;
import com.hahaxueche.ui.adapter.community.ArticleListPageAdapter;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.community.CommunityView;
import com.hahaxueche.util.RequestCode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wangshirui on 16/9/13.
 */
public class CommunityFragment extends HHBaseFragment implements CommunityView {
    private MainActivity mActivity;
    private CommunityPresenter mPresenter;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.tv_headline)
    TextView mTvHeadline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CommunityPresenter();
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        ArticleListPageAdapter adapter = new ArticleListPageAdapter(getChildFragmentManager(), getContext(), mPresenter.getArticleCaterories());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        mViewPager.setOffscreenPageLimit(mTabLayout.getTabCount());
        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.rly_test_lib,
            R.id.rly_group_buy,
            R.id.fly_headline})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_test_lib:
                mPresenter.clickTestLibCount();
                startActivity(new Intent(getContext(), ExamLibraryActivity.class));
                break;
            case R.id.rly_group_buy:
                mPresenter.clickGroupBuyCount();
                break;
            case R.id.fly_headline:
                Article headline = mPresenter.getHeadlineArticle();
                if (headline != null) {
                    Intent intent = new Intent(getContext(), ArticleActivity.class);
                    intent.putExtra("articleId", headline.id);
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_ARTICLE_DETAIL);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setHeadline(final Article article) {
        mTvHeadline.setText(article.title);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_ARTICLE_DETAIL) {
            if (resultCode == RESULT_OK && null != data) {
                Article article = data.getParcelableExtra("article");
                if (article != null) {
                    mPresenter.setHeadlineArticle(article);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
