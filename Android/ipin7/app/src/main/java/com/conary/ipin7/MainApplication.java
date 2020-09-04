package com.conary.ipin7;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

public class MainApplication extends Application
{
    private static MainApplication mInstance;
    public static Activity mActivity;
    public static synchronized MainApplication getInstance()
    {
        return mInstance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance =this;

        registerActivityCallbackToCheckIfAppIsRunning();
    }

    private void registerActivityCallbackToCheckIfAppIsRunning() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {
                // NO-OP
            }

            @Override
            public void onActivityStarted(final Activity activity) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activity.getWindow().setNavigationBarColor(Color.BLACK);
                }
                mActivity = activity;
            }

            @Override
            public void onActivityResumed(final Activity activity) {
                //no-op
            }

            @Override
            public void onActivityPaused(final Activity activity) {
                //no-op
            }

            @Override
            public void onActivityStopped(final Activity activity) {
                //no-op
            }

            @Override
            public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {
                //no-op
            }

            @Override
            public void onActivityDestroyed(final Activity activity) {
                //no-op
            }
        });
    }
}
