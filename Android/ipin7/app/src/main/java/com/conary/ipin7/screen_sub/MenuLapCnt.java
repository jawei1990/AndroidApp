package com.conary.ipin7.screen_sub;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conary.ipin7.R;
import com.conary.ipin7.utils.DataLog;
import com.conary.ipin7.view.ScreenScale;

public class MenuLapCnt extends Activity implements View.OnClickListener
{
    private AnimationDrawable animGuide;
    private ImageView ImgGuide;
    private ImageView BtnOk,BtnReturn, BtnRing;
    private TextView BtnOn,BtnOff;

    private LinearLayout BtnDel;
    private LinearLayout keyboardLayout;
    private Button Btn1,Btn2,Btn3,Btn4,Btn5,Btn6,Btn7,Btn8,Btn9,Btn0,BtnBack;
    private RelativeLayout BtnKeyDel,BtnFinish;
    private TextView tv_dis,tv_show_dis;

    private RelativeLayout guideLayout, displayLayout;
    private final int MaxInputLen = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scree_count);
        ScreenScale.initial(this);
        ScreenScale.changeAllViewSize(findViewById(R.id.screen_count));

        initView();
    }

    protected void onResume()
    {
        super.onResume();
        guideLayout.setVisibility(View.VISIBLE);
        displayLayout.setVisibility(View.INVISIBLE);
        keyboardLayout.setVisibility(View.INVISIBLE);
    }

    private void initView()
    {
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
        tv_show_dis = findViewById(R.id.tv_show_dis);

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
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.cnt_BtnNext:
                displayLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.cnt_BtnReturn:
                if(displayLayout.getVisibility() == View.VISIBLE)
                {
                    displayLayout.setVisibility(View.INVISIBLE);
                    guideLayout.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.cnt_btnRing:
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
            case R.id.cnt_BtnOn:
                BtnRing.setSelected(true);
                BtnOn.setSelected(true);
                BtnOff.setSelected(true);
                break;
            case R.id.cnt_BtnOff:
                BtnRing.setSelected(false);
                BtnOn.setSelected(false);
                BtnOff.setSelected(false);
                break;
            case R.id.cnt_btnDel:
                // Do Reset and clean log
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
                len = str.length()-1;

                if(len < 0) break;
                str = str.substring(0,len);
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
                Button btn = (Button)v;
                String str = "";
                int len = 0;
                str = tv_dis.getText().toString();
                len = str.length();
                if(len > MaxInputLen) break;

                str += btn.getText().toString();
                tv_dis.setText(str);
            }
            break;
        }
    }
}
