package com.hahaxueche.ui.widget.comboSeekBar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.hahaxueche.ui.widget.comboSeekBar.ComboSeekBar.Dot;
import com.hahaxueche.util.HHLog;

import java.util.List;

/**
 * seekbar background with text on it.
 *
 * @author sazonov-adm
 */
public class CustomDrawable extends Drawable {

    private final Drawable myBase;
    private final Paint textUnselected;
    private float mThumbRadius;
    /**
     * paints.
     */
    private final Paint unselectLinePaint;
    private List<Dot> mDots;
    private Paint selectLinePaint;
    private Paint selectCirclePaint;
    private Paint unSelectCirclePaint;
    private int mDotRadius;
    private Paint textSelected;
    private int mTextSize;
    private float mTextMargin;
    private int mTextHeight;
    private boolean mIsMultiline;
    private int mSelectedLineHeight;
    private int mUnselectedLineHeight;
    private int mSelectedLineColor;

    public CustomDrawable(Drawable base, ComboSeekBar slider,
                          float thumbRadius, List<Dot> dots, int color, int textSize,
                          int selectedLineHeight, int unselectedLineHeight, int dotRadius,
                          boolean isMultiline, int selectedLineColor,int unSelectedColor) {
        mIsMultiline = isMultiline;
        myBase = base;
        mDots = dots;
        mTextSize = textSize;
        mSelectedLineHeight = selectedLineHeight;
        mUnselectedLineHeight = unselectedLineHeight;
        mSelectedLineColor = selectedLineColor;
        mDotRadius = dotRadius;
        textUnselected = new Paint(Paint.ANTI_ALIAS_FLAG);
        textUnselected.setColor(unSelectedColor);
        //textUnselected.setAlpha(255);

        textSelected = new Paint(Paint.ANTI_ALIAS_FLAG);
        //textSelected.setTypeface(Typeface.DEFAULT_BOLD);
        textSelected.setColor(color);
        //textSelected.setAlpha(255);

        mThumbRadius = thumbRadius;

        unselectLinePaint = new Paint();
        unselectLinePaint.setColor(unSelectedColor);

        unselectLinePaint.setStrokeWidth(mUnselectedLineHeight);

        selectLinePaint = new Paint();
        selectLinePaint.setColor(mSelectedLineColor);
        selectLinePaint.setStrokeWidth(mSelectedLineHeight);

        selectCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectCirclePaint.setColor(mSelectedLineColor);

        unSelectCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unSelectCirclePaint.setColor(unSelectedColor);


        Rect textBounds = new Rect();
        //textSelected.setTextSize((int) (mTextSize * 2));
        //textSelected.getTextBounds("M", 0, 1, textBounds);

        textUnselected.setTextSize(mTextSize);
        textSelected.setTextSize(mTextSize);

        mTextHeight = textBounds.height();
        // mDotRadius = toPix(DOT_RADIUS);
        mTextMargin = 30;
    }


    @Override
    protected final void onBoundsChange(Rect bounds) {
        myBase.setBounds(bounds);
    }

    @Override
    protected final boolean onStateChange(int[] state) {
        invalidateSelf();
        return false;
    }

    @Override
    public final boolean isStateful() {
        return true;
    }

    @Override
    public final void draw(Canvas canvas) {
        int height = this.getIntrinsicHeight() / 2;
        if (mDots.size() == 0) {
            canvas.drawLine(0, height, getBounds().right, height,
                    unselectLinePaint);
            return;
        }
        int selectIndex = 0;

        for (int i = 0; i < mDots.size(); i++) {
            Dot dot = mDots.get(i);
            if (dot.isSelected) {
                selectIndex = i;
            }
        }
        for (int i = 0; i < mDots.size(); i++) {
            Dot dot = mDots.get(i);
            drawText(canvas, dot, dot.mX, height,selectIndex);
            if (i == selectIndex) {
                canvas.drawLine(mDots.get(0).mX, height, dot.mX, height,
                        selectLinePaint);
                canvas.drawLine(dot.mX, height, mDots.get(mDots.size() - 1).mX,
                        height, unselectLinePaint);
            }
            if (i <= selectIndex) {
                canvas.drawCircle(dot.mX, height, mDotRadius, selectCirclePaint);
            } else {
                canvas.drawCircle(dot.mX, height, mDotRadius, unSelectCirclePaint);
            }
        }
    }

    /**
     * @param canvas canvas.
     * @param dot    current dot.
     * @param x      x cor.
     * @param y      y cor.
     */
    private void drawText(Canvas canvas, Dot dot, float x, float y,int selectIndex) {
        final Rect textBounds = new Rect();
        textSelected.getTextBounds(dot.text, 0, dot.text.length(), textBounds);
        float xres;
        if (dot.id == (mDots.size() - 1)) {
            xres = getBounds().width() - textBounds.width();
        } else if (dot.id == 0) {
            xres = 0;
        } else {
            xres = x - (textBounds.width() / 2);
        }

        float yres;
        if (mIsMultiline) {
            if ((dot.id % 2) == 0) {
                yres = y - mTextMargin - mDotRadius;
            } else {
                yres = y + mTextHeight;
            }
        } else {
            yres = y + (mDotRadius * 2) + mTextMargin;
        }
        if(dot.id<=selectIndex){
            canvas.drawText(dot.text, xres, yres, textSelected);
        }else {
            canvas.drawText(dot.text, xres, yres, textUnselected);
        }
        /*if (dot.isSelected) {
            canvas.drawText(dot.text, xres, yres, textSelected);
        } else {
            canvas.drawText(dot.text, xres, yres, textUnselected);
        }*/
    }

    @Override
    public final int getIntrinsicHeight() {
        if (mIsMultiline) {
            return (int) (selectLinePaint.getStrokeWidth() + mDotRadius
                    + (mTextHeight) * 2 + mTextMargin);
        } else {
            int thumbHeight = (int) mThumbRadius * 2;
            int dotHeight = (int) mDotRadius * 2;
            return (int) (Math.max(thumbHeight, dotHeight) + 100);
            // return (int) (mThumbRadius * 2);
            // return (int) (mThumbRadius + mTextMargin + mTextHeight +
            // mDotRadius);
        }
    }

    @Override
    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    public void setDots(List<Dot> dots) {
        mDots = dots;
    }
}