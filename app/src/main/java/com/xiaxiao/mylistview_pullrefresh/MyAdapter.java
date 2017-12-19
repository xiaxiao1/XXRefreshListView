package com.xiaxiao.mylistview_pullrefresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by xiaxiao on 2017/8/8.
 */

public class MyAdapter extends BaseAdapter {

    private List<String> datas;
    private Context context;
    public MyAdapter(Context context, List<String> datas) {
        this.datas=datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }
        holder.tv.setText(datas.get(position));
        return convertView;
    }


    class Holder{

        TextView tv;
        public Holder(View view) {
            tv = (TextView) view.findViewById(R.id.tv);
        }
    }
}
