package com.conary.ipin7.screen_sub;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conary.ipin7.MainApplication;
import com.conary.ipin7.R;
import com.conary.ipin7.screen_main.MainActivity;
import com.conary.ipin7.usbModel.UsbModelImpl;
import com.conary.ipin7.utils.DataLog;
import com.conary.ipin7.view.ScreenScale;

public class MenuBike extends Activity implements View.OnClickListener,UsbModelImpl.UsbView
{
    private AnimationDrawable animGuide;
    private ImageView ImgGuide;
    private ImageView BtnOk,BtnReturn, BtnRing;
    private ImageView BtnGuBack;
    private TextView BtnOn,BtnOff;

    private RelativeLayout guideLayout,displayLayout,graphicLayout;
    private LinearLayout viewDataLayout,BtnDel;
    private UsbModelImpl mUsb = MainApplication.getInstance().getUsbImp();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scree_bike);
        ScreenScale.initial(this);
        ScreenScale.changeAllViewSize(findViewById(R.id.screen_bike));
        DataLog.e(" MenuSensor 1 ");

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
        BtnOk = findViewById(R.id.bike_BtnNext);
        BtnOk.setOnClickListener(this);

        ImgGuide = findViewById(R.id.bike_ImgGuid);
        animGuide = (AnimationDrawable) ImgGuide.getBackground();
        animGuide.start();

        guideLayout = findViewById(R.id.bike_guideLayout);
        displayLayout = findViewById(R.id.bike_displayLayout);

        BtnRing = findViewById(R.id.bike_btnRing);
        BtnRing.setOnClickListener(this);

        BtnOn = findViewById(R.id.bike_BtnOn);
        BtnOn.setOnClickListener(this);
        BtnOff = findViewById(R.id.bike_BtnOff);
        BtnOff.setOnClickListener(this);

        BtnReturn = findViewById(R.id.bike_BtnReturn);
        BtnReturn.setOnClickListener(this);

        viewDataLayout = findViewById(R.id.bike_viewData);
        viewDataLayout.setOnClickListener(this);

        graphicLayout = findViewById(R.id.bike_viewGraphic);
        graphicLayout.setOnClickListener(this);

        BtnDel = findViewById(R.id.bike_btnDel);
        BtnDel.setOnClickListener(this);

        BtnGuBack = findViewById(R.id.bike_BtnGuBack);
        BtnGuBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bike_BtnGuBack:
                Intent intent = null;
                intent  = new Intent(this, MainActivity.class);
                this.startActivity(intent);
            break;
            case R.id.bike_viewData:
            case R.id.bike_viewGraphic:
                if(viewDataLayout.getVisibility() == View.VISIBLE)
                {
                    viewDataLayout.setVisibility(View.INVISIBLE);
                    graphicLayout.setVisibility(View.VISIBLE);
                }
                else if(graphicLayout.getVisibility() == View.VISIBLE)
                {
                    viewDataLayout.setVisibility(View.VISIBLE);
                    graphicLayout.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.bike_BtnNext:
                guideLayout.setVisibility(View.INVISIBLE);
                displayLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.bike_BtnReturn:
                guideLayout.setVisibility(View.VISIBLE);
                displayLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.bike_btnRing:
                if(BtnRing.isSelected())
                {
                    BtnRing.setSelected(false);
                    BtnOn.setSelected(false);
                    BtnOff.setSelected(false);
                }
                else
                {
                    BtnRing.setSelected(true);
                    BtnOn.setSelected(true);
                    BtnOff.setSelected(true);
                }
                break;
            case R.id.bike_BtnOn:
                BtnRing.setSelected(true);
                BtnOn.setSelected(true);
                BtnOff.setSelected(true);
                break;
            case R.id.bike_BtnOff:
                BtnRing.setSelected(false);
                BtnOn.setSelected(false);
                BtnOff.setSelected(false);
                break;
            case R.id.bike_btnDel:
                // Reset screen
                break;
        }
    }

    @Override
    public void UsbDebugLog(String str) {

    }

    @Override
    public void USB_UI_Viwe(int data, Object obj) {

    }

}
