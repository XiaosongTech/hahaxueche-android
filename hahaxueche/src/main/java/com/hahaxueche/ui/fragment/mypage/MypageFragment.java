package com.hahaxueche.ui.fragment.myPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.user.Student;
import com.hahaxueche.presenter.myPage.MyPagePresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.myPage.MyPageView;
import com.hahaxueche.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/13.
 */
public class MyPageFragment extends HHBaseFragment implements MyPageView {
    @BindView(R.id.lly_not_login)
    LinearLayout mLlyNotLogin;
    @BindView(R.id.srl_my_page)
    SwipeRefreshLayout mSrlMyPage;
    @BindView(R.id.iv_my_avatar)
    SimpleDraweeView mIvMyAvatar;
    @BindView(R.id.tv_student_name)
    TextView mTvStudentName;
    @BindView(R.id.tv_account_balance)
    TextView mTvAccountBalance;
    @BindView(R.id.tv_payment_stage)
    TextView mTvPaymentStage;
    @BindView(R.id.tv_student_phase)
    TextView mTvStudentPhase;

    private MyPagePresenter mPresenter;
    private MainActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mPresenter = new MyPagePresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        return view;
    }

    @OnClick(R.id.tv_logout)
    public void logOut() {
        mPresenter.logOut();
    }

    @Override
    public void showNotLoginView() {
        mLlyNotLogin.setVisibility(View.VISIBLE);
        mSrlMyPage.setVisibility(View.GONE);
    }

    @Override
    public void showLoggedInView() {
        mLlyNotLogin.setVisibility(View.GONE);
        mSrlMyPage.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadStudentInfo(Student student) {
        mIvMyAvatar.setImageURI(student.avatar);
        mTvStudentName.setText(student.name);
        mTvAccountBalance.setText(Utils.getMoney(student.getAccountBalance()));
        mTvPaymentStage.setText(student.getPaymentStageLabel());
        mTvStudentPhase.setText(student.getStudentPhaseLabel());
    }

    @Override
    public void finishToStartLogin() {
        startActivity(new Intent(getContext(), StartLoginActivity.class));
        mActivity.finish();
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}
