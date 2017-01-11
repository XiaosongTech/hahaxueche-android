package com.hahaxueche.ui.dialog.community;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hahaxueche.R;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/25.
 */

public class CommentDialog extends Dialog {
    private Context mContext;
    private EditText etComment;

    public interface OnCommentListener {
        void send(String comment);

        void saveDraft(String draft);

        void clearDraft();
    }

    private OnCommentListener mOnCommentListener;

    public CommentDialog(Context context, OnCommentListener onCommentListener) {
        super(context);
        mContext = context;
        mOnCommentListener = onCommentListener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_comment, null);
        setContentView(view);
        initView(view);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }


    /**
     * 控件初始化
     *
     * @param view
     */
    private void initView(View view) {
        etComment = ButterKnife.findById(view, R.id.et_comment);
        TextView tvCancel = ButterKnife.findById(view, R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etComment.getText().toString();
                if (!TextUtils.isEmpty(comment)) {
                    mOnCommentListener.saveDraft(comment);
                } else {
                    mOnCommentListener.clearDraft();
                }
                dismiss();
            }
        });
        TextView tvSend = ButterKnife.findById(view, R.id.tv_send);
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etComment.getText().toString();
                if (TextUtils.isEmpty(comment)) {
                    Toast.makeText(mContext, "请填写评论~", Toast.LENGTH_SHORT).show();
                    return;
                }
                mOnCommentListener.send(comment);
                dismiss();
            }
        });
    }
}
