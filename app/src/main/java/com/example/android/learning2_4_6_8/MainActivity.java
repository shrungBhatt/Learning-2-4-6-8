package com.example.android.learning2_4_6_8;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mTaskListRecyclerView;
    private List<TaskData> mTaskDatas;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        PollService.setServiceAlarm(getApplicationContext(),true);

        mTaskListRecyclerView = findViewById(R.id.tasks_list_recycler_view);
        mTaskListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        fetchTaskData(getApplicationContext());

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class TaskHolder extends RecyclerView.ViewHolder {
        private TextView mTaskContentTextView;
        private TextView mRepCounterTextView;
        private TextView mDateTextView;

        TaskHolder(LayoutInflater layoutInflater, ViewGroup container) {
            super(layoutInflater.inflate(R.layout.list_item_task, container, false));

            mTaskContentTextView = itemView.findViewById(R.id.list_item_task_content_text_view);

            mDateTextView = itemView.findViewById(R.id.list_item_task_date_text_view);

            mRepCounterTextView = itemView.findViewById(R.id.list_item_rep_counter_text_view);

        }

        void bindTaskData(TaskData taskData){

            mTaskContentTextView.setText(taskData.getmTaskContent());
            mDateTextView.setText(taskData.getmEndDate());
            mRepCounterTextView.setText(String.valueOf(taskData.getmRepCounter()));

        }

    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {

        private List<TaskData> mTaskDatas;

        TaskAdapter(List<TaskData> taskDatas) {
            mTaskDatas = taskDatas;
        }

        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            return new TaskHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(TaskHolder holder, int position) {
            TaskData taskData = mTaskDatas.get(position);
            holder.bindTaskData(taskData);

        }

        @Override
        public int getItemCount() {
            return mTaskDatas.size();
        }
    }

    public void fetchTaskData(Context context){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        final String currentDate = sdf.format(new Date()).trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://ersnexus.esy.es/fetch_task_data.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mTaskDatas = parseFetchedJson(response);
                        if(mTaskDatas != null){
                            mTaskListRecyclerView.setAdapter(new TaskAdapter(mTaskDatas));
                        }
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
                params.put("current_date","14-12-2017");

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    private List<TaskData> parseFetchedJson(String result){

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


}
