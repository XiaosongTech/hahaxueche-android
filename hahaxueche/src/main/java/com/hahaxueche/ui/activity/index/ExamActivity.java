package com.hahaxueche.ui.activity.index;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hahaxueche.MyApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.examLib.Question;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.fragment.index.exam.ExamFragment;
import com.hahaxueche.utils.ExamLib;
import com.hahaxueche.utils.JsonUtils;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by wangshirui on 16/8/13.
 */
public class ExamActivity extends IndexBaseActivity implements ExamFragment.OnCollectRemoveListener {
    private ImageButton mIbtnBack;
    private TextView mTvTitle;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private TextView mTvPrevious;
    private TextView mTvNext;
    private TextView mTvPage;

    private ArrayList<Question> mQuestionList = new ArrayList<>();
    private int mCurrentPosition;
    private int mPageSize;
    private Intent mIntent;

    private SharedPreferencesUtil spUtil;
    private String mExamType;
    private String mExamMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        spUtil = new SharedPreferencesUtil(context);
        initQuestionList();
        initViews();
        initEvents();
        loadDatas();
    }

    private void initQuestionList() {
        mIntent = getIntent();
        mExamType = mIntent.getStringExtra("examType");
        mExamMode = mIntent.getStringExtra("examMode");
        ArrayList<Question> questions = null;
        try {
            Type type = new TypeToken<ArrayList<Question>>() {
            }.getType();
            if (mExamType.equals(ExamLib.EXAM_TYPE_1)) {
                questions = JsonUtils.deserialize(getJson("course1.txt"), type);
            } else {
                questions = JsonUtils.deserialize(getJson("course4.txt"), type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (questions != null && questions.size() > 0) {
            ArrayList<String> urls = new ArrayList<>();
            for (Question question : questions) {
                String explains = question.getExplains();
                if (explains.contains("http://")) {
                    String url = explains.substring(explains.indexOf("http://"), explains.indexOf("html") + 4);
                    if (!urls.contains(url)) {
                        urls.add(url);
                    }
                }
            }
            if (mExamMode.equals(ExamLib.TEST_MODE_TURN)) {
                mQuestionList = questions;
            } else if (mExamMode.equals(ExamLib.TEST_MODE_RANDOM)) {
                Collections.shuffle(questions);
                mQuestionList = questions;
            } else if (mExamMode.equals(ExamLib.TEST_MODE_MOCK_EXAM)) {
                Collections.shuffle(questions);
                mQuestionList.clear();
                for (int i = 0; i < 100; i++) {
                    mQuestionList.add(questions.get(i));
                }
            } else if (mExamMode.equals(ExamLib.TEST_MODE_MY_LIB)) {
                mQuestionList = spUtil.getCollectList(questions, mExamType);
            }
        } else {
            Toast.makeText(ExamActivity.this, "加载试题失败,请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        mPager = Util.instence(this).$(this, R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mTvTitle = Util.instence(this).$(this, R.id.tv_title);
        mTvPrevious = Util.instence(this).$(this, R.id.tv_previous);
        mTvNext = Util.instence(this).$(this, R.id.tv_next);
        mTvPage = Util.instence(this).$(this, R.id.tv_page);
    }

    private void initEvents() {
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                spUtil.setExamPosition(mExamType, mCurrentPosition);
                setPageText();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mIbtnBack.setOnClickListener(mClickListener);
        mTvNext.setOnClickListener(mClickListener);
        mTvPrevious.setOnClickListener(mClickListener);

    }

    private void loadDatas() {
        mCurrentPosition = 0;
        mPageSize = mQuestionList.size();
        if (mExamMode.equals(ExamLib.TEST_MODE_RANDOM)) {
            mTvTitle.setText("随机练题");
        } else if (mExamMode.equals(ExamLib.TEST_MODE_MOCK_EXAM)) {
            mTvTitle.setText("模拟考试");
        } else if (mExamMode.equals(ExamLib.TEST_MODE_MY_LIB)) {
            mTvTitle.setText("我的题库");
        }
        if (mExamMode.equals(ExamLib.TEST_MODE_TURN)) {//顺序联系,提示是否继续上次位置
            final int lastPos = spUtil.getExamPosition(mExamType);
            if (lastPos > 0) {
                BaseConfirmSimpleDialog baseConfirmSimpleDialog = new BaseConfirmSimpleDialog(ExamActivity.this, "提示", "上次练习到" + (lastPos + 1) + "题,是否继续",
                        "继续上次", "重新开始", new BaseConfirmSimpleDialog.onConfirmListener() {
                    @Override
                    public boolean clickConfirm() {
                        mCurrentPosition = lastPos + 1;
                        mPager.setCurrentItem(mCurrentPosition);
                        return true;
                    }
                }, new BaseConfirmSimpleDialog.onCancelListener() {
                    @Override
                    public boolean clickCancel() {
                        spUtil.clearExamPosition(mExamType);
                        return true;
                    }
                });
                baseConfirmSimpleDialog.show();
            }
        }
        setPageText();
    }

    private void setPageText() {
        mTvPage.setText((mCurrentPosition + 1) + "/" + mPageSize);
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ExamFragment.create(position, mQuestionList.get(position), mExamMode, mExamType);
        }

        @Override
        public int getCount() {
            return mQuestionList.size();
        }

    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ibtn_back:
                    ExamActivity.this.finish();
                    break;
                case R.id.tv_next:
                    if (mCurrentPosition == mPageSize - 1) {
                        BaseConfirmSimpleDialog baseConfirmSimpleDialog = new BaseConfirmSimpleDialog(ExamActivity.this, "提示", "已经是最后一题,是否从头开始?",
                                "从头开始", "再看看", new BaseConfirmSimpleDialog.onConfirmListener() {
                            @Override
                            public boolean clickConfirm() {
                                for (Question question : mQuestionList) {
                                    question.setUserAnswer(null);
                                }
                                spUtil.clearExamPosition(mExamType);
                                mCurrentPosition = 0;
                                mPager.setCurrentItem(mCurrentPosition);
                                return true;
                            }
                        }, new BaseConfirmSimpleDialog.onCancelListener() {
                            @Override
                            public boolean clickCancel() {
                                return true;
                            }
                        });
                        baseConfirmSimpleDialog.show();
                    } else {
                        mPager.setCurrentItem(mCurrentPosition + 1);
                    }
                    break;
                case R.id.tv_previous:
                    if (mCurrentPosition == 0) {
                        Toast.makeText(ExamActivity.this, "当前是第一题", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mPager.setCurrentItem(mCurrentPosition - 1);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 读取本地文件中JSON字符串
     *
     * @param fileName
     * @return
     */
    private String getJson(String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    getAssets().open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Override
    public void onCollectRemove(int position) {
        mQuestionList.remove(position);
        mPageSize = mQuestionList.size();
        if (position != mPageSize) {//不是最后一题,显示下一题
            mCurrentPosition = position;
        } else {
            if (position != 0) {//是最后一题显示上一题
                mCurrentPosition = position - 1;
            } else {
                //没有上一题,结束activity
                ExamActivity.this.finish();
            }
        }
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mCurrentPosition);
        setPageText();
    }
}
