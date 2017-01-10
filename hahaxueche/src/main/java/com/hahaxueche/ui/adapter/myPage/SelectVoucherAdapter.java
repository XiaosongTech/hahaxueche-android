package com.hahaxueche.ui.adapter.myPage;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/12.
 */

public class SelectVoucherAdapter extends RecyclerView.Adapter<SelectVoucherAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<Voucher> mVoucherList;
    private OnRecyclerViewItemClickListener mOnItemClickListener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public SelectVoucherAdapter(Context context, ArrayList<Voucher> VoucherArrayList, OnRecyclerViewItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mVoucherList = VoucherArrayList;
        mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_select_voucher, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Voucher voucher = mVoucherList.get(position);
        holder.tvAmount.setText(Utils.getMoney(voucher.amount));
        holder.tvTitle.setText(voucher.title);
        if (!TextUtils.isEmpty(voucher.expired_at)) {
            holder.tvExpiredAt.setText("有效期至 " + voucher.expired_at);
        } else {
            holder.tvExpiredAt.setText("长期有效");
        }
        holder.ivSelect.setImageDrawable(ContextCompat.getDrawable(mContext,
                voucher.isSelect ? R.drawable.ic_cashout_chack_btn : R.drawable.ic_cashout_unchack_btn));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mVoucherList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_amount)
        TextView tvAmount;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_expired_at)
        TextView tvExpiredAt;
        @BindView(R.id.iv_select)
        ImageView ivSelect;

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
