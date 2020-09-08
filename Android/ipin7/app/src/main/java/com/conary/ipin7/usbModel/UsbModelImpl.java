package com.conary.ipin7.usbModel;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.conary.ipin7.MainApplication;
import com.conary.ipin7.utils.ConversionUtils;
import com.conary.ipin7.utils.DataLog;
import com.conary.ipin7.utils.DeviceData;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.Arrays;
import java.util.Map;

import static com.conary.ipin7.utils.DeviceStatus.deviceOffset;
import static com.conary.ipin7.utils.DeviceStatus.isLaserOn;
import static com.conary.ipin7.utils.DeviceStatus.isContinous;

public class UsbModelImpl implements UsbModel
{

    private UsbDeviceConnection connection;
    private UsbSerialDevice mUsbSerialDevice;
    public static boolean serialPortConnected = false;
    private UsbManager mUserManager;
    private boolean isContinMode = false;
    private byte[] RxBuf = new byte[20];

    private HandlerThread mUsbThread;
    private Handler mBackgroundUsbHandler;

    private UsbView view;

    public interface UsbView
    {
        void UsbDebugLog(String str);
        void USB_UI_Viwe(int data,Object obj);
    }

    public UsbModelImpl(UsbManager mUserManager)
    {
        this.mUserManager = mUserManager;
    }

    public void USB_ViewInit(UsbView view)
    {
        this.view = view;
        mUsbThread = null;
    }

    public boolean RegisterUsbDevice()
    {
        Map<String, UsbDevice> ConnectionDevice = mUserManager.getDeviceList();
        DataLog.e("StartUsbConnection:::" + ConnectionDevice.isEmpty());
        if(!ConnectionDevice.isEmpty())
        {
            for(UsbDevice device: ConnectionDevice.values())
            {

                DataLog.e("Sdevice.getVendorId():::" + device.getVendorId());
                DataLog.e("device.getProductId():::" + device.getProductId());
                if( (device.getVendorId() == UsbConst.USB_VENDOR_ID &&
                     device.getProductId() == UsbConst.USB_PRODUCT_ID) ||
                    (device.getVendorId() == UsbConst.USB_FTDI_VENDOR_ID &&
                     device.getProductId() == UsbConst.USB_FTDI_PRODUCT_ID) )
                {
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(MainApplication.getInstance(), 0,
                            new Intent(MainApplication.getInstance(), UsbReceiver.class), 0);

                    if(mUserManager.hasPermission(device)){
                        DataLog.e("USB Permission OK");
                        return true;
                    }else{
                        DataLog.e("Request USB Permission");
                        mUserManager.requestPermission(device, mPermissionIntent);
                        return false;
                    }
                }
                else
                {
                    return true;
                }
            }
        }
        else
        {
            return true;
        }

        return false;
    }

    private void startBackgroundUsbThread()
    {
        mUsbThread = new HandlerThread("UsbBackground");
        mUsbThread.start();
        mBackgroundUsbHandler = new Handler(mUsbThread.getLooper());
        mBackgroundUsbHandler.post(ReadData);
    }

