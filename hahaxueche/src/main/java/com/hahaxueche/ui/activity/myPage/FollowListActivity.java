package com.hahaxueche.ui.activity.myPage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.myPage.FollowListPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.activity.findCoach.DrivingSchoolDetailDetailActivity;
import com.hahaxueche.ui.adapter.findCoach.CoachAdapter;
import com.hahaxueche.ui.view.myPage.FollowListView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;
import com.hahaxueche.util.RequestCode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/9.
 */

public class FollowListActivity extends HHBaseActivity implements FollowListView, XListView.IXListViewListener {
    private FollowListPresenter mPresenter;
    @BindView(R.id.xlv_coaches)
    XListView mXlvCoaches;
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;
    private CoachAdapter mCoachAdapter;
    private ArrayList<Coach> mCoachArrayList;
    private String mConsultantPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new FollowListPresenter();
        setContentView(R.layout.activity_follow_list);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        mXlvCoaches.setPullRefreshEnable(true);
        mXlvCoaches.setPullLoadEnable(true);
        mXlvCoaches.setAutoLoadEnable(true);
        mXlvCoaches.setXListViewListener(this);
        mXlvCoaches.setEmptyView(mTvEmpty);
        mPresenter.fetchCoaches();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("我关注的教练");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowListActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setPullLoadEnable(boolean enable) {
        mXlvCoaches.setPullLoadEnable(enable);
    }

    @Override
    public void refreshCoachList(ArrayList<Coach> CoachArrayList) {
        mCoachArrayList = CoachArrayList;
        mCoachAdapter = new CoachAdapter(getContext(), mCoachArrayList, mPresenter.getHotDrivingSchools(this), new CoachAdapter.OnCoachClickListener() {
            @Override
            public void callCoach(String phone) {
                mConsultantPhone = phone;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH);
                } else {
                    contactCoach();
                }
            }

            @Override
            public void clickCoach(Coach coach) {
                Intent intent = new Intent(getContext(), CoachDetailActivity.class);
                intent.putExtra("coach", coach);
                startActivity(intent);
            }

            @Override
            public void clickDrivingSchool(int drivingSchoolId) {
                Intent intent = new Intent(getContext(), DrivingSchoolDetailDetailActivity.class);
                intent.putExtra("drivingSchoolId", drivingSchoolId);
                startActivity(intent);
            }
        });
        mXlvCoaches.setAdapter(mCoachAdapter);
        mXlvCoaches.stopRefresh();
        mXlvCoaches.stopLoadMore();
    }

    @Override
    public void addMoreCoachList(ArrayList<Coach> CoachArrayList) {
        mCoachArrayList.addAll(CoachArrayList);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactCoach();
            } else {
                Toast.makeText(this, "请允许拨打电话权限，不然无法直接拨号联系教练", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 联系教练
     */
    private void contactCoach() {
        if (TextUtils.isEmpty(mConsultantPhone))
            return;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mConsultantPhone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }
}
