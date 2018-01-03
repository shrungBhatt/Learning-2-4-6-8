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



public class CompletedTaskFragment extends Fragment {

    private static final String TAG = "CompletedTaskFragment";

    private List<TaskData> mTaskDatas;
    private RecyclerView mCompletedTaskRecyclerView;


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = layoutInflater.inflate(R.layout.tab_completed_tasks, container, false);

        mCompletedTaskRecyclerView = v.findViewById(R.id.tab_completed_task_recycler_view);
        mCompletedTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCompletedTaskRecyclerView.
                addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        fetchCompletedTasks(getActivity(),TAG);

        return v;
    }

    private class CompletedTaskHolder extends RecyclerView.ViewHolder {
        private TextView mTaskHeaderTextView;
        private TextView mRepCounterTextView;
        private TextView mTaskContentTextView;
        private TextView mTaskDateTextView;

        CompletedTaskHolder(LayoutInflater layoutInflater, ViewGroup container) {
            super(layoutInflater.inflate(R.layout.list_item_completed_task_recycler_view, container,
                    false));

            mTaskHeaderTextView = itemView.
                    findViewById(R.id.list_item_completed_task_header_text_view);

            mTaskContentTextView = itemView.
                    findViewById(R.id.list_item_completed_task_content_text_view);

            mTaskDateTextView = itemView.findViewById(R.id.list_item_completed_task_date_text_view);

            mRepCounterTextView = itemView.
                    findViewById(R.id.list_item_completed_rep_counter_text_view);

        }

        void bindCompletedTaskData(TaskData taskData) {
            mTaskHeaderTextView.setText(taskData.getmTaskHeader());
            mTaskContentTextView.setText(taskData.getmTaskContent());
            mTaskDateTextView.setText(taskData.getmEndDate());
            mRepCounterTextView.setText(String.valueOf(taskData.getmRepCounter()));
        }
    }

    private class CompletedTaskAdapter extends RecyclerView.Adapter<CompletedTaskHolder> {

        private List<TaskData> mTaskDatas;

        CompletedTaskAdapter(List<TaskData> taskDatas) {
            mTaskDatas = taskDatas;
        }

        @Override
        public CompletedTaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new CompletedTaskHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(CompletedTaskHolder holder, int position) {
            TaskData taskData = mTaskDatas.get(position);
            holder.bindCompletedTaskData(taskData);
        }

        @Override
        public int getItemCount() {
            return mTaskDatas.size();
        }
    }


    private void fetchCompletedTasks(Context context, final String TAG) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "url",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mTaskDatas = Util.parseFetchedJson(response);
                        if(mTaskDatas.size() != 0 ){
                            mCompletedTaskRecyclerView.
                                    setAdapter(new CompletedTaskAdapter(mTaskDatas));
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error occurred in CompletedTaskFragment volley Request" +
                        error.toString());
            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
