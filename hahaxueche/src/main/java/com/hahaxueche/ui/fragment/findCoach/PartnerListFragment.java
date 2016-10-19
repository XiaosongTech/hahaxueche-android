package com.hahaxueche.ui.fragment.findCoach;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.Partner;
import com.hahaxueche.presenter.findCoach.PartnerListPresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.adapter.findCoach.PartnerAdapter;
import com.hahaxueche.ui.dialog.findCoach.PartnerFilterDialog;
import com.hahaxueche.ui.dialog.findCoach.PartnerSortDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.findCoach.PartnerListView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class PartnerListFragment extends HHBaseFragment implements PartnerListView, XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private MainActivity mActivity;
    private PartnerListPresenter mPresenter;
    @BindView(R.id.xlv_partners)
    XListView mXlvPartners;
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    private PartnerAdapter mPartnerAdapter;
    private ArrayList<Partner> mPartnerArrayList;
    private PartnerFilterDialog mFilterDialog;
    private PartnerSortDialog mSortDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mPresenter = new PartnerListPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partner_list, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        mXlvPartners.setPullRefreshEnable(true);
        mXlvPartners.setPullLoadEnable(true);
        mXlvPartners.setAutoLoadEnable(true);
        mXlvPartners.setXListViewListener(this);
        mXlvPartners.setOnItemClickListener(this);
        mXlvPartners.setEmptyView(mTvEmpty);
        mPresenter.fetchPartners();
        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setPullLoadEnable(boolean enable) {
        mXlvPartners.setPullLoadEnable(enable);
    }

    @Override
    public void refreshPartnerList(ArrayList<Partner> partnerArrayList) {
        mPartnerArrayList = partnerArrayList;
        mPartnerAdapter = new PartnerAdapter(getContext(), mPartnerArrayList);
        mXlvPartners.setAdapter(mPartnerAdapter);
        mXlvPartners.stopRefresh();
        mXlvPartners.stopLoadMore();
    }

    @Override
    public void addMorePartnerList(ArrayList<Partner> partnerArrayList) {
        mPartnerArrayList.addAll(partnerArrayList);
        mPartnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchPartners();
    }

    @Override
    public void onLoadMore() {
        mPresenter.addMorePartneres();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mPartnerArrayList != null && mPartnerArrayList.size() > 0 && position > 0 && position - 1 < mPartnerArrayList.size()) {
            Intent intent = new Intent(getContext(), CoachDetailActivity.class);
            intent.putExtra("coach", mPartnerArrayList.get(position - 1));
            startActivity(intent);
        }
    }

    @OnClick(R.id.fly_filter)
    public void showFilterDialog() {
        if (mFilterDialog == null) {
            mFilterDialog = new PartnerFilterDialog(getContext(), new PartnerFilterDialog.OnFilterListener() {
                @Override
                public void filter(String price, boolean C1Checked, boolean C2Checked) {
                    mPresenter.setFilters(price, C1Checked, C2Checked);
                    mPresenter.fetchPartners();
                }
            });
        }
        mFilterDialog.show();
    }

    @OnClick(R.id.fly_sort)
    public void showSortDialog() {
        if (mSortDialog == null) {
            mSortDialog = new PartnerSortDialog(getContext(), new PartnerSortDialog.OnSortListener() {
                @Override
                public void sort(int sortBy) {
                    mPresenter.setSortBy(sortBy);
                    mPresenter.fetchPartners();
                }
            });
        }
        mSortDialog.show();
    }
}
