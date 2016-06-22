package com.hahaxueche.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.ui.widget.scoreView.ClickScoreView;

/**
 * 评论dialog
 * Created by gibxin on 2016/3/2.
 */
public class ReviewDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private boolean mIsShowTitle = true;
    private String mPaymentStageName;
    private LinearLayout llyReviewTitle;
    private TextView tvReviewInfoTitle;
    private ClickScoreView clvScore;
    private EditText etReview;
    private TextView tvSureReview;
    private TextView tvLaterReview;
    private OnBtnClickListener mListener;
    private String mPaymentStageNumber;
    private String mCoachUserId;

    public interface OnBtnClickListener {
        public void onReview(String review, float score, String paymentStageNumber, String coachUserId);
    }

    public ReviewDialog(Context context, boolean isShowTitle, String paymentStageNumber, String coachUserId, String paymentStageName, OnBtnClickListener listener) {
        super(context);
        mContext = context;
        mIsShowTitle = isShowTitle;
        mPaymentStageName = paymentStageName;
        mListener = listener;
        mPaymentStageNumber = paymentStageNumber;
        mCoachUserId = coachUserId;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_review, null);
        setContentView(view);
        llyReviewTitle = (LinearLayout) view.findViewById(R.id.lly_review_title);
        tvReviewInfoTitle = (TextView) view.findViewById(R.id.tv_review_info_title);
        clvScore = (ClickScoreView) view.findViewById(R.id.clv_score);
        //默认5星
        clvScore.setScore(5f, false);
        etReview = (EditText) view.findViewById(R.id.et_review);
        tvSureReview = (TextView) view.findViewById(R.id.tv_sure_review);
        tvLaterReview = (TextView) view.findViewById(R.id.tv_later_review);
        loadDatas();
        initEvents();
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        //lp.x = 100; // 新位置X坐标
        //lp.y = 150; // 新位置Y坐标
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.width = (int) (d.getWidth() * 0.9);
        //lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    private void loadDatas() {
        if (!mIsShowTitle) {
            llyReviewTitle.setVisibility(View.GONE);
        } else {
            tvReviewInfoTitle.setText(mPaymentStageName + "打款成功！");
        }
        etReview.setHint(String.format(mContext.getResources().getString(R.string.myReviewHint), mPaymentStageName));
    }

    private void initEvents() {
        tvSureReview.setOnClickListener(this);
        tvLaterReview.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure_review:
                float score = clvScore.getScore();
                if (score == 0.0f) {
                    Toast.makeText(mContext, "亲，还没有打分哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                String review = etReview.getText().toString();
                if (mListener != null) {
                    mListener.onReview(review, score, mPaymentStageNumber, mCoachUserId);
                }
                break;
            case R.id.tv_later_review:
                dismiss();
                break;
        }
    }

    ;
}
