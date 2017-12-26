package com.example.android.learning2_4_6_8.homeactivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.example.android.learning2_4_6_8.AddTaskActivity;
import com.example.android.learning2_4_6_8.R;
import com.example.android.learning2_4_6_8.models.TaskData;
import com.example.android.learning2_4_6_8.service.FetchTodayTaskService;
import com.example.android.learning2_4_6_8.util.SharedPreferencesData;
import com.example.android.learning2_4_6_8.util.Util;

import java.util.List;


public class TaskHomeActivity extends AppCompatActivity {

    private static final String TAG = "TaskHomeActivity";
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<TaskData> mTaskDatas;

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

        mTabLayout.getTabAt(0).setText("Today");
        mTabLayout.getTabAt(1).setText("All");
        mTabLayout.getTabAt(2).setText("Completed");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_task:
                Intent intent = new Intent(getApplicationContext(),AddTaskActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_polling_status_check_box:
                boolean shouldStartService =
                        !FetchTodayTaskService.isServiceAlarmOn(getApplicationContext());
                if(shouldStartService){
                    SharedPreferencesData.setPollingCheckBoxVal(getApplicationContext(),true);
                }else{
                    SharedPreferencesData.setPollingCheckBoxVal(getApplicationContext(),false);
                }
                FetchTodayTaskService.setServiceAlarm(getApplicationContext(),shouldStartService);
                invalidateOptionsMenu();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_home_activity, menu);


        MenuItem pollingCheckBox = menu.findItem(R.id.menu_polling_status_check_box);
        if(SharedPreferencesData.isPollingCheckBoxChecked(getApplicationContext())){
            pollingCheckBox.setChecked(true);
        }else{
            pollingCheckBox.setChecked(false);
        }

        return true;
    }


}
