package com.hahaxueche.ui.widget.bannerView;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by gibxin on 2016/2/18.
 */
public class NetworkImageHolderView implements Holder<String> {
    private SimpleDraweeView mSimpleDraweeView;

    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        mSimpleDraweeView = new SimpleDraweeView(context);
        mSimpleDraweeView.setScaleType(ImageView.ScaleType.FIT_XY);
        return mSimpleDraweeView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        mSimpleDraweeView.setImageURI(Uri.parse(data));
    }
}
