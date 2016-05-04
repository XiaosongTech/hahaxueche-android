package com.hahaxueche.ui.activity.mySetting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
    private ArrayList<Referee> mRefereeList = new ArrayList<>();
    private boolean isOnLoadMore = false;
    private User mUser;
    private ProgressDialog pd;//进度框
    private ReferalBonusSummary mReferalBonusSummary;
    private SwipeRefreshLayout mSrlNoRefer;
    private ScrollView mSvNoRefer;
    private LinearLayout mLlyNoRefer;
    private ImageView mIvNoRefer;
    private boolean isRefresh = false;//是否刷新中

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
        mSrlNoRefer = Util.instence(this).$(this, R.id.srl_no_refer);
        mSvNoRefer = Util.instence(this).$(this, R.id.sv_no_refer);
        mLlyNoRefer = Util.instence(this).$(this, R.id.lly_no_refer);
        mIvNoRefer = Util.instence(this).$(this, R.id.iv_no_refer);
    }

    private void initEvent() {
        mIbtnBack.setOnClickListener(mClickListener);
        mTvWithdrawnAmount.setOnClickListener(mClickListener);
        mTvWithdraw.setOnClickListener(mClickListener);
        mSrlNoRefer.setOnClickListener(mClickListener);
        mSvNoRefer.setOnClickListener(mClickListener);
        mLlyNoRefer.setOnClickListener(mClickListener);
        mIvNoRefer.setOnClickListener(mClickListener);
        mSrlNoRefer.setOnRefreshListener(mRefreshListener);
        mSrlNoRefer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!isRefresh) {
                isRefresh = true;
                getRefereeList();
            }
        }
    };

    private void loadDatas() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        mUser = spUtil.getUser();
        fetchBonusSummary();
        getRefereeList();
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
        this.msPresenter.fetchRefereeList(mUser.getStudent().getId(), page, per_page, mUser.getSession().getAccess_token(), new MSCallbackListener<RefereeListResponse>() {
            @Override
            public void onSuccess(RefereeListResponse data) {
                mSrlNoRefer.setRefreshing(false);
                isRefresh = false;
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
                if (mRefereeList != null && mRefereeList.size() > 0) {
                    mXlvReferInfo.setVisibility(View.VISIBLE);
                    mSrlNoRefer.setVisibility(View.GONE);
                } else {
                    mXlvReferInfo.setVisibility(View.GONE);
                    mSrlNoRefer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                mSrlNoRefer.setRefreshing(false);
                isRefresh = false;
            }
        });
    }

    private void getRefereeList(String url) {
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
            }

            @Override
            public void onFailure(String errorEvent, String message) {
            }
        });
    }

    private void fetchBonusSummary() {
        pd = ProgressDialog.show(MakeMoneyInfoActivity.this, null, "数据加载中，请稍后……");
        this.msPresenter.fetchBonusSummary(mUser.getStudent().getId(), mUser.getSession().getAccess_token(), new MSCallbackListener<ReferalBonusSummary>() {
            @Override
            public void onSuccess(ReferalBonusSummary referalBonusSummary) {
                mReferalBonusSummary = referalBonusSummary;
                mTvPendingAmount.setText(Util.getMoney(mReferalBonusSummary.getPending_add_to_account()));
                mTvAvailableAmount.setText(Util.getMoney(mReferalBonusSummary.getAvailable_to_redeem()));
                mTvWithdrawnAmount.setText(Util.getMoney(mReferalBonusSummary.getRedeemed()));
                pd.dismiss();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                pd.dismiss();
            }
        });
    }


    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ibtn_back:
                    MakeMoneyInfoActivity.this.finish();
                    break;
                case R.id.tv_withdrawn_amount:
                    Intent intent;
                    if (mRefereeList != null && mRefereeList.size() > 0) {
                        intent = new Intent(getApplication(), RedeemedListActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getApplication(), ReferFriendsActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.tv_withdraw:
                    if (mRefereeList != null && mRefereeList.size() > 0) {
                        //我要提现
                        intent = new Intent(getApplication(), WithdrawActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("referalBonusSummary", mReferalBonusSummary);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 0);
                    } else {
                        intent = new Intent(getApplication(), ReferFriendsActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.srl_no_refer:
                    intent = new Intent(getApplication(), ReferFriendsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sv_no_refer:
                    intent = new Intent(getApplication(), ReferFriendsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.lly_no_refer:
                    intent = new Intent(getApplication(), ReferFriendsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.iv_no_refer:
                    intent = new Intent(getApplication(), ReferFriendsActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
                case RESULT_OK:
                    Toast.makeText(MakeMoneyInfoActivity.this, "提现成功", Toast.LENGTH_SHORT).show();
                    fetchBonusSummary();
                    break;
                default:
                    break;
            }
        }
    }

}
