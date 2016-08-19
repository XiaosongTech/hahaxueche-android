package com.hahaxueche.ui.adapter.mySetting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.student.Bank;

import java.util.List;

/**
 * Created by wangshirui on 16/8/18.
 */
public class OpenBankAdapter extends BaseAdapter {
    private List<Bank> mBankList;
    private int mResource;   //item的布局
    private Context mContext;
    private LayoutInflater mInflator;

    public OpenBankAdapter(Context context, List<Bank> bankList, int resource) {
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
            holder.tvOpenBankName = (TextView) view.findViewById(R.id.tv_open_bank_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Bank bank = mBankList.get(position);
        holder.tvOpenBankName.setText(bank.getName());
        return view;
    }

    static class ViewHolder {
        TextView tvOpenBankName;
    }
}