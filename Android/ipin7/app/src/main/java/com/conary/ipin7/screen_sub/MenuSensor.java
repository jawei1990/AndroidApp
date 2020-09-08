package com.conary.ipin7.screen_sub;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conary.ipin7.MainApplication;
import com.conary.ipin7.R;
import com.conary.ipin7.adapter.ListSensor;
import com.conary.ipin7.adapter.sensorAdapter;
import com.conary.ipin7.screen_main.MainActivity;
import com.conary.ipin7.usbModel.UsbModelImpl;
import com.conary.ipin7.utils.DeviceData;
import com.conary.ipin7.view.ScreenScale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MenuSensor extends Activity implements View.OnClickListener,UsbModelImpl.UsbView
{
    private AnimationDrawable animGuide;
    private ImageView ImgGuide;
    private ImageView BtnOk,BtnReturn, BtnRing;
    private TextView BtnOn,BtnOff;
    private ImageView BtnGuBack;

    private RelativeLayout guideLayout, displayLayout;
    private LinearLayout BtnDel;
    private ListView listView;
    private sensorAdapter listAdapter;
    private List<ListSensor> sensorList= new ArrayList<>();
    SimpleDateFormat TimerDateFormat = new SimpleDateFormat("YYYY/MM/dd hh:mm:ss");
    private UsbModelImpl mUsb = MainApplication.getInstance().getUsbImp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scree_sensor);

        ScreenScale.initial(this);
        ScreenScale.changeAllViewSize(findViewById(R.id.screen_sensor));
        initView();
        mUsb.USB_ViewInit(this);
    }

    protected void onResume()
    {
        super.onResume();
        guideLayout.setVisibility(View.VISIBLE);
        displayLayout.setVisibility(View.INVISIBLE);
    }

    private void initView()
    {
        BtnOk = findViewById(R.id.sen_BtnNext);
        BtnOk.setOnClickListener(this);

        ImgGuide = findViewById(R.id.sen_ImgGuid);
        animGuide = (AnimationDrawable) ImgGuide.getBackground();
        animGuide.start();

        guideLayout = findViewById(R.id.sen_guideLayout);
        displayLayout = findViewById(R.id.sen_displayLayout);

        BtnReturn = findViewById(R.id.sen_BtnReturn);
        BtnReturn.setOnClickListener(this);

        BtnRing = findViewById(R.id.sen_btnRing);
        BtnRing.setOnClickListener(this);

        BtnOn = findViewById(R.id.sen_BtnOn);
        BtnOn.setOnClickListener(this);
        BtnOff = findViewById(R.id.sen_BtnOff);
        BtnOff.setOnClickListener(this);

        BtnDel = findViewById(R.id.sen_btnDel);
        BtnDel.setOnClickListener(this);

        BtnGuBack = findViewById(R.id.sen_BtnGuBack);
        BtnGuBack.setOnClickListener(this);

        listAdapter = new sensorAdapter(this,sensorList);
        listView = findViewById(R.id.sen_listView);
        listView.setAdapter(listAdapter);

/*        for(int i = 0; i < 10; i++)
        {
            ListSensor sensor = new ListSensor("12:" + i + "0","test" + i);
            sensorList.add(sensor);
        }*/
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.sen_BtnGuBack:
                Intent intent = null;
                intent  = new Intent(this, MainActivity.class);
                this.startActivity(intent);
            break;
            case R.id.sen_BtnNext:
                displayLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.sen_BtnReturn:
                if(displayLayout.getVisibility() == View.VISIBLE)
                {
                    displayLayout.setVisibility(View.INVISIBLE);
                    guideLayout.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.sen_btnRing:
                if(BtnRing.isSelected())
                {
                    BtnRing.setSelected(false);
                    BtnOn.setSelected(false);
                    BtnOff.setSelected(false);
                    stopDetect();
                }
                else
                {
                    BtnRing.setSelected(true);
                    BtnOn.setSelected(true);
                    BtnOff.setSelected(true);
                    startDetect();
                }
            break;
            case R.id.sen_BtnOn:
                BtnRing.setSelected(true);
                BtnOn.setSelected(true);
                BtnOff.setSelected(true);
                startDetect();
//                AlarmDetect();
                break;
            case R.id.sen_BtnOff:
                BtnRing.setSelected(false);
                BtnOn.setSelected(false);
                BtnOff.setSelected(false);
//                CancleAlarmDetect();
                stopDetect();
                break;
            case R.id.sen_btnDel:
                // Clean log
                BtnOff.performClick();
                sensorList.removeAll(sensorList);
                listAdapter.notifyDataSetChanged();
                break;

        }
    }

    void CancleAlarmDetect()
    {
        Date currentTime = Calendar.getInstance().getTime();
        String strTime = TimerDateFormat.format(currentTime);
        ListSensor list  = new ListSensor(false,strTime,getString(R.string.sen_alarm_cancel));
        sensorList.add(list);
        listAdapter.notifyDataSetChanged();
    }

    void AlarmDetect()
    {
        Date currentTime = Calendar.getInstance().getTime();
        String strTime = TimerDateFormat.format(currentTime);
        ListSensor list  = new ListSensor(true,strTime,getString(R.string.sen_alarm_detect));
        sensorList.add(list);
        listAdapter.notifyDataSetChanged();
    }

    void startDetect()
    {
        Date currentTime = Calendar.getInstance().getTime();
        String strTime = TimerDateFormat.format(currentTime);
        ListSensor list  = new ListSensor(false,strTime,getString(R.string.sen_start_detect));
        sensorList.add(list);
        listAdapter.notifyDataSetChanged();
    }

    void stopDetect()
    {
        Date currentTime = Calendar.getInstance().getTime();
        String strTime = TimerDateFormat.format(currentTime);
        ListSensor list  = new ListSensor(false,strTime,getString(R.string.sen_end_detect));
        sensorList.add(list);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void UsbDebugLog(String str) {

    }

    @Override
    public void USB_UI_Viwe(int data,Object obj) {
        switch(data)
        {
            case DeviceData.DEVICE_UPDATE_DATA:
                break;
        }
    }
}
