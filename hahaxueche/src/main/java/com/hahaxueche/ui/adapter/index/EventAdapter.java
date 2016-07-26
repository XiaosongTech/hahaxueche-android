package com.hahaxueche.ui.adapter.index;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.activity.Event;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wangshirui on 16/7/26.
 */
public class EventAdapter extends BaseAdapter {
    private int mResource;   //item的布局
    private LayoutInflater mInflator;
    private Context mContext;
    private ArrayList<Event> mEventList;

    public EventAdapter(Context context, ArrayList<Event> eventList, int resource) {
        mContext = context;
        mEventList = eventList;
        mResource = resource;
    }

    @Override
    public int getCount() {
        return mEventList.size();
    }

    @Override
    public Object getItem(int position) {
        return mEventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflator.inflate(mResource, null);
            holder = new ViewHolder();
            holder.ivIcon = (CircleImageView) view.findViewById(R.id.iv_event_icon);
            holder.tvTitle = (TextView) view.findViewById(R.id.tv_event_title);
            holder.tvTimeLeftLabel = (TextView) view.findViewById(R.id.tv_time_left_label);
            holder.tvTimeLeft = (TextView) view.findViewById(R.id.tv_time_left);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Event event = mEventList.get(position);
        Picasso.with(mContext).load(event.getIcon()).into(holder.ivIcon);
        holder.tvTitle.setText(event.getTitle());
        //加载倒计时
        if (!TextUtils.isEmpty(event.getEnd_date())) {
            holder.tvTimeLeft.setVisibility(View.VISIBLE);
            holder.tvTimeLeftLabel.setVisibility(View.VISIBLE);
            String text = event.getCountDownText();
            SpannableString ss = new SpannableString(text);
            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.app_theme_color)), 0, text.indexOf("天"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new AbsoluteSizeSpan(Util.instence(mContext).sp2px(12)), text.indexOf("天"), text.indexOf("天") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.app_theme_color)), text.indexOf("天") + 1, text.indexOf("时"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new AbsoluteSizeSpan(Util.instence(mContext).sp2px(12)), text.indexOf("时"), text.indexOf("时") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.app_theme_color)), text.indexOf("时") + 1, text.indexOf("分"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new AbsoluteSizeSpan(Util.instence(mContext).sp2px(12)), text.indexOf("分"), text.indexOf("分") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.app_theme_color)), text.indexOf("分") + 1, text.indexOf("秒"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new AbsoluteSizeSpan(Util.instence(mContext).sp2px(12)), text.indexOf("秒"), text.indexOf("秒") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.tvTimeLeft.setText(ss);
        } else {
            holder.tvTimeLeft.setVisibility(View.INVISIBLE);
            holder.tvTimeLeftLabel.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    static class ViewHolder {
        CircleImageView ivIcon;
        TextView tvTitle;
        TextView tvTimeLeftLabel;
        TextView tvTimeLeft;
    }
}
