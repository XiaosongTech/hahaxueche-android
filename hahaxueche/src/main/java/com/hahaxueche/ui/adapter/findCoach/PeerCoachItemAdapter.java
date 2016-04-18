package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.coach.BriefCoachInfo;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gibxin on 2016/3/25.
 */
public class PeerCoachItemAdapter extends BaseAdapter {
    private List<BriefCoachInfo> coachList;
    private int resource;   //item的布局
    private Context context;
    private LayoutInflater inflator;

    public PeerCoachItemAdapter(Context context, List<BriefCoachInfo> coachList, int resource) {
        this.context = context;
        this.coachList = coachList;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return coachList.size();
    }

    @Override
    public Object getItem(int position) {
        return coachList.get(position);
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
            inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(resource, null);
            holder = new ViewHolder();
            holder.tvCoachName = (TextView) view.findViewById(R.id.tv_peer_coach);   //为了减少开销，则只在第一页时调用findViewById
            holder.civCoachAvatar = (CircleImageView) view.findViewById(R.id.cir_peer_coach);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        BriefCoachInfo coach = coachList.get(position);
        holder.tvCoachName.setText(coach.getName());
        getCoachAvatar(coach.getAvatar(), holder.civCoachAvatar);
        return view;
    }

    private void getCoachAvatar(String url, CircleImageView civCoachAvatar) {
        final int iconWidth = Util.instence(context).dip2px(40);
        final int iconHeight = iconWidth;
        Picasso.with(context).load(url).resize(iconWidth, iconHeight)
                .into(civCoachAvatar);
    }

    static class ViewHolder {
        TextView tvCoachName;
        CircleImageView civCoachAvatar;
    }
}
