package com.hahaxueche.ui.adapter.mySetting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.student.ReferalBonusTransaction;
import com.hahaxueche.utils.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/3.
 */
public class RedeemedAdapter extends BaseAdapter {
    private int mResource;   //item的布局
    private LayoutInflater mInflator;
    private Context mContext;
    private ArrayList<ReferalBonusTransaction> mRedeemedList;

    public RedeemedAdapter(Context context, ArrayList<ReferalBonusTransaction> redeemedList, int resource) {
        mContext = context;
        mRedeemedList = redeemedList;
        mResource = resource;
    }

    @Override
    public int getCount() {
        return mRedeemedList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRedeemedList.get(position);
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
            holder.tvRedeemedTime = (TextView) view.findViewById(R.id.tv_withdraw_time);
            holder.tvReferAmount = (TextView) view.findViewById(R.id.tv_refer_amount);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ReferalBonusTransaction referalBonusTransaction = mRedeemedList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfTime = new SimpleDateFormat("MM-dd HH:mm");
        try {
            Date time = sdf.parse(referalBonusTransaction.getCreated_at());
            holder.tvRedeemedTime.setText(sdfTime.format(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tvReferAmount.setText(Util.getMoney(referalBonusTransaction.getBonus_amount()));
        return view;
    }

    static class ViewHolder {
        TextView tvRedeemedTime;
        TextView tvReferAmount;
    }
}
