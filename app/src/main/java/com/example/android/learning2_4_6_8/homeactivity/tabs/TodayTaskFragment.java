package com.example.android.learning2_4_6_8.homeactivity.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.learning2_4_6_8.R;



public class TodayTaskFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = layoutInflater.inflate(R.layout.tab_today_tasks,container,false);

        return v;
    }
}
