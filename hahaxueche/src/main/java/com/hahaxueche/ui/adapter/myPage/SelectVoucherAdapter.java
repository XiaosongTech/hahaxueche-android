package com.hahaxueche.ui.adapter.myPage;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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

public class SelectVoucherAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<Voucher> mVoucherList;

    public SelectVoucherAdapter(Context context, ArrayList<Voucher> VoucherArrayList) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mVoucherList = VoucherArrayList;
    }

    @Override
    public int getCount() {
        return mVoucherList.size();
    }

    @Override
    public Object getItem(int position) {
        return mVoucherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_select_voucher, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
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
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_amount)
        TextView tvAmount;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_expired_at)
        TextView tvExpiredAt;
        @BindView(R.id.iv_select)
        ImageView ivSelect;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
