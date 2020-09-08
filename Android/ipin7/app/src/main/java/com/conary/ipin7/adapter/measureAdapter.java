package com.conary.ipin7.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.conary.ipin7.R;

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
        TextView tv_time = (TextView) v.findViewById(R.id.mea_tv_time);
        TextView tv_msg = (TextView) v.findViewById(R.id.men_tv_msg);

        tv_time.setText(item.getTime());
        tv_msg.setText(item.getLogs());

        return v;
    }
}
