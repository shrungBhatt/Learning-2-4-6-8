package com.example.android.learning2_4_6_8.util;

import android.annotation.SuppressLint;
import android.content.Context;
//import android.net.ConnectivityManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.learning2_4_6_8.models.TaskData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static android.content.Context.CONNECTIVITY_SERVICE;


public class Util {

    public static List<TaskData> parseFetchedJson(String result) {

        List<TaskData> taskDatas = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                TaskData taskData = new TaskData();

                taskData.setmId(Integer.valueOf(jsonObject.getString("id")));
                taskData.setmStartDate(jsonObject.getString("start_date"));
                taskData.setmEndDate(jsonObject.getString("end_date"));
                taskData.setmTaskHeader(jsonObject.getString("task_header"));
                taskData.setmTaskContent(jsonObject.getString("task_content"));
                taskData.setmRepCounter(Integer.valueOf(jsonObject.getString("rep_counter")));

                taskDatas.add(taskData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return taskDatas;
    }

    public void updateTaskData(List<TaskData> taskDatas, Context context, final String TAG) {


        for (int i = 0; i < taskDatas.size(); i++) {
            String startDate = taskDatas.get(i).getmStartDate();
            String endDate = taskDatas.get(i).getmEndDate();
            String taskHeader = taskDatas.get(i).getmTaskHeader();
            String taskContent = taskDatas.get(i).getmTaskContent();
            int repCounter = taskDatas.get(i).getmRepCounter();
            final int id = taskDatas.get(i).getmId();
            boolean mCase4Flag = false;


            switch (repCounter) {
                case 1:
                    endDate = parseAndIncrementDate(endDate, 4);
                    repCounter++;
                    break;

                case 2:
                    endDate = parseAndIncrementDate(endDate, 6);
                    repCounter++;
                    break;

                case 3:
                    endDate = parseAndIncrementDate(endDate, 8);
                    repCounter++;
                    break;

                case 4:
                    mCase4Flag = true;
                    addCompletedTask(context, TAG, startDate, endDate, taskHeader, taskContent);
                    deleteCompletedTask(context, TAG, id);
                    break;
            }

            final String finalEndDate = endDate;

            final int finalRepCounter = repCounter;

            if (!mCase4Flag) {
                updateTask(context, TAG, finalEndDate, finalRepCounter, id);
            }


        }
    }

    private void updateTask(Context context, final String TAG,
                            final String endDate, final int repCounter,
                            final int id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://ersnexus.esy.es/update_task_data.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley Error (PollService): " + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("end_date", endDate);
                params.put("rep_counter", String.valueOf(repCounter));
                params.put("id", String.valueOf(id));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    private void addCompletedTask(Context context, final String TAG, final String startDate,
                                  final String endDate, final String taskHeader,
                                  final String taskContent) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://ersnexus.esy.es/add_completed_tasks.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG,response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley Error occurred while adding completed task" +
                        error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("start_date", startDate);
                params.put("end_date", endDate);
                params.put("task_content", taskContent);
                params.put("task_header", taskHeader);
                return params;
            }


        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);


    }


    private void deleteCompletedTask(Context context, final String TAG, final int id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://ersnexus.esy.es/delete_completed_task.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG,response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley Error occurred while adding completed task" +
                        error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }


        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);


    }

    private String parseAndIncrementDate(String endDate, int addDays) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd-MM-yyyy");
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


    /*public static boolean isNetworkAvailableAndConnected(ConnectivityManager cm) {

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;

        return (isNetworkAvailable && cm.getActiveNetworkInfo().isConnected());
    }*/

    public static String getMacAddress(String TAG) {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception" + ex);
        }
        return "02:00:00:00:00:00";
    }
}
