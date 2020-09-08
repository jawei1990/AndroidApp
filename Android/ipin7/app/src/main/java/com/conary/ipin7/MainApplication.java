package com.conary.ipin7;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;

import com.conary.ipin7.usbModel.UsbModelImpl;
import com.conary.ipin7.utils.Database;
import com.conary.ipin7.utils.DeviceData;
import com.conary.ipin7.utils.UserPreferences;

public class MainApplication extends Application
{
    public static Activity mActivity;
    public static MainApplication mInstance;
    public Database mDataBase;
    private UserPreferences mUserPreferences;
    private UsbManager mUserManager;
    private UsbModelImpl mUsb;

    public static synchronized MainApplication getInstance()
    {
        return mInstance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance =this;

        initDataBase();
        initUserPreferences();
        initUsb();

        registerActivityCallbackToCheckIfAppIsRunning();
    }

    void initDataBase()
    {
        mDataBase = new Database(this);
        mDataBase.open();
    }

    private void initUserPreferences()
    {
        mUserPreferences = new UserPreferences(this);
    }

    private void initUsb()
    {
        mUserManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mUsb = new UsbModelImpl(mUserManager);
    }

    public UsbModelImpl getUsbImp(){ return mUsb;}

    public UserPreferences getUserPreferences(){ return mUserPreferences;}

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
                mUsb.SerialDataWrite(DeviceData.STOP_CONTINUOUS_MEASURE);
                mUsb.SerialDataWrite(DeviceData.CLOSE_LASER);
                mUsb.StopUsbConnection();
            }
        });
    }
}
