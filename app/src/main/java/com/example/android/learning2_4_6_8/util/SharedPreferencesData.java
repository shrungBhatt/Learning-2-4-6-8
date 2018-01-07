package com.example.android.learning2_4_6_8.util;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.List;


public class SharedPreferencesData {

    private static String PREF_IS_ALARM_ON = "isAlarmOn";
    private static String PREF_TASK_ARRAY_JSON = "taskArrayList";
    private static String PREF_POLLING_CHECKBOX_VAL= "pollingCheckboxValue";
    private static String FLAG_UPDATE_TASKS = "updateTasksFlag";

    public static boolean isAlarmOn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_ALARM_ON,false);
    }

    public static void setAlarmOn(Context context,boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putBoolean(PREF_IS_ALARM_ON,isOn).apply();
    }

    public static String getTaskArrayJson(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_TASK_ARRAY_JSON,null);
    }

    public static void setTaskArrayJson(Context context,String arrayJson){
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_TASK_ARRAY_JSON,arrayJson).apply();

    }

    public static boolean isPollingCheckBoxChecked(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_POLLING_CHECKBOX_VAL,false);
    }

    public static void setPollingCheckBoxVal(Context context,boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putBoolean(PREF_POLLING_CHECKBOX_VAL,isOn).apply();
    }

    public static boolean isFlagFetchTaskOn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(FLAG_UPDATE_TASKS,false);
    }

    public static void setFlagFetchTasks(Context context, boolean flagValue){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(FLAG_UPDATE_TASKS,flagValue).apply();
    }

}
