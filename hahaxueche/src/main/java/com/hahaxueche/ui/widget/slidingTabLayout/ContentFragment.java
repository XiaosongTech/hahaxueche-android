package com.hahaxueche.ui.widget.slidingTabLayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hahaxueche.R;


/**
 * Created by moon.zhong on 2015/3/9.
 */
public class ContentFragment extends Fragment {

    public static Fragment instance(String msg){
        ContentFragment fragment = new ContentFragment() ;
        Bundle bundle = new Bundle() ;
        bundle.putString("msg",msg);
        fragment.setArguments(bundle);
        return fragment ;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_item,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments() ;
        String msg = bundle.getString("msg") ;
        TextView textView = (TextView) view.findViewById(R.id.id_text_msg);
        textView.setText(msg);
    }
}
