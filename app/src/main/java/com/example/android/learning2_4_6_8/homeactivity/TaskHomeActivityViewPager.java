package com.example.android.learning2_4_6_8.homeactivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.android.learning2_4_6_8.homeactivity.tabs.AllTaskFragment;
import com.example.android.learning2_4_6_8.homeactivity.tabs.CompletedTaskFragment;
import com.example.android.learning2_4_6_8.homeactivity.tabs.TodayTaskFragment;


public class TaskHomeActivityViewPager extends FragmentStatePagerAdapter {

    private int mTabCount;

    public TaskHomeActivityViewPager(FragmentManager fm, int tabCount){
        super(fm);

        mTabCount = tabCount;

    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TodayTaskFragment();
            case 1:
                return new AllTaskFragment();
            case 2:
                return new CompletedTaskFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mTabCount;
    }
}
