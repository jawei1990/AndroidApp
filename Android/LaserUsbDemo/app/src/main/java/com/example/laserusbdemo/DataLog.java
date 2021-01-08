package com.example.laserusbdemo;

import android.util.Log;

public class DataLog
{
    private static final String DEFAULT_TAG = "Awei";
    public static void d(final String message) {
        Log.d(DEFAULT_TAG, message);
    }
    public static void e(final String message) { Log.e(DEFAULT_TAG, message); }
}
