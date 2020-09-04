package com.conary.ipin7.screen_sub;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.conary.ipin7.R;
import com.conary.ipin7.utils.DataLog;
import com.conary.ipin7.view.ScreenScale;

public class MenuSensor extends Activity implements View.OnClickListener
{
    private AnimationDrawable animGuide;
    private ImageView ImgGuide;
    private ImageView BtnOk,BtnReturn, BtnRing;
    private TextView BtnOn,BtnOff;

    private RelativeLayout guideLayout, displayLayout;
    private LinearLayout BtnDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scree_sensor);

        ScreenScale.initial(this);
        ScreenScale.changeAllViewSize(findViewById(R.id.screen_sensor));
        DataLog.e(" MenuSensor 1 ");

        initView();
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
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
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
                }
                else
                {
                    BtnRing.setSelected(true);
                    BtnOn.setSelected(true);
                    BtnOff.setSelected(true);
                }
            break;
            case R.id.sen_BtnOn:
                BtnRing.setSelected(true);
                BtnOn.setSelected(true);
                BtnOff.setSelected(true);
                break;
            case R.id.sen_BtnOff:
                BtnRing.setSelected(false);
                BtnOn.setSelected(false);
                BtnOff.setSelected(false);
                break;
            case R.id.sen_btnDel:
                // Clean log
                break;

        }
    }
}
