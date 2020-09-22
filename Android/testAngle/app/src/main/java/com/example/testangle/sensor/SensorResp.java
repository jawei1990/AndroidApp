package com.example.testangle.sensor;

import java.util.ArrayList;

public class SensorResp
{
    private ArrayList<Double> sensorDataLog = new ArrayList<Double>();
    private ArrayList<Double> tolerance = new ArrayList<Double>();
    private SensorCallback observer;

    private final double deg = 180 / Math.PI;
    private final double rad = Math.PI / 180;

    public SensorResp(SensorCallback sci)
    {
        tolerance.add(0,0.0);
        tolerance.add(1,0.0);
        tolerance.add(2,0.0);

        sensorDataLog.add(0,0.0);
        sensorDataLog.add(1,0.0);
        sensorDataLog.add(2,0.0);

        this.observer = sci;
    }

    public void setTolerance(Double azimuth, Double pitch, Double roll)
    {
        tolerance.add(0,azimuth);
        tolerance.add(1,pitch);
        tolerance.add(2,roll);
    }

    public void dispatcher(double[] value, double calData)
    {
        Double azimuth = value[0];
        Double pitch = value[1];
        Double roll = value[2];

        if(Math.abs(sensorDataLog.get(0) - azimuth) > tolerance.get(0) ||
           Math.abs(sensorDataLog.get(1) - pitch) > tolerance.get(1) ||
           Math.abs(sensorDataLog.get(2) - roll) > tolerance.get(2))
        {
            sensorDataLog.set(0,azimuth);
            sensorDataLog.set(1,pitch);
            sensorDataLog.set(2,roll);
            observer.SensorAngleCallback(sensorDataLog.get(0),sensorDataLog.get(1),sensorDataLog.get(2),calData);
        }
    }
}
