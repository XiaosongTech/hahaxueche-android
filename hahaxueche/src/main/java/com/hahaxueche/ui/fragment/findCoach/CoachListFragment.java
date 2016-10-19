package com.hahaxueche.ui.fragment.findCoach;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.findCoach.CoachListPresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.activity.findCoach.FieldFilterActivity;
import com.hahaxueche.ui.activity.findCoach.SearchCoachActivity;
import com.hahaxueche.ui.adapter.findCoach.CoachAdapter;
import com.hahaxueche.ui.dialog.findCoach.CoachFilterDialog;
import com.hahaxueche.ui.dialog.findCoach.CoachSortDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.findCoach.CoachListView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wangshirui on 16/9/13.
 */
public class CoachListFragment extends HHBaseFragment implements CoachListView, XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private MainActivity mActivity;
    private CoachListPresenter mPresenter;
    @BindView(R.id.xlv_coaches)
    XListView mXlvCoaches;
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    private CoachAdapter mCoachAdapter;
    private ArrayList<Coach> mCoachArrayList;
    private CoachFilterDialog mFilterDialog;
    private CoachSortDialog mSortDialog;
    //定位client
    public AMapLocationClient mLocationClient;
    //定位回调监听器
    public AMapLocationListener mLocationListener;
    //定位参数
    private AMapLocationClientOption mLocationOption;

    private static final int REQUEST_CODE_COACH_DETAIL = 1;
    private static final int REQUEST_CODE_FIELD_FILTER = 2;
    private static final int PERMISSIONS_REQUEST_LOCATION = 602;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mPresenter = new CoachListPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coach_list, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        mXlvCoaches.setPullRefreshEnable(true);
        mXlvCoaches.setPullLoadEnable(true);
        mXlvCoaches.setAutoLoadEnable(true);
        mXlvCoaches.setXListViewListener(this);
        mXlvCoaches.setOnItemClickListener(this);
        mXlvCoaches.setEmptyView(mTvEmpty);
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            startLocation();
        }
        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();//销毁定位客户端。
        }
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
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
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
            startActivityForResult(intent, REQUEST_CODE_COACH_DETAIL);
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

//    @OnClick(R.id.iv_map)
//    public void clickFieldFilter() {
//        Intent intent = new Intent(getContext(), FieldFilterActivity.class);
//        intent.putParcelableArrayListExtra("selectFields", mPresenter.getSelectFields());
//        startActivityForResult(intent, REQUEST_CODE_FIELD_FILTER);
//    }

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
//
//    @OnClick(R.id.iv_search)
//    public void clickSearchCoach() {
//        startActivity(new Intent(getContext(), SearchCoachActivity.class));
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_COACH_DETAIL) {
            if (resultCode == RESULT_OK && null != data) {
                Coach retCoach = data.getParcelableExtra("coach");
                if (retCoach != null) {
                    for (Coach coach : mCoachArrayList) {
                        if (coach.id.equals(retCoach.id)) {
                            coach.like_count = retCoach.like_count;
                            coach.liked = retCoach.liked;
                            mCoachAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_FIELD_FILTER) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<Field> fields = data.getParcelableArrayListExtra("selectFields");
                if (fields != null) {
                    mPresenter.setSelectFields(fields);
                    mPresenter.fetchCoaches();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                startLocation();
            } else {
                showMessage("请允许使用定位权限，不然我们无法精确的为您推荐教练");
            }
        }
    }

    private void startLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getContext());
        mLocationListener = new AMapLocationListener() {
                @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        mPresenter.setLocation(amapLocation.getLatitude(), amapLocation.getLongitude());
                    } else {
                        String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                        HHLog.e(errText);
                    }
                }
            }
        };
        mLocationClient.setLocationListener(mLocationListener);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000 * 10);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        HHLog.v("create location service");
        mLocationClient.startLocation();
        mPresenter.fetchCoaches();
    }
}
