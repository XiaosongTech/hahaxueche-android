package com.hahaxueche.ui.adapter.findCoach;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hahaxueche.R;
import com.hahaxueche.model.findCoach.CoachModel;
import com.hahaxueche.model.findCoach.FieldModel;
import com.hahaxueche.model.findCoach.FieldsModel;
import com.hahaxueche.model.signupLogin.CityModel;
import com.hahaxueche.model.util.ConstantsModel;
import com.hahaxueche.ui.dialog.MapDialog;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
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
    private ConstantsModel constantsModel;
    private List<FieldModel> fieldsList;
    private List<CityModel> cityList;
    private MapDialog mapDialog;
    private FieldModel mFieldModel;

    public CoachItemAdapter(Context context, List<CoachModel> coachList, int resource) {
        this.context = context;
        this.coachList = coachList;
        this.resource = resource;
        SharedPreferences sharedPreferences = context.getSharedPreferences("constants", Activity.MODE_PRIVATE);
        String constants = sharedPreferences.getString("constants", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ConstantsModel>() {
        }.getType();
        constantsModel = gson.fromJson(constants, type);
        fieldsList = constantsModel.getFields();
        cityList = constantsModel.getCities();
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
            holder.tvCoachOldPrice = (TextView) view.findViewById(R.id.tv_coach_old_price);
            holder.civCoachAvatar = (CircleImageView) view.findViewById(R.id.cir_coach_avatar);
            holder.ivIsGoldenCoach = (ImageView) view.findViewById(R.id.iv_is_golden_coach);
            holder.svCoachScore = (ScoreView) view.findViewById(R.id.sv_coach_score);
            holder.tvCoachLocation = (TextView) view.findViewById(R.id.tv_coach_location);
            holder.llyCoachLocation = (LinearLayout) view.findViewById(R.id.lly_coach_location);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        DecimalFormat dfInt = new DecimalFormat("#####");
        CoachModel coach = coachList.get(position);
        holder.tvCoachName.setText(coach.getName());
        double coachExperiences = 0d;
        if (!TextUtils.isEmpty(coach.getExperiences())) {
            coachExperiences = Double.parseDouble(coach.getExperiences());
        }
        holder.tvCoachTeachTime.setText(dfInt.format(coachExperiences) + "年教龄");
        holder.tvCoachPoints.setText(coach.getAverage_rating());
        holder.tvCoachActualPrice.setText(Util.getMoney(coach.getCoach_group().getTraining_cost()));
        holder.tvCoachOldPrice.setText(Util.getMoney(coach.getCoach_group().getMarket_price()));
        holder.tvCoachOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
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
                    for (CityModel city : cityList) {
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
        holder.llyCoachLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                mapDialog = new MapDialog(context, R.style.map_dialog, mFieldModel, v);
                mapDialog.show();
            }
        });
        return view;
    }

    private void getCoachAvatar(String url, CircleImageView civCoachAvatar) {
        final int iconWidth = Util.instence(context).dip2px(60);
        final int iconHeight = iconWidth;
        Picasso.with(context).load(url).resize(iconWidth, iconHeight)
                .into(civCoachAvatar);
    }

    static class ViewHolder {
        TextView tvCoachName;
        TextView tvCoachTeachTime;
        TextView tvCoachPoints;
        TextView tvCoachActualPrice;
        TextView tvCoachOldPrice;
        CircleImageView civCoachAvatar;
        ImageView ivIsGoldenCoach;
        ScoreView svCoachScore;
        TextView tvCoachLocation;
        LinearLayout llyCoachLocation;
    }
}
