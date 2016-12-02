package com.hahaxueche.presenter.community;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.ExamView;

/**
 * Created by wangshirui on 2016/12/2.
 */

public class ExamPresenter implements Presenter<ExamView> {
    private ExamView mExamView;
    private HHBaseApplication application;

    public void attachView(ExamView view) {
        this.mExamView = view;
        application = HHBaseApplication.get(mExamView.getContext());
    }

    public void detachView() {
        this.mExamView = null;
        application = null;
    }
}
