package com.example.fathima.tomton;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by FATHIMA on 11/13/2016.
 */
public class CustomList_View extends ArrayAdapter<String> {
    private String[] name;
    private String[] rollno;
    private String[] count;
    private String[] percent;
    private Activity context;

    public CustomList_View(Activity context, String[] name, String[] rollno, String[] count,String[] percent) {
        super(context, R.layout.list_att_view,name);
        this.context = context;
        this.name = name;
        this.rollno = rollno;
        this.count = count;
        this.percent=percent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_att_view, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.attendance_name);
        TextView textViewRoll = (TextView) listViewItem.findViewById(R.id.attendance_roll);
        TextView textViewStat = (TextView) listViewItem.findViewById(R.id.attendance_stat);
        TextView textViewPerc = (TextView) listViewItem.findViewById(R.id.attendance_perc);
        if(position%2==1)
        {
            listViewItem.setBackgroundColor(context.getResources().getColor(R.color.rowtwo));
        }
        textViewName.setText(name[position]);
        textViewRoll.setText(rollno[position]);
        textViewStat.setText(count[position]);
        textViewPerc.setText(percent[position]);
        return listViewItem;
    }
}