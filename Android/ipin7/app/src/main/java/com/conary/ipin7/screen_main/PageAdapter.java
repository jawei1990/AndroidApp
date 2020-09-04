package com.conary.ipin7.screen_main;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.conary.ipin7.utils.DataLog;

import java.util.ArrayList;

public class PageAdapter extends PagerAdapter
{
    private ArrayList<View> pageView;

    public PageAdapter(ArrayList<View> pageView)
    {
        this.pageView = pageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int idx, Object object)
    {
        DataLog.d("MainActivity Destory: " + idx);

        if(pageView.get(idx) != null)
            container.removeView(pageView.get(idx));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int idx)
    {
        container.addView(pageView.get(idx));
        return pageView.get(idx);
    }

    @Override
    public int getCount() {
        return pageView.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }
}
