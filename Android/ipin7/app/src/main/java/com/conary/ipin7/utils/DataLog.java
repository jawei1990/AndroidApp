package com.conary.ipin7.utils;

import android.os.Environment;
import android.util.Log;

import com.conary.ipin7.MainApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataLog
{
    private static final String DEFAULT_TAG = "iPin7";
    private static final int DEBUG = 0;
    private static final int ERROR = 1;

    private static final String StrPath = MainApplication.getInstance().getApplicationContext().getFilesDir().getAbsolutePath();

    public static void d(final String message) {
        log(0,message);
    }
    public static void e(final String message) { log(1,message); }

    public static void LapCnt(final String message)
    {
        Log.e(DEFAULT_TAG,message);
        appendLog(message,"LapCnt.txt");
    }

    public static void Measure(final String message)
    {
        Log.e(DEFAULT_TAG,message);
        appendLog(message,"Measure.txt");
    }

    private static String getTime()
    {
        SimpleDateFormat date = new SimpleDateFormat("yyMMdd HH:mm:ss.SSS");
        Calendar calDate = Calendar.getInstance();
        String time = date.format(calDate.getTime());
        return time;
    }

    private static void log(int type,String message)
    {
        if (type == DEBUG)
            Log.d(DEFAULT_TAG,message);
        else
            Log.e(DEFAULT_TAG,message);

        String time = getTime();
        DataBase.ExecSQL("insert into LOGS(data) values('" + time +":"+ message + "');");
    }

    public static void appendLog(String text,String file_name) {
        String time = getTime();
        text = time + "---  " + text;

        File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"iPin7");
        File file = new File(directory, file_name);
        try
        {
            if (!file.exists()) {
                directory.mkdirs();
                file.createNewFile();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Awei","---- err:" + e);
        }
    }
}
