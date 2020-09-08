package com.conary.ipin7.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences
{
    static final String IPIN7_PREFS = "ipin7_prefs";
    private final SharedPreferences mSharedPreferences;
    private final Context mContext;

    private static final String PREF_RACE_SINGLE_DISTANCE = IPIN7_PREFS + ".raceCnt_distance";
    private static final String PREF_RACE_SCREEN_TIME = IPIN7_PREFS + ".raceCnt_screenTime";

    public UserPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(IPIN7_PREFS, Context.MODE_PRIVATE);
        mContext = context;
    }

    public void set_CntDisData(String data)
    {
        mSharedPreferences.edit().putString(PREF_RACE_SINGLE_DISTANCE, data).apply();
    }
    public String get_CntDisData()
    {
        return mSharedPreferences.getString(PREF_RACE_SINGLE_DISTANCE, "100");
    }

    public void set_CntCurrentTime(String time)
    {
        mSharedPreferences.edit().putString(PREF_RACE_SCREEN_TIME, time).apply();
    }
    public String get_CntCurrentTime()
    {
        return mSharedPreferences.getString(PREF_RACE_SCREEN_TIME, "00:00:000");
    }
}
