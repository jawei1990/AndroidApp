package com.conary.ipin7.view;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.conary.ipin7.utils.DataLog;

public class ScreenScale
{
    public static Activity activity;
    public static boolean Scale = true;

    //Design Screen Size
    public static float design_width = 1920;
    public static float design_height = 1080;

    //Physical machine Size
    public static float machine_width = 1184;
    public static float machine_height = 720;

    public static void initial(Activity ac){
        DisplayMetrics dm = new DisplayMetrics();
        activity = ac;
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        DataLog.d("dm.widthPixels:" + dm.widthPixels);
        DataLog.d("dm.heightPixels:"+dm.heightPixels);

        if (dm.widthPixels>dm.heightPixels){
            if (design_height>design_width){
                float dw = design_width;
                design_width = design_height;
                design_height = dw;
            }
        }else{
            if (design_width>design_height){
                float dw = design_width;
                design_width = design_height;
                design_height = dw;
            }
        }
        System.out.println("design_width:"+design_width);
        System.out.println("design_height:"+design_height);
        float wp = design_width/(float)dm.widthPixels;
        float hp = design_height/(float)dm.heightPixels;
        DataLog.d("wp:"+wp);
        DataLog.d("hp:"+hp);
        if (Scale){
            if (wp>hp){
                machine_width = dm.widthPixels;
                machine_height = (int)(design_height/wp);
            }else{
                machine_width = (int)(design_width/hp);
                machine_height = dm.heightPixels;
            }
        }else{
            machine_width = dm.widthPixels;
            machine_height = dm.heightPixels;
        }
        DataLog.d("machine_width:"+machine_width);
        DataLog.d("machine_height:"+machine_height);
    }

    public static void initial(DisplayMetrics dm){
        float wp = design_width/(float)dm.widthPixels;
        float hp = design_height/(float)dm.heightPixels;
        if (Scale){
            if (wp>hp){
                machine_width = (int)(design_width/hp);
                machine_height = dm.heightPixels;
            }else{
                machine_width = dm.widthPixels;
                machine_height = (int)(design_height/wp);
            }
        }else{
            machine_width = dm.widthPixels;
            machine_height = dm.heightPixels;
        }
    }

    public static float GetScale(){
        return machine_width / design_width;
    }

    public static void changeAllViewSize(View layoutViews) {
        if (layoutViews instanceof ViewGroup) {
            View[] LayoutView = new View[((ViewGroup) layoutViews).getChildCount()];
            for (int i = 0; i < ((ViewGroup) layoutViews).getChildCount(); i++) {
                View v = ((ViewGroup) layoutViews).getChildAt(i);
                LayoutView[i] = v;
                GTE(v);
            }
        }
    }

    public static void changeAllViewSize1(View layoutViews) {
        if (layoutViews instanceof ViewGroup) {
            View[] LayoutView = new View[((ViewGroup) layoutViews).getChildCount()];
            for (int i = 0; i < ((ViewGroup) layoutViews).getChildCount(); i++) {
                View v = ((ViewGroup) layoutViews).getChildAt(i);
                LayoutView[i] = v;
                GTE1(v);
            }
        }
    }