    private Runnable ReadData =new Runnable () {

        public void run() {
            try
            {
                if(serialPortConnected)
                    mBackgroundUsbHandler.postDelayed(ReadData,500);

                if(isContinous)
                    SerialDataRead();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

    };

    private void stopBackgroundUsbThread()
    {
        if(mUsbThread != null)
        {
            mUsbThread.quitSafely();
            try
            {
                mUsbThread.join();
                mUsbThread = null;
                mBackgroundUsbHandler = null;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }

    private void StartSerialConnection(UsbDevice device)
    {
        DataLog.e("Ready to open USB device connection...");
        connection = mUserManager.openDevice(device);
        mUsbSerialDevice = UsbSerialDevice.createUsbSerialDevice(device,connection);
        if(mUsbSerialDevice != null)
        {
            try
            {
                if(mUsbSerialDevice.open())
                {
                    mUsbSerialDevice.setBaudRate(UsbConst.SERIAL_BAUD_RATE);
                    mUsbSerialDevice.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    mUsbSerialDevice.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    mUsbSerialDevice.setParity(UsbSerialInterface.PARITY_NONE);

                    //TODO: need to change to FLOW_CONTROL_RTS_CTS for FTDI or CP201x
//                mUsbSerialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    mUsbSerialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);

                    DataLog.e("Wait Read Call Back..........");
                    mUsbSerialDevice.read(ReadCallBack);
                    serialPortConnected = true;
                    view.USB_UI_Viwe(DeviceData.DEVICE_CONNECTED,null);
                    startBackgroundUsbThread();

                    byte[] init = new byte[1];
                    init[0] = (byte) 0x55;
                    mUsbSerialDevice.write(init);

                    byte[] status = {(byte)0xAA,(byte)0x80,0x00,0x00,(byte)0x80};
                    mUsbSerialDevice.write(status);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void USB_ContinuousMeasure()
    {
        DataLog.e("USB_ContinuousMeasure");
        isContinMode = true;
        SerialDataWrite(DeviceData.FAST_CONTINUOUS_MEASURE);
    }

    public void USB_OneShotMeasure()
    {
        Log.e("Awei","USB_OneShotMeasure");
        isContinMode = false;
        SerialDataWrite(DeviceData.ONE_SHOT_MEASURE);
    }

    public void USB_StopMeasure()
    {
        DataLog.e("USB_StopMeasure");
        isContinMode = false;
        SerialDataWrite(DeviceData.STOP_CONTINUOUS_MEASURE);
    }

    public void StartUsbConnection()
    {
        Map<String,UsbDevice> ConnectionDevice = mUserManager.getDeviceList();
        DataLog.e("StartUsbConnection:::" + ConnectionDevice.isEmpty());
        if(!ConnectionDevice.isEmpty())
        {
            for(UsbDevice device: ConnectionDevice.values())
            {

                DataLog.e("Sdevice.getVendorId():::" + device.getVendorId());
                DataLog.e("device.getProductId():::" + device.getProductId());
                if( (device.getVendorId() == UsbConst.USB_VENDOR_ID &&
                     device.getProductId() == UsbConst.USB_PRODUCT_ID) ||
                    (device.getVendorId() == UsbConst.USB_FTDI_VENDOR_ID &&
                     device.getProductId() == UsbConst.USB_FTDI_PRODUCT_ID) )
                {
                    StartSerialConnection(device);
                    return;
                }
            }
        }
    }

    public void StopUsbConnection()
    {
        try
        {
            if(connection != null)
            {
                mUsbSerialDevice.close();
                connection.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            stopBackgroundUsbThread();
            mUsbSerialDevice = null;
            connection = null;
            serialPortConnected = false;
        }
    }

    public boolean GetUsbConnection()
    {
        if(serialPortConnected)
        {
            view.USB_UI_Viwe(DeviceData.DEVICE_CONNECTED,null);
            return true;
        }
        else
        {
            view.USB_UI_Viwe(DeviceData.DEVICE_DISCONNECTED,null);
            return false;
        }
    }

    public void SerialDataWrite(byte[] data)
    {
        try
        {
            mUsbSerialDevice.write(data);
            rxStr = "";
        }
        catch(Exception e)
        {
            DataLog.e("USB >> Tx err:" + e.toString());
            e.printStackTrace();
        }
    }

    public void SerialDataRead()
    {
        Log.e("Awei","---- SerialDataRead");
        Arrays.fill(RxBuf, (byte)0);
        int len = mUsbSerialDevice.syncRead(RxBuf,10);
//        mUsbSerialDevice.read(ReadCallBack);

        if(len != 0)
            DecodeData(RxBuf);
    }

    private void DecodeStr(String StrData)
    {
        try
        {
            byte[] data = ConversionUtils.hexToByte(StrData);

//            view.UsbDebugLog("Rx:" + StrData);
//            Log.e("Awei","---- Rx:" + StrData);
            if (StrData.contains(DeviceData.DECODE_ONE_SHOT_PACKAGE_DATA))
            {
                if(data.length < 11)
                    return;

                Log.e("Awei","Rx Data:" + StrData);
                DataLog.e("Rx Data:" + StrData);
                byte[] encodeData = {data[6], data[7], data[8], data[9]};
                String str = ConversionUtils.bytesToHex(encodeData);
                Long mm = Long.parseLong(str, 16);

                byte[] payload = {data[10], data[11]};
                String signal = ConversionUtils.bytesToHex(payload);
                Long quality = Long.parseLong(signal, 16);

                double ret = (double) mm / 10 + deviceOffset; // Transfer to cm
                DataLog.e("Dline:" + ret + "cm," + (isContinMode ? "isContinus" : ",One Shot"));

                Log.e("Awei","Dline:" + ret + "cm," + (isContinMode ? "isContinus" : ",One Shot"));

                if(ret != 0)
                {
                    view.USB_UI_Viwe(DeviceData.DEVICE_UPDATE_DATA,ret);
                }
            }
            else if(StrData.contains(DeviceData.DECODE_LASER_ON_DATA))
            {
                Log.e("Awei","Laser ON");
                DataLog.e("Rx Data:" + StrData);
                isLaserOn = true;
            }
            else if(StrData.contains(DeviceData.DECODE_LASER_OFF_DATA))
            {
                Log.e("Awei","Laser Off xxxx");
                DataLog.e("Rx Data:" + StrData);
                isLaserOn = false;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void DecodeData(byte[] data)
    {
        try
        {
            String StrData = ConversionUtils.bytesToHex(data);
//            view.UsbDebugLog("Rx:" + StrData);
            Log.e("Awei","---- Rx:" + StrData);
            if (StrData.contains(DeviceData.DECODE_ONE_SHOT_PACKAGE_DATA))
            {
                DataLog.e("Rx Data:" + StrData);
                Log.e("Awei","Rx:" + StrData);
                byte[] encodeData = {data[6], data[7], data[8], data[9]};
                String str = ConversionUtils.bytesToHex(encodeData);
                Long mm = Long.parseLong(str, 16);

                byte[] payload = {data[10], data[11]};
                String signal = ConversionUtils.bytesToHex(payload);
                Long quality = Long.parseLong(signal, 16);

                double ret = (double) mm / 10 + deviceOffset; // Transfer to cm
                DataLog.e("Dline:" + ret + "cm," + (isContinMode ? "isContinus" : ",One Shot"));
                view.UsbDebugLog(
                        "ret:" + ret +",mm::" + mm +",quality::" + quality+ ",isContinMode:" + isContinMode);

                if(ret != 0)
                {
                    view.USB_UI_Viwe(DeviceData.DEVICE_UPDATE_DATA,ret);
                }
            }
            else if(StrData.contains(DeviceData.DECODE_LASER_ON_DATA))
            {
                DataLog.e("Rx Data:" + StrData);
                isLaserOn = true;
            }
            else if(StrData.contains(DeviceData.DECODE_LASER_OFF_DATA))
            {
                DataLog.e("Rx Data:" + StrData);
                isLaserOn = false;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private String rxStr = "";
    private UsbSerialInterface.UsbReadCallback ReadCallBack  = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] data)
        {
            DecodeData(data);
        }
    };
}
