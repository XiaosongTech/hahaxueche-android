package com.hahaxueche.ui.activity.community;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.examLib.Question;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.community.ExamPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.dialog.ShareDialog;
import com.hahaxueche.util.ExamLib;
import com.hahaxueche.util.SharedPrefUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class StartExamActivity extends HHBaseActivity {
    private SharedPrefUtil spUtil;

    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.cir_my_avatar)
    private SimpleDraweeView mCirAvatar;
    @BindView(R.id.tv_exam_status)
    private TextView mTvExamStatus;//考试状态--姓名/未登录,通过,继续努力
    @BindView(R.id.tv_record_remarks)
    private TextView mTvRecordRemarks;//成绩说明
    @BindView(R.id.tv_exam_time)
    private TextView mTvExamTime;//考试时间
    @BindView(R.id.rly_exam_score)
    private RelativeLayout mRlyScore;//分数布局
    @BindView(R.id.tv_exam_score)
    private TextView mTvScore;
    @BindView(R.id.rly_pass_score)
    private RelativeLayout mRlyPassScore;//合格标准
    @BindView(R.id.rly_exam_standard)
    private RelativeLayout mRlyExamStandard;//出题规则
    @BindView(R.id.rly_exam_type)
    private RelativeLayout mRlyExamType;
    @BindView(R.id.tv_exam_type)
    private TextView mTvExamType;
    @BindView(R.id.tv_exam_rules)
    private TextView mTvExamRules;//记分规则说明
    @BindView(R.id.tv_start_exam)
    private TextView mTvStartExam;//开始考试按钮
    @BindView(R.id.tv_try_again)
    private TextView mTvTryAgain;//再次考试按钮
    @BindView(R.id.tv_return)
    private TextView mTvReturnHomepage;//返回首页按钮
    @BindView(R.id.tv_check_wrong)
    private TextView mTvCheckWrong;//查看错题按钮

    private String mExamType;
    private String mStatus = ExamLib.EXAM_STATUS_READY;//默认准备状态
    private Student mStudent;
    private String mExamTime;//考试时间
    private String mExamScore;//得分
    private ArrayList<Question> mWrongQuestionList;


    private static final String ARG_EXAM_TYPE = "examType";
    private static final String ARG_EXAM_SCORE = "examScore";
    private static final String ARG_EXAM_TIME = "examTime";
    private static final String ARG_WRONG_QUESTION_LIST = "wrongQuestionList";
    private static final String ARG_STATUS = "status";
    private static final int REQUEST_CODE_EXAM_ACTIVITY = 0;

    private ShareAppDialog mShareDialog;
    private ExamPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ExamPresenter();
        setContentView(R.layout.activity_start_exam);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        spUtil = new SharedPrefUtil(getContext());
        //登录用户,显示头像,姓名
        User user = spUtil.getUser();
        if (user != null && user.isLogin()) {
            mStudent = user.student;
        }
        initActionBar();
        initViews();
        initEvents();
        if (savedInstanceState != null) {
            mExamType = savedInstanceState.getString(ARG_EXAM_TYPE);
            mExamScore = savedInstanceState.getString(ARG_EXAM_SCORE);
            mExamTime = savedInstanceState.getString(ARG_EXAM_TIME);
            mWrongQuestionList = (ArrayList<Question>) savedInstanceState.getSerializable(ARG_WRONG_QUESTION_LIST);
            mStatus = savedInstanceState.getString(ARG_STATUS);
        } else {
            Intent intent = getIntent();
            mExamType = intent.getStringExtra("examType");
        }
        refreshUI();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnHomePage();
            }
        });
        mTvTitle.setText("模拟考试");
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        mShareDialog = null;
        super.onDestroy();
    }

    private void initViews() {
        mTvCheckWrong.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //添加下划线
    }

    private void initEvents() {
        mTvStartExam.setOnClickListener(mClickListener);
        mTvTryAgain.setOnClickListener(mClickListener);
        mTvReturnHomepage.setOnClickListener(mClickListener);
        mTvCheckWrong.setOnClickListener(mClickListener);
    }

    private void refreshUI() {
        if (mStudent != null && !TextUtils.isEmpty(mStudent.avatar)) {//头像
            mCirAvatar.setImageURI(mStudent.avatar);
        }
        if (mStatus.equals(ExamLib.EXAM_STATUS_PASS)) {
            //通过
            mTvExamStatus.setText("通过啦!");
            mTvRecordRemarks.setVisibility(View.VISIBLE);
            mTvRecordRemarks.setText(getText(R.string.passRemarks));
            mTvExamTime.setText(mExamTime);
            mRlyScore.setVisibility(View.VISIBLE);
            mTvScore.setText(mExamScore);
            mRlyPassScore.setVisibility(View.GONE);
            mRlyExamStandard.setVisibility(View.GONE);
            mRlyExamType.setVisibility(View.GONE);
            mTvExamRules.setVisibility(View.GONE);
            mTvStartExam.setVisibility(View.GONE);
            mTvTryAgain.setVisibility(View.VISIBLE);
            mTvTryAgain.setText("继续挑战");
            mTvReturnHomepage.setVisibility(View.VISIBLE);
            mTvCheckWrong.setVisibility(View.VISIBLE);
        } else if (mStatus.equals(ExamLib.EXAM_STATUS_NOT_PASS)) {
            mTvExamStatus.setText("继续努力!");
            mTvRecordRemarks.setVisibility(View.VISIBLE);
            mTvRecordRemarks.setText(getText(R.string.notPassRemarks));
            mTvExamTime.setText(mExamTime);
            mRlyScore.setVisibility(View.VISIBLE);
            mTvScore.setText(mExamScore);
            mRlyPassScore.setVisibility(View.GONE);
            mRlyExamStandard.setVisibility(View.GONE);
            mRlyExamType.setVisibility(View.GONE);
            mTvExamRules.setVisibility(View.GONE);
            mTvStartExam.setVisibility(View.GONE);
            mTvTryAgain.setVisibility(View.VISIBLE);
            mTvTryAgain.setText("再次挑战");
            mTvReturnHomepage.setVisibility(View.VISIBLE);
            mTvCheckWrong.setVisibility(View.VISIBLE);

        } else {
            //准备状态
            mTvRecordRemarks.setVisibility(View.GONE);
            mRlyScore.setVisibility(View.GONE);
            mRlyPassScore.setVisibility(View.VISIBLE);
            mRlyExamStandard.setVisibility(View.VISIBLE);
            mRlyExamType.setVisibility(View.VISIBLE);
            mTvExamRules.setVisibility(View.VISIBLE);
            mTvStartExam.setVisibility(View.VISIBLE);
            mTvTryAgain.setVisibility(View.GONE);
            mTvReturnHomepage.setVisibility(View.GONE);
            mTvCheckWrong.setVisibility(View.GONE);
            if (mStudent != null && !TextUtils.isEmpty(mStudent.name)) {
                mTvExamStatus.setText(mStudent.name);
            } else {
                mTvExamStatus.setText("未登录");
            }
            mTvExamType.setText(mExamType);
            if (mExamType.equals(ExamLib.EXAM_TYPE_1)) {
                mTvExamTime.setText(ExamLib.EXAM_TIME_1_LABEL);
                mTvExamRules.setText(getText(R.string.examRule1));
            } else {
                mTvExamTime.setText(ExamLib.EXAM_TIME_4_LABEL);
                mTvExamRules.setText(getText(R.string.examRule4));
            }

        }
        if (mStatus.equals(ExamLib.EXAM_STATUS_PASS) || mStatus.equals(ExamLib.EXAM_STATUS_NOT_PASS)) {
            User user = spUtil.getUser();
            if (user != null && user.isLogin()) {
                showShare();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_EXAM_TYPE, mExamType);
        outState.putString(ARG_EXAM_SCORE, mExamScore);
        outState.putString(ARG_EXAM_TIME, mExamTime);
        outState.putSerializable(ARG_WRONG_QUESTION_LIST, mWrongQuestionList);
        outState.putString(ARG_STATUS, mStatus);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_start_exam:
                    startExam();
                    break;
                case R.id.tv_try_again:
                    startExam();
                    break;
                case R.id.tv_return:
                    returnHomePage();
                    break;
                case R.id.tv_check_wrong:
                    checkWrong();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 返回首页
     */
    private void returnHomePage() {
        StartExamActivity.this.finish();
    }

    /**
     * 开始模拟考试
     */
    private void startExam() {
        Intent intent = new Intent(StartExamActivity.this, ExamActivity.class);
        intent.putExtra("examMode", ExamLib.TEST_MODE_MOCK_EXAM);
        intent.putExtra("examType", mExamType);
        startActivityForResult(intent, REQUEST_CODE_EXAM_ACTIVITY);
    }

    /**
     * 查看错题
     */
    private void checkWrong() {
        Intent intent = new Intent(StartExamActivity.this, ExamActivity.class);
        intent.putExtra("examMode", ExamLib.TEST_MODE_CHECK_WRONG);
        intent.putExtra("examType", mExamType);
        Bundle bundle = new Bundle();
        bundle.putSerializable("wrongQuestionList", mWrongQuestionList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EXAM_ACTIVITY) {
            if (resultCode == RESULT_OK && null != data) {
                Bundle bundle = data.getExtras();
                mWrongQuestionList = (ArrayList<Question>) bundle.get("wrongQuestionList");
                mExamScore = data.getStringExtra("score");
                mExamTime = data.getStringExtra("time");
                mStatus = data.getStringExtra("status");
                refreshUI();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showShare() {
        if (mShareDialog == null) {
            mShareDialog = new ShareAppDialog(getContext(), mPresenter.getBonus());
        }
        mShareDialog.show();
    }
}
