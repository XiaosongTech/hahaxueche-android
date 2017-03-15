package com.hahaxueche.presenter.community;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.examLib.Question;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.ExamView;
import com.hahaxueche.util.ExamLib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshirui on 2016/12/2.
 */

public class ExamPresenter extends HHBasePresenter implements Presenter<ExamView> {
    private ExamView mView;
    private HHBaseApplication application;

    public void attachView(ExamView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
    }

    public void detachView() {
        this.mView = null;
        application = null;
    }

    public ArrayList<Question> getQuestions(String examType) {
        Gson gson = new Gson();
        return gson.fromJson(getJson(mView.getContext(),
                ExamLib.EXAM_TYPE_1.equals(examType) ? "course1.json" : "course4.json"),
                new TypeToken<List<Question>>() {
                }.getType());
    }

    private String getJson(Context mContext, String fileName) {
        StringBuilder sb = new StringBuilder();
        AssetManager am = mContext.getAssets();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    am.open(fileName)));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }
        } catch (IOException e) {
            e.printStackTrace();
            sb.delete(0, sb.length());
        }
        return sb.toString().trim();
    }
}
