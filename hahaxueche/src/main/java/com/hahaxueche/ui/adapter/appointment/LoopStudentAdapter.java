package com.hahaxueche.ui.adapter.appointment;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.BannerHighlight;
import com.hahaxueche.model.city.CityModel;
import com.hahaxueche.model.city.FieldModel;
import com.hahaxueche.model.coach.CoachModel;
import com.hahaxueche.ui.dialog.MapDialog;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.utils.Util;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gibxin on 2016/4/19.
 */
public class LoopStudentAdapter extends BaseAdapter {
    private ArrayList<BannerHighlight> mStudentList;
    private int resource;   //item的布局
    private Context context;
    private LayoutInflater inflator;

    public LoopStudentAdapter(Context context, ArrayList<BannerHighlight> studentList, int resource) {
        this.context = context;
        this.mStudentList = studentList;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return mStudentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStudentList.get(position);
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
            holder.civStudentAvatar = (CircleImageView) view.findViewById(R.id.civ_student_avatar);   //为了减少开销，则只在第一页时调用findViewById
            holder.tvStudentName = (TextView) view.findViewById(R.id.tv_student_name);
            holder.tvStudentText = (TextView) view.findViewById(R.id.tv_student_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        BannerHighlight student = mStudentList.get(position);
        holder.tvStudentText.setText(student.getText());
        holder.tvStudentName.setText(student.getName());
        getStudentAvatar(student.getAvatar_url(), holder.civStudentAvatar);
        return view;
    }

    private void getStudentAvatar(String url, CircleImageView civCoachAvatar) {
        final int iconWidth = Util.instence(context).dip2px(50);
        final int iconHeight = iconWidth;
        Picasso.with(context).load(url).resize(iconWidth, iconHeight)
                .into(civCoachAvatar);
    }

    static class ViewHolder {
        CircleImageView civStudentAvatar;
        TextView tvStudentName;
        TextView tvStudentText;
    }
}
