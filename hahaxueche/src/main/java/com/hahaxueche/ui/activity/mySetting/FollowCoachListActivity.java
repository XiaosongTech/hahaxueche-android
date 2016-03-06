package com.hahaxueche.ui.activity.mySetting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.CoachListResponse;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.presenter.mySetting.MSCallbackListener;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.adapter.findCoach.CoachItemAdapter;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by gibxin on 2016/2/29.
 */
public class FollowCoachListActivity extends MSBaseActivity implements XListView.IXListViewListener {
    private XListView xlvCoachList;
    private ImageButton ibtnFclBack;
    private CoachItemAdapter mAdapter;
    private List<CoachModel> coachList = new ArrayList<CoachModel>();
    private Handler mHandler;
    private int mRefreshIndex = 0;
    private String linkSelf;
    private String linkNext;
    private String linkPrevious;
    private String page;
    private String per_page = "10";
    private String access_token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_coach_list);
        SharedPreferences sharedPreferences = getSharedPreferences("session", Activity.MODE_PRIVATE);
        access_token = sharedPreferences.getString("access_token", "");
        initView();
        initEvent();
        if (xlvCoachList != null)
            xlvCoachList.autoRefresh();
    }

    private void initView() {
        xlvCoachList = (XListView) findViewById(R.id.xlv_follow_coach_list);
        xlvCoachList.setPullRefreshEnable(true);
        xlvCoachList.setPullLoadEnable(true);
        xlvCoachList.setAutoLoadEnable(true);
        xlvCoachList.setXListViewListener(this);
        xlvCoachList.setRefreshTime(getTime());
        if(TextUtils.isEmpty(linkNext)){
            xlvCoachList.setPullLoadEnable(false);
        }else {
            xlvCoachList.setPullLoadEnable(true);
        }
        mAdapter = new CoachItemAdapter(this, coachList, R.layout.view_coach_list_item);
        xlvCoachList.setAdapter(mAdapter);
        ibtnFclBack = (ImageButton) findViewById(R.id.ibtn_fcl_back);
    }

    private void initEvent() {
        ibtnFclBack.setOnClickListener(mClickListener);
        xlvCoachList.setOnItemClickListener(mItemClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ibtn_fcl_back:
                    FollowCoachListActivity.this.finish();
                    break;
            }
        }
    };
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplication(), CoachDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("coach", coachList.get(position - 1));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    @Override
    public void onRefresh() {
        if (!TextUtils.isEmpty(linkPrevious)) {
            getCoachList(linkPrevious);
        } else {
            getCoachList();
        }
        mAdapter = new CoachItemAdapter(FollowCoachListActivity.this, coachList, R.layout.view_coach_list_item);
        xlvCoachList.setAdapter(mAdapter);
    }

    @Override
    public void onLoadMore() {
        if (!TextUtils.isEmpty(linkNext)) {
            getCoachList(linkNext);
        } else {
            onLoad();
            //getCoachList();
        }
    }

    private void getCoachList() {
        this.msPresenter.getFollowCoachList(page, per_page, access_token, new MSCallbackListener<CoachListResponse>() {
            @Override
            public void onSuccess(CoachListResponse data) {
                coachList.clear();
                coachList = data.getData();
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
                if(TextUtils.isEmpty(linkNext)){
                    xlvCoachList.setPullLoadEnable(false);
                }else {
                    xlvCoachList.setPullLoadEnable(true);
                }
                mAdapter = new CoachItemAdapter(FollowCoachListActivity.this, coachList, R.layout.view_coach_list_item);
                xlvCoachList.setAdapter(mAdapter);
                onLoad();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCoachList(String url) {
        this.msPresenter.getFollowCoachList(url, access_token, new MSCallbackListener<CoachListResponse>() {
            @Override
            public void onSuccess(CoachListResponse data) {
                coachList.clear();
                coachList = data.getData();
                linkSelf = data.getLinks().getSelf();
                linkNext = data.getLinks().getNext();
                linkPrevious = data.getLinks().getPrevious();
                if(TextUtils.isEmpty(linkNext)){
                    xlvCoachList.setPullLoadEnable(false);
                }else {
                    xlvCoachList.setPullLoadEnable(true);
                }
                mAdapter = new CoachItemAdapter(FollowCoachListActivity.this, coachList, R.layout.view_coach_list_item);
                xlvCoachList.setAdapter(mAdapter);

                onLoad();
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onLoad() {
        xlvCoachList.stopRefresh();
        xlvCoachList.stopLoadMore();
        xlvCoachList.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }
}
