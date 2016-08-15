package com.hahaxueche.ui.fragment.index.exam;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.examLib.Question;
import com.hahaxueche.utils.ExamLib;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

/**
 * Created by wangshirui on 16/8/13.
 */
public class ExamFragment extends Fragment {

    public static final String ARG_PAGE = "pageNumber";
    public static final String ARG_QUESTION = "question";
    public static final String ARG_EXAM_TYPE = "examType";

    private TextView mTvQuestionType;
    private TextView mTvQuestion;
    private TextView mTvCollect;
    private FrameLayout mFlyImage;
    private SimpleDraweeView mIvUrl;
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

    private int mPageNumber;
    private Question mQuestion;
    private String mExamType;

    private SharedPreferencesUtil spUtil;


    public static ExamFragment create(int pageNumber, Question question, String examType) {
        ExamFragment fragment = new ExamFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putSerializable(ARG_QUESTION, question);
        args.putString(ARG_EXAM_TYPE, examType);
        fragment.setArguments(args);
        return fragment;
    }

    public ExamFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spUtil = new SharedPreferencesUtil(getContext());
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mQuestion = (Question) getArguments().get(ARG_QUESTION);
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
        mTvQuestionType = Util.instence(getContext()).$(view, R.id.tv_question_type);
        mTvQuestion = Util.instence(getContext()).$(view, R.id.tv_question);
        mTvCollect = Util.instence(getContext()).$(view, R.id.tv_collect);
        mFlyImage = Util.instence(getContext()).$(view, R.id.fly_image);
        mIvUrl = Util.instence(getContext()).$(view, R.id.iv_image_url);
        mTvItem1Label = Util.instence(getContext()).$(view, R.id.tv_item1_label);
        mTvItem2Label = Util.instence(getContext()).$(view, R.id.tv_item2_label);
        mTvItem3Label = Util.instence(getContext()).$(view, R.id.tv_item3_label);
        mTvItem4Label = Util.instence(getContext()).$(view, R.id.tv_item4_label);
        mTvItem1 = Util.instence(getContext()).$(view, R.id.tv_item1);
        mTvItem2 = Util.instence(getContext()).$(view, R.id.tv_item2);
        mTvItem3 = Util.instence(getContext()).$(view, R.id.tv_item3);
        mTvItem4 = Util.instence(getContext()).$(view, R.id.tv_item4);
        mRlyItem1 = Util.instence(getContext()).$(view, R.id.rly_item1);
        mRlyItem2 = Util.instence(getContext()).$(view, R.id.rly_item2);
        mRlyItem3 = Util.instence(getContext()).$(view, R.id.rly_item3);
        mRlyItem4 = Util.instence(getContext()).$(view, R.id.rly_item4);
        mIvItem1 = Util.instence(getContext()).$(view, R.id.iv_item1);
        mIvItem2 = Util.instence(getContext()).$(view, R.id.iv_item2);
        mIvItem3 = Util.instence(getContext()).$(view, R.id.iv_item3);
        mIvItem4 = Util.instence(getContext()).$(view, R.id.iv_item4);
        mLlyExplain = Util.instence(getContext()).$(view, R.id.lly_explain);
        mTvExplain = Util.instence(getContext()).$(view, R.id.tv_explain);
        mIvDash = Util.instence(getContext()).$(view, R.id.iv_dash);
        mIvDash.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mRlyItem1.setOnClickListener(mClickListener);
        mRlyItem2.setOnClickListener(mClickListener);
        mRlyItem3.setOnClickListener(mClickListener);
        mRlyItem4.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        mTvQuestionType.setText(mQuestion.getQuestionType());
        mTvQuestion.setText(mQuestion.getQuestion());
        //加载图片
        if (TextUtils.isEmpty(mQuestion.getUrl())) {
            mFlyImage.setVisibility(View.GONE);
        } else {
            mFlyImage.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(mQuestion.getUrl());
            DraweeController draweeController =
                    Fresco.newDraweeControllerBuilder()
                            .setUri(uri)
                            .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                            .build();
            mIvUrl.setController(draweeController);
            GenericDraweeHierarchy hierarchy = mIvUrl.getHierarchy();
            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

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
            mTvItem1.setText(mQuestion.getItem1());
            mTvItem2.setText(mQuestion.getItem2());
            mTvItem3.setText(mQuestion.getItem3());
            mTvItem4.setText(mQuestion.getItem4());
        }
        mTvExplain.setText(mQuestion.getExplains());
        mLlyExplain.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mQuestion.getUserAnswer())) {
            displayAnswer();
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
                    selectItem(1);
                    break;
                case R.id.rly_item2:
                    selectItem(2);
                    break;
                case R.id.rly_item3:
                    selectItem(3);
                    break;
                case R.id.rly_item4:
                    selectItem(4);
                    break;
                default:
                    break;
            }
        }
    };

    private void selectItem(int item) {
        //判断题和单选题,点击直接出答案
        if (!mQuestion.getQuestionType().equals(ExamLib.QUESTION_TYPE_MULTI_CHOICE) && TextUtils.isEmpty(mQuestion.getUserAnswer())) {
            if (mExamType.equals(ExamLib.TEST_MODE_TURN)) {
                //顺序答题,记住位置
                spUtil.setExamPosition(mExamType, mPageNumber);
            }
            mQuestion.setUserAnswer(String.valueOf(item));
            displayAnswer();
        }
    }

    private void displayAnswer() {
        if (mQuestion.getAnswer().equals("1")) {
            mTvItem1Label.setVisibility(View.INVISIBLE);
            mIvItem1.setVisibility(View.VISIBLE);
            mIvItem1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_right));
        } else if (mQuestion.getAnswer().equals("2")) {
            mTvItem2Label.setVisibility(View.INVISIBLE);
            mIvItem2.setVisibility(View.VISIBLE);
            mIvItem2.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_right));
        } else if (mQuestion.getAnswer().equals("3")) {
            mTvItem3Label.setVisibility(View.INVISIBLE);
            mIvItem3.setVisibility(View.VISIBLE);
            mIvItem3.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_right));
        } else if (mQuestion.getAnswer().equals("4")) {
            mTvItem4Label.setVisibility(View.INVISIBLE);
            mIvItem4.setVisibility(View.VISIBLE);
            mIvItem4.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_right));
        }
        if (!mQuestion.isCorrect()) {//答错要显示错误标记
            if (mQuestion.getUserAnswer().equals("1")) {
                mTvItem1Label.setVisibility(View.INVISIBLE);
                mIvItem1.setVisibility(View.VISIBLE);
                mIvItem1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_wrong));
            } else if (mQuestion.getUserAnswer().equals("2")) {
                mTvItem2Label.setVisibility(View.INVISIBLE);
                mIvItem2.setVisibility(View.VISIBLE);
                mIvItem2.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_wrong));
            } else if (mQuestion.getUserAnswer().equals("3")) {
                mTvItem3Label.setVisibility(View.INVISIBLE);
                mIvItem3.setVisibility(View.VISIBLE);
                mIvItem3.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_wrong));
            } else if (mQuestion.getUserAnswer().equals("4")) {
                mTvItem4Label.setVisibility(View.INVISIBLE);
                mIvItem4.setVisibility(View.VISIBLE);
                mIvItem4.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_wrong));
            }
        }
        mLlyExplain.setVisibility(View.VISIBLE);
    }
}
