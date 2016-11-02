package com.hahaxueche.ui.adapter.myPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.payment.WithdrawRecord;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/1.
 */

public class WithdrawRecordAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<WithdrawRecord> mWithdrawRecordList;

    public WithdrawRecordAdapter(Context context, ArrayList<WithdrawRecord> WithdrawRecordArrayList) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mWithdrawRecordList = WithdrawRecordArrayList;
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
        WithdrawRecordAdapter.ViewHolder holder;
        if (convertView != null) {
            holder = (WithdrawRecordAdapter.ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_withdraw_record, parent, false);
            holder = new WithdrawRecordAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        WithdrawRecord withdrawRecord = mWithdrawRecordList.get(position);
        holder.tvWithdrawAmount.setText(Utils.getMoney(withdrawRecord.amount));
        holder.tvWithdrawStatus.setText(withdrawRecord.getStatusLabel());
        holder.tvWithdrawTime.setText(withdrawRecord.withdrawed_at);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_withdraw_status)
        TextView tvWithdrawStatus;
        @BindView(R.id.tv_withdraw_amount)
        TextView tvWithdrawAmount;
        @BindView(R.id.tv_withdraw_time)
        TextView tvWithdrawTime;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
