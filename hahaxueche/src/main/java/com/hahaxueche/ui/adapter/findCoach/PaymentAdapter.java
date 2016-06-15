package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.student.Payment;

import java.util.List;

/**
 * Created by Administrator on 2016/5/27.
 */
public class PaymentAdapter extends BaseAdapter {
    private List<Payment> mPaymentList;
    private int mResource;   //item的布局
    private Context mContext;
    private LayoutInflater mInflator;

    public PaymentAdapter(Context context, List<Payment> paymentList, int resource) {
        this.mContext = context;
        this.mPaymentList = paymentList;
        this.mResource = resource;
    }

    @Override
    public int getCount() {
        return mPaymentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPaymentList.get(position);
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
            holder.ivLogo = (ImageView) view.findViewById(R.id.iv_payment_logo);
            holder.tvName = (TextView) view.findViewById(R.id.tv_payment_name);
            holder.tvRemarks = (TextView) view.findViewById(R.id.tv_payment_remarks);
            holder.ivSelect = (ImageView) view.findViewById(R.id.iv_payment_select);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Payment payment = mPaymentList.get(position);
        holder.ivLogo.setImageDrawable(ContextCompat.getDrawable(mContext, payment.getDrawableLogo()));
        holder.tvName.setText(payment.getName());
        if (payment.isActive()) {
            holder.tvRemarks.setText(payment.getRemarks());
        } else {
            holder.tvRemarks.setText("暂未开通");
        }
        if (payment.isSelect()) {
            holder.ivSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_cashout_chack_btn));
        } else {
            holder.ivSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_cashout_unchack_btn));
        }
        if (!payment.isActive()) {
            //未开通的支付方式，所有控件alpha 50%
            holder.ivLogo.setAlpha(0.5f);
            holder.tvName.setAlpha(0.5f);
            holder.tvRemarks.setAlpha(0.5f);
            holder.ivSelect.setAlpha(0.5f);
        }
        return view;
    }

    static class ViewHolder {
        ImageView ivLogo;
        TextView tvName;
        TextView tvRemarks;
        ImageView ivSelect;
    }
}
