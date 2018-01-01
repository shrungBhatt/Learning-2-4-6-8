package com.example.android.learning2_4_6_8.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.learning2_4_6_8.R;
import com.example.android.learning2_4_6_8.homeactivity.TaskHomeActivity;
import com.example.android.learning2_4_6_8.models.TaskData;
import com.example.android.learning2_4_6_8.util.SharedPreferencesData;
import com.example.android.learning2_4_6_8.util.Util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//This is the service class used to fetch the tasks from the server having current date.

public class FetchTodayTaskService extends IntentService {

    private List<TaskData> mTaskDatas;
    private int mTotalTasks;

    private static final String TAG = "FetchTodayTaskService";

    //Static method used to instantiate this service class using an intent.
    public static Intent newIntent(Context context) {
        return new Intent(context, FetchTodayTaskService.class);
    }

    public FetchTodayTaskService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "Received Intent: " + intent);
        Log.i(TAG, "Alarm Manager called");

        fetchTaskData(FetchTodayTaskService.this);//Fetch Task from the server.
    }


    //This method is used to set the service alarm on and off using a toggle button in TasKHomeActivity.java
    public static void setServiceAlarm(Context context, boolean isOn) {
        //Setting the trigger time to 00.00.00 (12:00 AM).
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        /*Check whether the current time before or after the trigger time.
        * If the time is before add 24 hours else continue. */
        long triggerTime = cal.getTimeInMillis();
        if (cal.before(Calendar.getInstance())) {
            triggerTime += AlarmManager.INTERVAL_DAY;
            Log.i(TAG, "Trigger time has already passed");
        }

        //Create a pending Intent for the AlarmManager.
        Intent i = FetchTodayTaskService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Set the alarm
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime,
                    AlarmManager.INTERVAL_DAY, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

    }

    //Method used to fetch the current days task from the server.
    public void fetchTaskData(final Context context) {

        Log.i(TAG,"fethcTaskData method called");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        final String currentDate = sdf.format(new Date()).trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://ersnexus.esy.es/fetch_task_data.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mTaskDatas = Util.parseFetchedJson(response);
                        SharedPreferencesData.setTaskArrayJson(context, response);

                        setmTotalTasks(mTaskDatas.size());
                        Log.i(TAG, String.valueOf(getmTotalTasks()));
                        sendNotification(FetchTodayTaskService.this);//Notify the user.


                        Util util = new Util();
                        if (mTaskDatas.size() != 0) {
                            util.updateTaskData(mTaskDatas,
                                    FetchTodayTaskService.this, TAG);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("current_date", currentDate);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    //Method used to check that whether the service alarm is ON or OFF.
    public static boolean isServiceAlarmOn(Context context) {
        Intent i = FetchTodayTaskService.newIntent(context);
        PendingIntent pi = PendingIntent.
                getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    //Method used to build the notification which will notify the user.
    private void sendNotification(Context context) {

        Intent i = TaskHomeActivity.newIntent(context);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker("Today's Task's")
                .setSmallIcon(R.drawable.ic_today_task)
                .setContentTitle("Today's Task's To Complete")
                .setContentText("Total tasks to complete: " + getmTotalTasks())
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLights(0xff00ff00, 1500, 1500)
                .build();

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(0, notification);

    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;

        return (isNetworkAvailable && cm.getActiveNetworkInfo().isConnected());
    }

    public int getmTotalTasks() {
        return mTotalTasks;
    }

    public void setmTotalTasks(int mTotalTasks) {
        this.mTotalTasks = mTotalTasks;
    }
}
