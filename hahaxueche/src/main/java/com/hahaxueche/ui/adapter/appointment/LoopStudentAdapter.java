package com.hahaxueche.ui.adapter.appointment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.BannerHighlight;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by gibxin on 2016/4/19.
 */
public class LoopStudentAdapter extends BaseAdapter {
    private ArrayList<BannerHighlight> mStudentList;
    private int resource;   //item的布局
    private Context context;
    private LayoutInflater inflator;

    public LoopStudentAdapter(Context context, ArrayList<BannerHighlight> studentList, int resource) {
        this.context = context;
        this.mStudentList = studentList;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return mStudentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStudentList.get(position);
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
            inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(resource, null);
            holder = new ViewHolder();
            holder.civStudentAvatar = (CircleImageView) view.findViewById(R.id.civ_student_avatar);   //为了减少开销，则只在第一页时调用findViewById
            holder.tvStudentName = (TextView) view.findViewById(R.id.tv_student_name);
            holder.tvStudentText = (TextView) view.findViewById(R.id.tv_student_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        BannerHighlight student = mStudentList.get(position);
        String infoText = student.getText();
        ArrayList<String> highlightsTextList = student.getHighlights();
        SpannableStringBuilder style = new SpannableStringBuilder(infoText);
        for (String highlight : highlightsTextList) {
            int index = infoText.indexOf(highlight);
            int length = highlight.length();
            style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.app_theme_color)), index, index + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        holder.tvStudentText.setText(style);
        holder.tvStudentName.setText(student.getName());
        getStudentAvatar(student.getAvatar_url(), holder.civStudentAvatar);
        if(position==0) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.listview_roll_in);
            view.startAnimation(animation);
        }else {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.listview_roll_down);
            view.startAnimation(animation);
        }
        return view;
    }

    private void getStudentAvatar(String url, CircleImageView civCoachAvatar) {
        final int iconWidth = Util.instence(context).dip2px(50);
        final int iconHeight = iconWidth;
        Picasso.with(context).load(url).resize(iconWidth, iconHeight)
                .into(civCoachAvatar);
    }

    static class ViewHolder {
        CircleImageView civStudentAvatar;
        TextView tvStudentName;
        TextView tvStudentText;
    }
}
