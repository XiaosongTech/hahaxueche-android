package com.hahaxueche.ui.fragment.myPage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.course.ScheduleEvent;
import com.hahaxueche.presenter.myPage.ScheduleListPresenter;
import com.hahaxueche.ui.adapter.myPage.ScheduleEventAdapter;
import com.hahaxueche.ui.dialog.BaseAlertSimpleDialog;
import com.hahaxueche.ui.dialog.BaseConfirmDialog;
import com.hahaxueche.ui.dialog.myPage.ScoreCoachDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.myPage.ScheduleListView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/5.
 */

public class ScheduleListFragment extends HHBaseFragment implements ScheduleListView, XListView.IXListViewListener {
    private int mBooked;
    private HHBaseApplication application;
    private ScheduleListPresenter mPresenter;
    @BindView(R.id.xlv_schedule_list)
    XListView mXlvSchedules;
    @BindView(R.id.lly_empty)
    LinearLayout mLlyEmpty;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    private ScheduleEventAdapter mScheduleAdapter;
    private ArrayList<ScheduleEvent> mScheduleEvents;

    public static ScheduleListFragment newInstance(int booked) {
        ScheduleListFragment fragment = new ScheduleListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("booked", booked);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ScheduleListPresenter();
        Bundle bundle = getArguments();
        mBooked = bundle.getInt("booked", 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_list, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        mPresenter.setBooked(mBooked);
        mXlvSchedules.setPullRefreshEnable(true);
        mXlvSchedules.setPullLoadEnable(true);
        mXlvSchedules.setAutoLoadEnable(true);
        mXlvSchedules.setXListViewListener(this);
        mXlvSchedules.setEmptyView(mLlyEmpty);
        mPresenter.fetchSchedules();
        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setPullLoadEnable(boolean enable) {
        mXlvSchedules.setPullLoadEnable(enable);
    }

    @Override
    public void refreshScheduleList(ArrayList<ScheduleEvent> scheduleEvents) {
        mScheduleEvents = scheduleEvents;
        mPresenter.groupScheduleList(mScheduleEvents);
        mScheduleAdapter = new ScheduleEventAdapter(getContext(), mScheduleEvents, mPresenter);
        mXlvSchedules.setAdapter(mScheduleAdapter);
        mXlvSchedules.stopRefresh();
        mXlvSchedules.stopLoadMore();
    }

    @Override
    public void addMoreScheduleList(ArrayList<ScheduleEvent> scheduleEvents) {
        mScheduleEvents.addAll(scheduleEvents);
        mPresenter.groupScheduleList(mScheduleEvents);
        mScheduleAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showBookDialog(String day, String startTime, String endTime, String courseName, final String scheduleEventId) {
        BaseConfirmDialog dialog = new BaseConfirmDialog(getContext(), "预约课程",
                "您预约了课程", "日期：" + day + "\n时间：" + startTime + "-" + endTime + "\n科目：" + courseName,
                "您最多只能预约2节课, 确定预约这节课吗？",
                "确认", "取消", new BaseConfirmDialog.onClickListener() {
            @Override
            public void clickConfirm() {
                mPresenter.bookSchedule(scheduleEventId);
            }

            @Override
            public void clickCancel() {
            }
        });
        dialog.show();
    }

    @Override
    public void showCancelDialog(String day, String startTime, String endTime, String courseName, final String scheduleEventId) {
        BaseConfirmDialog dialog = new BaseConfirmDialog(getContext(), "取消课程", "您是否要取消课程？",
                "日期：" + day + "\n时间：" + startTime + "-" + endTime + "\n科目：" + courseName, "",
                "暂不取消", "取消课程", new BaseConfirmDialog.onClickListener() {
            @Override
            public void clickConfirm() {
            }

            @Override
            public void clickCancel() {
                mPresenter.cancelSchedule(scheduleEventId);
            }
        });
        dialog.show();
    }

    @Override
    public void showReviewDialog(final String scheduleEventId) {
        ScoreCoachDialog dialog = new ScoreCoachDialog(getContext(), new ScoreCoachDialog.onScoreListener() {
            @Override
            public void onScore(float score) {
                mPresenter.reviewSchedule(scheduleEventId, score);
            }
        });
        dialog.show();
    }

    @Override
    public void showUnFinishCourseDialog() {
        BaseAlertSimpleDialog baseAlertDialog = new BaseAlertSimpleDialog(getContext(), "您还有未完成课程", "您的课程列表还有2节以上未完成的课程，请课程完成后再预约新课程。");
        baseAlertDialog.show();
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchSchedules();
    }

    @Override
    public void onLoadMore() {
        mPresenter.addMoreSchedulees();
    }
}
