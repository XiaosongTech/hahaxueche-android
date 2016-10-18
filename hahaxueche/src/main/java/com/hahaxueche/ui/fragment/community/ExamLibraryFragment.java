package com.hahaxueche.ui.fragment.community;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hahaxueche.R;
import com.hahaxueche.ui.fragment.HHBaseFragment;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ExamLibraryFragment extends HHBaseFragment {
    public static final String ARGS_PAGE = "args_page";
    private int mPage;

    public static ExamLibraryFragment newInstance(int page) {
        Bundle args = new Bundle();

        args.putInt(ARGS_PAGE, page);
        ExamLibraryFragment fragment = new ExamLibraryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARGS_PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_library, container, false);
//        TextView textView = (TextView) view.findViewById(R.id.textView);
//        textView.setText("第"+mPage+"页");
        return view;
    }
}
