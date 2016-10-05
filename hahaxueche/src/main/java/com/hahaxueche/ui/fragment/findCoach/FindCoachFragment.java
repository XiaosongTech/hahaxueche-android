package com.hahaxueche.ui.fragment.findCoach;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.findCoach.FindCoachPresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.activity.findCoach.SearchCoachActivity;
import com.hahaxueche.ui.adapter.findCoach.CoachAdapter;
import com.hahaxueche.ui.dialog.findCoach.CoachFilterDialog;
import com.hahaxueche.ui.dialog.findCoach.CoachSortDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.findCoach.FindCoachView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/13.
 */
public class FindCoachFragment extends HHBaseFragment implements FindCoachView, XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private MainActivity mActivity;
    private FindCoachPresenter mPresenter;
    @BindView(R.id.xlv_coaches)
    XListView mXlvCoaches;
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;
    private CoachAdapter mCoachAdapter;
    private ArrayList<Coach> mCoachArrayList;
    private CoachFilterDialog mFilterDialog;
    private CoachSortDialog mSortDialog;

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
        mXlvCoaches.setPullRefreshEnable(true);
        mXlvCoaches.setPullLoadEnable(true);
        mXlvCoaches.setAutoLoadEnable(true);
        mXlvCoaches.setXListViewListener(this);
        mXlvCoaches.setOnItemClickListener(this);
        mXlvCoaches.setEmptyView(mTvEmpty);
        mPresenter.fetchCoaches();
        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setPullLoadEnable(boolean enable) {
        mXlvCoaches.setPullLoadEnable(enable);
    }

    @Override
    public void refreshCoachList(ArrayList<Coach> coachArrayList) {
        mCoachArrayList = coachArrayList;
        mCoachAdapter = new CoachAdapter(getContext(), mCoachArrayList);
        mXlvCoaches.setAdapter(mCoachAdapter);
        mXlvCoaches.stopRefresh();
        mXlvCoaches.stopLoadMore();
    }

    @Override
    public void addMoreCoachList(ArrayList<Coach> coachArrayList) {
        mCoachArrayList.addAll(coachArrayList);
        mCoachAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchCoaches();
    }

    @Override
    public void onLoadMore() {
        mPresenter.addMoreCoaches();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCoachArrayList != null && mCoachArrayList.size() > 0 && position > 0 && position - 1 < mCoachArrayList.size()) {
            Intent intent = new Intent(getContext(), CoachDetailActivity.class);
            intent.putExtra("coach", mCoachArrayList.get(position - 1));
            startActivityForResult(intent, 1);
        }
    }

    @OnClick(R.id.fly_filter)
    public void showFilterDialog() {
        if (mFilterDialog == null) {
            mFilterDialog = new CoachFilterDialog(getContext(), new CoachFilterDialog.OnFilterListener() {
                @Override
                public void filter(String distance, String price, boolean isGoldenCoachOnly,
                                   boolean isVipOnly, boolean C1Checked, boolean C2Checked) {
                    mPresenter.setFilters(distance, price, isGoldenCoachOnly, isVipOnly, C1Checked, C2Checked);
                    mPresenter.fetchCoaches();
                }
            });
        }
        mFilterDialog.show();
    }

    @OnClick(R.id.fly_sort)
    public void showSortDialog() {
        if (mSortDialog == null) {
            mSortDialog = new CoachSortDialog(getContext(), new CoachSortDialog.OnSortListener() {
                @Override
                public void sort(int sortBy) {
                    mPresenter.setSortBy(sortBy);
                    mPresenter.fetchCoaches();
                }
            });
        }
        mSortDialog.show();
    }

    @OnClick(R.id.iv_search)
    public void clickSearchCoach() {
        startActivity(new Intent(getContext(), SearchCoachActivity.class));
    }
}
