package com.hahaxueche.ui.adapter.mySetting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.student.Student;
import com.hahaxueche.ui.widget.circleImageView.CircleImageView;

import java.util.ArrayList;

/**
 * Created by gibxin on 2016/4/29.
 */
public class ReferInfoAdapter extends BaseAdapter {
    private int mResource;   //item的布局
    private LayoutInflater mInflator;
    private Context mContext;
    private CircleImageView mCivStudentAvatar;
    private TextView mTvStudentName;
    private TextView mTvReferState;
    private TextView mTvReferAmount;
    private ArrayList<Student> mStudentList;

    public ReferInfoAdapter(Context context, ArrayList<Student> studentList, int resource) {
        mContext = context;
        mStudentList = studentList;
        mResource = resource;
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
            mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflator.inflate(mResource, null);
            holder = new ViewHolder();
            holder.civStudentAvatar = (CircleImageView) view.findViewById(R.id.civ_student_avatar);
            holder.tvStudentName = (TextView) view.findViewById(R.id.tv_student_name);
            holder.tvReferState = (TextView) view.findViewById(R.id.tv_refer_state);
            holder.tvReferAmount = (TextView) view.findViewById(R.id.tv_refer_amount);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Student student = mStudentList.get(position);
        //holder.tvReviewerName.setText(reviewInfo.getReviewer().getName());
        return view;
    }

    static class ViewHolder {
        CircleImageView civStudentAvatar;
        TextView tvStudentName;
        TextView tvReferState;
        TextView tvReferAmount;
    }
}


