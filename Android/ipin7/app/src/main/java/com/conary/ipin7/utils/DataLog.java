package com.conary.ipin7.utils;

import android.util.Log;

public class DataLog
{
    private static final String DEFAULT_TAG = "iPin7";
    public static void d(final String message) {
            Log.d(DEFAULT_TAG, message);
    }

    public static void e(final String message) {
        Log.d(DEFAULT_TAG, message);
    }

}
