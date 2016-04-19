package com.hahaxueche.ui.adapter.appointment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.coach.ScheduleEvent;
import com.hahaxueche.model.student.StudentModel;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by gibxin on 2016/4/19.
 */
public class ScheduleAdapter extends BaseAdapter {
    private ArrayList<ScheduleEvent> mScheduleEventList;
    private int resource;   //item的布局
    private Context context;
    private LayoutInflater inflator;

    public ScheduleAdapter(Context context, ArrayList<ScheduleEvent> scheduleEventArrayList, int resource) {
        this.context = context;
        this.mScheduleEventList = scheduleEventArrayList;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return mScheduleEventList.size();
    }

    @Override
    public Object getItem(int position) {
        return mScheduleEventList.get(position);
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
            holder.flIcon = (FrameLayout) view.findViewById(R.id.fl_schedule_icon);
            holder.tvYearMonth = (TextView) view.findViewById(R.id.tv_schedule_year_month);
            holder.tvDay = (TextView) view.findViewById(R.id.tv_schedule_day);
            holder.tvCoachName = (TextView) view.findViewById(R.id.tv_adapter_schedule_coach_name);
            holder.tvTime = (TextView) view.findViewById(R.id.tv_adapter_schedule_time);
            holder.tvScheduleInfo = (TextView) view.findViewById(R.id.tv_adapter_schedule_course_info);
            holder.llyAvatar = (LinearLayout) view.findViewById(R.id.lly_adapter_schedule_avatar);
            holder.tvMainButton = (TextView) view.findViewById(R.id.tv_adapter_main_button);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ScheduleEvent scheduleEvent = mScheduleEventList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfYearMonth = new SimpleDateFormat("yyyy.MM");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd日");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        try {
            Date startTime = sdf.parse(scheduleEvent.getStart_time());
            Date endTime = sdf.parse(scheduleEvent.getEnd_time());
            holder.tvYearMonth.setText(sdfYearMonth.format(startTime));
            holder.tvDay.setText(sdfDay.format(startTime));
            holder.tvCoachName.setText(scheduleEvent.getCoach().getName());
            holder.tvTime.setText("(" + sdfTime.format(startTime) + "-" + sdfTime.format(endTime) + ")");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        int registerStudentCount = scheduleEvent.getRegistered_students().size();

        for (int i = 0; i < scheduleEvent.getMax_st_count() / 4 + 1; i++) {
            LinearLayout.LayoutParams llyAvatarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llyAvatarParams.setMargins(0, Util.instence(context).dip2px(10), 0, 0);
            LinearLayout llyAvatar = new LinearLayout(context);
            llyAvatar.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 4; j++) {
                CircleImageView civStudentAvatar = new CircleImageView(context);
                LinearLayout.LayoutParams cirAvatarParams = new LinearLayout.LayoutParams(Util.instence(context).dip2px(50), Util.instence(context).dip2px(50));
                if (j != 0) {
                    cirAvatarParams.setMargins(Util.instence(context).dip2px(10), 0, 0, 0);
                }
                if (registerStudentCount > i * 4 + j) {
                    StudentModel student = scheduleEvent.getRegistered_students().get(i * 4 + j);
                    getStudentAvatar(student.getAvatar(), civStudentAvatar);
                    llyAvatar.addView(civStudentAvatar, cirAvatarParams);
                } else if (scheduleEvent.getMax_st_count() > i * 4 + j) {
                    civStudentAvatar.setBackgroundResource(R.drawable.ic_class_emptyava);
                    llyAvatar.addView(civStudentAvatar, cirAvatarParams);
                }
            }
            holder.llyAvatar.addView(llyAvatar, llyAvatarParams);
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
        TextView tvYearMonth;
        TextView tvDay;
        TextView tvCoachName;
        TextView tvTime;
        TextView tvScheduleInfo;
        TextView tvMainButton;
        FrameLayout flIcon;
        LinearLayout llyAvatar;
    }
}
