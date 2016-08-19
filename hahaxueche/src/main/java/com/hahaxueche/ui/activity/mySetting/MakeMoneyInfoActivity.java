package com.hahaxueche.ui.activity.mySetting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
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
        mXlvReferInfo = Util.instence(this).$(this, R.id.xlv_refer_info);
    }

    private void initEvent() {
        mIbtnBack.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        mUser = spUtil.getUser();
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
            }

            @Override
            public void onFailure(String errorEvent, String message) {
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



    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ibtn_back:
                    MakeMoneyInfoActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };


}
