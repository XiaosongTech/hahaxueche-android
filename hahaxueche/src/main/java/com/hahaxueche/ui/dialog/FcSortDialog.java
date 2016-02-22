package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;

/**
 * 寻找教练，排序dialog
 * Created by gibxin on 2016/1/30.
 */
public class FcSortDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private OnBtnClickListener mListener;
    private LinearLayout llySmartSort;
    private LinearLayout llyDistanceClose;
    private LinearLayout llyCommentNice;
    private LinearLayout llyPriceLow;
    private LinearLayout llyPopularHot;
    private ImageView ivSmartSort;
    private ImageView ivDistanceClose;
    private ImageView ivCommentNice;
    private ImageView ivPriceLow;
    private ImageView ivPopularHot;
    private TextView tvSmartSort;
    private TextView tvDistanceClose;
    private TextView tvCommentNice;
    private TextView tvPriceLow;
    private TextView tvPopularHot;

    public FcSortDialog(Context context, OnBtnClickListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_find_coach_sort, null);
        setContentView(view);
        initView(view);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
        lp.x = 20; // 新位置X坐标
        lp.y = 180; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

    public interface OnBtnClickListener {
        public void onFindCoachCort(String sortby);
    }

    private void initView(View view) {
        llySmartSort = (LinearLayout) view.findViewById(R.id.lly_smart_sort);
        llyDistanceClose = (LinearLayout) view.findViewById(R.id.lly_distance_close);
        llyCommentNice = (LinearLayout) view.findViewById(R.id.lly_comment_nice);
        llyPriceLow = (LinearLayout) view.findViewById(R.id.lly_price_low);
        llyPopularHot = (LinearLayout) view.findViewById(R.id.lly_popular_hot);
        llySmartSort.setOnClickListener(this);
        llyDistanceClose.setOnClickListener(this);
        llyCommentNice.setOnClickListener(this);
        llyPriceLow.setOnClickListener(this);
        llyPopularHot.setOnClickListener(this);
        ivSmartSort = (ImageView) view.findViewById(R.id.iv_smart_sort);
        ivDistanceClose = (ImageView) view.findViewById(R.id.iv_distance_close);
        ivCommentNice = (ImageView) view.findViewById(R.id.iv_comment_nice);
        ivPriceLow = (ImageView) view.findViewById(R.id.iv_price_low);
        ivPopularHot = (ImageView) view.findViewById(R.id.iv_popular_hot);
        tvSmartSort = (TextView) view.findViewById(R.id.tv_smart_sort);
        tvDistanceClose = (TextView) view.findViewById(R.id.tv_distance_close);
        tvCommentNice = (TextView) view.findViewById(R.id.tv_comment_nice);
        tvPriceLow = (TextView) view.findViewById(R.id.tv_price_low);
        tvPopularHot = (TextView) view.findViewById(R.id.tv_popular_hot);
    }

    private void resetViews() {
        ivSmartSort.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sort_auto_normal_btn));
        ivDistanceClose.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sort_local_normal_btn));
        ivCommentNice.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sort_nice_normal_btn));
        ivPriceLow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sort_price_normal_btn));
        ivPopularHot.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sort_hot_normal_btn));
        tvSmartSort.setTextColor(mContext.getResources().getColor(R.color.fCTxtGray));
        tvDistanceClose.setTextColor(mContext.getResources().getColor(R.color.fCTxtGray));
        tvCommentNice.setTextColor(mContext.getResources().getColor(R.color.fCTxtGray));
        tvPriceLow.setTextColor(mContext.getResources().getColor(R.color.fCTxtGray));
        tvPopularHot.setTextColor(mContext.getResources().getColor(R.color.fCTxtGray));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lly_smart_sort:
                resetViews();
                ivSmartSort.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sort_auto_hold_btn));
                tvSmartSort.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
                mListener.onFindCoachCort("0");
                this.dismiss();
                break;
            case R.id.lly_distance_close:
                resetViews();
                ivDistanceClose.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sort_local_hold_btn));
                tvDistanceClose.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
                mListener.onFindCoachCort("1");
                this.dismiss();
                break;
            case R.id.lly_comment_nice:
                resetViews();
                ivCommentNice.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sort_nice_hold_btn));
                tvCommentNice.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
                mListener.onFindCoachCort("2");
                this.dismiss();
                break;
            case R.id.lly_price_low:
                resetViews();
                ivPriceLow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sort_price_hold_btn));
                tvPriceLow.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
                mListener.onFindCoachCort("3");
                this.dismiss();
                break;
            case R.id.lly_popular_hot:
                resetViews();
                ivPopularHot.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sort_hot_hold_btn));
                tvPopularHot.setTextColor(mContext.getResources().getColor(R.color.app_theme_color));
                mListener.onFindCoachCort("4");
                this.dismiss();
                break;
        }
    }
}
