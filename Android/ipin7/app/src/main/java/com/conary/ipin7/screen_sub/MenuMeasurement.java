package com.conary.ipin7.screen_sub;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conary.ipin7.MainApplication;
import com.conary.ipin7.R;
import com.conary.ipin7.adapter.ListMeasure;
import com.conary.ipin7.adapter.measureAdapter;
import com.conary.ipin7.usbModel.UsbModelImpl;
import com.conary.ipin7.utils.DeviceData;
import com.conary.ipin7.view.ScreenScale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MenuMeasurement extends Activity implements View.OnClickListener, UsbModelImpl.UsbView
{
    private ImageView BtnReturn, BtnRing;
    private TextView BtnOn,BtnOff;

    private RelativeLayout  displayLayout;
    private LinearLayout BtnDel;
    private ListView listView;
    private measureAdapter listAdapter;
    private List<ListMeasure> measureList = new ArrayList<>();
    SimpleDateFormat TimerDateFormat = new SimpleDateFormat("YYYY/MM/dd hh:mm:ss");

    private UsbModelImpl mUsb = MainApplication.getInstance().getUsbImp();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scree_measure);
        ScreenScale.initial(this);
        ScreenScale.changeAllViewSize(findViewById(R.id.screen_measure));
        initView();
        mUsb.USB_ViewInit(this);
    }

    protected void onResume()
    {
        super.onResume();
        displayLayout.setVisibility(View.VISIBLE);
    }

    private void initView()
    {
        displayLayout = findViewById(R.id.mea_displayLayout);

        BtnReturn = findViewById(R.id.mea_BtnReturn);
        BtnReturn.setOnClickListener(this);

        BtnRing = findViewById(R.id.mea_btnRing);
        BtnRing.setOnClickListener(this);

        BtnOn = findViewById(R.id.mea_BtnOn);
        BtnOn.setOnClickListener(this);
        BtnOff = findViewById(R.id.mea_BtnOff);
        BtnOff.setOnClickListener(this);

        BtnDel = findViewById(R.id.mea_btnDel);
        BtnDel.setOnClickListener(this);

        listAdapter = new measureAdapter(this,measureList);
        listView = findViewById(R.id.mea_listView);
        listView.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.mea_btnRing:
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
            case R.id.mea_BtnOn:
                BtnRing.setSelected(true);
                BtnOn.setSelected(true);
                BtnOff.setSelected(true);
                break;
            case R.id.mea_BtnOff:
                BtnRing.setSelected(false);
                BtnOn.setSelected(false);
                BtnOff.setSelected(false);
                stopDetect();
                break;
            case R.id.mea_btnDel:
                // Clean log
                BtnOff.performClick();
                measureList.removeAll(measureList);
                listAdapter.notifyDataSetChanged();
                break;
        }
    }


    void CancleAlarmDetect()
    {
        Date currentTime = Calendar.getInstance().getTime();
        String strTime = TimerDateFormat.format(currentTime);
        ListMeasure list  = new ListMeasure(strTime,getString(R.string.sen_alarm_cancel));
        measureList.add(list);
        listAdapter.notifyDataSetChanged();
    }

    void updateDistance(double dis)
    {
        String strDis = String.valueOf(dis);
        Date currentTime = Calendar.getInstance().getTime();
        String strTime = TimerDateFormat.format(currentTime);
        ListMeasure list  = new ListMeasure(strTime,strDis);
        measureList.add(list);
        listAdapter.notifyDataSetChanged();

        Log.e("Awei","Dis:" + strDis);
    }

    void startDetect()
    {
        mUsb.USB_ContinuousMeasure();
        Date currentTime = Calendar.getInstance().getTime();
        String strTime = TimerDateFormat.format(currentTime);
        ListMeasure list  = new ListMeasure(strTime,"開始測量");
        measureList.add(list);
        listAdapter.notifyDataSetChanged();
    }

    void stopDetect()
    {
        mUsb.USB_StopMeasure();
        Date currentTime = Calendar.getInstance().getTime();
        String strTime = TimerDateFormat.format(currentTime);
        ListMeasure list  = new ListMeasure(strTime,"關閉測量");
        measureList.add(list);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void UsbDebugLog(String str) {

    }

    @Override
    public void USB_UI_Viwe(int data, Object obj) {
        switch(data)
        {
            case DeviceData.DEVICE_UPDATE_DATA:
                double StrData = (double) obj;
                updateDistance((double) obj);
                break;
        }
    }
}
