package com.hahaxueche.ui.activity.community;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.examLib.Question;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.community.ExamPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.dialog.BaseConfirmSimpleDialog;
import com.hahaxueche.ui.dialog.community.ExamSubmitAlertDialog;
import com.hahaxueche.ui.fragment.community.ExamFragment;
import com.hahaxueche.ui.view.community.ExamView;
import com.hahaxueche.util.ExamLib;
import com.hahaxueche.util.SharedPrefUtil;
import com.hahaxueche.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ExamActivity extends HHBaseActivity implements ExamFragment.OnCollectRemoveListener, ExamFragment.OnMockExamAnsweredListener, ExamView {
    private ImageView mIvBack;
    private TextView mTvTitle;
    private ImageView mIvSubmitExam;
    @BindView(R.id.pager)
    ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    @BindView(R.id.tv_previous)
    TextView mTvPrevious;
    @BindView(R.id.tv_next)
    TextView mTvNext;
    @BindView(R.id.tv_page)
    TextView mTvPage;
    private ExamPresenter mPresenter;

    private ArrayList<Question> mQuestionList = new ArrayList<>();
    private int mCurrentPosition;
    private int mPageSize;
    private Intent mIntent;

    private SharedPrefUtil spUtil;
    private String mExamType;
    private String mExamMode;
    private ExamLib mExamLib;

    private int examCountDownTime;
    private final MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ExamPresenter();
        setContentView(R.layout.activity_exam);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        spUtil = new SharedPrefUtil(getContext());
        initActionBar();
        initQuestionList();
        initViews();
        initEvents();
        loadDatas();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_exam);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        mIvSubmitExam = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_submit_exam);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExamActivity.this.finish();
            }
        });
        mIvSubmitExam.setOnClickListener(mClickListener);
    }

    private void initQuestionList() {
        mIntent = getIntent();
        mExamType = mIntent.getStringExtra("examType");
        mExamMode = mIntent.getStringExtra("examMode");
        mExamLib = new ExamLib(mExamType);
        if (mExamMode.equals(ExamLib.TEST_MODE_CHECK_WRONG)) {
            try {
                mQuestionList = mIntent.getParcelableArrayListExtra("wrongQuestionList");
            } catch (Exception e) {
                Toast.makeText(ExamActivity.this, "加载试题失败,请重试", Toast.LENGTH_SHORT).show();
            }
        } else {
            ArrayList<Question> questions = mPresenter.getQuestions(mExamType);
            if (questions != null && questions.size() > 0) {
                ArrayList<String> urls = new ArrayList<>();
                for (Question question : questions) {
                    String explains = question.explain;
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
        //清除已回答的答案
        if (mQuestionList != null && mQuestionList.size() > 0) {
            for (Question question : mQuestionList) {
                question.userAnswer = null;
            }
        }
    }

    private void initViews() {
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
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
            mTvTitle.setText("查看错题");
            mIvSubmitExam.setVisibility(View.GONE);
        } else if (mExamMode.equals(ExamLib.TEST_MODE_TURN)) {
            mTvTitle.setText("顺序练题");
            mIvSubmitExam.setVisibility(View.GONE);
        }
        if (mExamMode.equals(ExamLib.TEST_MODE_TURN)) {//顺序联系,提示是否继续上次位置
            final int lastPos = spUtil.getExamPosition(mExamType);
            if (lastPos > 0) {
                BaseConfirmSimpleDialog baseConfirmSimpleDialog = new BaseConfirmSimpleDialog(ExamActivity.this, "提示", "上次练习到" + (lastPos + 1) + "题,是否继续?",
                        "继续上次", "重新开始", new BaseConfirmSimpleDialog.onClickListener() {
                    @Override
                    public void clickConfirm() {
                        mCurrentPosition = lastPos + 1;
                        mPager.setCurrentItem(mCurrentPosition);
                    }

                    @Override
                    public void clickCancel() {
                        spUtil.clearExamPosition(mExamType);
                    }
                });
                baseConfirmSimpleDialog.show();
            }
        }
        setPageText();
    }

    private void setPageText() {
        String text = (mCurrentPosition + 1) + "/" + mPageSize;
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.haha_gray_dark)), 0, text.indexOf("/") + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(Utils.instence(getContext()).sp2px(22)), 0, text.indexOf("/") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.haha_gray_text)), text.indexOf("/") + 1, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(Utils.instence(getContext()).sp2px(18)), text.indexOf("/") + 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvPage.setText(ss);
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
                case R.id.tv_next:
                    if (mCurrentPosition == mPageSize - 1) {
                        if (mExamMode.equals(ExamLib.TEST_MODE_MOCK_EXAM)) {
                            //模拟考试,要提示交卷
                            submitExam();
                        } else {
                            //其他模式,提示是否从头开始
                            BaseConfirmSimpleDialog baseConfirmSimpleDialog = new BaseConfirmSimpleDialog(ExamActivity.this, "提示", "已经是最后一题,是否从头开始?",
                                    "从头开始", "再看看", new BaseConfirmSimpleDialog.onClickListener() {
                                @Override
                                public void clickConfirm() {
                                    for (Question question : mQuestionList) {
                                        question.userAnswer = null;
                                    }
                                    spUtil.clearExamPosition(mExamType);
                                    mCurrentPosition = 0;
                                    mPager.setCurrentItem(mCurrentPosition);
                                }

                                @Override
                                public void clickCancel() {

                                }
                            });
                            baseConfirmSimpleDialog.show();
                        }
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
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ExamActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 1) {
                    if (activity.examCountDownTime-- > 0) {
                        String time = activity.examCountDownTime / 60 + ":" + ((activity.examCountDownTime % 60 > 9) ? String.valueOf(activity.examCountDownTime % 60) : "0" + String.valueOf(activity.examCountDownTime % 60));
                        activity.mTvTitle.setText("模拟考试  " + time);
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
        Toast.makeText(getContext(), "时间到,已自动提交!", Toast.LENGTH_LONG).show();
    }

    /**
     * 点交卷事件
     */
    private void submitExam() {
        if (mExamLib.isShowForceSubmitDialog()) {//有未回答的问题,强制提交
            BaseConfirmSimpleDialog baseConfirmSimpleDialog = new BaseConfirmSimpleDialog(ExamActivity.this, "交卷提示",
                    mExamLib.getForceSubmitDialogHints(), "确认交卷", "继续做题", new BaseConfirmSimpleDialog.onClickListener() {
                @Override
                public void clickConfirm() {
                    submit();
                }

                @Override
                public void clickCancel() {
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
        mExamLib.addAnsweredQuestion(question.question_id);
        if (mExamLib.isAnsweredAll()) {//已经回答完全部问题
            ExamSubmitAlertDialog alertDialog = new ExamSubmitAlertDialog(ExamActivity.this, mExamLib.getAllAnsweredHints(), new ExamSubmitAlertDialog.onConfirmListener() {
                @Override
                public boolean clickConfirm() {
                    submit();
                    return true;
                }
            });
            alertDialog.show();
        } else {
            if (!question.isCorrect()) {
                mExamLib.addWrongQuestion(question);
                if (mExamLib.isShowContinueDialog() && mExamMode != null && mExamMode.equals(ExamLib.TEST_MODE_MOCK_EXAM)) {//错题超过及格线,提示提交
                    BaseConfirmSimpleDialog baseConfirmSimpleDialog = new BaseConfirmSimpleDialog(ExamActivity.this, "交卷提示",
                            mExamLib.getContinueDialogHints(), "确认交卷", "继续做题", new BaseConfirmSimpleDialog.onClickListener() {
                        @Override
                        public void clickConfirm() {
                            submit();
                        }

                        @Override
                        public void clickCancel() {
                            mExamLib.setContinue(true);
                        }
                    });
                    baseConfirmSimpleDialog.show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void finish() {
        User user = spUtil.getUser();
        if ((mExamMode.equals(ExamLib.TEST_MODE_TURN) || mExamMode.equals(ExamLib.TEST_MODE_RANDOM)) && user.isLogin()) {
            Intent intent = new Intent();
            intent.putExtra("isShowShare", true);
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }
}
