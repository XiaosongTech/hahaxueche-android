package com.hahaxueche.ui.dialog.login;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hahaxueche.R;
import com.hahaxueche.util.Utils;

import java.util.List;

/**
 * Created by gibxin on 2016/2/10.
 */
public class ButtonLayout extends LinearLayout implements View.OnClickListener {
    private Button cancel = null;
    private Context context = null;
    private int index = 0;
    private OnButtonClickListener buttonClickListener = null;
    private boolean addCancel = true;

    @Override
    public void onClick(View v) {
        if (buttonClickListener != null)
            buttonClickListener.onClick(v, (int) v.getTag());
    }

    public interface OnButtonClickListener {
        public void onClick(View v, int index);
    }

    public ButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        buttonClickListener = listener;
    }

    public ButtonLayout(Context context) {
        super(context);
        init(context);
    }

    public ButtonLayout(Context context, boolean cancel) {
        super(context);
        this.addCancel = cancel;
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setOrientation(VERTICAL);
        setGravity(Gravity.BOTTOM);
        setPadding(Utils.instence(context).dip2px(8), 0, Utils.instence(context).dip2px(8), Utils.instence(context).dip2px(8));
    }

    public void setButtonTxt(String[] txts) {
        LinearLayout layout = addLinearLayout();
        int len = txts.length;
        for (int i = 0; i < len; i++) {
            if (i > 0)
                addDivideLine(layout);
            addButton(layout, txts[i], R.color.coach_info_blue);
        }
        if (addCancel)
            addCancelButton();
    }

    private LinearLayout addLinearLayout() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(VERTICAL);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = Utils.instence(context).dip2px(8);
        addView(layout, params);
        layout.setBackgroundResource(R.drawable.coach_dialog_normal_btn_bg);
        return layout;
    }

    public void setButtonText(String[] txts, int[] color) {
        LinearLayout layout = addLinearLayout();
        int len = txts.length;
        for (int i = 0; i < len; i++) {
            if (i > 0)
                addDivideLine(layout);
            if (!addCancel && i == (len - 1))
                addLastButton(this, txts[i], color[i]);
            else
                addButton(layout, txts[i], color[i]);
        }
    }


    public void setButtonTxt(List<String> txts) {
        String[] temp = (String[]) txts.toArray(new String[txts.size()]);
        setButtonTxt(temp);
    }

    private void addDivideLine(LinearLayout layout) {
        ImageView view = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Utils.instence(context).dip2px(1));
        view.setBackgroundColor(Color.parseColor("#d8d8da"));
        layout.addView(view, params);
    }

    private Button addButton(LinearLayout layout, String txt, int color) {
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(params);
        button.setTextColor(getResources().getColor(color));
        button.setOnClickListener(this);
        button.setText(txt);
        button.setTag(index++);
        button.setBackgroundResource(android.R.color.transparent);
        layout.addView(button);
        return button;
    }

    private void addLastButton(LinearLayout layout, String txt, int color) {
        addButton(layout, txt, color).setBackgroundResource(R.drawable.coach_dialog_cancel_btn_bg);
    }

    private void addCancelButton() {
        Button button = addButton(this, "取消", R.color.coach_info_blue);
        button.setBackgroundResource(R.drawable.coach_dialog_cancel_btn_bg);
    }
}
