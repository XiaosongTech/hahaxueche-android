package com.hahaxueche.ui.adapter.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.community.News;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/22.
 */

public class NewsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<News> mNewsList;

    public NewsAdapter(Context context, ArrayList<News> NewsList) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mNewsList = NewsList;
    }

    @Override
    public int getCount() {
        return mNewsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNewsList.get(position);
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
            convertView = inflater.inflate(R.layout.adapter_news, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        News news = mNewsList.get(position);
        holder.tvPublishDate.setText(news.date);
        holder.tvSubTitle.setText(news.sub_title);
        holder.ivNewPic.setImageURI(news.pic_url);
        holder.tvTitle.setText(news.title);
        holder.tvContent.setText(news.content);
        holder.tvCommentCount.setText(Utils.getCount(news.comment_count));
        holder.tvLikeCount.setText(Utils.getCount(news.like_count));
        holder.tvReadCount.setText(Utils.getCount(news.read_count));
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

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
