package com.example.android.learning2_4_6_8.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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


public class FetchTodayTaskService extends IntentService {

    private List<TaskData> mTaskDatas;

    private static final String TAG = "FetchTodayTaskService";

    public static Intent newIntent(Context context){
        return new Intent(context,FetchTodayTaskService.class);
    }

    public FetchTodayTaskService(){
        super(TAG);
    }

    public static void setServiceAlarm(Context context,boolean isOn){
        Calendar cur_cal = new GregorianCalendar();
        cur_cal.setTimeInMillis(System.currentTimeMillis());


        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 19);
        cal.set(Calendar.MINUTE, 14);

        Intent i = FetchTodayTaskService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0,i,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(isOn){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),
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

        fetchTaskData(this);

        sendNotification(getApplicationContext());


    }

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

    public static boolean isServiceAlarmOn(Context context){
        Intent i = FetchTodayTaskService.newIntent(context);
        PendingIntent pi = PendingIntent.
                getService(context,0,i,PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    private void sendNotification(Context context){

        Intent i = TaskHomeActivity.newIntent(context);
        PendingIntent pi = PendingIntent.getActivity(context,0,i,0);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker("Today's Task's to Complete")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentTitle("Today's Task's to Complete")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(0,notification);

    }
}