package com.hahaxueche.ui.activity.mySetting;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.response.RefereeListResponse;
import com.hahaxueche.model.student.ReferalBonusSummary;
import com.hahaxueche.model.student.Referee;
import com.hahaxueche.model.student.RefereeStatus;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.adapter.findCoach.ReviewItemAdapter;
import com.hahaxueche.ui.adapter.mySetting.ReferInfoAdapter;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gibxin on 2016/4/29.
 */
public class MakeMoneyInfoActivity extends MSBaseActivity implements XListView.IXListViewListener {
    private ImageButton mIbtnBack;
    private TextView mTvPendingAmount;
    private TextView mTvAvailableAmount;
    private TextView mTvWithdrawnAmount;
    private TextView mTvWithdraw;
    private XListView mXlvReferInfo;
    private String linkSelf;
    private String linkNext;
    private String linkPrevious;
    private String page;
    private String per_page = "10";
    private ReferInfoAdapter mReferInfoAdapter;
    private ArrayList<Referee> mRefereeList;
    private boolean isOnLoadMore = false;
    private User mUser;
    private ProgressDialog pd;//进度框
    private boolean mLoadingFetchBonus;
    private boolean mLoadingFetchRefereeList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_money);
        initView();
        initEvent();
        loadDatas();
        mXlvReferInfo.setPullRefreshEnable(true);
        mXlvReferInfo.setPullLoadEnable(true);
        mXlvReferInfo.setAutoLoadEnable(true);
        mXlvReferInfo.setXListViewListener(this);
        mXlvReferInfo.setRefreshTime(getTime());
        if (TextUtils.isEmpty(linkNext)) {
            mXlvReferInfo.setPullLoadEnable(false);
        } else {
            mXlvReferInfo.setPullLoadEnable(true);
        }
        mReferInfoAdapter = new ReferInfoAdapter(this, mRefereeList, R.layout.adapter_refer_info);
        mXlvReferInfo.setAdapter(mReferInfoAdapter);
    }

    private void initView() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mTvPendingAmount = Util.instence(this).$(this, R.id.tv_pending_amount);
        mTvAvailableAmount = Util.instence(this).$(this, R.id.tv_available_amount);
        mTvWithdrawnAmount = Util.instence(this).$(this, R.id.tv_withdrawn_amount);
        mTvWithdraw = Util.instence(this).$(this, R.id.tv_withdraw);
        mXlvReferInfo = Util.instence(this).$(this, R.id.xlv_refer_info);
    }

    private void initEvent() {

    }

    private void loadDatas() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        mUser = spUtil.getUser();
        fetchBonusSummary();
    }

    @Override
    public void onRefresh() {
        isOnLoadMore = false;
        getRefereeList();
        if (TextUtils.isEmpty(linkNext)) {
            mXlvReferInfo.setPullLoadEnable(false);
        } else {
            mXlvReferInfo.setPullLoadEnable(true);
        }
    }

    @Override
    public void onLoadMore() {
        if (!TextUtils.isEmpty(linkNext) && !isOnLoadMore) {
            isOnLoadMore = true;
            getRefereeList(linkNext);
        } else {
            onLoad();
        }
    }

    private void onLoad() {
        mXlvReferInfo.stopRefresh();
        mXlvReferInfo.stopLoadMore();
        mXlvReferInfo.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }

    private void getRefereeList() {
        showLoading();
        mLoadingFetchRefereeList = true;
        this.msPresenter.fetchRefereeList(mUser.getStudent().getId(), page, per_page, mUser.getSession().getAccess_token(), new MSCallbackListener<RefereeListResponse>() {
            @Override
            public void onSuccess(RefereeListResponse data) {
                mRefereeList = data.getData();
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
                if (TextUtils.isEmpty(linkNext)) {
                    mXlvReferInfo.setPullLoadEnable(false);
                } else {
                    mXlvReferInfo.setPullLoadEnable(true);
                }
                mReferInfoAdapter = new ReferInfoAdapter(MakeMoneyInfoActivity.this, mRefereeList, R.layout.adapter_refer_info);
                mXlvReferInfo.setAdapter(mReferInfoAdapter);
                onLoad();
                mLoadingFetchRefereeList = false;
                dismissLoading();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                mLoadingFetchRefereeList = false;
                dismissLoading();
            }
        });
    }

    private void getRefereeList(String url) {
        showLoading();
        mLoadingFetchRefereeList = true;
        this.msPresenter.fetchRefereeList(url, mUser.getSession().getAccess_token(), new MSCallbackListener<RefereeListResponse>() {
            @Override
            public void onSuccess(RefereeListResponse data) {
                ArrayList<Referee> newRefereeList = data.getData();
                if (newRefereeList != null && newRefereeList.size() > 0) {
                    mRefereeList.addAll(newRefereeList);
                }
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
                if (TextUtils.isEmpty(linkNext)) {
                    mXlvReferInfo.setPullLoadEnable(false);
                } else {
                    mXlvReferInfo.setPullLoadEnable(true);
                }
                mReferInfoAdapter = new ReferInfoAdapter(MakeMoneyInfoActivity.this, mRefereeList, R.layout.adapter_refer_info);
                mXlvReferInfo.setAdapter(mReferInfoAdapter);
                onLoad();
                mLoadingFetchRefereeList = false;
                dismissLoading();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                mLoadingFetchRefereeList = false;
                dismissLoading();
            }
        });
    }

    private void fetchBonusSummary() {
        showLoading();
        mLoadingFetchBonus = true;
        this.msPresenter.fetchBonusSummary(mUser.getStudent().getId(), mUser.getSession().getAccess_token(), new MSCallbackListener<ReferalBonusSummary>() {
            @Override
            public void onSuccess(ReferalBonusSummary referalBonusSummary) {
                mTvPendingAmount.setText(Util.getMoney(referalBonusSummary.getPending_add_to_account()));
                mTvAvailableAmount.setText(Util.getMoney(referalBonusSummary.getAvailable_to_redeem()));
                mTvWithdrawnAmount.setText(Util.getMoney(referalBonusSummary.getRedeemed()));
                mLoadingFetchBonus = false;
                dismissLoading();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                mLoadingFetchBonus = false;
                dismissLoading();

            }
        });
    }

    /**
     * 显示进度框
     */
    private void showLoading() {
        if (pd == null || !pd.isShowing()) {
            pd = ProgressDialog.show(MakeMoneyInfoActivity.this, null, "数据加载中，请稍后……");
        }
    }

    /**
     * 隐藏进度框
     */
    private void dismissLoading() {
        if (!mLoadingFetchBonus && !mLoadingFetchRefereeList) {
            pd.dismiss();
        }
    }

}
