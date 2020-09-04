package com.conary.ipin7.screen_sub;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.sax.RootElement;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.conary.ipin7.R;
import com.conary.ipin7.utils.DataLog;
import com.conary.ipin7.view.ScreenScale;

import org.w3c.dom.Text;

public class MenuRadar extends Activity implements View.OnClickListener,View.OnTouchListener,SeekBar.OnSeekBarChangeListener
{
    ImageView BtnOK, BtnNext;
    TextView ed_near, ed_mid, ed_far;
    ImageView BtnNearRing, BtnMidRing, BtnFarRing;
    TextView BtnOn, BtnOff,tv_Dis;
    ImageView BtnRingCtl,BtnReturn,BtnRingSlider;
    SeekBar RingBar;

    RelativeLayout guideLayout,settingLayout,DetectLayout;
    LinearLayout keyboardLayout,BtnStartRing;
    Button Btn1,Btn2,Btn3,Btn4,Btn5,Btn6,Btn7,Btn8,Btn9,Btn0,BtnBack;
    RelativeLayout BtnDel,BtnFinish;

    private final int MaxInputLen = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scree_radar);
        DataLog.e(" MenuRadar 2 ");
        ScreenScale.initial(this);
        ScreenScale.changeAllViewSize(findViewById(R.id.screen_radar));

        initView();
    }

    protected void onResume()
    {
        super.onResume();
        guideLayout.setVisibility(View.VISIBLE);
        settingLayout.setVisibility(View.INVISIBLE);
        DetectLayout.setVisibility(View.INVISIBLE);
        keyboardLayout.setVisibility(View.INVISIBLE);
    }

    private void initView()
    {
        BtnNext = findViewById(R.id.BtnNext);
        BtnNext.setOnClickListener(this);

        BtnNearRing = findViewById(R.id.near_ring);
        BtnNearRing.setOnClickListener(this);
        BtnMidRing = findViewById(R.id.mid_ring);
        BtnMidRing.setOnClickListener(this);
        BtnFarRing = findViewById(R.id.far_ring);
        BtnFarRing.setOnClickListener(this);

        ed_near = findViewById(R.id.ed_near);
        ed_near.setOnTouchListener(this);
        ed_mid = findViewById(R.id.ed_mid);
        ed_mid.setOnTouchListener(this);
        ed_far = findViewById(R.id.ed_far);
        ed_far.setOnTouchListener(this);

        BtnStartRing = findViewById(R.id.btnStartRing);
        BtnStartRing.setOnClickListener(this);

        BtnRingCtl = findViewById(R.id.btnRing);
        BtnRingCtl.setOnClickListener(this);

        BtnOn = findViewById(R.id.BtnOn);
        BtnOn.setOnClickListener(this);
        BtnOff = findViewById(R.id.BtnOff);
        BtnOff.setOnClickListener(this);

        tv_Dis = findViewById(R.id.tv_show_dis);
        RingBar = findViewById(R.id.ringBar);
        RingBar.setOnSeekBarChangeListener(this);
        BtnRingSlider = findViewById(R.id.btnRingSlider);
        BtnRingSlider.setOnClickListener(this);

        BtnReturn = findViewById(R.id.BtnReturn);
        BtnReturn.setOnClickListener(this);

        guideLayout = findViewById(R.id.guideLayout);
        settingLayout = findViewById(R.id.settingLayout);
        keyboardLayout = findViewById(R.id.keyboardLayout);
        DetectLayout = findViewById(R.id.DetectLayout);

        Btn0 = findViewById(R.id.Btn0);
        Btn1 = findViewById(R.id.Btn1);
        Btn2 = findViewById(R.id.Btn2);
        Btn3 = findViewById(R.id.Btn3);
        Btn4 = findViewById(R.id.Btn4);
        Btn5 = findViewById(R.id.Btn5);
        Btn6 = findViewById(R.id.Btn6);
        Btn7 = findViewById(R.id.Btn7);
        Btn8 = findViewById(R.id.Btn8);
        Btn9 = findViewById(R.id.Btn9);
        BtnBack = findViewById(R.id.BtnBack);
        BtnDel = findViewById(R.id.BtnDel);
        BtnFinish = findViewById(R.id.BtnFinish);

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
        BtnDel.setOnClickListener(this);
        BtnFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnRingSlider:
                if(BtnRingSlider.isSelected())
                {
                    BtnRingSlider.setSelected(false);
                    RingBar.setEnabled(true);
                }
                else
                {
                    BtnRingSlider.setSelected(true);
                    RingBar.setEnabled(false);
                }
                break;
            case R.id.btnStartRing:
                DetectLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.BtnNext:
                guideLayout.setVisibility(View.INVISIBLE);
                settingLayout.setVisibility(View.VISIBLE);
                BtnReturn.setVisibility(View.VISIBLE);
                break;
            case R.id.near_ring:
                if(BtnNearRing.isSelected())
                    BtnNearRing.setSelected(false);
                else
                    BtnNearRing.setSelected(true);
                break;
            case R.id.mid_ring:
                if(BtnMidRing.isSelected())
                    BtnMidRing.setSelected(false);
                else
                    BtnMidRing.setSelected(true);
                break;
            case R.id.far_ring:
                if(BtnFarRing.isSelected())
                    BtnFarRing.setSelected(false);
                else
                    BtnFarRing.setSelected(true);
                break;
            case R.id.btnRing:
                if(BtnRingCtl.isSelected())
                {
                    BtnRingCtl.setSelected(false);
                    BtnOn.setSelected(false);
                    BtnOff.setSelected(false);
                }
                else
                {
                    BtnRingCtl.setSelected(true);
                    BtnOn.setSelected(true);
                    BtnOff.setSelected(true);
                }
                break;
            case R.id.BtnReturn:
                if(DetectLayout.getVisibility() == View.VISIBLE)
                {
                    DetectLayout.setVisibility(View.INVISIBLE);
                    settingLayout.setVisibility(View.VISIBLE);
                }
                else if(settingLayout.getVisibility() == View.VISIBLE)
                {
                    settingLayout.setVisibility(View.INVISIBLE);
                    guideLayout.setVisibility(View.VISIBLE);
                    BtnReturn.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.BtnOn:
                BtnRingCtl.setSelected(true);
                BtnOn.setSelected(true);
                BtnOff.setSelected(true);
                break;
            case R.id.BtnOff:
                BtnRingCtl.setSelected(false);
                BtnOn.setSelected(false);
                BtnOff.setSelected(false);
                break;
            case R.id.BtnBack:
                keyboardLayout.setVisibility(View.INVISIBLE);
                BtnReturn.setVisibility(View.VISIBLE);
                if(touchIdx == 0)
                    ed_near.setText("");
                else if(touchIdx == 1)
                    ed_mid.setText("");
                else if(touchIdx == 2)
                    ed_far.setText("");
                break;
            case R.id.BtnFinish:
                keyboardLayout.setVisibility(View.INVISIBLE);
                BtnReturn.setVisibility(View.VISIBLE);
                break;
            case R.id.BtnDel:
            {
                String str = "";
                int len = 0;
                if(touchIdx == 0)
                {
                    str = ed_near.getText().toString();
                    len = str.length()-1;

                    if(len < 0) break;
                    str = str.substring(0,len);
                    ed_near.setText(str);
                }
                else if(touchIdx == 1)
                {
                    str = ed_mid.getText().toString();
                    len = str.length()-1;

                    if(len < 0) break;
                    str = str.substring(0,len);
                    ed_mid.setText(str);
                }
                else if(touchIdx == 2)
                {
                    str = ed_far.getText().toString();
                    len = str.length()-1;

                    if(len < 0) break;
                    str = str.substring(0,len);
                    ed_far.setText(str);
                }
            }
            break;
            case R.id.Btn0:
            case R.id.Btn1:
            case R.id.Btn2:
            case R.id.Btn3:
            case R.id.Btn4:
            case R.id.Btn5:
            case R.id.Btn6:
            case R.id.Btn7:
            case R.id.Btn8:
            case R.id.Btn9:
            {
                Button btn = (Button)v;
                String str = "";
                int len = 0;
                if(touchIdx == 0)
                {
                    str = ed_near.getText().toString();
                    len = str.length();
                    if(len > MaxInputLen) break;

                    str += btn.getText().toString();
                    ed_near.setText(str);
                }
                else if(touchIdx == 1)
                {
                    str = ed_mid.getText().toString();

                    len = str.length();
                    if(len > MaxInputLen) break;

                    str += btn.getText().toString();
                    ed_mid.setText(str);
                }
                else if(touchIdx == 2)
                {
                    str = ed_far.getText().toString();

                    len = str.length();
                    if(len > MaxInputLen) break;

                    str += btn.getText().toString();
                    ed_far.setText(str);
                }
            }
            break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private int touchIdx = 0; // 0: near, 1: mid, 2: far
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        keyboardLayout.setVisibility(View.VISIBLE);
        BtnReturn.setVisibility(View.INVISIBLE);
        switch (v.getId())
        {
            case R.id.ed_near:
                touchIdx = 0;
                keyboardLayout.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                BtnFinish.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                BtnDel.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                BtnBack.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                Btn0.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                Btn1.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                Btn2.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                Btn3.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                Btn4.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                Btn5.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                Btn6.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                Btn7.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                Btn8.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                Btn9.setBackground(getDrawable(R.drawable.xml_btn_outline_red));
                break;
            case R.id.ed_mid:
                touchIdx = 1;
                keyboardLayout.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                BtnFinish.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                BtnDel.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                BtnBack.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                Btn0.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                Btn1.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                Btn2.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                Btn3.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                Btn4.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                Btn5.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                Btn6.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                Btn7.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                Btn8.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                Btn9.setBackground(getDrawable(R.drawable.xml_btn_outline_yellow));
                break;
            case R.id.ed_far:
                touchIdx = 2;
                keyboardLayout.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                BtnFinish.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                BtnDel.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                BtnBack.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                Btn0.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                Btn1.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                Btn2.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                Btn3.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                Btn4.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                Btn5.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                Btn6.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                Btn7.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                Btn8.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                Btn9.setBackground(getDrawable(R.drawable.xml_btn_outline_green));
                break;
        }

        return false;
    }
}
