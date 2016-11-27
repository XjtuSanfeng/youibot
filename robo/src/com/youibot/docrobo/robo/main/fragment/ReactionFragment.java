package com.youibot.docrobo.robo.main.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.netease.nim.robo.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReactionFragment extends Fragment implements View.OnClickListener {


    public ReactionFragment() {
        // Required empty public constructor
    }


    private Button btn_newreac;
    private EditText editText_datanow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mview=inflater.inflate(R.layout.fragment_reaction, null);
        initview(mview);
        return mview;
    }
    public void initview(View view)
    {
        btn_newreac=(Button)view.findViewById(R.id.btn_newreaction);
        editText_datanow=(EditText)view.findViewById(R.id.edit_datenow);
        editText_datanow.setText(Calendar.getInstance().get(Calendar.YEAR)+"年"+(Calendar.getInstance().get(Calendar.MONTH)+1)+"月"+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"日");

    }
    public void onClick(View view)
    {
        switch (view.getId()){
            case R.id.btn_newreaction:
                break;
        }
    }

}
