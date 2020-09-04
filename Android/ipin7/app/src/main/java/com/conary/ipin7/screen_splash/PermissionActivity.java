package com.conary.ipin7.screen_splash;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.conary.ipin7.screen_main.MainActivity;
import com.conary.ipin7.MainApplication;
import com.conary.ipin7.R;

public class PermissionActivity extends AppCompatActivity implements PermissionView
{
    private PermissionPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.screen_splash);
        mPresenter = new PermissionPresenterImpl(this);
    }

    @Override
    public void ShowMainScreen() {
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               WaitLoading();
            }
        }, 10);
    }

    private void WaitLoading()
    {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean hasPermission(String permission) {
        if (ContextCompat.checkSelfPermission(MainApplication.getInstance(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 1);
            return true;
        } else {
            Log.d(permission, "ok");
            return false;
        }
    }
}
