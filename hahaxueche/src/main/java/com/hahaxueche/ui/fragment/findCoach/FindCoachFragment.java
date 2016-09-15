package com.hahaxueche.ui.fragment.findCoach;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hahaxueche.R;

/**
 * Created by wangshirui on 16/9/13.
 */
public class FindCoachFragment extends Fragment {
    public static FindCoachFragment newInstance() {
        FindCoachFragment fragment = new FindCoachFragment();
        return fragment;
    }

    public FindCoachFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_coach, container, false);
        //TextView tv = (TextView)view.findViewById(R.id.tv_location);
        return view;
    }
}
