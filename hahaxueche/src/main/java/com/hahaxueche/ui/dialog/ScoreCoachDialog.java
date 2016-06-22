package com.hahaxueche.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;
import com.hahaxueche.ui.widget.scoreView.ClickScoreView;
import com.hahaxueche.utils.Util;

/**
 * Created by gibxin on 2016/4/20.
 */
public class ScoreCoachDialog {
    private Context mContext;
    private ClickScoreView csvScore;
    private Dialog mDialog;
    private View contentView;
    private TextView tvConfirm;
    private TextView tvCancel;
    private onScoreListener mOnScoreListener;

    public interface onScoreListener {
        public void onScore(float score);
    }

    public ScoreCoachDialog(Context context, onScoreListener onScoreListener) {
        mDialog = new Dialog(context, R.style.my_dialog);
        mContext = context;
        mOnScoreListener = onScoreListener;
        initView();
        initEvent();
        setDialogParams();
    }

    private void initView() {
        contentView = View.inflate(mContext, R.layout.dialog_score, null);
        csvScore = (ClickScoreView) contentView.findViewById(R.id.csv_score);
        tvConfirm = (TextView) contentView.findViewById(R.id.tv_dialog_score_confirm);
        tvCancel = (TextView) contentView.findViewById(R.id.tv_dialog_score_cancel);
        //默认5星
        csvScore.setScore(5f, false);
        mDialog.setContentView(contentView);
        mDialog.dismiss();
    }

    private void initEvent() {
        tvConfirm.setOnClickListener(mClickListener);
        tvCancel.setOnClickListener(mClickListener);
    }

    public void show() {
        mDialog.show();
        if (contentView != null)
            contentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.in_downup));
    }

    public void dismiss() {
        if (contentView != null)
            contentView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.out_updown));
        if (mDialog.isShowing())
            mDialog.dismiss();
    }

    private void setDialogParams() {
        Window window = mDialog.getWindow(); //得到对话框
        WindowManager.LayoutParams wl = window.getAttributes();
        //wl.width = WindowManager.LayoutParams.MATCH_PARENT;
        wl.width = Util.instence(mContext).getDm().widthPixels * 9 / 10;
        wl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //wl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL; //设置重力
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_dialog_score_confirm:
                    float score = csvScore.getScore();
                    if (score == 0.0f) {
                        Toast.makeText(mContext, "亲，还没有打分哦~", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mDialog.dismiss();
                    mOnScoreListener.onScore(score);
                    break;
                case R.id.tv_dialog_score_cancel:
                    mDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };
}
