package com.hahaxueche.ui.adapter.homepage;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/4/15.
 */

public class HotDrivingSchoolAdapter extends RecyclerView.Adapter<HotDrivingSchoolAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<DrivingSchool> mHotDrivingSchools;
    private OnRecyclerViewItemClickListener mOnItemClickListener;
    private Context mContext;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public HotDrivingSchoolAdapter(Context context, List<DrivingSchool> drivingSchools, OnRecyclerViewItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        mHotDrivingSchools = drivingSchools;
        mOnItemClickListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_hot_driving_school, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DrivingSchool drivingSchool = mHotDrivingSchools.get(position);
        String text = Utils.getMoney(drivingSchool.lowest_price) + "起";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.haha_gray)), text.indexOf("起"), text.indexOf("起") + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(Utils.instence(mContext).sp2px(12)), text.indexOf("起"), text.indexOf("起") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.mTvPrice.setText(ss);
        holder.mIvAvatar.setImageURI(drivingSchool.avatar);
        holder.mTvName.setText(drivingSchool.name);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mHotDrivingSchools.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_price)
        TextView mTvPrice;
        @BindView(R.id.iv_avatar)
        SimpleDraweeView mIvAvatar;
        @BindView(R.id.tv_name)
        TextView mTvName;

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
