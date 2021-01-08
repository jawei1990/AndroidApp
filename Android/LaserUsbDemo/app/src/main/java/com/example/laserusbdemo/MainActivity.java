package com.example.laserusbdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener
{

    public static Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, new MainScreen(), "devices").commit();
            //getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DevicesFragment(), "devices").commit();
        }
        else
            onBackStackChanged();
    }

    @Override
    public void onBackStackChanged() {
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED"))
        {
            MainScreen terminal = (MainScreen) getSupportFragmentManager().findFragmentByTag("terminal");
            if (terminal != null)
                terminal.detected("USB device detected");
        }
        else if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_DETACHED"))
        {
            MainScreen terminal = (MainScreen) getSupportFragmentManager().findFragmentByTag("terminal");
            if (terminal != null)
                terminal.lost("Device Lost");
        }
        super.onNewIntent(intent);

        Log.e("Awei","itent:" + intent.getAction());
    }

}

