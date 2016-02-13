package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.ui.util.circleImageView.CircleImageView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 教练item
 * Created by gibxin on 2016/2/13.
 */
public class CoachItemAdapter extends BaseAdapter {
    private List<CoachModel> coachList;
    private int resource;   //item的布局
    private Context context;
    private LayoutInflater inflator;
    private TextView tvCoachName;//教练姓名
    private TextView tvCoachTeachTime;//教龄
    private TextView tvCoachPoints;//教练评分
    private TextView tvCoachActualPrice;//实际价格
    private TextView tvCoachOldPrice;//原价
    private CircleImageView civCoachAvatar;//教练头像

    public CoachItemAdapter(Context context, List<CoachModel> coachList, int resource) {
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
        if (convertView == null) {
            inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(resource, null);
            tvCoachName = (TextView) convertView.findViewById(R.id.tv_coach_name);   //为了减少开销，则只在第一页时调用findViewById
            tvCoachTeachTime = (TextView) convertView.findViewById(R.id.tv_coach_teach_time);
            tvCoachPoints = (TextView) convertView.findViewById(R.id.tv_coach_points);
            tvCoachActualPrice = (TextView) convertView.findViewById(R.id.tv_coach_actual_price);
            tvCoachOldPrice = (TextView) convertView.findViewById(R.id.tv_coach_old_price);
            civCoachAvatar = (CircleImageView) convertView.findViewById(R.id.cir_coach_avatar);
        }
        CoachModel coach = coachList.get(position);
        tvCoachName.setText(coach.getCoachName());
        tvCoachTeachTime.setText(coach.getCoachTeachTime());
        tvCoachPoints.setText(coach.getCoachPoints());
        tvCoachActualPrice.setText(coach.getCoachActualPrice());
        tvCoachOldPrice.setText(coach.getCoachOldPrice());
        getCoachAvatar("http://haha-staging.oss-cn-shanghai.aliyuncs.com/uploads/student/avatar/06812c2b-9dea-4bdc-bbde-b9516627b206/20160213_111453.jpg",civCoachAvatar);
        return convertView;
    }

    private void getCoachAvatar(String url,CircleImageView civCoachAvatar){
        final int iconWidth = Util.instence(context).dip2px(60);
        final int iconHeight = iconWidth;
        Picasso.with(context).load(url).resize(iconWidth, iconHeight)
                .into(civCoachAvatar);
    }
}
