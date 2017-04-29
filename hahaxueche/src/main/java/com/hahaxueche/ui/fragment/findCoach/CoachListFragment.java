package com.hahaxueche.ui.fragment.findCoach;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.hahaxueche.ui.adapter.findCoach.CoachAdapter;
import com.hahaxueche.ui.dialog.BaseAlertSimpleDialog;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.popupWindow.findCoach.SortPopupWindow;
import com.hahaxueche.ui.popupWindow.findCoach.TypePopupWindow;
import com.hahaxueche.ui.view.findCoach.CoachListView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wangshirui on 16/9/13.
 */
public class CoachListFragment extends HHBaseFragment implements CoachListView, XListView.IXListViewListener,
        XListView.OnXScrollListener, AdapterView.OnItemClickListener {
    private MainActivity mActivity;
    private CoachListPresenter mPresenter;
    @BindView(R.id.xlv_coaches)
    XListView mXlvCoaches;
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;
    @BindView(R.id.rly_main)
    RelativeLayout mRlyMain;
    @BindView(R.id.iv_red_bag)
    ImageView mIvRedBag;
    @BindView(R.id.lly_filter)
    LinearLayout mLlyFilter;
    @BindView(R.id.fly_bg_half_trans)
    FrameLayout mFlyBgHalfTrans;
    @BindView(R.id.tv_zone)
    TextView mTvZone;
    @BindView(R.id.tv_price)
    TextView mTvPrice;
    @BindView(R.id.tv_type)
    TextView mTvType;
    @BindView(R.id.tv_sort)
    TextView mTvSort;
    private CoachAdapter mCoachAdapter;
    private ArrayList<Coach> mCoachArrayList;
    private SortPopupWindow mSortPopWindow;
    private TypePopupWindow mTypePopWindow;
    //定位client
    public AMapLocationClient mLocationClient;
    //定位回调监听器
    public AMapLocationListener mLocationListener;

    private final int POP_ZONE = 0;
    private final int POP_PRICE = 1;
    private final int POP_TYPE = 2;
    private final int POP_SORT = 3;

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
        mXlvCoaches.setOnScrollListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RequestCode.PERMISSIONS_REQUEST_LOCATION);
        } else {
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
        Snackbar.make(mRlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showRedBag(boolean isShow) {
        mIvRedBag.setVisibility(isShow ? View.VISIBLE : View.GONE);
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
            mPresenter.clickCoach(mCoachArrayList.get(position - 1).id);
            Intent intent = new Intent(getContext(), CoachDetailActivity.class);
            intent.putExtra("coach", mCoachArrayList.get(position - 1));
            startActivityForResult(intent, RequestCode.REQUEST_CODE_COACH_DETAIL);
        }
    }

    @OnClick({R.id.fly_sort,
            R.id.fly_type,
            R.id.iv_red_bag})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fly_sort:
                if (mSortPopWindow == null) {
                    mSortPopWindow = new SortPopupWindow(getActivity(), new SortPopupWindow.OnSortListener() {
                        @Override
                        public void sort(int sortBy) {
                            mPresenter.clickSortCount(sortBy);
                            mPresenter.setSortBy(sortBy);
                            mPresenter.fetchCoaches();
                        }

                        @Override
                        public void dismiss() {
                            hidePopWindow();
                        }
                    });
                }
                mSortPopWindow.showAsDropDown(view);
                showPopWindow(POP_SORT);
                break;
            case R.id.fly_type:
                if (mTypePopWindow == null) {
                    mTypePopWindow = new TypePopupWindow(getActivity(), new TypePopupWindow.OnTypeClickListener() {
                        @Override
                        public void selectType(int licenseType) {
                            mPresenter.setLicenseType(licenseType);
                            mPresenter.fetchCoaches();
                        }

                        @Override
                        public void clickQuestion(int licenseType) {
                            if (licenseType == Common.LICENSE_TYPE_C1) {
                                BaseAlertSimpleDialog dialog = new BaseAlertSimpleDialog(getContext(), "什么是C1手动档？",
                                        "C1为手动挡小型车驾照，取得了C1类驾驶证的人可以驾驶C2类车");
                                dialog.show();
                            } else {
                                BaseAlertSimpleDialog dialog = new BaseAlertSimpleDialog(getContext(), "什么是C2自动档？",
                                        "C2为自动挡小型车驾照，取得了C2类驾驶证的人不可以驾驶C1类车。" +
                                                "C2驾照培训费要稍贵于C1照。费用的差别主要是由于C2自动挡教练车数量比较少，使用过程中维修费用比较高所致。");
                                dialog.show();
                            }
                        }

                        @Override
                        public void dismiss() {
                            hidePopWindow();
                        }
                    });
                }
                mTypePopWindow.showAsDropDown(view);
                showPopWindow(POP_TYPE);
                break;
            case R.id.iv_red_bag:
                mPresenter.clickRedBag();
                break;
            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_COACH_DETAIL) {
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
            mActivity.controlMyPageBadge();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
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

    public void setSelectFields(ArrayList<Field> selectFields) {
        mPresenter.setSelectFields(selectFields);
        mPresenter.fetchCoaches();
    }

    public ArrayList<Field> getSelectFields() {
        return mPresenter.getSelectFields();
    }

    @Override
    public void onXScrolling(View view) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case XListView.SCROLL_STATE_FLING:
                break;
            case XListView.SCROLL_STATE_IDLE:
                showRedBag();
                break;
            case XListView.SCROLL_STATE_TOUCH_SCROLL:
                dismissRedBag();
                break;
            default:
                break;

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private void dismissRedBag() {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, mIvRedBag.getWidth() * 4 / 5, 0, 0);
        translateAnimation.setDuration(200);
        animationSet.addAnimation(translateAnimation);
        //让其保持动画结束时的状态。
        animationSet.setFillAfter(true);
        mIvRedBag.startAnimation(animationSet);
    }

    private void showRedBag() {
        mIvRedBag.clearAnimation();
    }

    /**
     * 显示下拉窗口
     *
     * @param order
     */
    private void showPopWindow(int order) {
        if (order == POP_ZONE) {
            mTvZone.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_orange), null);
        } else if (order == POP_PRICE) {
            mTvPrice.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_orange), null);
        } else if (order == POP_TYPE) {
            mTvType.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_orange), null);
        } else if (order == POP_SORT) {
            mTvSort.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_orange), null);
        }
        mFlyBgHalfTrans.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏下拉窗口
     */
    private void hidePopWindow() {
        mTvZone.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_gray), null);
        mTvPrice.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_gray), null);
        mTvType.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_gray), null);
        mTvSort.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getContext(), R.drawable.list_arrow_gray), null);
        mFlyBgHalfTrans.setVisibility(View.GONE);
    }

}
