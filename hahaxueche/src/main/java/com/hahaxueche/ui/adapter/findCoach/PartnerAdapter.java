package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.Partner;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class PartnerAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Partner> mPartnerList;

    public PartnerAdapter(Context context, ArrayList<Partner> PartnerList) {
        mContext = context;
        mPartnerList = PartnerList;
    }

    @Override
    public int getCount() {
        return mPartnerList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPartnerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.adapter_partner, null);
            holder = new ViewHolder();
            holder.tvPartnerName = ButterKnife.findById(view, R.id.tv_partner_name);
            holder.tvTeachTime = ButterKnife.findById(view, R.id.tv_teach_time);
            holder.tvPrice = ButterKnife.findById(view, R.id.tv_price);
            holder.ivPartnerAvatar = ButterKnife.findById(view, R.id.iv_partner_avatar);
            holder.tvApplaudCount = ButterKnife.findById(view, R.id.tv_applaud_count);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Partner partner = mPartnerList.get(position);
        holder.tvPartnerName.setText(partner.name);
        holder.ivPartnerAvatar.setImageURI(partner.avatar);
        holder.tvTeachTime.setText(partner.experiences + "年教龄");
        holder.tvPrice.setText(Utils.getMoney(partner.prices.get(0).price));
        holder.tvApplaudCount.setText(String.valueOf(partner.like_count));
        return view;
    }

    static class ViewHolder {
        TextView tvPartnerName;
        TextView tvTeachTime;
        TextView tvPrice;
        SimpleDraweeView ivPartnerAvatar;
        TextView tvApplaudCount;
    }
}
