package com.example.android.learning2_4_6_8;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    public static Intent newIntent(Context context){
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PollService.setServiceAlarm(getApplicationContext(),true);
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
