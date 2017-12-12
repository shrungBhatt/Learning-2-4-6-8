package com.example.android.learning2_4_6_8;

import android.content.Context;
import android.preference.PreferenceManager;


public class SharedPreferencesData {

    private static String PREF_IS_ALARM_ON = "isAlarmOn";

    public static boolean isAlarmOn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_ALARM_ON,false);
    }

    public static void setAlarmOn(Context context,boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putBoolean(PREF_IS_ALARM_ON,isOn).apply();
    }
}
