package com.hahaxueche.ui.adapter.myPage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.Bank;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/2.
 */

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Bank> mBankList;
    private OnRecyclerViewItemClickListener mOnItemClickListener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public BankAdapter(Context context, ArrayList<Bank> BankArrayList, OnRecyclerViewItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        mBankList = BankArrayList;
        mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_bank, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bank bank = mBankList.get(position);
        holder.tvOpenBankName.setText(bank.name);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mBankList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_open_bank_name)
        TextView tvOpenBankName;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(itemView, getAdapterPosition());
            }
        }
    }
}
