package com.conary.ipin7.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.ImageDecoder;

public class Database extends SQLiteOpenHelper
{
    private Context mContext;
    private final static int DB_VER = 1;
    private final static String DB_NAME = "ipin7.db";
    private String INIT_TABLE = "";

    private Database mDbHelper;
    public static SQLiteDatabase mDb;
    public static Cursor cursor;

    public Database(Context context)
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

        INIT_TABLE = "CREATE TABLE IF NOT EXISTS " + "LOGS" + "(" +
              "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
              "data CHAR" +
              ");";
        // init logs table for store debug message.
        db.execSQL(INIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public Database open()
    {
        if(mDbHelper == null)
            mDbHelper = new Database(mContext);

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
}
