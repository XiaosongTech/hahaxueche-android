package com.hahaxueche.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hahaxueche.ui.util.ScaleTransformation;
import com.hahaxueche.ui.widget.zoomImageView.ZoomImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gibxin on 2016/2/13.
 */
public class ZoomImgAdapter extends PagerAdapter{
    private List<View> picViews = new ArrayList<View>();
    private Context mContext = null;
    private ProgressBar mLoadingView;

    public void update(List<String> urls)
    {
        if (urls != null && urls.size() > 0)
        {
            if (picViews != null)
                picViews.clear();
            for (String url : urls)
                picViews.add(loadImage(url));
            notifyDataSetChanged();
        }
    }

    public void setLoadingView(ProgressBar mLoadingView)
    {
        this.mLoadingView = mLoadingView;
    }

    @Override
    public int getCount()
    {
        return picViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o)
    {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        ((ViewPager) container).removeView(picViews.get(position));
    }

    @Override
    public Object instantiateItem(View v, int position)
    {
        // TODO Auto-generated method stub
        ((ViewPager) v).addView(picViews.get(position));
        return picViews.get(position);
    }

    public ZoomImgAdapter(Context context)
    {
        super();
        this.mContext = context;
    }

    private ZoomImageView loadImage(String url)
    {
        ZoomImageView imageView = new ZoomImageView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        Picasso.with(mContext).load(Uri.parse(url))
                .transform(new ScaleTransformation(mContext)).into(imageView, new Callback()
        {
            @Override
            public void onSuccess()
            {
                mLoadingView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError()
            {
                mLoadingView.setVisibility(View.INVISIBLE);
            }
        });
        return imageView;
    }
}
