package com.hahaxueche.ui.activity.mySetting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.hahaxueche.R;
import com.hahaxueche.model.response.ReferalHistoryResponse;
import com.hahaxueche.model.student.ReferalBonusTransaction;
import com.hahaxueche.model.student.WithdrawRecord;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.adapter.mySetting.WithdrawRecordAdapter;
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
public class WithdrawRecordListActivity extends MSBaseActivity {
    private ImageButton mIbtnBack;
    private ListView mLvWithdrawRecordList;
    private WithdrawRecordAdapter mWithdrawRecordAdapter;
    private ArrayList<WithdrawRecord> mWithdrawRecordList = new ArrayList<>();
    private User mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_history);
        initView();
        initEvent();
        loadDatas();
    }

    private void initView() {
        mIbtnBack = Util.instence(this).$(this, R.id.ibtn_back);
        mLvWithdrawRecordList = Util.instence(this).$(this, R.id.lv_withdraw_history);
    }

    private void initEvent() {
        mIbtnBack.setOnClickListener(mClickListener);
    }

    private void loadDatas() {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(this);
        mUser = spUtil.getUser();
        fetchRedeemList();
    }


    private void fetchRedeemList() {
        this.msPresenter.fetchWithdrawRecordList(mUser.getSession().getAccess_token(), new MSCallbackListener<ArrayList<WithdrawRecord>>() {
            @Override
            public void onSuccess(ArrayList<WithdrawRecord> data) {
                if (data != null && data.size() > 0) {
                    mWithdrawRecordList = data;
                    mWithdrawRecordAdapter = new WithdrawRecordAdapter(WithdrawRecordListActivity.this, mWithdrawRecordList, R.layout.adapter_withdraw_history);
                    mLvWithdrawRecordList.setAdapter(mWithdrawRecordAdapter);
                }
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
                    WithdrawRecordListActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };
}
