package com.conary.ipin7.usbModel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.conary.ipin7.utils.DataLog;
import static com.conary.ipin7.usbModel.UsbModelImpl.serialPortConnected;

public class UsbReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if(UsbConst.ACTION_USB_PERMISSION.equals(action))
        {
            synchronized (this)
            {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false))
                {
                    DataLog.e("permission OK");
                }
                else
                {
                    DataLog.e("permission denied. QQ");
                }
            }
        }
        else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
        {
            DataLog.e("USB innnnnnnnnnnn");
            serialPortConnected = true;
        }
        else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action))
        {
            DataLog.e(" USB xxxxx Out");
            serialPortConnected = false;
        }
    }
}
