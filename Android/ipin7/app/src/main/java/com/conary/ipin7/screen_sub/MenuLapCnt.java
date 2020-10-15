package com.conary.ipin7.screen_sub;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.conary.ipin7.MainApplication;
import com.conary.ipin7.R;
import com.conary.ipin7.adapter.ListRaceCnt;
import com.conary.ipin7.adapter.RaceCntAdapter;
import com.conary.ipin7.screen_main.MainActivity;
import com.conary.ipin7.usbModel.UsbModelImpl;
import com.conary.ipin7.utils.DataBase;
import com.conary.ipin7.utils.DataLog;
import com.conary.ipin7.utils.UserPreferences;
import com.conary.ipin7.utils.UtilConst;
import com.conary.ipin7.view.ScreenScale;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MenuLapCnt extends Activity implements View.OnClickListener,UsbModelImpl.UsbView
{
    private AnimationDrawable animGuide;
    private ImageView ImgGuide;
    private ImageView BtnOk, BtnReturn, BtnRing;
    private TextView BtnOn, BtnOff;
    private ImageView BtnGuBack;

    private LinearLayout BtnDel;
    private LinearLayout keyboardLayout;
    private Button Btn1, Btn2, Btn3, Btn4, Btn5, Btn6, Btn7, Btn8, Btn9, Btn0, BtnBack;
    private RelativeLayout BtnKeyDel, BtnFinish;
    private TextView tv_dis, tv_time;

    private RelativeLayout guideLayout, displayLayout;
    private final int MaxInputLen = 4;
    private final String DATABASE_TABLE = "LapCnt";

    private ListView listView;
    private List<ListRaceCnt> cntList = new ArrayList<>();
    private RaceCntAdapter listAdapter;
    private UserPreferences mUserPreferences;
    private UsbModelImpl mUsb = MainApplication.getInstance().getUsbImp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scree_count);
        ScreenScale.initial(this);
        ScreenScale.changeAllViewSize(findViewById(R.id.screen_count));

        initView();
        mUsb.USB_ViewInit(this);
    }

    protected void onResume() {
        super.onResume();
        guideLayout.setVisibility(View.VISIBLE);
        displayLayout.setVisibility(View.INVISIBLE);
        keyboardLayout.setVisibility(View.INVISIBLE);
    }

    private void initView() {
        BtnOk = findViewById(R.id.cnt_BtnNext);
        BtnOk.setOnClickListener(this);

        ImgGuide = findViewById(R.id.cnt_ImgGuid);
        animGuide = (AnimationDrawable) ImgGuide.getBackground();
        animGuide.start();

        guideLayout = findViewById(R.id.cnt_guideLayout);
        displayLayout = findViewById(R.id.cnt_displayLayout);

        BtnReturn = findViewById(R.id.cnt_BtnReturn);
        BtnReturn.setOnClickListener(this);

        BtnRing = findViewById(R.id.cnt_btnRing);
        BtnRing.setOnClickListener(this);

        BtnOn = findViewById(R.id.cnt_BtnOn);
        BtnOn.setOnClickListener(this);
        BtnOff = findViewById(R.id.cnt_BtnOff);
        BtnOff.setOnClickListener(this);

        BtnDel = findViewById(R.id.cnt_btnDel);
        BtnDel.setOnClickListener(this);

        keyboardLayout = findViewById(R.id.cnt_keyboardLayout);
        tv_dis = findViewById(R.id.tv_cnt_dis);
        tv_dis.setOnClickListener(this);
        tv_time = findViewById(R.id.tv_cnt_time);
        tv_time.setOnClickListener(this); //TODO: Enable for test list data

        Btn0 = findViewById(R.id.cnt_Btn0);
        Btn1 = findViewById(R.id.cnt_Btn1);
        Btn2 = findViewById(R.id.cnt_Btn2);
        Btn3 = findViewById(R.id.cnt_Btn3);
        Btn4 = findViewById(R.id.cnt_Btn4);
        Btn5 = findViewById(R.id.cnt_Btn5);
        Btn6 = findViewById(R.id.cnt_Btn6);
        Btn7 = findViewById(R.id.cnt_Btn7);
        Btn8 = findViewById(R.id.cnt_Btn8);
        Btn9 = findViewById(R.id.cnt_Btn9);
        BtnBack = findViewById(R.id.cnt_BtnBack);
        BtnKeyDel = findViewById(R.id.cnt_BtnDel);
        BtnFinish = findViewById(R.id.cnt_BtnFinish);

        Btn0.setOnClickListener(this);
        Btn1.setOnClickListener(this);
        Btn2.setOnClickListener(this);
        Btn3.setOnClickListener(this);
        Btn4.setOnClickListener(this);
        Btn5.setOnClickListener(this);
        Btn6.setOnClickListener(this);
        Btn7.setOnClickListener(this);
        Btn8.setOnClickListener(this);
        Btn9.setOnClickListener(this);
        BtnBack.setOnClickListener(this);
        BtnKeyDel.setOnClickListener(this);
        BtnFinish.setOnClickListener(this);

        BtnGuBack = findViewById(R.id.cnt_BtnGuBack);
        BtnGuBack.setOnClickListener(this);

        listAdapter = new RaceCntAdapter(this, cntList);
        listView = findViewById(R.id.cnt_listView);
        listView.setAdapter(listAdapter);

/*        for(int i = 0; i < 10; i++)
        {
            ListRaceCnt list  = new ListRaceCnt("12:" + i + "0","test" + i);
            cntList.add(list);
        }*/

        mUserPreferences = MainApplication.getInstance().getUserPreferences();
        tv_time.setText(mUserPreferences.get_CntCurrentTime());
        tv_dis.setText(mUserPreferences.get_CntDisData());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.cnt_BtnGuBack:
                Intent intent = null;
                intent  = new Intent(this, MainActivity.class);
                this.startActivity(intent);
            break;
            case R.id.tv_cnt_time:
            {
                //For testing
                Message msg = new Message();
                msg.what = UtilConst.CNT_UPDATE_DIS;
                handler.sendMessage(msg);
            }
            break;
            case R.id.cnt_BtnNext:
                displayLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.cnt_BtnReturn:
                if (displayLayout.getVisibility() == View.VISIBLE)
                {
                    displayLayout.setVisibility(View.INVISIBLE);
                    guideLayout.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.cnt_btnRing:
                if (BtnRing.isSelected())
                {
                    BtnRing.setSelected(false);
                    BtnOn.setSelected(false);
                    BtnOff.setSelected(false);
                    stopCnt();
                }
                else
                {
                    BtnRing.setSelected(true);
                    BtnOn.setSelected(true);
                    BtnOff.setSelected(true);
                    startCnt();
                }
                break;
            case R.id.cnt_BtnOn:
                BtnRing.setSelected(true);
                BtnOn.setSelected(true);
                BtnOff.setSelected(true);
                startCnt();
                break;
            case R.id.cnt_BtnOff:
                BtnRing.setSelected(false);
                BtnOn.setSelected(false);
                BtnOff.setSelected(false);
                stopCnt();
                break;
            case R.id.cnt_btnDel:
                // Do Reset and clean log
                tv_time.setText("00:00:000");
                startTime = 0;
                setDisplayTimer(false,0);
                BtnOff.performClick();

                cntList.removeAll(cntList);
                listAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_cnt_dis:
                keyboardLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.cnt_BtnFinish:
                keyboardLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.cnt_BtnDel:
            {
                String str = "";
                int len = 0;

                str = tv_dis.getText().toString();
                len = str.length() - 1;

                if (len < 0) break;
                str = str.substring(0, len);
                tv_dis.setText(str);
            }
            break;
            case R.id.cnt_BtnBack:
            {
                keyboardLayout.setVisibility(View.INVISIBLE);
                tv_dis.setText("");
            }
            break;
            case R.id.cnt_Btn0:
            case R.id.cnt_Btn1:
            case R.id.cnt_Btn2:
            case R.id.cnt_Btn3:
            case R.id.cnt_Btn4:
            case R.id.cnt_Btn5:
            case R.id.cnt_Btn6:
            case R.id.cnt_Btn7:
            case R.id.cnt_Btn8:
            case R.id.cnt_Btn9:
            {
                Button btn = (Button) v;
                String str = "";
                int len = 0;
                str = tv_dis.getText().toString();
                len = str.length();
                if (len > MaxInputLen) break;

                str += btn.getText().toString();
                tv_dis.setText(str);
            }
            break;
        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what)
            {
                case UtilConst.CNT_UPDATE_DIS:
                {
                    // For test input data
                    try
                    {
                        String StrTime = tv_time.getText().toString();
                        String[] time = StrTime.split(":");
                        double dis = Double.valueOf(tv_dis.getText().toString());
                        double sec = 0;

                        sec =  Double.valueOf(time[0]) * 60+  Double.valueOf(time[1]) +  Double.valueOf(time[2]) *  0.001;
                        double speed = dis / sec;
                        String StrSpeed = String.format("%.2f",speed);
                        ListRaceCnt list  = new ListRaceCnt(StrTime,StrSpeed);
                        cntList.add(list);
                        listAdapter.notifyDataSetChanged();
                        listView.setSelection(ListView.FOCUS_DOWN);

                        DataLog.LapCnt(listAdapter.getCount()+":time:" + StrTime + ",speed:" + StrSpeed);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();

                        String StrTime = tv_time.getText().toString();
                        ListRaceCnt list  = new ListRaceCnt(StrTime,"Err Speed");
                        cntList.add(list);
                        listAdapter.notifyDataSetChanged();
                        listView.setSelection(ListView.FOCUS_DOWN);
                    }
                }
                break;
            }
        }
    };

    private void getData()
    {
        Cursor mCursor = DataBase.selDataBase(DATABASE_TABLE);
        Log.e("Awei","id:"+ mCursor.getCount());

        for(int i = 0; i < mCursor.getCount()-1 ; i++)
        {
            Log.e("Awei","---------");
            mCursor.moveToPosition(i);
            int idx = Integer.valueOf(mCursor.getString(mCursor.getColumnIndex("_idx")));
            String date = mCursor.getString(mCursor.getColumnIndex("_date"));
            double speed = Double.valueOf(mCursor.getString(mCursor.getColumnIndex("_speed")));
            Log.e("Awei","idx:" + idx + ",date:" + date + ",speed:" + speed);
        }

    }

    long startTime;
    void startCnt()
    {
        tv_time.setSelected(true);
        startTime =  System.currentTimeMillis();
        setDisplayTimer(true,100);
    }

    void stopCnt()
    {
        tv_time.setSelected(false);
        setDisplayTimer(false,0);
    }

    SimpleDateFormat TimerDateFormat = new SimpleDateFormat("mm:ss:SSS");
    private Task_DisplayTimer DisplayTimer = new Task_DisplayTimer();
    private Timer t_DisplayTimer = new Timer();
    private class Task_DisplayTimer extends TimerTask
    {
        public void run()
        {
            long difTime = System.currentTimeMillis() - startTime;
            String strTime = TimerDateFormat.format(difTime);
            tv_time.setText(strTime);

            if(BtnRing.isSelected())
                setDisplayTimer(true, 100);
        }
    }
    private void setDisplayTimer(boolean isActivity, int timer)
    {
        try
        {
            t_DisplayTimer.cancel();
            DisplayTimer = new Task_DisplayTimer();
            t_DisplayTimer.purge();
            t_DisplayTimer.cancel();
            t_DisplayTimer = new Timer();

            if(isActivity)
            {
                t_DisplayTimer.schedule(DisplayTimer,timer);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void UsbDebugLog(String str) {

    }

    @Override
    public void USB_UI_Viwe(int data, Object obj) {

    }
}
