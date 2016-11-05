package com.hahaxueche.ui.adapter.myPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.Bank;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/2.
 */

public class BankAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<Bank> mBankList;

    public BankAdapter(Context context, ArrayList<Bank> BankArrayList) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mBankList = BankArrayList;
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
        BankAdapter.ViewHolder holder;
        if (convertView != null) {
            holder = (BankAdapter.ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_bank, parent, false);
            holder = new BankAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        Bank bank = mBankList.get(position);
        holder.tvOpenBankName.setText(bank.name);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_open_bank_name)
        TextView tvOpenBankName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
