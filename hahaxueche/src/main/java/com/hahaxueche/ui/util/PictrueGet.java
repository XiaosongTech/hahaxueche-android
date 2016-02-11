package com.hahaxueche.ui.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * Created by gibxin on 2016/2/10.
 */
public class PictrueGet {
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height)
    {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        // matrix.postScale(scaleWidth, scaleHeight);
        matrix.postScale(Math.min(scaleWidth, scaleHeight), Math.min(scaleWidth, scaleHeight));
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    // 添加图片圆角
    public static Bitmap createCircleImage(Bitmap bitmapSource, float radius)
    {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setFilterBitmap(true);
        Bitmap target_bm = Bitmap.createBitmap((int) radius * 2, (int) radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target_bm);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapSource, 0, 0, paint);

        return target_bm;
    }

    // 可用于生成缩略图。

    /**
     * Creates a centered bitmap of the desired size. Recycles the input.
     *
     * @param source
     */
    public static Bitmap extractMiniThumb(Bitmap source, int width, int height)
    {
        return extractMiniThumb(source, width, height, true);
    }

    public static Bitmap extractMiniThumb(Bitmap source, int width, int height, boolean recycle)
    {
        if (source == null)
        {
            return null;
        }

        float scale;
        if (source.getWidth() < source.getHeight())
        {
            scale = width / (float) source.getWidth();
        } else
        {
            scale = height / (float) source.getHeight();
        }
        Matrix matrix = new Matrix();
        matrix.postScale(width / (float) source.getWidth(), height / (float) source.getHeight());
        // matrix.setScale(scale, scale);
        Bitmap miniThumbnail = transform(matrix, source, width, height, false);
        // Bitmap miniThumbnail = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
        // source.getHeight(), matrix, true);// 按缩放比例生成适应屏幕的新的bitmap；

        if (recycle && miniThumbnail != source)
        {
            source.recycle();
        }
        return miniThumbnail;
    }

    public static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight,
                                   boolean scaleUp)
    {
        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0))
        {
            /*
             * In this case the bitmap is smaller, at least in one dimension, than the target.
             * Transform it by placing as much of the image as possible into the target and leaving
             * the top/bottom or left/right (or both) black.
             */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
                    + Math.min(targetWidth, source.getWidth()), deltaYHalf
                    + Math.min(targetHeight, source.getHeight()));
            int dstX = (targetWidth - src.width()) / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
            c.drawBitmap(source, src, dst, null);
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect)
        {
            float scale = targetHeight / bitmapHeightF;
            if (scale < .9F || scale > 1F)
            {
                scaler.setScale(scale, scale);
            } else
            {
                scaler = null;
            }
        } else
        {
            float scale = targetWidth / bitmapWidthF;
            if (scale < .9F || scale > 1F)
            {
                scaler.setScale(scale, scale);
            } else
            {
                scaler = null;
            }
        }

        Bitmap b1;
        if (scaler != null)
        {
            // this is used for minithumb and crop, so we want to filter here.
            b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler,
                    true);
        } else
        {
            b1 = source;
        }

        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);

        if (b1 != source)
        {
            b1.recycle();
        }

        return b2;
    }
}
