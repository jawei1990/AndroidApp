package com.example.usbtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String ACTION_USB_PERMISSION = "com.example.usbtest.USB_PERMISSION";
    private final String TAG = "UsbTest";
    TextView tv;
    Button BtnOn,BtnOff,BtnSigle,BtnShots,BtnStop;

    private Handler handler;
    public byte[] writeBuffer;
    public byte[] readBuffer;
    public int actualNumBytes;

    public int numBytes;
    public byte count;
    public int status;
    public byte writeIndex = 0;
    public byte readIndex = 0;

    public byte baudRate_byte;


    private boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainApplication.driver = new CH34xUARTDriver((UsbManager) getSystemService(Context.USB_SERVICE),
                this, ACTION_USB_PERMISSION);
        CheckHostDevice();
        initUI();
        writeBuffer = new byte[512];
        readBuffer = new byte[512];
        isOpen = false;

    }

    private void CheckHostDevice()
    {
        if(!MainApplication.driver.UsbFeatureSupported())
        {
            Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Hint")
                    .setMessage("Not sup this smart phone, please change another smart phone...")
                    .setPositiveButton("OK", (dialogInterface,which)->finish()).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        else
        {
            int retval = MainApplication.driver.ResumeUsbPermission();
            if(retval == 0)
            {
                retval = MainApplication.driver.ResumeUsbList();
                if(retval == 0)
                {
                    if(MainApplication.driver.mDeviceConnection != null)
                    {
                        if(!MainApplication.driver.UartInit() )
                            return;

                        Log.e(TAG,"Device Open!");
                        Toast.makeText(MainActivity.this, "Device Open!",
                                Toast.LENGTH_SHORT).show();
                        isOpen = true;
                        MainApplication.driver.SetConfig(UsbConst.SERIAL_BAUD_RATE, UsbConst.DATA_BIT_8, UsbConst.STOP_BIT_1, UsbConst.PARITY_NONE,
                                UsbConst.FLOW_CTR_NONE);
                        new readThread().start();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Open failed!",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"Open failed!");
                    }
                }
            }
        }
    }

    private void initUI()
    {
        tv = findViewById(R.id.tv);
        BtnOn = findViewById(R.id.btnOn);
        BtnOff = findViewById(R.id.btnOff);
        BtnSigle = findViewById(R.id.btnShot);
        BtnShots = findViewById(R.id.btnShotCons);
        BtnStop = findViewById(R.id.btnStop);

        BtnOn.setOnClickListener(this);
        BtnOff.setOnClickListener(this);
        BtnSigle.setOnClickListener(this);
        BtnShots.setOnClickListener(this);
        BtnStop.setOnClickListener(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        CheckHostDevice();
    }

    @Override
    public void onDestroy()
    {
        isOpen = false;
        MainApplication.driver.CloseDevice();
        super.onDestroy();
    }

    private void readData(String data)
    {
        Log.e(TAG,"Rx Data:"+data);

        if(data.contains(DECODE_ONE_SHOT_PACKAGE_DATA))
        {
            double ret = 0.0f;
            // Avoid different contry has differ double transfer .
            try
            {
                byte[] encodeData = {readBuffer[6], readBuffer[7], readBuffer[8], readBuffer[9]};
                String string = bytesToHex(encodeData);
                Long mm = Long.parseLong(string, 16);

                byte[] payload = {readBuffer[10], readBuffer[11]};
                String signal = bytesToHex(payload);
                Long quality = Long.parseLong(signal, 16);

                ret = (double) mm / 10; // Transfer to cm

                String str = "Distance:" + String.valueOf(ret) + "cm";
                tv.setText("Distance:" + ret + "cm");
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private class readThread extends Thread {

        public void run() {
            while (true) {

                if (!isOpen) {
                    break;
                }

                int length = MainApplication.driver.ReadData(readBuffer, readBuffer.length);
                Log.e(TAG,"Rungggggg");
                if (length > 0) {
                    String recv = toHexString(readBuffer, length);
                    readData(recv);
//                    msg.obj = recv;
//                    handler.sendMessage(msg);
                }
            }
        }
    }

    public static String hexString = "0123456789ABCDEF";
    private final static char[] HEX_ARRAY = hexString.toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private String toHexString(byte[] arg, int length) {
        String result = new String();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result = result
                        + (Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])) + " ";
            }
            return result;
        }
        return "";
    }

    byte[] OPEN_LASER = {(byte) 0xAA,(byte)0x00,0x01,(byte)0xBE,(byte)0x00,0x01,0x00,0x01,(byte)0xc1};
    byte[] CLOSE_LASER = {(byte) 0xAA,(byte)0x00,0x01,(byte)0xBE,(byte)0x00,0x01,0x00,0x00,(byte)0xc0};
    byte[] STOP_CONTINUOUS_MEASURE = {0x58};
    byte[] CONTINUOUS_MEASURE = {(byte) 0xAA,0x00,0x00,0x20,0x00,0x01,0x00,0x04,0x25};
    byte[] ONE_SHOT_MEASURE = {(byte) 0xAA,0x00,0x00,0x20,0x00,0x01,0x00,0x00,0x21};

    String DECODE_ONE_SHOT_PACKAGE_DATA = "AA0000220003";
    String DECODE_LASER_ON_DATA = "AA0001BE00010001C1";
    String DECODE_LASER_OFF_DATA = "AA0001BE00010000C0";

    @Override
    public void onClick(View v)
    {
        int retval = 0;
        switch(v.getId())
        {
            case R.id.btnOn:
                retval = MainApplication.driver.WriteData(OPEN_LASER, OPEN_LASER.length);
                if (retval < 0)
                    Toast.makeText(MainActivity.this, "Write failed!",
                            Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnOff:
                retval = MainApplication.driver.WriteData(CLOSE_LASER, CLOSE_LASER.length);
                if (retval < 0)
                    Toast.makeText(MainActivity.this, "Write failed!",
                            Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnShot:
                retval = MainApplication.driver.WriteData(ONE_SHOT_MEASURE, ONE_SHOT_MEASURE.length);
                if (retval < 0)
                    Toast.makeText(MainActivity.this, "Write failed!",
                            Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnShotCons:
                retval = MainApplication.driver.WriteData(CONTINUOUS_MEASURE, CONTINUOUS_MEASURE.length);
                if (retval < 0)
                    Toast.makeText(MainActivity.this, "Write failed!",
                            Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnStop:
                retval = MainApplication.driver.WriteData(STOP_CONTINUOUS_MEASURE, STOP_CONTINUOUS_MEASURE.length);
                if (retval < 0)
                    Toast.makeText(MainActivity.this, "Write failed!",
                            Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
