package com.conary.ipin7.screen_splash;

import android.Manifest;
import android.content.pm.PackageManager;

import com.conary.ipin7.MainApplication;

public class PermissionPresenterImpl implements PermissionPresenter
{
    private PermissionView mView;

    PermissionPresenterImpl(PermissionView view)
    {
        this.mView = view;
    }

    /** Check Hw permission: External、Storge */
    public void CheckHwPermission()
    {
        if (mView.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
        {
        }
        else if (mView.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
        }
        else if (mView.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
        {
        }
        else if (mView.hasPermission(Manifest.permission.READ_PHONE_STATE)){
        }
        else
        {
                mView.ShowMainScreen();
        }
    }
}
