package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.course.ScheduleEvent;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/11/5.
 */

public interface ScheduleListView extends HHBaseView {
    void setPullLoadEnable(boolean enable);

    void refreshScheduleList(ArrayList<ScheduleEvent> scheduleEvents);

    void addMoreScheduleList(ArrayList<ScheduleEvent> scheduleEvents);

    void showMessage(String message);

    void showBookDialog(String day, String startTime, String endTime, String courseName, String scheduleEventId);

    void showCancelDialog(String day, String startTime, String endTime, String courseName, String scheduleEventId);

    void showReviewDialog(String scheduleEventId);

    void showUnFinishCourseDialog();
}
