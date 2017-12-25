package com.example.android.learning2_4_6_8.homeactivity.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.learning2_4_6_8.R;
import com.example.android.learning2_4_6_8.models.TaskData;
import com.example.android.learning2_4_6_8.service.FetchTodayTaskService;
import com.example.android.learning2_4_6_8.util.SharedPreferencesData;
import com.example.android.learning2_4_6_8.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TodayTaskFragment extends Fragment {
    private static final String TAG = "TodayTaskFragment";

    private TextView mDateTextView;
    private RecyclerView mRecyclerView;
    private List<TaskData> mTaskDatas;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = layoutInflater.inflate(R.layout.tab_today_tasks,container,false);

        mDateTextView = v.findViewById(R.id.tab_today_tasks_date_text_view);

        mRecyclerView = v.findViewById(R.id.tab_today_tasks_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String todaysDate = simpleDateFormat.format(new Date()).trim();

        mDateTextView.setText(todaysDate);


        String resultJson = SharedPreferencesData.getTaskArrayJson(getActivity());

        if(resultJson != null) {
            mTaskDatas = Util.parseFetchedJson(resultJson);
            if (mTaskDatas != null) {
                mRecyclerView.setAdapter(new TaskAdapter(mTaskDatas));
            } else {
                Log.e(TAG, "mTaskDatas is empty");
            }
        }else{
            Log.e(TAG,"resultJson is null");
        }

        return v;
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
            LayoutInflater inflater = LayoutInflater.from(getActivity());
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

}
