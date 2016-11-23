package com.hahaxueche.ui.adapter.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class ArticleCommentAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Comment> mArticleCommentList;

    public ArticleCommentAdapter(Context context, ArrayList<Comment> ArticleCommentArrayList) {
        inflater = LayoutInflater.from(context);
        mArticleCommentList = ArticleCommentArrayList;
    }

    @Override
    public int getCount() {
        return mArticleCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArticleCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArticleCommentAdapter.ViewHolder holder;
        if (convertView != null) {
            holder = (ArticleCommentAdapter.ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_article_comment, parent, false);
            holder = new ArticleCommentAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        Comment articleComment = mArticleCommentList.get(position);
        holder.ivAvatar.setImageURI(articleComment.student_avatar);
        holder.tvName.setText(articleComment.student_name);
        holder.tvComment.setText(articleComment.content);
        holder.tvDate.setText(Utils.getDateDotFromUTC(articleComment.created_at));
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_avatar)
        SimpleDraweeView ivAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_comment)
        TextView tvComment;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
