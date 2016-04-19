package com.hahaxueche.ui.activity.appointment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.R;
import com.hahaxueche.model.base.BannerHighlight;
import com.hahaxueche.model.coach.CoachModel;
import com.hahaxueche.model.student.StudentModel;
import com.hahaxueche.ui.activity.findCoach.FindCoachActivity;
import com.hahaxueche.ui.activity.index.IndexActivity;
import com.hahaxueche.ui.activity.mySetting.MySettingActivity;
import com.hahaxueche.ui.adapter.appointment.LoopStudentAdapter;
import com.hahaxueche.ui.dialog.BaseAlertDialog;
import com.hahaxueche.ui.fragment.appointment.AppointmentFragment;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by gibxin on 2016/1/27.
 */
public class AppointmentActivity extends Activity {
    private LinearLayout llyTabIndex;
    private LinearLayout llyTabFindCoach;
    private LinearLayout llyTabAppointment;
    private LinearLayout llyTabMySetting;
    private TextView mTvCoachSchedule;
    private TextView mTvMySchedule;
    private LinearLayout mLlySelectArea;
    private LinearLayout mLlyHasCoach;
    private RelativeLayout mRlyNoCoach;
    private StudentModel mStudent;
    private CoachModel mCurrentCoach;
    private String booked = "0";//0:教练将来的；1:自己已经booked的了 default to 0
    private BaseAlertDialog mAlertDialog;
    private ListView mLvLoopStudent;
    private LoopStudentAdapter mLoopStudentAdapter;
    private ArrayList<BannerHighlight> mBannerHightList;
    private ArrayList<BannerHighlight> mLoopBannerHightList = new ArrayList<>();
    private int loopIndex = 0;
    private final MyHandler mHandler = new MyHandler(this);

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
    }

    private void initEvent() {
        llyTabIndex.setOnClickListener(mClickListener);
        llyTabFindCoach.setOnClickListener(mClickListener);
        llyTabAppointment.setOnClickListener(mClickListener);
        llyTabMySetting.setOnClickListener(mClickListener);
        mTvCoachSchedule.setOnClickListener(mClickListener);
        mTvMySchedule.setOnClickListener(mClickListener);
    }

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
                    refreshUI();
                    break;
                case R.id.tv_ap_my_schedule:
                    booked = "1";
                    refreshUI();
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
        mStudent = spUtil.getStudent();
        mCurrentCoach = spUtil.getCurrentCoach();
        mBannerHightList = spUtil.getConstants().getBanner_highlights();
        for (int i = 0; i < 5; i++) {
            mLoopBannerHightList.add(mBannerHightList.get(i));
            loopIndex++;
        }
        mLoopStudentAdapter = new LoopStudentAdapter(AppointmentActivity.this, mLoopBannerHightList, R.layout.adapter_loop_student_schedule);
        mLvLoopStudent.setAdapter(mLoopStudentAdapter);
    }

    private void refreshUI() {
        if (mCurrentCoach != null) {
            mRlyNoCoach.setVisibility(View.GONE);
            mLlyHasCoach.setVisibility(View.VISIBLE);
            if (booked.equals("0")) {
                mTvCoachSchedule.setTextColor(ContextCompat.getColor(AppointmentActivity.this, R.color.haha_white));
                mTvMySchedule.setTextColor(ContextCompat.getColor(AppointmentActivity.this, R.color.haha_white_light));
            } else if (booked.equals("1")) {
                mTvCoachSchedule.setTextColor(ContextCompat.getColor(AppointmentActivity.this, R.color.haha_white_light));
                mTvMySchedule.setTextColor(ContextCompat.getColor(AppointmentActivity.this, R.color.haha_white));
            }
        } else {
            //没有教练
            mRlyNoCoach.setVisibility(View.VISIBLE);
            mLlyHasCoach.setVisibility(View.GONE);
            booked = "0";
            //提醒选择教练
            mAlertDialog = new BaseAlertDialog(AppointmentActivity.this, "", "您还没有选择教练哦~", "快去寻找教练，开启快乐学车之旅吧！");
            mAlertDialog.show();
            mHandler.sendEmptyMessage(1);
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
}
