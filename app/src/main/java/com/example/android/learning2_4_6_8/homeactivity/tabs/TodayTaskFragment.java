package com.example.android.learning2_4_6_8.homeactivity.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.learning2_4_6_8.R;
import com.example.android.learning2_4_6_8.models.TaskData;
import com.example.android.learning2_4_6_8.util.SharedPreferencesData;
import com.example.android.learning2_4_6_8.util.SimpleDividerItemDecoration;
import com.example.android.learning2_4_6_8.util.Util;

import java.util.List;


public class TodayTaskFragment extends Fragment {
    private static final String TAG = "TodayTaskFragment";

    private RecyclerView mRecyclerView;
    private List<TaskData> mTaskDatas;
    private TextView mTaskToDoStatusTextView;
    private ImageView mTaskNoTaskImageView;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = layoutInflater.inflate(R.layout.tab_today_tasks,container,false);



        mRecyclerView = v.findViewById(R.id.tab_today_tasks_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));


        mTaskToDoStatusTextView = v.findViewById(R.id.tab_today_tasks_no_task_text_view);
        mTaskNoTaskImageView = v.findViewById(R.id.tab_today_task_no_task_image_view);


        String resultJson = SharedPreferencesData.getTaskArrayJson(getActivity());
        if(resultJson == null){
            resultJson = "null";
        }

        if(!resultJson.equals("null")) {
            mTaskDatas = Util.parseFetchedJson(resultJson);
            if (mTaskDatas != null) {
                mRecyclerView.setAdapter(new TodayTaskAdapter(mTaskDatas));
            } else {
                Log.e(TAG, "mTaskDatas is empty");
            }
        }else{
            mRecyclerView.setVisibility(View.INVISIBLE);
            mTaskToDoStatusTextView.setVisibility(View.VISIBLE);
            mTaskNoTaskImageView.setVisibility(View.VISIBLE);
            Log.e(TAG,"resultJson is null");
        }


        return v;
    }


    private class TodayTaskHolder extends RecyclerView.ViewHolder {
        private TextView mTaskHeaderTextView;
        private TextView mRepCounterTextView;
        private TextView mTaskContentTextView;

        TodayTaskHolder(LayoutInflater layoutInflater, ViewGroup container) {
            super(layoutInflater.
                    inflate(R.layout.list_item_today_task_recycler_view, container, false));

            mTaskHeaderTextView = itemView.
                    findViewById(R.id.list_item_today_task_header_text_view);

            mTaskContentTextView = itemView.
                    findViewById(R.id.list_item_today_task_content_text_view);

            mRepCounterTextView = itemView.
                    findViewById(R.id.list_item_today_rep_counter_text_view);

        }

        void bindTodayTaskData(TaskData taskData){

            mTaskHeaderTextView.setText(taskData.getmTaskHeader());
            mTaskContentTextView.setText(taskData.getmTaskContent());
            mRepCounterTextView.setText(String.valueOf(taskData.getmRepCounter()));

        }

    }

    private class TodayTaskAdapter extends RecyclerView.Adapter<TodayTaskHolder> {

        private List<TaskData> mTaskDatas;

        TodayTaskAdapter(List<TaskData> taskDatas) {
            mTaskDatas = taskDatas;
        }

        @Override
        public TodayTaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new TodayTaskHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(TodayTaskHolder holder, int position) {
            TaskData taskData = mTaskDatas.get(position);
            holder.bindTodayTaskData(taskData);

        }

        @Override
        public int getItemCount() {
            return mTaskDatas.size();
        }
    }



}
