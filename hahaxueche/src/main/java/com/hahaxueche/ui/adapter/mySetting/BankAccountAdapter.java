package com.hahaxueche.ui.adapter.mySetting;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.student.Bank;

import java.util.List;

/**
 * Created by wangshirui on 16/8/1.
 */
public class BankAccountAdapter extends BaseAdapter {
    private List<Bank> mBankList;
    private int mResource;   //item的布局
    private Context mContext;
    private LayoutInflater mInflator;

    public BankAccountAdapter(Context context, List<Bank> bankList, int resource) {
        this.mContext = context;
        this.mBankList = bankList;
        this.mResource = resource;
    }

    @Override
    public int getCount() {
        return mBankList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBankList.get(position);
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
            holder.tvName = (TextView) view.findViewById(R.id.tv_bank_name);
            holder.tvRemarks = (TextView) view.findViewById(R.id.tv_bank_remarks);
            holder.ivSelect = (ImageView) view.findViewById(R.id.iv_bank_select);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Bank bank = mBankList.get(position);
        holder.tvName.setText(bank.getBank_name());
        holder.tvRemarks.setText(bank.getAccount_name()+" , "+bank.getAccount().substring(bank.getAccount().length()-4,bank.getAccount().length()));
        if (bank.isSelect()) {
            holder.ivSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_cashout_chack_btn));
        } else {
            holder.ivSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_cashout_unchack_btn));
        }
        return view;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvRemarks;
        ImageView ivSelect;
    }
}
