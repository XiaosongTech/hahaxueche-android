package com.hahaxueche.ui.fragment.findCoach;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.presenter.findCoach.FindCoachPresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.findCoach.FieldFilterActivity;
import com.hahaxueche.ui.activity.findCoach.SearchCoachActivity;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.findCoach.FindCoachView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class FindCoachFragment extends HHBaseFragment implements FindCoachView {
    private MainActivity mActivity;
    private FindCoachPresenter mPresenter;
    @BindView(R.id.iv_icon_left)
    ImageView mIvIconLeft;
    @BindView(R.id.tv_select_driving_school)
    TextView mTvSelectDrivingSchool;
    @BindView(R.id.tv_select_coach)
    TextView mTvSelectCoach;
    @BindView(R.id.iv_search)
    ImageView mIvSearch;
    private CoachListFragment mCoachListFragment;
    private DrivingSchoolListFragment mDrivingSchoolListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mPresenter = new FindCoachPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_coach, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void selectCoach() {
        mTvSelectCoach.setBackgroundResource(R.color.haha_white);
        mTvSelectCoach.setTextColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
    }

    @Override
    public void unSelectCoach() {
        mTvSelectCoach.setBackgroundResource(R.color.app_theme_color);
        mTvSelectCoach.setTextColor(ContextCompat.getColor(getContext(), R.color.haha_white));
    }

    @Override
    public void selectDrivingSchool() {
        mTvSelectDrivingSchool.setBackgroundResource(R.color.haha_white);
        mTvSelectDrivingSchool.setTextColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
    }

    @Override
    public void unSelectDrivingSchool() {
        mTvSelectDrivingSchool.setBackgroundResource(R.color.app_theme_color);
        mTvSelectDrivingSchool.setTextColor(ContextCompat.getColor(getContext(), R.color.haha_white));
    }

    @Override
    public void showCoachListFragment() {
        FragmentManager fm = getChildFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        if (mCoachListFragment == null) {
            mCoachListFragment = new CoachListFragment();
        }
        if (mDrivingSchoolListFragment == null) {
            mDrivingSchoolListFragment = new DrivingSchoolListFragment();
        }
        if (!mCoachListFragment.isAdded()) {//先判断是否被add过
            transaction.hide(mDrivingSchoolListFragment).add(R.id.id_content, mCoachListFragment).show(mCoachListFragment).commit();//隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(mDrivingSchoolListFragment).show(mCoachListFragment).commit();//隐藏当前的fragment，显示下一个
        }
    }

    @Override
    public void showDrivingSchoolListFragment() {
        FragmentManager fm = getChildFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        if (mCoachListFragment == null) {
            mCoachListFragment = new CoachListFragment();
        }
        if (mDrivingSchoolListFragment == null) {
            mDrivingSchoolListFragment = new DrivingSchoolListFragment();
        }
        if (!mDrivingSchoolListFragment.isAdded()) {//先判断是否被add过
            transaction.hide(mCoachListFragment).add(R.id.id_content, mDrivingSchoolListFragment).commit();//隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(mCoachListFragment).show(mDrivingSchoolListFragment).commit();//隐藏当前的fragment，显示下一个
        }
    }

    @Override
    public void navigateToSelectFields() {
        startActivity(new Intent(getContext(), FieldFilterActivity.class));
    }

    @OnClick({R.id.tv_select_driving_school,
            R.id.tv_select_coach,
            R.id.iv_icon_left,
            R.id.iv_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select_driving_school:
                mPresenter.selectDrivingSchool();
                break;
            case R.id.tv_select_coach:
                mPresenter.selectCoach();
                break;
            case R.id.iv_icon_left:
                mPresenter.clickLeftIcon();
                break;
            case R.id.iv_search:
                mPresenter.clickSearchCount();
                startActivity(new Intent(getContext(), SearchCoachActivity.class));
                break;
            default:
                break;
        }
    }

    public void onCityChange() {
        //城市更改，刷新练列表
        if (mCoachListFragment != null) {
            mCoachListFragment.onRefresh();
        }
        if (mDrivingSchoolListFragment != null) {
            mDrivingSchoolListFragment.onRefresh();
        }
    }
}
