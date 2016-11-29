package com.hahaxueche.ui.adapter.myPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.course.ScheduleEvent;
import com.hahaxueche.model.user.student.Student;
import com.hahaxueche.presenter.myPage.ScheduleListPresenter;
import com.hahaxueche.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/5.
 */

public class ScheduleEventAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<ScheduleEvent> mScheduleEventList;
    private ScheduleListPresenter mPresenter;

    public ScheduleEventAdapter(Context context, ArrayList<ScheduleEvent> ScheduleEventArrayList, ScheduleListPresenter presenter) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mScheduleEventList = ScheduleEventArrayList;
        mPresenter = presenter;
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
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_schedule_event, parent, false);
            holder = new ScheduleEventAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ScheduleEvent scheduleEvent = mScheduleEventList.get(position);
        holder.tvCoachName.setText(scheduleEvent.coach.name);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfYearMonth = new SimpleDateFormat("yyyy.MM");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd日");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        try {
            Date startTime = sdf.parse(scheduleEvent.start_time);
            Date endTime = sdf.parse(scheduleEvent.end_time);
            holder.tvYearMonth.setText(sdfYearMonth.format(startTime));
            holder.tvDay.setText(sdfDay.format(startTime));
            holder.tvTime.setText("(" + sdfTime.format(startTime) + " - " + sdfTime.format(endTime) + ")");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (scheduleEvent.isShowDay) {
            holder.flIcon.setVisibility(View.VISIBLE);
            holder.tvYearMonth.setVisibility(View.VISIBLE);
            holder.tvDay.setVisibility(View.VISIBLE);
            holder.vwDivider.setVisibility(View.VISIBLE);
        } else {
            holder.flIcon.setVisibility(View.INVISIBLE);
            holder.tvYearMonth.setVisibility(View.INVISIBLE);
            holder.tvDay.setVisibility(View.INVISIBLE);
            holder.vwDivider.setVisibility(View.GONE);
        }

        holder.tvScheduleInfo.setText(mPresenter.getCourseName(scheduleEvent.service_type) + "，" +
                scheduleEvent.registered_st_count + "人/" + scheduleEvent.max_st_count + "人");
        if (!holder.isLoadedLly) {
            int registerStudentCount = scheduleEvent.registered_students.size();
            for (int i = 0; i < scheduleEvent.max_st_count / 4 + 1; i++) {
                LinearLayout.LayoutParams llyAvatarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                llyAvatarParams.setMargins(0, Utils.instence(mContext).dip2px(10), 0, 0);
                LinearLayout llyAvatar = new LinearLayout(mContext);
                llyAvatar.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < 4; j++) {
                    SimpleDraweeView civStudentAvatar = new SimpleDraweeView(mContext);
                    GenericDraweeHierarchy hierarchy = civStudentAvatar.getHierarchy();
                    hierarchy.setRoundingParams(new RoundingParams().setRoundAsCircle(true));
                    LinearLayout.LayoutParams cirAvatarParams = new LinearLayout.LayoutParams(Utils.instence(mContext).dip2px(50), Utils.instence(mContext).dip2px(50));
                    if (j != 0) {
                        cirAvatarParams.setMargins(Utils.instence(mContext).dip2px(10), 0, 0, 0);
                    }
                    civStudentAvatar.setLayoutParams(cirAvatarParams);
                    if (registerStudentCount > i * 4 + j) {
                        Student student = scheduleEvent.registered_students.get(i * 4 + j);
                        civStudentAvatar.setImageURI(student.avatar);
                        llyAvatar.addView(civStudentAvatar, cirAvatarParams);
                    } else if (scheduleEvent.max_st_count > i * 4 + j) {
                        civStudentAvatar.setBackgroundResource(R.drawable.ic_class_emptyava);
                        llyAvatar.addView(civStudentAvatar, cirAvatarParams);
                    }
                }
                holder.llyAvatar.addView(llyAvatar, llyAvatarParams);
            }
            holder.isLoadedLly = true;
        }
        setMainButton(holder.tvMainButton, scheduleEvent);
        return convertView;
    }

    private void setMainButton(TextView mainButton, final ScheduleEvent scheduleEvent) {
        String status = scheduleEvent.status;
        if (status.equals("0")) {
            //预约课程
            if (scheduleEvent.registered_st_count == scheduleEvent.max_st_count) {
                //已选满
                mainButton.setText("已选满");
                mainButton.setBackgroundResource(R.drawable.rectangle_back_gray_sm);
                mainButton.setClickable(false);
            } else {
                mainButton.setText("预约课程");
                mainButton.setBackgroundResource(R.drawable.rectangle_back_orange_sm);
                mainButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.preBookSchedule(scheduleEvent);
                    }
                });
            }
        } else if (status.equals("1")) {
            //取消课程
            mainButton.setText("取消课程");
            mainButton.setBackgroundResource(R.drawable.rectangle_back_orange_heavy_sm);
            mainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.preCancelSchedule(scheduleEvent);
                }
            });
        } else if (status.equals("2")) {
            //课程评分
            mainButton.setText("课程评分");
            mainButton.setBackgroundResource(R.drawable.rectangle_back_green_sm);
            mainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.preReviewSchedule(scheduleEvent);
                }
            });
        } else if (status.equals("3")) {
            //已完成
            mainButton.setText("已完成");
            mainButton.setBackgroundResource(R.drawable.rectangle_back_gray_sm);
            mainButton.setClickable(false);
        }
    }


    static class ViewHolder {
        @BindView(R.id.tv_schedule_year_month)
        TextView tvYearMonth;
        @BindView(R.id.tv_schedule_day)
        TextView tvDay;
        @BindView(R.id.tv_adapter_schedule_coach_name)
        TextView tvCoachName;
        @BindView(R.id.tv_adapter_schedule_time)
        TextView tvTime;
        @BindView(R.id.tv_adapter_schedule_course_info)
        TextView tvScheduleInfo;
        @BindView(R.id.tv_adapter_main_button)
        TextView tvMainButton;
        @BindView(R.id.fl_schedule_icon)
        FrameLayout flIcon;
        @BindView(R.id.lly_adapter_schedule_avatar)
        LinearLayout llyAvatar;
        @BindView(R.id.vw_schedule_divider)
        View vwDivider;
        boolean isLoadedLly = false;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
