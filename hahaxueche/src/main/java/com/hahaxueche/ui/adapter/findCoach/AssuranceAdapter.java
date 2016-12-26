package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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

public class AssuranceAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<AssuranceProperty> mAssuranceList;
    private Context mContext;

    public AssuranceAdapter(Context context, ArrayList<AssuranceProperty> assuranceList) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        mAssuranceList = assuranceList;
    }

    @Override
    public int getCount() {
        return mAssuranceList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAssuranceList.get(position);
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
            convertView = inflater.inflate(R.layout.adapter_assurance, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        AssuranceProperty assuranceProperty = mAssuranceList.get(position);
        holder.ivIcon.setImageDrawable(ContextCompat.getDrawable(mContext, assuranceProperty.icon_drawable));
        holder.tvProperty.setText(assuranceProperty.property);
        holder.tvDescription.setText(assuranceProperty.description);
        holder.tvStatus.setText(assuranceProperty.status);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_property)
        TextView tvProperty;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.tv_status)
        TextView tvStatus;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
