package com.example.testangle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.testangle.sensor.SensorCallback;
import com.example.testangle.sensor.SensorCtr;
import com.example.testangle.sensor.SensorCtrItf;

public class MainActivity extends AppCompatActivity implements SensorCallback
{
    private TextView textA,textM,textG,temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textA = findViewById(R.id.tempA);
        textM = findViewById(R.id.tempM);
        textG = findViewById(R.id.tempG);
        temp = findViewById(R.id.temp);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SensorCtr sensorCtr = new SensorCtr(this.getApplicationContext(),this);
        sensorCtr.init(0.0,0.0,0.0);
        sensorCtr.on(3);
    }

    @Override
    public void SensorAngleCallback(Double azimuth, Double pitch, Double roll,Double calData) {
        String strAngle = "p:"+String.format("%.3f",pitch)+ "," +
                          "r:"+ String.format("%.3f",roll)+","+
                          "calData:" +String.format("%.3f",calData) ;

        String strAzimuth = "a:"+String.format("%.3f",azimuth);
        temp.setText(strAngle);
        textA.setText(strAzimuth);
    }
}