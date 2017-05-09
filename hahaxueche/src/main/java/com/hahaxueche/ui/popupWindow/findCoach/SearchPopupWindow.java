package com.hahaxueche.ui.popupWindow.findCoach;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.Utils;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/5/9.
 */

public class SearchPopupWindow extends PopupWindow {
    private Activity mActivity;
    private View contentView;
    private TextView mTvDrivingSchool;
    private TextView mTvCoach;
    private OnTypeListener mOnTypeListener;

    public interface OnTypeListener {
        void selectType(int type);

        void dismiss();
    }

    public SearchPopupWindow(Activity activity, OnTypeListener listener) {
        super(activity);
        this.mActivity = activity;
        this.mOnTypeListener = listener;
        initPopupWindow();
        initEvent();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mOnTypeListener.dismiss();
            }
        });
    }

    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.pop_search_type, null);
        setContentView(contentView);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(Utils.instence(mActivity).dip2px(100));
        setOutsideTouchable(true);  //默认设置outside点击无响应
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable(mActivity.getResources(), (Bitmap) null));
        mTvDrivingSchool = ButterKnife.findById(contentView, R.id.tv_driving_school);
        mTvCoach = ButterKnife.findById(contentView, R.id.tv_coach);
    }

    private void initEvent() {
        mTvDrivingSchool.setOnClickListener(mClickListener);
        mTvCoach.setOnClickListener(mClickListener);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_driving_school:
                    mOnTypeListener.selectType(Common.SEARCH_TYPE_DRIVING_SCHOOL);
                    dismiss();
                    break;
                case R.id.tv_coach:
                    mOnTypeListener.selectType(Common.SEARCH_TYPE_COACH);
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };

}
