package com.example.fathima.tomton;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


public class CustomList extends ArrayAdapter<String> {
    private String[] names;
    private String[] roll;
    protected Activity context;

    public CustomList(Activity context, String[] names, String[] roll) {
        super(context, R.layout.list_attendance, names);
        this.context=context;
        this.names = names;
       this.roll=roll;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_attendance, null, true);
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/FallingSky.otf");
        if(position%2==1)
        {
            listViewItem.setBackgroundColor(context.getResources().getColor(R.color.rowtwo));
        }
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.name);
        TextView textViewRoll = (TextView) listViewItem.findViewById(R.id.attendance_roll);
        CheckBox checkBox=(CheckBox)listViewItem.findViewById(R.id.checkBox);
        textViewName.setText(names[position]);
        textViewRoll.setText(roll[position]);
        textViewName.setTypeface(face);
        textViewRoll.setTypeface(face);
        checkBox.setTypeface(face);
        return  listViewItem;
    }
}