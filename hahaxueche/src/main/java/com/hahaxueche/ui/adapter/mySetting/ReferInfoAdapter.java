package com.hahaxueche.ui.adapter.mySetting;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.student.Referee;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by gibxin on 2016/4/29.
 */
public class ReferInfoAdapter extends BaseAdapter {
    private int mResource;   //item的布局
    private LayoutInflater mInflator;
    private Context mContext;
    private ArrayList<Referee> mRefereeList;

    public ReferInfoAdapter(Context context, ArrayList<Referee> refereeList, int resource) {
        mContext = context;
        mRefereeList = refereeList;
        mResource = resource;
    }

    @Override
    public int getCount() {
        return mRefereeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRefereeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflator.inflate(mResource, null);
            holder = new ViewHolder();
            holder.tvRefereePhone = (TextView) view.findViewById(R.id.tv_referee_phone);
            holder.tvRefereeName = (TextView) view.findViewById(R.id.tv_referee_name);
            holder.tvReferState = (TextView) view.findViewById(R.id.tv_refer_state);
            holder.tvReferAmount = (TextView) view.findViewById(R.id.tv_refer_amount);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Referee referee = mRefereeList.get(position);
        holder.tvRefereeName.setText(referee.getName());
        holder.tvRefereePhone.setText(referee.getPhone());
        holder.tvReferAmount.setText(Util.getMoney(10000));
        if (TextUtils.isEmpty(referee.getPurchased_at())) {
            holder.tvReferState.setText("已预约，还没有报名教练");
            holder.tvReferAmount.setTextColor(ContextCompat.getColor(mContext, R.color.haha_gray_heavier));
        } else {
            holder.tvReferState.setText("已经报名教练并付款");
            holder.tvReferAmount.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
        }
        return view;
    }

    static class ViewHolder {
        TextView tvRefereeName;
        TextView tvReferState;
        TextView tvReferAmount;
        TextView tvRefereePhone;
    }
}


