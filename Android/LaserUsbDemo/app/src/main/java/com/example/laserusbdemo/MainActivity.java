package com.example.laserusbdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener
{
   // public static  Activity activity;
   // public static Context context = null;
    public static String path;
    private TestScreen mCurFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        path = getFilePath();

        Log.e("Awei","path:" + path);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null)
        {
            mCurFragment = new TestScreen(this,this);
            //getSupportFragmentManager().beginTransaction().add(R.id.fragment, new MainScreen(), "devices").commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, mCurFragment, "devices").commit();
            //getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DevicesFragment(), "devices").commit();
        } else
            onBackStackChanged();
    }

    private String getFilePath()
    {
        String path = "";
        try
        {
            path = getExternalFilesDir(null).getPath().toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if (path.equals(""))
        {
            try
            {
                path = getFilesDir().getPath().toString();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return path;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            if(mCurFragment!=null)
                mCurFragment.FragmentOnKeyDown(keyCode);

            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        check();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurFragment.onPause();
    }


    public boolean hasPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
            return true;
        } else {
            Log.d(permission, "ok");
            return false;
        }
    }

    private void check(){
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
        {
        } else if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
        }
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
            //MainScreen terminal = (MainScreen) getSupportFragmentManager().findFragmentByTag("terminal");
            TestScreen terminal = (TestScreen) getSupportFragmentManager().findFragmentByTag("terminal");

            if (terminal != null)
                terminal.detected("USB device detected");
        }
        else if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_DETACHED"))
        {
            //MainScreen terminal = (MainScreen) getSupportFragmentManager().findFragmentByTag("terminal");
            TestScreen terminal = (TestScreen) getSupportFragmentManager().findFragmentByTag("terminal");

            if (terminal != null)
                terminal.lost("Device Lost");
        }
        super.onNewIntent(intent);

        Log.e("Awei","itent:" + intent.getAction());
    }

}

