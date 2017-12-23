package com.example.android.learning2_4_6_8.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.example.android.learning2_4_6_8.homeactivity.TaskHomeActivity;
import com.example.android.learning2_4_6_8.models.TaskData;
import com.example.android.learning2_4_6_8.util.SharedPreferencesData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//This is the service class used to fetch the tasks from the server having current date.

public class FetchTodayTaskService extends IntentService {

    private List<TaskData> mTaskDatas;

    private static final String TAG = "FetchTodayTaskService";

    //Static method used to instantiate this service class using an intent.
    public static Intent newIntent(Context context){
        return new Intent(context,FetchTodayTaskService.class);
    }

    public FetchTodayTaskService(){
        super(TAG);
    }

    //This method is used to set the service alarm on and off using a toggle button in TasKHomeActivity.java
    public static void setServiceAlarm(Context context,boolean isOn){
        //Setting the trigger time to 00.00.00 (12:00 AM).
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);

        /*Check whether the current time before or after the trigger time.
        * If the time is before add 24 hours else continue. */
        long triggerTime = cal.getTimeInMillis();
        if (cal.before(Calendar.getInstance())) {
            triggerTime += AlarmManager.INTERVAL_DAY;
        }

        //Create a pending Intent for the AlarmManager.
        Intent i = FetchTodayTaskService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0,i,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Set the alarm
        if(isOn){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,triggerTime,
                    AlarmManager.INTERVAL_DAY,pi);
        }else{
            alarmManager.cancel(pi);
            pi.cancel();
        }

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e(TAG,"Received Intent: " + intent);
        Log.e(TAG,"Alarm Manager called");

        fetchTaskData(this);//Fetch Task from the server.

        sendNotification(getApplicationContext());//Notify the user.
    }

    //Method used to fetch the current days task from the server.
    public void fetchTaskData(final Context context){

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        final String currentDate = sdf.format(new Date()).trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://ersnexus.esy.es/fetch_task_data.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG,"Received json: " + response);
                        mTaskDatas = parseFetchedJson(response);
                        SharedPreferencesData.setTaskArrayJson(context,response);
//                        updateTaskData(mTaskDatas);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error: ",error.toString());
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("current_date",currentDate);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    //Method used to parse the JSON of response of the server.
    public static List<TaskData> parseFetchedJson(String result){

        List<TaskData> taskDatas = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(result);

            for(int i=0 ; i<jsonArray.length() ; i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                TaskData taskData = new TaskData();

                taskData.setmId(Integer.valueOf(jsonObject.getString("id")));
                taskData.setmStartDate(jsonObject.getString("start_date"));
                taskData.setmEndDate(jsonObject.getString("end_date"));
                taskData.setmTaskContent(jsonObject.getString("task_content"));
                taskData.setmRepCounter(Integer.valueOf(jsonObject.getString("rep_counter")));

                taskDatas.add(taskData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return taskDatas;
    }


    //Volley request to update the current days task, to update their endDate and repCounter.
    private void updateTaskData(List<TaskData> taskDatas){

        for (int i = 0 ; i < taskDatas.size() ; i++) {
            String endDate = taskDatas.get(i).getmEndDate();
            int repCounter = taskDatas.get(i).getmRepCounter();
            final int id = taskDatas.get(i).getmId();

            switch (repCounter) {
                case 1:
                    endDate = parseAndIncrementDate(endDate,4);
                    repCounter++;
                    break;

                case 2:
                    endDate = parseAndIncrementDate(endDate,6);
                    repCounter++;
                    break;

                case 3:
                    endDate = parseAndIncrementDate(endDate,8);
                    repCounter++;
                    break;
            }

            final String finalEndDate = endDate;

            final int finalRepCounter = repCounter;

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    "http://ersnexus.esy.es/update_task_data.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e(TAG,response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG,"Volley Error (PollService): " + error.toString());
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("end_date",finalEndDate);
                    params.put("rep_counter",String.valueOf(finalRepCounter));
                    params.put("id",String.valueOf(id));
                    return params;
                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

    //Method used to Parse the endDate and Increment the date according to the repCounter value.
    public String parseAndIncrementDate(String endDate, int addDays){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(simpleDateFormat.parse(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DATE, addDays);
        endDate = simpleDateFormat.format(calendar.getTime()).trim();

        return endDate;
    }

    //Method used to check that whether the service alarm is ON or OFF.
    public static boolean isServiceAlarmOn(Context context){
        Intent i = FetchTodayTaskService.newIntent(context);
        PendingIntent pi = PendingIntent.
                getService(context,0,i,PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    //Method used to build the notification which will notify the user.
    private void sendNotification(Context context){

        Intent i = TaskHomeActivity.newIntent(context);
        PendingIntent pi = PendingIntent.getActivity(context,0,i,0);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker("Today's Task's to Complete")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentTitle("Today's Task's to Complete")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLights(0xff00ff00, 1500, 1500)
                .build();

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(0,notification);

    }
}
