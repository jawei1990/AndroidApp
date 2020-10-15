package com.conary.ipin7.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase extends SQLiteOpenHelper
{
    private Context mContext;
    private final static int DB_VER = 2;
    private final static String DB_NAME = "ipin7.db";
    private String INIT_TABLE = "";

    private DataBase mDbHelper;
    public static SQLiteDatabase mDb;
    public static Cursor cursor;

    public DataBase(Context context)
    {
        super(context, DB_NAME, null, DB_VER);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        INIT_TABLE = "CREATE TABLE IF NOT EXISTS " + "SENSOR_TABLE"+"(" +
                     "_id INTEGER primary key autoincrement," +
                     "time DATETIME ," +
                     "log TEXT " +
                    ");";
        // Init Sensor table
        db.execSQL(INIT_TABLE);

        INIT_TABLE = "CREATE TABLE IF NOT EXISTS " + "COUNTING_TABLE" + "("+
              "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
              "_idx INTEGER," +
              "_time DATETIME,"+
              "_speed DOUBLE"+
              ");";
        // Init counting table
        db.execSQL(INIT_TABLE);

        INIT_TABLE = "CREATE TABLE IF NOT EXISTS " + "BIKE_TABLE" + "("+
              "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
              "speed DOUBLE ," +
              " freq DOUBLE ," +
              "distance DOUBLE ," +
              "time DATETIME "+
              ");";

        //init bike table
        db.execSQL(INIT_TABLE);

        INIT_TABLE = "CREATE TABLE IF NOT EXISTS " + "MEASURE_TABLE" + "("+
                     "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "time DATETIME , "+
                     "distance DOUBLE" +
                     ");";

        //init measure table
        db.execSQL(INIT_TABLE);

        INIT_TABLE = "CREATE TABLE IF NOT EXISTS " + "LOGS" + "(" +
              "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
              "data CHAR" +
              ");";
        // init logs table for store debug message.
        db.execSQL(INIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete table if exists.
        db.execSQL("DROP TABLE IF EXISTS SENSOR_TABLE");
        db.execSQL("DROP TABLE IF EXISTS COUNTING_TABLE");
        db.execSQL("DROP TABLE IF EXISTS BIKE_TABLE");
        db.execSQL("DROP TABLE IF EXISTS MEASURE_TABLE");
        db.execSQL("DROP TABLE IF EXISTS LOGS");
        onCreate(db);
    }

    public DataBase open()
    {
        if(mDbHelper == null)
            mDbHelper = new DataBase(mContext);

        mDb = mDbHelper.getWritableDatabase();

        return this;
    }

    public void close()
    {
        if(mDbHelper == null)
            return;

        mDb.close();
        mDbHelper.close();
    }

    public static void ExecSQL(String action)
    {
        try
        {
            mDb.execSQL(action);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Cursor selDataBase(String name)
    {
        Cursor cursor;
        switch (name)
        {
            case "LapCnt":
                cursor = mDb.query("COUNTING_TABLE", null, null, null, null, null, null);
                break;
            case "Sensor":
                cursor = mDb.query("SENSOR_TABLE", null, null, null, null, null, null);
                break;
            case "bike":
                cursor = mDb.query("BIKE_TABLE", null, null, null, null, null, null);
                break;
            case "Measure":
                cursor = mDb.query("MEASURE_TABLE", null, null, null, null, null, null);
                break;
            default:
                cursor = mDb.query("LOGS", null, null, null, null, null, null);
                break;
        }

        return cursor;
    }

    public static long insLapCntData(int idx, String date, double speed)
    {
        ContentValues cv = new ContentValues();

        cv.put("_idx", idx);
        cv.put("_time", date);
        cv.put("_speed", speed);

        long row = mDb.insert("COUNTING_TABLE", null, cv);
        return row;
    }
}
