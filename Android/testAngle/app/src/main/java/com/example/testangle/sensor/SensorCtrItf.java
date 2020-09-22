package com.example.testangle.sensor;

public interface SensorCtrItf
{
    public boolean isSupport();

    public void on(int speed);

    public void off();

    public float getMaxRange();
}
