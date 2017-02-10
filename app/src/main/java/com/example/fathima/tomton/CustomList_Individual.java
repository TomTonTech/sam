package com.example.fathima.tomton;

        import android.app.Activity;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;


public class CustomList_Individual extends ArrayAdapter<String> {
    private String[] dates;
    private String[] period;
    private String[] status;
    protected Activity context;

    public CustomList_Individual(Activity context, String[] dates, String[] period,String[] status) {
        super(context, R.layout.list_attendance, dates);
        this.context=context;
        this.dates = dates;
        this.period=period;
        this.status=status;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_individual, null, true);
        TextView textViewDates = (TextView) listViewItem.findViewById(R.id.tv_dates);
        TextView textViewPeriod = (TextView) listViewItem.findViewById(R.id.tv_period);
        TextView textViewStatus = (TextView) listViewItem.findViewById(R.id.tv_status);
        if(position%2==1)
        {
            listViewItem.setBackgroundColor(context.getResources().getColor(R.color.rowtwo));
        }
        textViewDates.setText(dates[position]);
        textViewPeriod.setText(period[position]);
        textViewStatus.setText(status[position]);

        return  listViewItem;
    }
}
