package com.hahaxueche.ui.widget.imageSwitcher;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hahaxueche.utils.Util;

import java.util.List;
/**
 * Created by gibxin on 2016/2/13.
 */
public class ImageSwitcher extends FrameLayout{
    private ViewPager viewPager = null;
    private Indicator indicator = null;
    private Context context = null;
    private PhotoAdapter photoAdapter = null;

    public OnSwitchItemClickListener mListener;

    public interface  OnSwitchItemClickListener{
        public void onSwitchClick(String url, List<String> urls);
    }

    public ImageSwitcher(Context context) {
        super(context);
        init(context);
    }

    public ImageSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setOnSwitchItemClickListener(OnSwitchItemClickListener listener){
        mListener = listener;
    }


    private void init(Context context){
        this.context = context;
        addViewPager();
        addIndicator();
    }

    private void addViewPager(){
        viewPager = new ViewPager(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(viewPager, params);
        viewPager.setOnPageChangeListener(photoPageChangeListener);
    }

    private void addIndicator(){
        indicator = new Indicator(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Util.instence(context).dip2px(12), Gravity.BOTTOM);
        params.bottomMargin = Util.instence(context).dip2px(8);
        addView(indicator, params);
    }

    public void setIndicatorRadius(int radius){
        indicator.setRadius(radius);
    }

    public void setIndicatorDivide(int divide){
        indicator.setDivide(divide);
    }

    public void updateImages(List<String> urls){
        if (photoAdapter == null){
            photoAdapter = new PhotoAdapter(context);
            photoAdapter.setOnImgItemClickListener(onImgItemClickListener);
        }
        viewPager.setAdapter(photoAdapter);
        photoAdapter.update(urls);
        indicator.setTotal(urls.size());
        indicator.setCurrent(0);
        if(urls.size()<2){
            indicator.setVisibility(View.INVISIBLE);
        }else{
            indicator.setVisibility(View.VISIBLE);
        }
    }

    public void updateImages(List<String> urls, int h){
        if (photoAdapter == null){
            photoAdapter = new PhotoAdapter(context);
            photoAdapter.setOnImgItemClickListener(onImgItemClickListener);
        }
        viewPager.setAdapter(photoAdapter);
        photoAdapter.update(urls, h);
        indicator.setTotal(urls.size());
        indicator.setCurrent(0);
        if(urls.size()<2){
            indicator.setVisibility(View.INVISIBLE);
        }else{
            indicator.setVisibility(View.VISIBLE);
        }
    }

    public void updateImages(int[] res){
        if (photoAdapter == null){
            photoAdapter = new PhotoAdapter(context);
            photoAdapter.setOnImgItemClickListener(onImgItemClickListener);
        }
        viewPager.setAdapter(photoAdapter);
        photoAdapter.update(res);
        indicator.setTotal(res.length);
        indicator.setCurrent(0);
        indicator.setRadius(Util.instence(context).dip2px(3));
        indicator.setDivide(Util.instence(context).dip2px(13));
        if(res.length<2){
            indicator.setVisibility(View.INVISIBLE);
        }else{
            indicator.setVisibility(View.VISIBLE);
        }
    }

    PhotoAdapter.OnImgItemClickListener onImgItemClickListener = new PhotoAdapter.OnImgItemClickListener()
    {
        @Override
        public void onItemClickEvent(String url, List<String> urls)
        {
            if(mListener!= null)
                mListener.onSwitchClick(url, urls);
        }
    };


    ViewPager.OnPageChangeListener photoPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            indicator.setCurrent(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
