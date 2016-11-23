package com.hahaxueche.ui.fragment.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.user.User;
import com.hahaxueche.ui.activity.community.ExamActivity;
import com.hahaxueche.ui.activity.community.StartExamActivity;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.util.ExamLib;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ExamLibraryFragment extends HHBaseFragment {
    private String mExamType;
    private User user;

    public static ExamLibraryFragment newInstance(String type) {
        ExamLibraryFragment fragment = new ExamLibraryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mExamType = bundle.getString("type", ExamLib.EXAM_TYPE_1);
        HHBaseApplication application = HHBaseApplication.get(getContext());
        user = application.getSharedPrefUtil().getUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_library, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.tv_test_turn,
            R.id.tv_test_random,
            R.id.tv_mock_exam,
            R.id.tv_my_question_lib})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_test_turn:
                HashMap<String, String> map = new HashMap();
                if (user != null && user.isLogin()) {
                    map.put("student_id", user.student.id);
                    MobclickAgent.onEvent(getContext(), "online_test_page_order_tapped", map);
                } else {
                    MobclickAgent.onEvent(getContext(), "online_test_page_order_tapped");
                }
                navigateToExam(ExamLib.TEST_MODE_TURN);
                break;
            case R.id.tv_test_random:
                map = new HashMap();
                if (user != null && user.isLogin()) {
                    map.put("student_id", user.student.id);
                    MobclickAgent.onEvent(getContext(), "online_test_page_random_tapped", map);
                } else {
                    MobclickAgent.onEvent(getContext(), "online_test_page_random_tapped");
                }
                navigateToExam(ExamLib.TEST_MODE_RANDOM);
                break;
            case R.id.tv_mock_exam:
                map = new HashMap();
                if (user != null && user.isLogin()) {
                    map.put("student_id", user.student.id);
                    MobclickAgent.onEvent(getContext(), "online_test_page_simu_tapped", map);
                } else {
                    MobclickAgent.onEvent(getContext(), "online_test_page_simu_tapped");
                }
                navigateToStartExam();
                break;
            case R.id.tv_my_question_lib:
                map = new HashMap();
                if (user != null && user.isLogin()) {
                    map.put("student_id", user.student.id);
                    MobclickAgent.onEvent(getContext(), "online_test_page_my_lib_tapped", map);
                } else {
                    MobclickAgent.onEvent(getContext(), "online_test_page_my_lib_tapped");
                }
                navigateToExam(ExamLib.TEST_MODE_MY_LIB);
                break;
            default:
                break;
        }
    }

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
