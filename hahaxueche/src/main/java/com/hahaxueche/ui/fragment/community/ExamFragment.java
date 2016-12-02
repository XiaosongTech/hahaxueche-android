package com.hahaxueche.ui.fragment.community;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.examLib.Question;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.util.ExamLib;
import com.hahaxueche.util.SharedPrefUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/8/13.
 */
public class ExamFragment extends HHBaseFragment {

    public static final String ARG_PAGE = "pageNumber";
    public static final String ARG_QUESTION = "question";
    public static final String ARG_EXAM_MODE = "examMode";
    public static final String ARG_EXAM_TYPE = "examType";

    private TextView mTvQuestionType;
    private TextView mTvQuestion;
    private TextView mTvCollect;
    private TextView mTvRemove;
    private FrameLayout mFlyImage;
    private FrameLayout mFlyVideo;
    private SimpleDraweeView mIvUrl;
    private VideoView mVideo;
    private RelativeLayout mRlyItem1;
    private RelativeLayout mRlyItem2;
    private TextView mTvItem1Label;
    private TextView mTvItem1;
    private TextView mTvItem2Label;
    private TextView mTvItem2;
    private RelativeLayout mRlyItem3;
    private TextView mTvItem3Label;
    private TextView mTvItem3;
    private RelativeLayout mRlyItem4;
    private TextView mTvItem4Label;
    private TextView mTvItem4;
    private LinearLayout mLlyExplain;
    private TextView mTvExplain;
    private ImageView mIvDash;
    private ImageView mIvItem1;
    private ImageView mIvItem2;
    private ImageView mIvItem3;
    private ImageView mIvItem4;
    private TextView mTvSubmit;

    private int mPageNumber;
    private Question mQuestion;
    private String mExamMode;
    private String mExamType;

    private SharedPrefUtil spUtil;

    public interface OnCollectRemoveListener {
        void onCollectRemove(int position);
    }

    /**
     * 模拟测试时,答题回调
     */
    public interface OnMockExamAnsweredListener {
        void answer(Question question);
    }

    private OnCollectRemoveListener mOnCollectRemoveListener;
    private OnMockExamAnsweredListener mOnMockExamAnsweredListener;


    public static ExamFragment create(int pageNumber, Question question, String examMode, String examType) {
        ExamFragment fragment = new ExamFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putParcelable(ARG_QUESTION, question);
        args.putString(ARG_EXAM_MODE, examMode);
        args.putString(ARG_EXAM_TYPE, examType);
        fragment.setArguments(args);
        return fragment;
    }

