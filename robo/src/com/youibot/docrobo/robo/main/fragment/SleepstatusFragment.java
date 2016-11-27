package com.youibot.docrobo.robo.main.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nim.robo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SleepstatusFragment extends Fragment {


    public SleepstatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mview= inflater.inflate(R.layout.fragment_sleepstatus, null);
        return mview;
    }

}
