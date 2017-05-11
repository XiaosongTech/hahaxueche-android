package com.hahaxueche.ui.activity.findCoach;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.findCoach.SearchCoachPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.findCoach.CoachAdapter;
import com.hahaxueche.ui.adapter.findCoach.DrivingSchoolAdapter;
import com.hahaxueche.ui.popupWindow.findCoach.SearchPopupWindow;
import com.hahaxueche.ui.view.findCoach.SearchCoachView;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/10/4.
 */

public class SearchCoachActivity extends HHBaseActivity implements SearchCoachView {
    private EditText mEtCoachName;//教练姓名搜索框
    private ImageView mIvClear;//清除按钮
    private TextView mTvSearch;//搜索/取消
    private TextView mTvSearchType;
    @BindView(R.id.fly_main)
    FrameLayout mFlyMain;
    @BindView(R.id.lly_history)
    LinearLayout mLlyHistory;
    private SearchCoachPresenter mPresenter;
    private String mConsultantPhone;
    private SearchPopupWindow mSearchPopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SearchCoachPresenter();
        setContentView(R.layout.activity_search_coach);
        ButterKnife.bind(this);
        initActionBar();
        mPresenter.attachView(this);
        initEvents();
        mPresenter.searchTextChange(mEtCoachName.getText().toString());
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_search_coach);
        mEtCoachName = ButterKnife.findById(actionBar.getCustomView(), R.id.et_coach_name);
        mIvClear = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_clear);
        mTvSearch = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_search);
        mTvSearchType = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_search_type);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvSearchType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSearchPopWindow == null) {
                    mSearchPopWindow = new SearchPopupWindow(SearchCoachActivity.this, new SearchPopupWindow.OnTypeListener() {
                        @Override
                        public void selectType(int type) {
                            mPresenter.selectSearchType(type);
                        }

                        @Override
                        public void dismiss() {

                        }
                    });
                }
                mSearchPopWindow.showAsDropDown(view);
            }
        });
    }

    private void initEvents() {
        mEtCoachName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.searchTextChange(s.toString());
            }
        });
        mIvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtCoachName.setText("");
            }
        });
    }


    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setRightSearch() {
        mIvClear.setVisibility(View.VISIBLE);
        mTvSearch.setText("搜索");
        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addDataTrack("home_navigation_search_searched", getContext());
                //搜索教练
                mPresenter.searchCoach(mEtCoachName.getText().toString());
            }
        });
    }

    @Override
    public void setRightCancel() {
        mIvClear.setVisibility(View.GONE);
        mTvSearch.setText("取消");
        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchCoachActivity.this.finish();
            }
        });
    }

    @Override
    public void loadSearchHistory(LinkedList searchHistoryList) {
        mLlyHistory.removeAllViews();
        //热门驾校
        mLlyHistory.addView(getHotDrivingSchoolView());
        //分割线
        LinearLayout.LayoutParams vwHotDrivingSchoolParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.instence(this).dip2px(0.5f));
        View vwHotDrivingSchool = new View(this);
        vwHotDrivingSchool.setLayoutParams(vwHotDrivingSchoolParam);
        vwHotDrivingSchool.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
        mLlyHistory.addView(vwHotDrivingSchool);
        //历史记录行
        LinearLayout.LayoutParams paramTvHistory = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tvHistory = new TextView(this);
        tvHistory.setLayoutParams(paramTvHistory);
        tvHistory.setPadding(Utils.instence(this).dip2px(14), Utils.instence(this).dip2px(12), 0, Utils.instence(this).dip2px(12));
        tvHistory.setTextSize(16);
        tvHistory.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
        tvHistory.setText("搜索历史");
        mLlyHistory.addView(tvHistory);
        //分割线
        LinearLayout.LayoutParams paramVwHistory = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.instence(this).dip2px(0.5f));
        View viewHistory = new View(this);
        viewHistory.setLayoutParams(paramVwHistory);
        viewHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
        mLlyHistory.addView(viewHistory);
        //记录信息
        if (searchHistoryList != null && searchHistoryList.size() > 0) {
            for (final Object coachName : searchHistoryList) {
                LinearLayout.LayoutParams paramTv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramTv.setMargins(Utils.instence(this).dip2px(20), 0, 0, 0);
                TextView textView = new TextView(this);
                textView.setLayoutParams(paramTv);
                textView.setText(coachName.toString());
                textView.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
                textView.setPadding(0, Utils.instence(this).dip2px(15), 0, Utils.instence(this).dip2px(15));
                textView.setTextSize(14);
                textView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.icon_search_input), null, null, null);
                textView.setCompoundDrawablePadding(Utils.instence(this).dip2px(5));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEtCoachName.setText(coachName.toString());
                        mPresenter.searchCoach(mEtCoachName.getText().toString());
                    }
                });
                mLlyHistory.addView(textView);
                //分割线
                LinearLayout.LayoutParams paramVw = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.instence(this).dip2px(0.5f));
                paramVw.setMargins(Utils.instence(this).dip2px(20), 0, 0, 0);
                View view = new View(this);
                view.setLayoutParams(paramVw);
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
                mLlyHistory.addView(view);
            }
            //清除历史记录
            LinearLayout.LayoutParams paramTvClear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramTvClear.setMargins(0, Utils.instence(this).dip2px(12), 0, 0);
            TextView textView = new TextView(this);
            textView.setText("清除历史记录");
            textView.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
            textView.setTextSize(14);
            textView.setPadding(Utils.instence(this).dip2px(36), Utils.instence(this).dip2px(12), Utils.instence(this).dip2px(36), Utils.instence(this).dip2px(12));
            textView.setBackgroundResource(R.drawable.rect_bg_trans_bd_appcolor_ssm);
            textView.setLayoutParams(paramTvClear);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.clearHistory();
                }
            });
            mLlyHistory.addView(textView);
        }
    }

    @Override
    public void loadCoachList(final ArrayList<Coach> coachList) {
        mFlyMain.removeAllViews();
        if (coachList != null && coachList.size() > 0) {
            FrameLayout.LayoutParams paramMain = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            ListView listView = new ListView(this);
            listView.setLayoutParams(paramMain);
            CoachAdapter coachItemAdapter = new CoachAdapter(this, coachList, mPresenter.getHotDrivingSchools(this), new CoachAdapter.OnCoachClickListener() {
                @Override
                public void callCoach(String phone) {
                    mPresenter.addDataTrack("search_page_call_coach_tapped", getContext());
                    mConsultantPhone = phone;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH);
                    } else {
                        contactCoach();
                    }
                }

                @Override
                public void clickCoach(Coach coach) {
                    //事件纪录
                    HashMap<String, String> map = new HashMap();
                    map.put("coach_id", coach.id);
                    mPresenter.addDataTrack("search_page_coach_tapped", getContext(), map);
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
            listView.setAdapter(coachItemAdapter);
            listView.setDivider(ContextCompat.getDrawable(this, R.drawable.divider_left_20dp));
            listView.setDividerHeight(Utils.instence(this).dip2px(0.5f));
            ListView.LayoutParams paramsTvFooter = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
            /***********底部显示***********/
            TextView tvFooter = new TextView(this);
            tvFooter.setText("没有更多教练");
            tvFooter.setGravity(Gravity.CENTER);
            tvFooter.setPadding(0, Utils.instence(this).dip2px(10), 0, Utils.instence(this).dip2px(10));
            tvFooter.setTextSize(14);
            tvFooter.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            tvFooter.setLayoutParams(paramsTvFooter);
            listView.addFooterView(tvFooter);
            listView.setFooterDividersEnabled(false);
            mFlyMain.addView(listView);
        } else {
            //未搜索到教练
            FrameLayout.LayoutParams paramMain = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            tv.setText("木有找到匹配的教练，换个关键词重新试试吧～");
            tv.setLayoutParams(paramMain);
            tv.setTextSize(14);
            tv.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            mFlyMain.addView(tv);
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mFlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void disableButton() {
        mIvClear.setClickable(false);
        mTvSearch.setClickable(false);
    }

    @Override
    public void enableButton() {
        mIvClear.setClickable(true);
        mTvSearch.setClickable(true);
    }

    @Override
    public void setSearch(String text) {
        mTvSearchType.setText(text);
    }

    @Override
    public void loadDrivingSchoolList(List<DrivingSchool> drivingSchools) {
        mFlyMain.removeAllViews();
        if (drivingSchools != null && drivingSchools.size() > 0) {
            FrameLayout.LayoutParams paramMain = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            ListView listView = new ListView(this);
            listView.setLayoutParams(paramMain);
            DrivingSchoolAdapter drivingSchoolAdapter = new DrivingSchoolAdapter(this, drivingSchools, mPresenter.getHotDrivingSchools(this),
                    new DrivingSchoolAdapter.OnDrivingSchoolClickListener() {
                        @Override
                        public void callCoach(String phone) {
                            mPresenter.addDataTrack("search_page_call_school_tapped", getContext());
                            mConsultantPhone = phone;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH);
                            } else {
                                contactCoach();
                            }
                        }

                        @Override
                        public void clickDrivingSchool(DrivingSchool drivingSchool) {
                            //事件纪录
                            HashMap<String, String> map = new HashMap();
                            map.put("school_id", String.valueOf(drivingSchool.id));
                            mPresenter.addDataTrack("search_page_hot_school_tapped", getContext(), map);
                            Intent intent = new Intent(getContext(), DrivingSchoolDetailDetailActivity.class);
                            intent.putExtra("drivingSchoolId", drivingSchool.id);
                            startActivity(intent);
                        }
                    });
            listView.setAdapter(drivingSchoolAdapter);
            listView.setDivider(ContextCompat.getDrawable(this, R.drawable.divider_left_20dp));
            listView.setDividerHeight(Utils.instence(this).dip2px(0.5f));
            ListView.LayoutParams paramsTvFooter = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
            /***********底部显示***********/
            TextView tvFooter = new TextView(this);
            tvFooter.setText("没有更多驾校");
            tvFooter.setGravity(Gravity.CENTER);
            tvFooter.setPadding(0, Utils.instence(this).dip2px(10), 0, Utils.instence(this).dip2px(10));
            tvFooter.setTextSize(14);
            tvFooter.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            tvFooter.setLayoutParams(paramsTvFooter);
            listView.addFooterView(tvFooter);
            listView.setFooterDividersEnabled(false);
            mFlyMain.addView(listView);
        } else {
            FrameLayout.LayoutParams paramMain = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            tv.setText("木有找到匹配的驾校，换个关键词重新试试吧～");
            tv.setLayoutParams(paramMain);
            tv.setTextSize(14);
            tv.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_divider));
            mFlyMain.addView(tv);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CONTACT_COACH) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactCoach();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系教练");
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

    private LinearLayout getHotDrivingSchoolView() {
        int margin5dp = Utils.instence(this).dip2px(5);
        int margin8dp = Utils.instence(this).dip2px(8);
        int margin10dp = Utils.instence(this).dip2px(10);
        int margin15dp = Utils.instence(this).dip2px(15);
        int margin20dp = Utils.instence(this).dip2px(20);
        int padding3dp = Utils.instence(this).dip2px(3);

        LinearLayout llyHotDrivingSchool = new LinearLayout(this);
        LinearLayout.LayoutParams llyHotDrivingSchoolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llyHotDrivingSchoolParam.setMargins(0, 0, 0, margin10dp);
        llyHotDrivingSchool.setLayoutParams(llyHotDrivingSchoolParam);
        llyHotDrivingSchool.setBackgroundResource(R.color.haha_white);
        llyHotDrivingSchool.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout rlyHotSearch = new RelativeLayout(this);
        rlyHotSearch.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tvHotSearch = new TextView(this);
        RelativeLayout.LayoutParams tvHotSearchParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvHotSearchParam.setMargins(margin20dp, margin15dp, 0, margin15dp);
        tvHotSearch.setLayoutParams(tvHotSearchParam);
        tvHotSearch.setText("大家都在搜");
        tvHotSearch.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
        tvHotSearch.setTextSize(16);
        int tvHotSearchId = Utils.generateViewId();
        tvHotSearch.setId(tvHotSearchId);
        rlyHotSearch.addView(tvHotSearch);
        TextView tvHot = new TextView(this);
        RelativeLayout.LayoutParams tvHotParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvHotParam.addRule(RelativeLayout.RIGHT_OF, tvHotSearchId);
        tvHotParam.setMargins(margin5dp, margin10dp, 0, 0);
        tvHot.setLayoutParams(tvHotParam);
        tvHot.setText("hot!");
        tvHot.setTextColor(ContextCompat.getColor(this, R.color.haha_red));
        rlyHotSearch.addView(tvHot);
        llyHotDrivingSchool.addView(rlyHotSearch);

        View vwDivider = new View(this);
        vwDivider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                this.getResources().getDimensionPixelSize(R.dimen.divider_width)));
        vwDivider.setBackgroundResource(R.color.haha_gray_divider);
        llyHotDrivingSchool.addView(vwDivider);

        TableLayout tbDrivingSchool = new TableLayout(this);
        LinearLayout.LayoutParams tbDrivingSchoolParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tbDrivingSchoolParam.setMargins(0, 0, 0, margin15dp);
        tbDrivingSchool.setLayoutParams(tbDrivingSchoolParam);
        tbDrivingSchool.setStretchAllColumns(true);

        int maxColCount = 4;
        List<DrivingSchool> hotDrivingSchoolList = mPresenter.getHotDrivingSchools(this);
        for (int row = 0; row < hotDrivingSchoolList.size() / maxColCount; row++) {
            TableRow tr = new TableRow(this);
            TableLayout.LayoutParams trParam = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            trParam.setMargins(0, margin10dp, 0, 0);
            tr.setLayoutParams(trParam);
            for (int col = 0; col < maxColCount; col++) {
                if (row * maxColCount + col > hotDrivingSchoolList.size() - 1) {
                    break;
                }
                final int position = row * maxColCount + col;
                final DrivingSchool drivingSchool = hotDrivingSchoolList.get(position);
                TextView tvDrivingSchool = new TextView(this);
                TableRow.LayoutParams tvDrivingSchoolParam = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                tvDrivingSchoolParam.setMargins(margin8dp, 0, margin8dp, 0);
                tvDrivingSchool.setLayoutParams(tvDrivingSchoolParam);
                tvDrivingSchool.setBackgroundResource(R.drawable.rect_bg_gray_bd_gray_corner);
                tvDrivingSchool.setGravity(Gravity.CENTER);
                tvDrivingSchool.setPadding(0, padding3dp, 0, padding3dp);
                tvDrivingSchool.setText(drivingSchool.name);
                tvDrivingSchool.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
                tvDrivingSchool.setTextSize(12);
                tvDrivingSchool.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //事件纪录
                        HashMap<String, String> map = new HashMap();
                        map.put("index", String.valueOf(position));
                        mPresenter.addDataTrack("search_page_hot_school_tapped", getContext(), map);
                        Intent intent = new Intent(getContext(), DrivingSchoolDetailDetailActivity.class);
                        intent.putExtra("drivingSchoolId", drivingSchool.id);
                        startActivity(intent);
                    }
                });
                tr.addView(tvDrivingSchool);
            }
            tbDrivingSchool.addView(tr);
        }
        llyHotDrivingSchool.addView(tbDrivingSchool);
        return llyHotDrivingSchool;
    }
}
