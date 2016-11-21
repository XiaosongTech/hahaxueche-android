package com.hahaxueche.util;

import com.hahaxueche.model.examLib.Question;

import java.util.ArrayList;

/**
 * Created by wangshirui on 16/8/10.
 */
public class ExamLib {

    public static final String EXAM_TYPE_1 = "科目一";
    public static final String EXAM_TYPE_4 = "科目四";

    public static final String QUESTION_TYPE_TRUE_FALSE = "判断题";
    public static final String QUESTION_TYPE_SINGLE_CHOICE = "单选题";
    public static final String QUESTION_TYPE_MULTI_CHOICE = "多选题";

    public static final String TEST_MODE_TURN = "TURN";
    public static final String TEST_MODE_RANDOM = "RANDOM";
    public static final String TEST_MODE_MOCK_EXAM = "MOCK_EXAM";
    public static final String TEST_MODE_MY_LIB = "MY_LIB";
    public static final String TEST_MODE_CHECK_WRONG = "CHECK_WRONG";

    public static final String EXAM_STATUS_READY = "ready";
    public static final String EXAM_STATUS_PASS = "pass";
    public static final String EXAM_STATUS_NOT_PASS = "not_pass";

    public static final String EXAM_TIME_1_LABEL = "45分钟";
    public static final String EXAM_TIME_4_LABEL = "30分钟";

    public static final int EXAM_TIME_1 = 45 * 60;
    public static final int EXAM_TIME_4 = 30 * 60;

    public static final int QUESTION_NUMBER_EXAM1 = 100;
    public static final int QUESTION_NUMBER_EXAM4 = 50;

    private String mExamType;
    private int mQuestionCount;//考试题目数量
    private int mAllowWrongNumber;//可以错误的题数
    private ArrayList<Question> mWrongQuestionList;//错题列表
    private ArrayList<String> mAnsweredQuestionList;//已回答问题列表
    private boolean isContinue;//是否继续答题

    public ExamLib() {

    }

    public ExamLib(String examType) {
        mExamType = examType;
        if (mExamType.equals(EXAM_TYPE_1)) {
            mQuestionCount = QUESTION_NUMBER_EXAM1;
            mAllowWrongNumber = 10;
        } else {
            mQuestionCount = QUESTION_NUMBER_EXAM4;
            mAllowWrongNumber = 5;
        }
        mWrongQuestionList = new ArrayList<>();
        mAnsweredQuestionList = new ArrayList<>();
    }

    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    public void addAnsweredQuestion(String id) {
        if (!mAnsweredQuestionList.contains(id)) {
            mAnsweredQuestionList.add(id);
        }
    }

    public void addWrongQuestion(Question question) {
        if (!mWrongQuestionList.contains(question)) {
            mWrongQuestionList.add(question);
        }
    }

    public ArrayList<Question> getmWrongQuestionList() {
        return mWrongQuestionList;
    }


    /**
     * 是否显示继续答题提示
     *
     * @return
     */
    public boolean isShowContinueDialog() {
        return mWrongQuestionList.size() > mAllowWrongNumber && !isContinue;
    }

    /**
     * 继续答题提示
     *
     * @return
     */
    public String getContinueDialogHints() {
        return "目前您已经答错了" + mQuestionCount + "道题中的" + mWrongQuestionList.size() + "道,低于合格标准,是否继续答题?";
    }

    /**
     * 是否显示强制提交提示
     *
     * @return
     */
    public boolean isShowForceSubmitDialog() {
        return mAnsweredQuestionList.size() < mQuestionCount;
    }

    /**
     * 强制提交试卷提示
     *
     * @return
     */
    public String getForceSubmitDialogHints() {
        return "目前您已经回答了" + mQuestionCount + "道题中的" + mAnsweredQuestionList.size() + "道,还有" + (mQuestionCount - mAnsweredQuestionList.size()) + "道没作答,是否交卷?";
    }

    /**
     * 计算得分
     *
     * @return
     */
    public String getScore() {
        int rightCount = mAnsweredQuestionList.size() - mWrongQuestionList.size();//回答的-答错的
        int score = rightCount * (mExamType.equals(EXAM_TYPE_1) ? 1 : 2);
        return score + "分";
    }

    public String getUsedTime(int timeLeft) {
        int allTime = mExamType.endsWith(EXAM_TYPE_1) ? EXAM_TIME_1 : EXAM_TIME_4;
        return (allTime - timeLeft) / 60 + "分钟";
    }

    public String getExamStatus() {
        if (mExamType.equals(EXAM_TYPE_1)) {
            return (mQuestionCount - mAnsweredQuestionList.size() + mWrongQuestionList.size() > 10) ? EXAM_STATUS_NOT_PASS : EXAM_STATUS_PASS;
        } else {
            return (mQuestionCount - mAnsweredQuestionList.size() + mWrongQuestionList.size() > 5) ? EXAM_STATUS_NOT_PASS : EXAM_STATUS_PASS;
        }
    }

    public boolean isAnsweredAll() {
        return mAnsweredQuestionList != null && mAnsweredQuestionList.size() > 0 && mAnsweredQuestionList.size() == mQuestionCount;
    }

    /**
     * 已全部答题,提示
     *
     * @return
     */
    public String getAllAnsweredHints() {
        return "目前您已经回答了" + mAnsweredQuestionList.size() + "道题,马上交卷查看成绩?";
    }
}
