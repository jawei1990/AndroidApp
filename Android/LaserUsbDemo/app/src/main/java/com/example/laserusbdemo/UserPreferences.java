package com.example.laserusbdemo;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences
{
    static final String LASER_DEMO_PREFS = "laseDemo_prefs";

    private static final String PREF_OFFSET_DATA = LASER_DEMO_PREFS + ".offset_data";
    private final SharedPreferences mSharedPreferences;
    private final Context mContext;

    public UserPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(LASER_DEMO_PREFS, Context.MODE_PRIVATE);
        mContext = context;
    }

    public void setPrefOffsetData(int value)
    {
        mSharedPreferences.edit().putInt(PREF_OFFSET_DATA, value).apply();
    }

    public int getPrefOffsetData() {
        return mSharedPreferences.getInt(PREF_OFFSET_DATA, 0);
    }
}