    public static void GTE(View view0) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)view0.getLayoutParams();
        float mDensity = activity.getResources().getDisplayMetrics().density/3.0f;
        if (view0.getClass() == SeekBar.class){
            if (GetScale()==mDensity){
                layoutParams.width = reflashWidth(layoutParams.width);
                layoutParams.height = reflashHeight(layoutParams.height);
                layoutParams.leftMargin = reflashX(layoutParams.leftMargin);
                layoutParams.topMargin = reflashY(layoutParams.topMargin);
                layoutParams.rightMargin = reflashX(layoutParams.rightMargin);
                layoutParams.bottomMargin = reflashY(layoutParams.bottomMargin);
            }else{
                SeekBar view = (SeekBar)view0;
                view.setThumbOffset((int)(view.getThumbOffset()*mDensity));
                view.setScaleX(GetScale()/mDensity);
                view.setScaleY(GetScale()/mDensity);
                layoutParams.width = (int)(layoutParams.width*mDensity);
                layoutParams.height = (int)(layoutParams.height*mDensity);
                layoutParams.leftMargin = (int)(layoutParams.leftMargin*mDensity);
                layoutParams.topMargin = (int)(layoutParams.topMargin*mDensity);
                layoutParams.rightMargin = (int)(layoutParams.rightMargin*mDensity);
                layoutParams.bottomMargin = (int)(layoutParams.bottomMargin*mDensity);
            }
        }else{
            layoutParams.width = reflashWidth(layoutParams.width);
            layoutParams.height = reflashHeight(layoutParams.height);
            layoutParams.leftMargin = reflashX(layoutParams.leftMargin);
            layoutParams.topMargin = reflashY(layoutParams.topMargin);
            layoutParams.rightMargin = reflashX(layoutParams.rightMargin);
            layoutParams.bottomMargin = reflashY(layoutParams.bottomMargin);
        }
        if (view0.getClass() == RelativeLayout.class) {
            changeAllViewSize(view0);
        } else if (view0.getClass() == LinearLayout.class) {
            changeAllViewSize1(view0);
        }else if (view0.getClass() == TextView.class){
            TextView view = (TextView)view0;
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.getTextSize() * GetScale() / mDensity);
        }else if (view0.getClass() == EditText.class){
            EditText view = (EditText)view0;
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX,view.getTextSize()*GetScale()/mDensity);
        }else if (view0.getClass() == Button.class){
            Button view = (Button)view0;
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX,view.getTextSize()*GetScale()/mDensity);
        }
        view0.setLayoutParams(layoutParams);
    }

    public static void GTE1(View view0) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)view0.getLayoutParams();
        float mDensity = activity.getResources().getDisplayMetrics().density/3.0f;
        if (view0.getClass() == SeekBar.class){
            if (GetScale()==mDensity){
                layoutParams.width = reflashWidth(layoutParams.width);
                layoutParams.height = reflashHeight(layoutParams.height);
                layoutParams.leftMargin = reflashX(layoutParams.leftMargin);
                layoutParams.topMargin = reflashY(layoutParams.topMargin);
                layoutParams.rightMargin = reflashX(layoutParams.rightMargin);
                layoutParams.bottomMargin = reflashY(layoutParams.bottomMargin);
            }else{
                SeekBar view = (SeekBar)view0;
                view.setThumbOffset((int)(view.getThumbOffset()*mDensity));
                view.setScaleX(GetScale()/mDensity);
                view.setScaleY(GetScale()/mDensity);
                layoutParams.width = (int)(layoutParams.width*mDensity);
                layoutParams.height = (int)(layoutParams.height*mDensity);
                layoutParams.leftMargin = (int)(layoutParams.leftMargin*mDensity);
                layoutParams.topMargin = (int)(layoutParams.topMargin*mDensity);
                layoutParams.rightMargin = (int)(layoutParams.rightMargin*mDensity);
                layoutParams.bottomMargin = (int)(layoutParams.bottomMargin*mDensity);
            }
        }else{
            layoutParams.width = reflashWidth(layoutParams.width);
            layoutParams.height = reflashHeight(layoutParams.height);
            layoutParams.leftMargin = reflashX(layoutParams.leftMargin);
            layoutParams.topMargin = reflashY(layoutParams.topMargin);
            layoutParams.rightMargin = reflashX(layoutParams.rightMargin);
            layoutParams.bottomMargin = reflashY(layoutParams.bottomMargin);
        }
        if (view0.getClass() == RelativeLayout.class) {
            changeAllViewSize(view0);
        } else if (view0.getClass() == LinearLayout.class) {
            changeAllViewSize1(view0);
        }else if (view0.getClass() == TextView.class){
            TextView view = (TextView)view0;
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX,view.getTextSize()*GetScale()/mDensity);
        }else if (view0.getClass() == EditText.class){
            EditText view = (EditText)view0;
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX,view.getTextSize()*GetScale()/mDensity);
        }else if (view0.getClass() == Button.class){
            Button view = (Button)view0;
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX,view.getTextSize()*GetScale()/mDensity);
        }
        view0.setLayoutParams(layoutParams);
    }

    public static int reflashWidth(int a1) {
        return (int) ((a1 * machine_width) / design_width);
    }

    public static int reflashHeight(int a1) {
        return (int) ((a1 * machine_height) / design_height);
    }

    public static int reflashX(int a1) {
        return (int) ((a1 * machine_width) / design_width);
    }

    public static int reflashY(int a1) {
        return (int) ((a1 * machine_height) / design_height);
    }

}
