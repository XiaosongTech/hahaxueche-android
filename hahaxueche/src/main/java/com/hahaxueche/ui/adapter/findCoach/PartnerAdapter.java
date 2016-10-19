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
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class PartnerAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<Partner> mPartnerList;
    private HHBaseApplication application;
    private LayoutInflater inflator;

    public PartnerAdapter(Context context, ArrayList<Partner> PartnerList) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mPartnerList = PartnerList;
        application = HHBaseApplication.get(mContext);
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
            inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.adapter_partner, null);
            holder = new ViewHolder();
            holder.tvPartnerName = ButterKnife.findById(view, R.id.tv_partner_name);
            holder.tvTeachTime = ButterKnife.findById(view, R.id.tv_teach_time);
            holder.tvPartnerPoints = ButterKnife.findById(view, R.id.tv_partner_points);
            holder.tvPrice = ButterKnife.findById(view, R.id.tv_price);
            holder.ivPartnerAvatar = ButterKnife.findById(view, R.id.iv_partner_avatar);
            holder.svPartnerScore = ButterKnife.findById(view, R.id.sv_partner_score);
            holder.tvApplaudCount = ButterKnife.findById(view, R.id.tv_applaud_count);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Partner Partner = mPartnerList.get(position);
        holder.tvPartnerName.setText(Partner.name);
        holder.ivPartnerAvatar.setImageURI(Partner.avatar);
        holder.tvTeachTime.setText(Partner.experiences + "年教龄");
        holder.tvPartnerPoints.setText(Partner.average_rating + " (" + Partner.review_count + ")");
        holder.tvPrice.setText(Utils.getMoney(Partner.coach_group.training_cost));
        float score = Float.parseFloat(Partner.average_rating);
        if (score > 5) {
            score = 5;
        }
        holder.svPartnerScore.setScore(score, false);
        holder.tvApplaudCount.setText(String.valueOf(Partner.like_count));
        return view;
    }

    static class ViewHolder {
        TextView tvPartnerName;
        TextView tvTeachTime;
        TextView tvPartnerPoints;
        TextView tvPrice;
        SimpleDraweeView ivPartnerAvatar;
        ScoreView svPartnerScore;
        TextView tvApplaudCount;
    }
}
