package com.example.laserusbdemo;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataLog
{
    private static final String DEFAULT_TAG = "Awei";
    public static void d(final String message) {
        Log.d(DEFAULT_TAG, message);
    }
    public static void e(final String message) { Log.e(DEFAULT_TAG, message);
        appendLog(message,"sdcard/LaserDemo/OutPut.txt");}

    public static void appendLog(String text,String file_name) {
        File directory = new File(
                Environment.getExternalStorageDirectory() + File.separator + "LaserDemo");

        if (!directory.exists())
            directory.mkdirs();
        /*File externalStorageDir = Environment.getExternalStorageDirectory();
         * File logFile = new File(externalStorageDir , "test.txt");*/
        File logFile = new File(file_name);

        //File logFile = new File("sdcard/test.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
