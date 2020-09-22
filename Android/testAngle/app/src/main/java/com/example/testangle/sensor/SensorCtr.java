package com.example.testangle.sensor;

import android.content.Context;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class SensorCtr implements SensorCtrItf, Observer
{
    private SensorResp sensorResp;
    private Accelerometer accSensor;

    private double[] accData = new double[3];
    private double g, calData;
    double Ax,Ay,Az;

    private final double deg = 180 / Math.PI;
    private final double rad = Math.PI / 180;
    private Timer fuseTimer;
    private Handler handler = new Handler();

    public SensorCtr(Context context, SensorCallback callback)
    {
        accSensor = new Accelerometer(context);
        sensorResp = new SensorResp(callback);

        Ax = 0;
        Ay = 0;
        Az = 0;
    }

    public void init(Double azimuth_tol, Double pitch_tol, Double roll_tol)
    {
        sensorResp.setTolerance(azimuth_tol,pitch_tol,roll_tol);
    }

    public void dispose()
    {
        accSensor = null;
        sensorResp = null;
    }

    @Override
    public boolean isSupport() {
        if (accSensor.isSupport())
            return true;
        else
            return false;
    }

    @Override
    public void on(int speed) {
        if (accSensor.isSupport())
        {
            accSensor.addObserver(this);
            accSensor.on(speed);
        }

        fuseTimer = new Timer();
        switch (speed){
            case 0:
                fuseTimer.scheduleAtFixedRate(new calcualteDataTask(),
                        1, 224);
                break;
            case 1:
                fuseTimer.scheduleAtFixedRate(new calcualteDataTask(),
                        1, 77);
                break;
            case 2:
                fuseTimer.scheduleAtFixedRate(new calcualteDataTask(),
                        1, 37);
                break;
            default:
                fuseTimer.scheduleAtFixedRate(new calcualteDataTask(),
                        1, 16);
        }
    }

    private double r,p,z;
    class calcualteDataTask extends TimerTask
    {
        public void run()
        {
            g = Ax* Ax + Ay * Ay + Az * Az;
            g = Math.sqrt(g);

            z = Az / g;
            z = Math.acos(z) * deg ;
            accData[0] = z;

            p = Ax / g;
            p = Math.asin(p) * deg ;
            accData[1] = p;

            r = Ay / g;
            r = Math.asin(r) * deg ;
            accData[2] = r;

            double gp = p * rad;
            double gr = r * rad;

            double tmpGp = Math.sin(gp);
            tmpGp = tmpGp * tmpGp;

            double tmpGr = Math.sin(gr);
            tmpGr = tmpGr*tmpGr;

            double t1 = Math.sqrt(tmpGp+tmpGr);
            calData = Math.asin(t1) * deg;

            handler.post(updateValueTask);
        }
    }

    public void updateValues()
    {
        sensorResp.dispatcher(accData,calData);
    }

    public Runnable updateValueTask = new Runnable()
    {
        @Override
        public void run() {
            updateValues();
        }
    };

    @Override
    public void off() {

    }

    @Override
    public float getMaxRange() {
        return 0;
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable instanceof Accelerometer)
        {
            Ax = accSensor.getEvent().values[0];
            Ay = accSensor.getEvent().values[1];
            Az = accSensor.getEvent().values[2];
        }
    }
}
