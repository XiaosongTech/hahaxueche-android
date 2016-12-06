package com.hahaxueche.presenter.community;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.examLib.Question;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.ExamView;
import com.hahaxueche.util.ExamLib;

import java.util.ArrayList;

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

    public ArrayList<Question> getQuestions(String examType) {
        return examType.equals(ExamLib.EXAM_TYPE_1) ? application.getQuestions1() : application.getQuestions4();
    }
}
