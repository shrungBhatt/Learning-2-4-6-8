package com.example.android.learning2_4_6_8.homeactivity.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.learning2_4_6_8.R;
import com.example.android.learning2_4_6_8.models.TaskData;
import com.example.android.learning2_4_6_8.util.SimpleDividerItemDecoration;
import com.example.android.learning2_4_6_8.util.Util;

import java.util.List;


public class AllTaskFragment extends Fragment {

    private static final String TAG = "AllTaskFragment";

    private List<TaskData> mTaskDatas;
    private RecyclerView mAllTaskRecyclerView;



    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = layoutInflater.inflate(R.layout.tab_all_tasks, container, false);


        mAllTaskRecyclerView = v.findViewById(R.id.tab_all_task_recycler_view);
        mAllTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAllTaskRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        fetchAllTasks(getActivity(),TAG);


        return v;

    }

    private class AllTaskHolder extends RecyclerView.ViewHolder {
        private TextView mTaskHeaderTextView;
        private TextView mRepCounterTextView;
        private TextView mTaskContentTextView;
        private TextView mTaskDateTextView;

        AllTaskHolder(LayoutInflater layoutInflater, ViewGroup container) {
            super(layoutInflater.inflate(R.layout.list_item_all_task_recycler_view, container
                    , false));

            mTaskHeaderTextView = itemView.
                    findViewById(R.id.list_item_all_task_header_text_view);

            mTaskContentTextView = itemView.
                    findViewById(R.id.list_item_all_task_content_text_view);

            mTaskDateTextView = itemView.findViewById(R.id.list_item_all_task_date_text_view);

            mRepCounterTextView = itemView.findViewById(R.id.list_item_all_rep_counter_text_view);

        }

        void bindAllTaskData(TaskData taskData) {
            mTaskHeaderTextView.setText(taskData.getmTaskHeader());
            mTaskContentTextView.setText(taskData.getmTaskContent());
            mTaskDateTextView.setText(taskData.getmEndDate());
            mRepCounterTextView.setText(String.valueOf(taskData.getmRepCounter()));
        }
    }

    private class AllTaskAdapter extends RecyclerView.Adapter<AllTaskHolder> {

        private List<TaskData> mTaskDatas;

        AllTaskAdapter(List<TaskData> taskDatas) {
            mTaskDatas = taskDatas;
        }


        @Override
        public AllTaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new AllTaskHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(AllTaskHolder holder, int position) {
            TaskData taskData = mTaskDatas.get(position);
            holder.bindAllTaskData(taskData);

        }

        @Override
        public int getItemCount() {
            return mTaskDatas.size();
        }
    }

    public List<TaskData> fetchAllTasks(Context context, final String TAG) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://ersnexus.esy.es/fetch_all_task_data.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mTaskDatas = Util.parseFetchedJson(response);
                        if(mTaskDatas.size() != 0){
                            mAllTaskRecyclerView.setAdapter(new AllTaskAdapter(mTaskDatas));
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Received Volley error in AllTaskFragment: "+error.toString());

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);


        return mTaskDatas;
    }

}
