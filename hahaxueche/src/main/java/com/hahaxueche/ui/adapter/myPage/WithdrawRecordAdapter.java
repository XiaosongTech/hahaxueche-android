package com.hahaxueche.ui.adapter.myPage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

public class WithdrawRecordAdapter extends RecyclerView.Adapter<WithdrawRecordAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<WithdrawRecord> mWithdrawRecordList;

    public WithdrawRecordAdapter(Context context, ArrayList<WithdrawRecord> WithdrawRecordArrayList) {
        inflater = LayoutInflater.from(context);
        mWithdrawRecordList = WithdrawRecordArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_withdraw_record, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WithdrawRecord withdrawRecord = mWithdrawRecordList.get(position);
        holder.tvWithdrawAmount.setText(Utils.getMoney(withdrawRecord.amount));
        holder.tvWithdrawStatus.setText(withdrawRecord.getStatusLabel());
        holder.tvWithdrawTime.setText(withdrawRecord.withdrawed_at);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mWithdrawRecordList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_withdraw_status)
        TextView tvWithdrawStatus;
        @BindView(R.id.tv_withdraw_amount)
        TextView tvWithdrawAmount;
        @BindView(R.id.tv_withdraw_time)
        TextView tvWithdrawTime;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
