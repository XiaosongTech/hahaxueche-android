package com.hahaxueche.ui.adapter.myPage;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.user.Referrer;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/2.
 */

public class ReferrerAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<Referrer> mReferrerList;

    public ReferrerAdapter(Context context, ArrayList<Referrer> ReferrerArrayList) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mReferrerList = ReferrerArrayList;
    }

    @Override
    public int getCount() {
        return mReferrerList.size();
    }

    @Override
    public Object getItem(int position) {
        return mReferrerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReferrerAdapter.ViewHolder holder;

        if (convertView != null) {
            holder = (ReferrerAdapter.ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_referrer, parent, false);
            holder = new ReferrerAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        Referrer referrer = mReferrerList.get(position);
        holder.tvRefereeName.setText(referrer.name);
        holder.tvRefereePhone.setText(referrer.phone);
        holder.tvReferAmount.setText(Utils.getMoney(referrer.amount));
        holder.tvReferState.setText(referrer.sales_status);
        if (TextUtils.isEmpty(referrer.purchased_at)) {
            holder.tvReferAmount.setTextColor(ContextCompat.getColor(mContext, R.color.haha_gray_text));
        } else {
            holder.tvReferAmount.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_referee_name)
        TextView tvRefereeName;
        @BindView(R.id.tv_refer_state)
        TextView tvReferState;
        @BindView(R.id.tv_refer_amount)
        TextView tvReferAmount;
        @BindView(R.id.tv_referee_phone)
        TextView tvRefereePhone;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
