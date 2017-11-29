package com.example.android.learning2_4_6_8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;



public class StartUpReceiver extends BroadcastReceiver {

    private static final String TAG = "StartUpReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG,"Received BroadCast Intent: " + intent.getAction());

        boolean isOn = SharedPreferencesData.isAlarmOn(context);
        PollService.setServiceAlarm(context,isOn);
    }
}
