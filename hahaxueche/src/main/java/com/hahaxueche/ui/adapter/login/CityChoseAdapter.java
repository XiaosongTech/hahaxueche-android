package com.hahaxueche.ui.adapter.login;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.City;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/11.
 */
public class CityChoseAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<City> cities;
    private int selectedPosition = 0;
    private Context mContext;

    public CityChoseAdapter(Context context,
                            ArrayList<City> cities) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        this.cities = cities;
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public Object getItem(int position) {
        return cities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearSelection(int position) {
        selectedPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.adapter_city_chose, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.tvCity.setText(cities.get(position).name);

        if (selectedPosition == position) {
            holder.tvCity.setBackgroundColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
            holder.tvCity.setTextColor(ContextCompat.getColor(mContext, R.color.haha_white));
        } else {
            holder.tvCity.setBackgroundColor(ContextCompat.getColor(mContext, R.color.haha_white));
            holder.tvCity.setTextColor(ContextCompat.getColor(mContext, R.color.haha_gray_dark));
        }
        return convertView;
    }

    public class ViewHolder {
        @BindView(R.id.tv_city_name)
        TextView tvCity;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
