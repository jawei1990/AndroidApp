package com.example.laserusbdemo;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class measureAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private List<ListMeasure> mList;
    private DisplayMetrics dm;

    public measureAdapter(Context context, List<ListMeasure> list)
    {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dm = context.getResources().getDisplayMetrics();
        mList = list;
    }

    @Override
    public int getCount() {
        if(mList == null) return 0;
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if(convertView == null)
            v = mInflater.inflate(R.layout.mea_list_item, parent, false);
        else
            v = convertView;

        ListMeasure item = (ListMeasure) getItem(position);
        TextView tv_id = (TextView) v.findViewById(R.id.mea_tv_id);
        TextView tv_dis = (TextView) v.findViewById(R.id.men_tv_dis);
        TextView tv_temp = (TextView) v.findViewById(R.id.men_tv_tmp);
        TextView tv_times = (TextView) v.findViewById(R.id.men_tv_time);

        tv_id.setText(item.getId());
        tv_dis.setText(item.getDis());
        tv_temp.setText(item.getTemp());
        tv_times.setText(item.getTimes());

        return v;
    }
}
