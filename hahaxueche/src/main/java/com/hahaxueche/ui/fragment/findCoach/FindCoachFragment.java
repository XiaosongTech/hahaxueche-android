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
import com.hahaxueche.model.base.Field;
import com.hahaxueche.presenter.findCoach.FindCoachPresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.findCoach.FieldFilterActivity;
import com.hahaxueche.ui.activity.findCoach.SearchCoachActivity;
import com.hahaxueche.ui.dialog.BaseAlertSimpleDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.findCoach.FindCoachView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

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
    @BindView(R.id.iv_search)
    ImageView mIvSearch;
    private CoachListFragment mCoachListFragment;
    private PartnerListFragment mPartnerListFragment;
    private BaseAlertSimpleDialog mAlertDialog;
    private static final int REQUEST_CODE_FIELD_FILTER = 2;

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
        mIvIconLeft.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_maplist_btn));
    }

    @Override
    public void showLeftIconExplain() {
        mIvIconLeft.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_explain));
    }

    @Override
    public void showCoachListFragment() {
        FragmentManager fm = getChildFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        if (mCoachListFragment == null) {
            mCoachListFragment = new CoachListFragment();
        }
        if (mPartnerListFragment == null) {
            mPartnerListFragment = new PartnerListFragment();
        }
        if (!mCoachListFragment.isAdded()) {//先判断是否被add过
            transaction.hide(mPartnerListFragment).add(R.id.id_content, mCoachListFragment).commit();//隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(mPartnerListFragment).show(mCoachListFragment).commit();//隐藏当前的fragment，显示下一个
        }
    }

    @Override
    public void showPartnerListFragment() {
        FragmentManager fm = getChildFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        if (mCoachListFragment == null) {
            mCoachListFragment = new CoachListFragment();
        }
        if (mPartnerListFragment == null) {
            mPartnerListFragment = new PartnerListFragment();
        }
        if (!mPartnerListFragment.isAdded()) {//先判断是否被add过
            transaction.hide(mCoachListFragment).add(R.id.id_content, mPartnerListFragment).show(mPartnerListFragment).commit();//隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(mCoachListFragment).show(mPartnerListFragment).commit();//隐藏当前的fragment，显示下一个
        }
    }

    @Override
    public void showPartnerInfoDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new BaseAlertSimpleDialog(getContext(), "什么是陪练教练？", getString(R.string.partnerExplain));
        }
        mAlertDialog.show();
    }

    @Override
    public void navigateToSelectFields() {
        Intent intent = new Intent(getContext(), FieldFilterActivity.class);
        intent.putParcelableArrayListExtra("selectFields", mCoachListFragment.getSelectFields());
        startActivityForResult(intent, REQUEST_CODE_FIELD_FILTER);
    }

    @Override
    public void showSearchIcon(boolean enable) {
        mIvSearch.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.tv_select_coach,
            R.id.tv_select_partner,
            R.id.iv_icon_left,
            R.id.iv_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select_coach:
                mPresenter.selectCoach();
                break;
            case R.id.tv_select_partner:
                mPresenter.selectPartner();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FIELD_FILTER) {
            if (resultCode == RESULT_OK) {
                ArrayList<Field> fields = data.getParcelableArrayListExtra("selectFields");
                mCoachListFragment.setSelectFields(fields);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
