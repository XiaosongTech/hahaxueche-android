package com.hahaxueche.ui.fragment.findCoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hahaxueche.R;
import com.hahaxueche.ui.fragment.HHBaseFragment;

import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/19.
 */

public class PartnerListFragment extends HHBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partner_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
