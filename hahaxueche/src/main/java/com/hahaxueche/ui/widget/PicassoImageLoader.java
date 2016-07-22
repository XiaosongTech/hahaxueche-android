package com.hahaxueche.ui.widget;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.hahaxueche.utils.HahaCache;
import com.hahaxueche.utils.Util;
import com.qiyukf.unicorn.api.ImageLoaderListener;
import com.qiyukf.unicorn.api.UnicornImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

/**
 * Created by wangshirui on 16/7/20.
 */
public class PicassoImageLoader implements UnicornImageLoader {

    @Nullable
    @Override
    public Bitmap loadImageSync(String uri, int width, int height) {
        return null;
    }

    @Override
    public void loadImage(final String uri, final int width, final int height, final ImageLoaderListener listener) {
        // loadImage可能在任何线程调用，RequestCreator.into只能在UI线程调用，因此这里需要包装一下。
        // Utils.runOnUiThread 实现可见 demo 源码
        Util.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RequestCreator requestCreator = Picasso.with(HahaCache.context).load(uri);
                if (width > 0 && height > 0) {
                    requestCreator = requestCreator.resize(width, height);
                }
                requestCreator.into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (listener != null) {
                            listener.onLoadComplete(bitmap);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        if (listener != null) {
                            listener.onLoadFailed(null);
                        }
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        });
    }
}
