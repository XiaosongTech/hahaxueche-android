package com.hahaxueche.ui.adapter.community;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.community.Article;
import com.hahaxueche.ui.activity.community.ArticleActivity;
import com.hahaxueche.ui.fragment.community.ArticleListFragment;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/22.
 */

public class ArticleAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArticleListFragment mFragment;
    private ArrayList<Article> mArticleList;
    private String mCategoryLabel;

    public ArticleAdapter(ArticleListFragment fragment, ArrayList<Article> articleList, String categoryLabel) {
        inflater = LayoutInflater.from(fragment.getContext());
        mFragment = fragment;
        mArticleList = articleList;
        mCategoryLabel = categoryLabel;
    }

    @Override
    public int getCount() {
        return mArticleList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArticleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_article, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        final Article article = mArticleList.get(position);
        holder.tvPublishDate.setText(Utils.getDateDotFromUTC(article.created_at));
        holder.tvSubTitle.setText(mCategoryLabel);
        holder.ivNewPic.setImageURI(article.cover_image);
        holder.tvTitle.setText(article.title);
        holder.tvContent.setText(article.intro);
        int commentCount = 0;
        if (article.comments != null) {
            commentCount = article.comments.size();
        }
        holder.tvCommentCount.setText(Utils.getCount(commentCount));
        holder.tvLikeCount.setText(Utils.getCount(article.like_count));
        holder.tvReadCount.setText(Utils.getCount(article.view_count));
        holder.rlyAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mFragment.getContext(), ArticleActivity.class);
                intent.putExtra("article", article);
                mFragment.startActivityForResult(intent, 12);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_publish_date)
        TextView tvPublishDate;
        @BindView(R.id.tv_sub_title)
        TextView tvSubTitle;
        @BindView(R.id.iv_news_pic)
        SimpleDraweeView ivNewPic;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_comment_count)
        TextView tvCommentCount;
        @BindView(R.id.tv_like_count)
        TextView tvLikeCount;
        @BindView(R.id.tv_read_count)
        TextView tvReadCount;
        @BindView(R.id.rly_adapter)
        RelativeLayout rlyAdapter;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
