package com.example.testangle.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Observable;

public class Accelerometer extends Observable implements SensorEventListener,SensorCtrItf
{

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEvent event;

    public Accelerometer(Context context)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public boolean isSupport() {
        if(sensor == null)
            return  false;
        else
            return true;
    }

    @Override
    public void on(int speed) {
        switch (speed)
        {
            case 1:
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
                break;
            case 2:
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
                break;
            case 3:
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
                break;
            case 0:
            default:
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;

        }
    }

    @Override
    public void off() {
        sensorManager.unregisterListener(this,sensor);
    }

    @Override
    public float getMaxRange() {
        return sensor.getMaximumRange();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.event = event;
        setChanged();
        notifyObservers();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public SensorEvent getEvent(){return event;}
}
