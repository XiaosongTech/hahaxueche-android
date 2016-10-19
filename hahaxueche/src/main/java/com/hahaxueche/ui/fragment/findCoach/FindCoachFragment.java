package com.hahaxueche.ui.fragment.findCoach;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.presenter.findCoach.FindCoachPresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
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
    @BindView(R.id.tv_select_coach)
    TextView mTvSelectCoach;
    @BindView(R.id.tv_select_partner)
    TextView mTvSelectPartner;

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
    public void selectPartner() {
        mTvSelectPartner.setBackgroundResource(R.color.haha_white);
        mTvSelectPartner.setTextColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
    }

    @Override
    public void unSelectPartner() {
        mTvSelectPartner.setBackgroundResource(R.color.app_theme_color);
        mTvSelectPartner.setTextColor(ContextCompat.getColor(getContext(), R.color.haha_white));
    }

    @Override
    public void showLeftIconMap() {

    }

    @Override
    public void showLeftIconExplain() {

    }

    @OnClick({R.id.tv_select_coach,
            R.id.tv_select_partner})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select_coach:
                mPresenter.selectCoach();
                break;
            case R.id.tv_select_partner:
                mPresenter.selectPartner();
                break;
            default:
                break;
        }
    }
}
