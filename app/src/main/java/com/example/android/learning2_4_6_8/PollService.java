package com.example.android.learning2_4_6_8;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;


public class PollService extends IntentService {

    private static final String TAG = "PollService";

    private static final String ACTION_SHOW_NOTIFICATION =
            "com.example.Learning2-4-6-8.SHOW_NOTIFICATION";

    @NonNull
    public static Intent newIntent(Context context){
        return new Intent(context,PollService.class);
    }

    public PollService(){
        super(TAG);
    }

    public static void setServiceAlarm(@Nullable Context context,boolean isOn){
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0,i,0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if(isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(),1000*5,pi);
        }else{
            alarmManager.cancel(pi);
            pi.cancel();
        }

        SharedPreferencesData.setAlarmOn(context,true);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent i = MainActivity.newIntent(this);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,0);

        Notification notification = new Notification.Builder(this)
                .setTicker("New Notification")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("Learn This subject")
                .setContentText("Start Learning")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0,notification);
    }
}