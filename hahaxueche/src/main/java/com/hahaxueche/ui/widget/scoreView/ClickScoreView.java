package com.hahaxueche.ui.widget.scoreView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hahaxueche.R;


/**
 * Created by luwei on 15-8-26.
 */
public class ClickScoreView extends ScoreView implements View.OnClickListener,View.OnTouchListener{
    public ClickScoreView(Context context) {
        super(context);
    }

    public ClickScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(int d) {
        super.init(d);
        for (int i = 0; i < TOTAL; i++){
            getChildAt(i).setClickable(true);
            getChildAt(i).setOnTouchListener(this);
            getChildAt(i).setId(i);
        }
    }

    @Override
    public void onClick(View v) {
        setScore(v.getId() + 1,false);
    }

    @Override
    protected int getFullStarId() {
        return R.drawable.ic_assessstar_full_big;
    }

    @Override
    protected int getEmptyStarId() {
        return R.drawable.ic_assessstar_empty_big;
    }

    @Override
    protected int getHalfStarId() {
        return R.drawable.ic_assessstar_full_big;
    }

    @Override
    protected int getDivide() {
        return 6;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            float x = event.getX()/v.getWidth();
            if (x > 0.8f)
                setScore(v.getId() + 1,false);
            else if(x < 0.2f)
                setScore(v.getId() + 0.2f,false);
            else
                setScore(v.getId() + Math.round(x*10f)/10f,false);
        }
        return false;
    }
}
