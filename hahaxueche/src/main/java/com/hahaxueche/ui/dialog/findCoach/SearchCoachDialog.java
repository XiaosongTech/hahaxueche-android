package com.hahaxueche.ui.dialog.findCoach;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.MyApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.presenter.findCoach.FCCallbackListener;
import com.hahaxueche.presenter.findCoach.FCPresenter;
import com.hahaxueche.ui.adapter.findCoach.CoachItemAdapter;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Administrator on 2016/5/24.
 */
public class SearchCoachDialog {
    private Context mContext;
    private Dialog mDialog;
    private View contentView;
    private EditText mEtCoachName;//教练姓名搜索框
    private ImageView mIvClear;//清除按钮
    private TextView mTvSearch;//搜索/取消
    private FrameLayout mFlyMain;
    private LinearLayout mLlyHistory;
    private FCPresenter fcPresenter;
    private SharedPreferencesUtil spUtil;
    private ArrayList<Coach> mCoachList;

    public interface OnCoachItemClicktListener {
        boolean selectCoach(Coach coach);
    }

    private OnCoachItemClicktListener mOnCoachItemClicktListener;

    public SearchCoachDialog(Context context, OnCoachItemClicktListener onCoachItemClicktListener) {
        mDialog = new Dialog(context, R.style.FullScreen_Dialog);//全屏
        mContext = context;
        mOnCoachItemClicktListener = onCoachItemClicktListener;
        fcPresenter = ((MyApplication) mContext.getApplicationContext()).getFCPresenter();
        spUtil = new SharedPreferencesUtil(mContext);
        initView();
        initEvents();
        initSearchHistory();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_search_coach, null);
        mEtCoachName = (EditText) contentView.findViewById(R.id.et_coach_name);
        mTvSearch = (TextView) contentView.findViewById(R.id.tv_search);
        mIvClear = (ImageView) contentView.findViewById(R.id.iv_clear);
        mFlyMain = (FrameLayout) contentView.findViewById(R.id.fly_main);
        mLlyHistory = (LinearLayout) contentView.findViewById(R.id.lly_history);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvents() {
        if (TextUtils.isEmpty(mEtCoachName.getText())) {
            mTvSearch.setText("取消");
            mTvSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
        }
        mEtCoachName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    mIvClear.setVisibility(View.VISIBLE);
                    mTvSearch.setText("搜索");
                    mTvSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //搜索教练
                            searchCoach();
                        }
                    });
                } else {
                    mIvClear.setVisibility(View.GONE);
                    mTvSearch.setText("取消");
                    mTvSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialog.dismiss();
                        }
                    });
                }
            }
        });
        //清空
        mIvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEtCoachName.setText("");
            }
        });
    }


    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /**
     * 搜索教练
     */
    private void searchCoach() {
        final String keyword = mEtCoachName.getText().toString();
        fcPresenter.searchCoach(keyword, new FCCallbackListener<ArrayList<Coach>>() {
            @Override
            public void onSuccess(ArrayList<Coach> coachList) {
                spUtil.addSearchHistory(keyword);
                mFlyMain.removeAllViews();
                mCoachList = coachList;
                if (coachList != null && coachList.size() > 0) {
                    FrameLayout.LayoutParams paramMain = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    ListView listView = new ListView(mContext);
                    listView.setLayoutParams(paramMain);
                    CoachItemAdapter coachItemAdapter = new CoachItemAdapter(mContext, mCoachList, R.layout.view_coach_list_item);
                    listView.setAdapter(coachItemAdapter);
                    listView.setDivider(ContextCompat.getDrawable(mContext, R.drawable.layer_list_divider));
                    listView.setDividerHeight(Util.instence(mContext).dip2px(0.5f));
                    listView.setOnItemClickListener(mAdapterClickListener);
                    ListView.LayoutParams paramsTvFooter = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
                    /***********底部显示***********/
                    TextView tvFooter = new TextView(mContext);
                    tvFooter.setText("没有更多教练");
                    tvFooter.setGravity(Gravity.CENTER);
                    tvFooter.setPadding(0, Util.instence(mContext).dip2px(10), 0, Util.instence(mContext).dip2px(10));
                    tvFooter.setTextSize(14);
                    tvFooter.setTextColor(ContextCompat.getColor(mContext, R.color.haha_gray_light));
                    tvFooter.setLayoutParams(paramsTvFooter);
                    listView.addFooterView(tvFooter);
                    listView.setFooterDividersEnabled(false);
                    mFlyMain.addView(listView);
                } else {
                    //未搜索到教练
                    FrameLayout.LayoutParams paramMain = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    TextView tv = new TextView(mContext);
                    tv.setGravity(Gravity.CENTER);
                    tv.setText("抱歉，没有找到教练");
                    tv.setLayoutParams(paramMain);
                    tv.setTextSize(14);
                    tv.setTextColor(ContextCompat.getColor(mContext, R.color.haha_black_light));
                    mFlyMain.addView(tv);
                }
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AdapterView.OnItemClickListener mAdapterClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mDialog.dismiss();
            mOnCoachItemClicktListener.selectCoach(mCoachList.get(i));
        }
    };

    /**
     * 历史搜索记录
     */
    private void initSearchHistory() {
        LinkedList searchHistoryList = spUtil.getSearchHistory();
        if (searchHistoryList != null && searchHistoryList.size() > 0) {
            for (final Object coachName : searchHistoryList) {
                LinearLayout.LayoutParams paramTv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView textView = new TextView(mContext);
                textView.setLayoutParams(paramTv);
                textView.setText(coachName.toString());
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.haha_black_light));
                textView.setPadding(Util.instence(mContext).dip2px(20), Util.instence(mContext).dip2px(15), 0, Util.instence(mContext).dip2px(15));
                textView.setTextSize(14);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEtCoachName.setText(coachName.toString());
                        searchCoach();
                    }
                });
                mLlyHistory.addView(textView);
                //分割线
                LinearLayout.LayoutParams paramVw = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.instence(mContext).dip2px(0.5f));
                View view = new View(mContext);
                view.setLayoutParams(paramVw);
                view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.haha_gray_light));
                mLlyHistory.addView(view);
            }
            //清除历史记录
            LinearLayout.LayoutParams paramTvClear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramTvClear.setMargins(0, Util.instence(mContext).dip2px(12), 0, 0);
            TextView textView = new TextView(mContext);
            textView.setText("清空历史记录");
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
            textView.setTextSize(14);
            textView.setPadding(Util.instence(mContext).dip2px(36), Util.instence(mContext).dip2px(12), Util.instence(mContext).dip2px(36), Util.instence(mContext).dip2px(12));
            textView.setBackgroundResource(R.drawable.rect_back_transparent_corner_orange);
            textView.setLayoutParams(paramTvClear);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spUtil.clearSearchHistory();
                    mLlyHistory.removeAllViews();
                    //没有历史记录
                    addNoHistoryRecord();
                }
            });
            mLlyHistory.addView(textView);
        } else {
            //没有历史记录
            addNoHistoryRecord();
        }
    }

    private void addNoHistoryRecord() {
        //没有历史记录
        LinearLayout.LayoutParams paramTv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramTv.setMargins(0, Util.instence(mContext).dip2px(20), 0, 0);
        TextView textView = new TextView(mContext);
        textView.setText("暂无历史记录");
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.haha_gray_heavier));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(14);
        textView.setLayoutParams(paramTv);
        mLlyHistory.addView(textView);
    }
}
