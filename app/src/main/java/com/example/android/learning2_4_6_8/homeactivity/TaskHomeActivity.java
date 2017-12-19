package com.example.android.learning2_4_6_8.homeactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.example.android.learning2_4_6_8.R;
import com.example.android.learning2_4_6_8.service.FetchTodayTaskService;

/**
 * Created by jigsaw on 19/12/17.
 */

public class TaskHomeActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public static Intent newIntent(Context context){
        return new Intent(context,TaskHomeActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_home);


        mTabLayout = findViewById(R.id.home_activity_tablayout);

        mViewPager = findViewById(R.id.home_activity_viewpager);


        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        TaskHomeActivityViewPager viewPagerAdapter = new TaskHomeActivityViewPager(
                getSupportFragmentManager(),mTabLayout.getTabCount());

        mViewPager.setAdapter(viewPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText("Today's");
        mTabLayout.getTabAt(1).setText("All");
        mTabLayout.getTabAt(2).setText("Completed");

        Intent i = FetchTodayTaskService.newIntent(getApplicationContext());
        startService(i);

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_home_activity, menu);
        return true;
    }


}
