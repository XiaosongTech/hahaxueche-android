package com.hahaxueche.ui.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.Display;
import android.view.WindowManager;

import com.squareup.picasso.Transformation;

/**
 * Created by gibxin on 2016/2/13.
 */
public class ScaleTransformation implements Transformation {
    private Context mContext;
    private Display display;
    private Rect rect = new Rect();

    public ScaleTransformation(Context context)
    {
        mContext = context;
        WindowManager m = ((Activity) mContext).getWindowManager();
        display = m.getDefaultDisplay(); // 获取屏幕宽、高用
        display.getRectSize(rect);
    }

    @Override
    public Bitmap transform(Bitmap source)
    {
        int bmpWidth = source.getWidth();
        int bmpHeight = source.getHeight();
        Bitmap newBitMap = null;

        newBitMap = PictrueGet.zoomBitmap(source, rect.width(), rect.height());
//        if (bmpHeight > rect.height() || bmpWidth > rect.width())
//        {
//            newBitMap = PictrueGet.zoomBitmap(source, rect.width(), rect.height());
//        } else
//        {
//            newBitMap = source;
//        }
        if (source != newBitMap)
        {
            source.recycle();
        }
        return newBitMap;
    }

    @Override
    public String key()
    {
        return "square()";
    }
}
