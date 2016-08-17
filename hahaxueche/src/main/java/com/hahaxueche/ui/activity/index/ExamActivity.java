package com.hahaxueche.ui.activity.index;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by wangshirui on 16/8/13.
 */
public class ExamActivity extends IndexBaseActivity implements ExamFragment.OnCollectRemoveListener, ExamFragment.OnMockExamAnsweredListener {
    private ImageButton mIbtnBack;
    private TextView mTvTitle;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private TextView mTvPrevious;
    private TextView mTvNext;
    private TextView mTvPage;
    private ImageView mIvSubmitExam;

    private ArrayList<Question> mQuestionList = new ArrayList<>();
    private int mCurrentPosition;
    private int mPageSize;
    private Intent mIntent;

    private SharedPreferencesUtil spUtil;
    private String mExamType;
    private String mExamMode;
    private ExamLib mExamLib;

    private int examCountDownTime;
    private final MyHandler mHandler = new MyHandler(this);

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
        mExamLib = new ExamLib(mExamType);
        if (mExamMode.equals(ExamLib.TEST_MODE_CHECK_WRONG)) {
            try {
                Bundle bundle = mIntent.getExtras();
                mQuestionList = (ArrayList<Question>) bundle.getSerializable("wrongQuestionList");
            } catch (Exception e) {
                Toast.makeText(ExamActivity.this, "加载试题失败,请重试", Toast.LENGTH_SHORT).show();
            }
        } else {
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
                    int questionNumber = mExamType.equals(ExamLib.EXAM_TYPE_1) ? ExamLib.QUESTION_NUMBER_EXAM1 : ExamLib.QUESTION_NUMBER_EXAM4;
                    for (int i = 0; i < questionNumber; i++) {
                        mQuestionList.add(questions.get(i));
                    }
                } else if (mExamMode.equals(ExamLib.TEST_MODE_MY_LIB)) {
                    mQuestionList = spUtil.getCollectList(questions, mExamType);
                }
            } else {
                Toast.makeText(ExamActivity.this, "加载试题失败,请重试", Toast.LENGTH_SHORT).show();
            }
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
        mIvSubmitExam = Util.instence(this).$(this, R.id.iv_submit_exam);
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
        mIvSubmitExam.setOnClickListener(mClickListener);

    }

    private void loadDatas() {
        mCurrentPosition = 0;
        mPageSize = mQuestionList.size();
        if (mExamMode.equals(ExamLib.TEST_MODE_RANDOM)) {
            mTvTitle.setText("随机练题");
            mIvSubmitExam.setVisibility(View.GONE);
        } else if (mExamMode.equals(ExamLib.TEST_MODE_MOCK_EXAM)) {
            mTvTitle.setText("模拟考试");
            mIvSubmitExam.setVisibility(View.VISIBLE);
            if (mExamType.equals(ExamLib.EXAM_TYPE_1)) {
                examCountDownTime = ExamLib.EXAM_TIME_1;
            } else {
                examCountDownTime = ExamLib.EXAM_TIME_4;
            }
            mHandler.sendEmptyMessage(1);
        } else if (mExamMode.equals(ExamLib.TEST_MODE_MY_LIB)) {
            mTvTitle.setText("我的题库");
            mIvSubmitExam.setVisibility(View.GONE);
        } else if (mExamMode.equals(ExamLib.TEST_MODE_CHECK_WRONG)) {
            mIvSubmitExam.setVisibility(View.GONE);
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
                case R.id.iv_submit_exam:
                    submitExam();
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

    static class MyHandler extends Handler {
        private final WeakReference<ExamActivity> mActivity;

        public MyHandler(ExamActivity activity) {
            mActivity = new WeakReference<ExamActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ExamActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 1) {
                    if (activity.examCountDownTime-- > 0) {
                        activity.mTvTitle.setText("模拟考试  " + activity.examCountDownTime / 60 + ":" + activity.examCountDownTime % 60);
                        activity.mHandler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        activity.mHandler.sendEmptyMessage(2);
                    }
                } else if (msg.what == 2) {
                    activity.autoSubmit();
                }
            }
        }
    }

    /**
     * 时间到自动提交
     */
    private void autoSubmit() {
        submit();
        Toast.makeText(context, "时间到,已自动提交!", Toast.LENGTH_LONG).show();
    }

    /**
     * 点交卷事件
     */
    private void submitExam() {
        if (mExamLib.isShowForceSubmitDialog()) {//有未回答的问题,强制提交
            BaseConfirmSimpleDialog baseConfirmSimpleDialog = new BaseConfirmSimpleDialog(ExamActivity.this, "交卷提示",
                    mExamLib.getForceSubmitDialogHints(), "确认交卷", "继续做题", new BaseConfirmSimpleDialog.onConfirmListener() {
                @Override
                public boolean clickConfirm() {
                    submit();
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
            submit();
        }
    }

    /**
     * 交卷
     */
    private void submit() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        Log.v("gibxin","1111 wrongQuestionList-> "+mExamLib.getmWrongQuestionList().size());
        bundle.putSerializable("wrongQuestionList", mExamLib.getmWrongQuestionList());
        intent.putExtras(bundle);
        intent.putExtra("score", mExamLib.getScore());
        intent.putExtra("time", mExamLib.getUsedTime(examCountDownTime));
        intent.putExtra("status", mExamLib.getExamStatus());
        setResult(RESULT_OK, intent);
        ExamActivity.this.finish();
    }

    /**
     * exam fragment answered question
     *
     * @param question
     */
    @Override
    public void answer(Question question) {
        if (mExamLib == null) return;
        mExamLib.addAnsweredQuestion(question.getId());
        if (!question.isCorrect()) {
            mExamLib.addWrongQuestion(question);
            if (mExamLib.isShowContinueDialog()) {//错题超过及格线,提示提交
                BaseConfirmSimpleDialog baseConfirmSimpleDialog = new BaseConfirmSimpleDialog(ExamActivity.this, "交卷提示",
                        mExamLib.getContinueDialogHints(), "确认交卷", "继续做题", new BaseConfirmSimpleDialog.onConfirmListener() {
                    @Override
                    public boolean clickConfirm() {
                        submit();
                        return true;
                    }
                }, new BaseConfirmSimpleDialog.onCancelListener() {
                    @Override
                    public boolean clickCancel() {
                        mExamLib.setContinue(true);
                        return true;
                    }
                });
                baseConfirmSimpleDialog.show();
            }
        }
    }
}
