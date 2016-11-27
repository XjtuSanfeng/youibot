package com.youibot.docrobo.robo.main.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.netease.nim.robo.R;
import com.youibot.docrobo.robo.main.model.MainTab;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelfmonitorFragment extends MainTabFragment implements ViewPager.OnPageChangeListener, View.OnClickListener{


    private ViewPager mselfmonitorviewpager;

    private List<Fragment> mFragment = new ArrayList<>();
    private Calendar calendar;
    private LinearLayout vitalsigns,reactionsymp,sleepstatus,selfevalu,reviewinfo;

    public SelfmonitorFragment() {
        // Required empty public constructor
//        this.setContainerId(MainTab.SELF_MONITOR.fragmentId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selfmonitor, container, false);

        return view;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent();
    }
    protected void onInit() {
        initViews();
        initSubViewPager();
    }
    private void initViews(){

        vitalsigns = (LinearLayout) getView().findViewById(R.id.def_vitalsigns);
        reactionsymp = (LinearLayout) getView().findViewById(R.id.def_reactionsymp);
        sleepstatus = (LinearLayout) getView().findViewById(R.id.def_sleepstatus);
        selfevalu = (LinearLayout) getView().findViewById(R.id.def_selfevalu);
        reviewinfo=(LinearLayout) getView().findViewById(R.id.def_reviewinfo);

        vitalsigns.setOnClickListener(this);
        reactionsymp.setOnClickListener(this);
        sleepstatus.setOnClickListener(this);
        selfevalu.setOnClickListener(this);
        reviewinfo.setOnClickListener(this);

        mselfmonitorviewpager=(ViewPager)getView().findViewById(R.id.selfmonitor_viewpager);

        changeTextViewColor();
        changeSelectedTabState(0);


    }
    private void initSubViewPager(){
        mFragment.add(new VitalsignsFragment());
        mFragment.add(new ReactionFragment());
        mFragment.add(new SleepstatusFragment());
        mFragment.add(new SelfevalFragment());
        mFragment.add(new ReviewinfoFragment());
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        mselfmonitorviewpager.setAdapter(fragmentPagerAdapter);
        mselfmonitorviewpager.setOnPageChangeListener(this);
    }
    private void changeTextViewColor() {
        vitalsigns.setBackgroundColor(Color.parseColor("#FFFFFF"));
        reactionsymp.setBackgroundColor(Color.parseColor("#FFFFFF"));
        sleepstatus.setBackgroundColor(Color.parseColor("#FFFFFF"));
        selfevalu.setBackgroundColor(Color.parseColor("#FFFFFF"));
        reviewinfo.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }
    private void changeSelectedTabState(int position) {
        switch (position){
            case 0:
                vitalsigns.setBackgroundColor(Color.parseColor("#dfdfdd"));
                break;
            case 1:
                reactionsymp.setBackgroundColor(Color.parseColor("#dfdfdd"));
                break;
            case 2:
                sleepstatus.setBackgroundColor(Color.parseColor("#dfdfdd"));
                break;
            case 3:
                selfevalu.setBackgroundColor(Color.parseColor("#dfdfdd"));
                break;
            case 4:
                reviewinfo.setBackgroundColor(Color.parseColor("#dfdfdd"));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.def_vitalsigns:
                mselfmonitorviewpager.setCurrentItem(0,false);
                break;
            case R.id.def_reactionsymp:
                mselfmonitorviewpager.setCurrentItem(1,false);
                break;
            case R.id.def_sleepstatus:
                mselfmonitorviewpager.setCurrentItem(2,false);
                break;
            case R.id.def_selfevalu:
                mselfmonitorviewpager.setCurrentItem(3,false);
                break;
            case R.id.def_reviewinfo:
                mselfmonitorviewpager.setCurrentItem(4,false);
                break;
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


    }
    public void onPageSelected(int position) {
        changeTextViewColor();
        changeSelectedTabState(position);
    }
    @Override
    public void onPageScrollStateChanged(int state) {


    }
}
