package com.example.testthread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    Button BtnStart, BtnPause;
    Thread thread;
    boolean isThreadEnable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnStart = findViewById(R.id.BtnStart);
        BtnPause = findViewById(R.id.BtnPause);

        BtnStart.setOnClickListener(this);
        BtnPause.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.BtnStart:
                startFunct();
                break;
            case R.id.BtnPause:
                PauseFunct();
                break;
        }
    }

    private Runnable StartRunnable = new Runnable()
    {
        @Override
        public void run() {
            Log.e("Awei","threadStatus::" + isThreadEnable);
            while (isThreadEnable)
            {
                try
                {
                    Log.e("Awei","Thread B 開始..");
                    for(int i = 0; i < 5; i++) {
                        thread.sleep(1000);
                        Log.e("Awei","Thread B 執行.." + i);
                    }
                    Log.e("Awei","Thread B 即將結束..");
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    private void startFunct()
    {
        if(thread == null)
        {
            Log.e("Awei","---- startFunct ------");
            thread = new Thread(StartRunnable);
            isThreadEnable = true;
            thread.start();
        }
    }

    private void PauseFunct()
    {
        if(thread != null)
        {
            try
            {
                Log.e("Awei","Thread Pause ......");
                isThreadEnable = false;
                thread.interrupt();
                thread = null;
               // thread.wait();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}