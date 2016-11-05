package com.hahaxueche.ui.widget.imageSwitcher;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;


import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gibxin on 2016/2/13.
 */
public class PhotoAdapter extends PagerAdapter {
    private List<View> picViews = new ArrayList<View>();
    private Context context = null;

    private OnImgItemClickListener mListener;

    public interface OnImgItemClickListener {
        public void onItemClickEvent(String imgUrl, List<String> urls);
    }

    public void setOnImgItemClickListener(OnImgItemClickListener listener) {
        mListener = listener;
    }

    public void update(List<String> urls) {
        for (String url : urls)
            picViews.add(loadImage(url));
        notifyDataSetChanged();
    }

//    public void update(List<String> urls, int h) {
//        for (String url : urls)
//            picViews.add(loadImage(url, h));
//        notifyDataSetChanged();
//    }
//
//    public void update(int[] res) {
//        for (int i = 0; i < res.length; i++) {
//            picViews.add(loadImage(res[i]));
//        }
//        notifyDataSetChanged();
//    }

    @Override
    public int getCount() {
        return picViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(View v, int position) {
        ((ViewPager) v).addView(picViews.get(position));
        picViews.get(position).setOnClickListener(imgClickListener);
        return picViews.get(position);
    }

    public PhotoAdapter(Context context) {
        super();
        this.context = context;
    }

    private SimpleDraweeView loadImage(String url) {
        SimpleDraweeView mSimpleDraweeView = new SimpleDraweeView(context);
        mSimpleDraweeView.setTag(url);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSimpleDraweeView.setLayoutParams(params);
        GenericDraweeHierarchy hierarchy = mSimpleDraweeView.getHierarchy();
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        mSimpleDraweeView.setAspectRatio(1.2f);
        mSimpleDraweeView.setImageURI(url);
        return mSimpleDraweeView;
    }
//
//    private SimpleDraweeView loadImage(String url, int h) {
//        SimpleDraweeView imageView = new SimpleDraweeView(context);
//        imageView.setTag(url);
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        imageView.setLayoutParams(params);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        Utils.instence(context).loadImgToView(context, url,
//                Utils.instence(context).getDm().widthPixels, h, imageView);
////        Picasso.with(context).load(url).into(imageView);
//        return imageView;
//    }
//
//    private SimpleDraweeView loadImage(int res) {
//        SimpleDraweeView imageView = new SimpleDraweeView(context);
//        imageView.setTag(res);
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        imageView.setLayoutParams(params);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        Picasso.with(context).load(res).into(imageView);
//        return imageView;
//    }

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<String> urls = new ArrayList<String>();
            for (View view : picViews) {
                urls.add(view.getTag().toString());
            }
            if (mListener != null)
                mListener.onItemClickEvent(v.getTag().toString(), urls);
        }
    };
}
