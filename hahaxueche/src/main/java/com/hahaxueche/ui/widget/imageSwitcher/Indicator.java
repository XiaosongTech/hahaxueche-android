package com.hahaxueche.ui.widget.imageSwitcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by gibxin on 2016/2/13.
 */
public class Indicator extends View {
    private int total = 0;
    private int current = 0;
    private int currentColor = 0xffffffff;
    private int otherColor = 0xb0ffffff;
    private int radius = 4;
    private int divide = 10;
    private Paint paint = new Paint();

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Indicator(Context context) {

        super(context);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setCurrent(int current) {
        this.current = current;
        invalidate();
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setDivide(int divide) {
        this.divide = divide;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int tempX = (getWidth() - (total * (2 * radius + divide) - divide)) / 2 + divide;
        int tempY = getHeight() / 2;
        for (int i = 0; i < total; i++) {
            if (i == current)
                paint.setColor(currentColor);
            else
                paint.setColor(otherColor);
            canvas.drawCircle(tempX, tempY, radius, paint);
            tempX += divide;
        }
        super.onDraw(canvas);
    }
}
