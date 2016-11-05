package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.BaseModel;
import com.hahaxueche.model.base.BaseValid;
import com.hahaxueche.model.course.ScheduleEvent;
import com.hahaxueche.model.responseList.ScheduleEventResponseList;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Review;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.ScheduleListView;
import com.hahaxueche.util.ErrorUtil;
import com.hahaxueche.util.HHLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by wangshirui on 2016/11/5.
 */

public class ScheduleListPresenter implements Presenter<ScheduleListView> {
    private static final int PAGE = 1;
    private static final int PER_PAGE = 10;
    private ScheduleListView mScheduleListView;
    private Subscription subscription;
    private HHBaseApplication application;
    private String nextLink;
    private int booked = 0;//0:教练将来的；1:自己已经booked的了 default to 0

    public void attachView(ScheduleListView view) {
        this.mScheduleListView = view;
        application = HHBaseApplication.get(mScheduleListView.getContext());
    }

    public void detachView() {
        this.mScheduleListView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void setBooked(int booked) {
        this.booked = booked;
    }

    public int getBooked() {
        return booked;
    }

    public void fetchSchedules() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<ScheduleEventResponseList>>() {
                    @Override
                    public Observable<ScheduleEventResponseList> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.getSchedules(user.student.id, PAGE, PER_PAGE, booked, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ScheduleEventResponseList>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mScheduleListView.showProgressDialog("查找中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mScheduleListView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mScheduleListView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mScheduleListView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ScheduleEventResponseList responseList) {
                        if (responseList.data != null) {
                            mScheduleListView.refreshScheduleList(responseList.data);
                            nextLink = responseList.links.next;
                            mScheduleListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }
                    }
                });
    }

    public void addMoreSchedulees() {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<ScheduleEventResponseList>>() {
                    @Override
                    public Observable<ScheduleEventResponseList> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.getSchedules(nextLink, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ScheduleEventResponseList>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mScheduleListView.showProgressDialog("查找中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mScheduleListView.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mScheduleListView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mScheduleListView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ScheduleEventResponseList responseList) {
                        if (responseList.data != null) {
                            mScheduleListView.addMoreScheduleList(responseList.data);
                            nextLink = responseList.links.next;
                            mScheduleListView.setPullLoadEnable(!TextUtils.isEmpty(nextLink));
                        }
                    }
                });
    }

    public void groupScheduleList(ArrayList<ScheduleEvent> scheduleList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<String> dateStringList = new ArrayList<>();
        Collections.sort(scheduleList, comparator);
        try {
            for (ScheduleEvent scheduleEvent : scheduleList) {
                String scheduleDay = sdfDay.format(sdf.parse(scheduleEvent.start_time));
                if (dateStringList.contains(scheduleDay)) {
                    scheduleEvent.isShowDay = false;
                } else {
                    scheduleEvent.isShowDay = true;
                    dateStringList.add(scheduleDay);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    Comparator<ScheduleEvent> comparator = new Comparator<ScheduleEvent>() {
        public int compare(ScheduleEvent s1, ScheduleEvent s2) {
            return s1.start_time.compareTo(s2.start_time);
        }
    };

    public String getCourseName(int serviceType) {
        int cityId = 0;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.student != null) {
            cityId = user.student.city_id;
        }
        return application.getConstants().getCourseName(cityId, serviceType);
    }

    public void preBookSchedule(ScheduleEvent schedule) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = sdf.parse(schedule.start_time);
            Date endTime = sdf.parse(schedule.end_time);
            SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy年MM月dd日");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            mScheduleListView.showBookDialog(sdfDay.format(startTime), sdfTime.format(startTime), sdfTime.format(endTime), getCourseName(schedule.service_type), schedule.id);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void preCancelSchedule(ScheduleEvent schedule) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = sdf.parse(schedule.start_time);
            Date endTime = sdf.parse(schedule.end_time);
            SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy年MM月dd日");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            mScheduleListView.showCancelDialog(sdfDay.format(startTime), sdfTime.format(startTime), sdfTime.format(endTime), getCourseName(schedule.service_type), schedule.id);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void preReviewSchedule(ScheduleEvent schedule) {
        mScheduleListView.showReviewDialog(schedule.id);
    }

    public void bookSchedule(final String scheduleEventId) {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<ScheduleEvent>>() {
                    @Override
                    public Observable<ScheduleEvent> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.bookSchedule(user.student.id, scheduleEventId, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<ScheduleEvent>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mScheduleListView.showProgressDialog("预约中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mScheduleListView.dismissProgressDialog();
                        fetchSchedules();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mScheduleListView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mScheduleListView.forceOffline();
                        } else if (ErrorUtil.isHttp401(e)) {
                            mScheduleListView.showUnFinishCourseDialog();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(ScheduleEvent scheduleEvent) {
                    }
                });
    }

    public void cancelSchedule(final String scheduleEventId) {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<BaseModel>>() {
                    @Override
                    public Observable<BaseModel> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.cancelSchedule(user.student.id, scheduleEventId, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<BaseModel>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mScheduleListView.showProgressDialog("取消中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mScheduleListView.dismissProgressDialog();
                        fetchSchedules();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mScheduleListView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mScheduleListView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseModel baseModel) {
                    }
                });
    }

    public void reviewSchedule(final String scheduleEventId, final float score) {
        final User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) return;
        final HHApiService apiService = application.getApiService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("cell_phone", user.cell_phone);
        subscription = apiService.isValidToken(user.session.access_token, map)
                .flatMap(new Func1<BaseValid, Observable<Review>>() {
                    @Override
                    public Observable<Review> call(BaseValid baseValid) {
                        if (baseValid.valid) {
                            return apiService.reviewSchedule(user.student.id, scheduleEventId, score, user.session.access_token);
                        } else {
                            return application.getSessionObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<Review>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mScheduleListView.showProgressDialog("评价中，请稍后...");
                    }

                    @Override
                    public void onCompleted() {
                        mScheduleListView.dismissProgressDialog();
                        fetchSchedules();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mScheduleListView.dismissProgressDialog();
                        if (ErrorUtil.isInvalidSession(e)) {
                            mScheduleListView.forceOffline();
                        }
                        HHLog.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Review review) {
                    }
                });
    }


}