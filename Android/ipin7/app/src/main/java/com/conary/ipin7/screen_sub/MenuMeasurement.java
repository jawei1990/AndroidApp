package com.conary.ipin7.screen_sub;

import android.app.Activity;
import android.os.Bundle;

import com.conary.ipin7.R;
import com.conary.ipin7.utils.DataLog;

public class MenuMeasurement extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scree_bike);
        DataLog.e(" MenuMeasurement 3 ");
    }
}
