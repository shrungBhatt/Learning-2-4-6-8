package com.example.android.learning2_4_6_8.models;


import java.util.List;

public class TaskData {

    private int mId;
    private String mStartDate;
    private String mEndDate;
    private String mTaskContent;
    private String mTaskHeader;
    private int mRepCounter;
    private List<TaskData> mTaskDatas;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmStartDate() {
        return mStartDate;
    }

    public void setmStartDate(String mStartDate) {
        this.mStartDate = mStartDate;
    }

    public String getmEndDate() {
        return mEndDate;
    }

    public void setmEndDate(String mEndDate) {
        this.mEndDate = mEndDate;
    }

    public String getmTaskContent() {
        return mTaskContent;
    }

    public void setmTaskContent(String mTaskContent) {
        this.mTaskContent = mTaskContent;
    }

    public int getmRepCounter() {
        return mRepCounter;
    }

    public void setmRepCounter(int mRepCounter) {
        this.mRepCounter = mRepCounter;
    }

    public String getmTaskHeader() {
        return mTaskHeader;
    }

    public void setmTaskHeader(String mTaskHeader) {
        this.mTaskHeader = mTaskHeader;
    }

    public List<TaskData> getmTaskDatas() {
        return mTaskDatas;
    }

    public void setmTaskDatas(List<TaskData> mTaskDatas) {
        this.mTaskDatas = mTaskDatas;
    }
}
