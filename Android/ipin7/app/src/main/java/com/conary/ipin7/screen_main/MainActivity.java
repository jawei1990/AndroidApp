package com.conary.ipin7.screen_main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.conary.ipin7.MainApplication;
import com.conary.ipin7.R;
import com.conary.ipin7.screen_sub.MenuBike;
import com.conary.ipin7.screen_sub.MenuLapCnt;
import com.conary.ipin7.screen_sub.MenuMeasurement;
import com.conary.ipin7.screen_sub.MenuRadar;
import com.conary.ipin7.screen_sub.MenuSensor;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private ViewPager viewPager;
    private View view1,view2,view3,view4,view5;
    private ArrayList<View> pageview;
    private ImageView[] tips = new ImageView[5];
    private ImageView imageView;
    private ViewGroup group;

    private ImageView imgLadar,ImgCnt,ImgSensor,ImgMea,ImgBike;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.screen_main);

        MainApplication.getInstance().getUsbImp().StartUsbConnection();

        mContext = this;

        viewPager = (ViewPager)findViewById(R.id.viewPage);
        view1 = getLayoutInflater().inflate(R.layout.menu_bike,null);
        view2 = getLayoutInflater().inflate(R.layout.menu_count,null);
        view3 = getLayoutInflater().inflate(R.layout.menu_measure,null);
        view4 = getLayoutInflater().inflate(R.layout.menu_sensor,null);
        view5 = getLayoutInflater().inflate(R.layout.menu_lada,null);
        pageview = new ArrayList<View>();
        pageview.add(view1);
        pageview.add(view2);
        pageview.add(view3);
        pageview.add(view4);
        pageview.add(view5);

        ImgBike = view1.findViewById(R.id.viewImgBike);
        ImgCnt = view2.findViewById(R.id.viewImgCnt);
        ImgMea = view3.findViewById(R.id.viewImgMea);
        ImgSensor = view4.findViewById(R.id.viewImgSen);
        imgLadar = view5.findViewById(R.id.viewImgLada);

        imgLadar.setOnClickListener(this);
        ImgCnt.setOnClickListener(this);
        ImgSensor.setOnClickListener(this);
        ImgMea.setOnClickListener(this);
        ImgBike.setOnClickListener(this);

        group = (ViewGroup)findViewById(R.id.viewGroup);
        tips = new ImageView[pageview.size()];

        for(int i =0;i<pageview.size();i++){
            imageView = new ImageView(MainActivity.this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(20,20));
            imageView.setPadding(20, 0, 20, 0);
            tips[i] = imageView;

            //預設第一張圖顯示為選中狀態
            if (i == 0) {
                tips[i].setBackgroundResource(R.mipmap.png_sel);
            } else {
                tips[i].setBackgroundResource(R.mipmap.png_unsel);
            }

            group.addView(tips[i]);
        }

        viewPager.setAdapter(new PageAdapter(pageview));
        viewPager.addOnPageChangeListener(new PageChangeListener());
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId())
        {
            case R.id.viewImgBike:
                intent  = new Intent(mContext, MenuBike.class);
                mContext.startActivity(intent);
                break;
            case R.id.viewImgMea:
                intent  = new Intent(mContext, MenuMeasurement.class);
                mContext.startActivity(intent);
                break;
            case R.id.viewImgCnt:
                intent  = new Intent(mContext, MenuLapCnt.class);
                mContext.startActivity(intent);
                break;
            case R.id.viewImgLada:
                intent  = new Intent(mContext, MenuRadar.class);
                mContext.startActivity(intent);
                break;
            case R.id.viewImgSen:
                intent  = new Intent(mContext, MenuSensor.class);
                mContext.startActivity(intent);
                break;
        }
    }

    public class PageChangeListener implements ViewPager.OnPageChangeListener
    {
        final int MAX_PAGE = 4;
        final int FIRST_PAGE = 0;
        int currentPosition = 0;
        int scrollPage = 0;
        boolean isMoving = false;
        int tmpOffset = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
/*            if(isMoving)
            {
                Log.e("Awei","onPageScrolled:" + position + ",positionOffset:" + positionOffset + ",positionOffsetPixels:" + positionOffsetPixels);
            }*/

            scrollPage = position;
        }

        @Override
        public void onPageSelected(int position) {
//            Log.e("Awei","onPageSelected ---" + position);
            currentPosition = position;
            // 下方原點變化
            tips[position].setBackgroundResource(R.mipmap.png_sel);
            //這個圖片就是選中的view的圓點
            for(int i=0;i<pageview.size();i++){
                if (position != i) {
                    tips[i].setBackgroundResource(R.mipmap.png_unsel);
                    //這個圖片是未選中view的圓點
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            /*Log.e("Awei","onPageScrollStateChanged ---" + state);

            if(state == ViewPager.SCROLL_STATE_DRAGGING)
                isMoving = true;
            else if(state == ViewPager.SCROLL_STATE_IDLE)
                isMoving = false;*/

           /* if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (currentPosition == 0) {
                    if(scrollPage == 1)
                        viewPager.setCurrentItem(1, false);
                    else
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount(), false);
                }
                else if(currentPosition == viewPager.getAdapter().getCount())
                {
                    viewPager.setCurrentItem(0, false);
                }
            }*/
        }
    }
}