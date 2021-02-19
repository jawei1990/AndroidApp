package com.example.laserusbdemo;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences
{
    static final String LASER_DEMO_PREFS = "laseDemo_prefs";

    private static final String PREF_OFFSET = LASER_DEMO_PREFS + ".offset";
    private static final String PREF_OFFSET_DATA = LASER_DEMO_PREFS + ".offset_data";
    private static final String PREF_VISIBLE_DATA = LASER_DEMO_PREFS + ".visible_data";
    private static final String PREF_ROTATION_DATA = LASER_DEMO_PREFS + ".rotation_data";
    private final SharedPreferences mSharedPreferences;
    private final Context mContext;

    public UserPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(LASER_DEMO_PREFS, Context.MODE_PRIVATE);
        mContext = context;
    }

    public void setPrefOffset(int value)
    {
        mSharedPreferences.edit().putInt(PREF_OFFSET, value).apply();
    }

    public int getPrefOffset() {
        return mSharedPreferences.getInt(PREF_OFFSET, 0);
    }


    public void setPrefOffsetData(int value)
    {
        mSharedPreferences.edit().putInt(PREF_OFFSET_DATA, value).apply();
    }

    public int getPrefOffsetData() {
        return mSharedPreferences.getInt(PREF_OFFSET_DATA, 0);
    }

    public void setPrefVisibleData(boolean flag)
    {
        mSharedPreferences.edit().putBoolean(PREF_VISIBLE_DATA, flag).apply();
    }

    public boolean getPrefVisibleData() {
        return mSharedPreferences.getBoolean(PREF_VISIBLE_DATA, false);
    }

    public void setPrefRotationData(boolean flag)
    {
        mSharedPreferences.edit().putBoolean(PREF_ROTATION_DATA, flag).apply();
    }

    public boolean getPrefRotationData() {
        return mSharedPreferences.getBoolean(PREF_ROTATION_DATA, false);
    }
}
