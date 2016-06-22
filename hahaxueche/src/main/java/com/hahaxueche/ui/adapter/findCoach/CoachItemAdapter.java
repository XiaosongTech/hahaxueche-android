package com.hahaxueche.ui.adapter.findCoach;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.coach.Coach;
import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.city.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.ui.dialog.MapDialog;
import com.hahaxueche.ui.util.DistanceUtil;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.utils.SharedPreferencesUtil;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 教练item
 * Created by gibxin on 2016/2/13.
 */
public class CoachItemAdapter extends BaseAdapter {
    private List<Coach> coachList;
    private int resource;   //item的布局
    private Context context;
    private LayoutInflater inflator;
    private Constants mConstants;
    private List<FieldModel> fieldsList;
    private List<City> cityList;
    private MapDialog mapDialog;
    private FieldModel mFieldModel;
    private SharedPreferencesUtil spUtil;
    private String myLat;
    private String myLng;

    public CoachItemAdapter(Context context, List<Coach> coachList, int resource) {
        this.context = context;
        this.coachList = coachList;
        this.resource = resource;
        spUtil = new SharedPreferencesUtil(context);
        mConstants = spUtil.getConstants();
        if (mConstants != null) {
            fieldsList = mConstants.getFields();
            cityList = mConstants.getCities();
        }
        if (spUtil.getLocation() != null) {
            myLat = spUtil.getLocation().getLat();
            myLng = spUtil.getLocation().getLng();
        }
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
            holder.tvCoachName = (TextView) view.findViewById(R.id.tv_coach_name);   //为了减少开销，则只在第一页时调用findViewById
            holder.tvCoachTeachTime = (TextView) view.findViewById(R.id.tv_coach_teach_time);
            holder.tvCoachPoints = (TextView) view.findViewById(R.id.tv_coach_points);
            holder.tvCoachActualPrice = (TextView) view.findViewById(R.id.tv_coach_actual_price);
            holder.tvVIPPrice = (TextView) view.findViewById(R.id.tv_vip_price);
            holder.tvVIPLabel = (TextView) view.findViewById(R.id.tv_vip_label);
            holder.civCoachAvatar = (CircleImageView) view.findViewById(R.id.cir_coach_avatar);
            holder.ivIsGoldenCoach = (ImageView) view.findViewById(R.id.iv_is_golden_coach);
            holder.svCoachScore = (ScoreView) view.findViewById(R.id.sv_coach_score);
            holder.tvCoachLocation = (TextView) view.findViewById(R.id.tv_coach_location);
            holder.llyCoachLocation = (LinearLayout) view.findViewById(R.id.lly_coach_location);
            holder.tvDistance = (TextView) view.findViewById(R.id.tv_distance);
            holder.tvApplaudCount = (TextView) view.findViewById(R.id.tv_applaud_count);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        DecimalFormat dfInt = new DecimalFormat("#####");
        Coach coach = coachList.get(position);
        holder.tvCoachName.setText(coach.getName());
        double coachExperiences = 0d;
        if (!TextUtils.isEmpty(coach.getExperiences())) {
            coachExperiences = Double.parseDouble(coach.getExperiences());
        }
        holder.tvCoachTeachTime.setText(dfInt.format(coachExperiences) + "年教龄");
        holder.tvCoachPoints.setText(coach.getAverage_rating() + " (" + coach.getReview_count() + ")");
        holder.tvCoachActualPrice.setText(Util.getMoney(coach.getCoach_group().getTraining_cost()));
        //vip价格
        if (coach.getVip() == 0) {
            holder.tvVIPPrice.setVisibility(View.INVISIBLE);
            holder.tvVIPLabel.setVisibility(View.INVISIBLE);
        } else {
            holder.tvVIPPrice.setVisibility(View.VISIBLE);
            holder.tvVIPLabel.setVisibility(View.VISIBLE);
            holder.tvVIPPrice.setText(Util.getMoney(coach.getCoach_group().getVip_price()));
        }
        getCoachAvatar(coach.getAvatar(), holder.civCoachAvatar);
        if (coach.getSkill_level().equals("1")) {
            holder.ivIsGoldenCoach.setVisibility(View.VISIBLE);
        } else {
            holder.ivIsGoldenCoach.setVisibility(View.GONE);
        }
        float score = Float.parseFloat(coach.getAverage_rating());
        if (score > 5) {
            score = 5;
        }
        holder.svCoachScore.setScore(score, false);
        if (fieldsList != null) {
            for (FieldModel fieldsModel : fieldsList) {
                if (fieldsModel.getId().equals(coach.getCoach_group().getField_id())) {
                    for (City city : cityList) {
                        if (city.getId().equals(fieldsModel.getCity_id())) {
                            holder.tvCoachLocation.setText(city.getName() + fieldsModel.getSection());
                            break;
                        }
                    }
                    mFieldModel = fieldsModel;
                    break;
                }
            }
        }
        if (!TextUtils.isEmpty(myLat) && !TextUtils.isEmpty(myLng) && !TextUtils.isEmpty(mFieldModel.getLat()) && !TextUtils.isEmpty(mFieldModel.getLng())) {
            String kmString = DistanceUtil.getDistanceKm(Double.parseDouble(myLng), Double.parseDouble(myLat), Double.parseDouble(mFieldModel.getLng()), Double.parseDouble(mFieldModel.getLat()));
            String infoText = "距您" + kmString + "km";
            SpannableStringBuilder style = new SpannableStringBuilder(infoText);
            style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.app_theme_color)), 2, 2 + kmString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.tvDistance.setText(style);
        }
        holder.llyCoachLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                mapDialog = new MapDialog(context, R.style.map_dialog, mFieldModel, v);
                mapDialog.show();
            }
        });
        holder.tvApplaudCount.setText(String.valueOf(coach.getLike_count()));
        return view;
    }

    private void getCoachAvatar(String url, CircleImageView civCoachAvatar) {
        final int iconWidth = Util.instence(context).dip2px(60);
        final int iconHeight = iconWidth;
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(context).load(url).resize(iconWidth, iconHeight)
                    .into(civCoachAvatar);
        }
    }

    static class ViewHolder {
        TextView tvCoachName;
        TextView tvCoachTeachTime;
        TextView tvCoachPoints;
        TextView tvCoachActualPrice;
        TextView tvVIPPrice;
        TextView tvVIPLabel;
        CircleImageView civCoachAvatar;
        ImageView ivIsGoldenCoach;
        ScoreView svCoachScore;
        TextView tvCoachLocation;
        LinearLayout llyCoachLocation;
        TextView tvDistance;
        TextView tvApplaudCount;
    }
}
