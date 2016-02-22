package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
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
    private ImageView ivIsGoldenCoach;//是否金牌教练
    private ScoreView svCoachScore;//得分星
    private TextView tvCoachLocation;//地点

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
            ivIsGoldenCoach = (ImageView) convertView.findViewById(R.id.iv_is_golden_coach);
            svCoachScore = (ScoreView) convertView.findViewById(R.id.sv_coach_score);
            tvCoachLocation = (TextView) convertView.findViewById(R.id.tv_coach_location);
        }
        DecimalFormat dfInt = new DecimalFormat("#####");
        CoachModel coach = coachList.get(position);
        tvCoachName.setText(coach.getName());
        double coachExperiences = 0d;
        if (!TextUtils.isEmpty(coach.getExperiences())) {
            coachExperiences = Double.parseDouble(coach.getExperiences());
        }
        tvCoachTeachTime.setText(dfInt.format(coachExperiences) + "年教龄");
        tvCoachPoints.setText(coach.getAverage_rating());
        double traingCost = 0d;
        if (!TextUtils.isEmpty(coach.getCoach_group().getTraing_cost())) {
            traingCost = Double.parseDouble(coach.getCoach_group().getTraing_cost());
        }
        tvCoachActualPrice.setText("￥" + dfInt.format(traingCost) + "");
        double marketPrice = 0d;
        if (!TextUtils.isEmpty(coach.getCoach_group().getMarket_price())) {
            marketPrice = Double.parseDouble(coach.getCoach_group().getMarket_price());
        }
        tvCoachOldPrice.setText("￥" + dfInt.format(marketPrice));
        tvCoachOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        getCoachAvatar(coach.getAvatar_url(), civCoachAvatar);
        if (coach.getSkill_level().equals("1")) {
            ivIsGoldenCoach.setVisibility(View.VISIBLE);
        } else {
            ivIsGoldenCoach.setVisibility(View.GONE);
        }
        svCoachScore.setScore(Float.parseFloat(coach.getAverage_rating()), false);
        return convertView;
    }

    private void getCoachAvatar(String url, CircleImageView civCoachAvatar) {
        final int iconWidth = Util.instence(context).dip2px(60);
        final int iconHeight = iconWidth;
        Picasso.with(context).load(url).resize(iconWidth, iconHeight)
                .into(civCoachAvatar);
    }
}
