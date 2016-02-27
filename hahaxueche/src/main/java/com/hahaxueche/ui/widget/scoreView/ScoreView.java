package com.hahaxueche.ui.widget.scoreView;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.utils.Util;

import java.text.DecimalFormat;

/**
 * Created by luwei on 15-8-1.
 */
public class ScoreView extends LinearLayout{
    protected Context context = null;
    private int divide = 0;
    public static final int TOTAL = 5;
    protected float score = 0;
    private TextView scoreNum = null;
    public ScoreView(Context context) {
        super(context);
        this.context = context;
        init(getDivide());
    }

    public ScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(getDivide());
    }

    protected void init(int d){
        divide = Util.instence(context).dip2px(d);
        setOrientation(HORIZONTAL);
        for (int i = 0 ; i < TOTAL; i++){
            addScore(divide,getEmptyStarId());
        }
    }

    public void setScore(float score,boolean showText){
        float tempScore = score;
        this.score = score;
        short temp = (short)(TOTAL - score);
        int i = 0;
        while (score > 0){
            if (score >= 1)
                getChildAt(i).setBackgroundResource(getFullStarId());
            else
                getChildAt(i).setBackgroundResource(getHalfStarId());
            score--;
            i++;
        }
        while (temp > 0){
            getChildAt(i).setBackgroundResource(getEmptyStarId());
            temp--;
            i++;
        }
        if (showText)
            addScoreText(tempScore);
    }

    public float getScore(){
        return score;
    }

    private void addScore(int leftMargin,int id){
        ImageView view = new ImageView(context);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = leftMargin;
        view.setBackgroundResource(id);
        view.setLayoutParams(params);
        addView(view);
    }

    private void addScoreText(float score){
        if (scoreNum == null){
            scoreNum = new TextView(context);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 2 * divide;
            scoreNum.setTextColor(Color.parseColor("#ff9e00"));
            scoreNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            TextPaint tp = scoreNum.getPaint();
            tp.setFakeBoldText(true);
            addView(scoreNum,params);
        }
        DecimalFormat df2  = new DecimalFormat("###.0");
        scoreNum.setText(String.valueOf(df2.format(score)));
    }

    protected int getFullStarId(){
        return R.drawable.ic_stars_light;
    }

    protected int getEmptyStarId(){
        return R.drawable.ic_stars_grey;
    }

    protected int getHalfStarId(){
        return R.drawable.ic_stars_half;
    }

    protected int getDivide(){
        return 2;
    }

}
