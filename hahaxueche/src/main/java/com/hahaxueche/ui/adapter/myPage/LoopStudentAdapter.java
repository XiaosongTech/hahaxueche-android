package com.hahaxueche.ui.adapter.myPage;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.base.BannerHighlight;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/5.
 */

public class LoopStudentAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<BannerHighlight> mStudentList;

    public LoopStudentAdapter(Context context, ArrayList<BannerHighlight> studentList) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mStudentList = studentList;
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
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_loop_student_schedule, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        BannerHighlight student = mStudentList.get(position);
        String infoText = student.text;
        ArrayList<String> highlightsTextList = student.highlights;
        SpannableStringBuilder style = new SpannableStringBuilder(infoText);
        for (String highlight : highlightsTextList) {
            int index = infoText.indexOf(highlight);
            int length = highlight.length();
            style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.app_theme_color)), index, index + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        holder.tvStudentText.setText(style);
        holder.tvStudentName.setText(student.name);
        holder.civStudentAvatar.setImageURI(student.avatar_url);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.civ_student_avatar)
        SimpleDraweeView civStudentAvatar;
        @BindView(R.id.tv_student_name)
        TextView tvStudentName;
        @BindView(R.id.tv_student_text)
        TextView tvStudentText;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
