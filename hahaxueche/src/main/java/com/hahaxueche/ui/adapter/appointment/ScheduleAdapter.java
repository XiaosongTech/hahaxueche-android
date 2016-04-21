package com.hahaxueche.ui.adapter.appointment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.base.BaseApiResponse;
import com.hahaxueche.model.coach.ScheduleEvent;
import com.hahaxueche.model.review.ReviewInfo;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.BaseCallbackListener;
import com.hahaxueche.presenter.appointment.APPresenter;
import com.hahaxueche.presenter.appointment.APPresenterImpl;
import com.hahaxueche.ui.activity.appointment.AppointmentActivity;
import com.hahaxueche.ui.dialog.BaseAlertDialog;
import com.hahaxueche.ui.dialog.BaseConfirmDialog;
import com.hahaxueche.ui.dialog.ScoreCoachDialog;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.utils.SharedPreferencesUtil;
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
    private SharedPreferencesUtil spUtil;
    private String courseName;
    private String phaseName;
    private String mCityId;
    private String mBooked = "0";//0:教练将来的；1:自己已经booked的了 default to 0
    private String mTip;
    private APPresenter apPresenter;
    private User mUser;
    private onRefreshActivityUIListener mRefreshUIListener;


    public interface onRefreshActivityUIListener {
        void refreshActivityUI();
    }

    public ScheduleAdapter(Context context, ArrayList<ScheduleEvent> scheduleEventArrayList, int resource, String booked, User user, onRefreshActivityUIListener refreshUIListener) {
        this.context = context;
        this.mScheduleEventList = scheduleEventArrayList;
        this.resource = resource;
        this.mBooked = booked;
        this.mRefreshUIListener = refreshUIListener;
        mTip = "一次只能预约一节课，这节课完成后才可预约新课程，确定预约这节课么？";
        spUtil = new SharedPreferencesUtil(context);
        mCityId = spUtil.getMyCity().getId();
        apPresenter = new APPresenterImpl(context);
        mUser = user;
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
            holder.vwDivider = view.findViewById(R.id.vw_schedule_divider);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ScheduleEvent scheduleEvent = mScheduleEventList.get(position);
        holder.tvCoachName.setText(scheduleEvent.getCoach().getName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfYearMonth = new SimpleDateFormat("yyyy.MM");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd日");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        try {
            Date startTime = sdf.parse(scheduleEvent.getStart_time());
            Date endTime = sdf.parse(scheduleEvent.getEnd_time());
            holder.tvYearMonth.setText(sdfYearMonth.format(startTime));
            holder.tvDay.setText(sdfDay.format(startTime));
            holder.tvTime.setText("(" + sdfTime.format(startTime) + " - " + sdfTime.format(endTime) + ")");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (scheduleEvent.isShowDay()) {
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
        phaseName = spUtil.getPhaseName(String.valueOf(scheduleEvent.getStudent_phase()), mCityId);
        courseName = spUtil.getCourseName(String.valueOf(scheduleEvent.getService_type()), mCityId);
        holder.tvScheduleInfo.setText(courseName + "，" + phaseName + "，" + scheduleEvent.getRegistered_st_count() + "人/" + scheduleEvent.getMax_st_count() + "人");
        if (!holder.isLoadedLly) {
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
                        Student student = scheduleEvent.getRegistered_students().get(i * 4 + j);
                        getStudentAvatar(student.getAvatar(), civStudentAvatar);
                        llyAvatar.addView(civStudentAvatar, cirAvatarParams);
                    } else if (scheduleEvent.getMax_st_count() > i * 4 + j) {
                        civStudentAvatar.setBackgroundResource(R.drawable.ic_class_emptyava);
                        llyAvatar.addView(civStudentAvatar, cirAvatarParams);
                    }
                }
                holder.llyAvatar.addView(llyAvatar, llyAvatarParams);
            }
            holder.isLoadedLly = true;
        }
        setMainButton(holder.tvMainButton, scheduleEvent);
        return view;
    }

    private void setMainButton(TextView mainButton, final ScheduleEvent scheduleEvent) {
        String status = scheduleEvent.getStatus();
        if (status.equals("0")) {
            //预约课程
            if (scheduleEvent.getRegistered_st_count() == scheduleEvent.getMax_st_count()) {
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
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date startTime = sdf.parse(scheduleEvent.getStart_time());
                            Date endTime = sdf.parse(scheduleEvent.getEnd_time());
                            SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy年MM月dd日");
                            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
                            BaseConfirmDialog dialog = new BaseConfirmDialog(context, "预约课程", "您预约了课程", "日期：" + sdfDay.format(startTime) + "\n时间：" + sdfTime.format(startTime) + "-" + sdfTime.format(endTime) + "\n科目：" + courseName + "  阶段：" + phaseName, mTip,
                                    "确认", "取消", new BaseConfirmDialog.onConfirmListener() {
                                @Override
                                public boolean clickConfirm() {
                                    apPresenter.bookCourseSchedule(mUser.getStudent().getId(), scheduleEvent.getId(), mUser.getSession().getAccess_token(), new BaseCallbackListener<ScheduleEvent>() {
                                        @Override
                                        public void onSuccess(ScheduleEvent scheduleEvent) {
                                            Toast.makeText(context, "预约成功！", Toast.LENGTH_SHORT).show();
                                            mRefreshUIListener.refreshActivityUI();
                                        }

                                        @Override
                                        public void onFailure(String errorEvent, String message) {
                                            if (errorEvent.equals("40006")) {
                                                //40006 有未完成的课程
                                                BaseAlertDialog baseAlertDialog = new BaseAlertDialog(context, "预约失败", "您还有未完成课程", "您的课程列表还有未完成的课程，请课程完成后再预约新课程。");
                                                baseAlertDialog.show();
                                            } else if (errorEvent.equals("40005")) {
                                                //40005 有待评级的课程
                                                BaseAlertDialog baseAlertDialog = new BaseAlertDialog(context, "预约失败", "您还有待评级课程", "教练还没有对您之前的课程评级，待教练评级后再预约新课程。若长时间未评级请及时联系教练为您评级。");
                                                baseAlertDialog.show();
                                            } else {
                                                Toast.makeText(context, "预约失败", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                    return true;
                                }
                            }, new BaseConfirmDialog.onCancelListener() {
                                @Override
                                public boolean clickCancel() {
                                    return true;
                                }
                            });
                            dialog.show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
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
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date startTime = sdf.parse(scheduleEvent.getStart_time());
                        Date endTime = sdf.parse(scheduleEvent.getEnd_time());
                        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy年MM月dd日");
                        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
                        BaseConfirmDialog dialog = new BaseConfirmDialog(context, "取消课程", "您是否要取消课程？", "日期：" + sdfDay.format(startTime) + "\n时间：" + sdfTime.format(startTime) + "-" + sdfTime.format(endTime) + "\n科目：" + courseName + "  阶段：" + phaseName, "",
                                "暂不取消", "取消课程", new BaseConfirmDialog.onConfirmListener() {
                            @Override
                            public boolean clickConfirm() {
                                return true;
                            }
                        }, new BaseConfirmDialog.onCancelListener() {
                            @Override
                            public boolean clickCancel() {
                                apPresenter.cancelCourseSchedule(mUser.getStudent().getId(), scheduleEvent.getId(), mUser.getSession().getAccess_token(), new BaseCallbackListener<BaseApiResponse>() {
                                    @Override
                                    public void onSuccess(BaseApiResponse data) {
                                        Toast.makeText(context, "取消成功！", Toast.LENGTH_SHORT).show();
                                        mRefreshUIListener.refreshActivityUI();
                                    }

                                    @Override
                                    public void onFailure(String errorEvent, String message) {
                                        Toast.makeText(context, "取消失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return true;
                            }
                        });
                        dialog.show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (status.equals("2")) {
            //课程评分
            mainButton.setText("课程评分");
            mainButton.setBackgroundResource(R.drawable.rectangle_back_green_sm);
            mainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScoreCoachDialog dialog = new ScoreCoachDialog(context, new ScoreCoachDialog.onScoreListener() {
                        @Override
                        public void onScore(float score) {
                            apPresenter.reviewSchedule(mUser.getStudent().getId(), scheduleEvent.getId(), String.valueOf(score), mUser.getSession().getAccess_token(), new BaseCallbackListener<ReviewInfo>() {
                                @Override
                                public void onSuccess(ReviewInfo reviewInfo) {
                                    Toast.makeText(context, "评分成功！", Toast.LENGTH_SHORT).show();
                                    mRefreshUIListener.refreshActivityUI();
                                }

                                @Override
                                public void onFailure(String errorEvent, String message) {
                                    Toast.makeText(context, "评分失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    dialog.show();
                }
            });
        } else if (status.equals("3")) {
            //已完成
            mainButton.setText("已完成");
            mainButton.setBackgroundResource(R.drawable.rectangle_back_gray_sm);
            mainButton.setClickable(false);
        }
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
        boolean isLoadedLly = false;
        View vwDivider;
    }
}