    public ExamFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnCollectRemoveListener = (OnCollectRemoveListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCollectRemoveListener");
        }
        try {
            mOnMockExamAnsweredListener = (OnMockExamAnsweredListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMockExamAnsweredListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spUtil = new SharedPrefUtil(getContext());
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mQuestion = getArguments().getParcelable(ARG_QUESTION);
        mExamMode = getArguments().getString(ARG_EXAM_MODE);
        mExamType = getArguments().getString(ARG_EXAM_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_exam, container, false);
        initViews(rootView);
        loadDatas();
        return rootView;
    }

    private void initViews(View view) {
        mTvQuestionType = ButterKnife.findById(view, R.id.tv_question_type);
        mTvQuestion = ButterKnife.findById(view, R.id.tv_question);
        mTvCollect = ButterKnife.findById(view, R.id.tv_collect);
        mTvRemove = ButterKnife.findById(view, R.id.tv_remove_collect);
        mFlyImage = ButterKnife.findById(view, R.id.fly_image);
        mFlyVideo = ButterKnife.findById(view, R.id.fly_video);
        mIvUrl = ButterKnife.findById(view, R.id.iv_image_url);
        mVideo = ButterKnife.findById(view, R.id.video_question);
        mTvItem1Label = ButterKnife.findById(view, R.id.tv_item1_label);
        mTvItem2Label = ButterKnife.findById(view, R.id.tv_item2_label);
        mTvItem3Label = ButterKnife.findById(view, R.id.tv_item3_label);
        mTvItem4Label = ButterKnife.findById(view, R.id.tv_item4_label);
        mTvItem1 = ButterKnife.findById(view, R.id.tv_item1);
        mTvItem2 = ButterKnife.findById(view, R.id.tv_item2);
        mTvItem3 = ButterKnife.findById(view, R.id.tv_item3);
        mTvItem4 = ButterKnife.findById(view, R.id.tv_item4);
        mRlyItem1 = ButterKnife.findById(view, R.id.rly_item1);
        mRlyItem2 = ButterKnife.findById(view, R.id.rly_item2);
        mRlyItem3 = ButterKnife.findById(view, R.id.rly_item3);
        mRlyItem4 = ButterKnife.findById(view, R.id.rly_item4);
        mIvItem1 = ButterKnife.findById(view, R.id.iv_item1);
        mIvItem2 = ButterKnife.findById(view, R.id.iv_item2);
        mIvItem3 = ButterKnife.findById(view, R.id.iv_item3);
        mIvItem4 = ButterKnife.findById(view, R.id.iv_item4);
        mLlyExplain = ButterKnife.findById(view, R.id.lly_explain);
        mTvExplain = ButterKnife.findById(view, R.id.tv_explain);
        mIvDash = ButterKnife.findById(view, R.id.iv_dash);
        mIvDash.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mTvSubmit = ButterKnife.findById(view, R.id.tv_submit);
        mRlyItem1.setOnClickListener(mClickListener);
        mRlyItem2.setOnClickListener(mClickListener);
        mRlyItem3.setOnClickListener(mClickListener);
        mRlyItem4.setOnClickListener(mClickListener);
        mTvCollect.setOnClickListener(mClickListener);
        mTvRemove.setOnClickListener(mClickListener);
        mTvSubmit.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        mTvQuestionType.setText(mQuestion.getQuestionType());
        mTvQuestion.setText(mQuestion.question);
        //加载media
        if (mQuestion.mediatype.equals("1")) {
            mFlyVideo.setVisibility(View.GONE);
            mFlyImage.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(mQuestion.mediacontent);
            DraweeController draweeController =
                    Fresco.newDraweeControllerBuilder()
                            .setUri(uri)
                            .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                            .build();
            mIvUrl.setController(draweeController);
            GenericDraweeHierarchy hierarchy = mIvUrl.getHierarchy();
            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        } else if (mQuestion.mediatype.equals("2")) {
            mFlyVideo.setVisibility(View.VISIBLE);
            mFlyImage.setVisibility(View.GONE);
            Uri mVideoUri = Uri.parse(mQuestion.mediacontent);
            mVideo.setVideoPath(mVideoUri.toString());
            mVideo.start();
            mVideo.requestFocus();
            mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mp.setLooping(true);

                }
            });
        } else {
            mFlyVideo.setVisibility(View.GONE);
            mFlyImage.setVisibility(View.GONE);
        }
        //判断题
        if (mQuestion.getQuestionType().equals(ExamLib.QUESTION_TYPE_TRUE_FALSE)) {
            mTvItem1.setText("正确");
            mTvItem2.setText("错误");
            mRlyItem3.setVisibility(View.GONE);
            mRlyItem4.setVisibility(View.GONE);
        } else {
            mRlyItem3.setVisibility(View.VISIBLE);
            mRlyItem4.setVisibility(View.VISIBLE);
            mTvItem1.setText(mQuestion.answers.get(0) != null ? mQuestion.answers.get(0) : "");
            mTvItem2.setText(mQuestion.answers.get(1) != null ? mQuestion.answers.get(1) : "");
            mTvItem3.setText(mQuestion.answers.get(2) != null ? mQuestion.answers.get(2) : "");
            mTvItem4.setText(mQuestion.answers.get(3) != null ? mQuestion.answers.get(3) : "");
        }
        String explains = mQuestion.explain;
        if (explains.contains("http://")) {
            //含有链接的文字说明
            CharSequence explainStr = explains;
            final String url = explains.substring(explains.indexOf("http://"), explains.indexOf("html") + 4);
            SpannableString spexplainStr = new SpannableString(explainStr);
            spexplainStr.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(getContext(), BaseWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
                    ds.setUnderlineText(false);
                    ds.clearShadowLayer();
                }
            }, explains.indexOf("http://"), explains.indexOf("html") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spexplainStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.haha_blue)), explains.indexOf("http://"), explains.indexOf("html") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvExplain.setText(spexplainStr);
            mTvExplain.setHighlightColor(ContextCompat.getColor(getContext(), R.color.haha_blue));
            mTvExplain.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            mTvExplain.setText(mQuestion.explain);
        }

        mLlyExplain.setVisibility(View.GONE);
        if (mQuestion.hasAnswers()) {
            displayAnswer();
        }

        //显示收藏
        displayCollect();
        //显示多选题的提交按钮
        displayMultiSubmit();
    }

    private void displayMultiSubmit() {
        if (mQuestion.getQuestionType().equals(ExamLib.QUESTION_TYPE_MULTI_CHOICE) && !mQuestion.isSubmit) {
            mTvSubmit.setVisibility(View.VISIBLE);
        } else {
            mTvSubmit.setVisibility(View.GONE);
        }
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rly_item1:
                    selectItem("1");
                    break;
                case R.id.rly_item2:
                    selectItem("2");
                    break;
                case R.id.rly_item3:
                    selectItem("3");
                    break;
                case R.id.rly_item4:
                    selectItem("4");
                    break;
                case R.id.tv_collect:
                    collectQuestion();
                    break;
                case R.id.tv_remove_collect:
                    removeCollectQuesion();
                    break;
                case R.id.tv_submit:
                    submitMultiAnswer();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 多选题提交
     */
    private void submitMultiAnswer() {
        mQuestion.isSubmit = true;
        displayMultiSubmit();
        mOnMockExamAnsweredListener.answer(mQuestion);
        displayAnswer();
    }

    private void selectItem(String item) {
        ArrayList<String> userAnswerList = mQuestion.userAnswer;
        //判断题和单选题,点击直接出答案
        if (!mQuestion.getQuestionType().equals(ExamLib.QUESTION_TYPE_MULTI_CHOICE)) {
            if (userAnswerList == null || userAnswerList.size() == 0) {
                if (mExamMode.equals(ExamLib.TEST_MODE_TURN)) {
                    //顺序答题,记住位置
                    spUtil.setExamPosition(mExamMode, mPageNumber);
                }
                userAnswerList = new ArrayList<>();
                userAnswerList.add(item);
                mQuestion.userAnswer = userAnswerList;
                mOnMockExamAnsweredListener.answer(mQuestion);
                displayAnswer();
            }
        } else if (mQuestion.getQuestionType().equals(ExamLib.QUESTION_TYPE_MULTI_CHOICE)) {
            if (userAnswerList == null) {
                userAnswerList = new ArrayList<>();
                userAnswerList.add(item);
            } else {
                if (!userAnswerList.contains(item)) {
                    userAnswerList.add(item);
                } else {
                    userAnswerList.remove(item);
                }
            }
            mQuestion.userAnswer = userAnswerList;
            displayUserMultiAnswer();
        }
    }

    /**
     * 显示用户多选
     */
    private void displayUserMultiAnswer() {
        ArrayList<String> userAnswerList = mQuestion.userAnswer;
        if (userAnswerList == null) {
            userAnswerList = new ArrayList<>();
        }
        mTvItem1Label.setBackgroundResource(userAnswerList.contains("1") ? R.drawable.circle_half_appcolor : R.drawable.circle_gray);
        mTvItem1Label.setTextColor(ContextCompat.getColor(getContext(), userAnswerList.contains("1") ? R.color.app_theme_color : R.color.haha_gray_text));
        mTvItem2Label.setBackgroundResource(userAnswerList.contains("2") ? R.drawable.circle_half_appcolor : R.drawable.circle_gray);
        mTvItem2Label.setTextColor(ContextCompat.getColor(getContext(), userAnswerList.contains("2") ? R.color.app_theme_color : R.color.haha_gray_text));
        mTvItem3Label.setBackgroundResource(userAnswerList.contains("3") ? R.drawable.circle_half_appcolor : R.drawable.circle_gray);
        mTvItem3Label.setTextColor(ContextCompat.getColor(getContext(), userAnswerList.contains("3") ? R.color.app_theme_color : R.color.haha_gray_text));
        mTvItem4Label.setBackgroundResource(userAnswerList.contains("4") ? R.drawable.circle_half_appcolor : R.drawable.circle_gray);
        mTvItem4Label.setTextColor(ContextCompat.getColor(getContext(), userAnswerList.contains("4") ? R.color.app_theme_color : R.color.haha_gray_text));
    }

    /**
     * 显示答案
     */
    private void displayAnswer() {
        //如果是多选题,每个选项都显示对错,与用户选择的答案无关
        if (mQuestion.getQuestionType().equals(ExamLib.QUESTION_TYPE_MULTI_CHOICE)) {
            setItemView(mTvItem1Label, mIvItem1, mQuestion.answer_arr.contains("1"));
            setItemView(mTvItem2Label, mIvItem2, mQuestion.answer_arr.contains("2"));
            setItemView(mTvItem3Label, mIvItem3, mQuestion.answer_arr.contains("3"));
            setItemView(mTvItem4Label, mIvItem4, mQuestion.answer_arr.contains("4"));
            if (!mQuestion.isCorrect()) {
                //错题计入我的题库
                spUtil.addQuestionCollect(mExamType, mQuestion.questionid);
                displayCollect();
            }
        } else {
            if (mQuestion.answer.equals("1")) {
                setItemView(mTvItem1Label, mIvItem1, true);
            } else if (mQuestion.answer.equals("2")) {
                setItemView(mTvItem2Label, mIvItem2, true);
            } else if (mQuestion.answer.equals("3")) {
                setItemView(mTvItem3Label, mIvItem3, true);
            } else if (mQuestion.answer.equals("4")) {
                setItemView(mTvItem4Label, mIvItem4, true);
            }
            if (!mQuestion.isCorrect() && mQuestion.userAnswer != null) {//答错要显示错误标记
                if (mQuestion.userAnswer.contains("1")) {
                    setItemView(mTvItem1Label, mIvItem1, false);
                } else if (mQuestion.userAnswer.contains("2")) {
                    setItemView(mTvItem2Label, mIvItem2, false);
                } else if (mQuestion.userAnswer.contains("3")) {
                    setItemView(mTvItem3Label, mIvItem3, false);
                } else if (mQuestion.userAnswer.contains("4")) {
                    setItemView(mTvItem4Label, mIvItem4, false);
                }
                //错题计入我的题库
                spUtil.addQuestionCollect(mExamType, mQuestion.questionid);
                displayCollect();
            }
        }
        mLlyExplain.setVisibility(View.VISIBLE);
    }

    /**
     * 显示收藏
     */

    private void displayCollect() {
        if (mExamMode.equals(ExamLib.TEST_MODE_MY_LIB)) {
            mTvRemove.setVisibility(View.VISIBLE);
            mTvCollect.setVisibility(View.GONE);
        } else {
            mTvRemove.setVisibility(View.GONE);
            mTvCollect.setVisibility(View.VISIBLE);
            if (spUtil.isQuestionCollect(mExamType, mQuestion.questionid)) {
                mTvCollect.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getContext(), R.drawable.ic_question_alcollect), null, null);
            } else {
                mTvCollect.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getContext(), R.drawable.ic_question_collect), null, null);
            }
        }
    }

    /**
     * 收藏题目
     */
    private void collectQuestion() {
        String questionId = mQuestion.questionid;
        if (spUtil.isQuestionCollect(mExamType, questionId)) {
            spUtil.removeQuestionCollect(mExamType, questionId);
        } else {
            spUtil.addQuestionCollect(mExamType, questionId);
        }
        displayCollect();
    }

    private void removeCollectQuesion() {
        spUtil.removeQuestionCollect(mExamType, mQuestion.questionid);
        mOnCollectRemoveListener.onCollectRemove(mPageNumber);
    }

    private void setItemView(TextView textView, ImageView imageView, boolean isRight) {
        textView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), isRight ? R.drawable.ic_right : R.drawable.ic_wrong));
    }
}