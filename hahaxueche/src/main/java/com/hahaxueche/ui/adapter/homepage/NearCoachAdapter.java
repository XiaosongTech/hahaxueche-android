package com.hahaxueche.ui.adapter.homepage;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2017/4/15.
 */

public class NearCoachAdapter extends RecyclerView.Adapter<NearCoachAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Coach> mNearCoaches;
    private OnRecyclerViewItemClickListener mOnItemClickListener;
    private Context mContext;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public NearCoachAdapter(Context context, ArrayList<Coach> coaches, OnRecyclerViewItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        mNearCoaches = coaches;
        mOnItemClickListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_near_coach, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Coach coach = mNearCoaches.get(position);
        holder.mTvPrice.setText(Utils.getMoney(coach.coach_group.training_cost));
        holder.mIvAvatar.setImageURI(coach.avatar);
        holder.mTvName.setText(coach.name);
        if (!TextUtils.isEmpty(coach.distance)) {
            String distance = "距您" + Utils.getDistance(Double.parseDouble(coach.distance));
            SpannableString ss = new SpannableString(distance);
            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.app_theme_color)), distance.indexOf("距您") + 2, distance.indexOf("KM"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.mTvLocation.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            holder.mTvLocation.setText(ss);
        } else {
            HHBaseApplication application = HHBaseApplication.get(mContext);
            holder.mTvLocation.setText(application.getConstants().getSectionName(coach.coach_group.field_id));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mNearCoaches.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_price)
        TextView mTvPrice;
        @BindView(R.id.iv_avatar)
        SimpleDraweeView mIvAvatar;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_location)
        TextView mTvLocation;

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
