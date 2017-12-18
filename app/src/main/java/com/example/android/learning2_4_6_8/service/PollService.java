package com.example.android.learning2_4_6_8.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.learning2_4_6_8.MainActivity;
import com.example.android.learning2_4_6_8.models.TaskData;
import com.example.android.learning2_4_6_8.util.SharedPreferencesData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PollService extends IntentService {
    public static final String TAG = "PollService";

    private Context mContext;

    private List<TaskData> mTaskDatas;

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String endDate;
        String startDate;
        int repCounter;

        Log.e(TAG,"Intent received" + intent);

        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        MainActivity mainActivity = new MainActivity();
//        mainActivity.fetchTaskData(PollService.this);

        String arrayJson = SharedPreferencesData.getTaskArrayJson(this);
        mTaskDatas = mainActivity.parseFetchedJson(arrayJson);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        for (int i = 0;i<mTaskDatas.size();i++) {
            endDate = mTaskDatas.get(i).getmEndDate();
            repCounter = mTaskDatas.get(i).getmRepCounter();
            final int id = mTaskDatas.get(i).getmId();

            switch (repCounter) {
                case 1:
                    try {
                        calendar.setTime(simpleDateFormat.parse(endDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    calendar.add(Calendar.DATE, 4);
                    endDate = simpleDateFormat.format(calendar.getTime()).trim();
                    repCounter++;
                    break;

                case 2:
                    try {
                        calendar.setTime(simpleDateFormat.parse(endDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    calendar.add(Calendar.DATE, 6);
                    endDate = simpleDateFormat.format(calendar.getTime()).trim();
                    repCounter++;
                    break;

                case 3:
                    try {
                        calendar.setTime(simpleDateFormat.parse(endDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    calendar.add(Calendar.DATE, 8);
                    endDate = simpleDateFormat.format(calendar.getTime()).trim();
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

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;

        return (isNetworkAvailable && cm.getActiveNetworkInfo().isConnected());
    }
}
