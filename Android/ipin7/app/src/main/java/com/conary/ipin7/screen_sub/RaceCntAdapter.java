package com.conary.ipin7.screen_sub;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.conary.ipin7.R;

import java.util.List;

public class RaceCntAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private List<ListRaceCnt> mList;
    private DisplayMetrics dm;

    public RaceCntAdapter(Context context, List<ListRaceCnt> list)
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
            v = mInflater.inflate(R.layout.cnt_list_item, parent, false);
        else
            v = convertView;

        ListRaceCnt item = (ListRaceCnt) getItem(position);
        TextView tv_idx = (TextView) v.findViewById(R.id.cnt_tv_idx);
        TextView tv_time = (TextView) v.findViewById(R.id.cnt_tv_time);
        TextView tv_speed = (TextView) v.findViewById(R.id.cnt_tv_speed);

        tv_idx.setText(String.valueOf(position));
        tv_time.setText(item.getTime());
        tv_speed.setText(item.getSpeed());

        return v;
    }
}
