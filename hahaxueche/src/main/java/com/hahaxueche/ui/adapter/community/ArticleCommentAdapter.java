package com.hahaxueche.ui.adapter.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.community.Comment;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/4.
 */

public class ArticleCommentAdapter extends RecyclerView.Adapter<ArticleCommentAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Comment> mArticleCommentList;

    public ArticleCommentAdapter(Context context, ArrayList<Comment> ArticleCommentArrayList) {
        inflater = LayoutInflater.from(context);
        mArticleCommentList = ArticleCommentArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_article_comment, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment articleComment = mArticleCommentList.get(position);
        holder.ivAvatar.setImageURI(articleComment.student_avatar);
        holder.tvName.setText(articleComment.student_name);
        holder.tvComment.setText(articleComment.content);
        holder.tvDate.setText(Utils.getDateDotFromUTC(articleComment.created_at));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mArticleCommentList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avatar)
        SimpleDraweeView ivAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_comment)
        TextView tvComment;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
