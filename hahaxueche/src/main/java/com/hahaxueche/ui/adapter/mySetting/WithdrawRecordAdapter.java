package com.hahaxueche.ui.adapter.mySetting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.student.ReferalBonusTransaction;
import com.hahaxueche.model.student.WithdrawRecord;
import com.hahaxueche.utils.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/3.
 */
public class WithdrawRecordAdapter extends BaseAdapter {
    private int mResource;   //item的布局
    private LayoutInflater mInflator;
    private Context mContext;
    private ArrayList<WithdrawRecord> mWithdrawRecordList;

    public WithdrawRecordAdapter(Context context, ArrayList<WithdrawRecord> withdrawRecordList, int resource) {
        mContext = context;
        mWithdrawRecordList = withdrawRecordList;
        mResource = resource;
    }

    @Override
    public int getCount() {
        return mWithdrawRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWithdrawRecordList.get(position);
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
            holder.tvWithdrawStatus = (TextView) view.findViewById(R.id.tv_withdraw_status);
            holder.tvWithdrawAmount = (TextView) view.findViewById(R.id.tv_withdraw_amount);
            holder.tvWithdrawTime = (TextView) view.findViewById(R.id.tv_withdraw_time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        WithdrawRecord withdrawRecord = mWithdrawRecordList.get(position);
        holder.tvWithdrawAmount.setText(Util.getMoney(withdrawRecord.getAmount()));
        holder.tvWithdrawStatus.setText(withdrawRecord.getStatusLabel());
        holder.tvWithdrawTime.setText(withdrawRecord.getWithdrawed_at());
        return view;
    }

    static class ViewHolder {
        TextView tvWithdrawStatus;
        TextView tvWithdrawAmount;
        TextView tvWithdrawTime;
    }
}
