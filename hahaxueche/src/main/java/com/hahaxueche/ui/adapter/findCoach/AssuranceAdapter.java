package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.AssuranceProperty;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/12/26.
 */

public class AssuranceAdapter extends RecyclerView.Adapter<AssuranceAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<AssuranceProperty> mAssuranceList;
    private Context mContext;

    public AssuranceAdapter(Context context, ArrayList<AssuranceProperty> assuranceList) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        mAssuranceList = assuranceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_assurance, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AssuranceProperty assuranceProperty = mAssuranceList.get(position);
        holder.ivIcon.setImageDrawable(ContextCompat.getDrawable(mContext, assuranceProperty.icon_drawable));
        holder.tvProperty.setText(assuranceProperty.property);
        holder.tvDescription.setText(assuranceProperty.description);
        holder.tvStatus.setText(assuranceProperty.status);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mAssuranceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_property)
        TextView tvProperty;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.tv_status)
        TextView tvStatus;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
