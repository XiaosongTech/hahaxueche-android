package com.hahaxueche.ui.fragment.index.exam;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.ui.activity.index.ExamActivity;
import com.hahaxueche.ui.activity.index.StartExamActivity;
import com.hahaxueche.utils.ExamLib;

/**
 * Created by wangshirui on 16/8/10.
 */
public class ExamLibraryFragment extends Fragment {
    private String mExamType;

    private TextView mTvTestTurn;
    private TextView mTvTestRandom;
    private TextView mTvMockExam;
    private TextView mTvMyLib;

    public static Fragment instance(String type) {
        ExamLibraryFragment fragment = new ExamLibraryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exam_library, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        mExamType = bundle.getString("type", ExamLib.EXAM_TYPE_1);
        mTvTestTurn = (TextView) view.findViewById(R.id.tv_test_turn);
        mTvTestRandom = (TextView) view.findViewById(R.id.tv_test_random);
        mTvMockExam = (TextView) view.findViewById(R.id.tv_mock_exam);
        mTvMyLib = (TextView) view.findViewById(R.id.tv_my_question_lib);

        mTvTestTurn.setOnClickListener(mClickListener);
        mTvTestRandom.setOnClickListener(mClickListener);
        mTvMockExam.setOnClickListener(mClickListener);
        mTvMyLib.setOnClickListener(mClickListener);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_test_turn:
                    navigateToExam(ExamLib.TEST_MODE_TURN);
                    break;
                case R.id.tv_test_random:
                    navigateToExam(ExamLib.TEST_MODE_RANDOM);
                    break;
                case R.id.tv_mock_exam:
                    navigateToStartExam();
                    break;
                case R.id.tv_my_question_lib:
                    navigateToExam(ExamLib.TEST_MODE_MY_LIB);
                    break;
                default:
                    break;
            }
        }
    };

    private void navigateToExam(String examMode) {
        Intent intent = new Intent(getActivity(), ExamActivity.class);
        intent.putExtra("examMode", examMode);
        intent.putExtra("examType", mExamType);
        getActivity().startActivityForResult(intent, 1);
    }

    private void navigateToStartExam() {
        Intent intent = new Intent(getActivity(), StartExamActivity.class);
        intent.putExtra("examType", mExamType);
        getActivity().startActivity(intent);
    }
}