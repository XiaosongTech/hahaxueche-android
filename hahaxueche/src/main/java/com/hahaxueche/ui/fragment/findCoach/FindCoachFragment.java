package com.hahaxueche.ui.fragment.findCoach;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hahaxueche.ui.dialog.FcFilterDialog;
import com.hahaxueche.ui.util.comboSeekBar.ComboSeekBar;

import com.hahaxueche.R;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

/**
 * 寻找教练Fragment
 * Created by gibxin on 2016/1/24.
 */
public class FindCoachFragment extends Fragment {
    private String TAG = "FindCoachFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_coach, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getView().findViewById(R.id.lly_fc_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"111111");
            }
        });
    }
}
