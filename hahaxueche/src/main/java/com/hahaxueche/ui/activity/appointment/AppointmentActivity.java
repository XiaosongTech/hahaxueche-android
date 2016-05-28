package com.hahaxueche.ui.activity.appointment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.base.BannerHighlight;
import com.hahaxueche.model.coach.ScheduleEvent;
import com.hahaxueche.model.response.ScheduleEventListResponse;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.BaseCallbackListener;
import com.hahaxueche.ui.activity.findCoach.FCBaseActivity;
import com.hahaxueche.ui.activity.findCoach.FindCoachActivity;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.activity.mySetting.MySettingActivity;
import com.hahaxueche.ui.adapter.appointment.LoopStudentAdapter;
import com.hahaxueche.ui.adapter.appointment.ScheduleAdapter;
import com.hahaxueche.ui.dialog.BaseAlertDialog;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;
import com.hahaxueche.ui.widget.pullToRefreshView.XScrollView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gibxin on 2016/1/27.
 */
public class AppointmentActivity extends FCBaseActivity implements XListView.IXListViewListener {
    private LinearLayout llyTabIndex;
    private LinearLayout llyTabFindCoach;
    private LinearLayout llyTabAppointment;
    private LinearLayout llyTabMySetting;
    private TextView mTvCoachSchedule;
    private TextView mTvMySchedule;
    private LinearLayout mLlySelectArea;
    private LinearLayout mLlyHasCoach;
    private RelativeLayout mRlyNoCoach;
    private LinearLayout mLlyNoSchedule;
    private SwipeRefreshLayout mSrlNoSchedule;
    private ScrollView mSvNoSchedule;
    private XListView xlvScheduleList;
    private ScheduleAdapter mAdapter;
    private ArrayList<ScheduleEvent> scheduleList = new ArrayList<ScheduleEvent>();
    private String linkSelf;
    private String linkNext;
    private String linkPrevious;
    private String page;
    private String per_page = "10";
    private String booked = "0";//0:教练将来的；1:自己已经booked的了 default to 0
    private BaseAlertDialog mAlertDialog;
    private ListView mLvLoopStudent;
    private TextView mTvChoseCoach;
    private LoopStudentAdapter mLoopStudentAdapter;
    private ArrayList<BannerHighlight> mBannerHightList;
    private ArrayList<BannerHighlight> mLoopBannerHightList = new ArrayList<>();
    private int loopIndex = 0;
    private final MyHandler mHandler = new MyHandler(this);
    private boolean isOnLoadMore = false;
    private User mUser;
    private String mCurrentCoachId;
    private String mAccessToken;
    private boolean isRefresh = false;//是否刷新中

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        initView();
        initEvent();
        loadDatas();
        refreshUI();
    }

    private void initView() {
        llyTabIndex = Util.instence(this).$(this, R.id.lly_tab_index);
        llyTabFindCoach = Util.instence(this).$(this, R.id.lly_tab_find_coach);
        llyTabAppointment = Util.instence(this).$(this, R.id.lly_tab_appointment);
        llyTabMySetting = Util.instence(this).$(this, R.id.lly_tab_my_setting);
        mTvCoachSchedule = Util.instence(this).$(this, R.id.tv_ap_coach_schedule);
        mTvMySchedule = Util.instence(this).$(this, R.id.tv_ap_my_schedule);
        mLlyHasCoach = Util.instence(this).$(this, R.id.lly_has_coach);
        mRlyNoCoach = Util.instence(this).$(this, R.id.rly_no_coach);
        mLvLoopStudent = Util.instence(this).$(this, R.id.lv_loop_student);
        mLlySelectArea = Util.instence(this).$(this, R.id.lly_select_area);
        mLlyNoSchedule = Util.instence(this).$(this, R.id.lly_no_schedule);
        xlvScheduleList = Util.instence(this).$(this, R.id.xlv_schedule_list);
        mTvChoseCoach = Util.instence(this).$(this, R.id.tv_chose_coach);
        mSrlNoSchedule = Util.instence(this).$(this, R.id.srl_no_schedule);
        mSvNoSchedule = Util.instence(this).$(this, R.id.sv_no_schedule);
        xlvScheduleList.setPullRefreshEnable(true);
        xlvScheduleList.setPullLoadEnable(true);
        xlvScheduleList.setAutoLoadEnable(true);
        xlvScheduleList.setXListViewListener(this);
        xlvScheduleList.setRefreshTime(getTime());
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
        mTvCoachSchedule.setOnClickListener(mClickListener);
        mTvMySchedule.setOnClickListener(mClickListener);
        mTvChoseCoach.setOnClickListener(mClickListener);
        mSrlNoSchedule.setOnRefreshListener(mRefreshListener);
        mSrlNoSchedule.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!isRefresh) {
                isRefresh = true;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mSrlNoSchedule.setRefreshing(false);
                        getScheduleList();
                        isRefresh = false;
                    }
                }, 1500);
            }
        }
    };

    View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lly_tab_find_coach:
                    Intent intent = new Intent(getApplication(), FindCoachActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.lly_tab_index:
                    intent = new Intent(getApplication(), IndexActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.lly_tab_my_setting:
                    intent = new Intent(getApplication(), MySettingActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.tv_ap_coach_schedule:
                    booked = "0";
                    if (!TextUtils.isEmpty(mCurrentCoachId)) {
                        getScheduleList();
                    }
                    refreshUI();
                    break;
                case R.id.tv_ap_my_schedule:
                    booked = "1";
                    if (!TextUtils.isEmpty(mCurrentCoachId)) {
                        getScheduleList();
                    }
                    refreshUI();
                    break;
                case R.id.tv_chose_coach:
                    intent = new Intent(getApplication(), FindCoachActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;

            }
        }
    };

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private void loadDatas() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        mUser = spUtil.getUser();
        if (mUser != null) {
            if (null != mUser.getSession()) {
                mAccessToken = mUser.getSession().getAccess_token();
            }
            if (null != mUser.getStudent()) {
                mCurrentCoachId = mUser.getStudent().getCurrent_coach_id();
            }
            mBannerHightList = spUtil.getConstants().getBanner_highlights();
            for (int i = 0; i < 5; i++) {
                mLoopBannerHightList.add(mBannerHightList.get(i));
                loopIndex++;
            }
            mLoopStudentAdapter = new LoopStudentAdapter(AppointmentActivity.this, mLoopBannerHightList, R.layout.adapter_loop_student_schedule);
            mLvLoopStudent.setAdapter(mLoopStudentAdapter);
            if (!TextUtils.isEmpty(mCurrentCoachId)) {
                getScheduleList();
            }
        }
    }

    private void refreshUI() {
        if (!TextUtils.isEmpty(mCurrentCoachId)) {
            mRlyNoCoach.setVisibility(View.GONE);
            mLlyHasCoach.setVisibility(View.VISIBLE);
            if (booked.equals("0")) {
                mTvCoachSchedule.setTextColor(ContextCompat.getColor(AppointmentActivity.this, R.color.haha_white));
                mTvMySchedule.setTextColor(ContextCompat.getColor(AppointmentActivity.this, R.color.haha_white_light));
            } else if (booked.equals("1")) {
                mTvCoachSchedule.setTextColor(ContextCompat.getColor(AppointmentActivity.this, R.color.haha_white_light));
                mTvMySchedule.setTextColor(ContextCompat.getColor(AppointmentActivity.this, R.color.haha_white));
            }
            if (scheduleList != null && scheduleList.size() > 0) {
                mLlyNoSchedule.setVisibility(View.GONE);
                mSrlNoSchedule.setVisibility(View.GONE);
                mSvNoSchedule.setVisibility(View.GONE);
                xlvScheduleList.setVisibility(View.VISIBLE);
            } else {
                mLlyNoSchedule.setVisibility(View.VISIBLE);
                mSrlNoSchedule.setVisibility(View.VISIBLE);
                mSvNoSchedule.setVisibility(View.VISIBLE);
                mSrlNoSchedule.setOnRefreshListener(mRefreshListener);
                xlvScheduleList.setVisibility(View.GONE);
            }
        } else {
            //没有教练
            mRlyNoCoach.setVisibility(View.VISIBLE);
            mLlyHasCoach.setVisibility(View.GONE);
            booked = "0";
            //提醒选择教练
            mAlertDialog = new BaseAlertDialog(AppointmentActivity.this, "", "您还没有选择教练哦~", "快去寻找教练，开启快乐学车之旅吧！");
            mAlertDialog.show();
            if (mBannerHightList != null && mBannerHightList.size() > 5) {
                mHandler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    public void onRefresh() {
        isOnLoadMore = false;
        getScheduleList();
        if (TextUtils.isEmpty(linkNext)) {
            xlvScheduleList.setPullLoadEnable(false);
        } else {
            xlvScheduleList.setPullLoadEnable(true);
        }
    }

    @Override
    public void onLoadMore() {
        if (!TextUtils.isEmpty(linkNext) && !isOnLoadMore) {
            isOnLoadMore = true;
            getScheduleList(linkNext);
        } else {
            onLoad();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }

    static class MyHandler extends Handler {
        private final WeakReference<AppointmentActivity> mActivity;

        public MyHandler(AppointmentActivity activity) {
            mActivity = new WeakReference<AppointmentActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final AppointmentActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 1) {
                    activity.mLoopBannerHightList.remove(activity.mLoopBannerHightList.size() - 1);
                    if (++activity.loopIndex == activity.mBannerHightList.size()) {
                        activity.loopIndex = 0;
                    }
                    activity.mLoopBannerHightList.add(0, activity.mBannerHightList.get(activity.loopIndex));
                    activity.mLoopStudentAdapter.notifyDataSetChanged();
                    activity.mHandler.sendEmptyMessageDelayed(1, 3000);
                }
            }
        }
    }

    private void getScheduleList() {
        this.apPresenter.fetchCourseSchedule(mUser.getStudent().getId(), page, per_page, booked, mAccessToken, new BaseCallbackListener<ScheduleEventListResponse>() {
            @Override
            public void onSuccess(ScheduleEventListResponse data) {
                scheduleList = data.getData();
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
                if (TextUtils.isEmpty(linkNext)) {
                    xlvScheduleList.setPullLoadEnable(false);
                } else {
                    xlvScheduleList.setPullLoadEnable(true);
                }
                groupScheduleList(scheduleList);
                mAdapter = new ScheduleAdapter(AppointmentActivity.this, scheduleList, R.layout.adapter_schedule_event, booked, mUser, new ScheduleAdapter.onRefreshActivityUIListener() {
                    @Override
                    public void refreshActivityUI() {
                        loadDatas();
                    }
                });
                xlvScheduleList.setAdapter(mAdapter);
                onLoad();
                refreshUI();
            }

            @Override
            public void onFailure(String errorEvent, String message) {

            }
        });
    }

    private void getScheduleList(String url) {
        this.apPresenter.fetchCourseSchedule(url, mAccessToken, new BaseCallbackListener<ScheduleEventListResponse>() {
            @Override
            public void onSuccess(ScheduleEventListResponse data) {
                ArrayList<ScheduleEvent> newScheduleList = data.getData();
                if (newScheduleList != null && newScheduleList.size() > 0) {
                    scheduleList.addAll(newScheduleList);
                }
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
                if (TextUtils.isEmpty(linkNext)) {
                    xlvScheduleList.setPullLoadEnable(false);
                } else {
                    xlvScheduleList.setPullLoadEnable(true);
                }
                groupScheduleList(scheduleList);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter = new ScheduleAdapter(AppointmentActivity.this, scheduleList, R.layout.adapter_schedule_event, booked, mUser, new ScheduleAdapter.onRefreshActivityUIListener() {
                        @Override
                        public void refreshActivityUI() {
                            loadDatas();
                        }
                    });
                    xlvScheduleList.setAdapter(mAdapter);
                }
                isOnLoadMore = false;
                onLoad();
            }

            @Override
            public void onFailure(String errorEvent, String message) {

            }
        });
    }

    private void onLoad() {
        xlvScheduleList.stopRefresh();
        xlvScheduleList.stopLoadMore();
        xlvScheduleList.setRefreshTime(getTime());
    }

    private void groupScheduleList(ArrayList<ScheduleEvent> scheduleList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<String> dateStringList = new ArrayList<>();
        Collections.sort(scheduleList, comparator);
        try {
            for (ScheduleEvent scheduleEvent : scheduleList) {
                String scheduleDay = sdfDay.format(sdf.parse(scheduleEvent.getStart_time()));
                if (dateStringList.contains(scheduleDay)) {
                    scheduleEvent.setIsShowDay(false);
                } else {
                    scheduleEvent.setIsShowDay(true);
                    dateStringList.add(scheduleDay);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    Comparator<ScheduleEvent> comparator = new Comparator<ScheduleEvent>() {
        public int compare(ScheduleEvent s1, ScheduleEvent s2) {
            return s1.getStart_time().compareTo(s2.getStart_time());
        }
    };
}
