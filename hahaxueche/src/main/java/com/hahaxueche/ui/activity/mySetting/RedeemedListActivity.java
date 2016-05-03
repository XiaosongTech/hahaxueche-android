package com.hahaxueche.ui.activity.mySetting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import com.hahaxueche.R;
import com.hahaxueche.model.response.ReferalHistoryResponse;
import com.hahaxueche.model.student.ReferalBonusTransaction;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.adapter.mySetting.RedeemedAdapter;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/5/3.
 */
public class RedeemedListActivity extends MSBaseActivity implements XListView.IXListViewListener {
    private ImageButton mIbtnBack;
    private XListView mXlvRedeemedList;
    private String linkSelf;
    private String linkNext;
    private String linkPrevious;
    private String page;
    private String per_page = "10";
    private RedeemedAdapter mRedeemedAdapter;
    private ArrayList<ReferalBonusTransaction> mRedeemedList = new ArrayList<>();
    private boolean isOnLoadMore = false;
    private User mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_history);
        initView();
        initEvent();
        loadDatas();
        mXlvRedeemedList.setPullRefreshEnable(true);
        mXlvRedeemedList.setPullLoadEnable(true);
        mXlvRedeemedList.setAutoLoadEnable(true);
        mXlvRedeemedList.setXListViewListener(this);
        mXlvRedeemedList.setRefreshTime(getTime());
        if (TextUtils.isEmpty(linkNext)) {
            mXlvRedeemedList.setPullLoadEnable(false);
        } else {
            mXlvRedeemedList.setPullLoadEnable(true);
        }
        mRedeemedAdapter = new RedeemedAdapter(this, mRedeemedList, R.layout.adapter_refer_info);
        mXlvRedeemedList.setAdapter(mRedeemedAdapter);
    }

    private void initView() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mXlvRedeemedList = Util.instence(this).$(this, R.id.xlv_withdraw_history);
    }

    private void initEvent() {
        mIbtnBack.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        mUser = spUtil.getUser();
        fetchRedeemList();
    }

    @Override
    public void onRefresh() {
        isOnLoadMore = false;
        fetchRedeemList();
        if (TextUtils.isEmpty(linkNext)) {
            mXlvRedeemedList.setPullLoadEnable(false);
        } else {
            mXlvRedeemedList.setPullLoadEnable(true);
        }
    }

    @Override
    public void onLoadMore() {
        if (!TextUtils.isEmpty(linkNext) && !isOnLoadMore) {
            isOnLoadMore = true;
            fetchRedeemList(linkNext);
        } else {
            onLoad();
        }
    }

    private void onLoad() {
        mXlvRedeemedList.stopRefresh();
        mXlvRedeemedList.stopLoadMore();
        mXlvRedeemedList.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }

    private void fetchRedeemList() {
        this.msPresenter.fetchReferalHistoryList(mUser.getStudent().getId(), page, per_page, mUser.getSession().getAccess_token(), new MSCallbackListener<ReferalHistoryResponse>() {
            @Override
            public void onSuccess(ReferalHistoryResponse data) {
                mRedeemedList = data.getData();
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
                if (TextUtils.isEmpty(linkNext)) {
                    mXlvRedeemedList.setPullLoadEnable(false);
                } else {
                    mXlvRedeemedList.setPullLoadEnable(true);
                }
                mRedeemedAdapter = new RedeemedAdapter(RedeemedListActivity.this, mRedeemedList, R.layout.adapter_withdraw_history);
                mXlvRedeemedList.setAdapter(mRedeemedAdapter);
                onLoad();
            }

            @Override
            public void onFailure(String errorEvent, String message) {

            }
        });
    }

    private void fetchRedeemList(String url) {
        this.msPresenter.fetchReferalHistoryList(url, mUser.getSession().getAccess_token(), new MSCallbackListener<ReferalHistoryResponse>() {
            @Override
            public void onSuccess(ReferalHistoryResponse data) {
                ArrayList<ReferalBonusTransaction> newRefereeList = data.getData();
                if (newRefereeList != null && newRefereeList.size() > 0) {
                    mRedeemedList.addAll(newRefereeList);
                }
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
                if (TextUtils.isEmpty(linkNext)) {
                    mXlvRedeemedList.setPullLoadEnable(false);
                } else {
                    mXlvRedeemedList.setPullLoadEnable(true);
                }
                mRedeemedAdapter = new RedeemedAdapter(RedeemedListActivity.this, mRedeemedList, R.layout.adapter_withdraw_history);
                mXlvRedeemedList.setAdapter(mRedeemedAdapter);
                onLoad();
            }

            @Override
            public void onFailure(String errorEvent, String message) {

            }
        });
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ibtn_back:
                    RedeemedListActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };
}
